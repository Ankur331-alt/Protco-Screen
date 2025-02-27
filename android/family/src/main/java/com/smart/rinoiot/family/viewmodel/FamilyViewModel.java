package com.smart.rinoiot.family.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.dsh.matter.management.device.DeviceControllerCallback;
import com.dsh.matter.model.device.StateAttribute;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.smart.device.manager.DeviceNetworkManager;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.base.BaseViewPagerAdapter;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.datastore.persistence.UnifiedDeviceState;
import com.smart.rinoiot.common.device.DeviceCmdConverterUtils;
import com.smart.rinoiot.common.device.DeviceDataPoint;
import com.smart.rinoiot.common.event.DeviceChangeEventOrigin;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.event.FamilyChangeEventTarget;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.location.LocationUtils;
import com.smart.rinoiot.common.location.ip.IpUtils;
import com.smart.rinoiot.common.magicIndicator.GradientLinePagerIndicator;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.CommonNetworkManager;
import com.smart.rinoiot.common.matter.MtrDeviceControlManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.matter.MtrDeviceStatesManager;
import com.smart.rinoiot.common.matter.callback.MtrDeviceStatesCallback;
import com.smart.rinoiot.common.mqtt2.Manager.MqttConvertManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.mqtt2.Manager.TopicManager;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.view.CustomViewPager;
import com.smart.rinoiot.common.view.HomeManagerPopView;
import com.smart.rinoiot.common.voice.VoiceAssistantSettings;
import com.smart.rinoiot.common.weather.worker.FetchWeatherDataWorker;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.bean.AirConditionerTempCtrl;
import com.smart.rinoiot.family.bean.WallSwitchPosition;
import com.smart.rinoiot.family.listener.OnViewDataChangeObserver;
import com.smart.rinoiot.family.manager.FamilyDataChangeManager;
import com.smart.rinoiot.family.manager.FamilyNetworkManager;
import com.smart.rinoiot.family.manager.HomeDataManager;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author author
 */
public class FamilyViewModel extends BaseViewModel implements OnViewDataChangeObserver {
    private static final String TAG = FamilyViewModel.class.getSimpleName();

    private AssetBean currentFamily;

    private final WorkManager mWorkManager;

    private final PeriodicWorkRequest fetchWeatherDataRequest;

    private List<Fragment> fragmentList = new ArrayList<>();

    private List<ColorTransitionPagerTitleView> titleViews;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final MutableLiveData<AssetBean> familyDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AssetBean>> familyListLiveData = new MutableLiveData<>();
    private final MutableLiveData<DeviceInfoBean> deviceInfoChangeLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> voiceAssistantStatusLiveData = new MutableLiveData<>();

    public FamilyViewModel(@NonNull @NotNull Application application) {
        super(application);
        FamilyDataChangeManager.getInstance().setOnViewDataChangeObserver(this);
        // Let the work begin
        fetchWeatherDataRequest = new PeriodicWorkRequest.Builder(
                FetchWeatherDataWorker.class,
                FetchWeatherDataWorker.WEATHER_DATA_WORK_INTERVAL
        ).build();
        mWorkManager = WorkManager.getInstance(application);
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public MutableLiveData<AssetBean> getFamilyDetailLiveData() {
        return familyDetailLiveData;
    }

    public MutableLiveData<List<AssetBean>> getFamilyListLiveData() {
        return familyListLiveData;
    }

    public MutableLiveData<DeviceInfoBean> getDeviceInfoChangeLiveData() {
        return deviceInfoChangeLiveData;
    }

    public LiveData<List<UnifiedDeviceState>> getDeviceStatesLiveData(Set<String> deviceIds) {
        return LiveDataReactiveStreams.fromPublisher(
                DataSourceManager.getInstance().getDeviceStates(deviceIds)
                        .subscribeOn(Schedulers.io()).distinctUntilChanged()
        );
    }

    public MutableLiveData<Boolean> getVoiceAssistantStatusLiveData() {
        return voiceAssistantStatusLiveData;
    }

    public void getFamilyList() {
        FamilyNetworkManager.getInstance().getFamilyListAsync(new CallbackListener<List<AssetBean>>() {
            @Override
            public void onSuccess(List<AssetBean> familyList) {
                if (familyList == null || familyList.size() == 0) {
                    createDefaultFamily();
                } else {
                    AssetBean lastSelectFamilyBean = CacheDataManager.getInstance().getCurrentFamily();
                    if (lastSelectFamilyBean == null) {
                        lastSelectFamilyBean = familyList.get(0);
                    }
                    changeFamily(lastSelectFamilyBean, true);
                }
            }

            @Override
            public void onError(String code, String error) {
                List<AssetBean> familyList = CacheDataManager.getInstance().getFamilyList();
                if (familyList != null && familyList.size() > 0) {
                    AssetBean lastSelectFamilyBean = CacheDataManager.getInstance().getCurrentFamily();
                    if (lastSelectFamilyBean == null) {
                        lastSelectFamilyBean = familyList.get(0);
                    }
                    changeFamily(lastSelectFamilyBean, true);
                } else {
                    familyListLiveData.postValue(null);
                    familyDetailLiveData.postValue(null);
                    ToastUtil.showMsg(error);
                }
            }
        });
    }

    public void getFamilyDetail(String assetId,boolean isRefresh) {
        FamilyNetworkManager.getInstance().getFamilyDetailsAsync(assetId, new CallbackListener<AssetBean>() {
            @Override
            public void onSuccess(AssetBean data) {
                if (currentFamily == null) {
                    MqttManager.getInstance().subscribe(TopicManager.subscribeAssetIdNotify(data.getId()));
                } else {
                    if (!currentFamily.getId().equals(data.getId())) {
                        MqttManager.getInstance().unSubscribe(TopicManager.subscribeAssetIdNotify(currentFamily.getId()));
                        MqttManager.getInstance().subscribe(TopicManager.subscribeAssetIdNotify(data.getId()));
                    }
                }

                CacheDataManager.getInstance().setCurrentHomeId(assetId);
                CacheDataManager.getInstance().saveCurrentFamily(data);
                EventBus.getDefault().post(new DeviceEvent(
                        DeviceEvent.Type.CHANGE_FAMILY, FamilyChangeEventTarget.REFRESH_SCENES
                ));
                if (!isRefresh) {
                    EventBus.getDefault().post(new DeviceEvent(
                            DeviceEvent.Type.CHANGE_FAMILY_NEW));
                }
                currentFamily = data;
                getDeviceList(data);
            }

            @Override
            public void onError(String code, String error) {
                AssetBean familyDetail = CacheDataManager.getInstance().getCurrentFamily();
                if (familyDetail != null) {
                    currentFamily = familyDetail;
                    getDeviceList(familyDetail);
                } else {
                    familyListLiveData.postValue(null);
                    familyDetailLiveData.postValue(null);
                    ToastUtil.showMsg(error);
                }
            }
        });
    }

    private List<String> getAssetIdArray(AssetBean assetBean) {
        List<String> result = new ArrayList<>();
        if (assetBean != null) {
            result.add(assetBean.getId());

            List<AssetBean> childrenArray = assetBean.getChildrens();
            if (childrenArray != null && childrenArray.size() > 0) {
                for (AssetBean item : childrenArray) {
                    result.add(item.getId());
                }
            }
        }
        return result;
    }

    private List<String> getDeviceIdArray(List<DeviceInfoBean> deviceList) {
        if (deviceList == null || deviceList.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        for (DeviceInfoBean item : deviceList) {
            if (!TextUtils.isEmpty(item.getId())) {
                result.add(item.getId());
            }
        }

        return result;
    }

    public void changeFamily(AssetBean familyBean, boolean isRefresh) {
        // 空数据校验
        if (familyBean == null || TextUtils.isEmpty(familyBean.getId())) {
            hideLoading();
            return;
        }

        // 家庭列表校验
        List<AssetBean> familyList = CacheDataManager.getInstance().getFamilyList();
        if (familyList == null || familyList.size() == 0) {
            hideLoading();
            return;
        }

        // 判断所选家庭是否在家庭列表中，如果不在，默认选择第一个
        boolean isFind = false;
        for (AssetBean assetBean : familyList) {
            if (familyBean.getId().equals(assetBean.getId())) {
                isFind = true;
                break;
            }
        }
        if (!isFind) {
            familyBean = familyList.get(0);
        }

        // 切换的家庭与原家庭一致校验
        if (!isRefresh && currentFamily != null && !TextUtils.isEmpty(currentFamily.getId()) && currentFamily.getId().equals(familyBean.getId())) {
            hideLoading();
            return;
        }

        // clear cached data here
        DataSourceManager.getInstance().clear();

        AssetBean mAssetBean = null;
        for (AssetBean familyItemBean : familyList) {
            if (familyItemBean != null && familyItemBean.getId().equals(familyBean.getId())) {
                familyItemBean.setCurrentSelected(true);
                mAssetBean = familyItemBean;
            } else {
                if (familyItemBean != null) {
                    familyItemBean.setCurrentSelected(false);
                }
            }
        }

        if (mAssetBean == null) {
            familyList.get(0).setCurrentSelected(true);
            mAssetBean = familyList.get(0);
        }

        familyListLiveData.postValue(familyList);
        notifyData(familyList);

        getFamilyDetail(mAssetBean.getId(),isRefresh);
    }

    private void getDeviceList(AssetBean familyDetail) {
        List<String> assetIdArray = getAssetIdArray(familyDetail);
        FamilyNetworkManager.getInstance().getHomeDeviceListAsync(assetIdArray, new CallbackListener<List<DeviceInfoBean>>() {
            @Override
            public void onSuccess(List<DeviceInfoBean> data) {
                addDeviceListToAsset(data);
            }

            @Override
            public void onError(String code, String error) {
                List<DeviceInfoBean> deviceInfoList = CacheDataManager.getInstance().getAllDeviceList(assetIdArray.get(0));
                if (deviceInfoList != null) {
                    addDeviceListToAsset(deviceInfoList);
                } else {
                    familyListLiveData.postValue(null);
                    familyDetailLiveData.postValue(null);
                }
            }
        });
    }

    /**
     * 设备数据处理
     */
    private void addDeviceListToAsset(List<DeviceInfoBean> data) {
        currentFamily.setDeviceInfoBeans(data);
        if (currentFamily.getChildrens() != null && currentFamily.getChildrens().size() > 0) {
            for (AssetBean children : currentFamily.getChildrens()) {
                List<DeviceInfoBean> deviceList = new ArrayList<>();
                for (DeviceInfoBean deviceInfo : data) {
                    if (TextUtils.equals(children.getId(), deviceInfo.getAssetId())) {
                        deviceList.add(deviceInfo);
                    }
                }
                children.setDeviceInfoBeans(deviceList);
            }
        }

        CacheDataManager.getInstance().saveCurrentFamily(currentFamily);
        familyDetailLiveData.postValue(currentFamily);
        EventBus.getDefault().post(new DeviceEvent(
                DeviceEvent.Type.CHANGE_FAMILY, FamilyChangeEventTarget.REFRESH_DEVICES)
        );
    }

    void notifyData(List<AssetBean> familyList) {
        HomeDataManager.getInstance().setAssetBeans(familyList);
        AssetBean instanceBean = HomeDataManager.getInstance().getAssetBean();
        if (instanceBean != null) {
            for (AssetBean assetBean : familyList) {
                if (assetBean.getId().equals(instanceBean.getId())) {
                    HomeDataManager.getInstance().setAssetBean(assetBean);
                }
            }
        }
        FamilyDataChangeManager.getInstance().changeApiDataSuccess();
    }

    private void createDefaultFamily() {
        FamilyNetworkManager.getInstance().createDefaultFamily("Rino Home", new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                getFamilyList();
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                hideLoading();
            }
        });
    }

    public CommonNavigator createIndicator(ViewPager vp, List<String> titles) {
        CommonNavigator commonNavigator = new CommonNavigator(vp.getContext());
        titleViews = new ArrayList<>();
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                titleViews.add(colorTransitionPagerTitleView);
                colorTransitionPagerTitleView.setTextSize(24);
                colorTransitionPagerTitleView.setNormalColor(context.getResources().getColor(
                        R.color.cen_connect_step_selected_color, null
                ));
                colorTransitionPagerTitleView.setSelectedColor(context.getResources().getColor(
                        R.color.cen_connect_step_selected_color, null
                ));
                colorTransitionPagerTitleView.setText(titles.get(index));
                if (index == 0) {
                    colorTransitionPagerTitleView.setTextSize(28);
                    colorTransitionPagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                colorTransitionPagerTitleView.setOnClickListener(v -> {
                    vp.setCurrentItem(index);
                    setSelectedType(index);
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                //这是指示器跟文案内容一样长是必须设置setMode(MODE_WRAP_CONTENT)这个模式，指示器颜色渐变时，必须实现GradientLinePagerIndicator
                GradientLinePagerIndicator linePagerIndicator = new GradientLinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setPadding(0, DpUtils.dip2px(8), 0, DpUtils.dip2px(8));
                linePagerIndicator.setStartInterpolator(new AccelerateInterpolator());
                linePagerIndicator.setRoundRadius(DpUtils.dip2px(16));
                linePagerIndicator.setLineHeight(DpUtils.dip2px(16));
                linePagerIndicator.setLineWidth(DpUtils.dip2px(16));
                linePagerIndicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                return linePagerIndicator;
            }
        });
        return commonNavigator;
    }

    @Override
    public void onViewDataChange() {
        getFamilyList();
    }

    /**
     * 展示家庭管理弹框
     */
    public void showHomeManagerPopView(Activity appCompatActivity, View view, CustomViewPager viewPager) {
        List<AssetBean> assetBeans = new ArrayList<>();
        if (getFamilyListLiveData().getValue() != null) {
            assetBeans.addAll(getFamilyListLiveData().getValue());
        }

        new XPopup.Builder(appCompatActivity).dismissOnTouchOutside(true)
                .atView(view)
                .popupWidth(DpUtils.dip2px(225))
                .offsetX(DpUtils.dip2px(118))
                .offsetY(-view.getHeight())
                .asCustom(new HomeManagerPopView(appCompatActivity, assetBeans, (isHomeManager, assetBean) -> {
                    if (isHomeManager&&viewPager!=null&&viewPager.getChildCount()>3) {
                        SharedPreferenceUtil.getInstance().put(Constant.FAMILY_MANAGER, true);
                        viewPager.setCurrentItem(3);
                    } else {
                        showLoading();
                        changeFamily(assetBean, false);
                    }
                })).show();
    }

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

    /**
     * Binds a bar code to an asset
     *
     * @param barcode barcode
     */
    private void scanBindToAsset(String barcode) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("assetId", CacheDataManager.getInstance().getCurrentHomeId());
        map.put("barcode", barcode);
        map.put("bindMode", 0);
        DeviceNetworkManager.getInstance().bindToAsset(map, new CallbackListener<DeviceInfoBean>() {
            @Override
            public void onSuccess(DeviceInfoBean data) {
                ToastUtil.showMsg(getString(R.string.rino_nfc_bind_success));
                LgUtils.w(TAG + "  scanBindToAsset data=" + new Gson().toJson(data));
                getFamilyList();
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //首页扫一扫
        if (requestCode != Constant.SCAN_BIND_DEVICE_CODE || null == data) {
            return;
        }

        String barcode = data.getStringExtra(Constant.QR_CODE);
        if (!TextUtils.isEmpty(barcode)) {
            scanBindToAsset(barcode);
        } else {
            ToastUtil.showMsg("扫码获取二维码数据为空");
        }
    }

    public BaseViewPagerAdapter createFragmentAdapter(AppCompatActivity activity, List<Fragment> tabFragments) {
        fragmentList.clear();
        fragmentList = tabFragments;
        return new BaseViewPagerAdapter(activity.getSupportFragmentManager(), tabFragments);
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public void discoverDevice() {
        FamilyNetworkManager.getInstance().discoverDevice(new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                LgUtils.w(TAG+" discoverDevice onSuccess  data="+new Gson().toJson(data));
            }

            @Override
            public void onError(String code, String error) {
                LgUtils.w(TAG+" discoverDevice onError code="+code+"   error= "+error);
            }
        });
    }

    public void adjustAirConditionerTemp(
            AirConditionerTempCtrl adjustment, DeviceInfoBean deviceInfo
    ) {
        Log.d(TAG,
                "adjustAirConditionerTemp: id=" + deviceInfo.getId() +
                        " | adjustment=" + adjustment
        );

        // ToDo() 1. get the AC's current temperature
        //  2. increment the value by delta t = 1
        //  3  tell the network about it
        //  4. return status and the position for the UI to work its magic.
    }

    public void setDeviceColor(DeviceInfoBean deviceInfo, int hue, int brightness) {
        Log.d(TAG,
                "setDeviceColor: id=" + deviceInfo.getId() +
                        " | hue=" + hue + " + | brightness=" + brightness
        );

        if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
            if(MtrDeviceDataUtils.isEmpty(metadata)){
                return;
            }

            DeviceControllerCallback callback = new DeviceControllerCallback() {
                @Override
                public void onSuccess() {
                    DataSourceManager.getInstance().updateDeviceColorState(
                            deviceInfo.getId(), hue
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
                    .color(metadata, hue, 100, brightness, callback);
        }else{
            Map<String, Object> command = DeviceCmdConverterUtils
                    .toColorCmd(deviceInfo.getId(), hue, 100, brightness);
            MqttConvertManager.getInstance().publish(deviceInfo.getId(), command);
        }
    }

    public void setDeviceBrightness(DeviceInfoBean deviceInfo, int brightness) {
        Log.d(TAG,
                "setDeviceBrightness: id=" + deviceInfo.getId() +" | brightness=" + brightness
        );
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
        Log.d(TAG,
                "setDeviceColorTemp: id=" + deviceInfo.getId() + " | temp=" + temperature
        );
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
        }else{
            Map<String, Object> command = DeviceCmdConverterUtils
                    .toColorTemperatureCmd(deviceInfo.getId(), temperature);
            MqttConvertManager.getInstance().publish(deviceInfo.getId(), command);
        }
    }

    public void toggleDevicePower(DeviceInfoBean deviceInfo, int position) {
        Log.d(TAG, "toggleDevicePower: id=" + deviceInfo.getId() + " | pos=" + position);
        boolean power = !DeviceDataPoint.getPower(deviceInfo.getUnifiedDataPoints());
        if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
            if(MtrDeviceDataUtils.isEmpty(metadata)){
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

    public void toggleDeviceLikeStatus(DeviceInfoBean deviceInfo) {
        boolean liked = deviceInfo.isHomeScreen();
        Disposable disposable = CommonNetworkManager.getInstance()
                .likeRoomDevice(deviceInfo.getId(), !liked)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        status -> {
                            DataSourceManager.getInstance().updateDeviceLikeStatus(
                                    deviceInfo.getId(), !liked
                            );
                        },
                        throwable -> {
                            ToastUtil.showErrorMsg(throwable.getLocalizedMessage());
                        }
                );
        mCompositeDisposable.add(disposable);
    }

    public void toggleDeviceSwitches(DeviceInfoBean deviceInfo, WallSwitchPosition switchPos) {
        Log.d(TAG,
                "toggleDeviceSwitches: id=" + deviceInfo.getId() +" | sw=" + switchPos.ordinal()
        );

        boolean [] switches = DeviceDataPoint.getSwitches(deviceInfo.getUnifiedDataPoints());
        Log.d(TAG, "toggleDeviceSwitches: switches" + new Gson().toJson(switches));
        boolean power = !switches[switchPos.ordinal()];
        if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
            if(MtrDeviceDataUtils.isEmpty(metadata)){
                return;
            }
            DeviceControllerCallback callback = new DeviceControllerCallback() {
                @Override
                public void onSuccess() {
                    DataSourceManager.getInstance().updateDeviceSwitchState(
                            deviceInfo.getId(), switchPos.ordinal(), power
                    );
                }

                @Override
                public void onError(@Nullable Exception e) {
                    if(null == e){
                        return;
                    }
                    Log.e(TAG,"onError: " + e.getLocalizedMessage());
                }
            };
            MtrDeviceControlManager.getInstance(mContext.getApplicationContext())
                    .power(metadata, power, callback);
        }else{
            Map<String, Object> command = DeviceCmdConverterUtils
                    .toPowerCmd(deviceInfo.getId(), power, switchPos.ordinal());
            Disposable disposable = MqttConvertManager.getInstance()
                    .publishDps(deviceInfo.getId(), command)
                    .subscribeOn(Schedulers.io()).subscribe(
                            status -> DataSourceManager.getInstance().updateDeviceSwitchState(
                                    deviceInfo.getId(), switchPos.ordinal(), power
                            ),
                            throwable -> Log.e(TAG, "toggleDevicePower: failed to toggle")
                    );
            mCompositeDisposable.add(disposable);
        }
    }

    public void requestDeviceStates(List<DeviceInfoBean> devices){
        if(devices.isEmpty()){
            return;
        }

        // request matter devices states
        List<DeviceInfoBean> matterDevices = devices.parallelStream()
                .filter(MtrDeviceDataUtils::isMatterDevice).collect(Collectors.toList());
        matterDevices.forEach(deviceInfoBean -> {
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfoBean.getMetaInfo());
            if(MtrDeviceDataUtils.isEmpty(metadata)){
                return;
            }
            MtrDeviceStatesCallback callback = new MtrDeviceStatesCallback() {
                @Override
                public void onReport(HashMap<StateAttribute, Object> states) {
                    Log.d(TAG, "onReport: deviceId=" + deviceInfoBean.getId() +
                            " | states=" +  new Gson().toJson(states)
                    );
                    DataSourceManager.getInstance().updateDeviceStates(
                            deviceInfoBean.getId(), states
                    );
                }
            };
            MtrDeviceStatesManager.getInstance(mContext.getApplicationContext())
                    .queryDeviceStatesAsync(metadata,callback);
        });

        // request wifi device states
        Set<String> wifiDeviceIds = devices.parallelStream()
                .filter(deviceInfoBean -> !MtrDeviceDataUtils.isMatterDevice(deviceInfoBean))
                .map(DeviceInfoBean::getId)
                .filter(StringUtil::isNotBlank)
                .collect(Collectors.toSet());
        CommonNetworkManager.getInstance().getMultiDeviceDpInfoAsync(wifiDeviceIds);
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
                deviceInfo.setName(name);
                deviceInfoChangeLiveData.postValue(deviceInfo);
                EventBus.getDefault().post(new DeviceEvent(
                        DeviceEvent.Type.DEVICE_INFO_CHANGED,
                        DeviceChangeEventOrigin.HOUSEHOLD, deviceInfo.getId()
                ));
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
                deviceInfoChangeLiveData.postValue(null);
            }
        };

        CommonNetworkManager.getInstance().updateDeviceName(
                homeId, deviceInfo.getUuid(), name, listener
        );
    }

    public void syncDeviceLikedStatus(AssetBean familyDetail) {
        if(null == familyDetail){
            return;
        }

        // get the asset identifiers;
        Set<String> assetIds = new HashSet<>();
        assetIds.add(familyDetail.getId());
        List<AssetBean> rooms = familyDetail.getChildrens();
        if(null != rooms){
            Set<String> roomIds = rooms.parallelStream()
                    .map(AssetBean::getId)
                    .collect(Collectors.toSet());
            assetIds.addAll(roomIds);
        }

        Disposable disposable = CommonNetworkManager.getInstance()
                .fetchFavoriteDevices(assetIds)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        deviceList-> {
                            Set<String> deviceIds = deviceList.stream()
                                    .filter(DeviceInfoBean::isHomeScreen)
                                    .map(DeviceInfoBean::getId)
                                    .filter(StringUtil::isNotBlank).collect(Collectors.toSet());
                            DataSourceManager.getInstance().updateDeviceLikeStatus(
                                    deviceIds, true
                            );
                        },
                        throwable -> Log.e(TAG, "syncDeviceLikedStatus: " +
                                throwable.getLocalizedMessage()
                        )
                );
        mCompositeDisposable.add(disposable);
    }

    public void updateLastKnownLocation(){
        Observable<String> ipAddressObservable = IpUtils.getDevicePublicIpAddress();
        Disposable disposable = ipAddressObservable.subscribeOn(Schedulers.io())
                .flatMap(LocationUtils::requestGeolocation)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        geolocation ->
                                CacheDataManager.getInstance()
                                        .cacheDeviceLastKnownLocation(geolocation),
                        throwable -> Log.e(
                                TAG, "updateLastKnownLocation: error=" +
                                        throwable.getLocalizedMessage()
                        ),
                        () -> {
                            Log.d(TAG, "updateLastKnownLocation: completed");
                            mWorkManager.enqueueUniquePeriodicWork(
                                    FetchWeatherDataWorker.WEATHER_DATA_WORK_NAME,
                                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                                    fetchWeatherDataRequest
                            );
                        }
                );
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear();
    }

    public void toggleVoiceAssistantStatus() {
        VoiceAssistantSettings settings = CacheDataManager.getInstance().getVoiceAssistantSettings();
        settings.setListening(!settings.isListening());
        CacheDataManager.getInstance().cacheVoiceAssistantSettings(settings);
        voiceAssistantStatusLiveData.postValue(settings.isListening());
    }

    public void getVoiceAssistantStatus() {
        VoiceAssistantSettings settings = CacheDataManager.getInstance().getVoiceAssistantSettings();
        voiceAssistantStatusLiveData.postValue(settings.isListening());
    }
}
