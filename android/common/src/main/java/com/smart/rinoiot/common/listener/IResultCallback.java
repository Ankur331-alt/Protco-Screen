package com.smart.rinoiot.common.listener;

public interface IResultCallback {
    void onSuccess(String data);

    void onError(String code, String error);
}
