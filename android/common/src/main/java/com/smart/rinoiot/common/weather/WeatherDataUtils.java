package com.smart.rinoiot.common.weather;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.smart.rinoiot.common.api.CommonApiService;
import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.bean.Geolocation;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.weather.model.RinoWeatherData;
import com.smart.rinoiot.common.weather.model.TemperatureUnit;
import com.smart.rinoiot.common.weather.model.Units;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.qualifiers.ApplicationContext;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author edwin
 */
public class WeatherDataUtils {

    private static final String TAG = "WeatherDataUtils";

    /**
     * Queries the current weather for a certain location
     * @param location the location
     * @return the weather data
     */
    public static JSONObject getCurrentWeatherData(String location) {
        // check location
        if(StringUtil.isBlank(location)){
            Geolocation geolocation = CacheDataManager.getInstance().getDeviceLastKnownLocation();
            if(null == geolocation || StringUtil.isBlank(geolocation.getCity())){
                return null;
            }else{
                location = geolocation.getCity();
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("cityName", location);
        Log.d(TAG, "getCurrentWeatherData: location = " + new Gson().toJson(params));
        // Make the API call to get current weather data
        Call<BaseResponse<RinoWeatherData>> call = RetrofitUtils.getService(
                CommonApiService.class).fetchCityWeatherData(params);
        try {
            // Execute the API call synchronously
            Response<BaseResponse<RinoWeatherData>> response = call.execute();
            if (response.isSuccessful()) {
                return new JSONObject(new Gson().toJson(response.body()));
            }
            throw new Exception(String.valueOf(response.code()));
        } catch (Exception e) {
            Log.e(
                    TAG, "getCurrentWeatherData: Failed to get weather data. "
                            + e.getLocalizedMessage()
            );
        }
        return null;
    }

    /**
     * Getter for the weather data units of measurement
     *
     * @param context the application context
     * @return the units of measurement
     */
    public static Units getUnitsOfMeasurement(@ApplicationContext Context context){
        Units units = Units.Metric;
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(context);
        if(null == userInfo){
            return units;
        }

        if(TextUtils.isEmpty(userInfo.tempUnit)){
            return units;
        }

        String tempUnit = userInfo.tempUnit;
        if(TemperatureUnit.Fahrenheit.getSymbol().contentEquals(tempUnit)) {
            units = Units.Imperial;
        }else if(TemperatureUnit.Kelvin.getSymbol().contentEquals(tempUnit)) {
            units = Units.Standard;
        }
        return units;
    }
}
