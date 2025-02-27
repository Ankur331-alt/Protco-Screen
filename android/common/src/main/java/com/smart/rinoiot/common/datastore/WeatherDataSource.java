package com.smart.rinoiot.common.datastore;


import com.smart.rinoiot.common.datastore.persistence.WeatherData;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author edwin
 */
public interface WeatherDataSource {

    /**
     * Queries the weather data
     * @return the weather data
     */
    Flowable<WeatherData> select();

    /**
     * Inserts the weather data
     * @param weatherData the weather data
     * @return a completable
     */
    Completable insert(WeatherData weatherData);

    /**
     * Updates the weather data
     * @param weatherData the weather data
     * @return a completable
     */
    Completable update(WeatherData weatherData);
}
