package com.smart.rinoiot.center.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart.rinoiot.center.adapter.WifiAdapter;
import com.smart.rinoiot.center.manager.CenConstant;
import com.smart.rinoiot.center.manager.WifiScanManager;
import com.smart.rinoiot.center.viewmodel.CenLoginViewModel;
import com.smart.rinoiot.center.viewmodel.SetUpViewModel;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.utils.WifiUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.FragmentChangeWifiBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 修改wifi账号
 */
public class ChangeWifiFragment extends BaseFragment<FragmentChangeWifiBinding, SetUpViewModel> implements WifiScanManager.WifiListListener {
    public final static int FROM_AGREE_PERMISSION = 0x1001;
    private final String[] permissionList = {Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private boolean isConnected;

    @Override
    public void init() {
        initData();
    }

    @Override
    public FragmentChangeWifiBinding getBinding(LayoutInflater inflater) {
        return FragmentChangeWifiBinding.inflate(inflater);
    }

    private void initData() {
        WifiScanManager.getInstance().setListListener(this);
        initNetworkRecyclerView();
        isConnected = WifiUtil.getInstance().isWifiEnabled();
        if (!isConnected) {
            mViewModel.checkoutWifiStatus(this, CenLoginViewModel.NETWORK_LIST_TYPE);
        }
        updateCurrentNetwork(false);
        permissionData();
        binding.refresh.setOnRefreshListener(() -> {
            WifiScanManager.getInstance().initWifi(requireContext());
            updateCurrentNetwork(true);
            binding.refresh.setRefreshing(false);
        });
    }

    /**
     * 获取设备当前wifi名称
     */
    private void updateCurrentNetwork(boolean isOpen) {
        if (!isOpen) {
            getCurrentSsid();
            return;
        }
        //必须延迟，否则出现wifiManager还没有初始化完成拿不到ssid情况
        AppExecutors.getInstance().delayedThread().schedule(
                () -> AppExecutors.getInstance().mainThread().execute(this::getCurrentSsid), 2500, TimeUnit.MILLISECONDS
        );
    }

    /**
     * 获取wifi账号名称
     */
    private void getCurrentSsid() {
        isConnected = WifiUtil.getInstance().isWifiEnabled();
        String ssid = WifiUtil.getInstance().getSSID(getContext());

    }

    /**
     * 权限管理
     */
    private void permissionData() {
        if (EasyPermissions.hasPermissions(requireContext(), permissionList)) {
            WifiScanManager.getInstance().initWifi(requireContext());
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rino_user_permissions), FROM_AGREE_PERMISSION, permissionList);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull @NotNull List<String> perms) {
        if (requestCode == FROM_AGREE_PERMISSION) {
            WifiScanManager.getInstance().initWifi(requireContext());
            updateCurrentNetwork(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String @NotNull [] permissions, int @NotNull [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull @NotNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            ToastUtil.customSystem(getContext(), getString(R.string.rino_user_open_setting_permission), Toast.LENGTH_SHORT);
        } else {
            ToastUtil.customSystem(getContext(), getString(R.string.rino_user_permission_denied), Toast.LENGTH_SHORT);
        }
    }


    private WifiAdapter wifiAdapter;

    private void initNetworkRecyclerView() {
        wifiAdapter = new WifiAdapter(null);
        binding.wifiRecyclerView.setAdapter(wifiAdapter);
        wifiAdapter.setOnItemClickListener((adapter, view, position) -> {
            ScanResult scanResult = (ScanResult) adapter.getData().get(position);
            if (scanResult == null) {
                return;
            }
            SharedPreferenceUtil.getInstance().put(CenConstant.CURRENT_SSID, scanResult.SSID);
            binding.etPwd.setText(SharedPreferenceUtil.getInstance().get(scanResult.SSID, ""));
            Intent intent = new Intent();
            intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
            startActivityForResult(intent, CenLoginViewModel.NETWORK_LIST_TYPE);
        });
        binding.tvConfirm.setOnClickListener(v -> {
            String ssid = SharedPreferenceUtil.getInstance().get(CenConstant.CURRENT_SSID, "");
            SharedPreferenceUtil.getInstance().put(ssid, binding.etPwd.getText());
            WifiScanManager.getInstance().initWifi(requireContext());
            updateCurrentNetwork(true);
            /// WifiScanManager.getInstance().connectWifi(getContext(), ssid, SharedPreferenceUtil.getInstance().get(ssid, ""));

        });
        binding.tvCancel.setOnClickListener(v -> binding.etPwd.setText(""));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        WifiScanManager.getInstance().wifiBroadUnregister(requireContext());
    }

    @Override
    public void wifiSuccess(List<ScanResult> scanResults) {
        if (scanResults == null || scanResults.isEmpty()) {
            return;
        }
        List<ScanResult> results = new ArrayList<>();
        results.addAll(scanResults);
        ScanResult scanResult = null;
        for (ScanResult item : scanResults) {
            if (TextUtils.equals(item.SSID, WifiUtil.getInstance().getSSID())) {
                scanResult = item;
                break;
            }
        }
        results.remove(scanResult);
        List<ScanResult> tempResults = new ArrayList<>();
        tempResults.add(scanResult);
        tempResults.addAll(results);
        wifiAdapter.setNewInstance(results);
    }

    @Override
    public void wifiStatusChange(boolean isOpen) {
        AppExecutors.getInstance().delayedThread().schedule(() -> AppExecutors.getInstance().mainThread().execute(() -> {
            isConnected = WifiUtil.getInstance().isWifiEnabled();
            updateCurrentNetwork(true);
            if (mViewModel.confirmPopupView != null && isOpen) {
                mViewModel.confirmPopupView.dismiss();
            } else {
                mViewModel.checkoutWifiStatus(this, CenLoginViewModel.NETWORK_LIST_TYPE);
            }
        }), 2000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CenLoginViewModel.NETWORK_LIST_TYPE) {
            //wifi列表
            updateCurrentNetwork(false);
        }
    }
}
