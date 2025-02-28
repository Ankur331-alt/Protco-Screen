package com.smart.rinoiot.voice.news;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author edwin
 */
public class NewsApiClient {
    /**
     * Base url
     */
    private static final String BASE_URL = "https://duck.bluce.eu.org/";

    /**
     * Creates the news API service
     * @return the news API service
     */
    public static NewsApiService createService() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(NewsApiService.class);
    }
}
