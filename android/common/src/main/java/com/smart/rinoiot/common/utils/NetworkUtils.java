package com.smart.rinoiot.common.utils;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.permission.PermissionManager;

import java.net.InetAddress;
import java.util.List;

/**
 * 判断wifi、蓝牙是否可用
 */
public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    /**
     * 判断WIFI是否打开
     */
    public static boolean isWiFiConnected() {
        WifiManager wifiManager = (WifiManager) BaseApplication.getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return null != wifiManager && wifiManager.isWifiEnabled();
    }

    /**
     * Checks if the device has a live internet connection.
     * <p>
     * @return true if internet is available, false otherwise
     */
    public static boolean isInternetAvailable(){
        try {
            Log.d(TAG, "isInternetAvailable: host=" + Constant.API_HOSTNAME);
            InetAddress ipAddress = InetAddress.getByName(Constant.API_HOSTNAME);
            //You can replace it with your name
            return StringUtil.isNotBlank(ipAddress.getHostAddress());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断蓝牙是否打开
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    public static boolean isBluetoothOpen(Context context) {
        boolean isOpen = false;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            String[] permissionSystemList;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionSystemList = PermissionManager.permissionListHigh;
            } else {
                permissionSystemList = PermissionManager.permissionListLow;
            }
            List<String> deniedList = PermissionManager.getPermissionListBleByDeniedList(context, permissionSystemList);
            if (deniedList == null ||deniedList.isEmpty()) {
                isOpen = mBluetoothAdapter.isEnabled();
            }
        }
        return isOpen;
    }
}