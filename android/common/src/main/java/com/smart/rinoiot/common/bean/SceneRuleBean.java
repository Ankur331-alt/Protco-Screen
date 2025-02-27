package com.smart.rinoiot.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SceneRuleBean implements Serializable {
//    /** 是否启用（1=启用，0=未启用） */
//    private int enabled;
    /** 规则ID */
    private String id;
    /** 规则名称 */
    private String name;
    /** 本地联动（1=本地联动，0=非本地联动） */
    private int localLinkage = 0;
    /** 匹配类型(1=Any,2=All) */
    private int matchType = 2;
    /** 规则执行类型（1=平台，2=边缘网关，3=网关） */
    private int ruleExecutor = 1;
    /** 规则模式 (1=TCA,2=CA) */
    private int ruleMode = 1;
    /** 规则任务 */
    private List<SceneActionBean> actions = new ArrayList<>();
    /** 规则条件 */
    private List<SceneConditionBean> conditions = new ArrayList<>();
    /** 规则触发器 */
    private List<SceneTriggerBean> triggers = new ArrayList<>();

//    public int getEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(int enabled) {
//        this.enabled = enabled;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLocalLinkage() {
        return localLinkage;
    }

    public void setLocalLinkage(int localLinkage) {
        this.localLinkage = localLinkage;
    }

    public int getMatchType() {
        return matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }

    public int getRuleExecutor() {
        return ruleExecutor;
    }

    public void setRuleExecutor(int ruleExecutor) {
        this.ruleExecutor = ruleExecutor;
    }

    public int getRuleMode() {
        return ruleMode;
    }

    public void setRuleMode(int ruleMode) {
        this.ruleMode = ruleMode;
    }

    public List<SceneActionBean> getActions() {
        return actions;
    }

    public void setActions(List<SceneActionBean> actions) {
        this.actions = actions;
    }

    public List<SceneConditionBean> getConditions() {
        return conditions;
    }

    public void setConditions(List<SceneConditionBean> conditions) {
        this.conditions = conditions;
    }

    public List<SceneTriggerBean> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<SceneTriggerBean> triggers) {
        this.triggers = triggers;
    }
}
