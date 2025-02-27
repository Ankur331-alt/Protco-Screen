package com.smart.rinoiot.user.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityRegisterBinding;
import com.smart.rinoiot.user.fragment.ForgetPsdFragment;
import com.smart.rinoiot.user.fragment.InputCodeFragment;
import com.smart.rinoiot.user.fragment.InputPasswordFragment;
import com.smart.rinoiot.user.viewmodel.LoginViewModel;

import java.util.List;

public class ForgetPsdActivity extends BaseActivity<ActivityRegisterBinding, LoginViewModel> {

    public static final int REQUEST_CODE_FROM_COUNTRY_SELECT = 0x1001;

    private FragmentManager fragmentManager;

    private String account;
    private String code;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
        loadFragment(new ForgetPsdFragment(), "forgotPassword");

        mViewModel.getSetPsdLiveData().observe(this, isSuccess -> {
            mViewModel.hideLoading();
            if (isSuccess) {
                AppManager.getInstance().finishActivity(ForgetPsdActivity.this);
            }
        });
    }

    @Override
    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager != null && fragmentManager.getFragments().size() > 2) {
            removeFragment(fragmentManager.getFragments());
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
        // 获取到FragmentManager对象
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
        // 开启一个事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, fragment, tag);
        fragmentTransaction.commit();
    }

    private void removeFragment(List<Fragment> fragment) {
        // 获取到FragmentManager对象
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();

        // 开启一个事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment.get(fragment.size() - 1));
        fragmentTransaction.commit();
    }

    public void gotoNextFragment() {
        if (fragmentManager != null) {
            if (fragmentManager.getFragments().size() == 2) {
                loadFragment(new InputCodeFragment(), null);
            } else if (fragmentManager.getFragments().size() == 3) {
                loadFragment(new InputPasswordFragment(), null);
            }
        }
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void resetPassword(String password) {
        mViewModel.showLoading();
        mViewModel.resetPassword(account, password, CacheDataManager.getInstance().getCurrentCountry().getCountryCode(), code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE_FROM_COUNTRY_SELECT) {
            ((ForgetPsdFragment) fragmentManager.findFragmentByTag("forgotPassword")).setCountryBean(CacheDataManager.getInstance().getCurrentCountry());
        }
    }

    @Override
    public ActivityRegisterBinding getBinding(LayoutInflater inflater) {
        return ActivityRegisterBinding.inflate(inflater);
    }
}
