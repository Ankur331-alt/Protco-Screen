package com.smart.rinoiot.common.datastore.persistence;

import com.smart.rinoiot.common.datastore.WeatherDataSource;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author edwin
 */
public class WeatherDataStoreImpl implements WeatherDataSource {

    private final WeatherDataDao mWeatherDataDao;

    public WeatherDataStoreImpl(WeatherDataDao weatherDataDao) {
        this.mWeatherDataDao = weatherDataDao;
    }

    @Override
    public Flowable<WeatherData> select() {
        return this.mWeatherDataDao.select();
    }

    @Override
    public Completable insert(WeatherData weatherData) {
        return this.mWeatherDataDao.insert(weatherData);
    }

    @Override
    public Completable update(WeatherData weatherData) {
        return this.mWeatherDataDao.update(weatherData);
    }
}
