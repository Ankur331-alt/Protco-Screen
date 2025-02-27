package com.smart.rinoiot.common.rn;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.ble.BleScanConnectManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.DeviceControlManager;
import com.smart.rinoiot.common.utils.ActivityUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.PageActivityPathUtils;
import com.smart.rinoiot.common.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: Rino
 * @Package: com.smart.rino.hybrid.modules
 * @ClassName: RinoNativeModule
 * @Description: java类作用描述
 * @Author: ZhangStar
 * @Emali: ZhangStar666@gmali.com
 * @CreateDate: 2022/9/14 15:57
 * @UpdateUser: 更新者：
 * @UpdateDate: 2022/9/14 15:57
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class RinoNativeModule extends ReactContextBaseJavaModule implements LifecycleEventListener {


    public RinoNativeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        if (reactContext != null) {
            reactContext.addLifecycleEventListener(this);
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "RinoNativeModule";
    }

    /**
     * 关闭容器
     *
     * @param promise callback
     */
    @ReactMethod
    void back(Promise promise) {
        if (getCurrentActivity() != null) {
            if (commonFunPanelListener != null) {
                commonFunPanelListener.backPanel();
            } else {
                getCurrentActivity().finish();
            }
            promise.resolve(true);
        } else {
            promise.resolve(false);
        }
    }

    /**
     * 跳转设备详情页
     *
     * @param promise callback
     */
    @ReactMethod
    void showDeviceDetail(Promise promise) {
        if (getCurrentActivity() == null) return;
        DeviceInfoBean deviceInfoBean = (DeviceInfoBean) getCurrentActivity().getIntent().getSerializableExtra(Constant.PANEL_DEVICE_INFO);
        if (deviceInfoBean != null) {
            BleScanConnectManager.getInstance().bleDisConnect(deviceInfoBean.getUuid(), false);
            ActivityUtils.startActivity(getCurrentActivity(), deviceInfoBean, PageActivityPathUtils.DEVICE_PANEL_SETTING_ACTIVITY);
            promise.resolve(true);
            return;
        }
        promise.resolve(false);
    }

    /**
     * 跳转子面板--网关设备面板跳转网关子设备面板
     *
     * @param promise callback
     */
    @ReactMethod
    void openSubPanel(String deviceId, Promise promise) {
        if (getCurrentActivity() == null) {
            LgUtils.w("跳转子面板--网关设备面板跳转网关子设备面板   getCurrentActivity=null");
            return;
        }
        if (TextUtils.isEmpty(deviceId)) {
            LgUtils.w("跳转子面板--网关设备面板跳转网关子设备面板   设备数据为空");
            return;
        }
        DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(deviceId);
        if (deviceInfo == null) {
            LgUtils.w("跳转子面板--网关设备面板跳转网关子设备面板   设备数据为空");
            return;
        }
        DeviceControlManager.getInstance().gotoPanel(getCurrentActivity(), deviceInfo, false);
        promise.resolve(true);
    }

    /**
     * 跳转设备配网说明
     * 场景：面板配网时，跳转配网说明
     *
     * @param promise callback
     */
    @ReactMethod
    void openDeviceActivatorDescribe(Promise promise) {
        ToastUtil.showMsg("跳转设备配网说明");
        promise.resolve(true);
    }


    /**
     * 启用左滑返回
     * @param promise callback
     *
     */
    @ReactMethod
    void enablePopGesture(Promise promise){
//        back(promise);
    }

    /**
     * 启用左滑返回
     * @param promise callback
     *
     */
    @ReactMethod
    void disablePopGesture(Promise promise){
//        back(promise);
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }

    /**
     * 静态语言
     */
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        if (getCurrentActivity() != null) {
            DeviceInfoBean deviceInfo = (DeviceInfoBean) getCurrentActivity().getIntent().getSerializableExtra(Constant.PANEL_DEVICE_INFO);
            constants.put("appVersion", RNLanguageUtils.getLocalVersion());//当前APP版本
            if (deviceInfo != null) {
                constants.put("rnVersion", "0.70");//当前支持的RN版本
            }
        }
        constants.put("language", RNLanguageUtils.getLanguage(getCurrentActivity()));//当前的语言环境
        constants.put("tempUnit", "");//用户期望的温度单位
        constants.put("timezoneId", "");//用户期望的时区
        return constants;
    }

    /**常用功能点击返回按钮，特殊处理，不finish*/
    public static CommonFunPanelListener commonFunPanelListener;

    public static void setCommonFunPanelListener(CommonFunPanelListener lListener) {
        commonFunPanelListener = lListener;
    }

    public   interface CommonFunPanelListener {
        void backPanel();
    }
}
