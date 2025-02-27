package com.smart.rinoiot.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.utils.DpUtils;

/**
 * Description: 仿知乎底部评论弹窗
 * Create by dance, at 2018/12/25
 */
@SuppressLint("ViewConstructor")
public class CircleProgressPopView extends CenterPopupView {
    //    private CircleProgress circleProgress;
    private TextView tv_percent;
    private boolean isDownloadFlag;

    public CircleProgressPopView(@NonNull Context context, boolean isDownloadFlag) {
        super(context);
        this.isDownloadFlag = isDownloadFlag;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_center_circle_progress;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        LinearLayout ll_panel_loading = findViewById(R.id.ll_panel_loading);
        int size = DpUtils.dip2px(150);
        if (isDownloadFlag) {
            size = DpUtils.dip2px(160);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        layoutParams.gravity = Gravity.CENTER;
        ll_panel_loading.setLayoutParams(layoutParams);
        tv_percent = findViewById(R.id.tv_percent);
        tv_percent.setVisibility(isDownloadFlag ? VISIBLE : GONE);
//        circleProgress = findViewById(R.id.circleProgress);
//        circleProgress.setMax(100);
//        circleProgress.setShowText(true);
//        circleProgress.setStrokeWidth(DpUtils.dip2px(4));
//        circleProgress.setTextSize(DpUtils.sp2px(20));
    }

    //    public void setProgress(int progress) {
//        circleProgress.setCurrent(progress);
//    }
    @SuppressLint("SetTextI18n")
    public void setProgress(int progress) {
        if (tv_percent != null) {
            tv_percent.setText(progress + "%");
        }
    }
}