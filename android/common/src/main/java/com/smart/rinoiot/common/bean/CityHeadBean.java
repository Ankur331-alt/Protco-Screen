package com.smart.rinoiot.common.bean;

import com.chad.library.adapter.base.entity.SectionEntity;

public class CityHeadBean implements SectionEntity {
    private CityBean cityBean;
    private boolean isHeader = false;
    private String headerStr;

    public CityHeadBean(boolean isHeader, String headerStr) {
        this.headerStr = headerStr;
        this.isHeader = isHeader;
    }

    public CityHeadBean(CityBean cityBean) {
        this.cityBean = cityBean;
    }

    public void setCityBean(CityBean cityBean) {
        this.cityBean = cityBean;
    }

    public CityBean getCityBean() {
        return this.cityBean;
    }

    public boolean isHeader() {
        return this.isHeader;
    }

    public String getHeaderStr() {
        return this.headerStr;
    }

    public int getItemType() {
        return this.isHeader ? -99 : -100;
    }
}
