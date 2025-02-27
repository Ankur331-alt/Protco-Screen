package com.smart.rinoiot.scene.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.base.BaseViewPagerAdapter;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.DisplayDateTime;
import com.smart.rinoiot.common.datastore.persistence.WeatherData;
import com.smart.rinoiot.common.event.DeviceChangeEventOrigin;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.event.FamilyChangeEventTarget;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.DateUtils;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.view.TextInputDialog;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.activity.CreateSceneActivity;
import com.smart.rinoiot.scene.adapter.FavoriteDevicesAdapter;
import com.smart.rinoiot.scene.adapter.listener.FavoriteDevicesAdapterInterface.SeekBarChangeListener;
import com.smart.rinoiot.scene.databinding.FragmentSceneBinding;
import com.smart.rinoiot.scene.viewmodel.SceneViewModel;

import net.lucode.hackware.magicindicator.ViewPagerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author author
 */
public class SceneFragment extends BaseFragment<FragmentSceneBinding, SceneViewModel> {
    /**
     * favorite devices adapter
     */
    private FavoriteDevicesAdapter favoriteDevicesAdapter;

    private final List<DeviceInfoBean> mFavoriteDevices = new ArrayList<>();

    private final List<DeviceInfoBean> mRoomDevices = new ArrayList<>();

    private final List<AssetBean> mRooms = new ArrayList<>();

    private static final String TAG = "SceneFragment";

    private TextInputDialog deviceNameDialog;

    private DeviceInfoBean tempDeviceInfo = null;

    private String mFavoriteRoomId = "";

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        EventBus.getDefault().register(this);
        String[] mTitleDataList = {
                getString(R.string.rino_com_tap_to_run_scene),
                getString(R.string.rino_com_automated_scene)
        };
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ManualFragment());
        fragments.add(new AutoFragment());

        // initialize the date and time;
        setDateData();

        // setup modify device name dialog.
        deviceNameDialog = new TextInputDialog.Builder(requireContext())
                .setTitle(R.string.rino_common_modify_name)
                .setHint(R.string.rino_common_device_name)
                .setPositiveButton(R.string.rino_common_save, text -> mViewModel.updateDeviceName(
                        tempDeviceInfo, text
                ))
                .create();

        BaseViewPagerAdapter baseViewPagerAdapter = new BaseViewPagerAdapter(
                getChildFragmentManager(), fragments
        );

        binding.pager.setAdapter(baseViewPagerAdapter);
        binding.pager.addOnPageChangeListener(getOnPageChangeListener());
        binding.magicIndicator.setNavigator(
                mViewModel.createIndicator(binding.pager, mTitleDataList)
        );
        ViewPagerHelper.bind(binding.magicIndicator, binding.pager);

        binding.wpRooms.setOnWheelChangeListener((item, position) -> {
            String roomId = mRooms.get(position).getId();
            String homeId = CacheDataManager.getInstance().getCurrentHomeId();
            mViewModel.fetchRoomDevices(homeId, roomId);
        });

        // Set StaggeredGridLayoutManager with required number of columns
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                3, StaggeredGridLayoutManager.VERTICAL
        );
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvFavoriteDevices.setLayoutManager(layoutManager);
        binding.rvFavoriteDevices.setHasFixedSize(false);
        SimpleItemAnimator animator = (SimpleItemAnimator) binding.rvFavoriteDevices.getItemAnimator();
        if(null != animator){
            animator.setSupportsChangeAnimations(false);
        }

        // init favorite devices adapter
        favoriteDevicesAdapter = new FavoriteDevicesAdapter(requireContext(), mFavoriteDevices);
        setupFavDeviceAdapterListeners();

        binding.rvFavoriteDevices.setItemAnimator(new DefaultItemAnimator());
        binding.rvFavoriteDevices.setAdapter(favoriteDevicesAdapter);

        binding.ivAdd.setOnClickListener(v ->
                startActivity(new Intent(getContext(), CreateSceneActivity.class))
        );
        binding.refreshLayout.setOnRefreshListener(() -> {
            binding.refreshLayout.setRefreshing(false);
            loadSceneData();
            loadFavoriteRooms();
        });

        mViewModel.getFavoriteRoomsLiveData().observe(this, favoriteRooms -> {
            mRooms.clear();
            updateFavoriteRoomsWheel(favoriteRooms);
        });

        mViewModel.getDisplayDateTimeLiveData().observe(this, this::updateDisplayDateTime);
        mViewModel.getDeviceInfoChangeLiveData().observe(this, this::updateDeviceInfo);
        mViewModel.getWeatherDataLiveData().observe(this, this::updateWeatherData);
        mViewModel.getRoomDevicesLiveData().observe(this, roomDevices-> {
            // add new device observers
            mRoomDevices.clear();
            mRoomDevices.addAll(roomDevices);
            mViewModel.subscribeToDeviceStates(mRoomDevices);
        });

        mViewModel.getFavoriteDevicesLiveData().observe(this, this::updateFavoriteDevices);
    }

    private void updateWeatherData(WeatherData data){
        binding.tvCurrentAirQuality.setText(String.valueOf(data.getAirQualityIndex()));
        binding.tvCurrentTemperature.setText(String.format(getString(
                R.string.rino_com_temperature_value_format), data.getTemperature()
        ));
        binding.tvCurrentWindSpeed.setText(String.format(getString(
                R.string.rino_com_wind_speed_value_format), data.getWindSpeed()
        ));
        binding.tvCurrentHumidity.setText(String.format(getString(
                R.string.rino_com_humidity_value_format), data.getHumidity()
        ));
    }

    /**
     * Updates wheel picker room list
     * @param favoriteRooms the current rooms
     */
    @SuppressWarnings("all")
    private void updateFavoriteRoomsWheel(List<AssetBean> favoriteRooms) {
        mRooms.addAll(favoriteRooms);
        if(mRooms.isEmpty()){
            showPlaceholderView();
            mFavoriteRoomId = "";
        }else{
            showFavoriteRoomsView();
            binding.wpRooms.setCyclic(false);
            binding.wpRooms.setDataList(mRooms);
            mFavoriteRoomId = mRooms.get(0).getId();
            if(mRooms.size() > 3){
                mFavoriteRoomId = mRooms.get(2).getId();
            }
            int position = getFavoriteRoomPosition();
            Log.e(TAG, "updateFavoriteRoomsWheel: position" + position);
            binding.wpRooms.setCurrentPosition(position, true);
            String homeId = CacheDataManager.getInstance().getCurrentHomeId();
            mViewModel.fetchRoomDevices(homeId, mFavoriteRoomId);
        }
    }

    private int getFavoriteRoomPosition(){
        return IntStream.range(0, mRooms.size())
                .filter(index -> {
                    AssetBean room = mRooms.get(index);
                    if(null == room || StringUtil.isBlank(room.getId())){
                        return false;
                    }
                    return room.getId().contentEquals(mFavoriteRoomId);
                })
                .findFirst()
                .orElse(0);
    }

    /**
     * Shows the placeholder view
     */
    private void showPlaceholderView() {
        // show the add button
        binding.wpRooms.setVisibility(View.GONE);
        binding.rvFavoriteDevices.setVisibility(View.GONE);
        binding.llAddRoomBtn.setVisibility(View.VISIBLE);
        binding.tvNoDevicesLabel.setVisibility(View.VISIBLE);
        binding.ivFavoriteDevices.setVisibility(View.VISIBLE);
    }


    /**
     * Shows the favorite rooms view
     */
    private void showFavoriteRoomsView() {
        binding.tvNoDevicesLabel.setVisibility(View.GONE);
        binding.llAddRoomBtn.setVisibility(View.GONE);
        binding.wpRooms.setVisibility(View.VISIBLE);
    }

    private void showFavoriteDevicesPlaceholder() {
        binding.rvFavoriteDevices.setVisibility(View.GONE);
        binding.ivFavoriteDevices.setVisibility(View.VISIBLE);
    }

    private void hideFavoriteDevicesPlaceholder(){
        binding.rvFavoriteDevices.setVisibility(View.VISIBLE);
        binding.ivFavoriteDevices.setVisibility(View.GONE);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateFavoriteDevices(List<DeviceInfoBean> favoriteDevices) {
        mFavoriteDevices.clear();
        if(favoriteDevices.isEmpty()){
            favoriteDevicesAdapter.notifyDataSetChanged();
            showFavoriteDevicesPlaceholder();
        }else{
            mFavoriteDevices.addAll(favoriteDevices);
            favoriteDevicesAdapter.notifyDataSetChanged();
            hideFavoriteDevicesPlaceholder();
        }
    }

    /**
     * Updates device info
     * @param deviceInfo the device info
     */
    @SuppressWarnings("all")
    private void updateDeviceInfo(DeviceInfoBean deviceInfo) {
        tempDeviceInfo = null;
        if(null == deviceInfo || StringUtil.isBlank(deviceInfo.getId())){
            return;
        }

        int position = findDevicePosition(deviceInfo.getId());
        if(RecyclerView.NO_POSITION == position){
            return;
        }

        DeviceInfoBean deviceInfoBean = mFavoriteDevices.get(position);
        deviceInfoBean.setName(deviceInfo.getName());
        favoriteDevicesAdapter.notifyDataSetChanged();
    }

    private int findDevicePosition(String deviceId) {
        if (mFavoriteDevices.isEmpty() || StringUtil.isBlank(deviceId)) {
            return RecyclerView.NO_POSITION;
        }

        return IntStream.range(0, mFavoriteDevices.size())
                .filter(index -> {
                    DeviceInfoBean deviceInfoBean = mFavoriteDevices.get(index);
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
     * Initializes the favorite devices adapter and its trinkets
     */
    private void setupFavDeviceAdapterListeners() {
        if(null == favoriteDevicesAdapter) {
            return;
        }

        favoriteDevicesAdapter.setSeekBarChangeListener(new SeekBarChangeListener() {
            @Override
            public void onHueChange(DeviceInfoBean deviceInfo, int hue, int brightness) {
                mViewModel.setDeviceColor(deviceInfo, hue, brightness);
            }

            @Override
            public void onBrightnessChange(DeviceInfoBean deviceInfo, int brightness) {
                mViewModel.setDeviceBrightness(deviceInfo, brightness);
            }

            @Override
            public void onColorTemperatureChange(DeviceInfoBean deviceInfo, int temperature) {
                mViewModel.setDeviceColorTemp(deviceInfo, temperature);
            }
        });

        favoriteDevicesAdapter.setItemDoubleClickListener(deviceInfo -> {
            tempDeviceInfo = deviceInfo;
            deviceNameDialog.show();
        });

        favoriteDevicesAdapter.setToggleButtonListener(deviceInfo -> mViewModel.toggleDevicePower(
                deviceInfo
        ));
    }

    private ViewPager.OnPageChangeListener getOnPageChangeListener(){
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mViewModel.setSelectedType(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSceneData();
    }

    private void loadFavoriteRooms() {
        String homeId = CacheDataManager.getInstance().getCurrentHomeId();
        if (TextUtils.isEmpty(homeId)) {
            return;
        }
        mViewModel.fetchFavoriteRooms(homeId);
    }

    private void loadSceneData() {
        String homeId = CacheDataManager.getInstance().getCurrentHomeId();
        if (TextUtils.isEmpty(homeId)) {
            return;
        }
        mViewModel.getAllScene(homeId);
    }

    @Override
    public FragmentSceneBinding getBinding(LayoutInflater inflater) {
        return FragmentSceneBinding.inflate(inflater);
    }

    /**
     * mqtt上报修改状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("all")
    public void onEventBusNotify(DeviceEvent deviceEvent) {
        Log.d(TAG, "onEventBusNotify: message received=" + new Gson().toJson(deviceEvent));
        if (deviceEvent.getType() == DeviceEvent.Type.CHANGE_FAMILY) {
            if(FamilyChangeEventTarget.REFRESH_SCENES.equals(deviceEvent.getObj())){
                //切换家庭
                loadSceneData();
            }else if (FamilyChangeEventTarget.REFRESH_DEVICES.equals(deviceEvent.getObj())){
                loadFavoriteRooms();
            }
        }else if(deviceEvent.getType() == DeviceEvent.Type.DEVICE_INFO_CHANGED) {
            if(!DeviceChangeEventOrigin.HOME_PAGE.equals(deviceEvent.getObj())){
                loadFavoriteRooms();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 设置时间及天气
     */
    private void setDateData() {
        DisplayDateTime displayDateTime = DateUtils.getDisplayDateTime();
        binding.tvTime.setText(displayDateTime.getTime());
        binding.tvDate.setText(displayDateTime.getDate());
    }

    private void updateDisplayDateTime(DisplayDateTime displayDateTime){
        binding.tvTime.setText(displayDateTime.getTime());
        binding.tvDate.setText(displayDateTime.getDate());
    }
}
