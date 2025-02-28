package com.smart.rinoiot.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.listener.DialogOnListener;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/9
 */
@SuppressLint("ViewConstructor")
public class JoinFamilyPopupView extends CenterPopupView {
    private DialogOnListener dialogOnListener;

    public JoinFamilyPopupView(@NonNull Context context, DialogOnListener dialogOnListener) {
        super(context);
        this.dialogOnListener = dialogOnListener;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        TextView tvCancel = findViewById(R.id.tvCancel);
        TextView tvConfirm = findViewById(R.id.tvConfirm);
        TextView tvRefuse = findViewById(R.id.tvRefuse);

        tvCancel.setOnClickListener(view -> dismiss());

        tvConfirm.setOnClickListener(view -> {
            if (dialogOnListener != null) {
                dialogOnListener.onConfirm();
            }
            dismiss();
        });

        tvRefuse.setOnClickListener(view -> {
            if (dialogOnListener != null) {
                dialogOnListener.onCancel();
            }
            dismiss();
        });
        applyTheme();
    }

    protected void applyTheme() {
        if (popupInfo.isDarkTheme) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
        centerPopupContainer.setBackground(XPopupUtils.createDrawable(getResources().getColor(popupInfo.isDarkTheme ? R.color._xpopup_title_color
                : R.color._xpopup_dark_color), popupInfo.borderRadius));
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout._xpopup_join_family;
    }
}
