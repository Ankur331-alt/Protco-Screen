package com.smart.rinoiot.voice;

import android.Manifest;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.Lifecycle;

import com.dsh.openai.home.HomeCommandInference;
import com.dsh.openai.home.InferenceEngine;
import com.dsh.openai.home.InferenceResultListener;
import com.dsh.openai.home.InferenceToolsListener;
import com.dsh.openai.home.model.ControlIntent;
import com.dsh.openai.home.model.HomeDevice;
import com.dsh.openai.home.model.HomeInfo;
import com.dsh.openai.home.model.HomeLocation;
import com.dsh.openai.home.model.RoomInfo;
import com.dsh.openai.home.model.automation.AutomationInfo;
import com.dsh.openai.home.model.automation.Condition;
import com.dsh.openai.home.model.automation.MatchType;
import com.dsh.openai.home.model.automation.Task;
import com.dsh.openai.home.model.config.InferenceConfig;
import com.dsh.openai.home.model.config.ModelIdentifier;
import com.google.gson.Gson;
import com.rinoiot.iflytek.audio.SpeechStatus;
import com.rinoiot.iflytek.engine.MofeiEngine;
import com.rinoiot.iflytek.engine.MofeiEngineListener;
import com.rinoiot.iflytek.engine.model.tts.AudioStreamStatus;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.Geolocation;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.weather.WeatherDataUtils;
import com.smart.rinoiot.voice.audio.SoundPoolManager;
import com.smart.rinoiot.voice.automation.AutomationDataUtils;
import com.smart.rinoiot.voice.automation.AutomationManagementUtils;
import com.smart.rinoiot.voice.devices.DeviceControlUtils;
import com.smart.rinoiot.voice.devices.DeviceDataUtils;
import com.smart.rinoiot.voice.utils.ErrorMessageGenerator;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * @author edwin
 */
public class VoiceAssistantManager {

    private static final String TAG = "VoiceAssistantManager";

    /**
     * Inference engine
     */
    private final InferenceEngine inferenceEngine;

    /**
     * The voice assistance instance
     */
    private static VoiceAssistantManager instance;

    /**
     * The sound pool for generic sounds
     */
    private final SoundPoolManager soundPoolManager;

    /**
     * The application context
     */
    private final @ApplicationContext Context mContext;

    /**
     * The voice assistant listener
     */
    private final VoiceAssistantListener mAssistantListener;

    private final MofeiEngine mIflytekEngine;

    /**
     * Create an executor that executes tasks in a background thread.
     */
    @SuppressWarnings("all")
    private ExecutorService threadPool = Executors.newFixedThreadPool(1);

    private final Handler mMainLooper = new Handler(Looper.getMainLooper());

    /**
     * Inference result segments
     */
    private final List<String> mResultChunks = new ArrayList<>();
    private final AtomicBoolean mIsSynthesizing = new AtomicBoolean(false);
    private final AtomicBoolean mIsStreamComplete = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<Pair<String, Boolean>> mResultSegments = new ConcurrentLinkedQueue<>();

    @RequiresPermission( allOf = {Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET})
    private VoiceAssistantManager(
            @ApplicationContext Context context,
            Lifecycle lifecycle,
            VoiceAssistantListener assistantListener
    ) {
        // caching application context.
        mContext = context.getApplicationContext();

        // init the voice assistant
        this.mAssistantListener = assistantListener;

        mIflytekEngine = MofeiEngine.getInstance(context, getIflytekEngineListener());

        // initialize sound pool
        soundPoolManager = SoundPoolManager.getInstance(context);

        // initialize inference engine
        String openaiHost = CacheDataManager.getInstance().getInferenceServerAddress();
        Log.d(TAG, "VoiceAssistantManager: openai host: " + openaiHost);
        InferenceConfig config = new InferenceConfig(
                "Rino",
                BuildConfig.OPENAI_API_KEY,
                ModelIdentifier.getFourPointZeroPreview(),
                StringUtil.isBlank(openaiHost) ? BuildConfig.OPENAI_API_HOST : openaiHost
        );

        inferenceEngine = HomeCommandInference.getClient(
                context,
                config,
                getInferenceToolsListener(),
                getInferenceResultListener()
        );
        Log.d(TAG, "VoiceAssistantManager: adding inference engine to observer");
        lifecycle.addObserver(inferenceEngine);
        Log.d(TAG, "VoiceAssistantManager: initialization completed");
    }

    @RequiresPermission(allOf = {Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET})
    public static void init(
            @ApplicationContext Context context,
            Lifecycle lifecycle,
            VoiceAssistantListener assistantListener
    ) {
        if(null != instance){
            return;
        }

        synchronized (VoiceAssistantManager.class) {
            // set the recognition listener
            instance = new VoiceAssistantManager(
                    context.getApplicationContext(), lifecycle, assistantListener
            );
        }
    }

    /**
     * Accessor the current va instance.
     * ToDo() [CLEAN] clean this up after the testing campaign is completed
     * @return the current va instance
     */
    public static VoiceAssistantManager getInstance() {
        if(null==instance){
            throw new RuntimeException("Voice assistance is not initialized");
        }
        return instance;
    }

    private void cancelInference() {
        inferenceEngine.cancel(new Continuation<Unit>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {
            }
        });
    }

    public void startInference(String text){
        inferenceEngine.infer(text, true, new Continuation<Unit>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {
            }
        });
    }

    /**
     * Returns the wake up listener
     * @return the wake up listener
     */
    private MofeiEngineListener getIflytekEngineListener() {
        return new MofeiEngineListener() {
            @Override
            public void onWakeup() {
                cancelInference();
                resetInferenceResponse();
                soundPoolManager.play("wake-up");
                if(null != mAssistantListener){
                    mMainLooper.post(mAssistantListener::onWakeUp);
                }
            }

            @Override
            public void onResults(String text) {
                Log.d(TAG, "onResults: " + text);
                if(null != mAssistantListener && !TextUtils.isEmpty(text)){
                    mMainLooper.post(() -> mAssistantListener.onIntent(text));
                }else if(null != mAssistantListener && TextUtils.isEmpty(text)){
                    mMainLooper.post(mAssistantListener::onSleep);
                    return;
                }
                startInference(text);
            }

            @Override
            public void onSpeech(SpeechStatus status) {
                if(status == SpeechStatus.Finished && mResultSegments.isEmpty() && mIsStreamComplete.get()){
                    resetInferenceResponse();
                    mIflytekEngine.synthCompleted();
                    mMainLooper.post(mAssistantListener::onSleep);
                }
            }

            @Override
            public void onAudioStream(AudioStreamStatus status) {
                Log.d(TAG, "onAudioStream: status=" + status);
                if(status == AudioStreamStatus.Completed) {
                    synthesizeSpeech();
                }
            }

            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "onError: failed to wakeup. code=" + code  +  " | message= " + message);
            }
        };
    }

    /**
     * Initialize the inference listener
     *
     * @return the inference listener
     */
    private InferenceResultListener getInferenceResultListener() {
        return new InferenceResultListener() {
            @Override
            public void onCompletion(@NonNull String data, boolean stream, boolean complete) {
                handleResult(data, stream, complete);
                if(null != mAssistantListener){
                    mMainLooper.post(() -> mAssistantListener.onSpeaking(data));
                }
            }

            @Override
            public void onError(@NonNull Exception e) {
                Log.d(TAG, "onError: something went wrong=" + e.getLocalizedMessage());
                String errorMessage = ErrorMessageGenerator.getRandomErrorMessage();
                synthesizeSpeech(errorMessage, true);
                if(null != mAssistantListener){
                    mMainLooper.post(()-> mAssistantListener.onSpeaking(errorMessage));
                }
            }
        };
    }

    /**
     * Initialize the inference listener
     *
     * @return the inference listener
     */
    @SuppressWarnings("all")
    private InferenceToolsListener getInferenceToolsListener() {
        return new InferenceToolsListener() {
            @NonNull
            @Override
            public List<AutomationInfo> onQueryIotAutomations() {
                return AutomationDataUtils.getCachedAutomations();
            }

            @Override
            public boolean onManageAutomation(
                @NonNull String intent, @NonNull List<String> automationIds
            ) {
                return AutomationManagementUtils.manageAutomations(intent, automationIds);
            }

            @Override
            public boolean onCreateAutomation(
                @NonNull String name, @NonNull String loops, @NonNull MatchType matchType,
                @NonNull List<Task> tasks, @NonNull List<Condition> conditions
            ) {
                boolean status = AutomationManagementUtils.handleAutomationCreation(
                    name, loops, matchType, tasks, conditions
                );
                if(status){
                    mMainLooper.post(()-> mAssistantListener.refresh(RefreshTarget.Scenes));
                }
                return status;
            }

            @NonNull
            @Override
            public String onWebSearch(@NonNull String query) {
                return "The function is not supported in this version";
            }

            @Nullable
            @Override
            public List<JSONObject> onQueryNews(@NonNull String query) {
                HashMap<String, String> map = new HashMap<>();
                map.put("error", "This feature is not supported in this version");
                List<JSONObject> objects = new ArrayList<>();
                objects.add(new JSONObject(map));
                return objects;
            }

            @Override
            public JSONObject onQueryCurrentWeather(@NonNull String cityName) {
                return WeatherDataUtils.getCurrentWeatherData(cityName);
            }

            @NonNull
            @Override
            public HomeInfo onQueryCurrentHomeInfo() {
                return getCachedCurrentHomeInfo();
            }

            @NonNull
            @Override
            public String onQueryCurrentDateAndTime() {
                return getCurrentDateTime();
            }

            public boolean onDeviceControl(
                @NonNull List<String> list, ControlIntent intent, String value
            ) {
                boolean status = DeviceControlUtils.handleControl(mContext, list, intent, value);
                return status;
            }

            @NonNull
            @Override
            public List<HomeDevice> onQueryIotDevice() {
                return DeviceDataUtils.getCachedHomeDevice();
            }
        };
    }

    /**
     * Invokes the assistant's sleep method
     */
    private void sleep(){
        Log.d(TAG, "sleep: ");
        if(null != mAssistantListener){
            mMainLooper.post(mAssistantListener::onSleep);
        }
    }

    /**
     * Synthesizes the text to speech
     *
     * @param text The text to be synthesized
     */
    private void synthesizeSpeech(String text, boolean lastSeg) {
        threadPool.execute(() -> {
            Log.d(TAG, "synthesizeSpeech: text=" +  text +" | lastSeg=" + lastSeg);
            mIflytekEngine.synthText(text, lastSeg);
        });
    }

    /**
     * Synthesizes the text to speech
     */
    private void synthesizeSpeech() {
        threadPool.execute(() -> {
            // terminating condition
            if(mResultSegments.isEmpty() && mIsStreamComplete.get()){
                mIsSynthesizing.set(false);
                return;
            }

            // poll segments
            Pair<String, Boolean> segment = mResultSegments.poll();
            if(null != segment && StringUtil.isNotBlank(segment.first)){
                synthesizeSpeech(segment.first, segment.second);
            }else{
                synthesizeSpeech();
            }
        });
    }

    /**
     * Handles the inference response
     * @param data the stream data.
     * @param stream whether the response is streamed or not.
     * @param complete whether the entire response has been returned.
     */
    private void handleResult(String data, boolean stream, boolean complete) {
        threadPool.execute(() -> {
            String delimiters = "[,.:;?!](?=\\s)";
            Set<String> regexSet = new HashSet<>(Arrays.asList("," , "." , ";" , ":" , "?" , "!"));
            if (!stream) {
                return;
            }

            // process data
            List<String> splits = Arrays.asList(data.split(delimiters));
            for (String split : splits) {
                mResultChunks.add(split);
                boolean isMatched = false;
                for (String regex : regexSet) {
                    if (split.contains(regex)) {
                        isMatched = true;
                        break;
                    }
                }

                if (isMatched) {
                    String segment = TextUtils.join("" , mResultChunks).trim();
                    mResultSegments.add(new Pair<>(segment, complete));
                    mResultChunks.clear();
                    if (!mIsSynthesizing.get()) {
                        mIsSynthesizing.set(true);
                        mIflytekEngine.synthStarted();
                        synthesizeSpeech();
                    }
                }
            }

            // update stream complete status
            mIsStreamComplete.set(complete);
        });
    }

    /**
     * Converts the cached family info to home info
     *
     * @return The home info
     */
    private HomeInfo getCachedCurrentHomeInfo(){
        AssetBean home = CacheDataManager.getInstance().getCurrentFamily();
        if(null == home){
            return getDefaultHomeInfo();
        }

        Geolocation geolocation = CacheDataManager.getInstance().getDeviceLastKnownLocation();
        Log.d(TAG, "getCachedCurrentHomeInfo: geo location = " + new Gson().toJson(geolocation));
        HomeLocation homeLocation;
        if(null == geolocation){
            homeLocation = new HomeLocation("", "", "");
        }else{
            homeLocation = new HomeLocation(
                    geolocation.getCountry(), geolocation.getRegionName(), geolocation.getCity()
            );
        }

        List<RoomInfo> rooms = new ArrayList<>();
        home.getChildrens().forEach( room -> rooms.add(new RoomInfo(room.getName())));
        return new HomeInfo(
                home.getName(),
                homeLocation,
                rooms
        );
    }

    /**
     * Retrieves the default HomeInfo.
     *
     * @return The default HomeInfo object.
     */
    private HomeInfo getDefaultHomeInfo(){
        CityBean city = CacheDataManager.getInstance().getCityInfo();
        HomeLocation location = (null != city) ?
                new HomeLocation(city.getCountryName(), city.getProvince(), city.getName()) :
                new HomeLocation("", "", "");
        return new HomeInfo(
                "My Home",
                location,
                new ArrayList<>()
        );
    }

    /**
     * Get the current system type
     *
     * @return The current date and time
     */
    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEEE, yyyy-MM-dd HH:mm:ss a", Locale.getDefault()
        );
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Stops the voice assistant
     */
    public void stop(){
        cancelInference();
        if(null == mIflytekEngine){
            return;
        }
        try {
            sleep();
            mIflytekEngine.stop();
        }catch (Exception ex){
            String message = "stop: Failed to stop speech recognition. Cause="
                    + ex.getLocalizedMessage();
            Log.e(TAG, message);
        }
    }

    private void resetInferenceResponse(){
        mIsStreamComplete.set(true);
        mIsSynthesizing.set(false);
        mResultSegments.clear();
        mResultChunks.clear();
    }

    public void reset(){
        cancelInference();

        // clear and reset text segmentation data and flags()
        resetInferenceResponse();

        // reset engine
        if(null == mIflytekEngine){
            return;
        }
        
        try {
            mIflytekEngine.reset();
        }catch (Exception ex) {
            Log.e(TAG, "reset: Failed to reset. " + ex.getLocalizedMessage());
        }
    }

    /**
     * Starts the voice assistant
     */
    public void start(){
        if(null == mIflytekEngine){
            return;
        }

        try {
            mIflytekEngine.start();
        }catch (Exception ex){
            String message = "start: Failed to start speech recognition. Cause="
                    + ex.getLocalizedMessage();
            Log.e(TAG, message);
        }
    }

    public void destroy() {
        if(null == mIflytekEngine){
            return;
        }

        try {
            sleep();
            mIflytekEngine.destroy();
            instance = null;
        }catch (Exception ex){
            String message = "start: Failed to start speech recognition. Cause="
                    + ex.getLocalizedMessage();
            Log.e(TAG, message);
        }
    }
}
