package com.smart.rinoiot.common.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 国家模型
 *
 * @className CountryBean
 * @date: 2020/6/3 10:11 AM
 * @author: xf
 */
public class CountryBean {
    /** 国家名称 */
    @SerializedName(value = "n", alternate = "name")
    private String countryName;
    private String phoneCode;
    /** 国家码 */
    @SerializedName(value = "c", alternate = "code")
    private String countryCode;
    @SerializedName("r")
    private String region;
    private String matchingStr;

    /** 区域id */
    @SerializedName("areaId")
    private String areaId;
    /** 数据中心 Host */
    @SerializedName("domain")
    private String domain;
    /** 语言标识(zh_CN,en_US ....) */
    @SerializedName("langFlag")
    private String langFlag;
    /** 数据中心 Mqtt URL */
    @SerializedName("mqttUrl")
    private String mqttUrl;

    public String getMatchingStr() {
        return matchingStr;
    }

    public void setMatchingStr(String matchingStr) {
        this.matchingStr = matchingStr;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLangFlag() {
        return langFlag;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setLangFlag(String langFlag) {
        this.langFlag = langFlag;
    }

    public String getMqttUrl() {
        return mqttUrl;
    }

    public void setMqttUrl(String mqttUrl) {
        this.mqttUrl = mqttUrl;
    }
}
