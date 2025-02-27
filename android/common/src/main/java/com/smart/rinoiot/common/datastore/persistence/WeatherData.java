package com.smart.rinoiot.common.datastore.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author edwin
 */
@Entity(tableName = "weather_data")
public class WeatherData {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private final String id;
    @ColumnInfo(name = "icon")
    private final String icon;
    @ColumnInfo(name = "temperature")
    private final int temperature;
    @ColumnInfo(name = "wind_speed")
    private final double windSpeed;
    @ColumnInfo(name = "humidity")
    private final int humidity;
    @ColumnInfo(name = "aqi")
    private final int airQualityIndex;

    public WeatherData(@NonNull String id, String icon, int temperature, double windSpeed, int humidity, int airQualityIndex) {
        this.id = id;
        this.icon = icon;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.airQualityIndex = airQualityIndex;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public int getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getAirQualityIndex() {
        return airQualityIndex;
    }
}
