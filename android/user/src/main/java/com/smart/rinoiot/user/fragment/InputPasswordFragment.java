package com.smart.rinoiot.user.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.activity.ForgetPsdActivity;
import com.smart.rinoiot.user.activity.setting.ChangePwdSendCodeActivity;
import com.smart.rinoiot.user.databinding.FragmentInputPasswordBinding;
import com.smart.rinoiot.user.viewmodel.LoginViewModel;

public class InputPasswordFragment extends BaseFragment<FragmentInputPasswordBinding, LoginViewModel> implements View.OnClickListener {

    @Override
    public void init() {
        binding.tvConfirm.setOnClickListener(this);
    }

    @Override
    public FragmentInputPasswordBinding getBinding(LayoutInflater inflater) {
        return FragmentInputPasswordBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvConfirm) {
            String password = binding.etPassword.getText();
//            if (password.length() >= 6 && password.length() <= 20 && AppUtil.isContainsNumberAndLetter(password)) {
            if (requireActivity() instanceof ForgetPsdActivity) {
                ((ForgetPsdActivity) requireActivity()).resetPassword(password);
            } else if (requireActivity() instanceof ChangePwdSendCodeActivity) {
                ((ChangePwdSendCodeActivity) requireActivity()).resetPassword(password);
            }
//            } else {
//                ToastUtil.showMsg(getString(R.string.rino_user_password_rule));
//            }
        }
    }
}
