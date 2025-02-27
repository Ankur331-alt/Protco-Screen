package com.smart.rinoiot.user.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.activity.setting.ChangePwdSendCodeActivity;
import com.smart.rinoiot.user.databinding.FragmentModifyPasswordBinding;
import com.smart.rinoiot.user.viewmodel.LoginViewModel;

public class ModifyPsdFragment extends BaseFragment<FragmentModifyPasswordBinding, LoginViewModel> implements View.OnClickListener {

    private String account;

    @Override
    public void init() {
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(requireContext());
        String prefix = "";
        if (userInfo.registryType == 1) {//(1=邮箱注册，2=手机注册，3=第三方注册）
            prefix = getString(R.string.rino_user_email) + ": ";
            account = userInfo.email;
        } else if (userInfo.registryType == 2) {
            prefix = getString(R.string.rino_mine_account) + ": " + "+86";
            account = userInfo.phoneNumber;
        } else {
            prefix = getString(R.string.rino_mine_account) + ": ";
            account = TextUtils.isEmpty(userInfo.phoneNumber) ? "" : userInfo.phoneNumber;
        }

        binding.tvSendVerification.setText(String.format(getString(R.string.rino_user_cancel_account_get_verify_code), prefix + account));

        binding.tvSend.setOnClickListener(this);

        mViewModel.getCodeActionLiveData().observe(this, actions -> {
            mViewModel.hideLoading();
            if (TextUtils.isEmpty(actions)) {
                if (requireActivity() instanceof ChangePwdSendCodeActivity) {
                    if (requireActivity().getSupportFragmentManager().getFragments().size() == 2) {
                        ((ChangePwdSendCodeActivity) requireActivity()).setAccount(account);
                        ((ChangePwdSendCodeActivity) requireActivity()).gotoNextFragment();
                    }
                    ToastUtil.showMsg(getString(R.string.rino_user_send_code_success));
                }
            } else {
                ToastUtil.showMsg(actions);
            }
        });
    }

    @Override
    public FragmentModifyPasswordBinding getBinding(LayoutInflater inflater) {
        return FragmentModifyPasswordBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_send) {
            mViewModel.showLoading();
            mViewModel.getCode(account, Constant.SEND_CODE_TYPE_FOR_RESET_PASSWORD);
        }
    }
}
