package com.smart.rinoiot.common.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.databinding.DialogVoiceAssistantSettingsBinding;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.voice.VoiceAssistantSettings;

import java.time.Duration;
import java.util.Objects;

public class VoiceAssistantSettingsDialog extends Dialog {
    private String mTitle;

    private String mPositiveBtnText;

    private OnClickListener mPositiveBtnOnClickListener;

    private String mNegativeBtnText;

    private OnClickListener mNegativeBtnOnClickListener;

    private final InputMethodManager imm;

    private final DialogVoiceAssistantSettingsBinding mBinding;

    public VoiceAssistantSettingsDialog(@NonNull Context context) {
        super(context);
        mBinding = DialogVoiceAssistantSettingsBinding.inflate(getLayoutInflater());
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
    }

    private void initView(){
        // setup listeners
        mBinding.btnCancel.setOnClickListener(v -> {
            hideSoftInputFromWindow();
            if(null != this.mNegativeBtnOnClickListener){
                mNegativeBtnOnClickListener.onClick(Duration.ofSeconds(0), false);
            }
            dismiss();
        });

        mBinding.ivContinuousMode.setOnClickListener(view->{
            int key = mBinding.ivContinuousMode.getId();
            boolean checked = Boolean.parseBoolean(
                    String.valueOf(mBinding.ivContinuousMode.getTag(key))
            );
            int resId = !checked ?
                    R.drawable.ic_checkbox_check : R.drawable.ic_checkbox_uncheck;
            Drawable drawable = AppCompatResources.getDrawable(getContext(), resId);
            mBinding.ivContinuousMode.setImageDrawable(drawable);
            mBinding.ivContinuousMode.setTag(key, !checked);
        });

        mBinding.btnConfirm.setOnClickListener(v -> {
            hideSoftInputFromWindow();
            int progress = mBinding.sbContinuousListeningDuration.getProgress();
            int key = mBinding.ivContinuousMode.getId();
            boolean checked = Boolean.parseBoolean(
                    String.valueOf(mBinding.ivContinuousMode.getTag(key))
            );
            if(null != this.mPositiveBtnOnClickListener){
                mPositiveBtnOnClickListener.onClick(Duration.ofSeconds(progress), checked);
            }
            dismiss();
        });

        setOnShowListener(dialog -> {
            if(StringUtil.isNotBlank(this.mTitle)){
                mBinding.tvTitle.setText(this.mTitle);
            }


            if(StringUtil.isNotBlank(this.mPositiveBtnText)){
                mBinding.btnConfirm.setText(this.mPositiveBtnText);
            }

            if(StringUtil.isNotBlank(this.mNegativeBtnText)){
                mBinding.btnCancel.setText(this.mNegativeBtnText);
            }
        });

        Objects.requireNonNull(this.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);
        setContentView(mBinding.getRoot());
    }

    public void setTitle(String title) {
        this.mTitle = title;
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
        if(!imm.isActive()) {
            return;
        }
        imm.hideSoftInputFromWindow(
                mBinding.getRoot().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    public void setSettings(@NonNull VoiceAssistantSettings settings) {
        int duration = settings.getContinuousListeningDuration();
        mBinding.sbContinuousListeningDuration.setProgress(duration);
        int resId = settings.isContinuousMode() ?
                R.drawable.ic_checkbox_check : R.drawable.ic_checkbox_uncheck;
        Drawable drawable = AppCompatResources.getDrawable(getContext(), resId);
        mBinding.ivContinuousMode.setImageDrawable(drawable);
        int key = mBinding.ivContinuousMode.getId();
        mBinding.ivContinuousMode.setTag(key, settings.isContinuousMode());
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(false);
    }

    public interface OnClickListener{
        /**
         * Invoked when a button is clicked
         * @param duration the duration.
         * @param continuousMode whether the continuous interaction mode id selected.
         */
        void onClick(Duration duration, boolean continuousMode);
    }

    public static class Builder{
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

        public VoiceAssistantSettingsDialog create(){
            if(StringUtil.isBlank(title)){
                throw new IllegalArgumentException("Dialogue title cannot be blank");
            }

            VoiceAssistantSettingsDialog dialog = new VoiceAssistantSettingsDialog(mContext);
            dialog.setTitle(title);

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
