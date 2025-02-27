package com.smart.rinoiot.common.weather;

import com.smart.rinoiot.common.weather.model.AirPollutionResponse;
import com.smart.rinoiot.common.weather.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author edwin
 */
public interface WeatherApiService {
    /**
     * Queries the current weather for a certain location
     *
     * @param location the location
     * @param apiKey the api key
     * @param units Units of measurement
     * @return the query response
     */
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String location,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    /**
     * Queries the current air pollution data for a certain location
     *
     * @param lon longitude.
     * @param lat latitude.
     * @param apiKey the api key.
     * @return the query response.
     */
    @GET("air_pollution")
    Call<AirPollutionResponse> getCurrentAirPollution(
            @Query("lon") double lon,
            @Query("lat") double lat,
            @Query("appid") String apiKey
    );
}