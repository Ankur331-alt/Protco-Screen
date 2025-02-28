package com.smart.rinoiot.common.rn;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.gson.Gson;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.network.RetrofitUtils;

import org.jetbrains.annotations.NotNull;

public class RinoNetworkModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    public RinoNetworkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        if (reactContext != null) {
            reactContext.addLifecycleEventListener(this);
        }
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

    @NonNull
    @NotNull
    @Override
    public String getName() {
        return "RinoNetworkModule";
    }

    @ReactMethod
    public void rinoApiRequest(String url, ReadableMap postData, Promise promise) {
        if (url.startsWith("/api/")) url = url.replace("/api/", "");
        RetrofitUtils.getService().rinoRnApiRequest(url, postData.toHashMap()).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (promise != null) promise.resolve(new Gson().toJson(result));
            }

            @Override
            public void onError(String error, String msg) {
                if (promise != null) promise.reject("10001", msg);
            }
        });
    }
}

