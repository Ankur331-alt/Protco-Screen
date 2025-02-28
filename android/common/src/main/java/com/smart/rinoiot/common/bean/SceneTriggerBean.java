package com.smart.rinoiot.common.bean;

import com.smart.rinoiot.common.Constant;

import java.io.Serializable;

public class SceneTriggerBean implements Serializable {
    /** 城市编码 */
    private String cityCode;
    /** 城市名称 */
    private String cityName;
    /** 条件类型（1=时间段） */
    private int condType = 1;
    /** 结束时间（HH:mm） */
    private String end;
    /** 结束时间是否为 am */
    private boolean endIsAm;
    /** 预处理ID */
    private String id;
    /** 重复(7位代表一周的七天，值为1说明当天生效) */
    private String loops = "1111111";
    /** 规则ID */
    private String ruleId;
    /** 开始时间（HH:mm） */
    private String start;
    /** 开始时间是否为 am */
    private boolean startIsAm;
    /** 时间周期（1=全天，2=白天，3=夜晚，4=自定义） */
    private int timeInterval = Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_ALL_DAY;
    /** 时区ID（ 如：Asia/Shanghai） */
    private String tz;

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

    public int getCondType() {
        return condType;
    }

    public void setCondType(int condType) {
        this.condType = condType;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isEndIsAm() {
        return endIsAm;
    }

    public void setEndIsAm(boolean endIsAm) {
        this.endIsAm = endIsAm;
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

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public boolean isStartIsAm() {
        return startIsAm;
    }

    public void setStartIsAm(boolean startIsAm) {
        this.startIsAm = startIsAm;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }
}
