package com.smart.device.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.smart.device.R;
import com.smart.device.adapter.DeviceManagerAdapter;
import com.smart.device.databinding.ActivityDeviceManagerBinding;
import com.smart.device.viewmodel.ConnectViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.ble.BleScanConnectManager;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.matter.MtrDeviceManager;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.view.SpacesItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DeviceManagerActivity extends BaseActivity<ActivityDeviceManagerBinding, ConnectViewModel> {

    private DeviceManagerAdapter deviceAdapter;
    private final Map<String, Boolean> deleteArray = new HashMap<>();
    private int deleteNum;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_device_management);
    }

    @Override
    public void init() {
        String devId = getIntent().getStringExtra(Constant.DEV_ID);
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        binding.tvDelete.setVisibility(TextUtils.isEmpty(devId) && TextUtils.isEmpty(groupId) ? View.GONE : View.VISIBLE);
        deviceAdapter = new DeviceManagerAdapter(new ArrayList<>());
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        binding.recyclerView.addItemDecoration(new SpacesItemDecoration(10, 10));
        binding.recyclerView.setAdapter(deviceAdapter);

        deviceAdapter.setOnItemClickListener((adapter, view, position) -> {
            DeviceInfoBean deviceInfoBean = deviceAdapter.getItem(position);
            deviceInfoBean.setSelect(!deviceInfoBean.isSelect());
            deviceAdapter.notifyItemChanged(position);

            boolean hasSelect = false;
            for (DeviceInfoBean item : deviceAdapter.getData()) {
                if (item.isSelect()) {
                    hasSelect = true;
                    break;
                }
            }
            binding.tvDelete.setVisibility(hasSelect ? View.VISIBLE : View.GONE);
        });

        binding.tvDelete.setOnClickListener(v ->
                DialogUtil.showNormalMsg(DeviceManagerActivity.this, getString(R.string.rino_device_delete_title), getString(R.string.rino_device_delete_message), getString(R.string.rino_common_cancel), getString(R.string.rino_common_confirm), new DialogOnListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onConfirm() {
                        mViewModel.showLoading();
                        deleteArray.clear();
                        deleteNum = 0;
                        for (int i = 0; i < deviceAdapter.getData().size(); i++) {
                            DeviceInfoBean item = deviceAdapter.getData().get(i);
                            if (item.isSelect()) {
                                deleteNum++;
                                deleteArray.put(item.getId() + item.getGroupId(), false);
                                mViewModel.removeDeviceOrGroupData(item);
                            }
                        }
                    }
                })
        );


        List<DeviceInfoBean> deviceList = mViewModel.getLongClickSelectedData(devId, groupId);
        if (deviceList.size() > 0) {
            hideEmptyView();
            deviceAdapter.setNewInstance(deviceList);
            deviceAdapter.notifyDataSetChanged();
        } else {
            showEmptyView();
        }

        mViewModel.getRemoveDeviceRemoveLiveData().observe(this, removeDeviceAndGroupBean -> {
            if (removeDeviceAndGroupBean.isRemoveSuccess()) {
                DeviceInfoBean infoBean = removeDeviceAndGroupBean.getDeviceInfoBean();
                if (infoBean != null) {
                    removeMatterDevice(infoBean);
                    updateDeviceUi(infoBean);
                }
            } else {
                removeFail();
            }
        });
    }

    /**
     * Removes app from matter device fabric
     *
     * @param device the device
     */
    private void removeMatterDevice(DeviceInfoBean device){
        try {
            if(!MtrDeviceDataUtils.isMatterDevice(device)) {
                return;
            }

            Metadata metadata = MtrDeviceDataUtils.toMetadata(device.getMetaInfo());
            MtrDeviceManager.getInstance(getApplicationContext()).remove(metadata.getDeviceId());
        }catch (Exception ex) {
            Log.d(
                TAG,
                "removeMatterDevice: failed to remove matter device. Cause: " +
                        ex.getLocalizedMessage()
            );
        }
    }

    @Override
    public ActivityDeviceManagerBinding getBinding(LayoutInflater inflater) {
        return ActivityDeviceManagerBinding.inflate(inflater);
    }

    /**
     * 移除成功更新UI数据
     */
    private void updateDeviceUi(DeviceInfoBean deviceInfoBean) {
        CacheDataManager.getInstance().removeDeviceUpdateData(deviceInfoBean);
        deleteArray.put(deviceInfoBean.getId() + deviceInfoBean.getGroupId(), true);
        //群组或者设备在线
        if (deviceInfoBean.isCustomGroup() || deviceInfoBean.getOnlineStatus() == 1) {
            deleteNum--;
            if (deleteNum == 0) {
                for (Map.Entry<String, Boolean> entry : deleteArray.entrySet()) {
                    String deviceId = entry.getKey();
                    for (DeviceInfoBean item : deviceAdapter.getData()) {
                        if (TextUtils.equals(deviceId, item.getId() + item.getGroupId()) && entry.getValue()) {
                            deviceAdapter.getData().remove(item);
                            break;
                        }
                    }
                }
                deleteArray.clear();
                mViewModel.hideLoading();
                deviceAdapter.notifyDataSetChanged();
                if (deviceAdapter.getData().size() == 0) {
                    showEmptyView();
                }

                binding.tvDelete.setVisibility(View.GONE);
            }
        } else {//蓝牙配网设备移除
            try {
                JSONObject dataJson = new JSONObject();
                JSONObject content = new JSONObject();
                content.put("ble", "unbind");
                dataJson.put("data", content);
                dataJson.put("type", "thing.network.set");
                BleScanConnectManager.getInstance().sendMessage(deviceInfoBean.getUuid(), dataJson.toString(), data -> {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        //蓝牙设备解绑成功或失败
                        if (jsonObject.optInt("code", -1) == 1803
                                || jsonObject.optInt("code", -1) == 1804) {
                            BleScanConnectManager.getInstance().bleDisConnect(deviceInfoBean.getUuid(), true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }, null);
                AppExecutors.getInstance().delayedThread().schedule(() -> AppExecutors.getInstance().mainThread().execute(() -> {
                    deleteNum--;
                    if (deleteNum == 0) {
                        for (Map.Entry<String, Boolean> entry : deleteArray.entrySet()) {
                            String deviceId = entry.getKey();
                            for (DeviceInfoBean item1 : deviceAdapter.getData()) {
                                if (TextUtils.equals(deviceId, item1.getId() + item1.getGroupId()) && entry.getValue()) {
                                    deviceAdapter.getData().remove(item1);
                                    break;
                                }
                            }
                        }
                        deleteArray.clear();
                        mViewModel.hideLoading();
                        deviceAdapter.notifyDataSetChanged();
                        if (deviceAdapter.getData().size() == 0) {
                            showEmptyView();
                        }
                        BleScanConnectManager.getInstance().stopScan();
                        binding.tvDelete.setVisibility(View.GONE);
                    }
                }), 2, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 移除部分失败，更新ui
     */
    private void removeFail() {
        for (Map.Entry<String, Boolean> entry : deleteArray.entrySet()) {
            String deviceId = entry.getKey();
            for (DeviceInfoBean item : deviceAdapter.getData()) {
                if (deviceId.equals(item.getId() + item.getGroupId()) && entry.getValue()) {
                    deviceAdapter.getData().remove(item);
                    break;
                }
            }
        }
        deviceAdapter.notifyDataSetChanged();
        mViewModel.hideLoading();
        deleteNum = 0;
    }
}
