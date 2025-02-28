package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/11/17 17:29
 * @description 根据产品ID和协议查询配网引导数据
 */
public class ProductGuideStepBean implements Serializable {
    private String btnDesc;//按钮文案
    private String describe;//文案
    private String forId;//业务ID: default/方案ID/产品ID/app品类ID
    private String forType;//业务类型(0:协议默认；1：方案，2：产品，3：app品类)
    private String id;//
    private String protocolType;//通讯协议
    private String setpNum;//步骤
    private String showBtnDesc;//按钮文案预览
    private String showDescribe;//文案预览
    private String showSubtitle;//	副标题预览
    private String showTitle;//标题预览
    private String subtitle;//副标题
    private String title;//标题
    private String url;//图片url

    public String getBtnDesc() {
        return btnDesc;
    }

    public void setBtnDesc(String btnDesc) {
        this.btnDesc = btnDesc;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getForId() {
        return forId;
    }

    public void setForId(String forId) {
        this.forId = forId;
    }

    public String getForType() {
        return forType;
    }

    public void setForType(String forType) {
        this.forType = forType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public String getSetpNum() {
        return setpNum;
    }

    public void setSetpNum(String setpNum) {
        this.setpNum = setpNum;
    }

    public String getShowBtnDesc() {
        return showBtnDesc;
    }

    public void setShowBtnDesc(String showBtnDesc) {
        this.showBtnDesc = showBtnDesc;
    }

    public String getShowDescribe() {
        return showDescribe;
    }

    public void setShowDescribe(String showDescribe) {
        this.showDescribe = showDescribe;
    }

    public String getShowSubtitle() {
        return showSubtitle;
    }

    public void setShowSubtitle(String showSubtitle) {
        this.showSubtitle = showSubtitle;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
