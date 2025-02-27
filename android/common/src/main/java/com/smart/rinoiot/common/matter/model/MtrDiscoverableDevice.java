package com.smart.rinoiot.common.matter.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dsh.matter.model.scanner.DeviceSharePayload;
import com.smart.rinoiot.common.bean.Metadata;

import java.io.Serializable;

/**
 * @author edwin
 */
public class MtrDiscoverableDevice implements Serializable {

    @NonNull
    private final String deviceId;
    private String name;
    private String icon;
    private Metadata metadata;
    private DeviceBindingStatus status;
    private DeviceSharePayload sharePayload;

    public MtrDiscoverableDevice(
            @NonNull String deviceId,
            String name,
            String icon,
            DeviceSharePayload sharePayload
    ) {
        this.deviceId = deviceId;
        this.name = name;
        this.icon = icon;
        this.status = new DeviceBindingStatus.InProgress();
        this.sharePayload = sharePayload;
    }

    @NonNull
    public String getDeviceId() {
        return deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Metadata getMetadata() {
        return metadata;
    }
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public DeviceBindingStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceBindingStatus status) {
        this.status = status;
    }

    public DeviceSharePayload getSharePayload() {
        return sharePayload;
    }

    public void setSharePayload(DeviceSharePayload sharePayload) {
        this.sharePayload = sharePayload;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(null == obj){
            return false;
        }

        if(!(obj instanceof MtrDiscoverableDevice)){
            return false;
        }

        String that = ((MtrDiscoverableDevice) obj).getDeviceId();
        return this.deviceId.contentEquals(that);
    }
}
