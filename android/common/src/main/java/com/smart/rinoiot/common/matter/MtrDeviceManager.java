package com.smart.rinoiot.common.matter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dsh.matter.management.device.DeviceCommissionCallback;
import com.dsh.matter.management.device.DeviceDecommissionCallback;
import com.dsh.matter.management.device.DeviceManager;
import com.dsh.matter.model.scanner.DeviceSharePayload;
import com.dsh.matter.model.wifi.WiFiCredentials;
import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.matter.callback.DeviceManagerCallback;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.Observable;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * @author edwin
 */
public class MtrDeviceManager {

    /**
     *
     */
    private static final String TAG = "MtrDeviceManager";

    /**
     * An instance of the device states manager
     */
    private static MtrDeviceManager instance;

    /**
     * Mtr device states manager
     */
    private final DeviceManager mDeviceManager;

    private MtrDeviceManager(@ApplicationContext Context context) {
        mDeviceManager = new DeviceManager(context);
    }

    /**
     * Retrieves the instance of the device manager
     *
     * @param context application context
     * @return the instance of the device manager
     */
    public static MtrDeviceManager getInstance(@ApplicationContext Context context) {
        if(instance == null) {
            instance = new MtrDeviceManager(context);
        }
        return instance;
    }

    /**
     * Finishes device commissioning and terminates an current commissioning processes
     */
    public void stopDevicePairing() {
        mDeviceManager.finishCommissioningDevice();
    }

    /**
     * Removes a device matter fabric
     *
     * @param deviceId device identifier
     * @param callback op callback
     */
    public void remove(int deviceId, DeviceManagerCallback<Boolean> callback){
        DeviceDecommissionCallback decommissionCallback = new DeviceDecommissionCallback() {
            @Override
            public void onError(int i, @NonNull String s) {
                callback.onError(new IllegalStateException(s));
            }

            @Override
            public void onSuccess(long l) {
                callback.onSuccess(true);
            }
        };

        // Decommission device
        mDeviceManager.decommissionDevice(deviceId, decommissionCallback);
    }

    /**
     * Removes a device matter fabric
     *
     * @param deviceId device identifier
     */
    public void remove(long deviceId){
        DeviceDecommissionCallback decommissionCallback = new DeviceDecommissionCallback() {
            @Override
            public void onError(int i, @NonNull String s) {
                Log.e(TAG, "onError: " + s);
            }

            @Override
            public void onSuccess(long l) {
                Log.d(TAG, "onSuccess: " + l);
            }
        };

        // decommission device
        mDeviceManager.decommissionDevice(deviceId, decommissionCallback);
    }

    /**
     * Starts device commissioning
     *
     * @param onBoardingPayload on-boarding payload
     * @param callback device commissioning callback
     */
    public void startDevicePairing(String onBoardingPayload, DeviceCommissionCallback callback) {
        mDeviceManager.startCommissioningDevice(onBoardingPayload, callback);
    }

    /**
     * Open device commissioning window for device sharing
     *
     * @param metadata metadata
     * @param callback callback
     */
    public void startDeviceSharing(Metadata metadata, DeviceManagerCallback<DeviceSharePayload> callback){
        Continuation<DeviceSharePayload> continuation = new Continuation<DeviceSharePayload>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object obj) {
                try {
                    DeviceSharePayload payload =
                            new Gson().fromJson(new Gson().toJson(obj), DeviceSharePayload.class);
                    callback.onSuccess(payload);
                }catch (Exception ex) {
                    callback.onError(ex);
                }
            }
        };

        mDeviceManager.shareDevice(
                metadata.getDeviceId(),
                metadata.getDiscriminator(),
                180,
                continuation
        );
    }

    /**
     * Requests the number of commissioned fabrics
     * @param deviceId the device identifier
     * @param callback the callback.
     */
    public void getCommissionedFabricCount(long deviceId, DeviceManagerCallback<Integer> callback) {
        Continuation<Integer> continuation = new Continuation<Integer>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object obj) {
                try {
                    Integer count = Integer.parseInt(String.valueOf(obj));
                    callback.onSuccess(count);
                }catch (Exception ex) {
                    callback.onError(ex);
                }
            }
        };

        try {
            mDeviceManager.getCommissionedFabrics(deviceId, continuation);
        }catch (Exception exception) {
            Log.e(TAG, "getCommissionedFabricCount: " + exception.getLocalizedMessage());
            callback.onError(exception);
        }
    }

    /**
     * Requests the number of fabrics a device can be commissioned to
     * @param deviceId the device identifier
     * @param callback the callback.
     */
    public void getSupportedFabricCount(long deviceId, DeviceManagerCallback<Integer> callback) {
        Continuation<Integer> continuation = new Continuation<Integer>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object obj) {
                try {
                    Integer count = Integer.parseInt(String.valueOf(obj));
                    callback.onSuccess(count);
                }catch (Exception ex) {
                    callback.onError(ex);
                }
            }
        };

        try {
            mDeviceManager.getSupportedFabrics(deviceId, continuation);
        }catch (Exception exception) {
            Log.e(TAG, "getCommissionedFabricCount: " + exception.getLocalizedMessage());
            callback.onError(exception);
        }
    }

    /**
     * Continues device pairing with WiFi credentials provided by the user
     *
     * @param devicePtr device pointer
     * @param credentials WiFi credentials
     */
    public void continueDevicePairing(long devicePtr, WiFiCredentials credentials) {
        mDeviceManager.continueCommissioningDevice(devicePtr, credentials);
    }

    /**
     * Continues device commissioning
     *
     * @param devicePtr device pointer.
     * @param ignoreError whether to ignore device attestation failure or not.
     */
    public void continueDevicePairing(long devicePtr, boolean ignoreError) {
        mDeviceManager.continueCommissioningDevice(devicePtr, ignoreError);
    }
}
