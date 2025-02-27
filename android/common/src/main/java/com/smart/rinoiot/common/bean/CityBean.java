package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * 城市模型
 *
 * @className CountryBean
 * @date: 2020/6/3 10:11 AM
 * @author: xf
 */
public class CityBean implements Serializable {
    /** 标准城市代码 */
    private String cityCode;
    /** 拼音 */
    private String cityNamePy;
    /** 拼音简写 */
    private String cityNamePyj;
    /** 城市类型（1景点、0城市） */
    private String cityType;
    /** 国家名称 */
    private String countryName;
    /** 墨迹城市ID */
    private long id;
    /** 纬度 */
    private String lat;
    /** 经度 */
    private String lon;
    /** 城市名称 */
    private String name;
    /** 上级名称 */
    private String parentName;
    /** 省名称 */
    private String province;
    private String matchingStr;

    public String getMatchingStr() {
        return matchingStr;
    }

    public void setMatchingStr(String matchingStr) {
        this.matchingStr = matchingStr;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityNamePy() {
        return cityNamePy;
    }

    public void setCityNamePy(String cityNamePy) {
        this.cityNamePy = cityNamePy;
    }

    public String getCityNamePyj() {
        return cityNamePyj;
    }

    public void setCityNamePyj(String cityNamePyj) {
        this.cityNamePyj = cityNamePyj;
    }

    public String getCityType() {
        return cityType;
    }

    public void setCityType(String cityType) {
        this.cityType = cityType;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
