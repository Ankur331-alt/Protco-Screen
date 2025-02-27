package com.smart.rinoiot.common.location;

import com.smart.rinoiot.common.bean.Geolocation;

/**
 * @author edwin
 */
public interface GeolocationListener {

    /**
     * Invoked on successful events
     * @param location the geolocation
     */
    void onSuccess(Geolocation location);

    /**
     * Invoked on error events
     * @param message the error message
     */
    void onError(String message);
}
