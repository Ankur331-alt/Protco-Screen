package com.smart.rinoiot.common.matter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dsh.matter.management.device.DeviceStatesManager;
import com.dsh.matter.model.device.StateAttribute;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.MatterLocalBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.matter.callback.MtrDeviceStatesCallback;

import java.lang.reflect.Type;
import java.util.HashMap;

import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * @author edwin
 */
public class MtrDeviceStatesManager {

    /**
     *
     */
    private static final String TAG = "MtrDeviceStatesManager";

    /**
     * An instance of the device states manager
     */
    private static MtrDeviceStatesManager instance;

    /**
     * Mtr device states manager
     */
    private final DeviceStatesManager mDeviceStates;

    private MtrDeviceStatesManager(@ApplicationContext Context context) {
        mDeviceStates = new DeviceStatesManager(context);
    }

    /**
     * Retrieves the instance of the device state manager
     *
     * @param context application context
     * @return the instance of the device state manager
     */
    public static MtrDeviceStatesManager getInstance(@ApplicationContext Context context) {
        if (instance == null) {
            instance = new MtrDeviceStatesManager(context);
        }
        return instance;
    }

    /**
     * Queries all the device states asynchronously
     *
     * @param deviceId device identifier
     * @param callback device states callback
     */
    public void queryDeviceStatesAsync(int deviceId, MtrDeviceStatesCallback callback) {
        Continuation<HashMap<StateAttribute, Object>> continuation = new Continuation<HashMap<StateAttribute, Object>>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object statesObj) {
                HashMap<StateAttribute, Object> states = fromObject(statesObj);
                Log.d(TAG, "Received device states: " + new Gson().toJson(states));
                callback.onReport(states);
            }
        };
        mDeviceStates.readDeviceStates(deviceId, continuation);
    }

    /**
     * Queries all the device states asynchronously
     *
     * @param metadata device identifier
     * @param callback device states callback
     */
    public void queryDeviceStatesAsync(Metadata metadata, MtrDeviceStatesCallback callback) {
        Continuation<HashMap<StateAttribute, Object>> continuation = new Continuation<HashMap<StateAttribute, Object>>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object statesObj) {
                HashMap<StateAttribute, Object> states = fromObject(statesObj);
                Log.d(TAG, "Received device states: " + new Gson().toJson(states));
                callback.onReport(states);
            }
        };
        mDeviceStates.readDeviceStates(metadata.getDeviceId(),metadata.getDeviceType(), continuation);
    }

    /**
     * Converts device states object to a more structured easier to traverse map
     *
     * @param statesObj device states object
     * @return device states map.
     */
    private HashMap<StateAttribute, Object> fromObject(Object statesObj) {
        if (null == statesObj) {
            return new HashMap<>(0);
        }

        try {
            Type type = new TypeToken<HashMap<StateAttribute, Object>>() {
            }.getType();
            return new Gson().fromJson(new Gson().toJson(statesObj), type);
        } catch (Exception ex) {
            Log.e(TAG, "Failed to extract device states. Cause: " + ex.getLocalizedMessage());
            return new HashMap<>(0);
        }
    }

    /**
     * 根据设备列表中的数据，对应匹配matter数据状态
     *
     * @param deviceInfoBean device identifier
     */
    public void queryDeviceStatesAsync(DeviceInfoBean deviceInfoBean) {
        Log.d(TAG, "Received device states: start");
        if (deviceInfoBean != null) {
            Continuation<HashMap<StateAttribute, Object>> continuation = new Continuation<HashMap<StateAttribute, Object>>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object statesObj) {
                    HashMap<StateAttribute, Object> states = fromObject(statesObj);
                    Log.d(TAG, "Received device states: " + new Gson().toJson(states));
                    MatterLocalBean matterDeviceData = CacheDataManager.getInstance().getMatterDeviceData(deviceInfoBean.getId());
                    if (matterDeviceData == null) {
                        matterDeviceData = new MatterLocalBean();
                    }
                    MatterLocalBean matterDeviceDataTemp = new Gson().fromJson(new Gson().toJson(states), MatterLocalBean.class);
                    if (matterDeviceDataTemp != null) {
                        matterDeviceData.setBrightness(matterDeviceDataTemp.getBrightness() == 0 ? matterDeviceData.getBrightness() : matterDeviceDataTemp.getBrightness());
                        matterDeviceData.setSwitch(matterDeviceDataTemp.isSwitch());
                        matterDeviceData.setOnline(matterDeviceDataTemp.isOnline());
                        matterDeviceData.setColorTemperature(matterDeviceDataTemp.getColorTemperature() == 0 ? matterDeviceData.getColorTemperature() : matterDeviceDataTemp.getColorTemperature());
                        matterDeviceData.setColor(matterDeviceDataTemp.getColor() == null ? matterDeviceData.getColor() : matterDeviceDataTemp.getColor());
                    }
                    CacheDataManager.getInstance().saveMatterDeviceData(deviceInfoBean.getId(), matterDeviceData);
                    MtrDeviceControlManager.addQueryMatterDeviceOnlineStatus(deviceInfoBean.getId());
                    MtrDeviceControlManager.updateSwitchByDeviceAllSwitch(deviceInfoBean.getId());
                }
            };
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfoBean.getMetaInfo());
            if (metadata != null) {
                mDeviceStates.readDeviceStates(metadata.getDeviceId(), metadata.getDeviceType(), continuation);
            }
        }
    }
}
