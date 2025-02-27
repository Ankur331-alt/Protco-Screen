package com.smart.rinoiot.center.bean;

import androidx.annotation.Nullable;

/**
 * @author edwin
 */
public class IpCameraDashboardEvent {
    public static final IpCameraDashboardEvent OPEN_STREAMS =  new IpCameraDashboardEvent(
            "load-streams"
    );

    public static final IpCameraDashboardEvent CLOSE_STREAMS =  new IpCameraDashboardEvent(
            "close-streams"
    );

    private final String value;

    public IpCameraDashboardEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null == obj) {
            return false;
        }

        if(!(obj instanceof IpCameraDashboardEvent)){
            return false;
        }

        String that = ((IpCameraDashboardEvent) obj).getValue();
        return this.value.contentEquals(that);
    }
}
