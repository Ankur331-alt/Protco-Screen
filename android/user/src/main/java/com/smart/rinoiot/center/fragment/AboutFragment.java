package com.smart.rinoiot.center.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;

import com.smart.rinoiot.center.viewmodel.SetUpViewModel;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.utils.SystemUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.FragmentAboutBinding;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 关于设备信息
 */
@SuppressLint("SetTextI18n")
public class AboutFragment extends BaseFragment<FragmentAboutBinding, SetUpViewModel> {
    @Override
    public void init() {
        initData();
    }

    @Override
    public FragmentAboutBinding getBinding(LayoutInflater inflater) {
        return FragmentAboutBinding.inflate(inflater);
    }

    private void initData() {
        binding.includeModel.tvName.setText(getString(R.string.rino_user_equipment_model));
        binding.includeModel.tvSubName.setText(SystemUtil.getSystemModel());
        binding.includeHardwareVersion.tvName.setText(getString(R.string.rino_user_hardware_version));
        binding.includeHardwareVersion.tvSubName.setText(SystemUtil.getDeviceHardware());
        binding.includeSystemVersion.tvName.setText(getString(R.string.rino_user_system_version));
        binding.includeSystemVersion.tvSubName.setText("v" + SystemUtil.getSystemVersion());
    }

}
