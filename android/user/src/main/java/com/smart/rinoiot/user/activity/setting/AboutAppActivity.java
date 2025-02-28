package com.smart.rinoiot.user.activity.setting;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.activity.WebViewActivity;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityAboutAppBinding;
import com.smart.rinoiot.center.update.CheckUpdateUtil;
import com.smart.rinoiot.common.utils.AgreementPolicyUtils;
import com.smart.rinoiot.user.viewmodel.setting.AboutAppViewModel;

/**
 * @author  tw
 * @time 2022/9/29 15:41
 * @description : AboutAppActivity 版本信息
 */
public class AboutAppActivity extends BaseActivity<ActivityAboutAppBinding, AboutAppViewModel> implements View.OnClickListener {
    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
        initView();
        initListener();
    }

    private void initView() {
        //检测更新
        binding.includeUpdate.tvTitle.setText(getString(R.string.rino_common_version_check));
        binding.includeUpdate.ivRight.setVisibility(View.VISIBLE);
        binding.includeUpdate.getRoot().setVisibility(View.GONE);
        //隐私政策
        binding.includePrivacy.tvTitle.setText(getString(R.string.rino_user_privacy_policy));
        binding.includePrivacy.ivRight.setVisibility(View.VISIBLE);
        //用户协议
        binding.includeAgreement.tvTitle.setText(getString(R.string.rino_user_user_agreement));
        binding.includeAgreement.ivRight.setVisibility(View.VISIBLE);
        binding.tvOldVersion.setText(String.format(getString(R.string.rino_common_old_version), CheckUpdateUtil.getLocalVersion()));
    }

    private void initListener() {
        binding.includeAgreement.getRoot().setOnClickListener(this);
        binding.includePrivacy.getRoot().setOnClickListener(this);
        binding.includeUpdate.getRoot().setOnClickListener(this);
    }

    @Override
    public ActivityAboutAppBinding getBinding(LayoutInflater inflater) {
        return ActivityAboutAppBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.includeUpdate) {
            //版本更新
            AppUtil.setBrowser(this, Constant.APP_GOOGLE_URL);
        } else if (v.getId() == R.id.includePrivacy) {
            //隐私政策
            startActivity(new Intent(this, WebViewActivity.class)
                    .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_user_privacy_policy))
                    .putExtra(Constant.WEB_URL, AgreementPolicyUtils.getPrivacyPolicy(this)));
        } else if (v.getId() == R.id.includeAgreement) {
            //用户协议
            startActivity(new Intent(this, WebViewActivity.class)
                    .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_user_user_agreement))
                    .putExtra(Constant.WEB_URL, AgreementPolicyUtils.getAgreement(this)));
        }
    }
}
