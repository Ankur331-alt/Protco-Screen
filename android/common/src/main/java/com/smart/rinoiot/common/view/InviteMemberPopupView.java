package com.smart.rinoiot.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.listener.OnJoinMemberListener;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/8
 */
@SuppressLint("ViewConstructor")
public class InviteMemberPopupView extends CenterPopupView {

    private OnJoinMemberListener onJoinMemberListener;
    private String hintStr;
    private String title;

    public InviteMemberPopupView(@NonNull Context context, String title, String hintStr, OnJoinMemberListener onJoinMemberListener) {
        super(context);
        this.hintStr = hintStr;
        this.title = title;
        this.onJoinMemberListener = onJoinMemberListener;
    }


    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        TextView tvCancel = findViewById(R.id.tvCancel);
        TextView tvConfirm = findViewById(R.id.tvConfirm);
        EditText editText = findViewById(R.id.edit);
        TextView tvTitle = findViewById(R.id.tvTitle);
        View ivClear = findViewById(R.id.ivClear);

        if (!TextUtils.isEmpty(hintStr)) {
            editText.setHint(hintStr);
        }

        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        tvCancel.setOnClickListener(view -> dismiss());

        ivClear.setOnClickListener(v -> editText.setText(""));

        tvConfirm.setOnClickListener(view -> {
            String account = editText.getText().toString();
            if (TextUtils.isEmpty(account)) {
                return;
            }
            if (onJoinMemberListener != null) {
                onJoinMemberListener.onConfirm(account);
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
        return bindLayoutId != 0 ? bindLayoutId : R.layout._xpopup_invite_member;
    }
}
