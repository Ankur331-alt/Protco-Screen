package com.smart.device.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.smart.device.R;
import com.smart.device.bean.OtaUpgradeBean;
import com.smart.device.databinding.ActivityDevicePanelSettingBinding;
import com.smart.device.viewmodel.DeviceSettingViewModel;
import com.smart.group.activity.CreateGroupActivity;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.GroupBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.FamilyPermissionManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.utils.ActivityUtils;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.PartClickUtil;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.common.utils.ToastUtil;

/**
 * @author tw
 * @time 2022/10/13 11:15
 * @description 从面板进入设备设置页面
 */
@SuppressLint("SetTextI18n")
public class DevicePanelSettingActivity
        extends BaseActivity<ActivityDevicePanelSettingBinding, DeviceSettingViewModel>
        implements View.OnClickListener
{
    private DeviceInfoBean infoBean;
    private String devId, groupId;
    private OtaUpgradeBean otaUpgradeBean;

    private ShareDeviceMethodsDialog shareDeviceMethodsDialog;

    private static final int RENAME_REQUEST_CODE = 100;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    @SuppressWarnings("all")
    public void init() {
        StatusBarUtil.setTransparentFullTopWindStatusBar(this);
        setFullStatusBar(R.color.cen_main_theme_bg);
        toolBar.setToolBarBackground(getResources().getColor(R.color.cen_main_theme_bg, null));
        infoBean = (DeviceInfoBean) getIntent().getSerializableExtra("parmars");
        setListeners();
        if (infoBean != null) {
            devId = infoBean.getId();
            groupId = infoBean.getGroupId();
            ImageLoader.getInstance().bindRoundImageUrl(
                    infoBean.getImageUrl(), binding.ivLogo,
                    DpUtils.dip2px(10), R.drawable.icon_placeholder
            );
        }

        setupShareDeviceDialog();
        displayDeviceInfo();
        otaUpGradeData();
        permissionMemberRole();
    }

    private void setupShareDeviceDialog(){
        shareDeviceMethodsDialog = new ShareDeviceMethodsDialog(this);
        shareDeviceMethodsDialog.setPinCodeListener(view -> {
            Log.d(TAG, "onPinCode: going with the pin");
            startShareDeviceActivity(ShareDeviceMethod.PIN_CODE);
        });
        shareDeviceMethodsDialog.setQrCodeListener(view -> {
            Log.d(TAG, "onQrCode: going with the code");
            startShareDeviceActivity(ShareDeviceMethod.QR_CODE);
        });
    }

    private void startShareDeviceActivity(ShareDeviceMethod method){
        Intent intent = new Intent(this, ShareDeviceActivity.class);
        intent.putExtra(
                ShareDeviceMethod.SHARE_METHOD_KEY, method.getValue()
        );
        intent.putExtra(ShareDeviceMethod.DEVICE_ID_KEY, infoBean.getId());
        startActivity(intent);
    }

    /**
     * Sets the on click listener for clickable views in this activity.
     * ToDo() not everyone needs to listen at all times. use device info to check who should.
     */
    private void setListeners(){
        binding.llEdit.setOnClickListener(this);
        binding.tvDelete.setOnClickListener(this);
        binding.llGroup.setOnClickListener(this);
        binding.llShare.setOnClickListener(this);
        binding.copyDeviceId.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.checkUpgrade(devId);
    }

    @Override
    public ActivityDevicePanelSettingBinding getBinding(LayoutInflater inflater) {
        return ActivityDevicePanelSettingBinding.inflate(inflater);
    }

    @Override
    @SuppressWarnings("all")
    public void onClick(View view) {
        if (infoBean == null) {
            return;
        }
        if (view.getId() == R.id.llEdit) {
            ActivityUtils.startActivityForResult(
                    this, infoBean, DevicePanelRenameActivity.class, RENAME_REQUEST_CODE
            );
        } else if (view.getId() == R.id.tvDelete) {
            mViewModel.removeDeviceDialog(this, infoBean);
        } else if (view.getId() == R.id.copyDeviceId) {
            ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(binding.deviceId.getText());
            ToastUtil.showMsg(getString(R.string.rino_common_cope_succeuss));
        } else if (view.getId() == R.id.deviceUpdateTips) {
            //固件升级
            startActivity(new Intent(this, DeviceOTAUpgradeActivity.class)
                    .putExtra("parmars", otaUpgradeBean)
                    .putExtra(Constant.ASSETID, infoBean.getAssetId())
                    .putExtra(Constant.DEV_ID, infoBean.getId()));
        } else if (view.getId() == R.id.ll_group) {
            //创建群组
            Intent intent = new Intent(this, CreateGroupActivity.class);
            intent.putExtra(Constant.PRODUCT_ID, infoBean.getProductId());
            if (infoBean.isCustomGroup()) {
                intent.putExtra(Constant.GROUP_ID, groupId);
            } else {
                intent.putExtra(Constant.DEV_ID, devId);
            }
            startActivity(intent);
        }else if(view.getId() == R.id.ll_share){
            if(MtrDeviceDataUtils.isMatterDevice(infoBean)){
                startMatterDeviceSharing();
            }
        }
    }

    /**
     * Starts the matter device sharing flow.
     */
    private void startMatterDeviceSharing(){
        // ToDo start (matter) device sharing.
        Log.i(TAG, "onClick: starting matter device sharing");
        shareDeviceMethodsDialog.show();
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == RENAME_REQUEST_CODE) {
            binding.tvName.setText(data.getStringExtra(Constant.RENAME_CONTENT));
        }
    }

    /**
     * Builds the device tech details
     * @param resId string resource identifier
     * @return the spannable label.
     */
    private Spannable getTechnicalDetails(int resId){
        String label = String.format("%s: %s", getString(resId), infoBean.getId());
        int labelLen = (getString(resId) + ": ").length();
        return PartClickUtil.stringColor(
                this, label, R.color.f_f0f0f0,
                0, labelLen, Spannable.SPAN_INCLUSIVE_INCLUSIVE
        );
    }

    /**
     * 设置设备信息
     */
    private void displayDeviceInfo() {
        if(null == infoBean){
            return;
        }
        // set device identifier.
        binding.deviceId.setText(getTechnicalDetails(R.string.rino_device_detail_info_devid));

        // set device IP address.
        binding.deviceIp.setText(getTechnicalDetails(R.string.rino_device_detail_info_ipmac));

        // set device zoom (whatever that is)
        binding.deviceZoom.setText(getTechnicalDetails(R.string.rino_device_time_zone));
        if(MtrDeviceDataUtils.isMatterDevice(infoBean)){
            binding.tvDeviceType.setText(": Matter");
        } else if (infoBean.getOnlineStatus() == 0) {
            binding.tvDeviceType.setText(": Bluetooth");
        } else {
            binding.tvDeviceType.setText(": WiFi");
        }

        binding.tvSignal.setVisibility(View.GONE);
        if(MtrDeviceDataUtils.isMatterDevice(infoBean)){
            binding.llShare.setVisibility(View.VISIBLE);
            binding.llUpdateOta.setVisibility( View.GONE);
        }else{
            binding.llShare.setVisibility(View.GONE);
            binding.llDeviceInfo.setVisibility(infoBean.isCustomGroup() ? View.GONE : View.VISIBLE);
            binding.llUpdateOta.setVisibility(infoBean.isCustomGroup() ? View.GONE : View.VISIBLE);
            GroupBean groupDeviceInfo = CacheDataManager.getInstance().getGroupDeviceInfo(groupId);
            boolean updateGroup =  groupDeviceInfo != null &&
                    groupDeviceInfo.getDeviceList() != null &&
                    !groupDeviceInfo.getDeviceList().isEmpty();
            if (updateGroup) {
                binding.tvGroupCount.setText(String.valueOf(groupDeviceInfo.getDeviceList().size()));
                binding.tvName.setText(groupDeviceInfo.getName());
                binding.tvGroupName.setText(getString(R.string.rino_device_edit_group_name));
            } else {
                binding.tvGroupName.setText(getString(R.string.rino_device_create_group));
                binding.tvName.setText(infoBean.getName());
            }
        }
        int deleteBtnLabel = infoBean.isCustomGroup() ?
                R.string.rino_device_remove_group : R.string.rino_device_detail_delete_title;
        binding.tvDelete.setText(getString(deleteBtnLabel));
    }

    /**
     * 固件升级
     */
    private void otaUpGradeData() {
        mViewModel.getOtaUpgradeBeanMutableLiveData().observe(this, otaUpgradeBean -> {
            LgUtils.w("otaUpGradeData otaUpgradeBean=" + new Gson().toJson(otaUpgradeBean));
            if (otaUpgradeBean == null) {
                binding.deviceUpdateTips.setText(getString(R.string.rino_device_latest));
                binding.deviceUpdateTips.setEnabled(false);
                binding.ivUpgradeTips.setVisibility(View.GONE);
                return;
            }
            binding.deviceUpdateTips.setEnabled(true);
            this.otaUpgradeBean = otaUpgradeBean;
            binding.ivUpgradeTips.setVisibility(View.VISIBLE);
            binding.deviceUpdateTips.setText(getString(R.string.rino_device_ota_upgrade));
        });
        binding.deviceUpdateTips.setOnClickListener(this);
    }

    /**
     * 根据权限，展示对应的功能，面板设置页面不能修改名称+群组模块隐藏
     */
    private void permissionMemberRole() {
        boolean memberHasPermission = FamilyPermissionManager.getInstance()
                .getPermissionMemberRole(this, null);

        if (!memberHasPermission) {
            binding.llEdit.setEnabled(false);
            binding.ivEdit.setVisibility(View.GONE);
            binding.llGroup.setVisibility(View.GONE);
            binding.tvDelete.setVisibility(View.GONE);
        } else {
            binding.llEdit.setEnabled(true);
            binding.ivEdit.setVisibility(View.VISIBLE);
            binding.llGroup.setVisibility(View.VISIBLE);
        }
        if(null == infoBean){
            return;
        }
        if(infoBean.isCustomGroup()){
            return;
        }
        //蓝牙配网不展示
        if ((infoBean.getBindMode() == 1 || infoBean.getIsGroup() == 0)) {
            binding.llGroup.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null != shareDeviceMethodsDialog){
            shareDeviceMethodsDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != shareDeviceMethodsDialog){
            shareDeviceMethodsDialog.dismiss();
        }
    }
}
