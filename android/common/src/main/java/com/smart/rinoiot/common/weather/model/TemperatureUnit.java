package com.smart.rinoiot.common.weather.model;

/**
 * @author edwin
 */

public enum TemperatureUnit {
    /**
     * The Fahrenheit temperature unit
     */
    Fahrenheit("°F"),

    /**
     * The Celsius temperature unit
     */
    Celsius("°C"),

    /**
     * The Kelvin temperature unit
     */
    Kelvin("K");

    /**
     * The temperature unit symbol
     */
    private final String symbol;

    TemperatureUnit(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Getter for the temperature symbol
     *
     * @return the temperature symbol
     */
    public String getSymbol() {
        return symbol;
    }
}
