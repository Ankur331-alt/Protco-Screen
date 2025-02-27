package com.smart.rinoiot.scene.adapter.listener;

import com.smart.rinoiot.common.bean.DeviceInfoBean;

/**
 * @author edwin
 */
public interface FavoriteDevicesAdapterInterface {

    interface SeekBarChangeListener {
        /**
         * Invoked when the color has been changed
         * @param deviceInfo the device info.
         * @param brightness the current brightness
         * @param hue the color
         */
        void onHueChange(DeviceInfoBean deviceInfo, int hue, int brightness);

        /**
         * Invoked when the brightness level is changed
         * @param deviceInfo the device info.
         * @param brightness the current brightness level
         */
        void onBrightnessChange(DeviceInfoBean deviceInfo, int brightness);

        /**
         * Invoked when the color temperature is changed.
         * @param deviceInfo the device info.
         * @param temperature the color temperature value.
         */
        void onColorTemperatureChange(DeviceInfoBean deviceInfo, int temperature);
    }

    interface ItemClickListener {

        /**
         * Invoke when the item is clicked
         * @param deviceInfo the device info.
         */
        void onClick(DeviceInfoBean deviceInfo);
    }

    interface ItemLongClickListener {

        /**
         * Invoked on item long click/press
         * @param deviceInfo the device info.
         */
        void onLongClick(DeviceInfoBean deviceInfo);
    }

    interface  ItemDoubleClickListener {

        /**
         * Invoked on item double click
         * @param deviceInfo the device info
         */
        void onDoubleClick(DeviceInfoBean deviceInfo);
    }

    interface ToggleButtonListener {

        /**
         * Invoked in the device power toggle switch is clicked
         * @param deviceInfo the device info.
         */
        void onPowerToggle(DeviceInfoBean deviceInfo);
    }
}
