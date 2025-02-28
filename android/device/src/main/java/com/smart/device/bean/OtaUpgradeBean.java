package com.smart.device.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/10/17 11:57
 * @description OTA升级检测
 */
public class OtaUpgradeBean implements Serializable {
    /**
     *固件文件名称
     */
    private String firmwareName;

    /**
     * 固件下载地址
     */
    private String firmwareUrl;

    /**
     * 发布时间
     */
    private long publishTime;

    /**
     * 升级内容
     */
    private String upgradeInfo;

    /**
     * 升级类型（1=App提醒升级，2=App检测升级，3=App强制升级）
     */
    private int upgradeType;

    /**
     * 当前固件版本
     */
    private String versionTarget;

    /**
     * 固件文件MD5
     */
    private String firmwareMd5;

    /**
     * 固件大小
     */
    private long firmwareFileSize;

    /**
     * 固件升级任务ID
     */
    private String taskId;

    public long getFirmwareFileSize() {
        return firmwareFileSize;
    }

    public void setFirmwareFileSize(long firmwareFileSize) {
        this.firmwareFileSize = firmwareFileSize;
    }

    public String getFirmwareMd5() {
        return firmwareMd5;
    }

    public void setFirmwareMd5(String firmwareMd5) {
        this.firmwareMd5 = firmwareMd5;
    }

    public String getFirmwareName() {
        return firmwareName;
    }

    public void setFirmwareName(String firmwareName) {
        this.firmwareName = firmwareName;
    }

    public String getFirmwareUrl() {
        return firmwareUrl;
    }

    public void setFirmwareUrl(String firmwareUrl) {
        this.firmwareUrl = firmwareUrl;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getUpgradeInfo() {
        return upgradeInfo;
    }

    public void setUpgradeInfo(String upgradeInfo) {
        this.upgradeInfo = upgradeInfo;
    }

    public int getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(int upgradeType) {
        this.upgradeType = upgradeType;
    }

    public String getVersionTarget() {
        return versionTarget;
    }

    public void setVersionTarget(String versionTarget) {
        this.versionTarget = versionTarget;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
