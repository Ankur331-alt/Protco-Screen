package com.smart.rinoiot.user.bean;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.smart.rinoiot.common.bean.TimeZoneBean;

public class TimeZoneHeadBean implements SectionEntity {
    private TimeZoneBean timeZoneBean;
    private boolean isHeader = false;
    private String headerStr;

    public TimeZoneHeadBean(boolean isHeader, String headerStr) {
        this.headerStr = headerStr;
        this.isHeader = isHeader;
    }

    public String getHeaderStr() {
        return this.headerStr;
    }

    public void setTimeZoneBean(TimeZoneBean timeZoneBean) {
        this.timeZoneBean = timeZoneBean;
    }

    public TimeZoneHeadBean(TimeZoneBean timeZoneBean) {
        this.timeZoneBean = timeZoneBean;
    }

    public TimeZoneBean getTimeZoneBean() {
        return this.timeZoneBean;
    }

    public boolean isHeader() {
        return this.isHeader;
    }

    public int getItemType() {
        return this.isHeader ? -99 : -100;
    }
}
