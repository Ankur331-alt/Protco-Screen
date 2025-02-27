package com.smart.rinoiot.family.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.smart.device.activity.DeviceManagerActivity;
import com.smart.device.databinding.FragmentFamilyListBinding;
import com.smart.group.manager.GroupManager;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.datastore.persistence.UnifiedDeviceState;
import com.smart.rinoiot.common.device.DeviceDataPoint;
import com.smart.rinoiot.common.event.DeviceChangeEventOrigin;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.DeviceControlManager;
import com.smart.rinoiot.common.manager.FamilyPermissionManager;
import com.smart.rinoiot.common.manager.GroupDeviceControlManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.matter.MtrPanelControlManager;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.view.TextInputDialog;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.adapter.HouseholdDevicesAdapter;
import com.smart.rinoiot.family.adapter.listener.HouseholdDevicesAdapterInterface.SeekBarChangeListener;
import com.smart.rinoiot.family.adapter.listener.HouseholdDevicesAdapterInterface.TemperatureChangeListener;
import com.smart.rinoiot.family.adapter.listener.HouseholdDevicesAdapterInterface.ToggleButtonListener;
import com.smart.rinoiot.family.bean.AirConditionerTempCtrl;
import com.smart.rinoiot.family.bean.WallSwitchPosition;
import com.smart.rinoiot.family.viewmodel.FamilyViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author author
 */
public class RoomFragment extends BaseFragment<FragmentFamilyListBinding, FamilyViewModel> {

    private static final String TAG = "RoomFragment";

    private DeviceInfoBean tempDeviceInfo = null;
    private TextInputDialog deviceNameInputDialog;
    private HouseholdDevicesAdapter deviceAdapter;
    @SuppressWarnings("all")
    private static StockFunctionListener stockFunctionListener;
    private final List<DeviceInfoBean> mRoomDevices = new ArrayList<>();

    public RoomFragment(AssetBean assetData) {
        this.mRoomDevices.addAll((null == assetData) ? new ArrayList<>() :
                assetData.getDeviceInfoBeans()
        );
    }

    @Override
    @SuppressLint({"NewApi", "NotifyDataSetChanged"})
    public void init() {
        EventBus.getDefault().register(this);
        // initialize device name input dialog
        deviceNameInputDialog = new TextInputDialog.Builder(requireContext())
                .setTitle(R.string.rino_common_modify_name)
                .setHint(R.string.rino_common_device_name)
                .setPositiveButton(R.string.rino_common_save, text -> mViewModel.updateDeviceName(
                        tempDeviceInfo, text
                ))
                .create();

        // initialize household adapter
        deviceAdapter = new HouseholdDevicesAdapter(requireContext(), this.mRoomDevices);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        SimpleItemAnimator animator = (SimpleItemAnimator) binding.recyclerView.getItemAnimator();
        if(null != animator){
            animator.setSupportsChangeAnimations(false);
        }
        binding.recyclerView.setAdapter(deviceAdapter);

        // set listeners
        deviceAdapter.setToggleButtonListener(getToggleButtonListener());
        deviceAdapter.setSeekBarChangeListener(getSeekBarChangeListener());
        deviceAdapter.setTemperatureChangeListener(getTemperatureChangeListener());
        deviceAdapter.setItemDoubleClickListener(deviceInfo -> {
            tempDeviceInfo = deviceInfo;
            deviceNameInputDialog.show();
        });
        deviceAdapter.setItemLongClickListener(this::openDeviceManager);

        if(this.mRoomDevices.isEmpty()){
            pinedEmpty(false);
        }else{
            pinedEmpty(true);
            setupDeviceStatesObserver();
        }

        // set up observers
        setupObservers();
    }

    /**
     * Sets up the observers
     */
    private void setupObservers() {
        // observe changes in device info
        mViewModel.getDeviceInfoChangeLiveData().observe(this, deviceInfo -> {
            tempDeviceInfo = null;
            updateDeviceInfo(deviceInfo);
        });
    }


    /**
     * Updates device info
     * @param deviceInfo the device info
     */
    private void updateDeviceInfo(DeviceInfoBean deviceInfo){
        if(null == deviceInfo || StringUtil.isBlank(deviceInfo.getId())){
            return;
        }

        int position = findDevicePosition(deviceInfo.getId());
        if(RecyclerView.NO_POSITION==position){
            return;
        }

        DeviceInfoBean deviceInfoBean = this.mRoomDevices.get(position);
        deviceInfoBean.setName(deviceInfo.getName());
        deviceInfoBean.setHomeScreen(deviceInfo.isHomeScreen());
        deviceAdapter.notifyItemChanged(position);
    }

    /**
     * Updates device info
     * @param deviceId the device identifier
     */
    private void updateDeviceInfo(String deviceId){
        if (StringUtil.isBlank(deviceId)) {
            return;
        }

        DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(deviceId);
        updateDeviceInfo(deviceInfo);
    }

    /**
     * Sets up the device observers for device states.
     */
    private void setupDeviceStatesObserver() {
        if(this.mRoomDevices.isEmpty()){
            return;
        }

        // the room device identifiers
        Set<String> deviceIds = this.mRoomDevices.stream()
                .map(DeviceInfoBean::getId)
                .collect(Collectors.toSet());

        // remove any dangling observers
        mViewModel.getDeviceStatesLiveData(deviceIds).removeObservers(this);

        // set fresh observers
        mViewModel.getDeviceStatesLiveData(deviceIds).observe(this, this::updateDeviceStatus);
    }

    private void updateDeviceStatus(List<UnifiedDeviceState> states) {
        if(null == states || states.isEmpty()){
            return;
        }
        states.forEach(unifiedStates -> {
            Predicate<DeviceInfoBean> deviceInfoPredicate = deviceInfo ->
                    StringUtil.isNotBlank(deviceInfo.getId()) &&
                    deviceInfo.getId().contentEquals(unifiedStates.getDeviceId());
            Optional<DeviceInfoBean> optionalFirst = mRoomDevices.parallelStream()
                    .filter(deviceInfoPredicate).findFirst();
            if(!optionalFirst.isPresent()){
                return;
            }

            DeviceInfoBean deviceInfoBean = optionalFirst.get();
            deviceInfoBean.setOnlineStatus(unifiedStates.isOnline() ? 1 :0);
            deviceInfoBean.setHomeScreen(unifiedStates.isLiked());
            deviceInfoBean.setUnifiedDataPoints(DeviceDataPoint.toUnifiedDataPoints(
                    unifiedStates
            ));
        });
        deviceAdapter.notifyItemRangeChanged(0, mRoomDevices.size());
    }

    private int findDevicePosition(String deviceId) {
        if (this.mRoomDevices.isEmpty() || StringUtil.isBlank(deviceId)) {
            return RecyclerView.NO_POSITION;
        }

        return IntStream.range(0, this.mRoomDevices.size())
                .filter(index -> {
                    DeviceInfoBean deviceInfoBean = this.mRoomDevices.get(index);
                    if(null == deviceInfoBean || StringUtil.isBlank(deviceInfoBean.getId())){
                        return false;
                    }else{
                        return deviceInfoBean.getId().contentEquals(deviceId);
                    }
                })
                .findFirst()
                .orElse(RecyclerView.NO_POSITION);
    }

    /**
     * Returns an instance of the temperature change listener
     * @return an instance of the temperature change listener
     */
    private TemperatureChangeListener getTemperatureChangeListener() {
        return new TemperatureChangeListener() {
            @Override
            public void onIncrement(int position, DeviceInfoBean deviceInfo) {
                mViewModel.adjustAirConditionerTemp(AirConditionerTempCtrl.Increment, deviceInfo);
            }

            @Override
            public void onDecrement(int position, DeviceInfoBean deviceInfo) {
                mViewModel.adjustAirConditionerTemp(AirConditionerTempCtrl.Increment, deviceInfo);
            }
        };
    }

    /**
     * Returns an instance of the seek bar change listener.
     * @return as instance of the seek bar change listener.
     */
    private SeekBarChangeListener getSeekBarChangeListener() {
        return new SeekBarChangeListener() {
            @Override
            public void onHueChange(int position, DeviceInfoBean deviceInfo, int hue, int brightness) {
                mViewModel.setDeviceColor(deviceInfo, hue, brightness);
            }

            @Override
            public void onBrightnessChange(int position, DeviceInfoBean deviceInfo, int brightness) {
                mViewModel.setDeviceBrightness(deviceInfo, brightness);
            }

            @Override
            public void onColorTemperatureChange(
                    int position, DeviceInfoBean deviceInfo, int temperature
            ) {
                mViewModel.setDeviceColorTemp(deviceInfo, temperature);
            }
        };
    }

    /**
     * Returns an instance of the toggle button listener
     * @return the toggle button listener.
     */
    private ToggleButtonListener getToggleButtonListener() {
        return new ToggleButtonListener() {
            @Override
            public void onPowerToggle(int position, DeviceInfoBean deviceInfo) {
                mViewModel.toggleDevicePower(deviceInfo, position);
            }

            @Override
            public void onLikeToggle(int position, DeviceInfoBean deviceInfo) {
                mViewModel.toggleDeviceLikeStatus(deviceInfo);
            }

            @Override
            public void onWallSwitchToggle(
                    int position, WallSwitchPosition switchPos, DeviceInfoBean deviceInfo
            ) {
                mViewModel.toggleDeviceSwitches(deviceInfo, switchPos);
            }
        };
    }

    /**
     * Opens the device manager.
     * @param deviceInfoBean the device info.
     */
    private void openDeviceManager(DeviceInfoBean deviceInfoBean) {
        boolean hasPermission = FamilyPermissionManager.getInstance().getPermissionMemberRole(
                getContext(), null
        );
        if (!hasPermission) {
            DialogUtil.showNormalPermissionMsg((AppCompatActivity) getContext());
            return;
        }
        startActivity(new Intent(requireActivity(), DeviceManagerActivity.class)
                .putExtra(Constant.DEV_ID, deviceInfoBean.getId())
                .putExtra(Constant.GROUP_ID, deviceInfoBean.getGroupId()));
    }


    /**
     * Opens the device or device group's control panel
     * @param deviceInfoBean the device info
     */
    private void openControlPanel(DeviceInfoBean deviceInfoBean) {
        if (!isAdded()) {
            return;
        }

        if (deviceInfoBean.isCustomGroup()) {
            //群组设备
            if (deviceInfoBean.isGroupOffLineFlag()) {
                //群组下没有设备
                GroupManager.getInstance().showNormalSheet(requireActivity(), deviceInfoBean);
            } else {
                GroupDeviceControlManager.getInstance().gotoPanel(
                        requireContext(), deviceInfoBean
                );
            }
        } else if(MtrDeviceDataUtils.isMatterDevice(deviceInfoBean)) {
            MtrPanelControlManager.getInstance().goToPanel(requireContext(), deviceInfoBean);
        } else {
            DeviceControlManager.getInstance().gotoPanel(
                    requireContext(), deviceInfoBean, false
            );
        }
    }

    @Override
    public FragmentFamilyListBinding getBinding(LayoutInflater inflater) {
        return FragmentFamilyListBinding.inflate(inflater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * mqtt上报修改状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("all")
    public void onEventBusNotify(DeviceEvent deviceEvent) {
        Log.d(TAG, "onEventBusNotify: device changed=" + new Gson().toJson(deviceEvent));
        if(deviceEvent.getType() == DeviceEvent.Type.DEVICE_INFO_CHANGED) {
            if(!DeviceChangeEventOrigin.HOUSEHOLD.equals(deviceEvent.getObj())){
                String deviceId = deviceEvent.getMsg();
                updateDeviceInfo(deviceId);
            }
        }
    }

    /**
     * 根据权限及是否有数据展示空布局
     */
    private void pinedEmpty(boolean isHide) {
        boolean permissionMemberRole = FamilyPermissionManager.getInstance()
                .getPermissionMemberRole(getContext(), null);
    }

    public static void setStockFunctionListener(StockFunctionListener listener) {
        stockFunctionListener = listener;
    }

    public interface StockFunctionListener {

        /**
         * The stock function callback
         *
         * @param deviceInfoBean device information
         */
        void stockFunCallBack(DeviceInfoBean deviceInfoBean);
    }
}