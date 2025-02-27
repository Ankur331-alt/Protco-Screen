package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2023/3/14 21:15
 * @description 面板多语言数据
 */
public class PanelMultiLangBean implements Serializable {
    private Object commonLang;//公共词条
    private Object dpLang;//	产品功能点
    private Object panelLang;//面板公共词条
    private Object productLang;//产品多语言

    public Object getCommonLang() {
        return commonLang;
    }

    public void setCommonLang(Object commonLang) {
        this.commonLang = commonLang;
    }

    public Object getDpLang() {
        return dpLang;
    }

    public void setDpLang(Object dpLang) {
        this.dpLang = dpLang;
    }

    public Object getPanelLang() {
        return panelLang;
    }

    public void setPanelLang(Object panelLang) {
        this.panelLang = panelLang;
    }

    public Object getProductLang() {
        return productLang;
    }

    public void setProductLang(Object productLang) {
        this.productLang = productLang;
    }
}
