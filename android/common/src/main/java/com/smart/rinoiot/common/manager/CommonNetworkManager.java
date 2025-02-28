package com.smart.rinoiot.common.manager;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.api.CommonApiService;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Geolocation;
import com.smart.rinoiot.common.bean.MultiDeviceDpBean;
import com.smart.rinoiot.common.bean.PanelInfo;
import com.smart.rinoiot.common.bean.PanelMultiLangBean;
import com.smart.rinoiot.common.bean.ProductGuideBean;
import com.smart.rinoiot.common.bean.ProductGuideStepBean;
import com.smart.rinoiot.common.crash.OSSTokenBean;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.listener.DownloadFileListener;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.DeviceControlUtils;
import com.smart.rinoiot.common.utils.FileUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.weather.model.RinoWeatherData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author author
 */
public class CommonNetworkManager {
    private static final String TAG = CommonNetworkManager.class.getSimpleName();
    private static CommonNetworkManager instance;

    private CommonNetworkManager() {
    }

    public static CommonNetworkManager getInstance() {
        if (instance == null) {
            instance = new CommonNetworkManager();
        }
        return instance;
    }

    /**
     * 获取产品当前应用的面板
     */
    public void getPanelAsync(String productId, CallbackListener<PanelInfo> callback) {
        AppExecutors.getInstance().networkIO().execute(()->{
            RetrofitUtils.getService().getPanel(productId).enqueue(new BaseRequestListener<PanelInfo>() {
                @Override
                public void onResult(PanelInfo result) {
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
     * 文件下载
     */
    public void downloadFileAsync(String fileUrl, String fileName, DownloadFileListener callback) {
        AppExecutors.getInstance().networkIO().execute(()->{
            RetrofitUtils.getService().downloadFile(fileUrl).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.body() != null && response.code() == 200) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                FileUtils.writeFileFromResponseBody(fileName, response.body(), callback);
                            }
                        }.start();
                    } else {
                        AppExecutors.getInstance().mainThread().execute(()->{
                            if (callback != null) {
                                callback.onError(response.message());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        if (callback != null) {
                            callback.onError(throwable.getMessage());
                        }
                    });
                }
            });
        });

    }

    /**
     * 文件下载
     */
    public void downloadFile(String fileUrl, String fileName, DownloadFileListener callback) {
        RetrofitUtils.getService().downloadFile(fileUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.body() != null && response.code() == 200) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            FileUtils.writeFileFromResponseBody(fileName, response.body(), callback);
                        }
                    }.start();
                } else {
                    if (callback != null) {
                        callback.onError(response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                if (callback != null) {
                    callback.onError(throwable.getMessage());
                }
            }
        });
    }

    /**
     * 获取设备最新物模型信息
     */
    public void getDeviceDpList(String uuid, CallbackListener<List<DeviceDpBean>> callback) {
        RetrofitUtils.getService().getDeviceDpList(uuid).enqueue(new BaseRequestListener<List<DeviceDpBean>>() {
            @Override
            public void onResult(List<DeviceDpBean> result) {
                if (callback != null) {
                    callback.onSuccess(DeviceControlUtils.setDefaultVale(result));
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
     * 根据功能ID与配网类型查询配网引导数据
     */
    public void getByFunctionIdAndNetworkType(String productId, int networkType, CallbackListener<ProductGuideBean> callback) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("productId", productId);
        map.put("networkType", networkType);
        RetrofitUtils.getService().getByFunctionIdAndNetworkType(map).enqueue(new BaseRequestListener<ProductGuideBean>() {
            @Override
            public void onResult(ProductGuideBean result) {
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
     * 根据产品ID和协议查询配网引导数据
     */
    public void getByFunctionIdAndNetworkType(String protocolId, String protocolType, CallbackListener<List<ProductGuideStepBean>> callback) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("protocolId", TextUtils.isEmpty(protocolId)?"0":protocolId);
        map.put("protocolType", protocolType);
        RetrofitUtils.getService().getNetworkGuide(map).enqueue(new BaseRequestListener<List<ProductGuideStepBean>>() {
            @Override
            public void onResult(List<ProductGuideStepBean> result) {
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
     * 根据设备ID获取设备最新物模型信息
     */
    public void getDeviceDataPointsAsync(String deviceId, CallbackListener<List<DeviceDpBean>> callback) {
        AppExecutors.getInstance().networkIO().execute(()->{
            RetrofitUtils.getService(CommonApiService.class).getDpInfos(deviceId).enqueue(new BaseRequestListener<List<DeviceDpBean>>() {
                @Override
                public void onResult(List<DeviceDpBean> deviceDpBeans) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        if (callback != null) {
                            callback.onSuccess(deviceDpBeans);
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
     * 获取产品物模型 蓝牙配网
     */
    public void getModel(String productId, String uuid, CallbackListener<List<Object>> callback) {
        Map<String, Object> map = new HashMap<>(2);
        // 模型结构 (1 =精简，2 =迷你, 0 =完整版)
        map.put("modelFormat", 2);
        map.put("productId", productId);
        RetrofitUtils.getService(CommonApiService.class).getModel(map).enqueue(new BaseRequestListener<List<Object>>() {
            @Override
            public void onResult(List<Object> deviceDpBeans) {
                if (callback != null) {
                    callback.onSuccess(deviceDpBeans);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, Constant.MODE_FAIL + msg);
                }
            }
        });
    }

    /**
     * 获取多设备最新DP点数据
     * @param deviceIds the device identifiers
     */
    public void getMultiDeviceDpInfoAsync(Set<String> deviceIds) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("deviceIds", deviceIds);

        AppExecutors.getInstance().networkIO().execute(()->{
            RetrofitUtils.getService(CommonApiService.class).getMultiDeviceDpInfo(map).enqueue(new BaseRequestListener<MultiDeviceDpBean>() {
                @Override
                public void onResult(MultiDeviceDpBean deviceDpBeans) {
                    if (deviceDpBeans != null && deviceDpBeans.getDpValueMapping() != null && deviceDpBeans.getDpValueMapping().size() > 0) {
                        for (Map.Entry<String, List<DeviceDpBean>> entry : deviceDpBeans.getDpValueMapping().entrySet()) {
                            CacheDataManager.getInstance().saveDeviceDpList(entry.getKey(), entry.getValue());
                            Log.d(TAG, "onResult: device=" + entry.getKey() +" dps=" + new Gson().toJson(entry.getValue()));
                            DataSourceManager.getInstance().updateDeviceStates(entry.getKey(), entry.getValue());
                        }
                    }
                }

                @Override
                public void onError(String error, String msg) {
                }
            });
        });
    }

    /**
     * 获取 OSS token数据
     * @param callbackListener the OSS token callback listener
     */
    public void getOssToken(CallbackListener<OSSTokenBean> callbackListener) {
        RetrofitUtils.getService(CommonApiService.class).getOssToken(2).enqueue(new BaseRequestListener<OSSTokenBean>() {
            @Override
            public void onResult(OSSTokenBean result) {
                if (callbackListener != null) {
                    callbackListener.onSuccess(result);
                }
                LgUtils.w(TAG + "    getOssToken  onResult  result=" + new Gson().toJson(result));
            }

            @Override
            public void onError(String error, String msg) {
                if (callbackListener != null) {
                    callbackListener.onError(error, msg);
                }
                LgUtils.w(TAG + "    getOssToken  onError  error=" + error + "    msg=" + msg);
            }
        });
    }

    /**上传崩溃日志*/
    public void uploadCrashSave(String filePath) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("filePath",filePath);
        map.put("platform", 2);
        RetrofitUtils.getService(CommonApiService.class).uploadCrashSave(map).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                LgUtils.w(TAG + "    uploadCrashSave  onResult  result=" + new Gson().toJson(result));
            }

            @Override
            public void onError(String error, String msg) {
                LgUtils.w(TAG + "    uploadCrashSave  onError  error=" + error + "    msg=" + msg);
            }
        });
    }

    /**
     * 获取产品多语言词条
     */
    public void getPanelLangAsync(String productId, CallbackListener<PanelMultiLangBean> callback) {
        AppExecutors.getInstance().networkIO().execute(()-> {
            RetrofitUtils.getService(CommonApiService.class).getPanelLang(productId).enqueue(new BaseRequestListener<PanelMultiLangBean>() {
                @Override
                public void onResult(PanelMultiLangBean panelMultiLangBean) {
                    AppExecutors.getInstance().mainThread().execute(()->{
                        if (callback != null) {
                            callback.onSuccess(panelMultiLangBean);
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
     * 获取产品多语言词条
     */
    public void getPanelLang(String productId, CallbackListener<PanelMultiLangBean> callback) {
        RetrofitUtils.getService(CommonApiService.class).getPanelLang(productId).enqueue(new BaseRequestListener<PanelMultiLangBean>() {
            @Override
            public void onResult(PanelMultiLangBean panelMultiLangBean) {
                if (callback != null) {
                    callback.onSuccess(panelMultiLangBean);
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
     * Likes and dislikes a device in a room
     * @param deviceId the device identifier
     * @param status weather the device is liked or not
     * @return the request status
     */
    public Observable<Boolean> likeRoomDevice(String deviceId, boolean status){
        return Observable.create(emitter -> {

            CommonApiService service = RetrofitUtils.getService(CommonApiService.class);
            BaseRequestListener<Boolean> listener = new BaseRequestListener<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if(null != result){
                        emitter.onNext(result);
                    }
                    emitter.onComplete();
                }

                @Override
                public void onError(String error, String msg) {
                    Log.e(TAG, "onError: code=" + error + " | message=" +msg);
                    emitter.onError(new Exception(msg));
                }
            };

            if(status) {
                service.likeRoomDevice(Collections.singletonList(deviceId)).enqueue(listener);
            }else{
                service.dislikeRoomDevice(Collections.singletonList(deviceId)).enqueue(listener);
            }
        });
    }

    /**
     * Modifies the device name
     *
     * @param homeId the home identifier
     * @param deviceUuid the device UUID
     * @param deviceName the device name
     * @param callback the callback listener
     */
    public void updateDeviceName(
            String homeId, String deviceUuid, String deviceName, CallbackListener<Objects> callback
    ) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("assetId", homeId);
        params.put("deviceUuid", deviceUuid);
        params.put("deviceName", deviceName);

        BaseRequestListener<Object> listener = new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) {
                    callback.onSuccess((Objects) result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        };

        AppExecutors.getInstance().networkIO().execute(()-> RetrofitUtils.getService(
                CommonApiService.class
        ).updateDeviceName(params).enqueue(listener));
    }

    /**
     * Fetches the list of liked devices. Note that the status is named isHomeScreen
     *
     * @param assetIds asset identifier
     * @return the list with device like status
     */
    public Observable<List<DeviceInfoBean>> fetchFavoriteDevices(Set<String> assetIds){
        Map<String, Object> params = new HashMap<>(1);
        params.put("assetIds", assetIds);

        return Observable.create(emitter -> {
            BaseRequestListener<List<DeviceInfoBean>> listener = new BaseRequestListener<List<DeviceInfoBean>>() {
                @Override
                public void onResult(List<DeviceInfoBean> data) {
                    if(null != data){
                        emitter.onNext(data);
                    } else{
                        emitter.onNext(new ArrayList<>());
                    }
                    emitter.onComplete();
                }

                @Override
                public void onError(String error, String msg) {
                    Log.e(TAG, "onError: code=" + error + " | message=" +msg);
                    emitter.onError(new Exception(msg));
                }
            };

            RetrofitUtils.getService(CommonApiService.class)
                    .fetchFavoriteDevices(params).enqueue(listener);
        });
    }

    /**
     * Fetches the weather data
     * @param geolocation the geolocation data
     * @return observable.
     */
    public Observable<RinoWeatherData> fetchWeatherData(@NonNull Geolocation geolocation) {
        Map<String, Object> location = new HashMap<>(2);
        location.put("lat", geolocation.getLat());
        location.put("lon", geolocation.getLon());

        return Observable.create(emitter -> {
            BaseRequestListener<RinoWeatherData> listener = new BaseRequestListener<RinoWeatherData>() {
                @Override
                public void onResult(RinoWeatherData data) {
                    if(null != data) {
                        emitter.onNext(data);
                        emitter.onComplete();
                    }else{
                        emitter.onError(new Exception("Failed to fetch weather data"));
                    }
                }

                @Override
                public void onError(String error, String msg) {
                    Log.e(TAG, "onError: weather data. code=" + error + " | message=" +msg);
                    emitter.onError(new Exception(msg));
                }
            };
            RetrofitUtils.getService(CommonApiService.class)
                    .fetchWeatherData(location)
                    .enqueue(listener);
        });
    }
}
