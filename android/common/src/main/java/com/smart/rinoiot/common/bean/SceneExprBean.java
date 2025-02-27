package com.smart.rinoiot.common.bean;

import java.io.Serializable;

public class SceneExprBean implements Serializable {
    /** 条件code */
    private String propName;
    /** 条件判断 */
    private String expression;
    /** 条件值 */
    private Object value;
    /** 条件值单位 */
    private String unit;

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
