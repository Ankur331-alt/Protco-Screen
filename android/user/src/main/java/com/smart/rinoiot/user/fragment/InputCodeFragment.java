package com.smart.rinoiot.user.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.activity.ForgetPsdActivity;
import com.smart.rinoiot.user.activity.NotReceiveVerifyCodeActivity;
import com.smart.rinoiot.user.activity.setting.ChangePwdSendCodeActivity;
import com.smart.rinoiot.user.databinding.FragmentInputCodeBinding;
import com.smart.rinoiot.user.viewmodel.LoginViewModel;
import com.wynsbin.vciv.VerificationCodeInputView;

public class InputCodeFragment extends BaseFragment<FragmentInputCodeBinding, LoginViewModel> implements View.OnClickListener {

    private int type;

    private String account = "";
    private String code = "";

    @SuppressLint("SetTextI18n")
    @Override
    public void init() {
         if (requireActivity() instanceof ForgetPsdActivity) {
            type = Constant.SEND_CODE_TYPE_FOR_RESET_PASSWORD;
            account = ((ForgetPsdActivity) requireActivity()).getAccount();
            binding.tvChangeRegisterOfWay.setText(getString(R.string.rino_user_unable_accept_email_code));
        } else if (requireActivity() instanceof ChangePwdSendCodeActivity) {
            type = Constant.SEND_CODE_TYPE_FOR_RESET_PASSWORD;
            account = ((ChangePwdSendCodeActivity) requireActivity()).getAccount();
            binding.tvChangeRegisterOfWay.setText(getString(R.string.rino_user_unable_accept_email_code));
        }

        String resendStr = getString(R.string.rino_user_resend);
        binding.tvSubTitle.setText(String.format(getString(R.string.rino_user_been_sent_code), AppUtil.isEmail(account) ? account : ("+86" + account)));

        //开启倒计时
        mViewModel.countDown();
        mViewModel.getCountDownLiveData().observe(this, integer -> {
            if (integer == 0) {
                binding.tvResend.setTextColor(requireContext().getResources().getColor(R.color.main_theme_color));
                binding.tvResend.setText(resendStr);
                binding.tvResend.setEnabled(true);
            } else {
                binding.tvResend.setTextColor(requireContext().getResources().getColor(R.color.c_A5A5A5));
                binding.tvResend.setText(String.format(getString(R.string.rino_user_cancel_account_resend_verify_code), integer.toString()));
                binding.tvResend.setEnabled(false);
            }
        });

        binding.tvChangeRegisterOfWay.setOnClickListener(this);
        binding.tvResend.setOnClickListener(this);

        mViewModel.getCheckCodeLiveData().observe(this, isSuccess -> {
            mViewModel.hideLoading();
            if (isSuccess) {
                 if (requireActivity() instanceof ForgetPsdActivity) {
                    ((ForgetPsdActivity) requireActivity()).setCode(code);
                    ((ForgetPsdActivity) requireActivity()).gotoNextFragment();
                } else if (requireActivity() instanceof ChangePwdSendCodeActivity) {
                    ((ChangePwdSendCodeActivity) requireActivity()).setCode(code);
                    ((ChangePwdSendCodeActivity) requireActivity()).gotoNextFragment();
                }
            } else {
                ToastUtil.showMsg(getString(R.string.rino_user_code_check_tip));
            }
        });
        mViewModel.getCodeActionLiveData().observe(this, actions -> {
            mViewModel.hideLoading();
            if (TextUtils.isEmpty(actions)) {
                mViewModel.countDown();
                ToastUtil.showMsg(getString(R.string.rino_user_send_code_success));
            } else {
                ToastUtil.showMsg(actions);
            }
        });
        binding.codeView.setOnInputListener(new VerificationCodeInputView.OnInputListener() {
            @Override
            public void onComplete(String code) {
                InputCodeFragment.this.code = code;
                mViewModel.showLoading();
                mViewModel.checkCode(account, code);
            }

            @Override
            public void onInput() {
            }
        });
    }

    @Override
    public FragmentInputCodeBinding getBinding(LayoutInflater inflater) {
        return FragmentInputCodeBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvChangeRegisterOfWay) {
            startActivity(new Intent(getContext(), NotReceiveVerifyCodeActivity.class));
        } else if (v.getId() == R.id.tvResend) {
            mViewModel.showLoading();
            mViewModel.getCode(account, type);
        }
    }
}
