package com.smart.device.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * @author tw
 * @time 2022/12/14 14:09
 * @description 常用功能点数据处理
 */
public class StockDpInfoBean implements Serializable {
    private Object value;//默认值
    private String type;//数据类型
    private String name;//名称
    private String devId;//设备id
    private String key;//dp对应的标识
    private Map<String, Object> mapDpJson;//dp点数据，如枚举数据
    private Map<String, Object> mapDpImage;//枚举数据对应的图片


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public Map<String, Object> getMapDpJson() {
        return mapDpJson;
    }

    public void setMapDpJson(Map<String, Object> mapDpJson) {
        this.mapDpJson = mapDpJson;
    }

    public Map<String, Object> getMapDpImage() {
        return mapDpImage;
    }

    public void setMapDpImage(Map<String, Object> mapDpImage) {
        this.mapDpImage = mapDpImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
