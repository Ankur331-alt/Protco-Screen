package com.smart.rinoiot.center.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart.rinoiot.center.activity.CenFlashActivity;
import com.smart.rinoiot.center.adapter.WifiListAdapter;
import com.smart.rinoiot.center.manager.WifiScanManager;
import com.smart.rinoiot.center.viewmodel.CenLoginViewModel;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.utils.WifiUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.FragmentConnectBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description wifi连接
 */
public class ConnectFragment extends BaseFragment<FragmentConnectBinding, CenLoginViewModel> implements WifiScanManager.WifiListListener {
    public final static int FROM_AGREE_PERMISSION = 0x1001;
    private final String[] permissionList = {Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private boolean isConnected;

    @Override
    public void init() {
        initData();
    }

    @Override
    public FragmentConnectBinding getBinding(LayoutInflater inflater) {
        return FragmentConnectBinding.inflate(inflater);
    }

    private void initData() {
        WifiScanManager.getInstance().setListListener(this);
        binding.includeCurrentNetwork.getRoot().setBackgroundResource(R.drawable.shape_bg_item_10);
        initNetworkRecyclerView();
        isConnected = WifiUtil.getInstance().isWifiEnabled();
        if (!isConnected) {
            mViewModel.checkoutWifiStatus(this, CenLoginViewModel.NETWORK_LIST_TYPE);
        }
        updateCurrentNetwork(false);
        permissionData();
        binding.includeCurrentNetwork.getRoot().setOnClickListener(v -> ((CenFlashActivity) getActivity()).gotoNextFragment(new ScanLoginFragment()));
        binding.refresh.setOnRefreshListener(() -> {
            binding.refresh.setRefreshing(false);
            updateCurrentNetwork(false);
            permissionData();
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
        AppExecutors.getInstance().delayedThread().schedule(() -> AppExecutors.getInstance().mainThread().execute(this::getCurrentSsid), 1500, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取wifi账号名称
     */
    private void getCurrentSsid() {
        isConnected = WifiUtil.getInstance().isWifiEnabled();
        binding.llNetworkList.setVisibility(isConnected ? View.VISIBLE : View.GONE);
        String ssid = WifiUtil.getInstance().getSSID(getContext());
        if (!TextUtils.isEmpty(ssid)) {//设备没有连接wifi
            binding.includeCurrentNetwork.ivNetwork.setImageResource(R.drawable.icon_network_selected);
            binding.includeCurrentNetwork.tvConnectName.setText(ssid);
            binding.includeCurrentNetwork.tvConnectStatus.setText(getString(R.string.rino_user_current_network_available));
        }
        LgUtils.w("111111111111111  getCurrentSsid  isConnected="+isConnected+"   ssid="+ssid);
        binding.llCurrentNetwork.setVisibility(isConnected && !TextUtils.isEmpty(ssid) ? View.VISIBLE : View.GONE);

    }

    /**
     * 权限管理
     */
    private void permissionData() {
        if (EasyPermissions.hasPermissions(getContext(), permissionList)) {
            WifiScanManager.getInstance().initWifi(getContext());
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rino_user_permissions), FROM_AGREE_PERMISSION, permissionList);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull @NotNull List<String> perms) {
        if (requestCode == FROM_AGREE_PERMISSION) {
            WifiScanManager.getInstance().initWifi(getContext());
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


    private WifiListAdapter wifiListAdapter;

    private void initNetworkRecyclerView() {
        wifiListAdapter = new WifiListAdapter(null);
        binding.networkRecyclerView.setAdapter(wifiListAdapter);
        wifiListAdapter.setOnItemClickListener((adapter, view, position) -> {
            ScanResult scanResult = (ScanResult) adapter.getData().get(position);
            if (scanResult == null) return;
            Intent intent = new Intent();
            intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
            startActivityForResult(intent, CenLoginViewModel.NETWORK_LIST_TYPE);
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        WifiScanManager.getInstance().wifiBroadUnregister(getContext());
    }

    @Override
    public void wifiSuccess(List<ScanResult> scanResults) {
        if (scanResults == null || scanResults.isEmpty()) return;
        wifiListAdapter.setNewInstance(scanResults);
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
        }), 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CenLoginViewModel.NETWORK_LIST_TYPE) {//wifi列表
            updateCurrentNetwork(false);
        }
    }
}
