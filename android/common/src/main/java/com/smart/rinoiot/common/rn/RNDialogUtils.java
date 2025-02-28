package com.smart.rinoiot.common.rn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.view.CircleProgressPopView;

/**
 * 加载时间过长，进度条展示
 */
@SuppressLint("StaticFieldLeak")
public class RNDialogUtils {
    private AlertDialog loadingDialog;
    private static RNDialogUtils instance;
    private Context mContext;

    public static RNDialogUtils getInstance() {
        if (instance == null) {
            synchronized (RNDialogUtils.class) {
                if (instance == null) {
                    instance = new RNDialogUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context mContext) {
        this.mContext = mContext;
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
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.setCanceledOnTouchOutside(true);
    }

    public void showLoadingPopDialog(AppCompatActivity appCompatActivity) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }
        loadingDialog = new AlertDialog.Builder(mContext).create();
        WindowManager windowManager = appCompatActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = loadingDialog.getWindow().getAttributes();
        layoutParams.height = (int) (display.getHeight() * 0.6);
        layoutParams.width = display.getWidth();
        layoutParams.x = display.getWidth() / 2;
        layoutParams.y = (int) (display.getHeight() * 0.25);
        layoutParams.gravity = Gravity.NO_GRAVITY;
        loadingDialog.getWindow().setAttributes(layoutParams);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        loadingDialog.setCancelable(false);
        loadingDialog.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK);
        loadingDialog.show();
        View view = View.inflate(mContext, R.layout.loading_dialog, null);
        ConstraintLayout ll_dialog = view.findViewById(R.id.ll_dialog);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DpUtils.dip2px(100), DpUtils.dip2px(100));
        params.gravity = Gravity.CENTER;
        ll_dialog.setLayoutParams(params);
        loadingDialog.setContentView(view);
        loadingDialog.setCanceledOnTouchOutside(true);
    }

    public void stopDialogLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        AppExecutors.getInstance().mainThread().execute(() -> {
            if (customCircleProgressPopView != null) {
                customCircleProgressPopView.dismiss();
            }
        });
    }

    CircleProgressPopView customCircleProgressPopView;

    public void showPanelLoadingDialog() {
        if (mContext == null) return;
        if (customCircleProgressPopView != null && customCircleProgressPopView.isShow()) {
            return;
        }
        customCircleProgressPopView = new CircleProgressPopView(mContext,false);
        new XPopup.Builder(mContext).dismissOnBackPressed(false).dismissOnTouchOutside(false).asCustom(customCircleProgressPopView).show();

    }
}
