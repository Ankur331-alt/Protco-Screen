package com.smart.rinoiot.common.location.ip;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author edwin
 */
public interface IpAddressApiService {
    /**
     * Fetches the public IP address
     *
     * @return the public ip address
     */
    @GET("/")
    Call<String> getPublicIpAddress();
}
