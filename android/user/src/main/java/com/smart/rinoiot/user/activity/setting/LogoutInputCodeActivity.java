package com.smart.rinoiot.user.activity.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.PartClickUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.activity.NotReceiveVerifyCodeActivity;
import com.smart.rinoiot.user.databinding.ActivityLogoutInputCodeBinding;
import com.smart.rinoiot.user.viewmodel.setting.LogoutViewModel;
import com.wynsbin.vciv.VerificationCodeInputView;

/**
 * @Author : tw
 * @Time : On 2022/9/29 15:41
 * @Description : 注销账号输入验证码
 */
@SuppressLint({"StringFormatInvalid", "SetTextI18n", "StringFormatMatches"})
public class LogoutInputCodeActivity extends BaseActivity<ActivityLogoutInputCodeBinding, LogoutViewModel> implements View.OnClickListener {
    private String registryTypeAccount, verifyCode;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_mine_logout);
    }

    @Override
    public void init() {
        initView();
        initListener();
    }


    private void initView() {
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(this);
        if (userInfo != null) {
            //注册类型（1=邮箱注册，2=手机注册，3=第三方注册）
            int registryType = userInfo.registryType;
            String registryTypeName = "";
            registryTypeAccount = userInfo.email;
            if (registryType == 1) {
                registryTypeAccount = userInfo.email;
            } else if (registryType == 2) {
                registryTypeName = "+86";
                registryTypeAccount = userInfo.phoneNumber;
            }
            binding.tvSubTitle.setText(PartClickUtil.stringColor(this,
                    String.format(getString(R.string.rino_user_been_sent_code), (registryTypeName + ":" + registryTypeAccount))
                    , R.color.c_333333, getString(R.string.rino_user_been_sent_code).indexOf("%1s"),
                    getString(R.string.rino_user_been_sent_code).indexOf("%1s") +
                            (registryTypeName + ":" + registryTypeAccount).length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE));
        }
        mViewModel.getCodeActionLiveData().observe(this, actions -> {
            mViewModel.hideLoading();
            if (TextUtils.isEmpty(actions)) {
                ToastUtil.showMsg(getString(R.string.rino_user_send_code_success));
            } else {
                ToastUtil.showMsg(actions);
            }
        });


        //开启倒计时
        mViewModel.countDown();
        mViewModel.getCountDownLiveData().observe(this, integer -> {
            if (integer == 0) {
                binding.tvResend.setTextColor(getResources().getColor(R.color.main_theme_color));
                binding.tvResend.setText(getString(R.string.rino_user_resend));
                binding.tvResend.setEnabled(true);
            } else {
                binding.tvResend.setTextColor(getResources().getColor(R.color.c_A5A5A5));
                binding.tvResend.setText(String.format(getString(R.string.rino_user_cancel_account_resend_verify_code), integer.toString()));
                binding.tvResend.setEnabled(false);
            }
        });

        binding.codeView.setOnInputListener(new VerificationCodeInputView.OnInputListener() {
            @Override
            public void onComplete(String code) {
                mViewModel.showLoading();
                verifyCode = code;
                mViewModel.checkCode(registryTypeAccount, code);
            }

            @Override
            public void onInput() {
            }
        });
        mViewModel.getCheckCodeLiveData().observe(this, isSuccess -> {
            if (isSuccess) {
                mViewModel.cancelAccount(verifyCode);
            } else {
                ToastUtil.showMsg(getString(R.string.rino_user_code_check_tip));
            }
        });
    }

    private void initListener() {
        binding.tvResend.setOnClickListener(this);
        binding.tvChangeRegisterOfWay.setOnClickListener(this);

    }

    @Override
    public ActivityLogoutInputCodeBinding getBinding(LayoutInflater inflater) {
        return ActivityLogoutInputCodeBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvChangeRegisterOfWay) {//未收到验证码
            startActivity(new Intent(this, NotReceiveVerifyCodeActivity.class));
        } else if (v.getId() == R.id.tvResend) {//重新发送验证码
            mViewModel.getCode(registryTypeAccount, 5);
        }
    }

}
