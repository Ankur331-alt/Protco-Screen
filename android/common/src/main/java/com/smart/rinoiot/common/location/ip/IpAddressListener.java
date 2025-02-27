package com.smart.rinoiot.common.location.ip;

/**
 * @author edwin
 */
public interface IpAddressListener {
    void onSuccess(String ipAddress);
    void onError(String message);
}
