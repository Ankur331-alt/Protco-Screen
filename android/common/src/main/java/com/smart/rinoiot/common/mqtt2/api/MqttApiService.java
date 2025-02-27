package com.smart.rinoiot.common.mqtt2.api;

import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.network.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MqttApiService extends ApiService {
    /**
     * 设置设备属性
     */
    @POST(value = "business-app/v1/device/command/propsIssue")
    Call<BaseResponse<Object>> mqttPropsIssue(@Body Map<String, Object> params);

    /**
     * 批量设置设备属性
     */
    @POST(value = "business-app/v1/device/command/batchPropsIssue")
    Call<BaseResponse<Object>> mqttBatchPropsIssue(@Body Map<String, Object> params);

    /**
     * 设备属性上报
     */
    @POST(value = "business-app/v1/device/command/propsReport")
    Call<BaseResponse<Object>> mqttPropsReport(@Body Map<String, Object> params);

    /**
     * 固件升级
     */
    @POST(value = "business-app/v1/device/command/otaIssue")
    Call<BaseResponse<Object>> mqttOtaIssue(@Body Map<String, Object> params);

    /**
     * 群组控制
     */
    @POST(value = "business-app/v1/device/command/groupPropsIssue")
    Call<BaseResponse<Object>> mqttGroupPropsIssue(@Body Map<String, Object> params);

    /**
     * 开始/停止搜索并绑定设备
     */
    @POST(value = "business-app/v1/device/command/scanIssue")
    Call<BaseResponse<Object>> mqttScanIssue(@Body Map<String, Object> params);

    /**
     * 一键执行
     */
    @POST(value = "business-app/v1/device/command/onekeyExecuteIssue")
    Call<BaseResponse<Object>> mqttOneKeyExecuteIssue(@Body Map<String, Object> params);

    /**
     * 指定添加子设备
     */
    @POST(value = "business-app/v1/device/command/subAdd")
    Call<BaseResponse<Object>> mqttSubAdd(@Body Map<String, Object> params);
}
