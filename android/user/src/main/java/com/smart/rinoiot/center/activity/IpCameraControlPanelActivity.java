package com.smart.rinoiot.center.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;

import com.rino.IPCBusinessPluginManager;
import com.smart.rinoiot.center.bean.CameraTiltDirection;
import com.smart.rinoiot.center.bean.IpCameraIntercomStatus;
import com.smart.rinoiot.center.viewmodel.IpCameraDashboardViewModel;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.ipcimpl.AgoraEngineManager;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityIpCameraControlPanelBinding;

import java.util.concurrent.TimeUnit;

/**
 * @author edwin
 */
public class IpCameraControlPanelActivity extends BaseActivity<ActivityIpCameraControlPanelBinding, IpCameraDashboardViewModel> {

    private String uid;
    private String deviceId;
    private boolean isMute = true;

    /**
     * Intent extras keys
     */
    public static final String DEVICE_ID_EXTRA_KEY = "device-id";
    public static final String DEVICE_NAME_EXTRA_KEY = "device-name";

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    @SuppressWarnings("all")
    public void init() {
        StatusBarUtil.hideStatusBar(this);
        // setup panel listeners
        setupListeners();
        deviceId = getIntent().getStringExtra(DEVICE_ID_EXTRA_KEY);
        String deviceName = getIntent().getStringExtra(DEVICE_NAME_EXTRA_KEY);
        Log.d(TAG, "init: device name=" + deviceName + " | device id=" + deviceId);
        if (StringUtil.isBlank(deviceName) || StringUtil.isBlank(deviceId)) {
            showPlaceholder();
            return;
        }

        // fetch camera stream token
        mViewModel.fetchCameraStreamToken(binding.ipcCvCameraPreview, deviceId, deviceName);

        // setup observers
        setupObservers();
    }

    private void showPlaceholder() {
        binding.llVideoInformation.setVisibility(View.GONE);
        binding.llMediaKeys.setVisibility(View.GONE);
        binding.llPanTiltKeys.setVisibility(View.GONE);
        binding.ipcCvCameraPreview.setVisibility(View.GONE);
        binding.ivIpcPlaceholder.setVisibility(View.VISIBLE);
    }

    private void setupObservers() {
        // observe camera stream uuid
        mViewModel.getIpCameraStreamUidLiveData().observe(
                this, uid -> IpCameraControlPanelActivity.this.uid = uid
        );

        // observe intercom status
        mViewModel.getIntercomStatusMutableLiveData().observe(this, status -> {
            if (IpCameraIntercomStatus.IS_OPEN.equals(status)) {
                Drawable intercomOpen = AppCompatResources.getDrawable(
                        this, R.drawable.ic_intercom_active
                );
                binding.ivIntercomControl.setImageDrawable(intercomOpen);
            } else if (IpCameraIntercomStatus.IS_CLOSE.equals(status)) {
                Drawable intercomClosed = AppCompatResources.getDrawable(
                        this, R.drawable.ic_mic_on
                );
                binding.ivIntercomControl.setImageDrawable(intercomClosed);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        // register on back pressed listener
        binding.ivBack.setOnClickListener(v -> closePage());

        // register media controls
        binding.ivIntercomControl.setOnTouchListener((v, event) -> toggleMic(event));
        binding.ivVolume.setOnClickListener(v -> controlVolume());
        binding.ivRecordVideo.setOnClickListener(v -> recordVideo());
        binding.ivCaptureFrame.setOnClickListener(v -> captureCurrentVideoFrame());

        // register pan and tilt
        binding.vTiltUp.setOnTouchListener((v, event) -> cameraTiltControl(
                CameraTiltDirection.Up, event
        ));
        binding.vTiltRight.setOnTouchListener((v, event) -> cameraTiltControl(
                CameraTiltDirection.Right, event
        ));
        binding.vTiltDown.setOnTouchListener((v, event) -> cameraTiltControl(
                CameraTiltDirection.Down, event
        ));
        binding.vTiltLeft.setOnTouchListener((v, event) -> cameraTiltControl(
                CameraTiltDirection.Left, event
        ));
    }

    /**
     * Handles camera tilt control events.
     *
     * @param direction the tilt direction.
     * @param event     the touch event.
     * @return true on event matched, false otherwise.
     */
    private boolean cameraTiltControl(CameraTiltDirection direction, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mViewModel.startCameraTilt(deviceId, direction);
                return true;
            case MotionEvent.ACTION_UP:
                mViewModel.stopCameraTilt(deviceId);
                return true;
            default:
                return false;
        }
    }

    /**
     * Toggles the intercom
     *
     * @param event touch event
     * @return true on event matched, false otherwise.
     */
    private boolean toggleMic(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mViewModel.openIntercom(uid, deviceId);
                return true;
            case MotionEvent.ACTION_UP:
                mViewModel.closeIntercom(uid, deviceId);
                return true;
            default:
                return false;
        }
    }

    /**
     * Controls the video stream volume
     */
    private void controlVolume() {
        if (isMute) {
            AgoraEngineManager.create(null).enableAudio();
        } else {
            AgoraEngineManager.create(null).disableAudio();
        }
        isMute = !isMute;

        // update ui icon
        Drawable muteIcon = AppCompatResources.getDrawable(
                this,
                isMute ? R.drawable.ic_volume_mute :
                        R.drawable.ic_volume
        );
        binding.ivVolume.setImageDrawable(muteIcon);
    }

    private void captureCurrentVideoFrame() {
        // ToDo() implementation
    }

    private void recordVideo() {
        // ToDo() implementation
    }

    @Override
    public ActivityIpCameraControlPanelBinding getBinding(LayoutInflater inflater) {
        return ActivityIpCameraControlPanelBinding.inflate(inflater);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            closePage();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void closePage() {
        if (binding.ipcCvCameraPreview != null) binding.ipcCvCameraPreview.snapshot();
        AppExecutors.getInstance().delayedThread().schedule(() ->
                IPCBusinessPluginManager.create().unregisterAllIPCApi(),
                300, TimeUnit.MILLISECONDS
        );
        AppExecutors.getInstance().delayedThread().schedule(() ->
                        AppManager.getInstance().finishActivity(this),
                500, TimeUnit.MILLISECONDS
        );
    }
}
