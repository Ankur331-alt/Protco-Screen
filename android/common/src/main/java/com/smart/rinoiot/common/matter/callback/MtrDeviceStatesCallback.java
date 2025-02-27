package com.smart.rinoiot.common.matter.callback;

import com.dsh.matter.model.device.StateAttribute;

import java.util.HashMap;

/**
 * @author edwin
 */
public abstract class MtrDeviceStatesCallback {
    /**
     * Reports device states
     *
     * @param states device status
     */
    public abstract void onReport(HashMap<StateAttribute, Object> states);
}
