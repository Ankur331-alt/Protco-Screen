package com.smart.rinoiot.common.weather.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.Geolocation;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.CommonNetworkManager;
import com.smart.rinoiot.common.weather.model.RinoWeatherData;

import java.time.Duration;

import io.reactivex.schedulers.Schedulers;

/**
 * @author edwin
 */
public class FetchWeatherDataWorker extends Worker {

    private static final String TAG = "FetchWeatherDataWorker";
    public static final String WEATHER_DATA_WORK_NAME = "fetch-weather-data";
    public static final Duration WEATHER_DATA_WORK_INTERVAL = Duration.ofHours(1);

    public FetchWeatherDataWorker(
        @NonNull Context context,
        @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Geolocation geolocation = CacheDataManager.getInstance().getDeviceLastKnownLocation();
            Log.d(TAG, "doWork: location=" + new Gson().toJson(geolocation));
            RinoWeatherData weatherData = CommonNetworkManager.getInstance().fetchWeatherData(geolocation)
                    .subscribeOn(Schedulers.io()).blockingFirst();
            Log.d(TAG, "doWork: weather data=" + new Gson().toJson(weatherData));
            DataSourceManager.getInstance().saveWeatherData(weatherData);
            return Result.success();
        } catch (Exception exception) {
            Log.e(TAG, "Failed to get weather data. Cause: " + exception.getLocalizedMessage());
            return Result.failure();
        }
    }
}
