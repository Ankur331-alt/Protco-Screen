package com.smart.rinoiot.scene.bean;

import java.io.Serializable;

public class IconItemBean implements Serializable {
    /** 排序 */
    private int sortNumber;
    /** 唯一标识 */
    private String id;
    /** 图标链接 */
    private String iconUrl;
    /** 应用ID */
    private String appId;

    private boolean isSelect;

    public int getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
