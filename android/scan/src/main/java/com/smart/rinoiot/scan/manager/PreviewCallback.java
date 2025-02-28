package com.smart.rinoiot.scan.manager;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import com.smart.rinoiot.common.utils.LgUtils;

/**
 * @author tw
 * @time 2022/10/19 21:07
 * @description Callback interface used to deliver copies of preview frames as
 * they are displayed.
 */
public class PreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = PreviewCallback.class.getSimpleName();

    private final CameraConfigurationManager configManager;
    private Handler previewHandler;
    private int previewMessage;

    PreviewCallback(CameraConfigurationManager configManager) {
        this.configManager = configManager;
    }

    void setHandler(Handler previewHandler, int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Point cameraResolution = configManager.getCameraResolution();
        Handler thePreviewHandler = previewHandler;
        if (cameraResolution != null && thePreviewHandler != null) {
            Message message = thePreviewHandler.obtainMessage(previewMessage, cameraResolution.x, cameraResolution.y, data);
            message.sendToTarget();
            previewHandler = null;
        } else {
            LgUtils.d(TAG + "    Got preview callback, but no handler or resolution available");
        }
    }

}
