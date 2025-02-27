package com.smart.rinoiot.common.datastore.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author edwin
 * Data Access Object for the unified device states table.
 */
@Dao
public interface UnifiedDeviceStatesDao {

    /**
     * Queries device's unified states by device identifier.
     * @param deviceId the device identifier.
     * @return the device's unified states.
     */
    @Transaction
    @Query(value = "SELECT * FROM unified_device_states WHERE device_id = :deviceId LIMIT 1")
    Flowable<UnifiedDeviceState> select(String deviceId);

    /**
     * Queries all the device's unified states.
     * @return the list of all device states.
     */
    @Transaction
    @Query(value = "SELECT * FROM unified_device_states")
    Flowable<List<UnifiedDeviceState>> selectAll();

    /**
     * Queries all the device's unified states.
     * @param deviceIds the list of device identifiers
     * @return the list of all device states.
     */
    @Transaction
    @Query(value = "SELECT * FROM unified_device_states WHERE device_id IN (:deviceIds)")
    Flowable<List<UnifiedDeviceState>> selectAllIn(List<String> deviceIds);

    /**
     * Inserts a list of device states.
     * @param unifiedDeviceStates the list device states.
     * @return the number of records inserted.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<UnifiedDeviceState> unifiedDeviceStates);

    /**
     * Inserts device's states.
     * @param unifiedDeviceState the unified device states
     * @return the number of inserted records
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(UnifiedDeviceState unifiedDeviceState);

    /**
     * Updates states for certain device
     * @param unifiedDeviceState the unified device states
     * @return the number of updates records.
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(UnifiedDeviceState unifiedDeviceState);

    /**
     * Deletes all the device states from the table
     * @return the number of deleted records
     */
    @Transaction
    @Query(value = "DELETE FROM unified_device_states")
    Completable deleteAll();

    /**
     * Delete device's states
     * @param deviceId the device identifier.
     * @return the number of deleted records.
     */
    @Transaction
    @Query(value = "DELETE FROM unified_device_states WHERE device_id = :deviceId")
    Completable delete(String deviceId);

    /**
     * Updates the online status
     * @param deviceId the device identifier
     * @param online online status
     * @return a completable
     */
    @Transaction
    @Query(value = "UPDATE unified_device_states SET online = :online WHERE device_id = :deviceId")
    Completable updateOnline(String deviceId, boolean online);

    /**
     * Updates the light's hue
     *
     * @param deviceId the device identifier
     * @param hue the hue value
     * @return a completable
     */
    @Transaction
    @Query(value = "UPDATE unified_device_states SET hue = :hue WHERE device_id = :deviceId")
    Completable updateHue(String deviceId, int hue);

    /**
     * Updates the device's brightness
     *
     * @param deviceId the device identifier
     * @param brightness the brightness
     * @return a completable
     */
    @Transaction
    @Query(value = "UPDATE unified_device_states SET brightness = :brightness WHERE device_id = :deviceId")
    Completable updateBrightness(String deviceId, int brightness);

    /**
     * Update the device color temperature
     * @param deviceId the device identifier
     * @param temperature the temperature.
     * @return a completable.
     */
    @Transaction
    @Query(value = "UPDATE unified_device_states SET color_temperature = :temperature WHERE device_id = :deviceId")
    Completable updateColorTemperature(String deviceId, int temperature);

    /**
     * Updates the switch one status
     * @param deviceId the device identifier
     * @param status the status
     * @return a completable
     */
    @Transaction
    @Query(value = "UPDATE unified_device_states SET switch_1 = :status WHERE device_id = :deviceId")
    Completable updateSwitchOne(String deviceId, boolean status);

    /**
     * Updates the switch two status
     * @param deviceId the device identifier
     * @param status the status
     * @return a completable
     */
    @Transaction
    @Query(value = "UPDATE unified_device_states SET switch_2 = :status WHERE device_id = :deviceId")
    Completable updateSwitchTwo(String deviceId, boolean status);

    /**
     * Updates the switch three status
     * @param deviceId the device identifier
     * @param status the status
     * @return a completable
     */
    @Transaction
    @Query(value = "UPDATE unified_device_states SET switch_3 = :status WHERE device_id = :deviceId")
    Completable updateSwitchThree(String deviceId, boolean status);

    /**
     * Updates the switch four status
     * @param deviceId the device identifier
     * @param status the status
     * @return a completable
     */
    @Transaction
    @Query(value = "UPDATE unified_device_states SET switch_4 = :status WHERE device_id = :deviceId")
    Completable updateSwitchFour(String deviceId, boolean status);

    /**
     * Updates the liked status
     * @param deviceIds the device identifier
     * @param liked liked status
     * @return a completable
     */
    @Transaction
    @Query(value = "UPDATE unified_device_states SET liked = :liked WHERE device_id IN (:deviceIds)")
    Completable updateLiked(List<String> deviceIds, boolean liked);
}
