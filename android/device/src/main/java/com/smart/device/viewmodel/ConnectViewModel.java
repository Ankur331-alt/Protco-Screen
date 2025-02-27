package com.smart.device.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.device.bean.RemoveDeviceAndGroupBean;
import com.smart.device.manager.DeviceNetworkManager;
import com.smart.group.api.GroupDeviceApiService;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConnectViewModel extends BaseViewModel {
    public ConnectViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    /**
     * 移除设备或者群组
     */
    private final MutableLiveData<RemoveDeviceAndGroupBean> removeDeviceRemoveLiveData = new MutableLiveData<>();

    public MutableLiveData<RemoveDeviceAndGroupBean> getRemoveDeviceRemoveLiveData() {
        return removeDeviceRemoveLiveData;
    }

    public void removeDeviceOrGroupData(DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean.isCustomGroup()) {
            removeGroup(deviceInfoBean);
        } else {
            unbindToAsset(deviceInfoBean);
        }
    }

    private void unbindToAsset(DeviceInfoBean deviceInfoBean) {
        DeviceNetworkManager.getInstance().unbindToAsset(deviceInfoBean.getId(), 0, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                RemoveDeviceAndGroupBean item = new RemoveDeviceAndGroupBean();
                item.setDeviceInfoBean(deviceInfoBean);
                item.setRemoveSuccess(true);
                removeDeviceRemoveLiveData.postValue(item);
            }

            @Override
            public void onError(String code, String error) {
                RemoveDeviceAndGroupBean item = new RemoveDeviceAndGroupBean();
                item.setDeviceInfoBean(deviceInfoBean);
                item.setRemoveSuccess(false);
                removeDeviceRemoveLiveData.postValue(item);
                removeDeviceRemoveLiveData.postValue(item);
                ToastUtil.info(error);
            }
        });
    }

    /**
     * 删除群组
     */
    private void removeGroup(DeviceInfoBean deviceInfoBean) {
        RetrofitUtils.getService(GroupDeviceApiService.class).removeGroup(deviceInfoBean.getGroupId()).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                RemoveDeviceAndGroupBean item = new RemoveDeviceAndGroupBean();
                item.setDeviceInfoBean(deviceInfoBean);
                item.setRemoveSuccess(true);
                removeDeviceRemoveLiveData.postValue(item);
            }

            @Override
            public void onError(String code, String error) {
                RemoveDeviceAndGroupBean item = new RemoveDeviceAndGroupBean();
                item.setDeviceInfoBean(deviceInfoBean);
                item.setRemoveSuccess(false);
                removeDeviceRemoveLiveData.postValue(item);
                removeDeviceRemoveLiveData.postValue(item);
                ToastUtil.info(error);
            }
        });
    }

    /**
     * 长按设备，设置选中效果
     */
    public List<DeviceInfoBean> getLongClickSelectedData(String devId,String groupId) {
        List<DeviceInfoBean> deviceList = CacheDataManager.getInstance().getAllDeviceList(CacheDataManager.getInstance().getCurrentHomeId());
        if (deviceList != null && !deviceList.isEmpty()) {
            for (DeviceInfoBean info : deviceList) {
                if (TextUtils.isEmpty(groupId) && !info.isCustomGroup() && TextUtils.equals(devId, info.getId())//长按单设备
                ||!TextUtils.isEmpty(groupId) && TextUtils.equals(groupId, info.getGroupId())) {//长按群组设备
                    info.setSelect(true);
                    break;
                }
            }
        }
        return deviceList;
    }
}
