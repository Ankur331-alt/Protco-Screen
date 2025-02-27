package com.smart.rinoiot.common.matter;

import android.util.Log;

import com.dsh.matter.model.device.MtrDevice;
import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.utils.StringUtil;

/**
 * @author edwin
 */
public class MtrDeviceDataUtils {
    private static final String TAG = "MtrDeviceDataUtils";

    /**
     *
     */
    private static final String RINO_MTR_PRODUCT_ID_PREFIX = "matter";
    /**
     *
     */
    private static final String RINO_MTR_DEVICE_ID_PREFIX = "mt";
    /**
     *
     */
    private static final int ID_TOKEN_LENGTH = 2;

    private static final String MATTER_DEVICE_PROTOCOL = "11";

    public static String toMetaInfo(Metadata metadata){
        return new Gson().toJson(metadata);
    }

    /**
     * Builds cloud metadata from mrt device info
     *
     * @param metaInfo metadata string
     * @return the metadata
     */
    public static Metadata toMetadata(String metaInfo) {
        try {
            return new Gson().fromJson(metaInfo, Metadata.class);
        }catch (Exception exception) {
            Log.e(TAG, "toMetadata: invalid meta info" + metaInfo + ". "+ exception.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Builds cloud metadata from mrt device info
     *
     * @param mtrDevice matter device
     * @return the metadata
     */
    public static Metadata toMetadata(MtrDevice mtrDevice) {
        return new Metadata(
                mtrDevice.getId(),
                mtrDevice.getDeviceDescriptor().getDeviceType(),
                mtrDevice.getDeviceDescriptor().getVendorId(),
                mtrDevice.getDeviceDescriptor().getProductId(),
                mtrDevice.getSetupPayloadDescriptor().getVersion(),
                mtrDevice.getSetupPayloadDescriptor().getDiscriminator(),
                mtrDevice.getSetupPayloadDescriptor().getSetupPinCode(),
                mtrDevice.getSetupPayloadDescriptor().getCommissioningFlow(),
                mtrDevice.getSetupPayloadDescriptor().getHasShortDiscriminator(),
                mtrDevice.getSetupPayloadDescriptor().getDiscoveryCapabilities()
        );
    }

    /**
     * Builds a product id
     *
     * @param metadata the metadata
     * @return the product identifier
     */
    public static String toRinoProductId(Metadata metadata) {
        if(null == metadata || metadata.getDeviceType() <= 0) {
            throw new IllegalArgumentException("invalid matter data");
        }

        return RINO_MTR_PRODUCT_ID_PREFIX
                .concat("-")
                .concat(String.valueOf(metadata.getDeviceType()));
    }

    /**
     * Builds the Rino device identifier
     *
     * @param metadata metadata
     * @return the rino device identifier
     */
    public static String toRinoDeviceId(Metadata metadata){
        if(null == metadata){
            throw new IllegalArgumentException("invalid metadata");
        }

        return RINO_MTR_DEVICE_ID_PREFIX
                .concat(String.valueOf(metadata.getVendorId()))
                .concat(String.valueOf(metadata.getProductId()))
                .concat(String.valueOf(metadata.getDiscriminator()));
    }

    public static int toMtrDeviceId(String rinoDeviceId){
        if(StringUtil.strIsNull(rinoDeviceId)){
            throw new  IllegalArgumentException("invalid matter device id for rino a device");
        }
        String[] array = rinoDeviceId.split(RINO_MTR_PRODUCT_ID_PREFIX);
        if(array.length != ID_TOKEN_LENGTH){
            throw new  IllegalArgumentException("invalid matter device id for rino a device");
        }
        String productId = array[1];
        return Integer.parseInt(productId);
    }

    public static boolean isMatterDevice(DeviceInfoBean device){
        if(null == device || null == device.getProtocolType()){
            return false;
        }
        return MATTER_DEVICE_PROTOCOL.contentEquals(device.getProtocolType());
    }

    /**
     * Checks whether the metadata is empty or not.
     * @param metadata the matter device matter data.
     * @return true if empty, false otherwise.
     */
    public static boolean isEmpty(Metadata metadata) {
        if(null == metadata){
            return true;
        }
        return metadata.getDeviceId() <= 0 || metadata.getDeviceType() <= 0;
    }

    /**
     * Checks whether the metadata is not empty.
     * @param metadata the matter device matter data.
     * @return true if not empty, false otherwise.
     */
    public static boolean isNotEmpty(Metadata metadata) {
        return !isEmpty(metadata);
    }
}
