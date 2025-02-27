package com.smart.rinoiot.center.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.center.activity.CenFlashActivity;
import com.smart.rinoiot.center.manager.CenConstant;
import com.smart.rinoiot.center.viewmodel.SetUpViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.FragmentAccountBinding;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 账号页面
 */
public class AccountFragment extends BaseFragment<FragmentAccountBinding, SetUpViewModel> implements View.OnClickListener {
    @Override
    public void init() {
        initData();
    }

    @Override
    public FragmentAccountBinding getBinding(LayoutInflater inflater) {
        return FragmentAccountBinding.inflate(inflater);
    }

    private void initData() {
        //头像
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(getContext());
        if (userInfo != null) {
            ImageLoader.getInstance().bindCircleImageUrl(userInfo.avatarUrl, binding.ivProfilePhoto, R.drawable.icon_default_avatar);
            binding.tvUserName.setText(userInfo.nickname);
        }


        binding.tvLogout.setOnClickListener(this);
        mViewModel.getLogoutLiveData().observe(this, isSuccess -> {
            mViewModel.hideLoading();
            if (isSuccess) {
                UserInfoManager.getInstance().clear();
                SharedPreferenceUtil.getInstance().remove(Constant.COUNTRY_CODE);
                AppManager.getInstance().finishAllActivity();
                SharedPreferenceUtil.getInstance().put(CenConstant.IS_LOGIN, false);
                startActivity(new Intent(getContext(), CenFlashActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvLogout) {
            mViewModel.showLoading();
            mViewModel.logout();
        }
    }

}
