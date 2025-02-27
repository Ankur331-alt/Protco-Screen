package com.smart.rinoiot.family.activity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.view.InviteMemberPopupView;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.adapter.RoomDeviceAdapter;
import com.smart.rinoiot.family.bean.RoomDeviceBean;
import com.smart.rinoiot.family.databinding.ActivityRoomInfoBinding;
import com.smart.rinoiot.family.viewmodel.RoomViewModel;

import java.util.List;

public class RoomInfoActivity extends BaseActivity<ActivityRoomInfoBinding, RoomViewModel> {

    public AssetBean assetBean;
    private RoomDeviceAdapter currentRoomAdapter;
    private RoomDeviceAdapter currentHomeAdapter;
    private List<RoomDeviceBean> currentRoomList;
    private List<RoomDeviceBean> currentHomeList;
    private List<String> deviceIds;
    private List<String> beforeDeviceIds;
    private List<String> groupIds;
    private List<String> beforeGroupIds;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_family_room_nav_title);
    }

    @Override
    public void init() {
        assetBean = (AssetBean) getIntent().getSerializableExtra("room_detail");
        if (assetBean == null) return;
        mViewModel.getCurrentRoomFamilyDetail(assetBean);
        binding.roomNameView.tvTitle.setText(getString(R.string.rino_family_room_name_title));
        binding.roomNameView.tvSubTitle.setText(assetBean.getName());
        int padding = DpUtils.dip2px(20);
        binding.roomNameView.getRoot().setOnClickListener(view -> showModifyRoomNameDialog());

        mViewModel.getReNameLiveData().observe(this, isSuccess -> {
            mViewModel.hideLoading();
            if (isSuccess) {
                assetBean.setName(binding.roomNameView.tvSubTitle.getText().toString());
                if ((deviceIds == null || deviceIds.isEmpty()) && (groupIds == null || groupIds.isEmpty())) {
                    if (beforeDeviceIds != null && !beforeDeviceIds.isEmpty() || beforeGroupIds != null && !beforeGroupIds.isEmpty()) {
                        mViewModel.batchChangeDeviceAsset(true, assetBean, beforeDeviceIds, beforeGroupIds);
                    } else {
                        setResult(RESULT_OK);
                        finishThis();
                    }
                    return;
                }
                mViewModel.batchChangeDeviceAsset(false, assetBean, deviceIds, groupIds);
            }
        });
        mViewModel.getUpdateRoomDeviceLiveData().observe(this, aBoolean -> {
            if (aBoolean) {
                setResult(RESULT_OK);
                finishThis();
            }
        });
        binding.roomSupportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        currentRoomAdapter = new RoomDeviceAdapter();
        binding.roomSupportRecyclerView.setAdapter(currentRoomAdapter);

        binding.roomNotSupportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        currentHomeAdapter = new RoomDeviceAdapter();
        binding.roomNotSupportRecyclerView.setAdapter(currentHomeAdapter);

        mViewModel.getCurrentRoomLiveData().observe(this, roomDeviceBeans -> {
            deviceIds = mViewModel.getDeviceIds();
            beforeDeviceIds = mViewModel.getBeforeDeviceIds();
            groupIds = mViewModel.getGroupIds();
            beforeGroupIds = mViewModel.getBeforeGroupIds();
            currentRoomList = roomDeviceBeans;
            binding.roomSupportRecyclerView.setVisibility(currentRoomList == null || currentRoomList.isEmpty() ? View.GONE : View.VISIBLE);
            currentRoomAdapter.setNewInstance(currentRoomList);
        });
        mViewModel.getCurrentHomeLiveData().observe(this, roomDeviceBeans -> {
            currentHomeList = roomDeviceBeans;
            binding.llNotSupport.setVisibility(roomDeviceBeans == null ||
                    roomDeviceBeans.isEmpty() ? View.GONE : View.VISIBLE);
            currentHomeAdapter.setNewInstance(currentHomeList);
        });
        currentRoomAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            RoomDeviceBean roomDeviceBean = (RoomDeviceBean) adapter.getData().get(position);
            if (roomDeviceBean == null || roomDeviceBean.getDeviceInfoBean() == null) {
                return;
            }
            String devId = roomDeviceBean.getDeviceInfoBean().getId();
            String groupId = roomDeviceBean.getDeviceInfoBean().getGroupId();
            if (currentRoomList == null || currentRoomList.isEmpty()
                    || !isExit(currentRoomList, devId, groupId)
                    || currentHomeList == null) return;
            boolean isExit = false;
            if (groupIds.contains(groupId)) {
                isExit = true;
                groupIds.remove(roomDeviceBean.getDeviceInfoBean().getGroupId());
            } else {
                if (deviceIds.contains(devId)) {
                    isExit = true;
                    deviceIds.remove(roomDeviceBean.getDeviceInfoBean().getId());
                }
            }
            if (!isExit) return;
            currentRoomList.remove(roomDeviceBean);
            roomDeviceBean.setCurrentRoomFlag(false);
            currentHomeList.add(roomDeviceBean);
            currentRoomAdapter.notifyDataSetChanged();
            currentHomeAdapter.notifyDataSetChanged();
            binding.roomSupportRecyclerView.setVisibility(currentRoomList == null || currentRoomList.isEmpty() ? View.GONE : View.VISIBLE);
            binding.llNotSupport.setVisibility(currentHomeList.isEmpty() ? View.GONE : View.VISIBLE);
        });
        currentHomeAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            RoomDeviceBean roomDeviceBean = (RoomDeviceBean) adapter.getData().get(position);
            if (roomDeviceBean == null || roomDeviceBean.getDeviceInfoBean() == null) {
                return;
            }
            String devId = roomDeviceBean.getDeviceInfoBean().getId();
            String groupId = roomDeviceBean.getDeviceInfoBean().getGroupId();
            if (currentHomeList == null || currentHomeList.isEmpty()
                    || !isExit(currentHomeList, devId, groupId)
                    || currentRoomList == null) return;
            if (roomDeviceBean.getDeviceInfoBean().isCustomGroup()) {
                groupIds.add(roomDeviceBean.getDeviceInfoBean().getGroupId());
            } else {
                deviceIds.add(roomDeviceBean.getDeviceInfoBean().getId());
            }
            roomDeviceBean.setCurrentRoomFlag(true);
            currentRoomList.add(roomDeviceBean);
            currentHomeList.remove(roomDeviceBean);
            currentRoomAdapter.notifyDataSetChanged();
            currentHomeAdapter.notifyDataSetChanged();
            binding.roomSupportRecyclerView.setVisibility(currentRoomList == null || currentRoomList.isEmpty() ? View.GONE : View.VISIBLE);
            binding.llNotSupport.setVisibility(currentHomeList.isEmpty() ? View.GONE : View.VISIBLE);
        });
        binding.tvSave.setOnClickListener(v -> {
            mViewModel.showLoading();
            mViewModel.updateRoomName(assetBean.getId(), binding.roomNameView.tvSubTitle.getText().toString());
        });
    }

    @Override
    public ActivityRoomInfoBinding getBinding(LayoutInflater inflater) {
        return ActivityRoomInfoBinding.inflate(inflater);
    }

    /**
     * 修改房间名称
     */
    private void showModifyRoomNameDialog() {

        new XPopup.Builder(this).dismissOnTouchOutside(false)
                .asCustom(new InviteMemberPopupView(this, getString(R.string.rino_family_room_name_title)
                        , getString(R.string.rino_family_room_input_hint), name -> {
                    if (name.length() == 0) {
                        ToastUtil.showMsg(R.string.rino_family_room_name_not_empty);
                    } else {
                        binding.roomNameView.tvSubTitle.setText(name);
                    }
                })).show();
    }

    /**
     * 判断设备id是否在集合下
     */
    private boolean isExit(List<RoomDeviceBean> roomDeviceBeans, String devId, String groupId) {
        boolean isExit = false;
        for (RoomDeviceBean item : roomDeviceBeans) {
            if (item.getDeviceInfoBean() != null) {
                if (!TextUtils.isEmpty(groupId)) {
                    if (TextUtils.equals(groupId, item.getDeviceInfoBean().getGroupId())) {
                        isExit = true;
                        break;
                    }
                } else {
                    if (TextUtils.equals(devId, item.getDeviceInfoBean().getId())) {
                        isExit = true;
                        break;
                    }
                }
            }
        }
        return isExit;
    }
}
