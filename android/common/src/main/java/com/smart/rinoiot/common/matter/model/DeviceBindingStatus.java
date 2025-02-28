package com.smart.rinoiot.common.matter.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * @author edwin
 */
public class DeviceBindingStatus implements Serializable {

    public static class Success<T> extends DeviceBindingStatus {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static class InProgress extends DeviceBindingStatus {}

    public static class Found extends DeviceBindingStatus{}

    public static class Failed extends DeviceBindingStatus {

        private final String msg;

        private final DeviceBindingErrorCode code;

        public Failed(DeviceBindingErrorCode code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public DeviceBindingErrorCode getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
