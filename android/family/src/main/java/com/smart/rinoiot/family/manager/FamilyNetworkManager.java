package com.smart.rinoiot.family.manager;

import android.text.TextUtils;
import android.util.Log;

import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceGroupBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.GroupBean;
import com.smart.rinoiot.common.bean.InviteMemberBean;
import com.smart.rinoiot.common.bean.SortBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.family.api.FamilyApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * @author author
 */
public class FamilyNetworkManager {

    private static final String TAG = "FamilyNetworkManager";

    private static FamilyNetworkManager instance;

    private FamilyNetworkManager() {
    }

    public static FamilyNetworkManager getInstance() {
        if (instance == null) {
            instance = new FamilyNetworkManager();
        }
        return instance;
    }

    /**
     * 获取家庭列表
     */
    public void getFamilyListAsync(CallbackListener<List<AssetBean>> callback) {
        AppExecutors.getInstance().networkIO().execute(()->{
            RetrofitUtils.getService(FamilyApiService.class).getAssetTree().enqueue(new BaseRequestListener<List<AssetBean>>() {
                @Override
                public void onResult(List<AssetBean> result) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        CacheDataManager.getInstance().saveFamilyList(result);
                        if (callback != null) {
                            callback.onSuccess(result);
                        }
                    });
                }

                @Override
                public void onError(String error, String msg) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        if (callback != null) {
                            callback.onError(error, msg);
                        }
                    });
                }
            });
        });
    }

    public Observable<List<AssetBean>> getFamilyList(){
        return Observable.create(emitter -> {
            BaseRequestListener<List<AssetBean>> callback = new BaseRequestListener<List<AssetBean>>() {
                @Override
                public void onResult(List<AssetBean> result) {
                    if(null != result){
                        CacheDataManager.getInstance().saveFamilyList(result);
                        emitter.onNext(result);
                    }else{
                        emitter.onNext(new ArrayList<>());
                    }
                    emitter.onComplete();
                }

                @Override
                public void onError(String error, String msg) {
                    Log.e(TAG, "onError: error=" + error  +  " | msg=" + msg);
                    emitter.onError(new Exception(msg));
                }
            };
            RetrofitUtils.getService(FamilyApiService.class).getAssetTree().enqueue(callback);
        });
    }

    /**
     * 获取家庭列表
     */
    public void getFamilyList(CallbackListener<List<AssetBean>> callback) {
        RetrofitUtils.getService(FamilyApiService.class).getAssetTree().enqueue(new BaseRequestListener<List<AssetBean>>() {
            @Override
            public void onResult(List<AssetBean> result) {
                CacheDataManager.getInstance().saveFamilyList(result);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 发现设备
     */
    public void discoverDevice(CallbackListener<Object> callback) {
        RetrofitUtils.getService(FamilyApiService.class).discoverDevice().enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }


    /**
     * Fetches the family details asynchronously
     *
     * @param assetId asset identifier
     * @param callback callback
     */
    public void getFamilyDetailsAsync(String assetId, CallbackListener<AssetBean> callback) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            RetrofitUtils.getService(FamilyApiService.class).getAssetDetail(assetId).enqueue(new BaseRequestListener<AssetBean>() {
                @Override
                public void onResult(AssetBean result) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        if (callback != null) {
                            callback.onSuccess(result);
                        }
                    });
                }

                @Override
                public void onError(String error, String msg) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        if (callback != null) {
                            callback.onError(error, msg);
                        }
                    });
                }
            });
        });
    }

    /**
     * 获取家庭详情
     */
    public void getFamilyDetail(String assetId, CallbackListener<AssetBean> callback) {
        RetrofitUtils.getService(FamilyApiService.class).getAssetDetail(assetId).enqueue(new BaseRequestListener<AssetBean>() {
            @Override
            public void onResult(AssetBean result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 创建默认家庭
     */
    public void createDefaultFamily(String name, CallbackListener<String> callback) {
        createAsset(name, null, -1, -1, null, callback);
    }

    /**
     * 创建家庭
     */
    public void createFamily(String name, String address, double lat, double lng, CallbackListener<String> callback) {
        createAsset(name, address, lat, lng, null, callback);
    }

    /**
     * 创建房间
     */
    public void createRoom(String name, String parentId, CallbackListener<String> callback) {
        createAsset(name, null, -1, -1, parentId, callback);
    }

    /**
     * 创建资产
     *
     * @param name     家庭名称或者房间名称
     * @param address  家庭地址
     * @param lat      经度
     * @param lng      纬度
     * @param parentId 家庭id（创建家庭时，不需要传，创建房间时，需要）
     * @param callback 回调
     */
    private void createAsset(String name, String address, double lat, double lng, String parentId, CallbackListener<String> callback) {
        Map<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(address)) {
            map.put("address", address);
        }
        if (lat != -1) {
            map.put("lat", lat);
        }
        if (lng != -1) {
            map.put("lng", lng);
        }
        map.put("name", name);
        if (!TextUtils.isEmpty(parentId)) {
            map.put("parentId", parentId);
        }
        RetrofitUtils.getService(FamilyApiService.class).createAsset(map).enqueue(new BaseRequestListener<String>() {
            @Override
            public void onResult(String result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 选中家庭
     */
    public void selectedFamily(String familyId, CallbackListener<String> callback) {
        CacheDataManager.getInstance().setCurrentHomeId(familyId);
        RetrofitUtils.getService(FamilyApiService.class).selectedAsset(familyId).enqueue(new BaseRequestListener<String>() {
            @Override
            public void onResult(String result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 获取设备列表
     */
    public void getDeviceList(List<String> assetIds, CallbackListener<List<DeviceInfoBean>> callback) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("assetIds", assetIds);
        RetrofitUtils.getService(FamilyApiService.class).getDeviceList(map).enqueue(new BaseRequestListener<List<DeviceInfoBean>>() {
            @Override
            public void onResult(List<DeviceInfoBean> result) {
                CacheDataManager.getInstance().saveAllDeviceList(assetIds.get(0), result);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 获取被邀请记录
     */
    public void getInvitedList(CallbackListener<List<InviteMemberBean>> callback) {
        RetrofitUtils.getService(FamilyApiService.class).getInvitedList().enqueue(new BaseRequestListener<List<InviteMemberBean>>() {
            @Override
            public void onResult(List<InviteMemberBean> result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 接收或拒绝邀请
     */
    public void acceptInvited(HashMap<String, Object> map, CallbackListener<Boolean> callback) {
        RetrofitUtils.getService(FamilyApiService.class).acceptInvite(map).enqueue(new BaseRequestListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }


    /**
     * 删除家庭
     */
    public void deleteFamily(String assetId, CallbackListener<Boolean> callback) {
        RetrofitUtils.getService(FamilyApiService.class).deleteFamily(assetId).enqueue(new BaseRequestListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 排序
     */
    public void sortRoom(List<SortBean> sortBeans, CallbackListener<Boolean> callback) {
        RetrofitUtils.getService(FamilyApiService.class).sortRoom(sortBeans).enqueue(new BaseRequestListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 修改资产信息
     */
    public void updateFamilyInfo(HashMap<String, Object> map, CallbackListener<Object> callback) {
        RetrofitUtils.getService(FamilyApiService.class).updateFamilyInfo(map).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (result == null) {
                    if (callback != null) {
                        callback.onSuccess(true);
                    }
                    return;
                }
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 批量修改设备资产
     */
    public void batchChangeDeviceAsset(Map<String, Object> map, CallbackListener<Object> callback) {
        RetrofitUtils.getService(FamilyApiService.class).batchChangeDeviceAsset(map).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 获取首页设列表(群组、设备)
     *
     * @param assetIds the asset identifiers
     * @param callback the callback
     */
    public void getHomeDeviceListAsync(List<String> assetIds, CallbackListener<List<DeviceInfoBean>> callback) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("assetIds", assetIds);

        AppExecutors.getInstance().networkIO().execute(()->{
            RetrofitUtils.getService(FamilyApiService.class).getHomeDeviceList(map).enqueue(new BaseRequestListener<DeviceGroupBean>() {
                @Override
                public void onResult(DeviceGroupBean result) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        if (result != null) {
                            CacheDataManager.getInstance().saveAllDeviceList(assetIds.get(0), getHomeAllDeviceAndGroupData(result));
                            if (result.getGroupList() != null && !result.getGroupList().isEmpty()) {
                                CacheDataManager.getInstance().saveAllGroupDeviceList(assetIds.get(0), result.getGroupList());
                            }
                            if (callback != null) {
                                callback.onSuccess(getHomeAllDeviceAndGroupData(result));
                            }
                        }
                    });
                }

                @Override
                public void onError(String error, String msg) {
                    AppExecutors.getInstance().mainThread().execute(() ->{
                        ToastUtil.showMsg(msg);
                        if (callback != null) {
                            callback.onError(error, msg);
                        }
                    });
                }
            });
        });
    }


    /**
     * 获取首页设列表(群组、设备)
     */
    public void getHomeDeviceList(List<String> assetIds, CallbackListener<List<DeviceInfoBean>> callback) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("assetIds", assetIds);
        RetrofitUtils.getService(FamilyApiService.class).getHomeDeviceList(map).enqueue(new BaseRequestListener<DeviceGroupBean>() {
            @Override
            public void onResult(DeviceGroupBean result) {
                if (result != null) {
                    CacheDataManager.getInstance().saveAllDeviceList(assetIds.get(0), getHomeAllDeviceAndGroupData(result));
                    if (result.getGroupList() != null && !result.getGroupList().isEmpty()) {
                        CacheDataManager.getInstance().saveAllGroupDeviceList(assetIds.get(0), result.getGroupList());
                    }
                    if (callback != null) {
                        callback.onSuccess(getHomeAllDeviceAndGroupData(result));
                    }
                }
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showMsg(msg);
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * Retrieves the home's device and group data.
     *
     * @param deviceGroupBean the device group bean.
     * @return the list of devices.
     */
    public List<DeviceInfoBean> getHomeAllDeviceAndGroupData(DeviceGroupBean deviceGroupBean) {
        List<DeviceInfoBean> allDeviceList = new ArrayList<>();
        if (deviceGroupBean == null) {
            return allDeviceList;
        }
        List<GroupBean> groupList = deviceGroupBean.getGroupList();
        if (groupList != null && !groupList.isEmpty()) {
            //群组设备
            for (GroupBean groupBean : groupList) {
                allDeviceList.add(CacheDataManager.getInstance().getGroupDeviceSingleData(groupBean, deviceGroupBean.getDeviceList()));
            }
        }
        if (deviceGroupBean.getDeviceList() != null && !deviceGroupBean.getDeviceList().isEmpty()) {
            //正常设备
            /// for (DeviceInfoBean infoBean : deviceGroupBean.getDeviceList()) {
            ///     if (TextUtils.equals(infoBean.getId(),"1590293942631922752")) {//测试边缘网关
            ///         infoBean.setBrokerHost("192.168.31.3:1883");
            ///         break;
            ///     }
            /// }
            allDeviceList.addAll(deviceGroupBean.getDeviceList());
        }
        //根据创建时间排序
        try {
            allDeviceList.sort((firstDevice, secondDevice) -> Long.compare(secondDevice.getCreateTime(), firstDevice.getCreateTime()));
        }catch (Exception exception) {
            Log.e(TAG, "Failed to sort. Cause=" + exception.getLocalizedMessage());
        }
        return allDeviceList;
    }


    /**
     * 批量修改设备资产+设备id+群组id
     */
    public void batchChangeAssetAffiliation(Map<String, Object> map, CallbackListener<Object> callback) {
        RetrofitUtils.getService(FamilyApiService.class).batchChangeAssetAffiliation(map).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }
}
