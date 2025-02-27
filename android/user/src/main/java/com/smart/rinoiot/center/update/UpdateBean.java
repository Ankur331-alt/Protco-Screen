package com.smart.rinoiot.center.update;

import java.io.Serializable;

public class UpdateBean implements Serializable {
    private String allowLowestVersion;//允许的最低版本（低于此版本强制更新）
    private String appId;//应用ID
    private Integer platform;//平台(1=ios,2=android)
    private Integer publishStatus;//	发布状态（1=已发布）
    private String resUrl;//资源包地址
    private String upgradeContent;//更新内容
    private Integer upgradeTime;//指定升级时间
    private Integer upgradeType;//更新类型（0=静默，1=弱提示，2=强提示，3=强制更新）
    private String version;//版本号

    public String getAllowLowestVersion() {
        return allowLowestVersion;
    }

    public void setAllowLowestVersion(String allowLowestVersion) {
        this.allowLowestVersion = allowLowestVersion;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getResUrl() {
        return resUrl;
    }

    public void setResUrl(String resUrl) {
        this.resUrl = resUrl;
    }

    public String getUpgradeContent() {
        return upgradeContent;
    }

    public void setUpgradeContent(String upgradeContent) {
        this.upgradeContent = upgradeContent;
    }

    public Integer getUpgradeTime() {
        return upgradeTime;
    }

    public void setUpgradeTime(Integer upgradeTime) {
        this.upgradeTime = upgradeTime;
    }

    public Integer getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(Integer upgradeType) {
        this.upgradeType = upgradeType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
