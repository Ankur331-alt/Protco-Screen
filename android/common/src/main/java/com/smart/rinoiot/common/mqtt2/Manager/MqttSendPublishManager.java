package com.smart.rinoiot.common.mqtt2.Manager;

/**
 * @author tw
 * @time 2023/4/11 11:46
 * @description mqtt数据下发管理类
 */
public class MqttSendPublishManager {

    public static final String TAG = MqttSendPublishManager.class.getSimpleName();
    private static MqttSendPublishManager instance;

    public static MqttSendPublishManager getInstance() {
        if (instance == null) {
            instance = new MqttSendPublishManager();
        }
        return instance;
    }
}
