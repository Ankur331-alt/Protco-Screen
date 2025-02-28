package com.smart.rinoiot.common.matter.callback;

/**
 * @author edwin
 */
public abstract class DeviceManagerCallback<T> {

    /**
     * Invoked when the management operation is completed successfully
     *
     * @param data operation data
     */
    public abstract void onSuccess(T data);

    /**
     * Invoked when there is an error during the op.
     *
     * @param error exception
     */
    public abstract void onError(Exception error);
}
