package com.smart.rinoiot.scan.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.zxing.Result;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scan.R;
import com.smart.rinoiot.scan.databinding.ActivityScanBinding;
import com.smart.rinoiot.scan.decode.DecodeThread;
import com.smart.rinoiot.scan.listener.CaptureCallback;
import com.smart.rinoiot.scan.manager.BeepManager;
import com.smart.rinoiot.scan.manager.CameraManager;
import com.smart.rinoiot.scan.manager.CaptureActivityHandler;
import com.smart.rinoiot.scan.utils.InactivityTimer;
import com.smart.rinoiot.scan.utils.ZxingUtils;

import java.io.IOException;

/**
 * @author tw
 * @time 2022/10/19 20:04
 * @description 扫码页面
 */
public class ScanActivity extends BaseActivity<ActivityScanBinding, BaseViewModel> implements SurfaceHolder.Callback, CaptureCallback {
    private static final String TAG = ScanActivity.class.getSimpleName();
    private CameraManager cameraManager; // 相机设备管理器
    private ObjectAnimator objectAnimator; // ObjectAnimator相对其父类，功能更加强大了，可以对一个对象的属性进行动画操作
    private InactivityTimer inactivityTimer; // 无通信计时器
    private BeepManager beepManager; // 声音管理器（蜂鸣器）
    private boolean isPause = false; // 是否暂停
    private CaptureActivityHandler handler;
    private boolean isHasSurface = false; // SurfaceView控件是否存在，surfaceCreated()赋值
    private Rect mCropRect = null; // 矩形

    @Override
    public String getToolBarTitle() {
        return getIntent().getStringExtra(Constant.ACTIVITY_TITLE);
    }

    @Override
    public void init() {
        Window window = getWindow();
        // 保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initOpenAndLightData();
        manualData();
        initScan();
    }

    @Override
    public ActivityScanBinding getBinding(LayoutInflater inflater) {
        return ActivityScanBinding.inflate(inflater);
    }

    /**
     * 打开相册+闪退灯控制
     */
    private void initOpenAndLightData() {
        // 闪光灯控制
        binding.tbLight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.tvLight.setText(getString(R.string.rino_scan_turn_off_lights));
                ZxingUtils.openFlashlight(cameraManager);
            } else {
                binding.tvLight.setText(getString(R.string.rino_scan_turn_on_lights));
                ZxingUtils.closeFlashlight();
            }
        });

        // 打开相册
        findViewById(R.id.ll_album).setOnClickListener(v -> {
            // 未做运行时权限，外部加载
            ZxingUtils.openAlbum(this);
        });
    }

    /**
     * 扫码初始化
     */
    private void initScan() {
        // 扫描线性动画(属性动画可暂停)
        float curTranslationY = binding.scanLine.getTranslationY();
        objectAnimator = ObjectAnimator.ofFloat(binding.scanLine, "translationY",
                curTranslationY, DpUtils.getScreenHeight() - DpUtils.dip2px(400));
        // 动画持续的时间
        objectAnimator.setDuration(4000);
        // 线性动画 Interpolator 匀速
        objectAnimator.setInterpolator(new LinearInterpolator());
        // 动画重复次数
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        // 动画如何重复，从下到上，还是重新开始从上到下
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScan();
    }

    @Override
    protected void onPause() {
        pauseScan();
        super.onPause();
    }

    /**
     * 开始扫码
     */
    private void startScan() {
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        if (isPause) {
            // 如果是暂停，扫描动画暂停
            objectAnimator.resume();
            isPause = false;
        } else {
            // 扫描动画开始
            objectAnimator.start();
        }

        // 初始化相机管理
        cameraManager = new CameraManager(getApplication());
        handler = null; // 重置handler
        if (isHasSurface) {
            // 暂停，但未停止，因此表面仍然存在。surfaceCreated()方法不会被调用，所以这里是初始化相机
            initCamera(binding.capturePreview.getHolder());
        } else {
            // 安装回调并等待surfaceCreated()来初始化相机
            binding.capturePreview.getHolder().addCallback(this);
        }
        inactivityTimer.onResume();
    }

    //     初始化相机
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("surfaceHolder为空");
        }
        if (cameraManager.isOpen()) {
            LgUtils.e(TAG + "   摄像头已打开");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            LgUtils.e(TAG + ioe.getMessage());
            ZxingUtils.displayFrameworkBugMessageAndExit(this);
        } catch (RuntimeException e) {
            LgUtils.e(TAG + "    Unexpected error initializing camera   " + e);
            ZxingUtils.displayFrameworkBugMessageAndExit(this);
        }
    }


    // 初始化截取的矩形区域
    private void initCrop() {
        // 获取相机的宽高
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        // 获取布局中扫描框的位置信息
        int[] location = new int[2];

        int cropLeft = location[0];
        int cropTop = location[1] - ZxingUtils.getStatusBarHeight(this);
        mCropRect = new Rect(cropLeft, -cropTop, cameraWidth, cameraHeight - DpUtils.dip2px(100));
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        LgUtils.e(TAG + "    SurfaceHolder:" + holder);
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public Rect getCropRect() {
        return mCropRect;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void handleDecode(Result result, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        if (result != null) {
            if (TextUtils.isEmpty(result.getText())) {
                ToastUtil.showErrorMsg("二维码验证失败");
                finish();
                return;
            }
            postCheck(result);
            LgUtils.w(TAG + "   result  " + result.getText());
        }
    }

    private void postCheck(final Result rawResult) {
        try {
//            QrCodeVO vo = JSON.parseObject(rawResult.getText(), QrCodeVO.class);
//            if (vo == null || vo.getDealerId() == 0) {
//                ToastUtils.show(this, "二维码验证失败");
//                finish();
//                return;
//            }
//            post(vo);
            String strResult = rawResult.getText();
            if (TextUtils.isEmpty(strResult)) {
                ToastUtil.showErrorMsg("二维码验证失败");
                finish();
                return;
            }
            pageTurn(strResult);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showErrorMsg("二维码验证失败");
            finish();
        }
    }

    private void pageTurn(String info) {
        Intent intent = new Intent();
        intent.putExtra(Constant.QR_CODE, info);
        setResult(RESULT_OK, intent);
        LgUtils.e(TAG + "    二维码信息：" + info);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相册返回
        if ((requestCode == ZxingUtils.SELECT_PIC_KITKAT || requestCode == ZxingUtils.SELECT_PIC)
                && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = ZxingUtils.getPath(this, uri);
            Result result = ZxingUtils.scanningImage(path);
            if (result == null) {
                ToastUtil.showErrorMsg("未发现二维码/条形码");
            } else {
                // 数据返回
                String recode = ZxingUtils.recode(result.toString());
                if (TextUtils.isEmpty(recode)) {
                    ToastUtil.showErrorMsg("二维码验证失败");
                    finish();
                    return;
                }
                postCheck(result);
            }
        }
    }

    @Override
    public void onDestroy() {
        // 关闭计时器
        inactivityTimer.shutdown();
        if (objectAnimator != null) {
            // 结束动画
            objectAnimator.end();
        }
        super.onDestroy();
    }

    // 暂停扫码
    private void pauseScan() {
        if (handler != null) {
            // handler退出同步并置空
            handler.quitSynchronously();
            handler = null;
        }
        // 计时器暂停
        inactivityTimer.onPause();
        // 声音管理器关闭
        beepManager.close();
        // 相机管理器关闭驱动
        cameraManager.closeDriver();
        if (!isHasSurface) {
            // 如果SurfaceView还在使用remove
            binding.capturePreview.getHolder().removeCallback(this);
        }
        // 动画暂停
        objectAnimator.pause();
        isPause = true;
    }

    /**
     * 手动添加
     */
    private void manualData() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DpUtils.dip2px(88));
        layoutParams.leftMargin = layoutParams.rightMargin = DpUtils.dip2px(20);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.bottomMargin = DpUtils.dip2px(100);
        binding.llManual.setLayoutParams(layoutParams);
        binding.llManual.setPadding(DpUtils.dip2px(20), DpUtils.dip2px(28), 0, DpUtils.dip2px(28));

        binding.tvManualAdd.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etScanCode.getText())) {
                ToastUtil.showErrorMsg("手动输入扫码内容不能为空");
                return;
            }
            pageTurn(binding.etScanCode.getText().toString());
        });
    }
}
