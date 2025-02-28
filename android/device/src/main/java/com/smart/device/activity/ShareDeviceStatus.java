package com.smart.device.activity;

import com.dsh.matter.model.CommissioningErrorCode;

import java.io.Serializable;

/**
 * @author edwin
 */
public class ShareDeviceStatus implements Serializable {

    public static class Completed extends ShareDeviceStatus {
        private final String message;

        /**
         * The task is completed successfully.
         *
         * @param message a message to be displayed in the UI
         */
        public Completed(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * The task has been started but not yet completed
     */
    public static class InProgress extends ShareDeviceStatus{}

    /**
     * The task hasn't been started
     */
    public static class NotStarted extends ShareDeviceStatus{}

    public static class Failed extends ShareDeviceStatus {

        private final String message;

        private final CommissioningErrorCode code;

        /**
         * The task completed with an exception
         * @param code the error
         * @param message the error message
         */
        public Failed(CommissioningErrorCode code, String message) {
            this.code = code;
            this.message = message;
        }

        public CommissioningErrorCode getCode() {
            return code;
        }

        public String getMsg() {
            return message;
        }
    }
}
