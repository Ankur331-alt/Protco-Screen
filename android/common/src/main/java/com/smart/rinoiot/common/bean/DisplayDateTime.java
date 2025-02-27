package com.smart.rinoiot.common.bean;

/**
 * @author edwin
 */
public class DisplayDateTime {
    private final String date;

    private final String time;

    public DisplayDateTime(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
