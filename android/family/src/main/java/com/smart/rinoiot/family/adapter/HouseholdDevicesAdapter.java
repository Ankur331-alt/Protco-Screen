package com.smart.rinoiot.family.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.device.DeviceDataPoint;
import com.smart.rinoiot.common.device.UnifiedDataPoint;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.utils.ImageLoaderUtils;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.adapter.HouseholdDevicesAdapter.HouseholdDeviceViewHolder;
import com.smart.rinoiot.family.adapter.listener.HouseholdDevicesAdapterInterface.ItemClickListener;
import com.smart.rinoiot.family.adapter.listener.HouseholdDevicesAdapterInterface.ItemDoubleClickListener;
import com.smart.rinoiot.family.adapter.listener.HouseholdDevicesAdapterInterface.ItemLongClickListener;
import com.smart.rinoiot.family.adapter.listener.HouseholdDevicesAdapterInterface.SeekBarChangeListener;
import com.smart.rinoiot.family.adapter.listener.HouseholdDevicesAdapterInterface.TemperatureChangeListener;
import com.smart.rinoiot.family.adapter.listener.HouseholdDevicesAdapterInterface.ToggleButtonListener;
import com.smart.rinoiot.family.bean.WallSwitchPosition;
import com.smart.rinoiot.family.databinding.ItemHouseholdDeviceBinding;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author edwin
 */
public class HouseholdDevicesAdapter extends RecyclerView.Adapter<HouseholdDeviceViewHolder> {

    private static final String TAG = "HouseholdDevicesAdapter";

    private Context mContext;
    private List<DeviceInfoBean> mDevices;
    private ItemClickListener itemClickListener;
    private ToggleButtonListener toggleButtonListener;
    private SeekBarChangeListener seekBarChangeListener;
    private ItemLongClickListener itemLongClickListener;
    private TemperatureChangeListener temperatureChangeListener;

    private ItemDoubleClickListener itemDoubleClickListener;

    public HouseholdDevicesAdapter(Context context, List<DeviceInfoBean> mDevices) {
        this.mContext = context;
        this.mDevices = mDevices;
    }

    @NonNull
    @Override
    public HouseholdDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHouseholdDeviceBinding binding = ItemHouseholdDeviceBinding.inflate(
                LayoutInflater.from(parent.getContext())
        );
        return new HouseholdDeviceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseholdDeviceViewHolder holder, int position) {
        holder.bindData(mDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void setDevices(List<DeviceInfoBean> mDevices) {
        this.mDevices = mDevices;
    }

    /**
     * Setter for the item click listener.
     * @param listener the listener.
     */
    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * Setter for the toggle button listener
     * @param listener the listener.
     */
    public void setToggleButtonListener(ToggleButtonListener listener) {
        this.toggleButtonListener = listener;
    }

    /**
     * Setter for SeekBar change listener
     * @param listener the listener
     */
    public void setSeekBarChangeListener(SeekBarChangeListener listener) {
        this.seekBarChangeListener = listener;
    }

    /**
     * Setter for the item long click listeners
     * @param listener the listener
     */
    public void setItemLongClickListener(ItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    /**
     * Setter for the temperature change listeners
     * @param listener the listener
     */
    public void setTemperatureChangeListener(TemperatureChangeListener listener) {
        this.temperatureChangeListener = listener;
    }

    /**
     * Setter for the item double click listener
     * @param listener the listener
     */
    public void setItemDoubleClickListener(ItemDoubleClickListener listener) {
        this.itemDoubleClickListener = listener;
    }

    class HouseholdDeviceViewHolder extends RecyclerView.ViewHolder {

        ItemHouseholdDeviceBinding mBinding;

        public HouseholdDeviceViewHolder(@NonNull ItemHouseholdDeviceBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindData(DeviceInfoBean deviceInfo) {
            mBinding.tvDeviceName.setText(deviceInfo.getName());
            ImageLoaderUtils.getInstance().bindImageUrl(
                    deviceInfo.getImageUrl(), mBinding.ivDeviceIcon
            );

            Drawable likeIcon = AppCompatResources.getDrawable(
                    mContext,
                    deviceInfo.isHomeScreen() ? R.drawable.ic_liked :
                            R.drawable.ic_disliked
            );

            mBinding.ivDeviceFavIcon.setImageDrawable(likeIcon);
            if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
                boolean isPaired = MtrDeviceDataUtils.isNotEmpty(MtrDeviceDataUtils.toMetadata(
                        deviceInfo.getMetaInfo()
                ));

                mBinding.tvDeviceConfig.setText(mContext.getString(
                        isPaired ? R.string.rino_com_default_device_config_paired :
                                R.string.rino_com_default_device_config_unpaired
                ));

                mBinding.tvDeviceConfig.setTextColor(mContext.getColor(
                        isPaired ? R.color.cen_device_config_paired_color :
                                R.color.cen_connect_not_connect_color
                ));
                mBinding.tvDeviceConfig.setVisibility(View.VISIBLE);
            }else{
                mBinding.tvDeviceConfig.setVisibility(View.GONE);
            }

            // setup the listeners
            setupListeners();

            boolean onlineStatus = DeviceDataPoint.getOnlineStatus(deviceInfo.getUnifiedDataPoints());
            if(!onlineStatus){
                mBinding.ivDeviceSwitchIcon.setVisibility(View.GONE);
                mBinding.tvDeviceStatus.setText(R.string.rino_device_status_offline);
                mBinding.tvDeviceStatus.setTextColor(
                        mContext.getColor(R.color.cen_logout_start_gradient
                    ));
                return;
            }

            // display extra controls
            displayControls(deviceInfo);

            boolean powerStatus = DeviceDataPoint.getPower(deviceInfo.getUnifiedDataPoints());
            mBinding.tvDeviceStatus.setText(getStatus(powerStatus));
            mBinding.tvDeviceStatus.setTextColor(
                    mContext.getColor(R.color.cen_connect_not_connect_color
                    ));
            Drawable powerIcon = AppCompatResources.getDrawable(
                    mContext,
                    powerStatus ? R.drawable.ic_device_switch_status_on :
                            R.drawable.ic_device_switch_status_off
            );
            mBinding.ivDeviceSwitchIcon.setImageDrawable(powerIcon);
        }

        /**
         * Sets up the ui elements event listeners.
         */
        private void setupListeners() {
            // register the Seekbar listeners
            mBinding.sbBrightness.setOnSeekBarChangeListener(getBrightnessChangeListener());
            mBinding.sbColor.setOnSeekBarChangeListener(getColorChangeListener());
            mBinding.sbColorTemperature.setOnSeekBarChangeListener(getColorTempChangeListener());
            mBinding.sbSingleBrightness.setOnSeekBarChangeListener(getBrightnessChangeListener());

            // register the item click and double listeners
            mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                private long lastClickTime = 0;
                private static final long DOUBLE_CLICK_TIME_DELTA = 300;
                private Runnable singleClickRunnable;

                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) {
                        return;
                    }
                    long clickTime = System.currentTimeMillis();
                    DeviceInfoBean deviceInfo = mDevices.get(position);
                    // Check if the time between clicks is within the double click threshold
                    if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                        if (null != itemDoubleClickListener) {
                            itemDoubleClickListener.onDoubleClick(deviceInfo);
                        }
                        // Cancel any pending single click events
                        if (singleClickRunnable != null) {
                            mBinding.getRoot().removeCallbacks(singleClickRunnable);
                            singleClickRunnable = null;
                        }
                    } else if(null != itemClickListener){
                        // Handle single click event here
                        singleClickRunnable = () -> itemClickListener.onClick(deviceInfo);
                        mBinding.getRoot().postDelayed(singleClickRunnable, DOUBLE_CLICK_TIME_DELTA);
                    }
                    lastClickTime = clickTime;
                }
            });

            mBinding.getRoot().setOnLongClickListener(view-> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == itemLongClickListener){
                    return true;
                }
                itemLongClickListener.onLongClick(mDevices.get(position));
                return false;
            });

            // register the toggle listeners
            registerButtonListeners();
            registerToggleSwitchListeners();

            // register the temperature change listeners
            registerAcTemperatureChangeListeners();
        }

        /**
         * Register button listeners
         */
        private void registerButtonListeners() {
            mBinding.ivDeviceSwitchIcon.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == toggleButtonListener){
                    return;
                }
                toggleButtonListener.onPowerToggle(position, mDevices.get(position));
            });
            mBinding.ivDeviceFavIcon.setOnClickListener(view-> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == toggleButtonListener){
                    return;
                }
                toggleButtonListener.onLikeToggle(position, mDevices.get(position));
            });
        }

        /**
         * Registers the toggle switch listeners
         */
        private void registerToggleSwitchListeners(){
            mBinding.llToggleSwitchOne.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == toggleButtonListener){
                    return;
                }
                toggleButtonListener.onWallSwitchToggle(
                        position, WallSwitchPosition.First, mDevices.get(position)
                );
            });
            mBinding.llToggleSwitchTwo.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == toggleButtonListener){
                    return;
                }
                toggleButtonListener.onWallSwitchToggle(
                        position, WallSwitchPosition.Second, mDevices.get(position)
                );
            });
            mBinding.llToggleSwitchThree.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == toggleButtonListener){
                    return;
                }
                toggleButtonListener.onWallSwitchToggle(
                        position, WallSwitchPosition.Third, mDevices.get(position)
                );
            });
            mBinding.llToggleSwitchFour.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == toggleButtonListener){
                    return;
                }
                toggleButtonListener.onWallSwitchToggle(
                        position, WallSwitchPosition.Fourth, mDevices.get(position)
                );
            });
        }

        private void registerAcTemperatureChangeListeners() {
            mBinding.ivDecreaseAcTemperature.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == temperatureChangeListener){
                    return;
                }
                temperatureChangeListener.onDecrement(position, mDevices.get(position));
            });
            mBinding.ivIncreaseAcTemperature.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == temperatureChangeListener){
                    return;
                }
                temperatureChangeListener.onIncrement(position, mDevices.get(position));
            });
        }

        /**
         * Returns the brightness SeekBar change listener
         * @return the brightness SeekBar change listener
         */
        private SeekBar.OnSeekBarChangeListener getBrightnessChangeListener() {
            return new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int position = getBindingAdapterPosition();
                    if(position == RecyclerView.NO_POSITION || null == seekBarChangeListener) {
                        return;
                    }

                    int progress = seekBar.getProgress();
                    seekBarChangeListener.onBrightnessChange(position, mDevices.get(position), progress);
                }
            };
        }

        /**
         * Returns the color SeekBar change listener
         * @return the color SeekBar change listener
         */
        private SeekBar.OnSeekBarChangeListener getColorChangeListener() {
            return new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int position = getBindingAdapterPosition();
                    if(position == RecyclerView.NO_POSITION || null == seekBarChangeListener) {
                        return;
                    }
                    int progress = seekBar.getProgress();
                    int brightness = 10;
                    if(mBinding.llSingleBrightness.getVisibility() == View.VISIBLE) {
                        brightness = mBinding.sbSingleBrightness.getProgress();
                    }else if(mBinding.llSickBars.getVisibility() == View.VISIBLE) {
                        brightness = mBinding.sbBrightness.getProgress();
                    }
                    seekBarChangeListener.onHueChange(
                            position, mDevices.get(position), progress, brightness
                    );
                }
            };
        }

        /**
         * Returns the color temperature SeekBar change listener
         * @return the color temperature SeekBar change listener
         */
        private SeekBar.OnSeekBarChangeListener getColorTempChangeListener() {
            return new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int position = getBindingAdapterPosition();
                    if(position == RecyclerView.NO_POSITION || null == seekBarChangeListener) {
                        return;
                    }

                    int progress = seekBar.getProgress();
                    seekBarChangeListener.onColorTemperatureChange(position, mDevices.get(position), progress);
                }
            };
        }

        /**
         * Displays the device's controls.
         * @param deviceInfo the device info.
         */
        private void displayControls(DeviceInfoBean deviceInfo) {
            Set<UnifiedDataPoint> unifiedDataPoints = DeviceDataPoint.getUnifiedDataPoints(deviceInfo);
            if(isColorLight(unifiedDataPoints)){
                showColorLightExtraControls(deviceInfo.getUnifiedDataPoints());
            } else if(isExtraColorLight(unifiedDataPoints)) {
                showExtraColorLightExtraControls(deviceInfo.getUnifiedDataPoints());
            }else if(isDimmableLight(unifiedDataPoints)) {
                showDimmableLightExtraControls(deviceInfo.getUnifiedDataPoints());
            }else if(isTunableLight(unifiedDataPoints)){
                showTunableLightExtraControls(deviceInfo.getUnifiedDataPoints());
            } else if(isSingleSocket(unifiedDataPoints)){
                showSingleSocketExtraControls(deviceInfo.getUnifiedDataPoints());
            }else if(isMultiWaySwitch(unifiedDataPoints)) {
                showMultiWaySwitchExtraControls(
                        deviceInfo.getUnifiedDataPoints(), unifiedDataPoints.size()
                );
            }else {
                hideAllExtraControls();
            }
        }

        /**
         * Returns the device's status
         * @param powerStatus power status
         * @return teh device's status
         */
        private String getStatus(boolean powerStatus) {
            StringBuilder statusBuilder = new StringBuilder();
            if(!powerStatus) {
                statusBuilder.append(mContext.getString(
                        R.string.rino_com_device_power_status_off
                ));
                return statusBuilder.toString();
            }
            statusBuilder.append(mContext.getString(
                    R.string.rino_com_device_power_status_on
            ));
            return statusBuilder.toString();
        }

        /**
         * Displays the color light's extra controls.
         * @param unifiedDataPoints the device states
         */
        @SuppressWarnings("all")
        private void showColorLightExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
            hideAllExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            // get the brightness
            int brightness = DeviceDataPoint.getBrightness(unifiedDataPoints);
            mBinding.sbBrightness.setProgress(brightness, true);

            // update device status
            String status = mBinding.tvDeviceStatus.getText().toString();
            if(DeviceDataPoint.getPower(unifiedDataPoints)){
                mBinding.tvDeviceStatus.setText(
                        new StringBuilder().append(status)
                                .append(mContext.getString(
                                        R.string.rino_com_device_status_separator
                                )).append(brightness)
                                .append("%").toString()
                );
            }

            // no color temperature for this guy
            mBinding.sbColorTemperature.setVisibility(View.GONE);
            mBinding.vVerticalSpreader.setVisibility(View.VISIBLE);

            // get the color
            int color = DeviceDataPoint.getColor(unifiedDataPoints);
            mBinding.sbColor.setProgress(color, true);

            // at last
            mBinding.llSickBars.setVisibility(View.VISIBLE);
            mBinding.ivDeviceSwitchIcon.setVisibility(View.VISIBLE);
            mBinding.tvDeviceStatus.setVisibility(View.VISIBLE);
        }


        /**
         * Displays the color light's extra controls.
         * @param unifiedDataPoints the device states
         */
        @SuppressWarnings("all")
        private void showExtraColorLightExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
            hideAllExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            // get the brightness
            int brightness = Math.max(1, Math.min(100, DeviceDataPoint.getBrightness(unifiedDataPoints)));
            mBinding.sbBrightness.setProgress(brightness, true);

            // update device status
            String status = mBinding.tvDeviceStatus.getText().toString();
            if(DeviceDataPoint.getPower(unifiedDataPoints)){
                mBinding.tvDeviceStatus.setText(
                        new StringBuilder().append(status)
                                .append(mContext.getString(
                                        R.string.rino_com_device_status_separator
                                )).append(brightness)
                                .append("%").toString()
                );
            }

            // get the color temperature
            int colorTemperature = DeviceDataPoint.getColorTemperature(unifiedDataPoints);
            mBinding.sbColorTemperature.setProgress(colorTemperature, true);
            mBinding.sbColorTemperature.setVisibility(View.VISIBLE);

            // get the color
            int color = DeviceDataPoint.getColor(unifiedDataPoints);
            mBinding.sbColor.setProgress(color, true);
            mBinding.sbColor.setVisibility(View.VISIBLE);

            // at last
            mBinding.vVerticalSpreader.setVisibility(View.GONE);
            mBinding.llSickBars.setVisibility(View.VISIBLE);
            mBinding.ivDeviceSwitchIcon.setVisibility(View.VISIBLE);
            mBinding.tvDeviceStatus.setVisibility(View.VISIBLE);
        }

        /**
         * Displays the wall switch's extra controls
         * @param unifiedDataPoints the device states
         * @param count the switch count
         */
        private void showMultiWaySwitchExtraControls(
                Map<UnifiedDataPoint, Object> unifiedDataPoints, int count
        ) {
            hideAllExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            switch (count) {
                case 2:
                    showTwoWaySwitchExtraControls(unifiedDataPoints);
                    break;
                case 3:
                    showThreeWaySwitchExtraControls(unifiedDataPoints);
                    break;
                case 4:
                    showFourWaySwitchExtraControls(unifiedDataPoints);
                    break;
                default:
                    break;
            }

            mBinding.llSwitches.setVisibility(View.VISIBLE);
            mBinding.tvDeviceStatus.setVisibility(View.VISIBLE);
        }

        private void showTwoWaySwitchExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints){
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            // 2 way
            hideMultiWaySwitchExtraControls();
            mBinding.vStartSwitchOne.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchOne.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchTwo.setVisibility(View.VISIBLE);

            // set current states
            boolean[] switches = DeviceDataPoint.getSwitches(unifiedDataPoints);
            Drawable toggleSwitchOneIcon = getToggleSwitchIcon(
                    switches[WallSwitchPosition.First.ordinal()]
            );
            mBinding.ivToggleSwitchOne.setImageDrawable(toggleSwitchOneIcon);

            Drawable toggleSwitchTwoIcon = getToggleSwitchIcon(
                    switches[WallSwitchPosition.Second.ordinal()]
            );
            mBinding.ivToggleSwitchTwo.setImageDrawable(toggleSwitchTwoIcon);

            boolean power = switches[WallSwitchPosition.First.ordinal()] ||
                    switches[WallSwitchPosition.Second.ordinal()];
            mBinding.tvDeviceStatus.setText(getStatus(power));
        }

        private void showThreeWaySwitchExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints){
            hideMultiWaySwitchExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            // 3 way
            mBinding.vStartSwitchOne.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchOne.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchTwo.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchThree.setVisibility(View.VISIBLE);

            // set current states
            boolean[] switches = DeviceDataPoint.getSwitches(unifiedDataPoints);
            Drawable toggleSwitchOneIcon = getToggleSwitchIcon(
                    switches[WallSwitchPosition.First.ordinal()]
            );
            mBinding.ivToggleSwitchOne.setImageDrawable(toggleSwitchOneIcon);

            Drawable toggleSwitchTwoIcon = getToggleSwitchIcon(
                    switches[WallSwitchPosition.Second.ordinal()]
            );
            mBinding.ivToggleSwitchTwo.setImageDrawable(toggleSwitchTwoIcon);

            Drawable toggleSwitchThreeIcon = getToggleSwitchIcon(
                    switches[WallSwitchPosition.Third.ordinal()]
            );
            mBinding.ivToggleSwitchThree.setImageDrawable(toggleSwitchThreeIcon);

            // set power status
            boolean power = switches[WallSwitchPosition.First.ordinal()] ||
                    switches[WallSwitchPosition.Second.ordinal()] ||
                    switches[WallSwitchPosition.Third.ordinal()];
            mBinding.tvDeviceStatus.setText(getStatus(power));
        }

        private void showFourWaySwitchExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints){
            hideMultiWaySwitchExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            // show all the trinkets
            showMultiWaySwitchExtraControls();

            // set current states
            boolean[] switches = DeviceDataPoint.getSwitches(unifiedDataPoints);
            Drawable toggleSwitchOneIcon = getToggleSwitchIcon(
                    switches[WallSwitchPosition.First.ordinal()]
            );
            mBinding.ivToggleSwitchOne.setImageDrawable(toggleSwitchOneIcon);

            Drawable toggleSwitchTwoIcon = getToggleSwitchIcon(
                    switches[WallSwitchPosition.Second.ordinal()]
            );
            mBinding.ivToggleSwitchTwo.setImageDrawable(toggleSwitchTwoIcon);

            Drawable toggleSwitchThreeIcon = getToggleSwitchIcon(
                    switches[WallSwitchPosition.Third.ordinal()]
            );
            mBinding.ivToggleSwitchThree.setImageDrawable(toggleSwitchThreeIcon);

            Drawable toggleSwitchFourIcon = getToggleSwitchIcon(
                    switches[WallSwitchPosition.Fourth.ordinal()]
            );
            mBinding.ivToggleSwitchFour.setImageDrawable(toggleSwitchFourIcon);

            // set power status
            boolean power = switches[WallSwitchPosition.First.ordinal()] ||
                    switches[WallSwitchPosition.Second.ordinal()] ||
                    switches[WallSwitchPosition.Third.ordinal()] ||
                    switches[WallSwitchPosition.Fourth.ordinal()];
            mBinding.tvDeviceStatus.setText(getStatus(power));
        }

        private Drawable getToggleSwitchIcon(boolean power){
            return AppCompatResources.getDrawable(
                    mContext,
                    power ? R.drawable.ic_toggle_switch_on :
                            R.drawable.ic_toggle_switch_off
            );
        }

        private void hideMultiWaySwitchExtraControls(){
            // Hide the other controls
            mBinding.vStartSwitchOne.setVisibility(View.GONE);
            mBinding.llToggleSwitchOne.setVisibility(View.GONE);
            mBinding.vStartSwitchTwo.setVisibility(View.GONE);
            mBinding.llToggleSwitchTwo.setVisibility(View.GONE);
            mBinding.vStartSwitchThree.setVisibility(View.GONE);
            mBinding.llToggleSwitchThree.setVisibility(View.GONE);
            mBinding.vStartSwitchFour.setVisibility(View.GONE);
            mBinding.llToggleSwitchFour.setVisibility(View.GONE);
        }

        private void showMultiWaySwitchExtraControls(){
            mBinding.vStartSwitchOne.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchOne.setVisibility(View.VISIBLE);
            mBinding.vStartSwitchTwo.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchTwo.setVisibility(View.VISIBLE);
            mBinding.vStartSwitchThree.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchThree.setVisibility(View.VISIBLE);
            mBinding.vStartSwitchFour.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchFour.setVisibility(View.VISIBLE);
        }

        /**
         * Displays the single socket's extra controls
         * @param unifiedDataPoints the device states
         */
        private void showSingleSocketExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
            hideAllExtraControls();
            mBinding.vStartSwitchOne.setVisibility(View.VISIBLE);
            mBinding.llToggleSwitchOne.setVisibility(View.VISIBLE);
            mBinding.tvToggleSwitchOneLabel.setText(R.string.rino_com_default_toggle_switch_label);

            mBinding.vStartSwitchTwo.setVisibility(View.GONE);
            mBinding.llToggleSwitchTwo.setVisibility(View.GONE);
            mBinding.vStartSwitchThree.setVisibility(View.GONE);
            mBinding.llToggleSwitchThree.setVisibility(View.GONE);
            mBinding.vStartSwitchFour.setVisibility(View.GONE);
            mBinding.llToggleSwitchFour.setVisibility(View.GONE);
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            boolean power = DeviceDataPoint.getPower(unifiedDataPoints);
            Drawable toggleSwitchIcon = getToggleSwitchIcon(power);

            mBinding.ivToggleSwitchOne.setImageDrawable(toggleSwitchIcon);
            mBinding.llSwitches.setVisibility(View.VISIBLE);
            mBinding.tvDeviceStatus.setVisibility(View.VISIBLE);
        }

        /**
         * Displays the dimmable light's extra controls
         * @param unifiedDataPoints the device states
         */
        @SuppressWarnings("all")
        private void showDimmableLightExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
            hideAllExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            // get the brightness
            int brightness = Math.max(1, Math.min(100, DeviceDataPoint.getBrightness(unifiedDataPoints)));
            mBinding.sbSingleBrightness.setProgress(brightness);

            String status = mBinding.tvDeviceStatus.getText().toString();
            if(DeviceDataPoint.getPower(unifiedDataPoints)){
                mBinding.tvDeviceStatus.setText(
                        new StringBuilder().append(status)
                                .append(mContext.getString(
                                        R.string.rino_com_device_status_separator
                                )).append(brightness)
                                .append("%").toString()
                );
            }
            mBinding.ivDeviceSwitchIcon.setVisibility(View.VISIBLE);
            mBinding.tvDeviceStatus.setVisibility(View.VISIBLE);
            mBinding.llSingleBrightness.setVisibility(View.VISIBLE);
        }

        /**
         * Displays the tunable light's extra controls
         * @param unifiedDataPoints the device states
         */
        @SuppressWarnings("all")
        private void showTunableLightExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
            hideAllExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            // get the brightness
            int brightness = Math.max(1, Math.min(100, DeviceDataPoint.getBrightness(unifiedDataPoints)));
            mBinding.sbBrightness.setProgress(brightness, true);

            // update device status
            String status = mBinding.tvDeviceStatus.getText().toString();
            if(DeviceDataPoint.getPower(unifiedDataPoints)){
                mBinding.tvDeviceStatus.setText(
                        new StringBuilder().append(status)
                                .append(mContext.getString(
                                        R.string.rino_com_device_status_separator
                                )).append(brightness)
                                .append("%").toString()
                );
            }

            // get the color temperature
            int colorTemperature = DeviceDataPoint.getColorTemperature(unifiedDataPoints);
            mBinding.sbColorTemperature.setProgress(colorTemperature, true);

            // no color for this guy
            mBinding.sbColor.setVisibility(View.GONE);
            mBinding.vVerticalSpreader.setVisibility(View.VISIBLE);

            // at last
            mBinding.llSickBars.setVisibility(View.VISIBLE);
            mBinding.ivDeviceSwitchIcon.setVisibility(View.VISIBLE);
            mBinding.tvDeviceStatus.setVisibility(View.VISIBLE);
        }

        /**
         * Displays the air conditioner's extra controls
         * @param unifiedDataPoints the device states
         */
        private void showAirConditionerExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
            hideAllExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }
            // at last
            mBinding.llAirConditioner.setVisibility(View.VISIBLE);
        }

        /**
         * Displays the environment sensor's readings
         * @param unifiedDataPoints the device states
         */
        private void showEnvironmentSensorReadings(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
            hideAllExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }
            mBinding.llEnvironmentSensor.setVisibility(View.VISIBLE);
            // ToDo() get the current values for the controls
        }

        /**
         * Hides all the extra controls.
         */
        private void hideAllExtraControls(){
            mBinding.ivDeviceSwitchIcon.setVisibility(View.GONE);
            mBinding.tvDeviceStatus.setVisibility(View.INVISIBLE);
            mBinding.llSickBars.setVisibility(View.GONE);
            mBinding.llSingleBrightness.setVisibility(View.GONE);
            mBinding.llEnvironmentSensor.setVisibility(View.GONE);
            mBinding.llSwitches.setVisibility(View.GONE);
            mBinding.llAirConditioner.setVisibility(View.GONE);
        }

        /**
         * Checks if a set if data points belongs to a light
         * @param unifiedDataPoints the set of data points
         * @return true if they belong
         */
        @SuppressWarnings("all")
        private boolean isLight(Set<UnifiedDataPoint> unifiedDataPoints) {
            if(unifiedDataPoints.isEmpty()) {
                return false;
            }

            return isExtraColorLight(unifiedDataPoints) ||
                    isTunableLight(unifiedDataPoints) ||
                    isColorLight(unifiedDataPoints) ||
                    isDimmableLight(unifiedDataPoints);
        }

        /**
         * Checks if a set of data points belongs to a color light
         * @param unifiedDataPoints the set of data points
         * @return true if they belong.
         */
        private boolean isColorLight(Set<UnifiedDataPoint> unifiedDataPoints) {
            if(unifiedDataPoints.isEmpty()) {
                return false;
            }

            return unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_1_DP) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_COLOR_DP) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_BRIGHTNESS_DP) &&
                    !unifiedDataPoints.contains(UnifiedDataPoint.UNI_COLOR_TEMP_DP);
        }

        /**
         * Checks if a set of data points belongs to a color light
         * @param unifiedDataPoints the set of data points
         * @return true if they belong.
         */
        private boolean isExtraColorLight(Set<UnifiedDataPoint> unifiedDataPoints) {
            if(unifiedDataPoints.isEmpty()) {
                return false;
            }

            return unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_1_DP) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_COLOR_DP) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_BRIGHTNESS_DP) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_COLOR_TEMP_DP);
        }

        /**
         * Checks if a set of data points belongs to a dimmable light
         * @param unifiedDataPoints the set of data points
         * @return true if they belong.
         */
        private boolean isDimmableLight(Set<UnifiedDataPoint> unifiedDataPoints) {
            if(unifiedDataPoints.isEmpty()) {
                return false;
            }

            return !isColorLight(unifiedDataPoints) &&
                    !isExtraColorLight(unifiedDataPoints) &&
                    !unifiedDataPoints.contains(UnifiedDataPoint.UNI_COLOR_TEMP_DP) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_1_DP) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_BRIGHTNESS_DP);
        }

        /**
         * Checks if a set of data belongs to a tunable light bulb.
         * @param unifiedDataPoints  the set of data points
         * @return true of if they belong.
         */
        private boolean isTunableLight(Set<UnifiedDataPoint> unifiedDataPoints){
            if(unifiedDataPoints.isEmpty()) {
                return false;
            }

            return !isColorLight(unifiedDataPoints) &&
                    !isExtraColorLight(unifiedDataPoints) &&
                    !isDimmableLight(unifiedDataPoints) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_1_DP) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_BRIGHTNESS_DP) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_COLOR_TEMP_DP);
        }

        /**
         * Checks if is a set of data points belongs to a single pole socket
         * @param unifiedDataPoints the set of unified data points
         * @return true if they belong.
         */
        private boolean isSingleSocket(Set<UnifiedDataPoint> unifiedDataPoints) {
            if(unifiedDataPoints.isEmpty()) {
                return false;
            }

            return !isLight(unifiedDataPoints) &&
                    !unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_2_DP)&&
                    !unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_3_DP)&&
                    !unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_4_DP)&&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_1_DP);
        }

        /**
         * Checks if a set of data points belong to a wall switch
         * @param unifiedDataPoints the unified data points
         * @return true if they belong.
         */
        private boolean isMultiWaySwitch(Set<UnifiedDataPoint> unifiedDataPoints) {
            if(unifiedDataPoints.isEmpty()) {
                return false;
            }

            return !isLight(unifiedDataPoints) &&
                    (unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_2_DP) ||
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_3_DP) ||
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_4_DP)) &&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_1_DP);
        }

        /**
         * Checks if a set of data points belongs an environment[temperature and humidity] sensor.
         * @param unifiedDataPoints the set of data points.
         * @return true if they belong.
         */
        private boolean isEnvironmentSensor(Set<UnifiedDataPoint> unifiedDataPoints) {
            return false;
        }

        /**
         * Checks if a set of data points belongs to an air conditioner.
         * @param unifiedDataPoints the set of unified data points.
         * @return true if they belong.
         */
        private boolean isAirConditioner(Set<UnifiedDataPoint> unifiedDataPoints){
            return false;
        }
    }
}
