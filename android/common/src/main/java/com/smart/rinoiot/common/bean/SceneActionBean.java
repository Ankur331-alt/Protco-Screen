package com.smart.rinoiot.common.bean;

import java.io.Serializable;

public class SceneActionBean implements Serializable {
    /** 动作类型（1= 操作设备，2=执行场景，3=发送通知，4=延时） */
    private int actionType;
    /** 延迟(单位:s) */
    private long delay;
    /** 动作数据
     * actionType = 1,actionData.value = { props:{ "switch_led":true, ... }}
     * actionType = 2,actionData = { "enabled": true }
     * actionType = 3,actionData = { "noticeType":1 } */
    private String actionData = "";
    /** 唯一标识 */
    private String id;
    /** 序号 */
    private int orderNum;
    /** 规则ID */
    private String ruleId;
    /** 目标ID（与 action_type 配合使用, 比如设备id或者场景id） */
    private String targetId;

    /** 自定义属性，显示名称 */
    private String actionName;

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
