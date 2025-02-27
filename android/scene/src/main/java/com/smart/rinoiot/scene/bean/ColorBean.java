package com.smart.rinoiot.scene.bean;

import java.io.Serializable;

public class ColorBean implements Serializable {
    /** 颜色值 */
    private String colorRes;
    private boolean isSelect;

    public String getColorRes() {
        return colorRes;
    }

    public void setColorRes(String colorRes) {
        this.colorRes = colorRes;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
