package com.smart.rinoiot.center.bean;

import java.io.Serializable;

/**
 * @author edwin
 * Since we have no idea what a camera schema looks like at this moment.  This is just a stub class.
 * ToDo() It's attribute/properties should be replaced with the actual properties
 */
public class IpCameraBean implements Serializable {

    private String id;

    private String name;

    private String ipAddress;

    private String thumbnail;

    public IpCameraBean(String id, String name, String ipAddress, String thumbnail) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
