package com.smart.rinoiot.common.weather;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author edwin
 */
public class WeatherApiClient {

    /**
     * Base API url
     */
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    /**
     * Creates the Weather API service
     *
     * @return the weather API service
     */
    public static WeatherApiService createService() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WeatherApiService.class);
    }
}