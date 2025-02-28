package com.smart.device.activity;

import androidx.annotation.Nullable;

/**
 * @author edwin
 */
public class ShareDeviceMethod {
    public static final String SHARE_METHOD_KEY = "share-method";

    public static final String DEVICE_ID_KEY = "device-identifier";
    public static final ShareDeviceMethod PIN_CODE = new ShareDeviceMethod("pin-code");
    public static final ShareDeviceMethod QR_CODE = new ShareDeviceMethod("qr-code");

    private final String value;

    public ShareDeviceMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(null == obj){
            return false;
        }

        if(!(obj instanceof ShareDeviceMethod)){
            return false;
        }

        String that = ((ShareDeviceMethod) obj).getValue();
        return this.value.contentEquals(that);
    }
}
