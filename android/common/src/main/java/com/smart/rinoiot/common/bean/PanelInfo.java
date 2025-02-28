package com.smart.rinoiot.common.bean;

public class PanelInfo {
    /** UI包（android） */
    private String androidUrl;
    /** 模板id */
    private String id;
    /** 图片地址 */
    private String imgUrl;
    /** 多语言文件地址 */
    private String langUrl;

    public String getAndroidUrl() {
        return androidUrl;
    }

    public void setAndroidUrl(String androidUrl) {
        this.androidUrl = androidUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLangUrl() {
        return langUrl;
    }

    public void setLangUrl(String langUrl) {
        this.langUrl = langUrl;
    }
}
