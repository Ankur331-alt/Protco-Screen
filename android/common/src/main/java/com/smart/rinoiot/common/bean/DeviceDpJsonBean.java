package com.smart.rinoiot.common.bean;

import java.io.Serializable;
import java.util.Map;

public class DeviceDpJsonBean implements Serializable {
    /** 功能key */
    private String identifier;
    /** 属性名称 */
    private String name;
    /** 可上报下发 */
    private String accessMode;
    /** 属性描述 */
    private String desc;
    /** 功能值 */
    private DataType dataType;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public static class DataType {
        /** 类型 */
        private String type;
        /** 值 */
        private Map<String, Object> specs;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Map<String, Object> getSpecs() {
            return specs;
        }

        public void setSpecs(Map<String, Object> specs) {
            this.specs = specs;
        }
    }
}
