package com.smart.rinoiot.user.activity.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.smart.rinoiot.center.activity.CenFlashActivity;
import com.smart.rinoiot.center.manager.CenConstant;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.DataCleanManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.ActivityUtils;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.activity.TimeZoneActivity;
import com.smart.rinoiot.user.databinding.ActivitySettingBinding;
import com.smart.rinoiot.user.viewmodel.setting.SettingViewModel;

/**
 * @Author : tw
 * @Time : On 2022/9/29 14:39
 * @Description : SettingActivity 个人设置
 */
@SuppressLint("SetTextI18n")
public class SettingActivity extends BaseActivity<ActivitySettingBinding, SettingViewModel> implements View.OnClickListener {

    public final static int REQUEST_CODE_FROM_TIME_ZONE = 0x1001;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_mine_setting);
    }

    @Override
    public void init() {
        initView();
        initData();
        initListener();
    }

    /**
     * 控件显示隐藏
     */
    private void initView() {
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(this);
        //账号与安全
        binding.includeAccountSecurity.tvTitle.setText(getString(R.string.rino_mine_account_security));
        binding.includeAccountSecurity.ivRight.setVisibility(View.VISIBLE);
        //时区
        binding.includeTimeZone.tvTitle.setText(getString(R.string.rino_mine_time_zone));
        if (userInfo != null) {
            binding.includeTimeZone.tvSummary.setText(userInfo.tz);
        }
        binding.includeTimeZone.tvSummary.setVisibility(View.VISIBLE);
        binding.includeTimeZone.ivRight.setVisibility(View.VISIBLE);
        //温度单位
        binding.includeTempUnit.tvTitle.setText(getString(R.string.rino_mine_temp_unit));
        if (userInfo != null) {
            binding.includeTempUnit.tvSummary.setText(TextUtils.isEmpty(userInfo.tempUnit) ? "°C" : userInfo.tempUnit);
        }
        binding.includeTempUnit.tvSummary.setVisibility(View.VISIBLE);
        binding.includeTempUnit.ivRight.setVisibility(View.VISIBLE);
        //清除缓存
        binding.includeClearCache.tvTitle.setText(getString(R.string.rino_mine_clear_cache));
        long cacheSize = DataCleanManager.getAllSize(getApplicationContext());
        binding.includeClearCache.tvSummary.setText(cacheSize == 0 ? "" : DataCleanManager.getFormatSize(cacheSize));
        binding.includeClearCache.tvSummary.setVisibility(View.VISIBLE);
        binding.includeClearCache.ivRight.setVisibility(View.VISIBLE);
        //关于RINO
        binding.includeAbout.tvTitle.setText(getString(R.string.rino_mine_about_info));
        binding.includeAbout.ivRight.setVisibility(View.VISIBLE);
    }

    private void initListener() {
        binding.tvLogout.setOnClickListener(this);
        binding.includeAccountSecurity.getRoot().setOnClickListener(this);
        binding.includeTimeZone.getRoot().setOnClickListener(this);
        binding.includeTempUnit.getRoot().setOnClickListener(this);
        binding.includeClearCache.getRoot().setOnClickListener(this);
        binding.includeAbout.getRoot().setOnClickListener(this);
    }

    private void initData() {
        mViewModel.getLogoutLiveData().observe(this, isSuccess -> {
            mViewModel.hideLoading();
            if (isSuccess) {
                UserInfoManager.getInstance().clear();
                SharedPreferenceUtil.getInstance().remove(Constant.COUNTRY_CODE);
                AppManager.getInstance().finishAllActivity();
                startActivity(new Intent(this, CenFlashActivity.class));
            }
        });
    }

    @Override
    public ActivitySettingBinding getBinding(LayoutInflater inflater) {
        return ActivitySettingBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvLogout) {//退出登录
//            mViewModel.showLoading();
//            mViewModel.logout();
            SharedPreferenceUtil.getInstance().put(CenConstant.IS_LOGIN, false);
            startActivity(new Intent(this, CenFlashActivity.class));
            finishThis();
        } else if (v.getId() == R.id.includeAccountSecurity) {//账号与安全
            ActivityUtils.startActivity(this, null, AccountSecurityActivity.class);
        } else if (v.getId() == R.id.includeTimeZone) {//时区
            ActivityUtils.startActivityForResult(this, null, TimeZoneActivity.class, REQUEST_CODE_FROM_TIME_ZONE);
        } else if (v.getId() == R.id.includeTempUnit) {//温度单位
            mViewModel.showChangeUnit(this, binding.includeTempUnit.tvSummary);
        } else if (v.getId() == R.id.includeClearCache) {//清除缓存
            mViewModel.showCacheDialog(this, binding.includeClearCache.tvSummary);
        } else if (v.getId() == R.id.includeAbout) {//关于
            ActivityUtils.startActivity(this, null, AboutAppActivity.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE_FROM_TIME_ZONE) {
            String tz = data.getStringExtra("tz");
            if (!TextUtils.isEmpty(tz)) {
                binding.includeTimeZone.tvSummary.setText(tz);
                mViewModel.updateInfoData(tz);
            }
        }
    }
}
