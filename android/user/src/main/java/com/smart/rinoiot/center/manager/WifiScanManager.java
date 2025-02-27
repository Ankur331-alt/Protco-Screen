package com.smart.rinoiot.center.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.smart.rinoiot.common.utils.WifiUtil;

import java.util.List;

/**
 * @author tw
 * @time 2022/12/5 21:09
 * @description wifi 列表扫描
 */
public class WifiScanManager {
    private static final String TAG = WifiScanManager.class.getSimpleName();
    private static WifiScanManager instance;

    public static WifiScanManager getInstance() {
        if (instance == null) {
            instance = new WifiScanManager();
        }
        return instance;
    }

    /**
     * 初始化wifi列表监听
     */
    public void initWifi(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mContext.registerReceiver(wifiScanReceiver, intentFilter);
        wifiManager.startScan();
    }

    /**
     * 注销广播
     */
    public void wifiBroadUnregister(Context mContext) {
        mContext.unregisterReceiver(wifiScanReceiver);
    }

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                    || intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            ) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                if (wifiState == WifiManager.WIFI_STATE_ENABLING) {//打开
                    if (listListener != null) listListener.wifiStatusChange(true);
                }
                if (wifiState == WifiManager.WIFI_STATE_DISABLING) {//关闭
                    if (listListener != null) listListener.wifiStatusChange(false);
                }


            }
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success && listListener != null) {
                listListener.wifiSuccess(WifiUtil.getInstance().getWifiList());
            }
        }
    };

    public WifiListListener listListener;

    public void setListListener(WifiListListener listListener) {
        this.listListener = listListener;
    }

    public interface WifiListListener {
        void wifiSuccess(List<ScanResult> scanResults);

        void wifiStatusChange(boolean isOpen);

    }

//    public void connectWifi(Context context,String ssid,String password) {
//        WifiUtils.withContext(context)
//                .connectWith(ssid, password)
//                .setTimeout(40000)
//                .onConnectionResult(new ConnectionSuccessListener() {
//                    @Override
//                    public void success() {
//                        ToastUtil.showMsg("成功");
//                    }
//
//                    @Override
//                    public void failed(@NonNull ConnectionErrorCode errorCode) {
//                        ToastUtil.showMsg("失败");
//                    }
//                })
//                .start();
//    }
}
