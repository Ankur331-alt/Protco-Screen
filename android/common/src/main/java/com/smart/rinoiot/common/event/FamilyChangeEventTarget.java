package com.smart.rinoiot.common.event;

import androidx.annotation.Nullable;

/**
 * @author edwin
 */
public class FamilyChangeEventTarget {

    public static final FamilyChangeEventTarget REFRESH_SCENES = new FamilyChangeEventTarget(
            "refresh-scenes"
    );
    public static final FamilyChangeEventTarget REFRESH_DEVICES = new FamilyChangeEventTarget(
            "refresh-devices"
    );

    private final String value;

    public FamilyChangeEventTarget(String value) {
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

        if(!(obj instanceof FamilyChangeEventTarget)){
            return false;
        }

        String that = ((FamilyChangeEventTarget) obj).getValue();
        return this.value.contentEquals(that);
    }
}
