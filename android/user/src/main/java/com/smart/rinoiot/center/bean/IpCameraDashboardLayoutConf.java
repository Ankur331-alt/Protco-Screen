package com.smart.rinoiot.center.bean;

/**
 * @author edwin
 */
public class IpCameraDashboardLayoutConf {

    public static final IpCameraDashboardLayoutConf CONF_ONE = new IpCameraDashboardLayoutConf(
            1
    );
    public static final IpCameraDashboardLayoutConf CONF_TWO = new IpCameraDashboardLayoutConf(
            2
    );
    public static final IpCameraDashboardLayoutConf CONF_THREE = new IpCameraDashboardLayoutConf(
            3
    );
    public static final IpCameraDashboardLayoutConf CONF_FOUR = new IpCameraDashboardLayoutConf(
            4
    );
    public static final IpCameraDashboardLayoutConf CONF_FIVE = new IpCameraDashboardLayoutConf(
            5
    );
    public static final IpCameraDashboardLayoutConf CONF_SIX = new IpCameraDashboardLayoutConf(
            6
    );
    public static final IpCameraDashboardLayoutConf CONF_SEVEN = new IpCameraDashboardLayoutConf(
            7
    );
    public static final IpCameraDashboardLayoutConf CONF_EIGHT = new IpCameraDashboardLayoutConf(
            8
    );

    private final int value;

    public IpCameraDashboardLayoutConf(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
