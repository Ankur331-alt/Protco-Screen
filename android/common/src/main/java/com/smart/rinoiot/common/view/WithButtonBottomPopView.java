package com.smart.rinoiot.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.common.R;

/**
 * Description: 带按钮的底部提示弹窗
 * Create by dance, at 2018/12/25
 */
@SuppressLint("SetTextI18n")
public class WithButtonBottomPopView extends BottomPopupView implements View.OnClickListener {

    private String title = "";
    private String content = "";

    private TextView tvTitle;
    private TextView tvContent;

    private OnConfirmListener confirmListener;

    public WithButtonBottomPopView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom_with_button;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        getPopupImplView().setBackground(XPopupUtils.createDrawable(getResources().getColor(com.lxj.xpopup.R.color._xpopup_light_color),
                popupInfo.borderRadius, popupInfo.borderRadius, 0,0));

        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);

        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setVisibility(VISIBLE);
        }
        tvContent.setText(content);
    }

    public WithButtonBottomPopView setOnConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public WithButtonBottomPopView setTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCancel) {
            if (confirmListener != null) confirmListener.onCancel();
            dismiss();
        } else if (v.getId() == R.id.tvConfirm) {
            if (confirmListener != null) confirmListener.onConfirm();
            dismiss();
        }
    }

    public interface OnConfirmListener {
        void onConfirm();
        void onCancel();
    }
}