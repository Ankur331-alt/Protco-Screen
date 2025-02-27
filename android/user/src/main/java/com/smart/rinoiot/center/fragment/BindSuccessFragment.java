package com.smart.rinoiot.center.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.center.activity.CenFlashActivity;
import com.smart.rinoiot.center.manager.CenConstant;
import com.smart.rinoiot.center.viewmodel.CenLoginViewModel;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.center.activity.HomeActivity;
import com.smart.rinoiot.user.databinding.FragmentBindSuccessBinding;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 扫码绑定成功
 */
public class BindSuccessFragment extends BaseFragment<FragmentBindSuccessBinding, CenLoginViewModel> implements View.OnClickListener {
    @Override
    public void init() {
        initData();
    }

    @Override
    public FragmentBindSuccessBinding getBinding(LayoutInflater inflater) {
        return FragmentBindSuccessBinding.inflate(inflater);
    }

    private void initData() {
        binding.tvEnter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvEnter) {
            SharedPreferenceUtil.getInstance().put(CenConstant.IS_LOGIN,true);
            startActivity(new Intent(getContext(), HomeActivity.class));
            AppManager.getInstance().finishActivity(CenFlashActivity.class);
        }
    }

}
