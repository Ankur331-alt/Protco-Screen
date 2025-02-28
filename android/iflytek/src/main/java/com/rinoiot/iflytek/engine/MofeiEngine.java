package com.rinoiot.iflytek.engine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.google.gson.Gson;
import com.iflytek.alsa.AlsaRecorder;
import com.rinoiot.iflytek.audio.AudioConfig;
import com.rinoiot.iflytek.audio.SpeechStatus;
import com.rinoiot.iflytek.audio.TtsAudioPlayer;
import com.rinoiot.iflytek.engine.model.asr.AsrResponse;
import com.rinoiot.iflytek.engine.model.asr.Data;
import com.rinoiot.iflytek.engine.model.asr.Result;
import com.rinoiot.iflytek.engine.model.asr.Word;
import com.rinoiot.iflytek.engine.model.asr.WordSegment;
import com.rinoiot.iflytek.engine.model.tts.AudioStreamStatus;
import com.rinoiot.iflytek.recorder.AudioRecorder;
import com.rinoiot.iflytek.recorder.RecorderFactory;
import com.rinoiot.iflytek.utils.AudioAmplify;
import com.rinoiot.iflytek.utils.AudioFilter;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.voice.VoiceAssistantSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author edwin
 */
public class MofeiEngine {

    private static final String TAG = "MofeiEngine";

    private String uploadEntityResSid = null;
    @NonNull
    private final MorfeiCoreVui mMoFeiCoreVui;
    private final AudioRecorder audioRecorder;
    private static volatile MofeiEngine instance;
    private final Runnable checkEntityResRunnable;
    private final MofeiEngineListener mMofeiEngineListener;

    private int mIntactMode;
    private final AtomicBoolean mInterrupt;
    private final AtomicBoolean mSpeechActive;
    private final TtsAudioPlayer mTtsAudioPlayer;
    private final List<String> mNlpText = new ArrayList<>();
    private final AtomicBoolean mLastSynthText = new AtomicBoolean(false);

    /**
     * 授权信息，每个产品对应一个productid， 每个设备对应一个authid，
     * 由讯飞提供（和识别中的asrParams中的auth_id不是同一个，此authid用于本地能力授权）
     */
    public static final String INIT_PARAMS = "productid=morfeimf21, authid=63034010768c4218bb99371185c19d4f";

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private MofeiEngine(Context context, MofeiEngineListener listener) {
        mMoFeiCoreVui = Objects.requireNonNull(MorfeiCoreVui.getInstance(context, INIT_PARAMS));
        mMoFeiCoreVui.addVUIListener(getVuiListener());
        AudioConfig audioConfig = new AudioConfig(1, 0, 8, true);
        audioRecorder = RecorderFactory.getRecorder(audioConfig, getPcmDataListener());
        mMofeiEngineListener = listener;
        checkEntityResRunnable = () -> {
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            mMoFeiCoreVui.checkEntityRes(uploadEntityResSid);
        };

        int mAudioBufferSize = AudioTrack.getMinBufferSize(
                16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT
        );
        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC, 16000,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mAudioBufferSize, AudioTrack.MODE_STREAM
        );

        mInterrupt = new AtomicBoolean(false);
        mSpeechActive = new AtomicBoolean(false);
        mIntactMode = MorfeiCoreType.INTACT_MODE_CONTINUE;

        mTtsAudioPlayer = new TtsAudioPlayer(audioTrack, getTtsAudioPlayerListener());
        init();
    }

    private TtsAudioPlayer.Listener getTtsAudioPlayerListener(){
        return new TtsAudioPlayer.Listener() {
            @Override
            public void onStart() {
                // invoke the sleep callback
                if(null != mMofeiEngineListener){
                    mMofeiEngineListener.onSpeech(SpeechStatus.Started);
                }
            }

            @Override
            public void onStop() {
                // invoke the sleep callback
                if(null != mMofeiEngineListener){
                    mMofeiEngineListener.onSpeech(SpeechStatus.Finished);
                    mInterrupt.set(false);
                }
            }
        };
    }

    @SuppressLint("SdCardPath")
    private void init(){
        // 云端合成参数
        // appid、apikey参数需要替换为自己的应用
        // key-value对形式传入，多个key-value对使用逗号隔开
        final String ttsParams = "appid=u2160ec6, apikey=c7103d30a5e5a2bc52846459454ab13e,apisecret=64dbf14ceb01ff4af25d8c1d8ade788c,host=eu-central-1.aicloudapi.com,aue=raw,vcn=x4_EnUs_Gavin_assist,pitch=50,volume=80,speed=60, engine_type=cloud";
        Log.d(TAG, "init: ttsParams=" + ttsParams);
        int ret = mMoFeiCoreVui.startTTS(ttsParams);
        if (ret != 0){
            Log.e(TAG, "init: fail to start TTS with code=" + ret);
            return;
        }

        // 云端识别参数
        // appid、apikey参数需要替换为自己的应用
        // comm_params包含多个通用参数，key+value形式，多个key+value中间使用&分割
        //   -auth_id 32位数字和字母的组合，用于关联用户个性化数据
        //   -scene   为main时访问aiui生产环境，main_box测试环境（用户后台功能测试），正式版本发布时需要传入main
        // pers_params 个性化参数
        //   应用级{"appid":"xxxxxx"}，用户级{"auth_id":"xxx"}（与comm_params中auth_id一致），自定义级{"xxx":"xxx"}
        // user_params 自定义参数，透传到后处理云函数
        //   建议参数格式{\"key1\":\"value1\"}，在云函数中通过context->Custom->iflytek_data->user_data字段中获得自定义参数
        final String asrParams = "engine_type=cloud, appid=u2160ec6, apikey=c7103d30a5e5a2bc52846459454ab13e,apisecret=64dbf14ceb01ff4af25d8c1d8ade788c ,host=eu-central-1.aicloudapi.com,language=en_us,vad_eos=2000";

        // 本地离线命令词识别参数
        // key-value对形式传入，多个key-value对使用逗号隔开
        // cnn_mlp_res_file、main_keywords_res_file、asr_keywords_res_file 指定资源文件路径
        // channel_count 音频通道数  值为 1 或  3
        // channel_number 音频通道号 只有在channel_count 为 1时才需要配置 (值可选 1~4)
        Log.d(TAG, "init: asrParams=" + asrParams);
        ret = mMoFeiCoreVui.startASR(asrParams);
        if (ret != 0){
            Log.e(TAG, "init: failed " + ret);
            return;
        }

        // ToDo() these should be passed as params not read like this.
        VoiceAssistantSettings settings = CacheDataManager.getInstance().getVoiceAssistantSettings();
        String intactModeParams = "intact_time=" + settings.getContinuousListeningDuration() * 1000;
        Log.d(TAG, "init: intact time params=" + intactModeParams);
        if(settings.isContinuousMode()){
            mIntactMode = MorfeiCoreType.INTACT_MODE_CONTINUE;
        }else{
            mIntactMode = MorfeiCoreType.INTACT_MODE_ONESHOT;
        }
        Log.d(TAG, "init: intact mode=" + mIntactMode);
        ret = mMoFeiCoreVui.setIntactMode(mIntactMode, intactModeParams);
        if (ret != 0){
            Log.e(TAG, "init: failed to set intact mode with code=" + ret);
            return;
        }

        // 降噪和唤醒资源路径
        // key-value对形式传入，多个key-value对使用逗号隔开
        // 注意：cae.ini中需要正确配置aes_model（mlp_aes_1024.bin资源路径）
        final String caeIvwParams = "cae_res_path=/sdcard/mfcore_res/cae/cae.ini, ivw_res_path=/sdcard/mfcore_res/ivw/mfcore_ivw_res_haloumofei_hairuilou_halouxiaoxi.bin,ivw_keyword=hello_mofei&嗨锐喽&哈喽小犀,c_ncm=0:1000&1:1000, dist_lvl=0, dist_set=query_timeout+600&max_wk_wait+3000&finish_delay+1500&energy_MAX+1240062885888&energy_MIN+2480864000&";
        Log.d(TAG, "init: caeIvwParams=" + caeIvwParams);
        ret = mMoFeiCoreVui.startCaeIvw(caeIvwParams);
        Log.d(TAG, "init: caeIvw init status=" + ret);
    }

    private String handleAsrResponse(Data data){
        if(null == data){
            throw new IllegalArgumentException("No data found in asr response");
        }

        Result asrResult = data.getResult();
        if(null == asrResult){
            throw new IllegalArgumentException("No results found in asr data");
        }

        List<WordSegment> wordSegments = asrResult.getWs();
        if(null == wordSegments || wordSegments.isEmpty()){
            throw new IllegalArgumentException("No word segments found in response.");
        }

        StringBuilder intent = new StringBuilder();
        wordSegments.forEach(segment-> {
            List<Word> words = segment.getCw();
            if(null == words || words.isEmpty()) {
                return;
            }
            words.forEach(word -> intent.append(word.getW()));
        });
        return intent.toString();
    }

    @SuppressWarnings("all")
    private MofeiCoreVuiListener getVuiListener() {
        return new MofeiCoreVuiListener() {
            @Override
            public void onAsrResult(int type, String result) {
                try {
                    AsrResponse asrResponse = new Gson().fromJson(result, AsrResponse.class);
                    Log.d(TAG, "onAsrResult: result =" + new Gson().toJson(asrResponse));
                    if(null == asrResponse || null == asrResponse.getData()){
                        return;
                    }

                    Data data = asrResponse.getData();
                    String text = handleAsrResponse(data);
                    if(StringUtil.isBlank(text)){
                        return;
                    }

                    mNlpText.add(text);
                    if (data.getStatus() != 2) {
                        return;
                    }

                    if(null != mMofeiEngineListener){
                        String intent = String.join("", mNlpText);
                        mNlpText.clear();
                        mMofeiEngineListener.onResults(intent.trim());
                    }
                }catch (Exception ex) {
                    Log.e(TAG, "onAsrResult: failed to extract asr result" + ex.getLocalizedMessage());
                    mNlpText.clear();
                }
            }

            @Override
            public void onTtsAudio(byte[] audio, int is_last) {
                Log.d(TAG, "onTtsAudio: isLast = " + (is_last == 1));
                if(!mSpeechActive.get() && (is_last == 0)) {
                    mSpeechActive.set(true);
                    if(null != mMofeiEngineListener){
                        mMofeiEngineListener.onAudioStream(AudioStreamStatus.Started);
                    }
                }

                if(null != audio && audio.length > 0){
                    mTtsAudioPlayer.write(audio, (is_last == 1) && mLastSynthText.get());
                }else{
                    Log.d(TAG, "onTtsAudio: dataLength = 0");
                }

                if(is_last != 1){
                    return;
                }

                if(null != mMofeiEngineListener){
                    mMofeiEngineListener.onAudioStream(AudioStreamStatus.Completed);
                }

                mSpeechActive.set(false);
            }

            @Override
            public void onWakeup(String wakeupInfo) {
                Log.d(TAG, "onWakeup: info=" + wakeupInfo);
                cancelSpeech();
                if(null != mMofeiEngineListener){
                    mMofeiEngineListener.onWakeup();
                }
            }

            @Override
            public void onStatus(int type, int code, String detail) {
                Log.d(TAG, "onStatus: " + type + " | " + code + " | " + detail);
                switch (type) {
                    case MorfeiCoreType.STATUS_TYPR_SLEEP: {
                        Log.d(TAG, "onStatus: VUI 休眠需要重新唤醒");
                    }
                    break;
                    case MorfeiCoreType.STATUS_TYPR_ERROR: {
                        Log.e(TAG, "onStatus: VUI 发生错误");
                        if(null != mMofeiEngineListener){
                            mMofeiEngineListener.onError(code, detail);
                        }
                    }
                    break;
                    default:
                        break;
                }
            }

            @Override
            public void onEntityResult(int type, int code, String sid, String detail) {
                Log.d(TAG, "onEntityResult: " + type + " | " + code + " | " + sid + "| " + detail);
                if (MorfeiCoreType.ENT_RES_RESULT_UPLOAD == type && 0 == code) {
                    // 个性化数据上传成功后，需要间隔10s对上传的资源进行检查是否正确
                    uploadEntityResSid = sid;
                    Thread checkEntityResThread = new Thread(checkEntityResRunnable);
                    checkEntityResThread.start();
                }
            }
        };
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public static MofeiEngine getInstance(Context context, MofeiEngineListener listener) {
        if (instance == null) {
            synchronized (MofeiEngine.class) {
                if (instance == null) {
                    instance = new MofeiEngine(context, listener);
                }
            }
        }
        return instance;
    }

    private AlsaRecorder.PcmListener getPcmDataListener() {
        return (bytes, i) -> {
            //改变原始音频增益
            byte [] processed = AudioFilter.filter(
                    8, 1, bytes, 6
            );
            AudioAmplify.amplifyAll(processed, EngineConstants.RAW_AUDIO_GAIN);
            mMoFeiCoreVui.writeAudio(processed, MorfeiCoreType.AUDIO_STATUS_CONTINUE);
        };
    }

    public void synthText(String text, boolean last){
        // start text synth
        int ret = mMoFeiCoreVui.synthText(text);
        if (ret != 0){
            Log.e(TAG, "synthText: failed to synthesize text with code=" +  ret);
        }

        // update is last synth text
        mLastSynthText.set(last);
    }

    public void synthStarted() {
        if(mIntactMode != MorfeiCoreType.INTACT_MODE_CONTINUE){
            return;
        }

        mMoFeiCoreVui.playBegin();
    }

    public void synthCompleted() {
        if(mIntactMode != MorfeiCoreType.INTACT_MODE_CONTINUE){
            return;
        }

        mMoFeiCoreVui.playEnd();
    }

    public void cancelSpeech() {
        int ret = mMoFeiCoreVui.breakTTS();
        if (ret != 0){
            Log.e(TAG, "cancelSpeech: failed to break TTS with code=" + ret);
        }

        try {
            mTtsAudioPlayer.stop();
            if(mSpeechActive.get()){
                mInterrupt.set(true);
            }
            mSpeechActive.set(false);
        }catch (Exception exception) {
            Log.e(TAG, "cancelSpeech: failed to cancel speech. "+ exception.getLocalizedMessage());
        }
    }

    public void reset(){
        cancelSpeech();
        mMoFeiCoreVui.reset();
    }

    public void start(){
        if(null == audioRecorder){
            Log.e(TAG, "start: audio recorder is null");
            return;
        }
        audioRecorder.start();
    }

    public void stop() {
        if(null == audioRecorder){
            Log.e(TAG, "start: audio recorder is null");
            return;
        }
        audioRecorder.stop();
    }

    public void destroy(){
        Log.d(TAG, "destroy");
        try {
            if(null != audioRecorder){
                audioRecorder.destroy();
            }
        }catch (Exception ex){
            Log.d(TAG, "destroy: failed to destroy audio");
        }

        try{
            mMoFeiCoreVui.destroy();
        }catch (Exception exception) {
            Log.d(TAG, "destroy: failed to destroy mfcore");
        }
    }
}