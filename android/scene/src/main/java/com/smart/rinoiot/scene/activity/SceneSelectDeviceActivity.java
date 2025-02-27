package com.smart.rinoiot.scene.activity;

import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;

import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.base.BaseViewPagerAdapter;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.databinding.ActivitySceneSelectDeviceBinding;
import com.smart.rinoiot.scene.fragment.DeviceFragment;
import com.smart.rinoiot.scene.viewmodel.SceneViewModel;

import net.lucode.hackware.magicindicator.ViewPagerHelper;

import java.util.ArrayList;
import java.util.List;

public class SceneSelectDeviceActivity extends BaseActivity<ActivitySceneSelectDeviceBinding, SceneViewModel> {

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
//        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
//        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);

        binding.ivBack.setOnClickListener(v -> finishThis());

        AssetBean familyDetail = CacheDataManager.getInstance().getCurrentFamily();
        List<DeviceInfoBean> data = CacheDataManager.getInstance().sceneRemoveGroupDeviceData(familyDetail.getId());

        familyDetail.setDeviceInfoBeans(data);
        if (familyDetail.getChildrens() != null && familyDetail.getChildrens().size() > 0) {
            for (AssetBean children: familyDetail.getChildrens()) {
                List<DeviceInfoBean> deviceList = new ArrayList<>();
                for (DeviceInfoBean deviceInfo: data) {
                    if (children.getId().equals(deviceInfo.getAssetId())) {
                        deviceList.add(deviceInfo);
                    }
                }
                children.setDeviceInfoBeans(deviceList);
            }
        }

        List<String> mTitleDataList = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        mTitleDataList.add(getString(R.string.rino_scene_all_device));
        fragments.add(new DeviceFragment(familyDetail));

        if (familyDetail.getChildrens() != null) {
            for (AssetBean assetBean: familyDetail.getChildrens()) {
                mTitleDataList.add(assetBean.getName());
                fragments.add(new DeviceFragment(assetBean));
            }
        }

        BaseViewPagerAdapter baseViewPagerAdapter = new BaseViewPagerAdapter(getSupportFragmentManager(), fragments);
        binding.pager.setAdapter(baseViewPagerAdapter);
        binding.magicIndicator.setNavigator(mViewModel.createIndicator(binding.pager, mTitleDataList.toArray(new String[0])));

        ViewPagerHelper.bind(binding.magicIndicator, binding.pager);
    }

    @Override
    public ActivitySceneSelectDeviceBinding getBinding(LayoutInflater inflater) {
        return ActivitySceneSelectDeviceBinding.inflate(inflater);
    }
}
