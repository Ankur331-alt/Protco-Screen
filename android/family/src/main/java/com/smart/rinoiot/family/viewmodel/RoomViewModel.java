package com.smart.rinoiot.family.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.family.bean.RoomDeviceBean;
import com.smart.rinoiot.family.manager.FamilyNetworkManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomViewModel extends BaseViewModel {

    public RoomViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    private List<String> groupIds=new ArrayList<>();
    private List<String> beforeGroupIds=new ArrayList<>();

    public List<String> getGroupIds() {
        return groupIds;
    }

    public List<String> getBeforeGroupIds() {
        return beforeGroupIds;
    }

    private List<String> beforeDeviceIds = new ArrayList<>();

    public List<String> getBeforeDeviceIds() {
        return beforeDeviceIds;
    }

    private List<String> deviceIds = new ArrayList<>();

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    /**
     * 修改名称回调
     */
    private MutableLiveData<Boolean> reNameLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getReNameLiveData() {
        return reNameLiveData;
    }


    /**
     * 刷新名称
     */
    public void updateRoomName(String assetId, String name) {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("name", name);
        objectHashMap.put("assetId", assetId);
        updateRoomInfo(objectHashMap);
    }

    /**
     * 更新家庭信息
     */
    public void updateRoomInfo(HashMap<String, Object> map) {
        FamilyNetworkManager.getInstance().updateFamilyInfo(map, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                reNameLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                reNameLiveData.postValue(false);
                ToastUtil.showMsg(error);
            }
        });
    }

    /**
     * 批量修改设备资产
     *
     * @param assetId     当前房间所在的家庭id
     * @param assetRoomId 当前房间id
     * @param deviceIds   需要绑定到当前房间下设备集合
     */
    private MutableLiveData<Boolean> updateRoomDeviceLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getUpdateRoomDeviceLiveData() {
        return updateRoomDeviceLiveData;
    }

    /**
     * 批量修改设备资产
     */
    public void batchChangeDeviceAsset(boolean isAllRemove, AssetBean assetBean, List<String> deviceIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("assetId", isAllRemove ? assetBean.getParentId() : assetBean.getId());
        map.put("defaultAssetId", assetBean.getParentId());
        map.put("deviceIds", deviceIds);
        map.put("overwrite", true);
        FamilyNetworkManager.getInstance().batchChangeDeviceAsset(map, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                updateRoomDeviceLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                updateRoomDeviceLiveData.postValue(false);
                ToastUtil.showMsg(error);
            }
        });
    }

    /**
     * 批量修改资产归属关系 设备id+群组id
     */
    public void batchChangeDeviceAsset(boolean isAllRemove, AssetBean assetBean, List<String> deviceIds, List<String> groupIds) {
        if (deviceIds==null||deviceIds.isEmpty()) deviceIds = new ArrayList<>();
        if (groupIds==null||groupIds.isEmpty()) groupIds = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("assetId", isAllRemove ? assetBean.getParentId() : assetBean.getId());
        map.put("defaultAssetId", assetBean.getParentId());
        map.put("deviceIds", deviceIds);
        map.put("groupIds", groupIds);
        map.put("overwrite", true);
        FamilyNetworkManager.getInstance().batchChangeAssetAffiliation(map, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                updateRoomDeviceLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                updateRoomDeviceLiveData.postValue(false);
                ToastUtil.showMsg(error);
            }
        });
    }

    /**
     * 根据房间id和房间所在的家庭id，分别获取当前房间下的设备和除了当前房间下的所有设备
     */

    public void getCurrentRoomFamilyDetail(AssetBean assetBean) {
        FamilyNetworkManager.getInstance().getFamilyDetail(assetBean.getParentId(), new CallbackListener<AssetBean>() {
            @Override
            public void onSuccess(AssetBean data) {
                getDeviceList(assetBean.getId(), data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    /**
     * 当前房间下的设备
     */
    private MutableLiveData<List<RoomDeviceBean>> currentRoomLiveData = new MutableLiveData<>();

    public MutableLiveData<List<RoomDeviceBean>> getCurrentRoomLiveData() {
        return currentRoomLiveData;
    }

    /**
     * 当前家庭下除了当前房间下的设备
     */
    private MutableLiveData<List<RoomDeviceBean>> currentHomeLiveData = new MutableLiveData<>();

    public MutableLiveData<List<RoomDeviceBean>> getCurrentHomeLiveData() {
        return currentHomeLiveData;
    }

    private void getDeviceList(String assetRoomId, AssetBean familyDetail) {
        if (familyDetail == null || familyDetail.getChildrens() == null || familyDetail.getChildrens().isEmpty()) {
            return;
        }
        List<String> assetIds = new ArrayList<>();
        assetIds.add(familyDetail.getId());
        for (AssetBean assetBean : familyDetail.getChildrens()) {
            assetIds.add(assetBean.getId());
        }
        FamilyNetworkManager.getInstance().getHomeDeviceList(assetIds, new CallbackListener<List<DeviceInfoBean>>() {
            @Override
            public void onSuccess(List<DeviceInfoBean> data) {
                currentRoomLiveData.postValue(getCurrentRoomDeviceData(data, assetRoomId, true, familyDetail.getChildrens()));
                currentHomeLiveData.postValue(getCurrentRoomDeviceData(data, assetRoomId, false, familyDetail.getChildrens()));
            }

            @Override
            public void onError(String code, String error) {
            }
        });
    }

    /**
     * 设备数据处理
     */
    private List<RoomDeviceBean> getCurrentRoomDeviceData(List<DeviceInfoBean> deviceInfoBeans, String assetId,
                                                          boolean isCurrentRoom, List<AssetBean> assetBeans) {
        List<RoomDeviceBean> currentRoomDeviceInfoBeans = new ArrayList<>();
        for (DeviceInfoBean deviceInfoBean : deviceInfoBeans) {
            RoomDeviceBean roomDeviceBean = new RoomDeviceBean();
            boolean flag;
            if (isCurrentRoom) {
                flag = TextUtils.equals(deviceInfoBean.getAssetId(), assetId);
            } else {
                flag = !TextUtils.equals(deviceInfoBean.getAssetId(), assetId);
            }
            if (flag) {
                roomDeviceBean.setCurrentRoomFlag(isCurrentRoom);
                if (isCurrentRoom) {
                    if (deviceInfoBean.isCustomGroup()) {//群组设备
                        groupIds.add(deviceInfoBean.getGroupId());
                        beforeGroupIds.add(deviceInfoBean.getGroupId());
                    } else {
                        deviceIds.add(deviceInfoBean.getId());
                        beforeDeviceIds.add(deviceInfoBean.getId());
                    }
                } else {
                    for (AssetBean assetBean : assetBeans) {
                        if (TextUtils.equals(assetBean.getId(), deviceInfoBean.getAssetId())) {//当前设备所在的房间id
                            roomDeviceBean.setRoomName(assetBean.getName());
                            break;
                        }
                    }
                }
                roomDeviceBean.setDeviceInfoBean(deviceInfoBean);
                currentRoomDeviceInfoBeans.add(roomDeviceBean);
            }
        }
        return currentRoomDeviceInfoBeans;
    }
}
