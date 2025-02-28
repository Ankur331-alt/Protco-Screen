package com.smart.rinoiot.user.activity;

import android.text.Spannable;
import android.view.LayoutInflater;

import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.utils.PartClickUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityNotReceiveVerifyCodeBinding;

/**
 * @author tw
 * @time 2022/11/18 15:41
 * @description 未收到验证码 页面
 */
public class NotReceiveVerifyCodeActivity extends BaseActivity<ActivityNotReceiveVerifyCodeBinding, BaseViewModel> {
    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_user_unable_accept_email_code).substring(0, getString(R.string.rino_user_unable_accept_email_code).length() - 1);
    }

    @Override
    public void init() {
        String desc = getString(R.string.rino_user_not_receive_verify_code_desc6);
        binding.tvVerifyAccount.setText(PartClickUtil.stringColor(this,
                desc, R.color.main_theme_color, desc.length() - 22, desc.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE));
    }

    @Override
    public ActivityNotReceiveVerifyCodeBinding getBinding(LayoutInflater inflater) {
        return ActivityNotReceiveVerifyCodeBinding.inflate(inflater);
    }
}
