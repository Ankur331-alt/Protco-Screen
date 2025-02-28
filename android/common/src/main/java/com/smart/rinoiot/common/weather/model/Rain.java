package com.smart.rinoiot.common.weather.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author edwin
 */
public class Rain implements Serializable {
    @SerializedName("1h")
    private double oneHour;

    public double getOneHour() {
        return oneHour;
    }

    public void setOneHour(double oneHour) {
        this.oneHour = oneHour;
    }
}