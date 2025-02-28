//package com.smart.Rino.common.modules;
//
//import androidx.annotation.NonNull;
//
//import com.facebook.react.bridge.Callback;
//import com.facebook.react.bridge.LifecycleEventListener;
//import com.facebook.react.bridge.Promise;
//import com.facebook.react.bridge.ReactApplicationContext;
//import com.facebook.react.bridge.ReactContextBaseJavaModule;
//import com.facebook.react.bridge.ReactMethod;
//import com.facebook.react.modules.core.DeviceEventManagerModule;
//import com.znkit.smart.listener.EventListener;
//import com.znkit.smart.listener.OnWifiConfigListener;
//import com.znkit.smart.manager.EventManager;
//import com.znkit.smart.manager.NfcManager;
//import com.znkit.smart.nfc.NfcConnectManager;
//import com.znkit.smart.nfc.WifiConnectManager;
//
//public class WifiConfigModule extends ReactContextBaseJavaModule implements LifecycleEventListener, EventListener {
//    public WifiConfigModule(@NonNull ReactApplicationContext reactContext) {
//        super(reactContext);
//        if (reactContext != null) {
//            reactContext.addLifecycleEventListener(this);
//        }
//    }
//
//    @ReactMethod
//    public void wifiEnable(String devId, Callback callback) {
//        DeviceBean deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(devId);
//        if (deviceBean != null) {
//            callback.invoke((deviceBean.isCloudOnline() || deviceBean.getMeta() == null || ((boolean) deviceBean.getMeta().get("wifiEnable"))));
//        } else {
//            callback.invoke(false);
//        }
//    }
//
//    @ReactMethod
//    public void startWifiEnable(String devId, Promise promise) {
//        if (getCurrentActivity() != null && NfcManager.getInstance().getMiffyWifiConfigService() != null) {
//            NfcManager.getInstance().getMiffyWifiConfigService().setOnWifiConfigListener(new OnWifiConfigListener() {
//                @Override
//                public void onSuccess(DeviceBean deviceBean) {
//                    if (promise == null) {
//                        return;
//                    }
//                    promise.resolve(JSON.toJSONString(deviceBean));
//                }
//
//                @Override
//                public void onError(String code, String errorMsg) {
//                    if (promise == null) {
//                        return;
//                    }
//                    promise.reject(code, errorMsg);
//                }
//            });
//            NfcManager.getInstance().getMiffyWifiConfigService().showInputWifi(getCurrentActivity(), devId);
//        }
//    }
//
//    @ReactMethod
//    public void getChannel(String devId, Callback callback) {
//        if (NfcManager.getInstance().getMiffyNfcService() != null) {
//            callback.invoke(NfcManager.getInstance().getMiffyNfcService().getOpenType());
//        } else {
//            callback.invoke("normal");
//        }
//
//    }
//
//
//    @NonNull
//    @Override
//    public String getName() {
//        return "WifiConfigModule";
//    }
//
//    @Override
//    public void onHostResume() {
//        EventManager.getInstance().setOnEventListener(this);
//    }
//
//    @Override
//    public void onHostPause() {
//
//    }
//
//    @Override
//    public void onHostDestroy() {
//        if (NfcManager.getInstance().getMiffyWifiConfigService() != null) {
//            NfcManager.getInstance().getMiffyWifiConfigService().onDestroy();
//        }
//        EventManager.getInstance().removeListener();
//    }
//
//    @Override
//    public void onDataChange(String eventName, Object data) {
//        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
//                .emit(eventName, data);
//    }
//}
