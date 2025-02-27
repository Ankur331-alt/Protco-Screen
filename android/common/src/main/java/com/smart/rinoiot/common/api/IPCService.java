package com.smart.rinoiot.common.api;

import com.smart.rinoiot.common.bean.AgoraUserTokenVO;
import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.network.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface IPCService extends ApiService {

    /**
     * 通知设备加入频道
     */
    @POST(value = "business-app/v1/device/command/notifyDeviceJoin")
    Call<BaseResponse<Object>> notifyDeviceJoin(@Body Map<String, Object> params);


    /**
     * 根据设备id获取声网Token
     */
    @POST(value = "business-app/v1/agora/getAgoraToken")
    Call<BaseResponse<AgoraUserTokenVO>> getAgoraToken(@QueryMap Map<String, Object> params);

    @POST(value = "/api/business-app/v1/device/command/commonCmd")
    Call<BaseResponse<Object>> commonCmd(@Body Map<String, Object> params);
}
