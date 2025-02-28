package com.smart.rinoiot.common.weather.model;

import java.io.Serializable;

/**
 * @author edwin
 */
public class Coordinates implements Serializable {
    private final double lon;

    private final double lat;

    public Coordinates(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}
