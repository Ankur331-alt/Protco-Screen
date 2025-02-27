package com.smart.rinoiot.voice.search;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author edwin
 */
public class SearchApiClient {
    /**
     * Base url
     */
    private static final String BASE_URL = "https://tools.plusminus.cyou:6040/";

    /**
     * Creates the news API service
     * @return the news API service
     */
    public static SearchApiService createService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(SearchApiService.class);
    }
}