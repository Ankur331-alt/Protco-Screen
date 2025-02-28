package com.smart.rinoiot.user.bean;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.smart.rinoiot.common.bean.CountryBean;

public class CountryHeadBean implements SectionEntity {
    private CountryBean countryBean;
    private boolean isHeader = false;
    private String headerStr;

    public CountryHeadBean(boolean isHeader, String headerStr) {
        this.headerStr = headerStr;
        this.isHeader = isHeader;
    }

    public String getHeaderStr() {
        return this.headerStr;
    }

    public void setCountryBean(CountryBean countryBean) {
        this.countryBean = countryBean;
    }

    public CountryHeadBean(CountryBean countryBean) {
        this.countryBean = countryBean;
    }

    public CountryBean getCountryBean() {
        return this.countryBean;
    }

    public boolean isHeader() {
        return this.isHeader;
    }

    public int getItemType() {
        return this.isHeader ? -99 : -100;
    }
}
