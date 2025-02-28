package com.smart.rinoiot.center.bean;

/**
 * @author edwin
 */
public enum CameraTiltDirection {

    /**
     * 上: top: 0
     */
    Up(0),

    /**
     * 右: right: 2
     */
    Right(2),

    /**
     * 下:  bottom: 4
     */
    Down(4),

    /**
     * 左: left: 6
     */
    Left(6);

    final int value;

    CameraTiltDirection(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
