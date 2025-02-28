package com.smart.rinoiot.center.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.smart.rinoiot.center.fragment.ConnectFragment;
import com.smart.rinoiot.center.manager.CenConstant;
import com.smart.rinoiot.center.manager.FragManager;
import com.smart.rinoiot.center.viewmodel.CenLoginViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityCenFlashBinding;

/**
 * @author tw
 * @time 2022/12/5 15:01
 * @description 中控屏启动页面 中控屏wifi连接--》扫码登录 --》绑定成功
 */
public class CenFlashActivity extends BaseActivity<ActivityCenFlashBinding, CenLoginViewModel> {
    private FragmentManager fragmentManager;

    /**
     * 当前fragment是第几个
     */
    private int type = 1;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @SuppressLint("NewApi")
    @Override
    public void init() {
        StatusBarUtil.setNormalStatusBar(this, R.color.transparent);
        setFullStatusBar(R.color.transparent);
        SharedPreferenceUtil.getInstance().put(Constant.USER_TOKEN_EXPIRED, false);
        if (UserInfoManager.getInstance().isLogin(this)) {
            Constant.CLIENT_ID = SharedPreferenceUtil.getInstance().get(Constant.MQTT_CLIENT_ID, Constant.CLIENT_ID);
            startActivity(new Intent(this, HomeActivity.class));
            finishThis();
        } else {
            initData(type);
            fragmentManager = FragManager.getInstance().loadFragment(
                    this, fragmentManager, new ConnectFragment(), CenConstant.CONNECT_WIFI
            );
        }
    }

    @Override
    public ActivityCenFlashBinding getBinding(LayoutInflater inflater) {
        return ActivityCenFlashBinding.inflate(inflater);
    }


    @Override
    public void onBackPressed() {
        if (fragmentManager != null && fragmentManager.getFragments() != null && fragmentManager.getFragments().size() > 2) {
            FragManager.getInstance().removeFragment(this, fragmentManager, fragmentManager.getFragments());
            type--;
            initData(type);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 初始化 步骤样式
     *
     * @param type 1:连接网络；2、扫码；3、绑定成功
     */
    private void initData(int type) {
        binding.includeStepOne.tvConnectNetwork.setText(getString(R.string.rino_user_connect_network));
        binding.includeStepOne.tvConnectStep.setBackgroundResource(type == 1 ? R.drawable.icon_cen_current_selected_step : R.drawable.icon_cen_selected_normal_step);
        binding.includeStepOne.tvConnectStep.setTextColor(getResources().getColor(type == 1 ? R.color.cen_connect_step_selected_color : R.color.cen_connect_step_normal_color));
        binding.includeStepOne.viewStepBeforeLine.setVisibility(View.INVISIBLE);
        binding.includeStepOne.tvConnectStep.setText("1");


        binding.includeStepTwo.tvConnectNetwork.setText(getString(R.string.rino_user_scan_code));
        binding.includeStepTwo.tvConnectStep.setBackgroundResource(type == 2 ? R.drawable.icon_cen_current_selected_step : type > 2 ? R.drawable.icon_cen_selected_normal_step : R.drawable.icon_cen_current_normal_step);
        binding.includeStepTwo.tvConnectStep.setTextColor(getResources().getColor(type == 2 ? R.color.cen_connect_step_selected_color : R.color.cen_connect_step_normal_color));
        binding.includeStepTwo.tvConnectStep.setText("2");

        binding.includeStepThree.tvConnectNetwork.setText(getString(R.string.rino_user_binding_succeeded));
        binding.includeStepThree.viewStepAfterLine.setVisibility(View.INVISIBLE);
        binding.includeStepThree.tvConnectStep.setText("3");
        binding.includeStepThree.tvConnectStep.setBackgroundResource(type == 3 ? R.drawable.icon_cen_current_selected_step : R.drawable.icon_cen_current_normal_step);
        binding.includeStepThree.tvConnectStep.setTextColor(getResources().getColor(type == 3 ? R.color.cen_connect_step_selected_color : R.color.cen_connect_step_normal_color));

    }


    /**
     * 进入下一个页面
     */
    public void gotoNextFragment(Fragment fragment) {
        FragManager.getInstance().gotoNextFragment(this, fragmentManager, fragment);
        type++;
        initData(type);
    }
}
