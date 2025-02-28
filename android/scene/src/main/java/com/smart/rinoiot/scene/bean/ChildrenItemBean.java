package com.smart.rinoiot.scene.bean;

import java.io.Serializable;

public class ChildrenItemBean implements Serializable {
    /** item类型，条件或者任务 */
    private int beanType;
    /** 条件类型，一键执行、天气变化时、定时、设备状态变化时 */
    private String conditionType;
    /** 配置类型，单选、多选、进度条 */
    private String configType;
    /** 配置名称 */
    private String configName;
    /** 配置 value */
    private String configValue;
    /** 条件的 value1 */
    private String conditionValue1;
    /** 条件的 value2 */
    private String conditionValue2;
    /** 条件的 value3 */
    private String conditionValue3;

    public int getBeanType() {
        return beanType;
    }

    public void setBeanType(int beanType) {
        this.beanType = beanType;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConditionValue1() {
        return conditionValue1;
    }

    public void setConditionValue1(String conditionValue1) {
        this.conditionValue1 = conditionValue1;
    }

    public String getConditionValue2() {
        return conditionValue2;
    }

    public void setConditionValue2(String conditionValue2) {
        this.conditionValue2 = conditionValue2;
    }

    public String getConditionValue3() {
        return conditionValue3;
    }

    public void setConditionValue3(String conditionValue3) {
        this.conditionValue3 = conditionValue3;
    }
}
