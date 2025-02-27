package com.smart.rinoiot.common.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lxj.xpopup.core.BottomPopupView;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.listener.DialogOnListener;

import androidx.annotation.NonNull;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/8
 */
public class ExitCreateFamilyPopupView extends BottomPopupView {

    private DialogOnListener dialogOnListener;

    public ExitCreateFamilyPopupView(@NonNull Context context,DialogOnListener dialogOnListener) {
        super(context);
        this.dialogOnListener = dialogOnListener;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        TextView tvCancel = findViewById(R.id.tvCancel);
        TextView tvConfirm = findViewById(R.id.tvConfirm);

        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogOnListener!=null){
                    dialogOnListener.onConfirm();
                }
            }
        });
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout._xpopup_exit_create_family;
    }
}
