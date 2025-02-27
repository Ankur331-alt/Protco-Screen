package com.smart.group.activity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.smart.device.R;
import com.smart.device.activity.DevicePanelSettingActivity;
import com.smart.device.databinding.ActivityCreateGroupBinding;
import com.smart.group.adapter.AddGroupDeviceAdapter;
import com.smart.group.viewmodel.CreateGroupViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.CreateGroupBean;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.rn.PanelActivity;
import com.smart.rinoiot.common.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tw
 * @time 2022/11/10 16:02
 * @description 创建群组设备
 */
public class CreateGroupActivity extends BaseActivity<ActivityCreateGroupBinding, CreateGroupViewModel> {
    private AddGroupDeviceAdapter addGroupDeviceAdapter;
    private AddGroupDeviceAdapter notAddGroupDeviceAdapter;
    private List<CreateGroupBean> currentRoomList;
    private List<CreateGroupBean> currentHomeList;
    private List<String> deviceIds;
    private String groupId, devId, productId, groupName;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
        initToolBar();
        groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        devId = getIntent().getStringExtra(Constant.DEV_ID);
        productId = getIntent().getStringExtra(Constant.PRODUCT_ID);
        if (!TextUtils.isEmpty(groupId)) {
            mViewModel.getGroupDetail(groupId);
//            GroupBean groupDeviceInfo = CacheDataManager.getInstance().getGroupDeviceInfo(groupId);
//            if (groupDeviceInfo != null) {
//                groupName = groupDeviceInfo.getName();
//            }
        }
        binding.tvGroupName.setText(getString(TextUtils.isEmpty(groupId) ?
                R.string.rino_device_create_group : R.string.rino_device_add));
        binding.tvGroupTips.setText(getString(TextUtils.isEmpty(groupId) ?
                R.string.rino_device_create_group_desc : R.string.rino_device_edit_group_tips));
        deviceIds = new ArrayList<>();
        initRecyclerViewData();
        initRecyclerViewListener();
    }

    private void initRecyclerViewData() {
        addGroupDeviceAdapter = new AddGroupDeviceAdapter();
        binding.groupAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.groupAddedRecyclerView.setAdapter(addGroupDeviceAdapter);
        setScroolData(binding.groupAddedRecyclerView);

        notAddGroupDeviceAdapter = new AddGroupDeviceAdapter();
        binding.groupNotAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.groupNotAddedRecyclerView.setAdapter(notAddGroupDeviceAdapter);
        setScroolData(binding.groupNotAddedRecyclerView);

        if (TextUtils.isEmpty(groupId)) {
            currentRoomList = CacheDataManager.getInstance().getGroupDeviceData(null, devId);
            for (CreateGroupBean item : currentRoomList) {
                deviceIds.add(item.getDevId());
            }
            addGroupDeviceAdapter.setNewInstance(currentRoomList);

            currentHomeList = CacheDataManager.getInstance().getAllGroupDeviceData(productId, null, devId);
            notAddGroupDeviceHideOrShow();
            notAddGroupDeviceAdapter.setNewInstance(currentHomeList);
        }

    }

    private void initRecyclerViewListener() {
        addGroupDeviceAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            /**
             * 移除设备
             * */
            CreateGroupBean createGroupBean = (CreateGroupBean) adapter.getData().get(position);
            if (createGroupBean == null) {
                return;
            }
            String devId = createGroupBean.getDevId();
            if (!deviceIds.contains(devId) || currentRoomList == null || currentRoomList.isEmpty()
                    || !isExit(currentRoomList, devId)
                    || currentHomeList == null) return;
            deviceIds.remove(createGroupBean.getDevId());
            currentRoomList.remove(createGroupBean);
            createGroupBean.setCurrentRoomFlag(false);
            currentHomeList.add(createGroupBean);
            notAddGroupDeviceHideOrShow();
            addGroupDeviceAdapter.notifyDataSetChanged();
            notAddGroupDeviceAdapter.notifyDataSetChanged();
        });

        notAddGroupDeviceAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            /**
             * 添加设备
             * */
            CreateGroupBean createGroupBean = (CreateGroupBean) adapter.getData().get(position);
            if (createGroupBean == null) {
                return;
            }
            String devId = createGroupBean.getDevId();
            if (currentHomeList == null || currentHomeList.isEmpty()
                    || !isExit(currentHomeList, devId)
                    || currentRoomList == null) return;
            deviceIds.add(createGroupBean.getDevId());
            createGroupBean.setCurrentRoomFlag(true);
            currentRoomList.add(createGroupBean);
            currentHomeList.remove(createGroupBean);
            addGroupDeviceAdapter.notifyDataSetChanged();
            notAddGroupDeviceAdapter.notifyDataSetChanged();
            notAddGroupDeviceHideOrShow();
        });
        mViewModel.getGroupBeanLiveData().observe(this, groupBean -> {
            AppManager.getInstance().finishActivity(DevicePanelSettingActivity.class);
            AppManager.getInstance().finishActivity(PanelActivity.class);
            EventBus.getDefault().post(groupBean);
            finishThis();
        });
        mViewModel.getGroupDetailLiveData().observe(this, groupBean -> {
            if (groupBean == null) return;
            groupName = groupBean.getName();
            currentRoomList = CacheDataManager.getInstance().getGroupDeviceData(groupBean, devId);
            for (CreateGroupBean item : currentRoomList) {
                deviceIds.add(item.getDevId());
            }
            addGroupDeviceAdapter.setNewInstance(currentRoomList);

            currentHomeList = CacheDataManager.getInstance().getAllGroupDeviceData(productId, groupBean, devId);
            notAddGroupDeviceHideOrShow();
            notAddGroupDeviceAdapter.setNewInstance(currentHomeList);
        });
    }

    /**
     * 初始化标题栏数据
     */
    private void initToolBar() {
        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        setToolBarRightText(getString(R.string.rino_common_save));
        hideToolBarBack();
        setToolBarLeftText(getString(R.string.rino_common_cancel));

    }

    @Override
    public ActivityCreateGroupBinding getBinding(LayoutInflater inflater) {
        return ActivityCreateGroupBinding.inflate(inflater);
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        mViewModel.showNormalGroupMsg(this, groupName, deviceIds, groupId);
    }

    /**
     * 判断设备id是否在集合下
     */
    private boolean isExit(List<CreateGroupBean> roomDeviceBeans, String devId) {
        boolean isExit = false;
        for (CreateGroupBean item : roomDeviceBeans) {
            if (TextUtils.equals(devId, item.getDevId())) {
                isExit = true;
                break;
            }
        }
        return isExit;
    }

    /**
     * 可添加群组创建显示或隐藏
     */
    private void notAddGroupDeviceHideOrShow() {
        boolean isEmpty = currentHomeList == null || currentHomeList.isEmpty();
        binding.llNotAddEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.groupNotAddedRecyclerView.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
    }
}
