package com.smart.rinoiot.common.rn;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.UpdateDeviceInfoManager;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.swmansion.reanimated.ReanimatedPackage;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * @author edwin
 */
@AndroidEntryPoint
public class MtrDevicePanelActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {

    private static final String TAG = "MtrDevicePanelActivity";

    /**
     * Device information
     */
    DeviceInfoBean deviceInfo;

    /**
     *
     */
    private ReactRootView mReactRootView;

    /**
     *
     */
    private ReactInstanceManager mReactInstanceManager;

    /**
     *
     */
    private static final String RN_MODULE_NAME = "RinoRCTApp";

    /**
     *
     */
    private static final String RN_DIRECTORY_NAME_KEY = "rnDirName";

    /**
     *
     */
    private static final String  DEVICE_BEAN_EXTRA_KEY = "deviceBean";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG ,"[*** LifeCycle ***] : onCreate");

        // Initialize the panel
        RNDialogUtils.getInstance().init(this);
        RNDialogUtils.getInstance().showPanelLoadingDialog();

        AppManager.getInstance().addActivity(this);
        UpdateDeviceInfoManager.getInstance().setPanelWifiConfigFlag(false);
        StatusBarUtil.setTransparentFullIntegralStatusBar(this, false);

        deviceInfo = (DeviceInfoBean) getIntent().getSerializableExtra(Constant.PANEL_DEVICE_INFO);
        String dirName = getIntent().getStringExtra(RN_DIRECTORY_NAME_KEY);

        // ToDo() 这是访问本地开发JS服务的代码，主要用于开发调试环节使用，后续这里可以单独领出来做一个Activity给到RN的开发人员任意配置IP+PORT,
        /// SharedPreferences mPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        /// mPreferences.edit().putString("debug_http_host","192.168.31.40:8081").commit();

        // Initialize JS view
        mReactRootView = new ReactRootView(this);
        List<ReactPackage> packages = getPackages();
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setCurrentActivity(this)
                .setBundleAssetName("native_rn/index.android.bundle")
                .setJSBundleFile(getJSBundleFile(dirName))
                .setJSMainModulePath("index")
                .addPackages(packages)
                .setUseDeveloperSupport(BuildConfig.DEBUG || SharedPreferenceUtil.getInstance().get(Constant.PANEL_DEBUG, false))
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();

        // Start the react application with the device bean
        mReactRootView.startReactApplication(
                mReactInstanceManager,
                RN_MODULE_NAME, getIntent().getBundleExtra(DEVICE_BEAN_EXTRA_KEY));

        // Setup Ui Elements
        setupUiLayerElements();

        // Setup observers
        setupObservers();

        setContentView(mReactRootView);
    }

    /**
     * Getter for the JSBundleFile
     *
     * @param dirName directory name
     * @return the JSBundleFile
     */
    private String getJSBundleFile(String dirName) {
        String path = this.getApplicationContext().getFilesDir().getPath();
        return path.concat("/").concat(dirName).concat("/").concat("index.android.bundle");
    }

    /**
     * Getter for the react-native packages
     *
     * @return the list of react-native packages
     */
    private List<ReactPackage> getPackages() {
        List<ReactPackage> packages = new PackageList(getApplication()).getPackages();
        // Adding manually Reanimated package here, with overriding getReactInstanceManager method
        packages.add(new ReanimatedPackage() {
            @Override
            public ReactInstanceManager getReactInstanceManager(ReactApplicationContext reactContext) {
                // Implement here your way to get the ReactInstanceManager
                return MtrDevicePanelActivity.this.mReactInstanceManager;
            }
        });
        packages.add(new RinoReactPackage());
        return packages;
    }

    private void setupObservers() {
    }

    private void setupUiLayerElements() {
    }


    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG ,"[*** LifeCycle ***] : onResume");

        // ToDo() resume device states subscriptions

        if (mReactInstanceManager == null) {
            return;
        }
        mReactInstanceManager.onHostResume(this, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG ,"[*** LifeCycle ***] : onPause");

        // ToDo() pause/stop device states subscriptions

        if (mReactInstanceManager == null) {
            return;
        }
        mReactInstanceManager.onHostPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG ,"[*** LifeCycle ***] : onDestroy");

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
        }

        RNDialogUtils.getInstance().stopDialogLoading();
        AppManager.getInstance().finishActivity(this);
    }
}