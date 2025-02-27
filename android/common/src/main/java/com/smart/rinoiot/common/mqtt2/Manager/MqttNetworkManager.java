package com.smart.rinoiot.common.mqtt2.Manager;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.mqtt2.api.MqttApiService;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class MqttNetworkManager {

    public static final String TAG = MqttNetworkManager.class.getSimpleName();
    private static MqttNetworkManager instance;

    public static MqttNetworkManager getInstance() {
        if (instance == null) {
            instance = new MqttNetworkManager();
        }
        return instance;
    }

    /**
     * 设置设备属性快捷开关  单设备+群组设备
     */
    public void mqttPropsIssueSwitchSingleAndGroup(DeviceInfoBean deviceInfoBean, boolean isOpen) {
        Map<String, Object> params = new HashMap<>();
        if (deviceInfoBean != null) {
            JsonObject switchPublishJson = MqttConvertManager.getInstance().sendSwitchPublish(deviceInfoBean, isOpen);
            String deviceId = TextUtils.isEmpty(deviceInfoBean.getGroupId()) ? deviceInfoBean.getId() : deviceInfoBean.getGroupId();
            params.put("data", switchPublishJson);
            //单设备
            if (TextUtils.isEmpty(deviceInfoBean.getGroupId())) {
                params.put("deviceId", deviceId);
                mqttPropsIssue(params, null);
            } else {
                params.put("groupId", deviceId);
                mqttGroupPropsIssue(params, null);
            }
        }
    }

    /**
     * 设置设备属性
     */
    public void mqttPropsIssue(Map<String, Object> params, CallbackListener<Boolean> callbackListener) {
        Log.d(TAG, "mqttPropsIssue: params = " + new Gson().toJson(params));
        RetrofitUtils.getService(MqttApiService.class).mqttPropsIssue(params).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callbackListener != null) {
                    callbackListener.onSuccess((Boolean) result);
                }
                LgUtils.w(TAG + "   mqttPropsIssue   onResult  result=" + result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showMsg(msg);
                if (callbackListener != null) {
                    callbackListener.onError(error, msg);
                }
                LgUtils.w(TAG + "   mqttPropsIssue   onError  error=" + error + "   msg=" + msg);
            }
        });
    }

    /**
     * 批量设置设备属性
     */
    public void mqttBatchPropsIssue(Map<String, Object> params) {
        RetrofitUtils.getService(MqttApiService.class).mqttBatchPropsIssue(params).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                LgUtils.w(TAG + "   mqttBatchPropsIssue   onResult  result=" + result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showMsg(msg);
                LgUtils.w(TAG + "   mqttBatchPropsIssue   onError  error=" + error + "   msg=" + msg);
            }
        });
    }

    /**
     * 设备属性上报
     */
    public void mqttPropsReport(Map<String, Object> params) {
        RetrofitUtils.getService(MqttApiService.class).mqttPropsReport(params).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                LgUtils.w(TAG + "   mqttPropsReport   onResult  result=" + result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showMsg(msg);
                LgUtils.w(TAG + "   mqttPropsReport   onError  error=" + error + "   msg=" + msg);
            }
        });
    }

    /**
     * 固件升级
     */
    public void mqttOtaIssue(Map<String, Object> params) {
        RetrofitUtils.getService(MqttApiService.class).mqttOtaIssue(params).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                LgUtils.w(TAG + "   mqttOtaIssue   onResult  result=" + result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showMsg(msg);
                LgUtils.w(TAG + "   mqttOtaIssue   onError  error=" + error + "   msg=" + msg);
            }
        });
    }

    /**
     * 群组控制
     */
    public void mqttGroupPropsIssue(Map<String, Object> params, CallbackListener<Boolean> callbackListener) {
        RetrofitUtils.getService(MqttApiService.class).mqttGroupPropsIssue(params).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callbackListener != null) {
                    callbackListener.onSuccess((Boolean) result);
                }
                LgUtils.w(TAG + "   mqttGroupPropsIssue   onResult  result=" + result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callbackListener != null) {
                    callbackListener.onError(error, msg);
                }
                ToastUtil.showMsg(msg);
                LgUtils.w(TAG + "   mqttGroupPropsIssue   onError  error=" + error + "   msg=" + msg);
            }
        });
    }

    /**
     * 开始/停止搜索并绑定设备  网关配多个网关子设备时
     */
    public void mqttScanIssue(Map<String, Object> params,CallbackListener<Boolean> callbackListener) {
        RetrofitUtils.getService(MqttApiService.class).mqttScanIssue(params).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callbackListener!=null) {
                    callbackListener.onSuccess((Boolean) result);
                }
                LgUtils.w(TAG + "   mqttScanIssue   onResult  result=" + result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showMsg(msg);
                if (callbackListener!=null) {
                    callbackListener.onError(error,error);
                }
                LgUtils.w(TAG + "   mqttScanIssue   onError  error=" + error + "   msg=" + msg);
            }
        });
    }

    /**
     * 开始/停止搜索并绑定设备  网关配单个网关子设备时
     */
    public void mqttSubAdd(Map<String, Object> params,CallbackListener<Boolean> callbackListener) {
        RetrofitUtils.getService(MqttApiService.class).mqttSubAdd(params).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callbackListener!=null) {
                    callbackListener.onSuccess((Boolean) result);
                }
                LgUtils.w(TAG + "   mqttSubAdd   onResult  result=" + result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showMsg(msg);
                if (callbackListener!=null) {
                    callbackListener.onError(error,error);
                }
                LgUtils.w(TAG + "   mqttSubAdd   onError  error=" + error + "   msg=" + msg);
            }
        });
    }

    /**
     * 一键执行
     */
    public void mqttOneKeyExecuteIssue(Map<String, Object> params) {
        RetrofitUtils.getService(MqttApiService.class).mqttOneKeyExecuteIssue(params).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                LgUtils.w(TAG + "   mqttOneKeyExecuteIssue   onResult  result=" + result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showMsg(msg);
                LgUtils.w(TAG + "   mqttOneKeyExecuteIssue   onError  error=" + error + "   msg=" + msg);
            }
        });
    }
}
