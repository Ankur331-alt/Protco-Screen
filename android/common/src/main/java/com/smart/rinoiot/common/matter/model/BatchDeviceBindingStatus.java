package com.smart.rinoiot.common.matter.model;

import java.io.Serializable;

/**
 * @author edwin
 */
public class BatchDeviceBindingStatus implements Serializable {

    public static class Discovering extends BatchDeviceBindingStatus {}

    public static class Pairing extends BatchDeviceBindingStatus {}

    public static class Paired extends BatchDeviceBindingStatus {
        private final boolean successful;

        public Paired() {
            this.successful = false;
        }

        public Paired(boolean status) {
            this.successful = status;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

    public static class Uploading extends BatchDeviceBindingStatus {}

    public static class Completed extends BatchDeviceBindingStatus {
        private final boolean successful;

        public Completed() {
            this.successful = false;
        }

        public Completed(boolean status) {
            this.successful = status;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

    public static class Error extends BatchDeviceBindingStatus {}
}
