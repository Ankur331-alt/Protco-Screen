package com.smart.rinoiot.common.location;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.offlinemap.City;
import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.Geolocation;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.AppUtil;

import java.util.List;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author edwin
 */
public class LocationUtils {

    private static final String TAG = "LocationUtils";

    /**
     * Requests the geolocation info for a certain public IP address
     *
     * @param publicIpAddress a public ip address.
     * @return an geolocation observable
     */
    public static Observable<Geolocation> requestGeolocation(String publicIpAddress) {
        return Observable.create(emitter -> {
            GeolocationApiService service = GeolocationApiClient.createService();
            Call<GeolocationResponse> call = service.getDeviceGeolocation(publicIpAddress);
            call.enqueue(new Callback<GeolocationResponse>() {
                @Override
                public void onResponse(
                        @NonNull Call<GeolocationResponse> call,
                        @NonNull Response<GeolocationResponse> response
                ) {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = new Gson().toJson(response.body());
                        Geolocation location = new Gson().fromJson(json, Geolocation.class);
                        if(null != location){
                            emitter.onNext(location);
                            emitter.onComplete();
                        }else{
                            emitter.onError(new Exception("Failed to fetch geolocation info"));
                        }
                    } else {
                        Log.e(TAG, "onResponse: failed to fetch geolocation");
                        emitter.onError(new Exception("Failed to fetch geolocation info"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeolocationResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    emitter.onError(t);
                }
            });
        });
    }

    /**
     * Request city info
     *
     * @param context the application context
     */
    public static Observable<CityBean> requestCityInfo(@ApplicationContext Context context) {
        return Observable.create(emitter -> {
            try {
                AMapLocationClient mLocationClient = new AMapLocationClient(context);
                AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                mLocationOption.setGeoLanguage(AppUtil.changeMapLanguage(context));
                mLocationOption.setNeedAddress(true);
                mLocationOption.setOnceLocation(true);
                mLocationClient.setLocationOption(mLocationOption);
                mLocationClient.setLocationListener(aMapLocation -> {
                    if (aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            String locationCity = aMapLocation.getCity();
                            List<CityBean> cityList = CacheDataManager.getInstance().getCityList();
                            CityBean matchCity = null;
                            for (CityBean cityBean : cityList) {
                                if (cityBean.getName().toLowerCase().contains(locationCity.toLowerCase())) {
                                    matchCity = cityBean;
                                    break;
                                }
                            }
                            if(null != matchCity){
                                emitter.onNext(matchCity);
                                emitter.onComplete();
                            }else{
                                emitter.onError(new Exception("City info not found"));
                            }
                        } else {
                            emitter.onError(new Exception(
                                    "Failed to get city data. Cause=" + aMapLocation.getErrorInfo()
                            ));
                        }
                    }
                });
                mLocationClient.startLocation();
            } catch (Exception e) {
                emitter.onError(new Exception(
                        "Failed to get city data. Cause=" + e.getLocalizedMessage()
                ));
            }
        });
    }
}
