package com.smart.device.activity;

import android.content.Intent;
import android.view.LayoutInflater;

import com.smart.device.databinding.ActivityDeviceCommissioningMethodBinding;
import com.smart.device.viewmodel.MtrDeviceCommissioningViewModel;
import com.smart.rinoiot.common.base.BaseActivity;

/**
 * @author edwin
 */
public class DeviceCommissioningMethodActivity extends BaseActivity<ActivityDeviceCommissioningMethodBinding, MtrDeviceCommissioningViewModel> {
    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
        // register listeners
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.llAutomaticPairing.setOnClickListener(v -> goToAutomaticDeviceCommissioning());
    }

    private void goToAutomaticDeviceCommissioning() {
        Intent intent = new Intent(this, DeviceAutomaticCommissioningActivity.class);
        startActivity(intent);
    }

    @Override
    public ActivityDeviceCommissioningMethodBinding getBinding(LayoutInflater inflater) {
        return ActivityDeviceCommissioningMethodBinding.inflate(inflater);
    }
}
