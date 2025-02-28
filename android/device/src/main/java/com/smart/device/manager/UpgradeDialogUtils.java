package com.smart.device.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.smart.device.R;
import com.smart.device.activity.DeviceOTAUpgradeActivity;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.utils.AppExecutors;

import java.util.concurrent.TimeUnit;


/**
 * OTA升级 加载进度条
 */
@SuppressLint({"InflateParams", "StaticFieldLeak"})
public class UpgradeDialogUtils {
    private AlertDialog loadingDialog;
    public static UpgradeDialogUtils instance;
    private Context mContext;
    private ImageView iv_progress;
    private TextView tv_progress;
    private String defuatProgress;

    public static UpgradeDialogUtils getInstance() {
        if (instance == null) {
            synchronized (UpgradeDialogUtils.class) {
                if (instance == null) {
                    instance = new UpgradeDialogUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context mContext, String defuatProgress) {
        this.mContext = mContext;
        this.defuatProgress = defuatProgress;
    }

    public void showLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }
        loadingDialog = new AlertDialog.Builder(mContext).create();
        WindowManager.LayoutParams params = loadingDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        loadingDialog.getWindow().setAttributes(params);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        loadingDialog.setCancelable(false);
        loadingDialog.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK);
        loadingDialog.show();
        View view = LayoutInflater.from(mContext).inflate(R.layout.upgrade_loading_dialog, null);
        iv_progress = view.findViewById(R.id.iv_progress);
        tv_progress = view.findViewById(R.id.tv_progress);
        ImageView iv_cancel = view.findViewById(R.id.iv_cancel);
        loadingDialog.setContentView(view);
        loadingDialog.setCanceledOnTouchOutside(false);
        tv_progress.setText(defuatProgress);
        startLoading();
        if (listener != null) {
            iv_cancel.setOnClickListener(listener);
        }
    }

    public void stopDialogLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * 加载动画
     */
    private Animation animation;

    public void startLoading() {
        animation = AnimationUtils.loadAnimation(mContext,R.anim.video_loading_anim);
        if (iv_progress != null) {
            iv_progress.startAnimation(animation);
        }
    }

    /**
     * 停止动画
     */
    public void stopLoading() {
        if (animation != null) {
            animation.cancel();
        }
        if (iv_progress != null) {
            iv_progress.clearAnimation();
        }
    }

    /**
     * 设置进度条及完成文案
     *
     * @param otaStatus 1: 下载完成;2：下载中;3:下载错误
     * @param progress  进度条
     */
    public void setUpgradeOtaInfo(int otaStatus, String progress) {
        if (otaStatus == 1) {//下载完成
            stopLoading();
            if (iv_progress != null) {
                iv_progress.setImageResource(R.drawable.icon_ota_upgrade_success);
            }
            AppExecutors.getInstance().delayedThread().schedule(() -> {
                stopDialogLoading();
                AppManager.getInstance().finishActivity(DeviceOTAUpgradeActivity.class);
            }, 2000, TimeUnit.MILLISECONDS);
        }
        if (tv_progress != null) {
            tv_progress.setText(progress);
        }
        if (otaStatus == 3) {
            AppExecutors.getInstance().delayedThread().schedule(this::stopDialogLoading, 2000, TimeUnit.MILLISECONDS);
        }

    }

    private View.OnClickListener listener;

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
