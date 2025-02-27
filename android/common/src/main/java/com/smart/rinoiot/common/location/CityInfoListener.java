package com.smart.rinoiot.common.location;

import com.smart.rinoiot.common.bean.CityBean;

/**
 * @author edwin
 */
public interface CityInfoListener {
    /**
     * Invoked on successful events
     * @param city the city data
     */
    void onSuccess(CityBean city);

    /**
     * Invoked on error events
     * @param message the error message
     */
    void onError(String message);
}
