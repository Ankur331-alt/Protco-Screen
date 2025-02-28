package com.smart.rinoiot.common.api;

import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.MultiDeviceDpBean;
import com.smart.rinoiot.common.bean.PanelMultiLangBean;
import com.smart.rinoiot.common.bean.ProductInfoBean;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.crash.OSSTokenBean;
import com.smart.rinoiot.common.network.ApiService;
import com.smart.rinoiot.common.weather.model.RinoWeatherData;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommonApiService extends ApiService {
    /**
     * 根据UUID和PID获取简单设备信息
     */
    @POST(value = "business-app/v1/device/getSimpleDeviceInfo")
    Call<BaseResponse<ProductInfoBean>> getSimpleDeviceInfo(@Body Map<String, Object> params);

    /** 刷新token */
    @FormUrlEncoded
    @POST("auth/oauth/token")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<BaseResponse<UserInfoBean>> refreshToken(@FieldMap Map<String, Object> params);

    /**
     * 根据设备ID获取设备最新物模型信息
     */
    @POST(value = "business-app/v1/device/getDpInfos/{deviceId}")
    Call<BaseResponse<List<DeviceDpBean>>> getDpInfos(@Path("deviceId") String deviceId);

    /**
     * 获取产品物模型 蓝牙配网
     */
    @POST(value = "business-app/v1/product/getModel")
    Call<BaseResponse<List<Object>>> getModel(@Body Map<String, Object> params);

    /**
     * 获取多设备最新DP点数据
     */
    @POST(value = "business-app/v1/device/getMulitDeviceDpInfos")
    Call<BaseResponse<MultiDeviceDpBean>> getMultiDeviceDpInfo(@Body Map<String, Object> params);

    /**
     * 获取临时oss token
     */
    @POST(value = "business-app/v1/file/getOssToken")
    Call<BaseResponse<OSSTokenBean>> getOssToken(@Query(value = "type") int type);

    /**
     * 上传崩溃日志
     */
    @POST(value = "business-app/v1/crashLog/save")
    Call<BaseResponse<Object>> uploadCrashSave(@Body Map<String, Object> params);

    /**
     * 获取产品多语言词条
     */
    @POST(value = "business-app/v1/product/{productId}/getLang")
    Call<BaseResponse<PanelMultiLangBean>> getPanelLang(@Path("productId") String productId);

    /**
     * 绑定Matter设备
     */
    @POST(value = "business-app/v1/device/bind/bindMatter")
    Call<BaseResponse<String>> bindMatter(@Body Map<String, Object> params);

    @POST(value = "business-app/v1/device/matter/matterFind")
    Call<BaseResponse<Boolean>> matterFind(@Body Map<String, Object> params);

    @POST(value = "business-app/v1/device/addDeviceHomeScreen")
    Call<BaseResponse<Boolean>> likeRoomDevice(@Body List<String> deviceIds);

    @POST(value = "business-app/v1/device/removeDeviceHomeScreen")
    Call<BaseResponse<Boolean>> dislikeRoomDevice(@Body List<String> deviceIds);

    /** 修改设备昵称+绑定到(子资产)房间下*/
    @POST(value = "business-app/v1/device/initDevice")
    Call<BaseResponse<Object>> updateDeviceName(@Body Map<String, Object> params);

    /**
     * Fetches the list of liked devices from the server. Watch out for the asset identifier.
     * @param params the query params
     * @return the list of liked devices under the list of provided assets
     */
    @POST(value = "business-app/v1/device/getHomeScreenDeviceList")
    Call<BaseResponse<List<DeviceInfoBean>>> fetchFavoriteDevices(@Body Map<String, Object> params);


    /**
     * Fetches the weather data
     * @param params contains the location's latitude and longitude
     * @return the weather data
     */
    @FormUrlEncoded
    @POST(value = "business-app/v1/weather/getWeatherByPoint")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<BaseResponse<RinoWeatherData>> fetchWeatherData(@FieldMap Map<String, Object> params);

    /**
     * Fetches the weather data
     * @param params contains the location's latitude and longitude
     * @return the weather data
     */
    @FormUrlEncoded
    @POST(value = "business-app/v1/weather/getWeatherByCityName")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<BaseResponse<RinoWeatherData>> fetchCityWeatherData(@FieldMap Map<String, Object> params);
}
