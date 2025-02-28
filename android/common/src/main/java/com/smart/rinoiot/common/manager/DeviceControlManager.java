package com.smart.rinoiot.common.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.PanelInfo;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.listener.DownloadFileListener;
import com.smart.rinoiot.common.rn.BundleJSONConverter;
import com.smart.rinoiot.common.rn.PanelActivity;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.FileUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.view.CircleProgressPopView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 单设备面板数据处理
 */
public class DeviceControlManager {
    private static DeviceControlManager instance;

    private DeviceControlManager() {
    }

    public static DeviceControlManager getInstance() {
        if (instance == null) {
            instance = new DeviceControlManager();
        }
        return instance;
    }

    /**
     * 打开面板容器
     *
     * @param isNFCFlag FNC配网成功直接进入面板，蓝牙默认设置已连接
     */
    public void gotoPanel(Context context, DeviceInfoBean deviceInfoBean, boolean isNFCFlag) {
        if (deviceInfoBean != null) {
            getDpInfos(context, deviceInfoBean, isNFCFlag);
        } else {
            ToastUtil.showMsg("设备基础信息为空，不能打开面板");
        }
    }

    private void getPanel(Context context, DeviceInfoBean deviceInfoBean, boolean isNFCFlag) {
        CommonNetworkManager.getInstance().getPanelAsync(deviceInfoBean.getProductId(), new CallbackListener<PanelInfo>() {
            @Override
            public void onSuccess(PanelInfo data) {
                String fileName = deviceInfoBean.getProductId() + data.getId();
                if (!FileUtils.isFileExistsForFilesDir(fileName)) {
                    CircleProgressPopView dialog = new CircleProgressPopView(context,true);
                    new XPopup.Builder(context).dismissOnBackPressed(false).dismissOnTouchOutside(false).asCustom(dialog).show();

                    String downLoadFileName = fileName + ".zip";
                    CommonNetworkManager.getInstance().downloadFileAsync(data.getAndroidUrl(), downLoadFileName, new DownloadFileListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onProgress(int progress) {
                            AppExecutors.getInstance().mainThread().execute(() -> dialog.setProgress(progress));
                        }

                        @Override
                        public void onFinish(String path) {
                            AppExecutors.getInstance().mainThread().execute(() -> {
                                if (FileUtils.uncompressZip4j(downLoadFileName, "", true)) {
                                    gotoPanel(context, fileName, deviceInfoBean, isNFCFlag);
                                }
                                dialog.dismiss();
                            });
                        }

                        @Override
                        public void onError(String msg) {
                            AppExecutors.getInstance().mainThread().execute(() -> {
                                ToastUtil.showMsg(msg);
                                dialog.dismiss();
                            });
                        }
                    });
                } else {
                    gotoPanel(context, fileName, deviceInfoBean, isNFCFlag);
                }
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
            }
        });
    }

    private void gotoPanel(Context context, String rnDirName, DeviceInfoBean deviceInfoBean, boolean isNFCFlag) {
        Bundle bundle = new Bundle();
        bundle.putString("id", deviceInfoBean.getId());
        bundle.putString("name", deviceInfoBean.getName());
        bundle.putString("deviceType", getDeviceTypeData(deviceInfoBean.getProtocolType())); // enum 设备类型
        bundle.putString("productId", deviceInfoBean.getProductId());

        ArrayList<Bundle> dataPointArray = new ArrayList<>();
        Bundle dataPointArrayItem = null;
        Bundle dataPointValues = new Bundle();
        JSONArray jsonArray = new JSONArray();
        for (DeviceDpBean item : deviceInfoBean.getDpInfoVOList()) {
            if (!TextUtils.isEmpty(item.getDpJson())) {
                dataPointArrayItem = BundleJSONConverter.jsonStringToBundle(item.getDpJson());
            }
            if (item.getValue() instanceof Integer) {
                dataPointValues.putInt(item.getKey(), (int) item.getValue());
            } else if (item.getValue() instanceof Double) {
                dataPointValues.putDouble(item.getKey(), (Double) item.getValue());
            } else if (item.getValue() instanceof Float) {
                dataPointValues.putFloat(item.getKey(), (Float) item.getValue());
            } else if (item.getValue() instanceof Boolean) {
                dataPointValues.putBoolean(item.getKey(), (Boolean) item.getValue());
            } else if (item.getValue() instanceof String) {
                dataPointValues.putString(item.getKey(), String.valueOf(item.getValue()));
            } else {
                dataPointValues.putString(item.getKey(), new Gson().toJson(item.getValue()));
            }
            dataPointArray.add(dataPointArrayItem);
            try {
                JSONObject jsonObject = new JSONObject(item.getDpJson());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        bundle.putString("dataPointJson", jsonArray.toString()); // String 物模型
        bundle.putParcelableArrayList("dataPointArray", dataPointArray); // Array 物模型
        bundle.putBundle("dataPointValues", dataPointValues); // Dictionary或Map 云端物模型数据
        bundle.putBoolean("isGroup", false);
        bundle.putInt("bleConnectStatus", 1);
        bundle.putBoolean("isOnline", deviceInfoBean.getOnlineStatus() == 1);
        int networkType = 1;
        boolean isSupportBLE = false;
        if (TextUtils.equals(deviceInfoBean.getProtocolType(), Constant.PROTOCOL_TYPE_FOR_SINGLE_BLUETOOTH)) {//单蓝牙
            networkType = 0;
            isSupportBLE = true;
        } else if (TextUtils.equals(deviceInfoBean.getProtocolType(), Constant.PROTOCOL_TYPE_FOR_WIFI_BLE)) {//wifi+蓝牙
            if (deviceInfoBean.getBindMode() == 1) {//绑定模式（0=网络，1=蓝牙）
                networkType = 1;
            } else {
                networkType = 2;
            }
            isSupportBLE = true;
        }
        bundle.putInt("networkType", networkType);
        bundle.putBoolean("isSupportBLE", isSupportBLE);

        PanelParamsManager.getInstance().panelParamsRawData(bundle,deviceInfoBean,rnDirName);
        context.startActivity(new Intent(context, PanelActivity.class)
                .putExtra("deviceBean", bundle).putExtra("rnDirName", rnDirName)
//                .putExtra(Constant.PANEL_DEVICE_ID, deviceInfoBean.getId())
                .putExtra(Constant.PANEL_DEVICE_INFO, deviceInfoBean));
    }

    /**
     * 根据设备类型，返回对应的数据
     * 枚举列表：
     * * BLE:单点蓝牙
     * * WiFi:单WiFi
     * * BLEWiFi:蓝牙WiFi双模
     * * ZigBee
     * 通讯协议（1：WIFI+BLE，2：蓝牙Mesh（SIG），3：Zigbee，4：PLC，5：传统BLE，6：NB-IOT） protocolType
     */
    private String getDeviceTypeData(String protocolType) {
        String deviceType = "";
        if (TextUtils.equals(protocolType, Constant.PROTOCOL_TYPE_FOR_WIFI_BLE)) {
            deviceType = "BLEWiFi";
        } else if (TextUtils.equals(protocolType, Constant.PROTOCOL_TYPE_FOR_ZIGBEE)) {
            deviceType = "ZigBee";
        } else if (TextUtils.equals(protocolType, Constant.PROTOCOL_TYPE_FOR_SINGLE_BLUETOOTH)) {
            deviceType = "BLE";
        }
        return deviceType;
    }

    /**
     * 根据设备id获取设备物模型
     */
    private void getDpInfos(Context context, DeviceInfoBean deviceInfoBean, boolean isNFCFlag) {
        CommonNetworkManager.getInstance().getDeviceDataPointsAsync(deviceInfoBean.getId(), new CallbackListener<List<DeviceDpBean>>() {
            @Override
            public void onSuccess(List<DeviceDpBean> data) {
                if (data != null && data.size() > 0) {
                    deviceInfoBean.setDpInfoVOList(data);
                    getPanel(context, deviceInfoBean, isNFCFlag);
                } else {
                    ToastUtil.showMsg("设备最新数据，无法获取，无法打开面板");
                }
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
            }
        });
    }
}
