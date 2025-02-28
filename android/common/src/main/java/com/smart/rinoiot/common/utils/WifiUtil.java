package com.smart.rinoiot.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseApplication;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WifiUtil {
    private static final String TAG = WifiUtil.class.getSimpleName();
    private static WifiUtil instance;

    public static WifiUtil getInstance() {
        if (instance == null) {
            instance = new WifiUtil();
        }
        return instance;
    }

    // 定义WifiManager对象
    private static WifiManager mWifiManager;
    // 定义WifiInfo对象
    private static WifiInfo mWifiInfo;

    public WifiUtil() {
        refreshWifiInfo();
    }

    /**
     * WIFI是否开启
     */
    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * Checks if the device has a live internet connection.
     * <p>
     * @return true if internet is available, false otherwise
     */
    public boolean isInternetAvailable(){
        boolean enabled = isWifiEnabled();
        if(!enabled){
            return false;
        }

        try {
            Log.d(TAG, "isInternetAvailable: host=" + Constant.API_HOSTNAME);
            InetAddress ipAddress = InetAddress.getByName(Constant.API_HOSTNAME);
            //You can replace it with your name
            return StringUtil.isNotBlank(ipAddress.getHostAddress());
        } catch (Exception e) {
            return false;
        }
    }

    private static void refreshWifiInfo() {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) BaseApplication.getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /**
     * 判断 wifi 是否是 5G 频段.
     * 需要权限:
     * <uses-permission android:name="android.permission.INTERNET" />
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     */
    public boolean isWifi5G() {
        if (mWifiInfo == null || mWifiManager == null) return false;
        int freq = 0;
        refreshWifiInfo();
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            freq = mWifiInfo.getFrequency();
        } else {
            String ssid = mWifiInfo.getSSID();
            if (ssid != null && ssid.length() > 2) {
                String ssidTemp = ssid.substring(1, ssid.length() - 1);
                List<ScanResult> scanResults = mWifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(ssidTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }
        return freq > 4900 && freq < 5900;
    }

//    public int obtainWifiInfo() {
//        // Wifi的连接速度及信号强度：
//        float strength = 0;
//        // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        if (mWifiInfo.getBSSID() != null) {
//            // 链接信号强度，5为获取的信号强度值在5以内
//            strength = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 5);
//            // 链接速度
//            int speed = mWifiInfo.getLinkSpeed();
//            // 链接速度单位
//            String units = WifiInfo.LINK_SPEED_UNITS;
//            // Wifi源名称
//            String ssid = mWifiInfo.getSSID();
//        }
//        return (int) ((strength / 5) * 100);
//    }

    /**
     * 检查wifi是否处开连接状态
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }

    /**
     * 检查wifi强弱并更改图标显示
     */
    public static int checkWifiState(Context context) {
        if (isWifiConnect(context)) {
            WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            int wifi = mWifiInfo.getRssi();//获取wifi信号强度
            if (wifi > -70 && wifi < 0) {//最强
                return 1;
            } else {//较弱
                return 2;
            }
        } else {
            //无连接
            return -1;
        }

    }

    /**
     * 获取当前连接的ssid
     */
    public String getSSID() {
        refreshWifiInfo();
        if (mWifiInfo == null || !isWifiEnabled()) return "";
        String ssid = mWifiInfo.getSSID();
        if (TextUtils.isEmpty(ssid)) {
            return "";
        } else {
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.replaceAll("^\"|\"$", "");
            }
            if (TextUtils.equals("<unknown ssid>", ssid)) {
                ssid = "";
            }
            return ssid;
        }
    }

    /**
     * 获取wifi列表
     */
    public List<ScanResult> getWifiList() {
        List<ScanResult> scanResults = new ArrayList<>();
        if (mWifiManager != null) {
            List<ScanResult> tempScanResults = mWifiManager.getScanResults();
            for (ScanResult item : tempScanResults) {
                if (!TextUtils.isEmpty(item.SSID)) {
                    scanResults.add(item);
                }
            }
        }
        return scanResults;
    }


    /**
     * 获取当前连接的ssid
     */
    private static final String WIFISSID_UNKNOW = "<unknown ssid>";

    public String getSSID(Context context) {
        /*
         *  先通过 WifiInfo.getSSID() 来获取
         */

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        String result = wifiId != null ? wifiId.trim() : null;
        LgUtils.w("111111111111111  getSSID  info=" + new Gson().toJson(info) + "    wifiId=" + wifiId + "   result=" + result);
        if (!TextUtils.isEmpty(result)) {
            // 部分机型上获取的 ssid 可能会带有 引号
            if (result.charAt(0) == '"' && result.charAt(result.length() - 1) == '"') {
                result = result.substring(1, result.length() - 1);
            }
        }
        // 如果上面通过 WifiInfo.getSSID() 来获取到的是 空或者 <unknown ssid>，则使用 networkInfo.getExtraInfo 获取
//        if (TextUtils.isEmpty(result) || WIFISSID_UNKNOW.equalsIgnoreCase(result.trim())) {
//            NetworkInfo networkInfo = getNetworkInfo(context);
//            if (networkInfo.isConnected()) {
//                if (networkInfo.getExtraInfo() != null) {
//                    result = networkInfo.getExtraInfo().replace("\"", "");
//                }
//            }
//        }
        // 如果获取到的还是 空或者 <unknown ssid>，则遍历 wifi 列表来获取
        if (TextUtils.isEmpty(result) || WIFISSID_UNKNOW.equalsIgnoreCase(result.trim())) {
            result = getSSIDByNetworkId(context);
        }
        return TextUtils.equals(WIFISSID_UNKNOW, result) ? "" : result;
    }

    public NetworkInfo getNetworkInfo(Context context) {
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connectivityManager) {
                return connectivityManager.getActiveNetworkInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     *  遍历wifi列表来获取
     */
    private String getSSIDByNetworkId(Context context) {
        String ssid = WIFISSID_UNKNOW;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int networkId = wifiInfo.getNetworkId();
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (wifiConfiguration.networkId == networkId) {
                    ssid = wifiConfiguration.SSID;
                    break;
                }
            }
        }
        return ssid;
    }

    /**
     * 通过反射出不同版本的connect方法来连接Wifi
     *
     * @param netId
     */
    @SuppressLint("ObsoleteSdkInt")
    private Method connectWifiByReflectMethod(int netId) {
        Method connectMethod = null;
        if (mWifiManager == null) return connectMethod;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            LgUtils.i(TAG + "   connectWifiByReflectMethod road 1");
            // 反射方法： connect(int, listener) , 4.2 <= phone's android version
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connect".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    LgUtils.i(TAG + "   connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");
                    return null;
                }
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            // 反射方法: connect(Channel c, int networkId, ActionListener listener)
            // 暂时不处理4.1的情况 , 4.1 == phone's android version
            LgUtils.i(TAG + "   connectWifiByReflectMethod road 2");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            LgUtils.i(TAG + "   connectWifiByReflectMethod road 3");
            // 反射方法：connectNetwork(int networkId) ,
            // 4.0 <= phone's android version < 4.1
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connectNetwork".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId);
                } catch (Exception e) {
                    e.printStackTrace();
                    LgUtils.i(TAG + "   connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");
                    return null;
                }
            }
        } else {
            // < android 4.0
            return null;
        }
        return connectMethod;
    }

    /**
     * 切换网络
     */
    public void changeWifiConnect(int netId) {
        if (mWifiManager == null) return;
        Method connectMethod = connectWifiByReflectMethod(netId);
        if (connectMethod == null) {
            LgUtils.i(TAG + "     connect wifi by enableNetwork method");
            // 通用API
            mWifiManager.enableNetwork(netId, true);
        }
    }


    /**
     * 切换到指定wifi
     *
     * @param wifiName 指定的wifi名字
     * @param wifiPwd  wifi密码，如果已经保存过密码，可以传入null
     * @return
     */
    public void changeToWifi(String wifiName, String wifiPwd) {
        if (mWifiManager == null) {
            LgUtils.i(TAG + "   ***** init first ***** ");
            return;
        }
        printCurWifiInfo();
        String __wifiName__ = "\"" + wifiName + "\"";

        List wifiList = mWifiManager.getConfiguredNetworks();
        boolean bFindInList = false;
        for (int i = 0; i < wifiList.size(); ++i) {
            WifiConfiguration wifiInfo0 = (WifiConfiguration) wifiList.get(i);

            // 先找到对应的wifi
            if (__wifiName__.equals(wifiInfo0.SSID) || wifiName.equals(wifiInfo0.SSID)) {
                // 1、 先启动，可能已经输入过密码，可以直接启动
                LgUtils.i(TAG + "   set wifi 1 = " + wifiInfo0.SSID);
                doChange2Wifi(wifiInfo0.networkId);
                bFindInList = true;
                break;
            }

        }

        // 2、如果wifi还没有输入过密码，尝试输入密码，启动wifi
        if (!bFindInList) {
            WifiConfiguration wifiNewConfiguration = createWifiInfo(wifiName, wifiPwd);//使用wpa2的wifi加密方式
            int newNetworkId = mWifiManager.addNetwork(wifiNewConfiguration);
            if (newNetworkId == -1) {
                LgUtils.i(TAG + "   操作失败,需要您到手机wifi列表中取消对设备连接的保存");
            } else {
                doChange2Wifi(newNetworkId);
            }
        }

    }

    private void doChange2Wifi(int newNetworkId) {
        // 如果wifi权限没打开（1、先打开wifi，2，使用指定的wifi
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        changeWifiConnect(newNetworkId);
    }

    /**
     * 创建 WifiConfiguration，这里创建的是wpa2加密方式的wifi
     *
     * @param ssid     wifi账号
     * @param password wifi密码
     * @return
     */
    private WifiConfiguration createWifiInfo(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        config.preSharedKey = "\"" + password + "\"";
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

    public void printCurWifiInfo() {
        if (mWifiManager == null) {
            return;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        LgUtils.i(TAG + "   cur wifi = " + wifiInfo.getSSID());
        LgUtils.i(TAG + "   cur getNetworkId = " + wifiInfo.getNetworkId());
    }

}
