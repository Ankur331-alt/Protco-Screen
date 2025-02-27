package com.smart.rinoiot.common.listener;

public interface CallbackListener<T> {
    void onSuccess(T data);

    void onError(String code, String error);
}
