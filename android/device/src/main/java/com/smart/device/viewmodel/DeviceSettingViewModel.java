package com.smart.device.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.smart.device.R;
import com.smart.device.bean.OtaUpgradeBean;
import com.smart.device.manager.DeviceNetworkManager;
import com.smart.group.api.GroupDeviceApiService;
import com.smart.group.manager.GroupManager;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.GroupBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.ble.BleScanConnectManager;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.matter.MtrDeviceManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.rn.PanelActivity;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author tw
 * @time 2022/10/13 11:54
 * @description 面板设置module
 */
public class DeviceSettingViewModel extends BaseViewModel {
    private static final String TAG = DeviceSettingViewModel.class.getSimpleName();

    public DeviceSettingViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    /**
     * 选择删除设备的模式，删除设备+删除设备并清掉缓存
     */
    public void removeDeviceDialog(AppCompatActivity activity, DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean == null) {
            LgUtils.w(TAG + "   设备数据为空");
            return;
        }
        if (deviceInfoBean.isCustomGroup()) {
            //群组
            showDeleteRoomDialog(activity, deviceInfoBean.getGroupId());
        } else {
            DialogUtil.showBottomSheet(activity, "", new String[]{getString(R.string.rino_device_detail_delete_type_unbind), getString(R.string.rino_device_detail_delete_type_unbind_clear)}, (position, text) -> {
                if (position == 0) {
                    //解除绑定
                    unBindDevice(activity, deviceInfoBean);
                } else {
                    //解除并清除数据
                    unBindDeviceClearData(activity, deviceInfoBean);
                }

                if(MtrDeviceDataUtils.isMatterDevice(deviceInfoBean)){
                    Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfoBean.getMetaInfo());
                    try {
                        MtrDeviceManager.getInstance(activity.getApplicationContext()).remove(
                                metadata.getDeviceId()
                        );
                    } catch (Exception ex) {
                        Log.d(TAG, "removeDeviceDialog: matter device clean up failed");
                    }
                }
            });
        }
    }

    /**
     * 是否解散群组
     */
    private void showDeleteRoomDialog(AppCompatActivity activity, String groupId) {
        DialogUtil.showNormalMsg(activity, getString(R.string.rino_common_alert_title), getString(R.string.rino_device_dissolve_group), new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                GroupManager.getInstance().removeGroup(activity, groupId, true);
            }
        });
    }

    /**
     * 解除绑定
     */
    private void unBindDevice(AppCompatActivity activity, DeviceInfoBean deviceInfoBean) {
        DeviceNetworkManager.getInstance().unbindToAsset(deviceInfoBean.getId(), 0, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                sendUnbindBle(activity, deviceInfoBean);

            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    /**
     * 解除绑定并清除数据
     */
    private void unBindDeviceClearData(AppCompatActivity activity, DeviceInfoBean deviceInfoBean) {
        DeviceNetworkManager.getInstance().unbindToAsset(deviceInfoBean.getId(), 1, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                sendUnbindBle(activity, deviceInfoBean);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    /**
     * 通过ble发送解绑消息
     */
    private void sendUnbindBle(AppCompatActivity activity, DeviceInfoBean deviceInfoBean) {
        CacheDataManager.getInstance().removeDeviceUpdateData(deviceInfoBean);
        if (deviceInfoBean.getOnlineStatus() == 0) {
            try {
                JSONObject dataJson = new JSONObject();
                JSONObject content = new JSONObject();
                content.put("ble", "unbind");
                dataJson.put("data", content);
                dataJson.put("type", "thing.network.set");
                showLoading();
                BleScanConnectManager.getInstance().sendMessage(deviceInfoBean.getUuid(), dataJson.toString(), data -> BleScanConnectManager.getInstance().bleDisConnect(deviceInfoBean.getUuid(), true), null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                AppExecutors.getInstance().delayedThread().schedule(() -> AppExecutors.getInstance().mainThread().execute(() -> {
                    hideLoading();
                    AppManager.getInstance().finishActivity(PanelActivity.class);
                    BleScanConnectManager.getInstance().stopScan();
                    activity.finish();
                }), 2, TimeUnit.SECONDS);
            }
            return;
        }
        AppManager.getInstance().finishActivity(PanelActivity.class);
        activity.finish();
    }

    /**
     * 绑定资产设备
     */
    private final MutableLiveData<String> updateInfoLiveData = new MutableLiveData<>();

    public MutableLiveData<String> getUpdateInfoLiveData() {
        return updateInfoLiveData;
    }

    public void bindToAsset(String assetId, String deviceId, String deviceName) {
        DeviceNetworkManager.getInstance().initDevice(assetId, deviceId, deviceName, new CallbackListener<Objects>() {
            @Override
            public void onSuccess(Objects data) {
                updateInfoLiveData.postValue(deviceName);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    /**
     * 修改名称提示框
     */
    public void renameDialogShowMsg(AppCompatActivity activity, DeviceInfoBean infoBean) {
        DialogUtil.showInputDialog(activity, getString(R.string.rino_device_panel_dialog_rename_title),
                infoBean.getName(), infoBean.getName(), text -> {
                    if (TextUtils.isEmpty(text) || TextUtils.equals(text, infoBean.getName())) {
                        return;
                    }
                    bindToAsset(TextUtils.isEmpty(infoBean.getAssetId()) ?
                                    CacheDataManager.getInstance().getCurrentHomeId() : infoBean.getAssetId(),
                            infoBean.getUuid(), text);
                }, () -> {

                });
    }

    /**
     * 修改群组名称
     */
    public void updateGroupName(AppCompatActivity activity, String oldName, List<String> devIds, String groupId) {
        DialogUtil.showInputGroupNameDialog(activity, getString(R.string.rino_device_create_group_rename), "", oldName, text -> saveOrUpdate(devIds, text, groupId), () -> {

        });
    }

    /**
     * 创建或更新群组
     */
    public void saveOrUpdate(List<String> deviceIds, String groupName, String groupId) {
        if (TextUtils.isEmpty(groupName)) {
            ToastUtil.showMsg(getString(R.string.rinp_device_group_input_name));
            return;
        }
        showLoading();
        Map<String, Object> map = new HashMap<>();
        map.put("assetId", CacheDataManager.getInstance().getCurrentHomeId());
        map.put("deviceIds", deviceIds);
        map.put("name", groupName);
        map.put("id", groupId);
        RetrofitUtils.getService(GroupDeviceApiService.class).saveOrUpdate(map).enqueue(new BaseRequestListener<GroupBean>() {
            @Override
            public void onResult(GroupBean result) {
                hideLoading();
                ToastUtil.info(getString(R.string.rino_common_operation_success));
                updateInfoLiveData.postValue(result.getName());
                CacheDataManager.getInstance().updateModeData(result);
            }

            @Override
            public void onError(String error, String msg) {
                hideLoading();
                ToastUtil.info(msg);
            }
        });
    }

    /**
     * 检查设备固件是否需要升级
     */
    private final MutableLiveData<OtaUpgradeBean> otaUpgradeBeanMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<OtaUpgradeBean> getOtaUpgradeBeanMutableLiveData() {
        return otaUpgradeBeanMutableLiveData;
    }

    public void checkUpgrade(String devId) {
        DeviceNetworkManager.getInstance().checkUpgrade(devId, new CallbackListener<OtaUpgradeBean>() {
            @Override
            public void onSuccess(OtaUpgradeBean data) {
                otaUpgradeBeanMutableLiveData.postValue(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    /**
     * 停止OTA升级
     */
    public void stopUpgrade(AppCompatActivity appCompatActivity, DialogOnListener dialogOnListener) {
        DialogUtil.showNormalMsg(appCompatActivity, getString(R.string.rino_common_alert_title),
                getString(R.string.rino_device_upgrade_stop), getString(R.string.rino_device_upgrade_quit),
                getString(R.string.rino_device_upgrade_not_quit)
                , dialogOnListener);
    }

    /**
     * 开始升级
     *
     * @param params upgrade parameters
     */
    public void startUpgrade(Map<String, Object> params) {
        // ToDo() implement method
    }
}
