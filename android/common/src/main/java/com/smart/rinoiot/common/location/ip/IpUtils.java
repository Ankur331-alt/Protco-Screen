package com.smart.rinoiot.common.location.ip;

import android.util.Log;

import androidx.annotation.NonNull;

import com.smart.rinoiot.common.utils.StringUtil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author edwin
 */
public class IpUtils {

    private static final String TAG = "IPUtils";
    public static String getDeviceIpAddress() {
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<InetAddress> ipAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : ipAddresses) {
                    // Check for IPv4 addresses and exclude loopback addresses
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getDevicePublicIpAddress(final IpAddressListener listener) {
        IpAddressApiService service = IpAddressApiClient.createService();
        Call<String> call = service.getPublicIpAddress();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String ipAddress = response.body();
                    listener.onSuccess(ipAddress);
                } else {
                    listener.onError("empty body");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                listener.onError(t.getLocalizedMessage());
            }
        });
    }

    public static Observable<String> getDevicePublicIpAddress(){
        return Observable.create(emitter -> {
            IpAddressApiService service = IpAddressApiClient.createService();
            Call<String> call = service.getPublicIpAddress();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(
                        @NonNull Call<String> call, @NonNull Response<String> response
                ) {
                    Log.d(TAG, "onResponse: got the ip address");
                    if (response.isSuccessful() && StringUtil.isNotBlank(response.body())) {
                        String ipAddress = response.body();
                        emitter.onNext(ipAddress);
                        emitter.onComplete();
                    } else {
                        Log.e(TAG, "onResponse: failed to get the Ip address");
                        emitter.onError(new Exception("empty body"));
                    }

                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: failed IP. " + t.getLocalizedMessage());
                    emitter.onError(t);
                }
            });
        });
    }
}
