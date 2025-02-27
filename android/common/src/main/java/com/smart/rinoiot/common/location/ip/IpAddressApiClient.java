package com.smart.rinoiot.common.location.ip;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author edwin
 */
public class IpAddressApiClient {

    private static final String BASE_URL = "https://api64.ipify.org";

    public static IpAddressApiService createService() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(IpAddressApiService.class);
    }
}
