package com.smart.rinoiot.scene.bean;


import com.smart.rinoiot.common.scene.ConditionEnum;

import java.io.Serializable;

public class SingleSelectBean implements Serializable {
    private String name;
    private String value;
    private boolean select;
    private boolean showLine = true;
    private ConditionEnum mEnum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void setEnum(ConditionEnum mEnum) {
        this.mEnum = mEnum;
    }

    public ConditionEnum getEnum() {
        return mEnum;
    }

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }
}
