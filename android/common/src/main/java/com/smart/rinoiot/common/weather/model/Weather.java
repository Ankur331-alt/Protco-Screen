package com.smart.rinoiot.common.weather.model;

import java.io.Serializable;

/**
 * @author edwin
 */
public class Weather implements Serializable {
    private int id;
    private String main;
    private String icon;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}