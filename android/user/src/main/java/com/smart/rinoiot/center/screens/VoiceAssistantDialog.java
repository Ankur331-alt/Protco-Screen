package com.smart.rinoiot.center.screens;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.PopVoiceWakeupBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

public class VoiceAssistantDialog extends Dialog {

    public static final String TAG = "VoiceAssistantDialog";

    private final PopVoiceWakeupBinding mBinding;

    /**
     * The wake phrase
     */
    private static final String WAKE_UP_PHRASE = "Hello MORFEI";

    public VoiceAssistantDialog(@NonNull Context context) {
        super(context);
        mBinding = PopVoiceWakeupBinding.inflate(getLayoutInflater());

        // Set the content view
        setContentView(mBinding.getRoot());

        // Make the dialog fullscreen
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(this.getWindow()).getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(layoutParams);

        // Make the background transparent
        Objects.requireNonNull(this.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Start the wake up animation
        startWakeUpAnim();

        // Set on click callback
        mBinding.getRoot().setOnClickListener((view)-> dismiss());

        // Make the dialog cancelable by default.
        this.setCancelable(true);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
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
            String text = mBinding.tvAnswer.getText() + answer;
            mBinding.tvAnswer.setText(text);
            mBinding.pbLoading.setVisibility(View.GONE);
            mBinding.tvAnswer.setVisibility(isShow ? View.VISIBLE : View.GONE);
            mBinding.llResponse.setVisibility(isShow ? View.VISIBLE : View.GONE);
        });
    }

    private void hideResponse(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mBinding.tvAnswer.setText("");
            mBinding.llResponse.setVisibility(View.GONE);
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.d(TAG, "dismiss: dismissing");
        hideResponse();
        setQuestion("");
        hideLoadingAnim();
        EventBus.getDefault().post(new VoicePopUpEvent(VoicePopUpEventType.Dismiss));
    }

    public void handleEvent(VoicePopUpEvent event) {
        if(null == mBinding){
            return;
        }

        switch (event.getType()) {
            case WakeUp:
                Log.d(TAG, "handleEvent: waking up");
                hideLoadingAnim();
                hideResponse();
                setQuestion(WAKE_UP_PHRASE);
                startWakeUpAnim();
                break;
            case Sleep:
                Log.d(TAG, "handleEvent: sleeping");
                dismiss();
                setQuestion(WAKE_UP_PHRASE);
                hideLoadingAnim();
                break;
            case Speech:
                startSpeechAnim();
                setResponse(event.getData());
                break;
            case Intent:
                Log.d(TAG, "handleEvent: intent");
                startSpeechAnim();
                showLoadingAnim();
                setQuestion(event.getData());
                break;
            case Reset:
                Log.d(TAG, "handleEvent: reset");
                hideLoadingAnim();
                hideResponse();
                setQuestion("");
                startWakeUpAnim();
                break;
            default:break;
        }
    }
}
