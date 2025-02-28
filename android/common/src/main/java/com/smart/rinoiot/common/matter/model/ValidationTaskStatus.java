package com.smart.rinoiot.common.matter.model;

import java.io.Serializable;

/**
 * @author edwin
 */
public class ValidationTaskStatus implements Serializable {

    /**
     * Indicates that the task has failed
     */
    public static class Failed extends ValidationTaskStatus {

        /**
         * Failure message
         */
        private final String msg;

        public Failed(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * Indicates that the task has passed
     */
    public static class Passed extends ValidationTaskStatus {

        /**
         * Validated data
         */
        private final String data;

        public Passed(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }
}
