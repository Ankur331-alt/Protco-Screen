package com.smart.rinoiot.center.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.center.update.CheckUpdateUtil;
import com.smart.rinoiot.center.viewmodel.SetUpViewModel;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.FragmentUpdateBinding;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 应用升级
 */
@SuppressLint("SetTextI18n")
public class UpdateFragment extends BaseFragment<FragmentUpdateBinding, SetUpViewModel> implements View.OnClickListener {
    @Override
    public void init() {
        initData();
    }

    @Override
    public FragmentUpdateBinding getBinding(LayoutInflater inflater) {
        return FragmentUpdateBinding.inflate(inflater);
    }

    private void initData() {
        binding.tvLocationVersion.setText(getString(R.string.rino_device_ota_current_version) + " v" + CheckUpdateUtil.getLocalVersion());
        binding.ivSwitchStatus.setOnClickListener(this);
        update();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivSwitchStatus) {
            binding.ivSwitchStatus.setSelected(!binding.ivSwitchStatus.isSelected());
            update();
        }
    }


    private void update() {
        if (binding.ivSwitchStatus.isSelected()) {
            mViewModel.checkUpdate();
        }
    }
}
