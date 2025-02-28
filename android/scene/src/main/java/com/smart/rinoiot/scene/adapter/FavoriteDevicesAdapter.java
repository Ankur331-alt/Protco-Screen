package com.smart.rinoiot.scene.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.smart.rinoiot.common.utils.ImageLoaderUtils;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.adapter.FavoriteDevicesAdapter.FavoriteDeviceViewHolder;
import com.smart.rinoiot.scene.adapter.listener.FavoriteDevicesAdapterInterface.ItemClickListener;
import com.smart.rinoiot.scene.adapter.listener.FavoriteDevicesAdapterInterface.ItemDoubleClickListener;
import com.smart.rinoiot.scene.adapter.listener.FavoriteDevicesAdapterInterface.ItemLongClickListener;
import com.smart.rinoiot.scene.adapter.listener.FavoriteDevicesAdapterInterface.SeekBarChangeListener;
import com.smart.rinoiot.scene.adapter.listener.FavoriteDevicesAdapterInterface.ToggleButtonListener;
import com.smart.rinoiot.scene.databinding.ItemFavoriteDeviceBinding;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author edwin
 */
public class FavoriteDevicesAdapter extends RecyclerView.Adapter<FavoriteDeviceViewHolder> {
    private final Context mContext;
    private List<DeviceInfoBean> mDevices;
    private ItemClickListener itemClickListener;
    private ToggleButtonListener toggleButtonListener;
    private SeekBarChangeListener seekBarChangeListener;
    private ItemLongClickListener itemLongClickListener;

    private ItemDoubleClickListener itemDoubleClickListener;

    private static final String TAG = "FavoriteDevicesAdapter";

    public FavoriteDevicesAdapter(Context context, List<DeviceInfoBean> mDevices) {
        this.mContext = context;
        this.mDevices = mDevices;
    }

    @NonNull
    @Override
    public FavoriteDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteDeviceBinding binding = ItemFavoriteDeviceBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new FavoriteDeviceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteDeviceViewHolder holder, int position) {
        holder.bindData(this.mDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return (null == this.mDevices) ? 0 : this.mDevices.size();
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setToggleButtonListener(ToggleButtonListener listener) {
        this.toggleButtonListener = listener;
    }

    public void setSeekBarChangeListener(SeekBarChangeListener listener) {
        this.seekBarChangeListener = listener;
    }

    public void setItemLongClickListener(ItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public void setItemDoubleClickListener(ItemDoubleClickListener listener) {
        this.itemDoubleClickListener = listener;
    }

    class FavoriteDeviceViewHolder extends RecyclerView.ViewHolder {
        ItemFavoriteDeviceBinding mBinding;

        public FavoriteDeviceViewHolder(@NonNull ItemFavoriteDeviceBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        private void bindData(DeviceInfoBean deviceInfo) {
            ImageLoaderUtils.getInstance().bindImageUrl(
                    deviceInfo.getImageUrl(), mBinding.ivDeviceIcon
            );

            mBinding.tvDeviceName.setText(deviceInfo.getName());

            // setup the listeners
            setupListeners();

            // check online status
            boolean onlineStatus = DeviceDataPoint.getOnlineStatus(deviceInfo.getUnifiedDataPoints());
            if(!onlineStatus){
                mBinding.ivDeviceSwitchIcon.setVisibility(View.GONE);
                mBinding.tvDeviceStatus.setText(R.string.rino_device_status_offline);
                mBinding.tvDeviceStatus.setTextColor(
                        mContext.getColor(R.color.cen_logout_start_gradient
                        ));
                mBinding.llSeekBars.setVisibility(View.GONE);

                Drawable background = AppCompatResources.getDrawable(
                        mContext, R.drawable.shape_favorite_device_off_bg
                );
                mBinding.llParent.setBackground(background);
                return;
            }

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

            Drawable background = AppCompatResources.getDrawable(
                    mContext,
                    powerStatus ? R.drawable.shape_favorite_device_on_bg :
                            R.drawable.shape_favorite_device_off_bg
            );
            mBinding.llParent.setBackground(background);

            // display extra controls
             displayControls(deviceInfo);
        }

        private void setupListeners() {
            // register seek bar listeners
            mBinding.sbColor.setOnSeekBarChangeListener(getColorChangeListener());
            mBinding.sbBrightness.setOnSeekBarChangeListener(getBrightnessChangeListener());
            mBinding.sbColorTemperature.setOnSeekBarChangeListener(getColorTempChangeListener());

            // register item click  and double listener
            mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                private long lastClickTime = 0;
                private Runnable singleClickRunnable;
                private static final long DOUBLE_CLICK_TIME_DELTA = 300;

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
                    } else if (null != itemClickListener){
                        // Handle single click event here
                        singleClickRunnable = () -> itemClickListener.onClick(deviceInfo);
                        mBinding.getRoot().postDelayed(singleClickRunnable, DOUBLE_CLICK_TIME_DELTA);
                    }
                    lastClickTime = clickTime;
                }
            });

            // register item long click listener
            mBinding.getRoot().setOnLongClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == itemLongClickListener){
                    return false;
                }
                itemLongClickListener.onLongClick(mDevices.get(position));
                return false;
            });

            // register device power toggle button listener
            mBinding.ivDeviceSwitchIcon.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if(position == RecyclerView.NO_POSITION || null == toggleButtonListener){
                    return;
                }
                toggleButtonListener.onPowerToggle(mDevices.get(position));
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
                    seekBarChangeListener.onBrightnessChange(mDevices.get(position), progress);
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
                    int brightness = 100;
                    if(mBinding.llSeekBars.getVisibility() == View.VISIBLE) {
                        brightness = mBinding.sbBrightness.getProgress();
                    }
                    int progress = seekBar.getProgress();
                    seekBarChangeListener.onHueChange(mDevices.get(position), progress, brightness);
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
                    seekBarChangeListener.onColorTemperatureChange(mDevices.get(position), progress);
                }
            };
        }


        /**
         * Returns the device's status
         * @param powerStatus power status
         * @return teh device's status
         */
        private String getStatus(boolean powerStatus) {
            StringBuilder statusBuilder = new StringBuilder();
            if(!powerStatus) {
                statusBuilder.append(mBinding.getRoot().getContext().getString(
                        R.string.rino_com_device_power_status_off
                ));
                return statusBuilder.toString();
            }
            statusBuilder.append(mBinding.getRoot().getContext().getString(
                    R.string.rino_com_device_power_status_on
            ));
            return statusBuilder.toString();
        }

        /**
         * Displays the device's controls.
         * @param deviceInfo the device info.
         */
        private void displayControls(DeviceInfoBean deviceInfo) {
            Set<UnifiedDataPoint> unifiedDataPoints = DeviceDataPoint.getUnifiedDataPoints(deviceInfo);
            if(isExtraColorLight(unifiedDataPoints)) {
                showColorLightExtraControls(deviceInfo.getUnifiedDataPoints());
            }else if(isSingleSocket(unifiedDataPoints)){
                showSingleSocketExtraControls(deviceInfo.getUnifiedDataPoints());
            }else {
                hideAllExtraControls();
            }
        }

        /**
         * Displays the single socket's extra controls
         * @param unifiedDataPoints the device states
         */
        private void showSingleSocketExtraControls(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
            hideAllExtraControls();
            if(unifiedDataPoints.isEmpty()){
                return;
            }

            mBinding.tvDeviceStatus.setVisibility(View.VISIBLE);
            mBinding.ivDeviceSwitchIcon.setVisibility(View.VISIBLE);
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
            int brightness = Math.max(1, Math.min(100, DeviceDataPoint.getBrightness(unifiedDataPoints)));
            mBinding.sbBrightness.setProgress(brightness, true);

            // update device status
            String status = mBinding.tvDeviceStatus.getText().toString();
            if(DeviceDataPoint.getPower(unifiedDataPoints)){
                mBinding.tvDeviceStatus.setText(
                        new StringBuilder().append(status)
                                .append(mBinding.getRoot().getContext().getString(
                                        R.string.rino_com_device_status_separator
                                )).append(brightness)
                                .append("%").toString()
                );
            }

            // get the color temperature
            int colorTemperature = DeviceDataPoint.getColorTemperature(unifiedDataPoints);
            mBinding.sbColorTemperature.setProgress(colorTemperature, true);

            // get the color
            int color = DeviceDataPoint.getColor(unifiedDataPoints);
            mBinding.sbColor.setProgress(color, true);

            // at last
            mBinding.llSeekBars.setVisibility(View.VISIBLE);
            mBinding.tvDeviceStatus.setVisibility(View.VISIBLE);
            mBinding.ivDeviceSwitchIcon.setVisibility(View.VISIBLE);
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
         * Checks if is a set of data points belongs to a single pole socket
         * @param unifiedDataPoints the set of unified data points
         * @return true if they belong.
         */
        private boolean isSingleSocket(Set<UnifiedDataPoint> unifiedDataPoints) {
            if(unifiedDataPoints.isEmpty()) {
                return false;
            }

            return !isExtraColorLight(unifiedDataPoints) &&
                    !unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_2_DP)&&
                    !unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_3_DP)&&
                    !unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_4_DP)&&
                    unifiedDataPoints.contains(UnifiedDataPoint.UNI_SWITCH_1_DP);
        }

        /**
         * Hides all the extra controls.
         */
        private void hideAllExtraControls(){
            mBinding.ivDeviceSwitchIcon.setVisibility(View.GONE);
            mBinding.llSeekBars.setVisibility(View.GONE);
            mBinding.tvDeviceStatus.setVisibility(View.INVISIBLE);
        }
    }
}