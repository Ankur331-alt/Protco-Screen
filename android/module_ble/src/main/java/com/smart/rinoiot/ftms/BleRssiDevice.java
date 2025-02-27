package com.smart.rinoiot.ftms;

import com.smart.rinoiot.broadcastParse.ScanRecordBean;
import com.smart.rinoiot.core.ble.model.BleDevice;

import org.jetbrains.annotations.NotNull;

public class BleRssiDevice extends BleDevice {

    private ScanRecordBean manufacturerInfoScanRecord;
    private int rssi;
    private long rssiUpdateTime;

    private int stepNumber;
    private int heartRate;

    public ScanRecordBean getManufacturerInfoScanRecord() {
        return manufacturerInfoScanRecord;
    }

    public void setManufacturerInfoScanRecord(ScanRecordBean myScanRecord) {
        this.manufacturerInfoScanRecord = myScanRecord;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }


    public BleRssiDevice(String address, String name) {
        super(address, name);
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getRssiUpdateTime() {
        return rssiUpdateTime;
    }

    public void setRssiUpdateTime(long rssiUpdateTime) {
        this.rssiUpdateTime = rssiUpdateTime;
    }

    @Override
    public @NotNull String toString() {
        return "BleRssiDevice{" +
                " rssi=" + rssi +
                ", rssiUpdateTime=" + rssiUpdateTime +
                '}';
    }

}
