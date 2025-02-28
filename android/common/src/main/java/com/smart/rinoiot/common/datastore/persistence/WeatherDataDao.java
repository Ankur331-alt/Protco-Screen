package com.smart.rinoiot.common.datastore.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author edwin
 */
@Dao
public interface WeatherDataDao {

    /**
     * Queries the weather data
     * @return the weather data
     */
    @Query(value = "SELECT * FROM weather_data LIMIT 1")
    Flowable<WeatherData> select();

    /**
     * Inserts the weather data
     * @param weatherData the weather data
     * @return a completable
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(WeatherData weatherData);

    /**
     * Updates the weather data
     * @param weatherData the weather data
     * @return a completable
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(WeatherData weatherData);
}
