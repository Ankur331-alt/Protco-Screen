package com.smart.rinoiot.common.datastore.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @author edwin
 * The Room database that contains the unified device states table
 */
@Database(entities = {UnifiedDeviceState.class, WeatherData.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    /**
     * An instance of the weather data dao.
     * @return an instance of the weather data dao.
     */
    public abstract WeatherDataDao weatherDataDao();

    /**
     * An instance of the unified device states dao.
     * @return an instance of the unified device states dao.
     */
    public abstract UnifiedDeviceStatesDao unifiedDeviceStatesDao();

    public static AppDatabase getInstance(Context context) {
        if(null == INSTANCE){
            synchronized (AppDatabase.class) {
                if(null == INSTANCE) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "AppDatabase.db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
