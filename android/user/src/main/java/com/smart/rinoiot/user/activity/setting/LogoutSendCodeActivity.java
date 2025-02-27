package com.smart.rinoiot.user.activity.setting;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.ActivityUtils;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityLogoutSendCodeBinding;
import com.smart.rinoiot.user.viewmodel.setting.LogoutViewModel;

/**
 * @Author : tw
 * @Time : On 2022/9/29 15:41
 * @Description : 注销账号发送验证码
 */
@SuppressLint({"StringFormatInvalid", "SetTextI18n", "StringFormatMatches"})
public class LogoutSendCodeActivity extends BaseActivity<ActivityLogoutSendCodeBinding, LogoutViewModel> implements View.OnClickListener {
    String registryTypeAccount;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_mine_logout);
    }

    @Override
    public void init() {
        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        initView();
        initListener();
    }


    private void initView() {
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(this);
        if (userInfo != null) {
            ImageLoader.getInstance().bindCircleImageUrl(userInfo.avatarUrl, binding.ivAvatar, R.drawable.icon_default_avatar);
            //注册类型（1=邮箱注册，2=手机注册，3=第三方注册）
            int registryType = userInfo.registryType;
            String registryTypeName;
            registryTypeAccount = "";
            if (registryType == 1) {
                registryTypeName = getString(R.string.rino_user_email);
                registryTypeAccount = userInfo.email;
            } else if (registryType == 2) {
                registryTypeName = getString(R.string.rino_user_phone);
                registryTypeAccount = userInfo.phoneNumber;
            } else {
                registryTypeName = getString(R.string.rino_mine_account);
                registryTypeAccount = TextUtils.isEmpty(userInfo.phoneNumber) ? "" : userInfo.phoneNumber;
            }
            binding.tvAccountDescribe.setText(String.format(getString(R.string.rino_user_cancel_account_get_verify_code,
                    (registryTypeName + ": " + registryTypeAccount))));
        }
        mViewModel.getCodeActionLiveData().observe(this, actions -> {
            mViewModel.hideLoading();
            if (TextUtils.isEmpty(actions)) {
                ToastUtil.showMsg(getString(R.string.rino_user_send_code_success));
                ActivityUtils.startActivity(this, null, LogoutInputCodeActivity.class);
            } else {
                ToastUtil.showMsg(actions);
            }
        });
    }

    private void initListener() {
        binding.tvLogoutCode.setOnClickListener(this);

    }

    @Override
    public ActivityLogoutSendCodeBinding getBinding(LayoutInflater inflater) {
        return ActivityLogoutSendCodeBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvLogoutCode) {//获取验证码
            mViewModel.getCode(registryTypeAccount, 5);
        }
    }

}
