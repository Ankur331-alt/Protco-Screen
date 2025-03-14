package com.smart.rinoiot.scan.manager;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.smart.rinoiot.common.utils.LgUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author tw
 * @time 2022/10/19 21:04
 * @description 该类主要负责设置相机的参数信息，获取最佳的预览界面
 */
public class CameraConfigurationManager {

    private static final String TAG = "CameraConfiguration";

    private static final int MIN_PREVIEW_PIXELS = 480 * 320;
    private static final double MAX_ASPECT_DISTORTION = 0.15;

    private final Context context;

    // 屏幕分辨率
    private Point screenResolution;
    // 相机分辨率
    private Point cameraResolution;

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    void initFromCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        Point theScreenResolution;
        theScreenResolution = getDisplaySize(display);

        screenResolution = theScreenResolution;
        LgUtils.i(TAG+"    Screen resolution: " + screenResolution);

        // 因为换成了竖屏显示，所以不替换屏幕宽高得出的预览图是变形的
        Point screenResolutionForCamera = new Point();

        int tempX = screenResolution.x;
        int tempY = screenResolution.y;

        screenResolutionForCamera.x = tempX;
        screenResolutionForCamera.y = tempY;

        if (tempX < tempY) {
            screenResolutionForCamera.x = tempY;
            screenResolutionForCamera.y = tempX;
        }

        cameraResolution = findBestPreviewSizeValue(parameters, screenResolutionForCamera);
        LgUtils.i(TAG+"    Camera resolution x: " + cameraResolution.x);
        LgUtils.i(TAG+"    Camera resolution y: " + cameraResolution.y);
    }

    @SuppressWarnings("deprecation")
    private Point getDisplaySize(final Display display) {
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) {
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        return point;
    }

    void setDesiredCameraParameters(Camera camera, boolean safeMode) {
        Camera.Parameters parameters = camera.getParameters();

        if (parameters == null) {
            LgUtils.w(TAG+"    Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }

        LgUtils.i(TAG+"    Initial camera parameters: " + parameters.flatten());

        if (safeMode) {
            LgUtils.w(TAG+"    In camera config safe mode -- most settings will not be honored");
        }

        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
        camera.setParameters(parameters);

        Camera.Parameters afterParameters = camera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize != null && (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
            LgUtils.w(TAG+"    Camera said it supported preview size " + cameraResolution.x + 'x' + cameraResolution.y + ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
            cameraResolution.x = afterSize.width;
            cameraResolution.y = afterSize.height;
        }

        // 设置相机预览为竖屏
        camera.setDisplayOrientation(90);
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    public Point getScreenResolution() {
        return screenResolution;
    }

    /**
     * 从相机支持的分辨率中计算出最适合的预览界面尺寸
     */
    private Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            LgUtils.w(TAG+"    Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            return new Point(defaultSize.width, defaultSize.height);
        }

        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, (a, b) -> {
            int aPixels = a.height * a.width;
            int bPixels = b.height * b.width;
            return Integer.compare(bPixels, aPixels);
        });

        if (Log.isLoggable(TAG, Log.INFO)) {
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                previewSizesString.append(supportedPreviewSize.width).append('x').append(supportedPreviewSize.height).append(' ');
            }
            LgUtils.i(TAG+"    Supported preview sizes: " + previewSizesString);
        }

        double screenAspectRatio = (double) screenResolution.x / (double) screenResolution.y;

        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            boolean isCandidatePortrait = realWidth < realHeight;
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;

            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                Point exactPoint = new Point(realWidth, realHeight);
                LgUtils.i(TAG+"    Found preview size exactly matching screen size: " + exactPoint);
                return exactPoint;
            }
        }

        // If no exact match, use largest preview size. This was not a great
        // idea on older devices because
        // of the additional computation needed. We're likely to get here on
        // newer Android 4+ devices, where
        // the CPU is much more powerful.
        if (!supportedPreviewSizes.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewSizes.get(0);
            Point largestSize = new Point(largestPreview.width, largestPreview.height);
            LgUtils.i(TAG+"    Using largest suitable preview size: " + largestSize);
            return largestSize;
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        LgUtils.i(TAG+"    No suitable preview sizes, using default: " + defaultSize);

        return defaultSize;
    }
}
