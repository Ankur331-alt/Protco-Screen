package com.smart.rinoiot.scan.listener;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;

import com.google.zxing.Result;
import com.smart.rinoiot.scan.manager.CameraManager;

/**
 * @author tw
 * @time 2022/10/20 11:20
 * @description 扫码回调接口
 */
public interface CaptureCallback {
    Rect getCropRect();

    Handler getHandler();

    CameraManager getCameraManager();

    void handleDecode(Result result, Bundle bundle);

    void setResult(int resultCode, Intent intent);

    void finish();
}
