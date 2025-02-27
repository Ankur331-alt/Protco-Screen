package com.smart.rinoiot.common.mqtt2;

/**
 * @author tw
 * @time 2023/4/10 20:07
 * @description mqtt 常量
 */
public class MqttConstant {

    /**
     * 设备升级进度通知
     */
    public static final String MQTT_OTA_PROGRESS = "ota_progress";

    /**
     * 设备属性变化通知
     */
    public static final String MQTT_PROPERTY_CHANGE = "property_change";

    /**
     * 设备状态改变通知
     */
    public static final String MQTT_DEVICE_STATUS = "status";

    /**
     * 绑定结果通知
     */
    public static final String MQTT_BIND_RESULT = "bind_result";

    /**
     * Matter pair code
     */
    public static final String MQTT_MATTER_PAIR = "matter_pair";

    public static class Property {

        public static final String CODE_KEY = "code";

        public static final String DEVICES_KEY = "devices";

        public static final String GATEWAY_ID_KEY = "gatewayId";

        public static final String PAGE_KEY = "page";

        public static final String TYPE_KEY = "type";

        public static final String UUID_KEY = "uuid";

        public static final String RESULT_KEY = "result";

        public static final String PROPERTIES_KEY = "properties";

        public static final String DEVICE_ID_KEY = "deviceId";

        public static final String TIMESTAMP_KEY = "ts";

        public static final String VALUE_KEY = "value";

        public static final String BIND_KEY = "bind";

        public static final String HEADER_KEY = "header";

        public  static final String STATUS_KEY = "status";

        public static final String IS_ONLINE_KEY ="isOnline";

        public static final String ONLINE_KEY = "online";

        public static final String TRIGGER_KEY = "trigger";

        public static final String DATA_KEY = "data";

        public static final String EVENT_TYPE_KEY = "event_type";

        public static final String APP_EVENT_KEY = "app_event";

        public static final String SECOND_KEY = "second";

        public static final String FIND_KEY = "find";

        public static final String GROUP_ID_KEY = "groupId";
    }
}
