package com.smart.rinoiot.common.datastore;

import com.smart.rinoiot.common.datastore.persistence.UnifiedDeviceState;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author edwin
 * The access point for managing the unified device states
 */
public interface UnifiedDeviceStateDataSource {
    /**
     * Queries device's unified states by device identifier.
     * @param deviceId the device identifier.
     * @return the device's unified states.
     */
    Flowable<UnifiedDeviceState> getStates(String deviceId);

    /**
     * Queries all the device's unified states.
     * @return the list of all device states.
     */
    Flowable<List<UnifiedDeviceState>> getStates();

    /**
     * Queries unified states for a list of certain devices.
     *
     * @param deviceIds device device identifiers.
     * @return the list of all the device states in the list.
     */
    Flowable<List<UnifiedDeviceState>> getStates(List<String> deviceIds);

    /**
     * Inserts a list of device states.
     * @param unifiedDeviceStates the list device states.
     * @return a completable
     */
    Completable addStates(List<UnifiedDeviceState> unifiedDeviceStates);

    /**
     * Inserts device's states.
     * @param unifiedDeviceState the unified device states
     * @return a completable
     */
    Completable addStates(UnifiedDeviceState unifiedDeviceState);

    /**
     * Updates states for certain device
     * @param unifiedDeviceState the unified device states
     * @return a completable
     */
    Completable updateStates(UnifiedDeviceState unifiedDeviceState);

    /**
     * Updates states for certain device
     * @param deviceId the device identifier
     * @param online the online status
     * @return a completable
     */
    Completable updateOnlineState(String deviceId, boolean online);

    /**
     * Updates states for certain device
     * @param deviceId the unified device states
     * @param hue the hue value
     * @return a completable
     */
    Completable updateHueState(String deviceId, int hue);

    /**
     * Updates states for certain device
     * @param deviceId the unified device states
     * @param brightness  the brightness value
     * @return a completable
     */
    Completable updateBrightnessState(String deviceId, int brightness);

    /**
     * Updates states for certain device
     * @param deviceId the device identifier
     * @param temperature the device temperature
     * @return a completable
     */
    Completable updateColorTemperatureState(String deviceId, int temperature);

    /**
     * Updates states for certain device
     * @param deviceId the device identifier
     * @param status the power status
     * @return a completable
     */
    Completable updateSwitchOneState(String deviceId, boolean status);

    /**
     * Updates states for certain device
     * @param deviceId the device identifier
     * @param status the power status
     * @return a completable
     */
    Completable updateSwitchTwoState(String deviceId, boolean status);

    /**
     * Updates states for certain device
     * @param deviceId the device identifier
     * @param status the power status
     * @return a completable
     */
    Completable updateSwitchThreeState(String deviceId, boolean status);

    /**
     * Updates states for certain device
     * @param deviceId the device identifier
     * @param status  the power status
     * @return a completable
     */
    Completable updateSwitchFourState(String deviceId, boolean status);

    /**
     * Updates the liked status
     * @param deviceIds the device identifier
     * @param liked liked status
     * @return a completable
     */
    Completable updateLiked(List<String> deviceIds, boolean liked);

    /**
     * Deletes all the device states from the table
     * @return the number of deleted records
     */
    Completable deleteStates();

    /**
     * Delete device's states
     * @param deviceId the device
     * @return the number of deleted records.
     */
    Completable deleteStates(String deviceId);
}
