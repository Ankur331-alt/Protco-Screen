package com.smart.rinoiot.center.bean;

import androidx.annotation.Nullable;

/**
 * @author edwin
 */
public class IpCameraIntercomStatus {

    public static final IpCameraIntercomStatus IS_OPEN = new IpCameraIntercomStatus("is-open");

    public static final IpCameraIntercomStatus IS_CLOSE = new IpCameraIntercomStatus("is-closed");

    private final String value;

    public IpCameraIntercomStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(null == obj) {
            return false;
        }

        if (!(obj instanceof IpCameraIntercomStatus)) {
            return false;
        }

        String that = ((IpCameraIntercomStatus) obj).getValue();
        return this.value.contentEquals(that);
    }
}
