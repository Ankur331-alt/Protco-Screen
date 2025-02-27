package com.smart.rinoiot.user.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.CountryBean;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.activity.CountryActivity;
import com.smart.rinoiot.user.activity.ForgetPsdActivity;
import com.smart.rinoiot.user.databinding.FragmentForgetPasswordBinding;
import com.smart.rinoiot.user.viewmodel.LoginViewModel;

public class ForgetPsdFragment extends BaseFragment<FragmentForgetPasswordBinding, LoginViewModel> implements View.OnClickListener {

    private CountryBean countryBean;

    public void setCountryBean(CountryBean countryBean) {
        this.countryBean = countryBean;
        binding.tvCountryName.setText(countryBean.getCountryName());
    }

    @Override
    public void init() {
        countryBean = CacheDataManager.getInstance().getCurrentCountry();

        binding.viewCountry.setOnClickListener(this);
        binding.tvFindPassword.setOnClickListener(this);

        binding.tvCountryName.setText(countryBean.getCountryName());

        mViewModel.getCodeActionLiveData().observe(this, actions -> {
            String account = binding.etAccount.getText();
            mViewModel.hideLoading();
            if (TextUtils.isEmpty(actions)) {
                if (requireActivity() instanceof ForgetPsdActivity) {
                    if (requireActivity().getSupportFragmentManager().getFragments().size() == 2) {
                        ((ForgetPsdActivity) requireActivity()).setAccount(account);
                        ((ForgetPsdActivity) requireActivity()).gotoNextFragment();
                    }
                    ToastUtil.showMsg(getString(R.string.rino_user_send_code_success));
                }
            } else {
                ToastUtil.showMsg(actions);
            }
        });
    }

    @Override
    public FragmentForgetPasswordBinding getBinding(LayoutInflater inflater) {
        return FragmentForgetPasswordBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvFindPassword) {
            if (TextUtils.isEmpty(binding.etAccount.getText())) {
                ToastUtil.showMsg(R.string.rino_user_login_account_tip_title);
                return;
            }

            if (!AppUtil.isPhoneNumber(binding.etAccount.getText()) && !AppUtil.isEmail(binding.etAccount.getText())) {
                ToastUtil.showMsg(R.string.rino_user_please_input_right_format);
                return;
            }

            mViewModel.showLoading();
            mViewModel.getCode(binding.etAccount.getText(), Constant.SEND_CODE_TYPE_FOR_RESET_PASSWORD);
        } else if (v.getId() == R.id.viewCountry) {
            requireActivity().startActivityForResult(new Intent(requireContext(), CountryActivity.class), ForgetPsdActivity.REQUEST_CODE_FROM_COUNTRY_SELECT);
        }
    }
}
