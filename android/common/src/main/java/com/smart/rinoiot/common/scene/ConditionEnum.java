package com.smart.rinoiot.common.scene;

/**
 * @author edwin
 */
public enum ConditionEnum {
    /**
     * 干燥
     */
    HUMIDITY_DRY("干燥", "dry"),
    /**
     * 舒适
     */
    HUMIDITY_COMFORT("舒适", "comfort"),
    /**
     * 潮湿
     */
    HUMIDITY_WET("潮湿", "wet"),
    /**
     * 晴天
     */
    WEATHER_SUNNY("晴天", "sunny"),
    /**
     * 阴天
     */
    WEATHER_CLOUDY("阴天", "cloudy"),
    /**
     * 雨天
     */
    WEATHER_RAINY("雨天", "rainy"),
    /**
     * 雪天
     */
    WEATHER_SNOWY("雪天", "snowy"),
    /**
     * 霾天
     */
    WEATHER_POLLUTED("霾天", "polluted"),
    /**
     * 优
     */
    GOOD("优", "good"),
    /**
     * 良
     */
    FINE("良", "fine"),
    /**
     * 污染
     */
    POLLUTED("污染", "polluted"),
    /**
     * 日出
     */
    SUNSETRISE_SUNRISE("日出", "sunrise"),
    /**
     * 日落
     */
    SUNSETRISE_SUNSET("日落", "sunset");

    private String name;
    private String value;

    ConditionEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static String getConditionEnumName(String value) {
        ConditionEnum[] conditionEnums = values();
        for (ConditionEnum conditionEnum : conditionEnums) {
            if (conditionEnum.getValue().equals(value)) {
                return conditionEnum.getName();
            }
        }
        return null;
    }

    public static String getConditionEnumValue(String name) {
        ConditionEnum[] conditionEnums = values();
        for (ConditionEnum conditionEnum : conditionEnums) {
            if (conditionEnum.getName().equals(name)) {
                return conditionEnum.getValue();
            }
        }
        return null;
    }
}
