package com.smart.rinoiot.common.rn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.smart.rinoiot.common.listener.EventListener;
import com.smart.rinoiot.common.manager.EventManager;

/**
 * @ProjectName: Rino
 * @Package: com.smart.rino.hybrid.modules
 * @ClassName: RinoEmitterModule
 * @Description: java类作用描述
 * @Author: ZhangStar
 * @Emali: ZhangStar666@gmali.com
 * @CreateDate: 2022/9/14 15:57
 * @UpdateUser: 更新者：
 * @UpdateDate: 2022/9/14 15:57
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class RinoEmitterModule extends ReactContextBaseJavaModule implements EventListener, LifecycleEventListener {

    public RinoEmitterModule(@Nullable ReactApplicationContext reactContext) {
        super(reactContext);
        if (reactContext != null) {
            reactContext.addLifecycleEventListener(this);
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "RinoEmitterModule";
    }


    @Override
    public void onHostResume() {
        EventManager.getInstance().setOnEventListener(this);
    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        if (getReactApplicationContext() != null) {
            getReactApplicationContext().removeLifecycleEventListener(this);
        }
        EventManager.getInstance().removeListener();
    }

    @Override
    public void onDataChange(String eventName, Object data) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, data);
    }
}
