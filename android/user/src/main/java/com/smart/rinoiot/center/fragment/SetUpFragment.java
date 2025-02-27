package com.smart.rinoiot.center.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.smart.rinoiot.center.activity.CenFlashActivity;
import com.smart.rinoiot.center.activity.HomeActivity;
import com.smart.rinoiot.center.manager.CenConstant;
import com.smart.rinoiot.center.viewmodel.SetUpViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.base.BaseViewPagerAdapter;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.FragmentSetUpBinding;

import net.lucode.hackware.magicindicator.ViewPagerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 设置页面
 */
public class SetUpFragment extends BaseFragment<FragmentSetUpBinding, SetUpViewModel> implements View.OnClickListener {
    @Override
    public void init() {
        initData();
    }

    @Override
    public FragmentSetUpBinding getBinding(LayoutInflater inflater) {
        return FragmentSetUpBinding.inflate(inflater);
    }

    private void initData() {
        binding.magicIndicator.removeAllViews();
        List<String> titleData = getSetUpTitleData();
        binding.magicIndicator.setNavigator(mViewModel.createIndicator(binding.pager, titleData));
        BaseViewPagerAdapter baseViewPagerAdapter = new BaseViewPagerAdapter(
                getChildFragmentManager(), getSetUpPageData()
        );

        binding.pager.setAdapter(baseViewPagerAdapter);
        binding.pager.setCurrentItem(0);
        //防止fragment页面销毁时，重新加载数据异常
        binding.pager.setOffscreenPageLimit(titleData.size());
        ViewPagerHelper.bind(binding.magicIndicator, binding.pager);
        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mViewModel.setSelectedType(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 获取设置页面横向Tab页
     */
    public List<Fragment> getSetUpPageData() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new AccountFragment());
        fragments.add(new FamilyManagerFragment());
        fragments.add(new ChangeWifiFragment());
        fragments.add(new UpdateFragment());
        fragments.add(new AboutFragment());
        fragments.add(new MoreFragment());
        return fragments;
    }

    /**
     * 获取设置页面横向Tab页标题
     */
    public List<String> getSetUpTitleData() {
        List<String> tabTitles = new ArrayList<>();
        tabTitles.add(getString(R.string.rino_user_account));
        tabTitles.add(getString(R.string.rino_common_family_management));
        tabTitles.add(getString(R.string.rino_user_wifi));
        tabTitles.add(getString(R.string.rino_user_system_update));
        tabTitles.add(getString(R.string.rino_user_about_devices));
        tabTitles.add(getString(R.string.rino_scene_more_title));
        return tabTitles;
    }

    @Override
    @SuppressWarnings("all")
    public void onClick(View v) {
        if (v.getId() == R.id.tvEnter) {
            SharedPreferenceUtil.getInstance().put(CenConstant.IS_LOGIN, true);
            startActivity(new Intent(getContext(), HomeActivity.class));
            AppManager.getInstance().finishActivity(CenFlashActivity.class);
        }
    }

    @Override
    @SuppressWarnings("all")
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&SharedPreferenceUtil.getInstance().get(Constant.FAMILY_MANAGER,false)) {
            SharedPreferenceUtil.getInstance().put(Constant.FAMILY_MANAGER, false);
            if (binding.pager!=null&&binding.pager.getChildCount()>2) {
                binding.pager.setCurrentItem(1);
            }
        }
    }
}
