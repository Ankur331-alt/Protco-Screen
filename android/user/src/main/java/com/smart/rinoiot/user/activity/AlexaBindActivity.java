package com.smart.rinoiot.user.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.activity.WebViewActivity;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.voice.AlexaManager;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityAlexaBindBinding;
import com.smart.rinoiot.user.viewmodel.AlexaBindViewModel;

/**
 * @author tw
 * @time 2022/11/30 17:09
 * @description alexa 第三方语音绑定、解绑、手动绑定教程页面
 */
public class AlexaBindActivity extends BaseActivity<ActivityAlexaBindBinding, AlexaBindViewModel>
        implements View.OnClickListener {
    //    private boolean isFirst = true;
    @Override
    public String getToolBarTitle() {
        return null;
    }


    @Override
    public void init() {
        initData();
        initListener();
    }

    private void initData() {
        boolean alexaBindStatus = getIntent().getBooleanExtra(Constant.ALEXA_BIND_STATUS, false);
        alexaBindShowOrHide(alexaBindStatus);
        mViewModel.getIsBindLiveData().observe(this, this::alexaBindShowOrHide);
        mViewModel.getEnableSkillLiveData().observe(this, aBoolean -> {
            if (aBoolean) mViewModel.voiceIsBind();
        });
        mViewModel.getDisableSkillLiveData().observe(this, aBoolean -> {
            if (aBoolean) mViewModel.voiceIsBind();
        });

    }

    /**
     * 绑定状态ui展示
     *
     * @param alexaBindStatus true:已绑定 false：未绑定
     */
    private void alexaBindShowOrHide(boolean alexaBindStatus) {
        binding.tvAlexaBindStatus.setText(getString(alexaBindStatus ? R.string.rino_voice_bind_alexa : R.string.rino_voice_unbind_alexa));
        binding.llBind.setVisibility(!alexaBindStatus ? View.VISIBLE : View.GONE);
//        binding.tvManualUnBind.setVisibility(alexaBindStatus ? View.VISIBLE : View.GONE);
        binding.tvManualUnBind.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            Uri appLinkData = intent.getData();
            //https://iot.rinoiot.com/auth?code=ANbffJwAtDvDUNqYlrrH&state=state
            if (appLinkData != null) {
                getAlexaResponseCode(appLinkData.toString());
            }
        }
    }

    private void initListener() {
        binding.tvOneClickVerify.setOnClickListener(this);
        binding.tvManualBindGuide.setOnClickListener(this);
        binding.tvManualUnBind.setOnClickListener(this);
    }

    @Override
    public ActivityAlexaBindBinding getBinding(LayoutInflater inflater) {
        return ActivityAlexaBindBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvOneClickVerify) {//一键授权
            alexaVerifyType();
        } else if (v.getId() == R.id.tvManualBindGuide) {//手动绑定教程
            startActivity(new Intent(this, WebViewActivity.class)
                    .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_mine_alexa))
                    .putExtra(Constant.WEB_URL, getString(R.string.rino_mine_alexa_voice_guide)));
        } else if (v.getId() == R.id.tvManualUnBind) {//解除绑定
//            mViewModel.alexaUnbindDialog(this);
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!isFirst) {
//            mViewModel.voiceIsBind();
//        }
//        isFirst = false;
//        AlexaManager.getInstance().onResume();
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        AlexaManager.getInstance().onStart(this);
//    }


    /**
     * alexa一键授权方式
     */
    public void alexaVerifyType() {
        if (AlexaManager.getInstance().isInstalled(this, AlexaManager.ALEXA_PACKAGE_NAME)) {//拉起alexa app
            Uri data = Uri.parse(AlexaManager.APP_TO_ALEXA_APP_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, data);
            startActivity(intent);
        } else {//走浏览器
            startActivityForResult(new Intent(this, WebViewActivity.class)
//                    .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_mine_google_assistant))
                    .putExtra(Constant.WEB_URL, AlexaManager.APP_TO_ALEXA_WEB_URL), 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String alexaCallBack = data.getStringExtra(Constant.ALEXA_VERIFY_CALL_BACK);
            getAlexaResponseCode(alexaCallBack);
        }
    }

    private void getAlexaResponseCode(String alexaCallBack) {
        String alexaCode = "";
        if (!TextUtils.isEmpty(alexaCallBack) && alexaCallBack.contains("&")) {
            String[] split = alexaCallBack.split("&");
            if (split != null && split.length > 0) {
                String hostCode = split[0];
                if (!TextUtils.isEmpty(hostCode) && hostCode.contains("=")) {
                    String[] split1 = hostCode.split("=");
                    if (split1 != null && split1.length > 1) {
                        alexaCode = split1[1];
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(alexaCode)) {
            mViewModel.voiceEnableSkill(alexaCode);
        }
    }
}
