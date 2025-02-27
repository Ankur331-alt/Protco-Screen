package com.smart.rinoiot.family.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.SortBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.api.FamilyApiService;
import com.smart.rinoiot.family.listener.OnRemoveFamilyListener;
import com.smart.rinoiot.family.manager.FamilyDataChangeManager;
import com.smart.rinoiot.family.manager.FamilyNetworkManager;
import com.smart.rinoiot.family.manager.HomeDataManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author author
 */
public class FamilyInfoViewModel extends BaseViewModel {

    /**
     * 是否重新排序
     *
     */
    public MutableLiveData<Boolean> isSort = new MutableLiveData<>();

    /**
     * 是否发送邀请
     */
    private final MutableLiveData<Boolean> isSendInvite = new MutableLiveData<>();

    /**
     * 家庭详情
     */
    private final MutableLiveData<AssetBean> detailLiveData = new MutableLiveData<>();

    /**
     * 家庭设备列表
     */
    private final MutableLiveData<List<DeviceInfoBean>> deviceListLiveData = new MutableLiveData<>();

    public FamilyInfoViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> getIsSort() {
        return isSort;
    }

    public MutableLiveData<Boolean> getIsSendInvite() {
        return isSendInvite;
    }

    public MutableLiveData<List<DeviceInfoBean>> getDeviceListLiveData() {
        return deviceListLiveData;
    }

    public MutableLiveData<AssetBean> getDetailLiveData() {
        return detailLiveData;
    }

    /**
     * 获取家庭详情
     * @param assetId asset identifiers
     */
    public void getFamilyDetail(String assetId) {
        RetrofitUtils.getService(FamilyApiService.class).getAssetDetail(assetId).enqueue(new BaseRequestListener<AssetBean>() {
            @Override
            public void onResult(AssetBean result) {
                detailLiveData.setValue(result);
                getAllDeviceList(result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showErrorMsg(msg);
            }
        });
    }

    /**
     * 邀请成员
     */
    public void inviteMember(String account, String assetId) {
        HashMap<String, Object> hashMap = new HashMap<>(2);
        hashMap.put("account", account);
        hashMap.put("assetId", assetId);
        RetrofitUtils.getService(FamilyApiService.class).inviteMember(hashMap).enqueue(new BaseRequestListener<String>() {
            @Override
            public void onResult(String result) {
                ToastUtil.showMsg(getString(R.string.rino_family_invitation_sent));
                isSendInvite.setValue(true);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showErrorMsg(msg);
            }
        });
    }

    /**
     * 删除指定家庭
     *
     * @param assetId asset identifier
     */
    public void deleteFamily(String assetId, OnRemoveFamilyListener onRemoveFamilyListener) {
        //判断成员等级 如果不为创建者 走退出家庭接口 如果为创建者 走删除家庭接口
        if (HomeDataManager.getInstance().getRole() != 1) {
            BaseRequestListener<Boolean> callback = new BaseRequestListener<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (onRemoveFamilyListener != null) {
                        onRemoveFamilyListener.OnRemove();
                    }
                }

                @Override
                public void onError(String error, String msg) {
                    ToastUtil.showErrorMsg(msg);
                }
            };
            RetrofitUtils.getService(FamilyApiService.class).exitFamily(assetId).enqueue(callback);
        } else {
            FamilyNetworkManager.getInstance().deleteFamily(assetId, new CallbackListener<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    onRemoveFamilyListener.OnRemove();
                }

                @Override
                public void onError(String code, String error) {
                    ToastUtil.showErrorMsg(error);
                }
            });
        }
    }

    /**
     * 刷新名称
     */
    public void updateFamilyName(String name) {
        HashMap<String, Object> objectHashMap = new HashMap<>(5);
        objectHashMap.put("name", name);
        AssetBean assetBean = HomeDataManager.getInstance().getAssetBean();
        objectHashMap.put("address", assetBean.getAddress());
        objectHashMap.put("lat", assetBean.getLat());
        objectHashMap.put("lng", assetBean.getLng());
        objectHashMap.put("assetId", assetBean.getId());
        updateFamilyInfo(objectHashMap);
    }

    /**
     * 刷新地址
     */
    public void updateFamilyAddress(String address, double lat, double lng) {
        HashMap<String, Object> objectHashMap = new HashMap<>(5);
        AssetBean assetBean = HomeDataManager.getInstance().getAssetBean();
        objectHashMap.put("name", assetBean.getName());
        objectHashMap.put("address", address);
        objectHashMap.put("lat", lat);
        objectHashMap.put("lng", lng);
        objectHashMap.put("assetId", assetBean.getId());
        updateFamilyInfo(objectHashMap);
    }


    /**
     * 更新家庭信息
     */
    public void updateFamilyInfo(HashMap<String, Object> map) {
        FamilyNetworkManager.getInstance().updateFamilyInfo(map, new CallbackListener<Object>() {

            @Override
            public void onSuccess(Object data) {
                ToastUtil.showMsg(getString(R.string.rino_family_update_info));
                FamilyDataChangeManager.getInstance().changeViewDataSuccess();
            }

            @Override
            public void onError(String code, String error) {

            }
        });
    }

    public void getAllDeviceList(AssetBean assetBean) {
        List<String> assetIdArray = getAssetIdArray(assetBean);
        FamilyNetworkManager.getInstance().getHomeDeviceList(assetIdArray, new CallbackListener<List<DeviceInfoBean>>() {
            @Override
            public void onSuccess(List<DeviceInfoBean> data) {
                deviceListLiveData.postValue(data);
            }

            @Override
            public void onError(String code, String error) {
                deviceListLiveData.postValue(new ArrayList<>());
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
}
