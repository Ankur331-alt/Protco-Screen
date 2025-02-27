package com.smart.rinoiot.broadcastParse;

import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * @Author : Victor
 * @CreateDate : 2021/8/18 9:43
 * @Description : 解析数据实体类
 */

public class ScanRecordBean {

    // Flags of the advertising data.
    private int mAdvertiseFlags;

    // Transmission power level(in dB).
    private int mTxPowerLevel;

    // Local name of the Bluetooth LE device.
    private String mDeviceName;

    // Raw bytes of scan record.
    private byte[] mBytes;

    //Manufacturer UUID
    private String mManufacturerUuid;

    //Manufacturer ID
    private String mManufacturerId;

    //Manufacturer PID
    private String mManufacturerPid;

    //Mac Address
    private String mMacAddress;

    //ServiceUuids
    private List<String> mServiceUuids;

    public ScanRecordBean(){

    }

    public ScanRecordBean(List<String> serviceUuids,
                          String localName,
                          String manufacturerUuid,
                          String manufacturerId,
                          String manufacturerPid,
                          String macAddress,
                          int advertiseFlags,
                          int txPowerLevel,
                          byte[] bytes) {
        mServiceUuids = serviceUuids;
        mManufacturerUuid = manufacturerUuid;
        mManufacturerId = manufacturerId;
        mDeviceName = localName;
        mManufacturerPid = manufacturerPid;
        mMacAddress = macAddress;
        mAdvertiseFlags = advertiseFlags;
        mTxPowerLevel = txPowerLevel;
        mBytes = bytes;
    }

    public String getManufacturerUuid() {
        return mManufacturerUuid;
    }

    public String getManufacturerId() {
        return mManufacturerId;
    }

    public String getManufacturerPid() {
        return mManufacturerPid;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public List<String> getServiceUuids() {
        return mServiceUuids;
    }

    public int getAdvertiseFlags() {
        return mAdvertiseFlags;
    }

    public int getTxPowerLevel() {
        return mTxPowerLevel;
    }

    @Nullable
    public String getDeviceName() {
        return mDeviceName;
    }

    public byte[] getBytes() {
        return mBytes;
    }

    @NotNull
    @Override
    public String toString() {
        return "ScanRecord [mAdvertiseFlags=" + mAdvertiseFlags + ", serviceUuids=" + mServiceUuids
                + ", mDeviceName=" + mDeviceName
                + ", manufacturerUuid=" + mManufacturerUuid
                + ", manufacturerId=" + mManufacturerId
                + ", manufacturerPid=" + mManufacturerPid
                + ", macAddress=" + mMacAddress
                + ", mTxPowerLevel=" + mTxPowerLevel + "]";
    }

}
