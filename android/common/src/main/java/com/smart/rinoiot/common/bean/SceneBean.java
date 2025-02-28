package com.smart.rinoiot.common.bean;

import java.io.Serializable;

public class SceneBean implements Serializable {
    /** 资产ID */
    private String assetId;
    /** 场景ID */
    private String id;
    /** 背景颜色 */
    private String bgColor = "#DCDCF4";
    /** 是否在首页显示（1=显示，0=不显示） */
    private int isHome = 0;
    /** 封面图片 */
    private String coverUrl = "https://storage-app.rinoiot.com/scene/icon/default-icon.png";
    /** 是否启用（1=启用，0=未启用） */
    private int enabled;
    /** 场景名称 */
    private String name;
    /** 规则ID */
    private String ruleId;
    /** 场景类型（1=一键执行，2=自动化） */
    private int sceneType;
    /** 用户ID */
    private String userId;
    /** 规则源数据 */
    private SceneRuleBean ruleMetaData = new SceneRuleBean();

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public int getSceneType() {
        return sceneType;
    }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SceneRuleBean getRuleMetaData() {
        return ruleMetaData;
    }

    public void setRuleMetaData(SceneRuleBean ruleMetaData) {
        this.ruleMetaData = ruleMetaData;
    }

    public int getIsHome() {
        return isHome;
    }

    public void setIsHome(int isHome) {
        this.isHome = isHome;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }
}
