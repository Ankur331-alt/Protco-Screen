package com.smart.rinoiot.family.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.smart.device.activity.DeviceCommissioningMethodActivity;
import com.smart.device.activity.DeviceManagerActivity;
import com.smart.group.manager.GroupManager;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.base.BaseViewPagerAdapter;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.GroupBean;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.FamilyPermissionManager;
import com.smart.rinoiot.common.manager.GroupDeviceControlManager;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.activity.RoomManagerActivity;
import com.smart.rinoiot.family.databinding.FragmentFamilyBinding;
import com.smart.rinoiot.family.viewmodel.FamilyViewModel;
import com.smart.rinoiot.scan.activity.ScanActivity;

import net.lucode.hackware.magicindicator.ViewPagerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author author
 */
public class FamilyFragment extends BaseFragment<FragmentFamilyBinding, FamilyViewModel> implements View.OnClickListener {

    /**
     *
     */
    private static final String TAG = "FamilyFragment";
    List<Fragment> fragments = new ArrayList<>();
    private final Map<Integer, AssetBean> roomMap = new HashMap<>(0);

    @Override
    public void init() {
        LgUtils.w(TAG + "  1111111111111111111111111111111   init");
        EventBus.getDefault().register(this);
        binding.ivMore.setOnClickListener(this);
        mViewModel.getFamilyDetailLiveData().observe(this, familyDetail -> {
            if (!FamilyPermissionManager.getInstance().getPermissionMemberRole(getContext(), null)) {
                binding.ivMore.setVisibility(View.VISIBLE);
            } else {
                binding.ivMore.setVisibility(View.VISIBLE);
            }
            mViewModel.hideLoading();
            binding.refreshLayout.finishRefresh();
            if (familyDetail == null) {
                return;
            }
            List<String> mTitleDataList = new ArrayList<>();
            fragments.clear();
            mTitleDataList.add(getString(R.string.rino_device_all));
            fragments.add(new RoomFragment(familyDetail));

            if (familyDetail.getChildrens() != null) {
                for (AssetBean assetBean : familyDetail.getChildrens()) {
                    mTitleDataList.add(assetBean.getName());
                    fragments.add(new RoomFragment(assetBean));
                }
            }

            binding.magicIndicator.removeAllViews();
            binding.magicIndicator.setNavigator(mViewModel.createIndicator(binding.pager, mTitleDataList));
            BaseViewPagerAdapter baseViewPagerAdapter = new BaseViewPagerAdapter(getChildFragmentManager(), fragments);
            binding.pager.setAdapter(baseViewPagerAdapter);
            binding.pager.setCurrentItem(0);
            //防止fragment页面销毁时，重新加载数据异常
            binding.pager.setOffscreenPageLimit(mTitleDataList.size());
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
        });

        binding.refreshLayout.setRefreshHeader(new MaterialHeader(requireContext()));
        //下拉 刷新整个RecyclerView
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> mViewModel.getFamilyList());

//        mViewModel.showLoading();
    }

    private boolean isFirst = true, isVisibleToUser;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LgUtils.w(TAG + "  1111111111111111111111111111111   setUserVisibleHint  isFirst=" + isFirst + "   isVisibleToUser=" + isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (!isFirst && isAdded())
                mViewModel.getFamilyDetail(CacheDataManager.getInstance().getCurrentHomeId(),true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isFirst = false;
        LgUtils.w(TAG + "  1111111111111111111111111111111   onResume  isFirst=" + isFirst + "   isVisibleToUser=" + isVisibleToUser);
        if (isVisibleToUser) {
            mViewModel.getFamilyList();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public FragmentFamilyBinding getBinding(LayoutInflater inflater) {
        return FragmentFamilyBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivMore) {
            String[] arrList = new String[]{getString(R.string.rino_device_management), getString(R.string.rino_family_room_management), getString(R.string.rino_family_add_matter_device)};
            int[] iconRes = new int[]{R.drawable.ic_device_manager, R.drawable.ic_room_manager, R.drawable.ic_room_manager};
            DialogUtil.showHomeAttachListPopupView(v, arrList, iconRes, (position, text) -> {
                if (position == 0) {
                    Intent intent = new Intent(requireActivity(), DeviceManagerActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    AssetBean currentFamily = CacheDataManager.getInstance().getCurrentFamily();
                    String homeId = CacheDataManager.getInstance().getCurrentHomeId();
                    List<DeviceInfoBean> deviceList = CacheDataManager.getInstance().getAllDeviceList(homeId);
                    Intent intent = new Intent(requireActivity(), RoomManagerActivity.class)
                            .putExtra("family_detail", currentFamily)
                            .putExtra("device_list", new Gson().toJson(deviceList));
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(requireActivity(), DeviceCommissioningMethodActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * mqtt上报修改状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusNotify(DeviceEvent deviceEvent) {
        if (deviceEvent.getType() == DeviceEvent.Type.PANEL_WIFI_CONFIG_SUCCESS
                || deviceEvent.getType() == DeviceEvent.Type.MINE_SCAN_BIND_DEVICE
                || deviceEvent.getType() == DeviceEvent.Type.REMOVE_GROUP
                || deviceEvent.getType() == DeviceEvent.Type.DEVICE_INFO_CHANGED
                || deviceEvent.getType() == DeviceEvent.Type.DP_OPERATOR) {
            mViewModel.getFamilyDetail(CacheDataManager.getInstance().getCurrentHomeId(), true);
        }
    }

    /**
     * mqtt上报修改状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusNotify(GroupBean groupBean) {
        String homeId = CacheDataManager.getInstance().getCurrentHomeId();
        List<DeviceInfoBean> removedDevices = CacheDataManager.getInstance()
                .sceneRemoveGroupDeviceData(homeId);
        DeviceInfoBean deviceInfoBean = CacheDataManager.getInstance()
                .getGroupDeviceSingleData(groupBean, removedDevices);
        if (deviceInfoBean.isGroupOffLineFlag()) {
            //群组下没有设备
            GroupManager.getInstance().showNormalSheet(getActivity(), deviceInfoBean);
            return;
        }
        GroupDeviceControlManager.getInstance().gotoPanel(getContext(), deviceInfoBean);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mViewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String @NotNull [] permissions, int @NotNull [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull @NotNull List<String> perms) {
        startActivityForResult(new Intent(getActivity(), ScanActivity.class)
                .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_device_scan)), Constant.SCAN_BIND_DEVICE_CODE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull @NotNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            String message = getString(R.string.rino_common_open_setting_permission);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            String message = getString(R.string.rino_common_permission_denied);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
