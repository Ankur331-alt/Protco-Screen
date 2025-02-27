package com.smart.rinoiot.family.adapter.listener;

import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.family.bean.WallSwitchPosition;

/**
 * @author edwin
 */
public interface HouseholdDevicesAdapterInterface {

    interface SeekBarChangeListener {
        /**
         * Invoked when the color has been changed
         * @param position the position in the list
         * @param deviceInfo the device info.
         * @param hue the color
         * @param brightness the current brightness
         */
        void onHueChange(int position, DeviceInfoBean deviceInfo, int hue, int brightness);

        /**
         * Invoked when the brightness level is changed
         * @param position the position in the list
         * @param deviceInfo the device info.
         * @param brightness the current brightness level
         */
        void onBrightnessChange(int position, DeviceInfoBean deviceInfo, int brightness);

        /**
         * Invoked when the color temperature is changed.
         * @param position the position in the list
         * @param deviceInfo the device info.
         * @param temperature the color temperature value.
         */
        void onColorTemperatureChange(int position, DeviceInfoBean deviceInfo, int temperature);
    }

    interface ItemClickListener {

        /**
         * Invoke when the item is clicked.
         * @param deviceInfo the device info.
         */
        void onClick(DeviceInfoBean deviceInfo);
    }

    interface ItemLongClickListener {

        /**
         * Invoked on item long click/press.
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
         * Invoked when the power button is toggled.
         * @param position the position in the list.
         * @param deviceInfo the device info.
         */
        void onPowerToggle(int position, DeviceInfoBean deviceInfo);

        /**
         * Invoked when the like button is toggled.
         * @param position the position in the list.
         * @param deviceInfo the device info.
         */
        void onLikeToggle(int position, DeviceInfoBean deviceInfo);

        /**
         * Invoked when a switch is toggled.
         * @param position the position in the list.
         * @param switchPos the position of the switch.
         * @param deviceInfo the device info.
         */
        void onWallSwitchToggle(int position, WallSwitchPosition switchPos, DeviceInfoBean deviceInfo);
    }

    interface TemperatureChangeListener {

        /**
         * Invoked when the temperature value is incremented.
         * @param position the position of the device in the list.
         * @param deviceInfo the device info.
         */
        void onIncrement(int position, DeviceInfoBean deviceInfo);

        /**
         * Invoked when the temperature value is decremented.
         * @param position the position of the device in the list.
         * @param deviceInfo the device info.
         */
        void onDecrement(int position, DeviceInfoBean deviceInfo);
    }
}
