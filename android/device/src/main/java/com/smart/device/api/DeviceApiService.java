package com.smart.device.api;

import com.smart.device.bean.OtaUpgradeBean;
import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.network.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DeviceApiService extends ApiService {

    /** 解除资产设备绑定 */
    @POST(value = "business-app/v1/device/unbindFromAsset")
    Call<BaseResponse<Object>> unbindToAsset(@Body Map<String, Object> params);

    /** 检查设备固件是否需要升级*/
    @POST(value = "business-app/v1/ota/checkUpgrade/{deviceId}")
    Call<BaseResponse<OtaUpgradeBean>> checkUpgrade(@Path("deviceId") String deviceId);

    /** 修改设备昵称+绑定到(子资产)房间下*/
    @POST(value = "business-app/v1/device/initDevice")
    Call<BaseResponse<Object>> initDevice(@Body Map<String, Object> params);

    /** 绑定资产设备 */
    @POST(value = "business-app/v1/device/bindToAsset")
    Call<BaseResponse<DeviceInfoBean>> bindToAsset(@Body Map<String, Object> params);
}
