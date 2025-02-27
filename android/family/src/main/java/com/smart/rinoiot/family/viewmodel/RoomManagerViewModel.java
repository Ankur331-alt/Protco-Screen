package com.smart.rinoiot.family.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.SortBean;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.event.FamilyChangeEventTarget;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.listener.OnRemoveFamilyListener;
import com.smart.rinoiot.family.manager.FamilyNetworkManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/7
 */
public class RoomManagerViewModel extends BaseViewModel {
    /**
     * 是否删除了房间
     *
     * @param application
     */
    public MutableLiveData<Boolean> isDeleteRoom = new MutableLiveData<>();

    private static final String TAG = "RoomManagerViewModel";

    public MutableLiveData<Boolean> getIsDeleteRoom() {
        return isDeleteRoom;
    }

    /**
     * 家庭详情
     */
    private final MutableLiveData<AssetBean> familyDetailLiveData = new MutableLiveData<>();

    public MutableLiveData<AssetBean> getFamilyDetailLiveData() {
        return familyDetailLiveData;
    }

    /**
     * 是否重新排序
     *
     * @param application
     */
    public MutableLiveData<Boolean> isSort = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsSort() {
        return isSort;
    }

    public RoomManagerViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * 获取设备列表
     *
     * @param assetIds
     */
    public void getDeviceInfoList(List<String> assetIds, CallbackListener<List<DeviceInfoBean>> callbackListener) {
        FamilyNetworkManager.getInstance().getHomeDeviceList(assetIds, new CallbackListener<List<DeviceInfoBean>>() {
            @Override
            public void onSuccess(List<DeviceInfoBean> data) {
                if (callbackListener != null) {
                    callbackListener.onSuccess(data);
                }
            }

            @Override
            public void onError(String code, String error) {
            }
        });
    }

    /**
     * 删除房间  需要同步回调
     */
    public void removeRoom(String assetId, OnRemoveFamilyListener onRemoveFamilyListener) {
        FamilyNetworkManager.getInstance().deleteFamily(assetId, new CallbackListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                ToastUtil.showMsg(getString(R.string.rino_family_delete_success));
                onRemoveFamilyListener.OnRemove();
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    /**
     * 重新排序
     */
    public void sortRoomList(List<AssetBean> assetBeans) {
        List<SortBean> sortBeans = new ArrayList<>();
        for (int i = 0; i < assetBeans.size(); i++) {
            SortBean sortBean = new SortBean();
            sortBean.setTargetId(assetBeans.get(i).getId());
            sortBean.setSortNumber(i);
            sortBeans.add(sortBean);
        }
        FamilyNetworkManager.getInstance().sortRoom(sortBeans, new CallbackListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                isSort.setValue(true);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    public void getFamilyDetail(String assetId) {
        FamilyNetworkManager.getInstance().getFamilyDetail(assetId, new CallbackListener<AssetBean>() {
            @Override
            public void onSuccess(AssetBean data) {
                getDeviceList(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
            }
        });
    }

    private void getDeviceList(AssetBean familyDetail) {
        List<String> assetIdArray = getAssetIdArray(familyDetail);
        FamilyNetworkManager.getInstance().getHomeDeviceList(assetIdArray, new CallbackListener<List<DeviceInfoBean>>() {
            @Override
            public void onSuccess(List<DeviceInfoBean> data) {
                AssetBean currentFamilyDetail = addDeviceListToAsset(familyDetail, data);
                familyDetailLiveData.postValue(currentFamilyDetail);
                try {
                    CacheDataManager.getInstance().saveCurrentFamily(currentFamilyDetail);
                    EventBus.getDefault().post(new DeviceEvent(
                            DeviceEvent.Type.CHANGE_FAMILY, FamilyChangeEventTarget.REFRESH_DEVICES
                    ));
                }catch (Exception exception) {
                    Log.d(TAG, "onSuccess: failed to cache and notify");
                }
            }

            @Override
            public void onError(String code, String error) {
                familyDetailLiveData.postValue(familyDetail);
            }
        });
    }

    private List<String> getAssetIdArray(AssetBean assetBean) {
        List<String> result = new ArrayList<>();
        if (assetBean != null) {
            result.add(assetBean.getId());

            List<AssetBean> childrenArray = assetBean.getChildrens();
            if (childrenArray != null && childrenArray.size() > 0) {
                for (AssetBean item : childrenArray) {
                    result.add(item.getId());
                }
            }
        }
        return result;
    }

    public AssetBean addDeviceListToAsset(AssetBean familyDetail, List<DeviceInfoBean> data) {
        if (data == null || data.size() == 0) return familyDetail;

        familyDetail.setDeviceInfoBeans(data);
        if (familyDetail.getChildrens() != null && familyDetail.getChildrens().size() > 0) {
            for (AssetBean children : familyDetail.getChildrens()) {
                List<DeviceInfoBean> deviceList = new ArrayList<>();
                for (DeviceInfoBean deviceInfo : data) {
                    if (children.getId().equals(deviceInfo.getAssetId())) {
                        deviceList.add(deviceInfo);
                    }
                }
                children.setDeviceInfoBeans(deviceList);
            }
        }
        return familyDetail;
    }
}
