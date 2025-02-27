package com.smart.rinoiot.common.datastore.persistence;

import com.smart.rinoiot.common.datastore.UnifiedDeviceStateDataSource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * @author edwin
 */
public class UnifiedDeviceStateDataSourceImpl implements UnifiedDeviceStateDataSource {

    private final UnifiedDeviceStatesDao mUniDeviceStatesDao;

    public UnifiedDeviceStateDataSourceImpl(UnifiedDeviceStatesDao uniDeviceStatesDao) {
        this.mUniDeviceStatesDao = uniDeviceStatesDao;
    }

    @Override
    public Flowable<UnifiedDeviceState> getStates(String deviceId) {
        return mUniDeviceStatesDao.select(deviceId);
    }

    @Override
    public Flowable<List<UnifiedDeviceState>> getStates() {
        return mUniDeviceStatesDao.selectAll();
    }

    @Override
    public Flowable<List<UnifiedDeviceState>> getStates(List<String> deviceIds) {
        return mUniDeviceStatesDao.selectAllIn(deviceIds);
    }

    @Override
    public Completable addStates(List<UnifiedDeviceState> unifiedDeviceStates) {
        return mUniDeviceStatesDao.insertAll(unifiedDeviceStates);
    }

    @Override
    public Completable addStates(UnifiedDeviceState unifiedDeviceState) {
        return mUniDeviceStatesDao.insert(unifiedDeviceState);
    }

    @Override
    public Completable updateStates(UnifiedDeviceState unifiedDeviceState) {
        return mUniDeviceStatesDao.update(unifiedDeviceState);
    }

    @Override
    public Completable updateOnlineState(String deviceId, boolean online) {
        return mUniDeviceStatesDao.updateOnline(deviceId, online);
    }

    @Override
    public Completable updateHueState(String deviceId, int hue) {
        return mUniDeviceStatesDao.updateHue(deviceId, hue);
    }

    @Override
    public Completable updateBrightnessState(String deviceId, int brightness) {
        return mUniDeviceStatesDao.updateBrightness(deviceId, brightness);
    }

    @Override
    public Completable updateColorTemperatureState(String deviceId, int temperature) {
        return mUniDeviceStatesDao.updateColorTemperature(deviceId, temperature);
    }

    @Override
    public Completable updateSwitchOneState(String deviceId, boolean status) {
        return mUniDeviceStatesDao.updateSwitchOne(deviceId, status);
    }

    @Override
    public Completable updateSwitchTwoState(String deviceId, boolean status) {
        return mUniDeviceStatesDao.updateSwitchTwo(deviceId, status);
    }

    @Override
    public Completable updateSwitchThreeState(String deviceId, boolean status) {
        return mUniDeviceStatesDao.updateSwitchThree(deviceId, status);
    }

    @Override
    public Completable updateSwitchFourState(String deviceId, boolean status) {
        return mUniDeviceStatesDao.updateSwitchFour(deviceId, status);
    }

    @Override
    public Completable updateLiked(List<String> deviceIds, boolean liked) {
        return mUniDeviceStatesDao.updateLiked(deviceIds, liked);
    }

    @Override
    public Completable deleteStates() {
        return mUniDeviceStatesDao.deleteAll();
    }

    @Override
    public Completable deleteStates(String deviceId) {
        return mUniDeviceStatesDao.delete(deviceId);
    }
}
