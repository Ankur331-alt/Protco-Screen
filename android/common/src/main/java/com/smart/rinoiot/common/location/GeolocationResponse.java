package com.smart.rinoiot.common.location;

import java.io.Serializable;

public class GeolocationResponse implements Serializable {

    /**
     *  The response status
     */
    private String status;

    /**
     * The country name
     */
    private String country;

    /**
     * The country code
     */
    private String countryCode;

    /**
     * The region/state/province
     */
    private String region;

    /**
     * The region/state/province name
     */
    private String regionName;

    /**
     * The city name
     */
    private String city;

    /**
     * The geolocation's ZIP code
     */
    private String zip;

    /**
     * The geolocation's lat
     */
    private double lat;

    /**
     * The geolocation's lon
     */
    private double lon;

    /**
     * The geolocation's timezone
     */
    private String timezone;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
