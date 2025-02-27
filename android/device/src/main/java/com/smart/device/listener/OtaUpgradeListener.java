package com.smart.device.listener;

/**
 * @author tw
 * @time 2022/10/18 19:11
 * @description OTA固件升级回调
 */
public interface OtaUpgradeListener {
//    void upgradeStart();//开始下载

    void upgradeComplete();//更新完成状态

    void upgradeProgress(int progress);//更新进度条

    void upgradeError(String errorMsg);//更新失败
}
