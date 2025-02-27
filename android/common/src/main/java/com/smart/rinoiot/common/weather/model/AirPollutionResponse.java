package com.smart.rinoiot.common.weather.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author edwin
 */
public class AirPollutionResponse implements Serializable {
    @SerializedName("coord")
    private Coordinates coordinates;

    @SerializedName("list")
    private List<AirPollution> pollution;

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public List<AirPollution> getPollution() {
        return pollution;
    }

    public void setPollution(List<AirPollution> pollution) {
        this.pollution = pollution;
    }
}
