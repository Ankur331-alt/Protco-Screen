package com.smart.rinoiot.common.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart.rinoiot.common.databinding.DialogTextInputBinding;
import com.smart.rinoiot.common.utils.StringUtil;

import java.util.Objects;

/**
 * @author edwin
 */
public class TextInputDialog extends Dialog {

    private String mTitle;

    private String mHint;

    private String mPositiveBtnText;

    private OnClickListener mPositiveBtnOnClickListener;

    private String mNegativeBtnText;

    private OnClickListener mNegativeBtnOnClickListener;

    private final InputMethodManager imm;

    private final DialogTextInputBinding mBinding;

    public TextInputDialog(@NonNull Context context) {
        super(context);
        mBinding = DialogTextInputBinding.inflate(getLayoutInflater());
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
    }

    private void initView(){
        // setup listeners
        mBinding.btnCancel.setOnClickListener(v -> {
            hideSoftInputFromWindow();
            if(null != this.mNegativeBtnOnClickListener){
                mNegativeBtnOnClickListener.onClick("");
            }
            dismiss();
        });

        mBinding.btnConfirm.setOnClickListener(v -> {
            hideSoftInputFromWindow();
            String text = mBinding.etInputText.getText().toString();
            if(null != this.mPositiveBtnOnClickListener){
                mPositiveBtnOnClickListener.onClick(text);
            }
            dismiss();
        });

        setOnShowListener(dialog -> {
            if(StringUtil.isNotBlank(this.mTitle)){
                mBinding.tvTitle.setText(this.mTitle);
            }

            if(StringUtil.isNotBlank(this.mHint)){
                mBinding.etInputText.setHint(this.mHint);
            }

            if(StringUtil.isNotBlank(this.mPositiveBtnText)){
                mBinding.btnConfirm.setText(this.mPositiveBtnText);
            }

            if(StringUtil.isNotBlank(this.mNegativeBtnText)){
                mBinding.btnCancel.setText(this.mNegativeBtnText);
            }

            mBinding.etInputText.setText("");
            mBinding.etInputText.requestFocus();
        });

        Objects.requireNonNull(this.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);
        setContentView(mBinding.getRoot());
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setHint(String hint) {
        this.mHint = hint;
    }

    public void setPositiveBtnText(String text) {
        this.mPositiveBtnText = text;
    }

    public void setPositiveBtnOnClickListener(OnClickListener onClickListener) {
        this.mPositiveBtnOnClickListener = onClickListener;
    }

    public void setNegativeBtnText(String text) {
        this.mNegativeBtnText = text;
    }

    public void setNegativeBtnOnClickListener(OnClickListener onClickListener) {
        this.mNegativeBtnOnClickListener = onClickListener;
    }

    private void hideSoftInputFromWindow() {
        Log.d("Dialog", "hideSoftInputFromWindow: status" +  imm.isActive());
        if(!imm.isActive()) {
            return;
        }
        imm.hideSoftInputFromWindow(
                mBinding.getRoot().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(false);
    }

    public interface OnClickListener{
        /**
         * Invoked when a button is clicked
         * @param text the text
         */
        void onClick(@Nullable String text);
    }

    public static class Builder{

        private String hint;
        private String title;
        private final Context mContext;
        private String positiveBtnText;
        private String negativeBtnText;
        private OnClickListener positiveBtnOnClickListener;
        private OnClickListener negativeBtnOnClickListener;

        public Builder(@NonNull Context context){
            mContext = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int resId) {
            this.title = mContext.getString(resId);
            return this;
        }

        public Builder setHint(String hint) {
            this.hint = hint;
            return this;
        }

        public Builder setHint(int resId) {
            this.hint = mContext.getString(resId);
            return this;
        }

        public Builder setPositiveButton(String text, OnClickListener onClickListener) {
            this.positiveBtnText = text;
            this.positiveBtnOnClickListener = onClickListener;
            return this;
        }

        public Builder setPositiveButton(int resId, OnClickListener onClickListener) {
            this.positiveBtnText = mContext.getString(resId);
            this.positiveBtnOnClickListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(String text, OnClickListener onClickListener) {
            this.negativeBtnText = text;
            this.negativeBtnOnClickListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(int resId, OnClickListener onClickListener) {
            this.negativeBtnText = mContext.getString(resId);
            this.negativeBtnOnClickListener = onClickListener;
            return this;
        }

        public TextInputDialog create(){
            if(StringUtil.isBlank(title)){
                throw new IllegalArgumentException("Dialogue title cannot be blank");
            }

            TextInputDialog dialog = new TextInputDialog(mContext);
            dialog.setTitle(title);

            if(StringUtil.isBlank(this.hint)){
                dialog.setHint(this.hint);
            }

            if (null != this.positiveBtnOnClickListener) {
                dialog.setPositiveBtnOnClickListener(this.positiveBtnOnClickListener);
            }

            if(StringUtil.isNotBlank(this.positiveBtnText)){
                dialog.setPositiveBtnText(this.positiveBtnText);
            }

            if(null != this.negativeBtnOnClickListener){
                dialog.setNegativeBtnOnClickListener(this.negativeBtnOnClickListener);
            }

            if(StringUtil.isNotBlank(this.negativeBtnText)){
                dialog.setNegativeBtnText(this.negativeBtnText);
            }
            return dialog;
        }
    }
}
