package com.smart.rinoiot.common.weather.model;

import java.io.Serializable;

/**
 * @author edwin
 */
public class Clouds implements Serializable {
    private int all;

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }
}