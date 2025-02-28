package com.smart.rinoiot.common.mqtt2.Manager;

/**
 * mqtt 2.0  订阅通知
 */
public class TopicManager {
    /**
     * APP消息通知
     * 返回数据格式：
     * {
     * "id":"45lkj3551234001",//消息ID
     * "ts":1626197189,//时间戳（秒）
     * "code":"ota_progress",//事件编码，见设备事件列表
     * "data":{},//数据结构体 见APP消息列表->数据结构定义
     * }
     * 通知类型：
     * ota_progress  设备升级进度通知
     * property_change   设备属性变化通知
     * status  设备状态改变通知
     * bind_result  绑定结果通知
     */
    public static String subscribeAssetIdNotify(String assetId) {
        return String.format("app/v2/%1s/notify", assetId);
    }

    public static String subscribeUserNotifications(String userId) {
        return String.format("app/v2/%1s/userNotify", userId);
    }

    /**
     * APP设置设备属性 wifi配网
     */
    public static String publishDpToDevice(String userId) {
        return String.format("app/%1$s/device/property/set", userId);
    }

    /**
     * 设备主动属性上报 ble配网
     */
    public static String publishCloudDpToDevice(String uuid) {
        return String.format("rlink/%1$s/thing/property/report", uuid);
    }

    /**
     * 设备上线通知
     */
    public static String deviceOnlineNotify(String uuid) {
        return String.format("app/%1$s/device/status/report", uuid);
    }

    /**
     * 通知设备系统事件
     */
    public static String deviceSysEventNotify(String uuid) {
        return String.format("rlink/%1$s/sys/event/trigger", uuid);
    }

    /**
     * OTA数据响应上报订阅主题
     */
    public static String subscribeSysEventTopic(String assetId) {
        return String.format("app/%1$s/device/ota/report", assetId);
    }

    /**
     * App temporary login via QR code
     * @param token token
     * @return subscription topic
     */
    public static String getAppQrLoginTopic(String token) {
        return String.format("temp/v2/%1$s/app_qr_login", token);
    }
}
