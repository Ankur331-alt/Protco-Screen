package com.smart.device.activity;

import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.device.R;
import com.smart.device.bean.OtaUpgradeBean;
import com.smart.device.databinding.ActivityDeviceOtaUpgradeBinding;
import com.smart.device.listener.OtaUpgradeListener;
import com.smart.device.manager.DownloadManager;
import com.smart.device.manager.UpgradeDialogUtils;
import com.smart.device.viewmodel.DeviceSettingViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttConvertManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.mqtt2.Manager.TopicManager;
import com.smart.rinoiot.common.utils.FileUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.PartClickUtil;
import com.smart.rinoiot.common.utils.StatusBarUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tw
 * @time 2022/10/13 11:15
 * @description 固件升级
 */
public class DeviceOTAUpgradeActivity extends BaseActivity<ActivityDeviceOtaUpgradeBinding, DeviceSettingViewModel> implements View.OnClickListener, OtaUpgradeListener {
    private OtaUpgradeBean otaUpgradeBean;
    private String assetId;
    private String devId;
    private DeviceInfoBean infoBean;

    private boolean isOtaUpgradeFlag = false;//OTA是否升级

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_device_ready_upgrade);
    }

    @Override
    public void init() {
        StatusBarUtil.setTransparentFullTopWindStatusBar(this);
        setFullStatusBar(R.color.main_theme_bg);
        toolBar.setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
        otaUpgradeBean = (OtaUpgradeBean) getIntent().getSerializableExtra("parmars");
        infoBean = (DeviceInfoBean) getIntent().getSerializableExtra(Constant.PANEL_DEVICE_INFO);
        if (infoBean != null) {
            assetId = infoBean.getAssetId();
            devId = infoBean.getId();
            if (otaUpgradeBean == null) {
                mViewModel.showLoading();
                mViewModel.checkUpgrade(devId);
            }
        }
        if (otaUpgradeBean != null) {
            otaUpgradeData();
        }
        mViewModel.getOtaUpgradeBeanMutableLiveData().observe(this, otaUpgradeBean -> {
            if (otaUpgradeBean != null) {
                this.otaUpgradeBean = otaUpgradeBean;
                otaUpgradeData();
            }
        });
    }

    private void otaUpgradeData() {
        DownloadManager.getInstance().initDownLoad(this);
        DownloadManager.getInstance().setOtaUpgradeListener(this);
        initData();
    }

    @Override
    public ActivityDeviceOtaUpgradeBinding getBinding(LayoutInflater inflater) {
        return ActivityDeviceOtaUpgradeBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (otaUpgradeBean == null) {
            return;
        }

        if (v.getId() == R.id.copy_ota_update_version) {
            /// otaUpgradeBean.setFirmwareUrl("https://pub.xiuzhan365.com/resourceFiles/web/home/videos/xiuzhan_new.mp4");
            /// DownloadManager.getInstance().downloadTask(this, otaUpgradeBean.getFirmwareUrl());
            getOTAUrlFileSize();
        } else if (v.getId() == R.id.iv_cancel) {
            //关闭OTA升级
            UpgradeDialogUtils.getInstance().stopDialogLoading();
            UpgradeDialogUtils.getInstance().stopLoading();
            DownloadManager.getInstance().stopDownLoad();
            mViewModel.stopUpgrade(this, new DialogOnListener() {
                @Override
                public void onCancel() {
                    String path = getFilesDir().getPath() + "/OTA/" + otaUpgradeBean.getFirmwareUrl().substring(otaUpgradeBean.getFirmwareUrl().lastIndexOf("/"));
                    DownloadManager.getInstance().clearDownloadFile(path);
                    FileUtils.deleteOTAFile(path);
                }

                @Override
                public void onConfirm() {
                    DownloadManager.getInstance().downloadTask(DeviceOTAUpgradeActivity.this, otaUpgradeBean.getFirmwareUrl());

                }
            });
        }
    }

    /**
     * 设置设备信息
     */
    private void initData() {
        if (otaUpgradeBean != null) {
            DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(devId);
            if (deviceInfo != null) {
                binding.tvOtaCurrentVersion.setText(PartClickUtil.stringColor(this, getString(R.string.rino_device_ota_current_version) + deviceInfo.getFirmwareVersion(), R.color.f_454545, 0, (getString(R.string.rino_device_ota_current_version)).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE));
            }
            binding.tvOtaUpdateVersion.setText(PartClickUtil.stringColor(this, getString(R.string.rino_device_ota_update_version) + ": " + otaUpgradeBean.getVersionTarget(), R.color.f_454545, 0, (getString(R.string.rino_device_ota_update_version) + ": ").length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE));
            binding.tvUpgradeTips.setText(PartClickUtil.stringColor(this, getString(R.string.rino_device_ota_update_tips) + ": " + otaUpgradeBean.getUpgradeInfo(), R.color.f_454545, 0, (getString(R.string.rino_device_ota_update_tips) + ": ").length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE));
        }
        binding.copyOtaUpdateVersion.setOnClickListener(this);
    }

    @Override
    public void upgradeComplete() {
        UpgradeDialogUtils.getInstance().setUpgradeOtaInfo(1, getString(R.string.rino_device_upgrade_finish));
    }

    @Override
    public void upgradeProgress(int progress) {
        UpgradeDialogUtils.getInstance().setUpgradeOtaInfo(2, getString(R.string.rino_device_upgrade_progress) + progress + "%");

    }

    @Override
    public void upgradeError(String errorMsg) {
        UpgradeDialogUtils.getInstance().setUpgradeOtaInfo(3, errorMsg);

    }

    /**
     * 固件升级
     */
    /**
     * 固件升级
     */
    private void getOTAUrlFileSize() {
        new Thread(() -> {
            try {
                String version = "1.0.0";
                if (!TextUtils.isEmpty(devId)) {
                    DeviceInfoBean deviceInfoBean = CacheDataManager.getInstance().getDeviceInfo(devId);
                    if (deviceInfoBean != null) {
                        version = TextUtils.isEmpty(deviceInfoBean.getFirmwareVersion()) ? version : deviceInfoBean.getFirmwareVersion();
                    }
                }
                JSONObject object = new JSONObject();
                object.put("fileSize", otaUpgradeBean.getFirmwareFileSize());
                object.put("md5sum", otaUpgradeBean.getFirmwareMd5());
                object.put("url", otaUpgradeBean.getFirmwareUrl());
                object.put("version", version);

                MqttConvertManager.getInstance().deviceOtaUpgradeSendPublish(object, devId);

                //固件开始升级时，上报接口
                if (infoBean != null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("deviceId", devId);
                    params.put("firmwareId", infoBean.getFirmwareId());
                    if (otaUpgradeBean != null) {
                        params.put("firmwareVersion", otaUpgradeBean.getVersionTarget());
                        params.put("taskId", otaUpgradeBean.getTaskId());
                    }
                    mViewModel.startUpgrade(params);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusNotify(DeviceEvent deviceEvent) {
        /**
         * "progress":{
         *      * "state":"downloading",
         *      * "percent":"10",
         *      * "result_code":"0",
         *      * "result_msg":""
         *      * }
         * */
        if (deviceEvent.getType() == DeviceEvent.Type.OTA_UPGRADE) {//成功，更新OTA
            String progress = (String) deviceEvent.getObj();
            try {
                JSONObject jsonObject = new JSONObject(progress);
                if (jsonObject.has("state")) {
                    String state = jsonObject.getString("state");
                    if (!TextUtils.isEmpty(state)) {
                        if (TextUtils.equals(state, "downloading")) {//下载中
                            String percent = jsonObject.getString("percent");
                            UpgradeDialogUtils.getInstance().setUpgradeOtaInfo(2, getString(R.string.rino_device_upgrade_progress) + percent + "%");
                        } else if (TextUtils.equals(state, "done")) {//更新完成
                            UpgradeDialogUtils.getInstance().setUpgradeOtaInfo(1, getString(R.string.rino_device_upgrade_finish));
                            MqttManager.getInstance().unSubscribe(TopicManager.subscribeSysEventTopic(assetId));
                        } else if (TextUtils.equals(state, "fail")) {//下载失败
                            String result_msg = state;
                            if (jsonObject.has("result_msg")) {
                                result_msg = jsonObject.getString("result_msg");
                            }
                            UpgradeDialogUtils.getInstance().setUpgradeOtaInfo(3, result_msg);
                        }
                    }
                    LgUtils.w("固件升级  onEventBusNotify 固件升级中 state=" + state);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                LgUtils.w("固件升级  onEventBusNotify 固件升级中 异常=" + e.getMessage());
                UpgradeDialogUtils.getInstance().setUpgradeOtaInfo(3, e.getMessage());
            }
        }
    }
}
