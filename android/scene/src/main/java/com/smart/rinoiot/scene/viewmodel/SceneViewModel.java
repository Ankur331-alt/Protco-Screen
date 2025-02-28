package com.smart.rinoiot.scene.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.viewpager.widget.ViewPager;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.dsh.matter.management.device.DeviceControllerCallback;
import com.google.gson.Gson;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.DisplayDateTime;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.bean.SceneDeviceDpBean;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.datastore.persistence.UnifiedDeviceState;
import com.smart.rinoiot.common.datastore.persistence.WeatherData;
import com.smart.rinoiot.common.device.DeviceCmdConverterUtils;
import com.smart.rinoiot.common.device.DeviceDataPoint;
import com.smart.rinoiot.common.event.DeviceChangeEventOrigin;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.magicIndicator.GradientLinePagerIndicator;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.CommonNetworkManager;
import com.smart.rinoiot.common.matter.MtrDeviceControlManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.mqtt2.Manager.MqttConvertManager;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.activity.SceneListSelectActivity;
import com.smart.rinoiot.scene.manager.DisplayTimeTask;
import com.smart.rinoiot.scene.manager.SceneNetworkManager;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author author
 */
public class SceneViewModel extends BaseViewModel {
    private static final String TAG = "SceneViewModel";

    private Disposable mDevicesStatesDisposable = null;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public SceneViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    private final MutableLiveData<DisplayDateTime> displayDateTimeLiveData = new MutableLiveData<>();

    private final MutableLiveData<DeviceInfoBean> deviceInfoChangeLiveData = new MutableLiveData<>();

    @SuppressWarnings("all")
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(
            new DisplayTimeTask(displayDateTimeLiveData),
            0, 1, TimeUnit.MINUTES
    );

    public LiveData<DisplayDateTime> getDisplayDateTimeLiveData() {
        return displayDateTimeLiveData;
    }

    /**
     * 定位城市回调
     */
    private final MutableLiveData<CityBean> cityLiveData = new MutableLiveData<>();

    public MutableLiveData<CityBean> getCityLiveData() {
        return cityLiveData;
    }

    /**
     * 场景列表数据
     */
    private final MutableLiveData<List<SceneBean>> sceneListLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<DeviceInfoBean>> roomDevicesLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<AssetBean>> favoriteRoomsLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<DeviceInfoBean>> mFavoriteDevicesLiveData  = new MutableLiveData<>();

    public MutableLiveData<List<DeviceInfoBean>> getFavoriteDevicesLiveData() {
        return mFavoriteDevicesLiveData;
    }

    public MutableLiveData<List<DeviceInfoBean>> getRoomDevicesLiveData() {
        return roomDevicesLiveData;
    }

    public MutableLiveData<List<SceneBean>> getSceneListLiveData() {
        return sceneListLiveData;
    }

    public LiveData<List<UnifiedDeviceState>> getDeviceStatesLiveData(Set<String> deviceIds) {
        return Transformations.distinctUntilChanged(LiveDataReactiveStreams.fromPublisher(
                DataSourceManager.getInstance().getDeviceStates(deviceIds)
                        .subscribeOn(Schedulers.io()).distinctUntilChanged()
        ));
    }

    public LiveData<WeatherData> getWeatherDataLiveData() {
        return Transformations.distinctUntilChanged(LiveDataReactiveStreams.fromPublisher(
                DataSourceManager.getInstance().getWeatherData()
                        .subscribeOn(Schedulers.io()).distinctUntilChanged()
        ));
    }

    public MutableLiveData<DeviceInfoBean> getDeviceInfoChangeLiveData() {
        return deviceInfoChangeLiveData;
    }

    public MutableLiveData<List<AssetBean>> getFavoriteRoomsLiveData() {
        return favoriteRoomsLiveData;
    }

    public void getAllScene(String assetId) {
        Disposable disposable =  SceneNetworkManager.getInstance()
                .getScenes(assetId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sceneListLiveData::postValue,
                        throwable -> sceneListLiveData.postValue(
                                CacheDataManager.getInstance().getAllSceneList(assetId)
                        )
                );
        mCompositeDisposable.add(disposable);
    }

    public void fetchRoomDevices(String homeId, String roomId) {
        if(StringUtil.isBlank(homeId) || StringUtil.isBlank(roomId)) {
            roomDevicesLiveData.postValue(new ArrayList<>());
            return;
        }

        // get cached home info
        AssetBean currentHome = CacheDataManager.getInstance().getCurrentFamily();
        if(null == currentHome || !homeId.contentEquals(currentHome.getId())){
            roomDevicesLiveData.postValue(new ArrayList<>());
            return;
        }

        // get rooms in cached home info
        List<AssetBean> rooms = currentHome.getChildrens();
        if (rooms == null || rooms.isEmpty()) {
            roomDevicesLiveData.postValue(new ArrayList<>());
            return;
        }

        // get room devices
        Predicate<AssetBean> roomPredicate = room -> StringUtil.isNotBlank(room.getId()) &&
                roomId.contentEquals(room.getId());
        Optional<AssetBean> found = rooms.stream().filter(roomPredicate).findFirst();
        if(!found.isPresent()){
            roomDevicesLiveData.postValue(new ArrayList<>());
            return;
        }

        List<DeviceInfoBean> roomDevices = found.get().getDeviceInfoBeans();
        // update room devices
        roomDevicesLiveData.postValue(roomDevices);
    }

    public void subscribeToDeviceStates(List<DeviceInfoBean> roomDevices) {
        if(null != mDevicesStatesDisposable){
            mDevicesStatesDisposable.dispose();
            mCompositeDisposable.remove(mDevicesStatesDisposable);
            mDevicesStatesDisposable = null;
        }

        if(null == roomDevices || roomDevices.isEmpty()){
            mFavoriteDevicesLiveData.postValue(new ArrayList<>());
            return;
        }

        // get device states
        Set<String> deviceIds = roomDevices.parallelStream()
                .map(DeviceInfoBean::getId)
                .collect(Collectors.toSet());
        mDevicesStatesDisposable = DataSourceManager.getInstance().getDeviceStates(deviceIds)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        unifiedDeviceStates -> filterFavoriteDevices(roomDevices, unifiedDeviceStates),
                        throwable -> mFavoriteDevicesLiveData.postValue(new ArrayList<>())
                );
        mCompositeDisposable.add(mDevicesStatesDisposable);
    }

    private void filterFavoriteDevices(
            List<DeviceInfoBean> roomDevices, List<UnifiedDeviceState> unifiedDeviceStates
    ){
        List<UnifiedDeviceState> favoriteDevicesStates = unifiedDeviceStates.stream()
                .filter(UnifiedDeviceState::isLiked)
                .collect(Collectors.toList());
        List<DeviceInfoBean> favoriteDevices =  new ArrayList<>();
        favoriteDevicesStates.forEach(favStates -> {
            Predicate<DeviceInfoBean> deviceInfoPredicate = deviceInfo ->
                    StringUtil.isNotBlank(deviceInfo.getId()) &&
                            deviceInfo.getId().contentEquals(favStates.getDeviceId());
            Optional<DeviceInfoBean> optionalFirst =
                    roomDevices.stream().filter(deviceInfoPredicate).findFirst();
            if(!optionalFirst.isPresent()) {
                return;
            }

            DeviceInfoBean deviceInfoBean = optionalFirst.get();
            deviceInfoBean.setOnlineStatus(favStates.isOnline() ? 1 :0);
            deviceInfoBean.setUnifiedDataPoints(DeviceDataPoint.toUnifiedDataPoints(favStates));
            favoriteDevices.add(deviceInfoBean);
        });
        mFavoriteDevicesLiveData.postValue(favoriteDevices);
    }

    public CommonNavigator createIndicator(ViewPager vp, String[] titles) {
        CommonNavigator commonNavigator = new CommonNavigator(vp.getContext());
        titleViews = new ArrayList<>();
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleViews.add(titleView);
                titleView.setTextSize(22);
                titleView.setNormalColor(context.getResources().getColor(
                        R.color.cen_connect_not_connect_color, null
                ));
                titleView.setSelectedColor(context.getResources().getColor(
                        R.color.cen_connect_step_selected_color, null
                ));
                titleView.setText(titles[index]);
                if (index == 0) {
                    titleView.setTextSize(22);
                }
                titleView.setOnClickListener(v -> {
                    vp.setCurrentItem(index);
                    setSelectedType(index);
                });

                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                GradientLinePagerIndicator linePagerIndicator = new GradientLinePagerIndicator(
                        context
                );
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setPadding(
                        0, DpUtils.dip2px(8), 0, DpUtils.dip2px(8)
                );
                linePagerIndicator.setStartInterpolator(new AccelerateInterpolator());
                linePagerIndicator.setRoundRadius(DpUtils.dip2px(14));
                linePagerIndicator.setLineHeight(DpUtils.dip2px(14));
                linePagerIndicator.setLineWidth(DpUtils.dip2px(14));
                linePagerIndicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                return linePagerIndicator;
            }
        });
        return commonNavigator;
    }

    public void toLocationCity(Context context) {
        try {
            AMapLocationClient mLocationClient = new AMapLocationClient(context);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setGeoLanguage(AppUtil.changeMapLanguage(context));
            mLocationOption.setNeedAddress(true);
            mLocationOption.setOnceLocation(true);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(aMapLocation -> {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        String locationCity = aMapLocation.getCity();
                        List<CityBean> cityList = CacheDataManager.getInstance().getCityList();

                        CityBean matchCity = null;
                        for (CityBean cityBean : cityList) {
                            if (cityBean.getName().toLowerCase().contains(locationCity.toLowerCase())) {
                                matchCity = cityBean;
                                break;
                            }
                        }
                        if (matchCity != null) {
                            cityLiveData.postValue(matchCity);
                        }
                    } else {
                        /// cityLiveData.postValue(null);
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        LgUtils.e("AMapError --> location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            });
            mLocationClient.startLocation();
        } catch (Exception e) {
            /// cityLiveData.postValue(null);
            e.printStackTrace();
        }
    }

    private List<ColorTransitionPagerTitleView> titleViews;

    public void setSelectedType(int selectedIndex) {
        if (titleViews == null || titleViews.isEmpty()) {
            return;
        }
        for (ColorTransitionPagerTitleView textview : titleViews) {
            textview.setTypeface(Typeface.SANS_SERIF);
            textview.setTextSize(24);
        }
        if (selectedIndex < titleViews.size()) {
            titleViews.get(selectedIndex).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            titleViews.get(selectedIndex).setTextSize(28);
        }
    }

    public void gotoDeviceFunctionSelect(
            Context context,
            String deviceId,
            Serializable actionArrayList,
            int conditionOrTask, String deviceInfo
    ) {
        showLoading();
        CallbackListener<SceneDeviceDpBean> listener = new CallbackListener<SceneDeviceDpBean>() {
            @Override
            public void onSuccess(SceneDeviceDpBean data) {
                hideLoading();
                Serializable deviceDpList = (Serializable) (
                        conditionOrTask == Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION ?
                                data.getConditionSupportDps() : data.getActionSupportDps()
                );
                context.startActivity(new Intent(context, SceneListSelectActivity.class)
                        .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_device_select_function))
                        .putExtra("action_array_list", actionArrayList)
                        .putExtra("condition_or_task", conditionOrTask)
                        .putExtra("device_info", deviceInfo)
                        .putExtra("device_dp_list", deviceDpList)
                        .putExtra(
                                Constant.SCENE_LIST_SELECT_TYPE,
                                Constant.SCENE_LIST_SELECT_FOR_DEVICE
                        ));
            }

            @Override
            public void onError(String code, String error) {
                hideLoading();
                ToastUtil.showMsg(error);
            }
        };
        SceneNetworkManager.getInstance().getSceneSupportDpList(deviceId, listener);
    }

    public void setDeviceColor(DeviceInfoBean deviceInfo, int hue, int brightness) {
        if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
            if(MtrDeviceDataUtils.isEmpty(metadata)){
                return;
            }
            DeviceControllerCallback callback = new DeviceControllerCallback() {
                @Override
                public void onSuccess() {
                    DataSourceManager.getInstance().updateDeviceColorState(deviceInfo.getId(), hue);
                }

                @Override
                public void onError(@Nullable Exception e) {
                    if(null == e){
                        return;
                    }
                    Log.e(TAG, "onError: " + e.getLocalizedMessage());
                }
            };
            MtrDeviceControlManager.getInstance(mContext.getApplicationContext())
                    .color(metadata, hue, 100, brightness, callback);
        }else{
            Map<String, Object> command = DeviceCmdConverterUtils
                    .toColorCmd(deviceInfo.getId(), hue, 100, brightness);
            MqttConvertManager.getInstance().publish(deviceInfo.getId(), command);
        }
        DataSourceManager.getInstance().updateDeviceColorState(deviceInfo.getId(), hue);
    }

    public void setDeviceBrightness(DeviceInfoBean deviceInfo, int brightness) {
        Log.d(TAG,
                "setDeviceBrightness: id=" + deviceInfo.getId() + " | brightness=" + brightness);
        if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
            if(MtrDeviceDataUtils.isEmpty(metadata)){
                return;
            }
            DeviceControllerCallback callback = new DeviceControllerCallback() {
                @Override
                public void onSuccess() {
                    DataSourceManager.getInstance().updateDeviceBrightnessState(
                            deviceInfo.getId(), brightness
                    );
                }

                @Override
                public void onError(@Nullable Exception e) {
                    if(null == e){
                        return;
                    }
                    Log.e(TAG, "onError: " + e.getLocalizedMessage());
                }
            };
            MtrDeviceControlManager.getInstance(mContext.getApplicationContext())
                    .brightness(metadata, brightness, callback);
        }else{
            Map<String, Object> command = DeviceCmdConverterUtils
                    .toBrightnessCmd(deviceInfo.getId(), brightness);
            MqttConvertManager.getInstance().publish(deviceInfo.getId(), command);
        }
    }

    public void setDeviceColorTemp(DeviceInfoBean deviceInfo, int temperature) {
        Log.d(TAG, "setDeviceColorTemp: id=" + deviceInfo.getId() +" | temp=" + temperature);
        if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
            if(MtrDeviceDataUtils.isEmpty(metadata)){
                return;
            }
            DeviceControllerCallback callback = new DeviceControllerCallback() {
                @Override
                public void onSuccess() {
                    DataSourceManager.getInstance().updateDeviceColorTempState(
                            deviceInfo.getId(), temperature
                    );
                }

                @Override
                public void onError(@Nullable Exception e) {
                    if(null == e){
                        return;
                    }
                    Log.e(TAG, "onError: " + e.getLocalizedMessage());
                }
            };
            int colorTemperature = Math.min(Math.max((100-temperature), 0), 100);
            MtrDeviceControlManager.getInstance(mContext.getApplicationContext())
                    .colorTemperature(metadata, colorTemperature, callback);
            MtrDeviceControlManager.getInstance(mContext.getApplicationContext())
                    .colorTemperature(metadata, colorTemperature, callback);
        }else{
            Map<String, Object> command = DeviceCmdConverterUtils
                    .toColorTemperatureCmd(deviceInfo.getId(), temperature);
            MqttConvertManager.getInstance().publish(deviceInfo.getId(), command);
        }
    }

    public void toggleDevicePower(DeviceInfoBean deviceInfo) {
        Log.d(TAG, "toggleDevicePower: id=" + deviceInfo.getId());
        boolean power = !DeviceDataPoint.getPower(deviceInfo.getUnifiedDataPoints());
        if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
            if (MtrDeviceDataUtils.isEmpty(metadata)){
                return;
            }

            DeviceControllerCallback callback = new DeviceControllerCallback() {
                @Override
                public void onSuccess() {
                    DataSourceManager.getInstance().updateDevicePowerState(
                            deviceInfo.getId(), power
                    );
                }

                @Override
                public void onError(@Nullable Exception e) {
                    if(null == e){
                        return;
                    }
                    Log.e(TAG, "onError: " + e.getLocalizedMessage());
                }
            };
            MtrDeviceControlManager.getInstance(mContext.getApplicationContext())
                    .power(metadata, power, callback);
        }else{
            Map<String, Object> command = DeviceCmdConverterUtils
                    .toPowerCmd(deviceInfo.getId(), power);
            Disposable disposable = MqttConvertManager.getInstance()
                    .publishDps(deviceInfo.getId(), command)
                    .subscribeOn(Schedulers.io()).subscribe(
                            status -> DataSourceManager.getInstance().updateDevicePowerState(
                                    deviceInfo.getId(), power
                            ),
                            throwable -> Log.e(TAG, "toggleDevicePower: failed to toggle")
                    );
            mCompositeDisposable.add(disposable);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null) {
            executorService.shutdown();
        }
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        mCompositeDisposable.clear();
    }

    /**
     * Updates the device name
     * @param deviceInfo the device info
     * @param name the new name for the device
     */
    public void updateDeviceName(DeviceInfoBean deviceInfo, String name) {
        if(null == deviceInfo || StringUtil.isBlank(name)){
            Log.e(TAG, "updateDeviceName: the device info is null or name is blank");
            return;
        }

        String homeId = CacheDataManager.getInstance().getCurrentHomeId();
        if(StringUtil.isBlank(homeId)){
            Log.e(TAG, "updateDeviceName: home identifier is blank");
            return;
        }

        CallbackListener<Objects> listener =  new CallbackListener<Objects>() {
            @Override
            public void onSuccess(Objects data) {
                Log.d(TAG, "onSuccess: objects=" + new Gson().toJson(data));
                deviceInfo.setName(name);
                deviceInfoChangeLiveData.postValue(deviceInfo);
                EventBus.getDefault().post(new DeviceEvent(
                        DeviceEvent.Type.DEVICE_INFO_CHANGED,
                        DeviceChangeEventOrigin.HOME_PAGE, deviceInfo.getId()
                ));
            }

            @Override
            public void onError(String code, String error) {
                Log.d(TAG, "onError: code=" + code + " | error=" + error);
                ToastUtil.showErrorMsg(error);
                deviceInfoChangeLiveData.postValue(null);
            }
        };

        CommonNetworkManager.getInstance().updateDeviceName(
                homeId, deviceInfo.getUuid(), name, listener
        );
    }

    public void fetchFavoriteRooms(String homeId) {
        if(StringUtil.isBlank(homeId)){
            return;
        }

        AssetBean home = CacheDataManager.getInstance().getCurrentFamily();
        if(null == home){
            favoriteRoomsLiveData.postValue(new ArrayList<>());
            return;
        }

        List<AssetBean> rooms = home.getChildrens();
        if(null == rooms || rooms.isEmpty()){
            favoriteRoomsLiveData.postValue(new ArrayList<>());
        }else{
            favoriteRoomsLiveData.postValue(rooms);
        }
    }
}
