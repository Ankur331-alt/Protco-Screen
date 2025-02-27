package com.smart.device.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.device.R;
import com.smart.device.databinding.ActivityDevicePanelRenameBinding;
import com.smart.device.viewmodel.DeviceSettingViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.GroupBean;
import com.smart.rinoiot.common.bean.GroupDeviceItemBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.UpdateDeviceInfoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tw
 * @time 2022/10/13 11:15
 * @description 从面板进入设备设置页面 修改设备名称
 */
public class DevicePanelRenameActivity extends BaseActivity<ActivityDevicePanelRenameBinding, DeviceSettingViewModel> implements View.OnClickListener {
    private DeviceInfoBean infoBean;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
        infoBean = (DeviceInfoBean) getIntent().getSerializableExtra("parmars");
        if (infoBean != null) {
            binding.tvRename.setText(infoBean.getName());
            ImageLoader.getInstance().bindImageUrl(infoBean.getImageUrl(), binding.ivDeviceIcon, R.drawable.icon_placeholder, R.drawable.icon_placeholder);
        }
        binding.tvRename.setOnClickListener(this);
        mViewModel.getUpdateInfoLiveData().observe(this, s -> {
            binding.tvRename.setText(s);
            UpdateDeviceInfoManager.getInstance().sendPanelRenameNotice(s);
        });
    }

    @Override
    public ActivityDevicePanelRenameBinding getBinding(LayoutInflater inflater) {
        return ActivityDevicePanelRenameBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvRename) {
            if (infoBean != null) {
                if (infoBean.isCustomGroup()) {
                    GroupBean groupDeviceInfo = CacheDataManager.getInstance().getGroupDeviceInfo(infoBean.getGroupId());
                    if (groupDeviceInfo != null && groupDeviceInfo.getDeviceList() != null && !groupDeviceInfo.getDeviceList().isEmpty()) {
                        List<String> devIds = new ArrayList<>();
                        for (GroupDeviceItemBean item : groupDeviceInfo.getDeviceList()) {
                            devIds.add(item.getId());
                        }
                        mViewModel.updateGroupName(this, groupDeviceInfo.getName(), devIds, groupDeviceInfo.getId());
                    }
                } else {
                    mViewModel.renameDialogShowMsg(this, infoBean);
                }
            }
        }
    }

    /**
     * 修改昵称，返回设备名称
     */
    private void resultBack() {
        Intent intent = new Intent();
        intent.putExtra(Constant.RENAME_CONTENT, binding.tvRename.getText().toString());
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBack(View view) {
        resultBack();
        super.onBack(view);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            resultBack();
        }
        return super.onKeyUp(keyCode, event);
    }
}
