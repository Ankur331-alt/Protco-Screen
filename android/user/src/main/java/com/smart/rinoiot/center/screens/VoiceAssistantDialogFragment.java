package com.smart.rinoiot.center.screens;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.PopVoiceWakeupBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

public class VoiceAssistantDialogFragment extends DialogFragment {

    public static final String TAG = "VoiceAssistantDialog";

    private PopVoiceWakeupBinding mBinding;

    /**
     * The wake phrase
     */
    private final String wakePhrase = "Hello MORFEI";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mBinding = PopVoiceWakeupBinding.inflate(getLayoutInflater());
        mBinding.getRoot().setOnClickListener((view)-> {
            EventBus.getDefault().post(new VoicePopUpEvent(VoicePopUpEventType.Dismiss));
            dismiss();
        });

        setQuestion(wakePhrase);
        Dialog mDialog = new Dialog(requireContext(), R.style.Theme_AppCompat);
        mDialog.setContentView(mBinding.getRoot());
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        startWakeUpAnim();
        return mDialog;
    }

    private void startWakeUpAnim() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mBinding.ivIcon.setBackgroundResource(R.drawable.anim_assistant_wake_up);
            AnimationDrawable wakeUpAnimation = (AnimationDrawable) mBinding.ivIcon.getBackground();
            wakeUpAnimation.start();
        });
    }

    private void startSpeechAnim() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mBinding.ivIcon.setBackgroundResource(R.drawable.amin_assistant_speech);
            AnimationDrawable speechAnimation = (AnimationDrawable) mBinding.ivIcon.getBackground();
            speechAnimation.start();
        });
    }

    private void showLoadingAnim(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mBinding.tvAnswer.setVisibility(View.GONE);
            mBinding.pbLoading.setVisibility(View.VISIBLE);
            mBinding.llResponse.setVisibility(View.VISIBLE);
        });
    }

    private void hideLoadingAnim(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mBinding.pbLoading.setVisibility(View.VISIBLE);
        });
    }

    private void setQuestion(String text) {
        boolean isShow = !TextUtils.isEmpty(text);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mBinding.tvQuestion.setVisibility(isShow ? View.VISIBLE : View.GONE);
            mBinding.tvQuestion.setText(text);
        });
    }

    private void setResponse(String answer) {
        boolean isShow = !TextUtils.isEmpty(answer);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mBinding.tvAnswer.setText(answer);
            mBinding.pbLoading.setVisibility(View.GONE);
            mBinding.tvAnswer.setVisibility(isShow ? View.VISIBLE : View.GONE);
            mBinding.llResponse.setVisibility(isShow ? View.VISIBLE : View.GONE);
        });
    }

    private void hideResponse(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> mBinding.llResponse.setVisibility(View.GONE));
    }

    public void handleEvent(VoicePopUpEvent event) {
        if(null == mBinding){
            return;
        }

        switch (event.getType()) {
            case WakeUp:
                setResponse("");
                hideLoadingAnim();
                setQuestion(wakePhrase);
                startWakeUpAnim();
                break;
            case Sleep:
                setQuestion(wakePhrase);
                hideLoadingAnim();
                dismiss();
                break;
            case Speech:
                startSpeechAnim();
                setResponse(event.getData());
                break;
            case Intent:
                startSpeechAnim();
                showLoadingAnim();
                setQuestion(event.getData());
                break;
            case Reset:
                hideResponse();
                setQuestion("");
                startWakeUpAnim();
                break;
            default:break;
        }
    }
}
