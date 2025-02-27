package com.smart.rinoiot.common.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 国家模型
 *
 * @className TimeZoneBean
 * @date: 2020/6/3 10:11 AM
 * @author: xf
 */
public class TimeZoneBean {
    /** 国家名称 */
    @SerializedName(value = "name")
    private String countryName;
    /** 时区 */
    private String tz;

    private String matchingStr;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getMatchingStr() {
        return matchingStr;
    }

    public void setMatchingStr(String matchingStr) {
        this.matchingStr = matchingStr;
    }
}
