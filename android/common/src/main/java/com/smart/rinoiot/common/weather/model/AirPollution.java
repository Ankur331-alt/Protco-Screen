package com.smart.rinoiot.common.weather.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author edwin
 */
public class AirPollution implements Serializable {

    @SerializedName("main")
    private Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
