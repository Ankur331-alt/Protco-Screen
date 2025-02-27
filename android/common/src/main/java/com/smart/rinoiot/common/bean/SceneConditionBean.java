package com.smart.rinoiot.common.bean;

import java.io.Serializable;

public class SceneConditionBean implements Serializable {
    /** 城市编码 */
    private String cityCode;
    /** 城市名称 */
    private String cityName;
    /** 条件模式（1=表达式） */
    private int condMode = 1;
    /** 条件类型(0=一键执行，1=气象变化，2=位置变化，3=定时器，4=设备属性变化) */
    private int condType;
    /** 条件表达式( { "propName":"switch", "expression":"==","value": true} ) (JSON 字符串) */
    private String expr;
    /** 场景ID */
    private String id;
    /** 重复(7位代表一周的七天，值为1说明当天生效) */
    private String loops = "0000000";
    /** 排序 */
    private int orderNum;
    /** 规则实例ID */
    private String ruleId;
    /** 目标ID（配合 cond_type 使用） */
    private String targetId;
    /** 时间12小时制 (hh:mm) */
    private String time;
    /** 是否为 AM */
    private boolean timeIsAm;
    /** 时区ID（ 如：Asia/Shanghai） */
    private String tz;
    /** 定时执行日期 */
    private String executeDate;

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCondMode() {
        return condMode;
    }

    public void setCondMode(int condMode) {
        this.condMode = condMode;
    }

    public int getCondType() {
        return condType;
    }

    public void setCondType(int condType) {
        this.condType = condType;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoops() {
        return loops;
    }

    public void setLoops(String loops) {
        this.loops = loops;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isTimeIsAm() {
        return timeIsAm;
    }

    public void setTimeIsAm(boolean timeIsAm) {
        this.timeIsAm = timeIsAm;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(String executeDate) {
        this.executeDate = executeDate;
    }
}
