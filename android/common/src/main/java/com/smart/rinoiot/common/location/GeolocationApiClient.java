package com.smart.rinoiot.common.location;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author edwin
 */
public class GeolocationApiClient {

    /**
     * Base url
     */
    private static final String BASE_URL = "http://ip-api.com/";

    /**
     * Creates a Geolocation API service
     * @return a geolocation API services
     */
    public static GeolocationApiService createService() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GeolocationApiService.class);
    }
}
