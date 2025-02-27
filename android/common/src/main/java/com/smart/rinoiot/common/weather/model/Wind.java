package com.smart.rinoiot.common.weather.model;

import java.io.Serializable;

/**
 * @author edwin
 */
public class Wind implements Serializable {
    private int deg;
    private double gust;
    private double speed;

    public int getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }

    public double getGust() {
        return gust;
    }

    public void setGust(double gust) {
        this.gust = gust;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}