package com.smart.rinoiot.scan.manager;

import android.hardware.Camera;

import com.smart.rinoiot.common.utils.LgUtils;

/**
 * @author tw
 * @time 2022/10/19 21:09
 * @description 打开相机api调用
 */
public class OpenCameraInterface {

    private static final String TAG = OpenCameraInterface.class.getName();

    /**
     * Opens the requested camera with {@link Camera#open(int)}, if one exists.
     *
     * @param cameraId camera ID of the camera to use. A negative value means
     *                 "no preference"
     * @return handle to {@link Camera} that was opened
     */
    public static Camera open(int cameraId) {

        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            LgUtils.w(TAG + "    No cameras!");
            return null;
        }

        boolean explicitRequest = cameraId >= 0;

        if (!explicitRequest) {
            // Select a camera if no explicit camera requested
            int index = 0;
            while (index < numCameras) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(index, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    break;
                }
                index++;
            }

            cameraId = index;
        }

        Camera camera;
        if (cameraId < numCameras) {
            LgUtils.i(TAG + "    Opening camera #" + cameraId);
            camera = Camera.open(cameraId);
        } else {
            if (explicitRequest) {
                LgUtils.w(TAG + "    Requested camera does not exist: " + cameraId);
                camera = null;
            } else {
                LgUtils.i(TAG + "    No camera facing back; returning camera #0");
                camera = Camera.open(0);
            }
        }

        return camera;
    }

    /**
     * Opens a rear-facing camera with {@link Camera#open(int)}, if one exists,
     * or opens camera 0.
     *
     * @return handle to {@link Camera} that was opened
     */
    public static Camera open() {
        return open(-1);
    }

}
