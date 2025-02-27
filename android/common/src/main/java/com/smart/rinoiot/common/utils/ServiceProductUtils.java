package com.smart.rinoiot.common.utils;

import android.text.TextUtils;

import com.smart.rinoiot.common.BuildConfig;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.network.RetrofitUtils;

/**
 * @author tw
 * @time 2022/10/15 16:42
 * @description 接口地址配置
 */
public class ServiceProductUtils {
    private static final String TAG = ServiceProductUtils.class.getSimpleName();
    private static ServiceProductUtils instance;

    public static ServiceProductUtils getInstance() {
        if (instance == null) {
            instance = new ServiceProductUtils();
        }
        return instance;
    }

    public void setServiceProductData(String hostUrl, String mqttUrl) {
        LgUtils.w(TAG + "   hostUrl=" + hostUrl + "   mqttUrl=" + mqttUrl);
        boolean isEmptyHost = TextUtils.isEmpty(hostUrl);
        boolean isEmptyMqtt = TextUtils.isEmpty(mqttUrl);
        String mqttId = "";
        int port = 1883;
        if (!isEmptyMqtt && mqttUrl.contains(":")) {
            String[] split = mqttUrl.split(":");
            mqttId = split[0];
            port = Integer.parseInt(split[1]);
        }
        Constant.MQTT_HOST_PROT = port;

        if (BuildConfig.DEBUG) {
            Constant.API_HOSTNAME = "test.rinoiot.com";
            Constant.AUTHORIZATION_ID = "rinoiot-iot-app";
            Constant.AUTHORIZATION_PASSWORD = "1234567";
            Constant.AES_APPID = "bBcDt1WghhBgWF3M";
            Constant.BASE_URL = (isEmptyHost ? "https://test.rinoiot.com" : hostUrl) + "/api/";
            Constant.MQTT_HOST_URL = isEmptyMqtt ? "testmqtt.rinoiot.com" : mqttId;
            Constant.WEB_SOCKET_HOST_URL = "test.rinoiot.com";
            Constant.BASE_URL_H5 = "https://test.rinoiot.com/";
        } else {//只需要正式环境处理
            Constant.API_HOSTNAME = "service.rinoiot.com";
            Constant.AUTHORIZATION_ID = "rinoiot-iot-app";
            Constant.AUTHORIZATION_PASSWORD = "SLGH6SBEuPRduskN45oJbzOoUPHJgZ7i";
            Constant.AES_APPID = "6e9QcQlsUtUPx1mG";
            Constant.BASE_URL = (isEmptyHost ? "https://service.rinoiot.com" : hostUrl) + "/api/";
            Constant.MQTT_HOST_URL = isEmptyMqtt ? "mqtt.rinoiot.com" : mqttId;
            Constant.BASE_URL_H5 = "https://iot.rinoiot.com/";
            Constant.WEB_SOCKET_HOST_URL = "iot.rinoiot.com";
        }

        Constant.API_HOSTNAME = "service.rinoiot.com";
        Constant.AUTHORIZATION_ID = "rinoiot-iot-app";
        Constant.AUTHORIZATION_PASSWORD = "SLGH6SBEuPRduskN45oJbzOoUPHJgZ7i";
        Constant.AES_APPID = "6e9QcQlsUtUPx1mG";
        Constant.BASE_URL = (isEmptyHost ? "https://service.rinoiot.com" : hostUrl) + "/api/";
        Constant.MQTT_HOST_URL = isEmptyMqtt ? "mqtt.rinoiot.com" : mqttId;
        Constant.BASE_URL_H5 = "https://iot.rinoiot.com/";
        Constant.WEB_SOCKET_HOST_URL = "iot.rinoiot.com";

        Constant.MQTT_HOST = "tcp://" + Constant.MQTT_HOST_URL + ":" + Constant.MQTT_HOST_PROT;
        RetrofitUtils.retrofit = null;
    }

    /**
     * 获取接口地址、MQTT、端口
     */
    public void init() {
        setServiceProductData("", "");
    }
}
