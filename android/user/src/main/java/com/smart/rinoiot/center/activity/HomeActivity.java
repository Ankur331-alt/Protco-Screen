package com.smart.rinoiot.center.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.smart.rinoiot.center.bean.IpCameraDashboardEvent;
import com.smart.rinoiot.center.fragment.InformationFragment;
import com.smart.rinoiot.center.fragment.IpCameraDashboardFragment;
import com.smart.rinoiot.center.fragment.SetUpFragment;
import com.smart.rinoiot.center.screens.VoiceAssistantDialog;
import com.smart.rinoiot.center.screens.VoicePopUpEvent;
import com.smart.rinoiot.center.screens.VoicePopUpEventType;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.ble.BleManager;
import com.smart.rinoiot.common.customtab.TabControllerManager;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.event.FamilyChangeEventTarget;
import com.smart.rinoiot.common.location.LocationUtils;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.mqtt2.Manager.TopicManager;
import com.smart.rinoiot.common.rn.comfun.CommonPanelUtils;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.common.view.TextInputDialog;
import com.smart.rinoiot.common.voice.VoiceAssistantSettings;
import com.smart.rinoiot.family.fragment.FamilyFragment;
import com.smart.rinoiot.family.fragment.RoomFragment;
import com.smart.rinoiot.family.viewmodel.FamilyViewModel;
import com.smart.rinoiot.scene.fragment.SceneFragment;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityHomeBinding;
import com.smart.rinoiot.user.manager.UserNetworkManager;
import com.smart.rinoiot.voice.RefreshTarget;
import com.smart.rinoiot.voice.VoiceAssistantListener;
import com.smart.rinoiot.voice.VoiceAssistantManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/**
 * @author author
 */
@RequiresApi(api = 31)
public class HomeActivity extends BaseActivity<ActivityHomeBinding, FamilyViewModel>
        implements View.OnClickListener, RoomFragment.StockFunctionListener {

    private static final String TAG = "HomeActivity";

    /**
     * Navigation controller
     */
    private NavigationController navigationController;

    /**
     * Register the permissions callback, which handles the user's response to the
     * system permissions dialog. Save the return value, an instance of
     * ActivityResultLauncher, as an instance variable.
     */
    private ActivityResultLauncher<String> requestPermissionLauncher;

    /**
     * Create an executor that executes tasks in a background thread.
     */
    @SuppressWarnings("all")
    private ExecutorService voicePopUpPool = Executors.newFixedThreadPool(1);

    /**
     * Create an executor that executes tasks in a background thread.
     */
    @SuppressWarnings("all")
    private ExecutorService refreshDataPool = Executors.newFixedThreadPool(1);

    /**
     * Voice assistant dialog.
     */
    private VoiceAssistantDialog mVoiceAssistantDialog;

    private TextInputDialog mTextCommandsDialog;

    /**
     * Voice assistant dialog disposable.
     */
    private Disposable mVoicePopupDisposable;

    private boolean isForeground = true;

    private boolean hasMessage = false;

    /**
     * Composite disposable
     */
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void init() {
        StatusBarUtil.setNormalStatusBar(this, R.color.transparent);
        setFullStatusBar(R.color.transparent);
        mViewModel.showLoading();
        mViewModel.getFamilyList();
        // spin up mqtt
        initializeMqtt();
        setFamilyNameSize();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SceneFragment());
        fragmentList.add(new FamilyFragment());
        fragmentList.add(new IpCameraDashboardFragment());
        fragmentList.add(new SetUpFragment());
        fragmentList.add(new InformationFragment());
        leftTabInit(fragmentList);
        mViewModel.setContext(this);

        mViewModel.getFamilyListLiveData().observe(this, familyList -> {
            mViewModel.hideLoading();
            if (familyList != null && familyList.size() > 0) {
                for (AssetBean familyBean : familyList) {
                    if (familyBean != null && familyBean.isCurrentSelected()) {
                        binding.tvFamilyName.setText(familyBean.getName());
                    }
                }
                mViewModel.discoverDevice();
            }
        });

        mViewModel.getFamilyDetailLiveData().observe(this, familyDetail -> {
            if (null == familyDetail) {
                return;
            }
            List<DeviceInfoBean> devices = familyDetail.getDeviceInfoBeans();
            DataSourceManager.getInstance().addDeviceStates(devices);
            mViewModel.requestDeviceStates(devices);
            mViewModel.syncDeviceLikedStatus(familyDetail);
        });

        mViewModel.getVoiceAssistantStatusLiveData().observe(this, this::handleVoiceAssistantStatusLiveData);

        // update device's last known location
        mViewModel.updateLastKnownLocation();
        updateCityInfo();

        // initialize request permissions launcher
        requestPermissionLauncher = getRequestPermissionLauncher();

        // initializes the listeners
        setupListeners();

        // check permissions
        checkPermissions();

        // 缓存城市列表
        getCitiesList();
        RoomFragment.setStockFunctionListener(this);

        mVoiceAssistantDialog = new VoiceAssistantDialog(this);

        binding.fabListening.setOnClickListener(view -> mViewModel.toggleVoiceAssistantStatus());

        // STUB[BEGIN]
        mTextCommandsDialog = new TextInputDialog.Builder(this)
                .setTitle("Morfei")
                .setHint("Your wish")
                .setPositiveButton("Infer" , text -> {
                    try {
                        cancelVoicePopUpDismiss();
                        showAssistantModal();
                        sendVoicePopUpMessage(VoicePopUpEventType.WakeUp);
                        VoiceAssistantSettings settings = CacheDataManager.getInstance().getVoiceAssistantSettings();
                        dismissVoicePopUp(Duration.ofSeconds(settings.getContinuousListeningDuration()));
                        VoiceAssistantManager.getInstance().startInference(text);
                    }catch (Exception exception) {
                        Log.d(TAG, "init: failed to infer. " + exception.getLocalizedMessage());
                    }
                }).create();
        mTextCommandsDialog.setCancelable(false);
        // STUB[END]
    }

    private void initializeMqtt() {
        Disposable disposable = Observable.create(emitter -> {
            if (!UserInfoManager.getInstance().isLogin(this)) {
                return;
            }
            UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(this);
            MqttManager.getInstance().mqttConnectInit(userInfo, this);
            // Subscribe to user notifications
            MqttManager.getInstance().subscribe(
                    TopicManager.subscribeUserNotifications(userInfo.id)
            );
        }).delay(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d->{}, throwable -> Log.d(TAG, "initializeMqtt: failed to est con."));
        mCompositeDisposable.add(disposable);
    }

    private void handleVoiceAssistantStatusLiveData(Boolean status){
        Log.d(TAG, "handleVoiceAssistantStatusLiveData: " +  status);
        if(status == null || !status){
            try {
                VoiceAssistantManager.getInstance().stop();
                Drawable drawable = AppCompatResources.getDrawable(
                        this, R.drawable.icon_microphone_off_round
                );
                binding.fabListening.setImageDrawable(drawable);
            }catch (Exception ex) {
                Log.e(TAG, "init: Failed to mute voice assistant. " + ex.getLocalizedMessage());
            }
        }else{
            try {
                VoiceAssistantManager.getInstance().start();
                Drawable drawable = AppCompatResources.getDrawable(
                        this, R.drawable.icon_microphone_on_round
                );
                binding.fabListening.setImageDrawable(drawable);
            }catch (Exception ex) {
                Log.e(TAG, "init: Failed to activate voice assistant. " + ex.getLocalizedMessage());
            }
        }
        try {
            int position = navigationController.getSelected();
            Log.d(TAG, "init: position=" + position);
            binding.fabListening.setVisibility(position != 0 ? View.GONE : View.VISIBLE);
        }catch (Exception ex) {
            Log.d(TAG, "init: failed to handle fab");
        }
    }

    @SuppressLint("MissingPermission")
    private ActivityResultLauncher<String> getRequestPermissionLauncher() {
        return registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        AppExecutors.getInstance().delayedThread().schedule(
                                () -> AppExecutors.getInstance().mainThread().execute(() -> {
                                    VoiceAssistantManager.init(
                                            getApplicationContext(), getLifecycle(), getVoiceAssistantListener()
                                    );
                                    mViewModel.getVoiceAssistantStatus();
                                }), 2500, TimeUnit.MILLISECONDS
                        );
                        // You can use the API that requires the permission.
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // feature requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                        Log.d(TAG, "requestPermissionLauncher: some permissions are missing.");
                    }
                });
    }

    private void getCitiesList() {
        // 缓存城市列表
        Disposable disposable = UserNetworkManager.getInstance().getCityList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cities -> {
                            if (cities != null && cities.size() > 0) {
                                CacheDataManager.getInstance().saveCityList(cities);
                            }
                        },
                        throwable -> Log.e(
                                TAG, "getCitiesList: failed to query cities list." +
                                        throwable.getLocalizedMessage()
                        ));
        mCompositeDisposable.add(disposable);
    }

    /**
     * Overloads onDestroy
     */
    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            VoiceAssistantManager.getInstance().stop();
        } catch (Exception ex) {
            Log.e(TAG, "onDestroy: Failed to stop assistant. Cause=" + ex.getLocalizedMessage());
        }
        mCompositeDisposable.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // init ble here
        BleManager.init(this);

        // dismiss dialog if its showing
        if(null != mVoiceAssistantDialog && mVoiceAssistantDialog.isShowing()){
            mVoiceAssistantDialog.dismiss();
        }

        try {
            if(null != VoiceAssistantManager.getInstance()) {
                // set va listening status
                mViewModel.getVoiceAssistantStatus();
            }
        }catch (Exception exception) {
            Log.d(TAG, "onResume: failed to get instance");
        }
    }

    private void updateCityInfo() {
        Disposable disposable = LocationUtils.requestCityInfo(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            Log.d(TAG, "onSuccess: City = " + new Gson().toJson(data));
                            CacheDataManager.getInstance().cacheCityInfo(data);
                        },
                        throwable -> Log.e(TAG, "onError: " + throwable.getLocalizedMessage())
                );
        mCompositeDisposable.add(disposable);
    }

    /**
     * Checks audio permissions
     */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        ) {
            // You can use the API that requires the permission.
            AppExecutors.getInstance().delayedThread().schedule(
                    () -> AppExecutors.getInstance().mainThread().execute(() -> {
                        VoiceAssistantManager.init(
                                getApplicationContext(), getLifecycle(), getVoiceAssistantListener()
                        );
                        mViewModel.getVoiceAssistantStatus();
                    }), 2500, TimeUnit.MILLISECONDS
            );
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            Log.d(TAG, "checkPermissions: request permissions");
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    /**
     * The voice assistant listener
     *
     * @return the voice assistant listener
     */
    private VoiceAssistantListener getVoiceAssistantListener() {
        return new VoiceAssistantListener() {
            @Override
            public void onWakeUp() {
                cancelVoicePopUpDismiss();
                showAssistantModal();
                sendVoicePopUpMessage(VoicePopUpEventType.WakeUp);
                VoiceAssistantSettings settings = CacheDataManager.getInstance().getVoiceAssistantSettings();
                dismissVoicePopUp(Duration.ofSeconds(settings.getContinuousListeningDuration()));
            }

            @Override
            public void onSpeaking(String text) {
                cancelVoicePopUpDismiss();
                sendVoicePopUpMessage(VoicePopUpEventType.Speech, text);
            }

            @Override
            public void onIntent(String text) {
                cancelVoicePopUpDismiss();
                sendVoicePopUpMessage(VoicePopUpEventType.Intent, text);
            }

            @Override
            public void onSleep() {
                // We are going to delay the inevitable
                Log.d(TAG, "onSleep: ");
                if(null != mVoiceAssistantDialog && mVoiceAssistantDialog.isShowing()) {
                    resetVoicePopup();
                    VoiceAssistantSettings settings = CacheDataManager.getInstance().getVoiceAssistantSettings();
                    dismissVoicePopUp(Duration.ofSeconds(settings.getContinuousListeningDuration()));
                }
            }

            /**
             * Invoked when the assistant need to refresh the contents of the screen
             *
             * @param target the part of the screen to be refreshed
             */
            @Override
            public void refresh(RefreshTarget target) {
                refreshScreen(target);
            }
        };
    }

    /**
     * Refresh the screen contents
     *
     * @param target the part of the screen that need refreshing
     */
    private void refreshScreen(RefreshTarget target) {
        refreshDataPool.execute(() -> {
            if(RefreshTarget.Scenes.name().contentEquals(target.name())){
                DeviceEvent event = new DeviceEvent(
                        DeviceEvent.Type.CHANGE_FAMILY, FamilyChangeEventTarget.REFRESH_SCENES
                );
                EventBus.getDefault().post(event);
            }
        });
    }

    /**
     * 未读消息中心
     */
    public void unreadUpdate(boolean hasMessage) {
        this.hasMessage = hasMessage;
        navigationController.setHasMessage(
                mViewModel.getFragmentList().size() - 1, hasMessage
        );
    }

    private void setupListeners() {
        // on click listener
        binding.tvFamilyName.setOnClickListener(this);
    }

    @Override
    public ActivityHomeBinding getBinding(LayoutInflater inflater) {
        return ActivityHomeBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvFamilyName) {
            mViewModel.showHomeManagerPopView(
                    this, binding.tvFamilyName, binding.viewPager
            );
        }
    }

    /**
     * 设置家庭名称展示大小
     */
    private void setFamilyNameSize() {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        DpUtils.dip2px(118), LinearLayout.LayoutParams.MATCH_PARENT
                );
        binding.llLeft.setLayoutParams(params);
        binding.llLeft.setPadding(0, DpUtils.dip2px(64), 0, 0);
    }

    @Override
    public void stockFunCallBack(DeviceInfoBean deviceInfoBean) {
        CommonPanelUtils.getInstance().downloadCommonPanel(
                deviceInfoBean.getAndroidCommonlyShortcutPackage()
        );
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    private void leftTabInit(List<Fragment> fragmentList) {
        navigationController = TabControllerManager.getInstance()
                .createBottomBarItem(this, binding.pageNavigation);
        navigationController.setupWithViewPager(binding.viewPager);
        binding.viewPager.setAdapter(mViewModel.createFragmentAdapter(this, fragmentList));
        binding.viewPager.setOffscreenPageLimit(mViewModel.getFragmentList().size());
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                navigationController.setHasMessage(
                        mViewModel.getFragmentList().size() - 1, hasMessage
                );
                setBottomNavigationBarColor();
                if (fragmentList.get(index) instanceof IpCameraDashboardFragment) {
                    EventBus.getDefault().post(IpCameraDashboardEvent.OPEN_STREAMS);
                } else if (fragmentList.get(old) instanceof IpCameraDashboardFragment) {
                    EventBus.getDefault().post(IpCameraDashboardEvent.CLOSE_STREAMS);
                }

                if(!(fragmentList.get(index) instanceof SceneFragment)){
                    binding.fabListening.setVisibility(View.GONE);
                }else{
                    binding.fabListening.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onRepeat(int index) {

            }
        });
    }

    public void changeForeground(boolean isForeground) {
        this.isForeground = isForeground;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AppManager.getInstance().getTopActivity() instanceof HomeActivity) {
            EventBus.getDefault().post(IpCameraDashboardEvent.CLOSE_STREAMS);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusNotify(VoicePopUpEvent popUpEvent) {
        Log.d(TAG, "onEventBusNotify: " +  new Gson().toJson(popUpEvent));
        try {
            disposeVoicePopupDisposable();
            VoiceAssistantManager.getInstance().reset();
        }catch(Exception ex) {
            Log.e(TAG, "onEventBusNotify: failed to cancel=" + ex.getLocalizedMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        try {
            if(null != mVoiceAssistantDialog && mVoiceAssistantDialog.isShowing()) {
                mVoiceAssistantDialog.dismiss();
            }
            VoiceAssistantManager.getInstance().stop();
        }catch (Exception exception) {
            Log.d(TAG, "onPause: ");
        }
    }

    /**
     * Shows the assistant's modal
     */
    public void showAssistantModal() {
        if(mVoiceAssistantDialog.isShowing()) {
            return;
        }

        AppCompatActivity activity = AppManager.getInstance().getTopActivity();
        if(!(activity instanceof HomeActivity)){
            return;
        }

        try {
            mVoiceAssistantDialog.show();
        }catch (Exception ex) {
            mVoiceAssistantDialog = new VoiceAssistantDialog(activity);
            mVoiceAssistantDialog.show();
        }
    }

    private void disposeVoicePopupDisposable(){
        if(null != mVoicePopupDisposable && !mVoicePopupDisposable.isDisposed()) {
            mVoicePopupDisposable.dispose();
        }
    }

    /**
     * Sends a message to dismiss a Pop Up vew if one is open
     */
    private void dismissVoicePopUp(Duration delay) {
        // dispose anything pending
        disposeVoicePopupDisposable();
        // create a new one.
        mVoicePopupDisposable = Completable.complete()
            .delay(delay.getSeconds(), TimeUnit.SECONDS)
            .doOnComplete(() -> {
                disposeVoicePopupDisposable();
                sendVoicePopUpMessage(VoicePopUpEventType.Sleep);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    ()->{},
                    error-> Log.e(TAG, "dismissVoicePopUp: " + error.getLocalizedMessage())
            );
    }

    /**
     * Cancels the request to dismiss a Pop Up view
     */
    private void cancelVoicePopUpDismiss() {
        if(null != mVoicePopupDisposable && !mVoicePopupDisposable.isDisposed()) {
            mVoicePopupDisposable.dispose();
        }
    }

    /**
     * Sends a message to the Voice Pop Up view
     *
     * @param type event type
     */
    private void sendVoicePopUpMessage(VoicePopUpEventType type) {
        if(!mVoiceAssistantDialog.isShowing()) {
            return;
        }
        mVoiceAssistantDialog.handleEvent(new VoicePopUpEvent(type));
    }

    /**
     * Resets the voice pop up.
     */
    private void resetVoicePopup() {
        if(!mVoiceAssistantDialog.isShowing()) {
            return;
        }
        sendVoicePopUpMessage(VoicePopUpEventType.Reset);
    }

    /**
     * Sends a message to the Voice Pop Up view
     *
     * @param type the event type
     * @param data the event data
     */
    private void sendVoicePopUpMessage(VoicePopUpEventType type, String data) {
        if(!mVoiceAssistantDialog.isShowing()) {
            return;
        }
        mVoiceAssistantDialog.handleEvent(new VoicePopUpEvent(data, type));
    }
}
