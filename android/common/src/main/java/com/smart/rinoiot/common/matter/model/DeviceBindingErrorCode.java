package com.smart.rinoiot.common.matter.model;

/**
 * @author edwin
 */

public enum DeviceBindingErrorCode {
    /**
     * Failed to find device or pair device over matter
     */
    NotFound,
    /**
     * Filed to upload device to cloud
     */
    UploadFailed,

    /**
     * Failed to install device
     */
    InstallationFailed,

    /**matter sdk配网成功  寻找到设备*/
    MatterFoundSuccess
}
