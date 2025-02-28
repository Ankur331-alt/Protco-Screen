package com.smart.rinoiot.common.rn;

import android.bluetooth.BluetoothAdapter;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.PackageList;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.smart.rinoiot.common.BuildConfig;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.ble.BleManager;
import com.smart.rinoiot.common.ble.BleScanConnectManager;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.UpdateDeviceInfoManager;
import com.smart.rinoiot.common.receiver.BluetoothMonitorReceiver;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.swmansion.reanimated.ReanimatedPackage;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class PanelActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler, BluetoothMonitorReceiver.BleStatusListener {

    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;
    DeviceInfoBean deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进入面板重新设置接口通知消息
        UpdateDeviceInfoManager.getInstance().setPanelWifiConfigFlag(false);
        RNDialogUtils.getInstance().init(this);
        RNDialogUtils.getInstance().showPanelLoadingDialog();
        deviceInfo = (DeviceInfoBean) getIntent().getSerializableExtra(Constant.PANEL_DEVICE_INFO);
        StatusBarUtil.setTransparentFullIntegralStatusBar(this, false);
        AppManager.getInstance().addActivity(this);
        registerBluetooth();
        // ToDo() 这是访问本地开发JS服务的代码，主要用于开发调试环节使用，后续这里可以单独领出来做一个Activity给到RN的开发人员任意配置IP+PORT,
        /// SharedPreferences mPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        /// mPreferences.edit().putString("debug_http_host","192.168.31.40:8081").commit();

        String path = this.getApplicationContext().getFilesDir().getPath();
        String dirName = getIntent().getStringExtra("rnDirName");

        //初始化JSView
        mReactRootView = new ReactRootView(this);
        List<ReactPackage> packages = new PackageList(getApplication()).getPackages();
        // Adding manually Reanimated package here, with overriding getReactInstanceManager method
        packages.add(new ReanimatedPackage() {
            @Override
            public ReactInstanceManager getReactInstanceManager(ReactApplicationContext reactContext) {
                // Implement here your way to get the ReactInstanceManager
                return PanelActivity.this.getReactInstanceManager();
            }
        });
        packages.add(new RinoReactPackage());
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setCurrentActivity(this)
                .setBundleAssetName("native_rn/index.android.bundle")
                .setJSBundleFile(path + "/" + dirName + "/" + "index.android.bundle")
                .setJSMainModulePath("index")
                .addPackages(packages)
                .setUseDeveloperSupport(BuildConfig.DEBUG || SharedPreferenceUtil.getInstance().get(Constant.PANEL_DEBUG,false))
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        // deviceBean参数是给RN初始化的一些信息
        mReactRootView.startReactApplication(mReactInstanceManager, "RinoRCTApp", getIntent().getBundleExtra("deviceBean"));

        /// BarConfig barConfig = new BarConfig(this);
        /// mReactRootView.setPadding(0, barConfig.getStatusBarHeight(), 0, 0);
        setContentView(mReactRootView);
    }

    private ReactInstanceManager getReactInstanceManager() {
        return mReactInstanceManager;
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
            if (UpdateDeviceInfoManager.getInstance().isPanelWifiConfigFlag()) {
                return;
            }
            AppExecutors.getInstance().delayedThread().schedule(() -> {
                if (deviceInfo != null && deviceInfo.getOnlineStatus() == 0) {//设备在线，通信方式走mqtt，离线走蓝牙协议
//                    CommonNetworkManager.getInstance().getModel(deviceInfo.getProductId(), deviceInfo.getUuid());
                    BleScanConnectManager.getInstance().sendMessage(deviceInfo.getUuid(), "", null, null);
                }
            }, 1000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
        }
        if (deviceInfo != null) {
            if (UpdateDeviceInfoManager.getInstance().isPanelWifiConfigFlag()) {//以从蓝牙配网变成wifi配网，不需要断开蓝牙连接
                UpdateDeviceInfoManager.getInstance().setPanelWifiConfigFlag(false);
                return;
            }
            BleScanConnectManager.getInstance().bleDisConnect(deviceInfo.getUuid(), false);
        }
        BleManager.stopScan();
        unRegisterBluetooth();
        RNDialogUtils.getInstance().stopDialogLoading();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    private BluetoothMonitorReceiver bluetoothMonitorReceiver;

    private void registerBluetooth() {
        bluetoothMonitorReceiver = new BluetoothMonitorReceiver();
        bluetoothMonitorReceiver.setBleStatusListener(this);
        IntentFilter intentFilter = new IntentFilter();
        // 监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        // 监视蓝牙设备与APP连接的状态
//        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);

        // 注册广播
        registerReceiver(bluetoothMonitorReceiver, intentFilter);
    }

    private void unRegisterBluetooth() {
        if (bluetoothMonitorReceiver != null) {
            unregisterReceiver(bluetoothMonitorReceiver);
        }
        bluetoothMonitorReceiver = null;
    }

    @Override
    public void callBack(boolean open) {
        if (!open) {
            UpdateDeviceInfoManager.getInstance().sendPanelBleConnectStatusNotice(0);
        }
    }
}
