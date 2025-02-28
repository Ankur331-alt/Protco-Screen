package com.smart.rinoiot.common.weather.model;

import java.io.Serializable;

/**
 * @author edwin
 */
public class RinoWeatherData implements Serializable {
    private int air;
    private String aqiType;
    private String cityLat;
    private String cityLon;
    private String cityName;
    private String climateType;
    private String country;
    private String countryName;
    private int humidity;
    private String humidityType;
    private double pm25;
    private String pm25Type;
    private String sunriseDate;
    private String sunsetDate;
    private double temperature;
    private String weatherDescription;
    private int weatherId;
    private String weatherMain;
    private double windSpeed;

    public int getAir() {
        return air;
    }

    public void setAir(int air) {
        this.air = air;
    }

    public String getAqiType() {
        return aqiType;
    }

    public void setAqiType(String aqiType) {
        this.aqiType = aqiType;
    }

    public String getCityLat() {
        return cityLat;
    }

    public void setCityLat(String cityLat) {
        this.cityLat = cityLat;
    }

    public String getCityLon() {
        return cityLon;
    }

    public void setCityLon(String cityLon) {
        this.cityLon = cityLon;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getClimateType() {
        return climateType;
    }

    public void setClimateType(String climateType) {
        this.climateType = climateType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getHumidityType() {
        return humidityType;
    }

    public void setHumidityType(String humidityType) {
        this.humidityType = humidityType;
    }

    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public String getPm25Type() {
        return pm25Type;
    }

    public void setPm25Type(String pm25Type) {
        this.pm25Type = pm25Type;
    }

    public String getSunriseDate() {
        return sunriseDate;
    }

    public void setSunriseDate(String sunriseDate) {
        this.sunriseDate = sunriseDate;
    }

    public String getSunsetDate() {
        return sunsetDate;
    }

    public void setSunsetDate(String sunsetDate) {
        this.sunsetDate = sunsetDate;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public String getWeatherMain() {
        return weatherMain;
    }

    public void setWeatherMain(String weatherMain) {
        this.weatherMain = weatherMain;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}
