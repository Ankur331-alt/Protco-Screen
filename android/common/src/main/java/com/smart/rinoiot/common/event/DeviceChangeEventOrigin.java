package com.smart.rinoiot.common.event;

import androidx.annotation.Nullable;

/**
 * @author edwin
 */
public class DeviceChangeEventOrigin {

    public static final DeviceChangeEventOrigin CLOUD = new DeviceChangeEventOrigin(
            "cloud"
    );
    public static final DeviceChangeEventOrigin HOME_PAGE = new DeviceChangeEventOrigin(
            "home-page"
    );
    public static final DeviceChangeEventOrigin HOUSEHOLD = new DeviceChangeEventOrigin(
            "household"
    );

    private final String value;

    public DeviceChangeEventOrigin(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(null == obj){
            return false;
        }

        if(!(obj instanceof DeviceChangeEventOrigin)){
            return false;
        }

        String that = ((DeviceChangeEventOrigin) obj).getValue();
        return this.value.contentEquals(that);
    }
}
