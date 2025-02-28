package com.smart.rinoiot.common.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;

import com.facebook.react.bridge.WritableArray;
import com.google.gson.Gson;
import com.smart.rinoiot.common.BleConfigConstant;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.api.CommonApiService;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.ProductInfoBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.MultiDevDisNetworkListener;
import com.smart.rinoiot.common.listener.StringCallback;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.EventManager;
import com.smart.rinoiot.common.manager.UpdateDeviceInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.mqtt2.Manager.TopicManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.rn.BundleJSONConverter;
import com.smart.rinoiot.common.rn.RNConstant;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.ftms.BleRssiDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author tw
 * @time 2022/10/25 21:35
 * @description 蓝牙扫描+蓝牙连接
 */
public class BleScanConnectManager implements BleManager.BleScanListener, ConnectStatusListener {
    private static final String TAG = BleScanConnectManager.class.getSimpleName();
    private static BleScanConnectManager instance;
    private ScanResultCallback scanResultCallback;
    private ConnectCallback connectCallback;
    /**
     * 通讯协议（1：WIFI+BLE，2：蓝牙Mesh（SIG），3：Zigbee，4：PLC，5：传统BLE，6：NB-IOT）
     */
    private String protocolType = "-1";
    private boolean isCallBack;//防止蓝牙多次回调
    private String uuid;

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public static BleScanConnectManager getInstance() {
        if (instance == null) {
            instance = new BleScanConnectManager();
        }
        return instance;
    }

    public BleScanConnectManager() {
        BleManager.setBleScanListener(this);
    }

    public void setScanResultCallback(ScanResultCallback scanResultCallback) {
        this.scanResultCallback = scanResultCallback;
    }

    public void setConnectCallback(ConnectCallback connectCallback) {
        this.connectCallback = connectCallback;
    }

    /**
     * 开启扫描
     */
    public void startScan() {
        BleManager.startBleScan();
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        BleScanConnectDataManager.getInstance().getProductInfoBeanMap().clear();
        BleManager.stopScan();

    }

    /**
     * 扫描后生成的数据通过uuid，获取设备昵称和头像
     *
     * @param productInfoBean product information
     */
    private void getSimpleDeviceInfo(ProductInfoBean productInfoBean) {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productInfoBean.getDevicePid());
        map.put("uuid", productInfoBean.getDeviceUuid());
        RetrofitUtils.getService(CommonApiService.class).getSimpleDeviceInfo(map).enqueue(new BaseRequestListener<ProductInfoBean>() {
            @Override
            public void onResult(ProductInfoBean data) {
                LgUtils.w(TAG + " 根据uuid获取设备的头像和昵称  data=" + data != null);
                if (data == null) {
                    return;
                }
                productInfoBean.setImageUrl(data.getImageUrl());
                productInfoBean.setProtocolName(data.getProtocolName());
                productInfoBean.setProtocolType(data.getProtocolType());
                productInfoBean.setDistributionNetMode(data.getDistributionNetMode());
                LgUtils.w(TAG + "   根据uuid获取设备的头像和昵称  data=" + new Gson().toJson(productInfoBean));
                if (scanResultCallback != null && (data.getDistributionNetMode() == 1 || data.getDistributionNetMode() == 2 || data.getDistributionNetMode() == 3)) {
                    scanResultCallback.onScanResult(productInfoBean, 0);
                }
            }

            @Override
            public void onError(String error, String msg) {
                LgUtils.w(TAG + "   根据uuid获取设备的头像和昵称异常  errorMsg=" + msg + "   " + productInfoBean.getDeviceUuid());
            }
        });
    }

    @Override
    public void bleScanResultCallBack(ProductInfoBean productInfoBean, int devConfigType) {//扫描到结果，回调
        if (BleScanConnectDataManager.getInstance().getReScanMap().containsKey(productInfoBean.getDeviceUuid())) {
//            String message = BleScanConnectDataManager.getInstance().getMessageMap().get(productInfoBean.getDeviceUuid());
            StringCallback stringCallback = BleScanConnectDataManager.getInstance().getUuidStringCallBackData(productInfoBean.getDeviceUuid());
            sendMessage(productInfoBean.getDeviceUuid(), "", stringCallback, null);
            BleScanConnectDataManager.getInstance().removeReScanByUuid(productInfoBean.getDeviceUuid());
        } else {
            if (devConfigType == 1 || devConfigType == 2) {
                ProductInfoBean searchShowProductInfoBean = BleScanConnectDataManager.getInstance().getSearchShowProductInfoBean(productInfoBean.getDeviceUuid());
                searchShowProductInfoBean.setCanNetStatus(productInfoBean.isCanNetStatus());
                searchShowProductInfoBean.setCanBind(productInfoBean.isCanBind());
                LgUtils.w(TAG + "   bleScanResultCallBack devConfigType=" + devConfigType + "   productInfoBean=" + new Gson().toJson(searchShowProductInfoBean));
                if (scanResultCallback != null && (searchShowProductInfoBean.getDistributionNetMode() == 1 || searchShowProductInfoBean.getDistributionNetMode() == 2 || searchShowProductInfoBean.getDistributionNetMode() == 3)) {
                    scanResultCallback.onScanResult(searchShowProductInfoBean, devConfigType);
                    return;
                }
            }
            if (productInfoBean.isCanBind()) {
                LgUtils.e(TAG + "        蓝牙设备以绑定  " + productInfoBean.getDeviceUuid());
                return;
            }
            if (TextUtils.equals(protocolType, "-1")) {//蓝牙自动搜索
                getSimpleDeviceInfo(productInfoBean);
            } else {//手动配网（NFC配网）
                LgUtils.e(TAG + "  twdsadad  scanDevice.getProtocolType()=" + productInfoBean.getProtocolType()
                        + "   scanDevice.getDeviceUuid()=" + productInfoBean.getDeviceUuid()
                        + "    uuid==" + uuid);
//                        if (scanDevice.getProtocolType() == protocolType) {
                // 手动配网
                if (!TextUtils.isEmpty(uuid) && TextUtils.equals(productInfoBean.getDeviceUuid(), uuid)) {
                    nfcConfigNetworkData(productInfoBean);
                }
            }
        }


    }

    /**
     * NFC重连机制
     */
    private int nfcRetryCount;

    public void setNfcRetryCount(int nfcRetryCount) {
        this.nfcRetryCount = nfcRetryCount;
    }

    public void retryNfcConfig(String uuid) {
        nfcRetryCount++;
        LgUtils.w(TAG + "   nfcRetryCount=" + nfcRetryCount + "   uuid=" + uuid
                + "   BleScanConnectDataManager.getInstance().getProductInfoBeanMap().containsKey(uuid)="
                + BleScanConnectDataManager.getInstance().getProductInfoBeanMap().containsKey(uuid));
        if (!TextUtils.isEmpty(uuid) && BleScanConnectDataManager.getInstance().getProductInfoBeanMap().containsKey(uuid) && nfcRetryCount < 4) {
            ProductInfoBean productInfoBean = BleScanConnectDataManager.getInstance().getProductInfoBeanMap().get(uuid);
            if (productInfoBean != null) {
                nfcConfigNetworkData(productInfoBean);
                return;
            }
        }
        if (nfcRetryCount < 4) {
            startConnectDevice(Constant.PROTOCOL_TYPE_FOR_SINGLE_BLUETOOTH, uuid);
        } else {
            stopScan();
        }
    }

    /**
     * 发送蓝牙消息
     *
     * @param uuid                       设备uuid
     * @param message                    发送消息
     * @param stringCallback             回调
     * @param multiDevDisNetworkListener 蓝牙连接数回调监听
     */
    public void sendMessage(String uuid, String message, StringCallback stringCallback,
                            MultiDevDisNetworkListener multiDevDisNetworkListener) {
        try {
            if (!TextUtils.isEmpty(message)) {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.has("type")) {
                    BleScanConnectDataManager.getInstance().saveMessageTypeUuid(uuid, jsonObject.getString("type"));
                    LgUtils.w(TAG + "  sendMessage  type=" + jsonObject.getString("type") + "  uuid=" + uuid);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            BleManager.setConnectStatusListener(this);
            BleScanConnectDataManager.getInstance().saveStringCallBackByUuid(uuid, stringCallback);
            BleScanConnectDataManager.getInstance().addConnectCount(uuid);
//            BleScanConnectDataManager.getInstance().saveMessageUuid(uuid, message);
            BleScanConnectDataManager.getInstance().saveMessageByteUuid(uuid, message);
            if (TextUtils.isEmpty(uuid)) {
                LgUtils.w(TAG + "  sendMessage   uuid 为空");
                return;
            }
            Map<String, ProductInfoBean> productInfoBeanMap = BleScanConnectDataManager.getInstance().getProductInfoBeanMap();
            if (productInfoBeanMap.containsKey(uuid)) {//已扫描设备
                ProductInfoBean productInfoBean = productInfoBeanMap.get(uuid);
                if (productInfoBean != null && !TextUtils.isEmpty(productInfoBean.getAddress())) {
                    if (productInfoBean.getConnectStatus() == 2 || productInfoBean.getConnectStatus() == 1) {//已连接
                        LgUtils.w(TAG + "  蓝牙设备已扫出  且已连接 uuid=" + uuid);
                        BleRssiDevice device;
                        if (BleScanConnectDataManager.getInstance().getBleRssiDeviceMap().containsKey(uuid)) {
                            device = BleScanConnectDataManager.getInstance().getBleRssiDeviceMap().get(uuid);
                            LgUtils.w(TAG + "  蓝牙设备已扫出  且已连接  device从缓存中获取  uuid=" + uuid);
                        } else {
                            device = new BleRssiDevice(productInfoBean.getAddress(), "RY");
                            LgUtils.w(TAG + "  蓝牙设备已扫出  且已连接  device创建新的  uuid=" + uuid);
                        }
                        writeByBluetooth(uuid, device);
                        return;
                    }
                    LgUtils.w(TAG + "  蓝牙设备已扫出  直接连接  uuid=" + uuid);
                    BleManager.sendMessage(productInfoBean.getDeviceUuid(), productInfoBean.getAddress());
                    return;
                }
            }
            BleScanConnectDataManager.getInstance().saveReScanByUuid(uuid, true);
            startScan();//该蓝牙设备没有被扫码过，需要重新扫码匹配
        }
    }

    @Override
    public void connectionState(String uuid, int connectStatus) {//状态变化 进入面板连接设备时发送通知
        if (BleScanConnectDataManager.getInstance().getProductInfoBeanMap().containsKey(uuid)) {
            ProductInfoBean productInfoBean = BleScanConnectDataManager.getInstance().getProductInfoBeanMap().get(uuid);
            productInfoBean.setConnectStatus(connectStatus);
        }
        UpdateDeviceInfoManager.getInstance().sendPanelBleConnectStatusNotice(connectStatus);
    }

    @Override
    public void notifyChanged(String uuid, BluetoothGattCharacteristic characteristic) {//数据变化
        onBleCharacteristicChanged(uuid, characteristic.getValue());
    }

    @Override
    public void notifySuccess(String uuid, BleRssiDevice device) {
        BleScanConnectDataManager.getInstance().saveBleRssiDeviceUuid(uuid, device);
        writeByBluetooth(uuid, device);
    }

    @Override
    public void connectFail(String uuid) {
        StringCallback uuidStringCallBackData = BleScanConnectDataManager.getInstance().getUuidStringCallBackData(uuid);
        if (uuidStringCallBackData != null) {
            uuidStringCallBackData.onCallback(Constant.BLE_CONNECT_FAIL);
        }
        if (connectCallback != null) {
            connectCallback.onConnectFail(uuid);
        }
    }

    /**
     * 蓝牙数据发生变化
     */
    private void onBleCharacteristicChanged(String uuid, byte[] dataArr) {
        if (dataArr != null && dataArr.length > 0) {
            LgUtils.i(TAG + " onCharacteristicChanged bluetooth --> notify --> " + ByteUtils.byteArrayToHexString(dataArr) + " --> " + new String(dataArr));
            try {
//                        byte[] bytes = new byte[dataArr.length - 2];
//                        System.arraycopy(dataArr, 1, bytes, 0, dataArr.length - 2);
//                        String end = RlinkPackDataUtil.rnLinkCRC(ByteUtils.byteArrayToHexString2(bytes));
//                        if (ByteUtils.getIntFromByte(dataArr[dataArr.length - 1]) == Integer.parseInt(end, 16)) {
                int dataLength = ByteUtils.getIntFromByte(dataArr[8]);
//                            int sumDataLength = ByteUtils.getIntFromByte(dataArr[6]) * 256 + ByteUtils.getIntFromByte(dataArr[7]);
                int sumPackage = ByteUtils.getIntFromByte(dataArr[4]) * 256 + ByteUtils.getIntFromByte(dataArr[5]);
                int packageIndex = ByteUtils.getIntFromByte(dataArr[2]) * 256 + ByteUtils.getIntFromByte(dataArr[3]);
                String data = AppUtil.byteArrayToString(AppUtil.subBytes(dataArr, 9, dataLength));
                Map<Integer, String> deviceResponseData = BleScanConnectDataManager.getInstance().getUuidResponseData(uuid);
                if (deviceResponseData.size() < sumPackage) {
                    if (!deviceResponseData.containsKey(packageIndex)) {
                        deviceResponseData.put(packageIndex, data);
                    }
                }
                if (deviceResponseData.size() == sumPackage) {
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < sumPackage; i++) {
                        result.append(deviceResponseData.get(i));
                    }
                    BleScanConnectDataManager.getInstance().removeUuidResponseData(uuid);//移除数据

                    LgUtils.i(TAG + " onCharacteristicChanged   bluetooth --> notify end --> " + result.toString());
                    StringCallback stringCallback = BleScanConnectDataManager.getInstance().getUuidStringCallBackData(uuid);
                    if (result.toString() != null || !TextUtils.isEmpty(result.toString())) {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        if (TextUtils.equals("thing.property.report", jsonObject.optString("type", ""))) {
                            DeviceInfoBean deviceInfoBean = CacheDataManager.getInstance().getDeviceInfoByUuid(uuid);
                            if (deviceInfoBean == null) {
                                LgUtils.i(TAG + " onCharacteristicChanged   bluetooth --> 找不到uuid对应的设备");
                                return;
                            }
                            //蓝牙操作设备是时，提设备上报mqtt数据
                            JSONObject report = new JSONObject(result.toString());
                            Iterator<String> keys = report.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (report.has("type")) {
                                    report.remove("type");
                                    break;
                                }
                            }
                            report.put("appReport", 1);
                            MqttManager.getInstance().publish(TopicManager.publishCloudDpToDevice(deviceInfoBean.getUuid()), report.toString().getBytes());
                            JSONArray jsonArray = new JSONArray();
                            JSONObject json = new JSONObject();
                            json.put("deviceId", deviceInfoBean.getId());
                            if (jsonObject.has("data")) {
                                json.put("properties", new JSONArray().put(jsonObject.getJSONObject("data")));
                                jsonArray.put(json);
                                WritableArray writableArray = BundleJSONConverter.jsonToReact(jsonArray);
                                LgUtils.i(TAG + " onCharacteristicChanged   bluetooth --> 发数据给rn --> " + jsonArray.toString());
                                EventManager.getInstance().sendData(RNConstant.DEVICE_DATA_POINT_UPDATE, writableArray);
                            }
                        } else {
                            if (stringCallback != null) {
                                stringCallback.onCallback(result.toString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LgUtils.e(TAG + "   onCharacteristicChanged   异常   e=" + e.getMessage());
            }
        }
    }

    /**
     * 断开设备连接
     *
     * @param isRemoveFlag true:移除设备
     */
    public void bleDisConnect(String uuid, boolean isRemoveFlag) {
        BleManager.stopScan();
        Map<String, ProductInfoBean> productInfoBeanMap = BleScanConnectDataManager.getInstance().getProductInfoBeanMap();
        if (productInfoBeanMap.containsKey(uuid)) {//已扫描设备
            ProductInfoBean productInfoBean = productInfoBeanMap.get(uuid);
            if (productInfoBean != null && !TextUtils.isEmpty(productInfoBean.getAddress())) {
                productInfoBean.setConnectStatus(0);//断开连接
                BleScanConnectDataManager.getInstance().removeMessageByteUuid(uuid);//移除消息内容
                BleScanConnectDataManager.getInstance().removeUuidStringCallBackData(uuid);//移除消息回调
                if (isRemoveFlag) {//移除扫描数据
                    BleManager.close(productInfoBean.getAddress());
                    BleScanConnectDataManager.getInstance().removeBleRssiDeviceUuid(uuid);//移除通知注册
                    BleScanConnectDataManager.getInstance().removeUuidProductInfoBeanData(uuid);
                } else {
                    BleManager.disConnect(productInfoBean.getAddress());
                }
                LgUtils.w(TAG + "    bleDisConnect  断开设备连接  ");
            }
        }
    }

    /**
     * 断开设备连接且清除数据
     *
     * @param isRemoveFlag true:移除设备
     */
    public void bleClose(String uuid, boolean isRemoveFlag) {
        BleManager.stopScan();
        Map<String, ProductInfoBean> productInfoBeanMap = BleScanConnectDataManager.getInstance().getProductInfoBeanMap();
        if (productInfoBeanMap.containsKey(uuid)) {//已扫描设备
            ProductInfoBean productInfoBean = productInfoBeanMap.get(uuid);
            if (productInfoBean != null && !TextUtils.isEmpty(productInfoBean.getAddress())) {
                BleManager.close(productInfoBean.getAddress());
                productInfoBean.setConnectStatus(0);//断开连接
//                BleScanConnectDataManager.getInstance().removeMessageUuid(uuid);//移除消息内容
                BleScanConnectDataManager.getInstance().removeMessageByteUuid(uuid);//移除消息内容
                BleScanConnectDataManager.getInstance().removeUuidStringCallBackData(uuid);//移除消息回调
                if (isRemoveFlag) {//移除扫描数据
                    BleScanConnectDataManager.getInstance().removeBleRssiDeviceUuid(uuid);//移除通知注册
                    BleScanConnectDataManager.getInstance().removeUuidProductInfoBeanData(uuid);
                }
                LgUtils.w(TAG + "    bleClose  断开设备连接  ");
            }
        }
    }

    /**
     * 发送蓝牙数据
     */
    public void writeByBluetooth(String uuid, BleRssiDevice device) {
//        String message = BleScanConnectDataManager.getInstance().getMessageMap().get(uuid);
        List<byte[]> bytes = BleScanConnectDataManager.getInstance().getMessageByteDataByUuid(uuid);
        LgUtils.w(TAG + "    writeByBluetooth   要写入的数据  bytes=" + bytes.size()
                + " key=  " + (uuid + BleScanConnectDataManager.getInstance().getMessageType(uuid)));
        if (bytes.isEmpty()) {
            LgUtils.w(TAG + "    writeByBluetooth   mesage数据为空，只需要连接蓝牙设备，不需要监听消息");
            return;
        }

        try {
            BleManager.writeData(uuid, device, bytes.get(0), () -> {
                bytes.remove(bytes.get(0));
                writeByBluetooth(uuid, device);
            });
            Thread.sleep(130);//必须加上延迟，否则发送消息频繁，导致蓝牙设备处理不过来，出现断开连接问题
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开所有设备连接
     */
    public void disconnectAll() {
        for (String str : BleScanConnectDataManager.getInstance().getProductInfoBeanMap().keySet()) {
            BleScanConnectDataManager.getInstance().getProductInfoBeanMap().get(str).setConnectStatus(0);
        }
        BleManager.disconnectAll();
        LgUtils.w(TAG + "   disconnectAll");
    }

    /**
     * 断开所有设备连接
     */
    public void closetAll() {
        for (String str : BleScanConnectDataManager.getInstance().getProductInfoBeanMap().keySet()) {
            BleScanConnectDataManager.getInstance().getProductInfoBeanMap().get(str).setConnectStatus(0);
        }
        BleManager.closeAll();
        LgUtils.w(TAG + "   closetAll");
    }

    /**
     * 手动配网
     */
    public void startConnectDevice(String protocolType) {
        this.protocolType = protocolType;
        startScan();
    }

    /**
     * NFC开启配网
     */
    public void startConnectDevice(String protocolType, String uuid) {
        this.protocolType = protocolType;
        this.uuid = uuid;
        if (!TextUtils.isEmpty(uuid) && BleScanConnectDataManager.getInstance().getProductInfoBeanMap().containsKey(uuid)) {
            nfcConfigNetworkData(BleScanConnectDataManager.getInstance().getProductInfoBeanMap().get(uuid));
            return;
        }
        startScan();
    }

    /**
     * nfc走单蓝牙配网流程
     */
    /**
     * nfc走单蓝牙配网流程
     */
    private void nfcConfigNetworkData(ProductInfoBean productInfo) {
        if (connectCallback != null) {
            connectCallback.onScanSuccess(productInfo);
        }
        try {
            JSONObject data = new JSONObject();
            data.put("type", BleConfigConstant.DEVICE_INFORMATION_GET);
            isCallBack = false;
            sendMessage(uuid, data.toString(), data12 -> {
                LgUtils.w(TAG + "   NFC   data=" + data12 + "   isCallBack=" + isCallBack + "  connectCallback=" + connectCallback);
                try {
                    JSONObject bleSignObject = new JSONObject(data12);
                    String type = bleSignObject.optString("type", "");
                    if (TextUtils.equals(type, BleConfigConstant.DEVICE_INFORMATION_GET_RESPONSE) && !isCallBack) {
                        isCallBack = true;
                        SignBleConfigBean signBleConfigBean = new Gson().fromJson(new JSONObject(data12).get("data").toString(), SignBleConfigBean.class);
                        if (signBleConfigBean != null && connectCallback != null) {
                            connectCallback.onRegisterCloudResult(productInfo.getDeviceUuid(), signBleConfigBean);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
