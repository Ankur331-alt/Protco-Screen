package com.smart.rinoiot.common.location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author edwin
 */
public interface GeolocationApiService {
    /**
     * Fetch the Geolocation info for a given public IP address
     *
     * @param ipAddress the public ip address
     * @return the geolocation info
     */
    @GET("json/{ipAddress}")
    Call<GeolocationResponse> getDeviceGeolocation(@Path("ipAddress") String ipAddress);
}
