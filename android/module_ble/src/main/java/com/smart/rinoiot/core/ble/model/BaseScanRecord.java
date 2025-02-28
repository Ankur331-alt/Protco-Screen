package com.smart.rinoiot.core.ble.model;

import com.smart.rinoiot.broadcastParse.ScanRecordBean;
import com.smart.rinoiot.core.ble.utils.ByteUtils;
import java.util.List;

/**
 * @Author : Victor
 * @CreateDate : 2021/8/17 20:10
 * @Description :
 */
public abstract class BaseScanRecord {

    public final int DATA_TYPE_FLAGS = 0x01;
    public final int DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL = 0x02;
    public final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 0x03;
    public final int DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL = 0x04;
    public final int DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE = 0x05;
    public final int DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL = 0x06;
    public final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 0x07;
    public final int DATA_TYPE_LOCAL_NAME_SHORT = 0x08;
    public final int DATA_TYPE_LOCAL_NAME_COMPLETE = 0x09;
    public final int DATA_TYPE_TX_POWER_LEVEL = 0x0A;
    public final int DATA_TYPE_SERVICE_DATA = 0x16;
    public final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;

    /**
     * 截取指定位置的字节数组
     */
    public byte[] extractBytes(byte[] scanRecord, int start, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(scanRecord, start, bytes, 0, length);
        return bytes;
    }

    /**
     * Parse service UUIDs.
     */
    public void parseServiceUuid(byte[] scanRecord, int currentPos, int dataLength,
                                         int uuidLength, List<String> serviceUuids) {
        while (dataLength > 0) {
            byte[] uuidBytes = extractBytes(scanRecord, currentPos, uuidLength);
            serviceUuids.add(ByteUtils.bytes2HexStr(exchangeBytesPosition(uuidBytes)));
            dataLength -= uuidLength;
            currentPos += uuidLength;

        }
    }

    /**
     * 将byte数组转成MacAddress
     */
    public String getMacAddress(byte[] mac){

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mac.length; i++) {

            if (i != 0) {
                sb.append(":");

            }

            int temp = mac[i] & 0xff;

            String str = Integer.toHexString(temp);

            if (str.length() == 1) {
                sb.append("0").append(str);

            } else {
                sb.append(str);
            }

        }

        return sb.toString().toUpperCase();

    }

    /**
     * 交换byte数组中两个元素的位置
     */
    public byte[] exchangeBytesPosition(byte[] mBytes) {

        byte[] bytes = new byte[2];
        bytes[0] = mBytes[1];
        bytes[1] = mBytes[0];
        return bytes;
    }

    public abstract ScanRecordBean parseFromBytes(byte[] scanRecord);

}
