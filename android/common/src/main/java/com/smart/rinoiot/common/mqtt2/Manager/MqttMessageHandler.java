package com.smart.rinoiot.common.mqtt2.Manager;

import android.text.TextUtils;

import com.smart.rinoiot.common.utils.LgUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * mqtt消息转换
 */
class MqttMessageHandler {
    private static final String TAG = "MqttMessageHandler";

    /**
     * App QR login topic postfix
     */
    private static final String APP_QR_LOGIN_TOPIC_POSTFIX = "app_qr_login";

    public synchronized void handlerMsg(String topic, MqttMessage mqttMessage) {
        String payload = new String(mqttMessage.getPayload());
        LgUtils.i(TAG + "mqtt: 接收到的服务器消息 topic=" + topic + " | payload=" + payload);
        if(!TextUtils.isEmpty(topic) && topic.contains(APP_QR_LOGIN_TOPIC_POSTFIX)){
            MqttConvertManager.getInstance().appQrLoginMessageHandler(payload);
        }else{
            MqttConvertManager.getInstance().mqttMsgDealData(payload);
        }
    }
}
