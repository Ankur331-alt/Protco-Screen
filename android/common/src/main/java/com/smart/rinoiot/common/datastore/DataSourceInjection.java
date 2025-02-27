package com.smart.rinoiot.common.datastore;

import android.content.Context;

import com.smart.rinoiot.common.datastore.persistence.UnifiedDeviceStateDataSourceImpl;
import com.smart.rinoiot.common.datastore.persistence.AppDatabase;
import com.smart.rinoiot.common.datastore.persistence.WeatherDataStoreImpl;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * @author edwin
 * Enables injection of data sources
 */
public class DataSourceInjection {
    public static UnifiedDeviceStateDataSource provideUnifiedDeviceStateDataSource(
        @ApplicationContext Context context
    ) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new UnifiedDeviceStateDataSourceImpl(database.unifiedDeviceStatesDao());
    }

    public static WeatherDataSource provideWeatherDataSource(
            @ApplicationContext Context context
    ){
        AppDatabase database = AppDatabase.getInstance(context);
        return new WeatherDataStoreImpl((database.weatherDataDao()));
    }
}
