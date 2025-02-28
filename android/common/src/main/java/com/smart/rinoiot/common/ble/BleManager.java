package com.smart.rinoiot.common.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.text.TextUtils;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.bean.ProductInfoBean;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.core.ble.Ble;
import com.smart.rinoiot.core.ble.callback.BleConnectCallback;
import com.smart.rinoiot.core.ble.callback.BleMtuCallback;
import com.smart.rinoiot.core.ble.callback.BleNotifyCallback;
import com.smart.rinoiot.core.ble.callback.BleScanCallback;
import com.smart.rinoiot.core.ble.callback.BleWriteCallback;
import com.smart.rinoiot.core.ble.model.BleDevice;
import com.smart.rinoiot.core.ble.model.BleFactory;
import com.smart.rinoiot.core.ble.model.EntityData;
import com.smart.rinoiot.core.ble.utils.UuidUtils;
import com.smart.rinoiot.ftms.BleRssiDevice;
import com.smart.rinoiot.ftms.MyBleWrapperCallback;

import java.util.List;
import java.util.UUID;

@SuppressLint("StaticFieldLeak")
public class BleManager {
    private static final String TAG = BleManager.class.getSimpleName();
    /**
     * 设备管理类
     */
    private static final Ble<BleRssiDevice> ble = Ble.getInstance();

    /**
     * 蓝牙扫描监听
     */
    private static BleScanListener bleScanListener;

    public static void setBleScanListener(BleScanListener bleScanListener) {
        BleManager.bleScanListener = bleScanListener;
    }

    /**
     * 蓝牙连接监听
     */
    public static ConnectStatusListener mConnectStatusListener;

    public static void setConnectStatusListener(ConnectStatusListener connectStatusListener) {
        mConnectStatusListener = connectStatusListener;
    }

    public static void init(Context context) {
        //设置是否输出打印蓝牙日志
        Ble.options().setLogBleEnable(true)
                //设置是否抛出蓝牙异常
                .setThrowBleException(true)
                //设置全局蓝牙操作日志TAG
                .setLogTAG("AndroidBLE")
                //设置是否自动连接
                .setAutoConnect(false)
                //设置是否过滤扫描到的设备(已扫描到的不会再次扫描)
                .setIgnoreRepeat(false)
                //连接异常时（如蓝牙协议栈错误）,重新连接次数
                .setConnectFailedRetryCount(5)
                //设置连接超时时长
                .setConnectTimeout(10 * 1000)
                //设置扫描时长
                .setScanPeriod(60 * 1000)
                //最大连接数量
                .setMaxConnectNum(7)
                //设置主服务的uuid
                .setUuidService(UUID.fromString(UuidUtils.uuid16To128("1910")))
                //设置可写特征的uuid
                .setUuidWriteCha(UUID.fromString(UuidUtils.uuid16To128("2b11")))
                //                .setUuidReadCha(UUID.fromString(UuidUtils.uuid16To128("fd02")))//设置可读特征的uuid （选填）
                //设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）
                .setUuidNotifyCha(UUID.fromString(UuidUtils.uuid16To128("2b10")))
                .setFactory(new BleFactory() {
                    @Override
                    public BleDevice create(String address, String name) {
                        return new BleRssiDevice(address, name);
                    }
                })
                .setBleWrapperCallback(new MyBleWrapperCallback())
                .create(context, new Ble.InitCallback() {
                    @Override
                    public void success() {
                        LgUtils.e(TAG + "   蓝牙库初始化成功");
                    }

                    @Override
                    public void failed(int failedCode) {
                        LgUtils.e(TAG + "    蓝牙库初始化失败" + failedCode);
                    }
                });
    }


    /**
     * 统一使用蓝牙扫描
     */
    public static void startBleScan() {
        if (!ble.isSupportBle(ble.getContext())) {
            //手机是否支持蓝牙设备
            ToastUtil.showMsg(ble.getContext().getString(R.string.rino_common_ble_is_support));
            return;
        }
        if (!ble.isBleEnable()) {
            //手机是否打开系统蓝牙
            ToastUtil.showMsg(ble.getContext().getString(R.string.rino_common_ble_open));
            return;
        }
        LgUtils.e(TAG + "    扫描是否已经开启 startBleScan: " + ble.isScanning());
        if (ble.isScanning()) {
            ble.stopScan();
        }
        ble.startScan(new BleScanCallback<BleRssiDevice>() {
            @Override
            public void onLeScan(BleRssiDevice device, int rssi, byte[] scanRecord) {
                LgUtils.e(TAG + "  deviceAddress=" + device.getBleAddress()
                        + "  devicename=" + device.getBleName());
                scanResultDeal(device, scanRecord, device.getBleAddress());
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                ToastUtil.showErrorMsg(String.valueOf(errorCode));
                LgUtils.e(TAG + "    onScanFailed: " + errorCode);
            }
        });

    }

    /**
     * 停止统一扫描
     */
    public static void stopScan() {
        LgUtils.e(TAG + "    stopScan: bleManager 停止扫描");
        if (ble.isScanning()) {
            ble.stopScan();
        }
    }


    /**
     * 扫描过滤+数据格式处理
     */
    private static void scanResultDeal(BleRssiDevice device, byte[] scanRecord, String address) {
        if (!TextUtils.equals(device.getBleName(), "RY")) {
            return;
        }
        ProductInfoBean productInfoBean = new ProductInfoBean();
        List<AdRecord> adRecords = AdRecordUtils.parseScanRecordAsList(scanRecord);
        if (!adRecords.isEmpty()) {
            for (AdRecord adRecord : adRecords) {
                if (adRecord.getType() == 255) {
                    // uuid
                    productInfoBean.setDeviceUuid(ByteUtils.byteArrayToString(ByteUtils.subBytes(adRecord.getData(), adRecord.getLength() - 17, 16)));
                    if (TextUtils.isEmpty(productInfoBean.getDeviceUuid()) || productInfoBean.getDeviceUuid().length() < 16) {
                        //uuid小于16位，直接过滤
                        break;
                    }

                    //bit7：设置配网标志(1: 不在配网状态；0: 在配网状态)。
                    //bit6：绑定标志(1 绑定；0: 未绑定)。绑定成功则为非配网状态bit7置1，其他情况都在配网状态
                    boolean isCanBind = (ByteUtils.getIntFromByte(adRecord.getData()[2]) >> 6 & 1) == 0;
                    productInfoBean.setUniversalFirmware(ByteUtils.getIntFromByte(adRecord.getData()[2]) >> 4 & 1);
                    productInfoBean.setCanBind(!isCanBind);
                    if (!isCanBind) {
                        //已绑定
                        productInfoBean.setCanNetStatus(false);
                    } else {//右移8位，获取bit7对于的值
                        productInfoBean.setCanNetStatus((ByteUtils.getIntFromByte(adRecord.getData()[2]) >> 7 & 1) == 0);
                    }
                    if ((adRecord.getData()[6] >> 2 & 1) == 1 || (adRecord.getData()[6] >> 3 & 1) == 1) {
                        productInfoBean.setProtocolType(Constant.PROTOCOL_TYPE_FOR_WIFI_BLE);
                    } else {
                        productInfoBean.setProtocolType(Constant.PROTOCOL_TYPE_FOR_SINGLE_BLUETOOTH);
                    }
                    // ToDo() test scanDevice.setProtocolType(Constant.PROTOCOL_TYPE_FOR_SINGLE_BLUETOOTH);
                    productInfoBean.setEncryptType(ByteUtils.getIntFromByte(adRecord.getData()[4]));
                } else if (adRecord.getType() == 22) {
                    // pid
                    productInfoBean.setDevicePid(ByteUtils.byteArrayToString(ByteUtils.subBytes(adRecord.getData(), 3, adRecord.getData().length - 3)));
                }
                productInfoBean.setAddress(address);
            }
            if (TextUtils.isEmpty(productInfoBean.getDeviceUuid())) {
                LgUtils.e(TAG + "        蓝牙设备uuid位空 ");
                return;
            }
            if (BleScanConnectDataManager.getInstance().getProductInfoBeanMap()
                    .containsKey(productInfoBean.getDeviceUuid())) {
                //已存在，
                ProductInfoBean infoBean = BleScanConnectDataManager.getInstance().getProductInfoBeanMap().get(productInfoBean.getDeviceUuid());
                if (infoBean != null) {
                    int configType = 0;
                    if (!infoBean.isCanBind() && productInfoBean.isCanBind()) {
                        //已保存的数据未绑定，当前扫到的设备已绑定，且都是一个设备，更新缓存数据
                        configType = 1;
                    } else if (infoBean.isCanBind() && !productInfoBean.isCanBind()
                            || !infoBean.isCanNetStatus() && productInfoBean.isCanNetStatus()
                            || infoBean.isCanNetStatus() && !productInfoBean.isCanNetStatus()
                    ) {
                        //已保存的数据已绑定，当前扫到的设备未绑定，且都是一个设备，更新缓存数据
                        //已保存的数据未在配网状态，当前扫到的设备已在配网状态，且都是一个设备，更新缓存数据
                        //已保存的数据已在配网状态，当前扫到的设备已未在配网状态，且都是一个设备，更新缓存数据
                        configType = 2;
                    }
                    if (configType != 0) {
                        LgUtils.e("BleScanConnectManager       缓存中已存在且为绑定状态，扫描更新时，为未绑定状态，更新缓存  Uuid()=" + productInfoBean.getDeviceUuid());
                        //只要uuid不为空，就保存数据
                        BleScanConnectDataManager.getInstance().addProductInfoBean(productInfoBean.getDeviceUuid(), productInfoBean);
                        if (bleScanListener != null) {
                            bleScanListener.bleScanResultCallBack(productInfoBean, configType);
                        }
                        return;
                    }
                }
            }
            if (!productInfoBean.isCanBind() && BleScanConnectDataManager.getInstance().getProductInfoBeanMap()
                    .containsKey(productInfoBean.getDeviceUuid())) {
                //未绑定的设备，过滤多次回调
                LgUtils.e("BleScanConnectManager 未绑定的设备，过滤多次回调  Uuid()=" + productInfoBean.getDeviceUuid());
                return;
            }
            //只要uuid不为空，就保存数据
            BleScanConnectDataManager.getInstance().addProductInfoBean(productInfoBean.getDeviceUuid(), productInfoBean);
            LgUtils.e(TAG + "      productInfoBean.getDeviceUuid()=  " + productInfoBean.getDeviceUuid()
                    + "   productInfoBean.isCanBind()=" + productInfoBean.isCanBind());
            if (bleScanListener != null) {
                bleScanListener.bleScanResultCallBack(productInfoBean, 0);
            }
        }
    }

    //重试次数
    private static int retryCount;

    public static void sendMessage(String uuid, String address) {
        stopScan();
        retryCount = 0;
        ble.connect(address, new BleConnectCallback<BleRssiDevice>() {
            @Override
            public void onConnectionChanged(BleRssiDevice device) {//蓝牙连接状态
                String tempUuid = BleScanConnectDataManager.getInstance().getDeviceUuidByMac(device.getBleAddress(), uuid);
                LgUtils.w("writeByBluetooth   BleManager.isDeviceBusy(device)  onConnectionChanged  111111="
                        + device.getBleAddress() + "  2222222=" + BleScanConnectDataManager.getInstance()
                        .getProductInfoBeanByUuid(tempUuid).getAddress()
                        + "      " + TextUtils.equals(device.getBleAddress(), BleScanConnectDataManager.getInstance()
                        .getProductInfoBeanByUuid(tempUuid).getAddress())
                        + "    uuid=" + tempUuid);
                LgUtils.w(TAG + "    sendMessage   onConnectionChanged   device.getConnectionState()=" + device.getConnectionState() + "   uuid=" + tempUuid);
                if (mConnectStatusListener != null) {
                    mConnectStatusListener.connectionState(tempUuid, device.getConnectionState());
                }
            }

            @Override
            public void onConnectFailed(BleRssiDevice device, int errorCode) {//连接失败
                super.onConnectFailed(device, errorCode);
                String tempUuid = BleScanConnectDataManager.getInstance().getDeviceUuidByMac(device.getBleAddress(), uuid);
                LgUtils.w("writeByBluetooth   BleManager.isDeviceBusy(device)  onConnectFailed  111111="
                        + device.getBleAddress() + "  2222222=" + BleScanConnectDataManager.getInstance()
                        .getProductInfoBeanByUuid(tempUuid).getAddress()
                        + "      " + TextUtils.equals(device.getBleAddress(), BleScanConnectDataManager.getInstance()
                        .getProductInfoBeanByUuid(tempUuid).getAddress())
                        + "    uuid=" + tempUuid);
                LgUtils.w(TAG + "    sendMessage   onConnectFailed   errorCode=" + errorCode + "   retryCount=" + retryCount + "   uuid=" + tempUuid);
                retryCount++;
                if (mConnectStatusListener != null) {
                    mConnectStatusListener.connectionState(tempUuid, device.getConnectionState());
                    if (retryCount > 5) {
                        mConnectStatusListener.connectFail(tempUuid);
                    }
                }
            }

            @Override
            public void onServicesDiscovered(BleRssiDevice device, BluetoothGatt gatt) {//服务端连接信息
                super.onServicesDiscovered(device, gatt);
                String tempUuid = BleScanConnectDataManager.getInstance().getDeviceUuidByMac(device.getBleAddress(), uuid);
                LgUtils.w(TAG + "    sendMessage   onServicesDiscovered   " + "   uuid=" + tempUuid);
                LgUtils.w("writeByBluetooth   BleManager.isDeviceBusy(device)  onServicesDiscovered  111111="
                        + device.getBleAddress() + "  2222222=" + BleScanConnectDataManager.getInstance()
                        .getProductInfoBeanByUuid(tempUuid).getAddress()
                        + "      " + TextUtils.equals(device.getBleAddress(), BleScanConnectDataManager.getInstance()
                        .getProductInfoBeanByUuid(tempUuid).getAddress())
                        + "    uuid=" + tempUuid);
                if (device.getConnectionState() == BleDevice.CONNECTED) {//连接成功。设置MTU，读写蓝牙包长度，大于128即可
                    ble.setMTU(address, 200, new BleMtuCallback<BleRssiDevice>() {
                        @Override
                        public void onMtuChanged(BleDevice device, int mtu, int status) {//设置成功，设置蓝牙设备消息通知
                            super.onMtuChanged(device, mtu, status);
                            String tempUuid = BleScanConnectDataManager.getInstance().getDeviceUuidByMac(device.getBleAddress(), uuid);
                            LgUtils.w("writeByBluetooth   BleManager.isDeviceBusy(device)  onMtuChanged  111111="
                                    + device.getBleAddress() + "  2222222=" + BleScanConnectDataManager.getInstance()
                                    .getProductInfoBeanByUuid(tempUuid).getAddress()
                                    + "      " + TextUtils.equals(device.getBleAddress(), BleScanConnectDataManager.getInstance()
                                    .getProductInfoBeanByUuid(tempUuid).getAddress())
                                    + "    uuid=" + tempUuid);
                            LgUtils.w(TAG + "    sendMessage   onMtuChanged   mtu=" + mtu + "   status=" + status + "   uuid=" + tempUuid);
                            ble.enableNotify((BleRssiDevice) device, true, new BleNotifyCallback<BleRssiDevice>() {
                                @Override
                                public void onChanged(BleRssiDevice device, BluetoothGattCharacteristic characteristic) {//监听消息变化
                                    String tempUuid = BleScanConnectDataManager.getInstance().getDeviceUuidByMac(device.getBleAddress(), uuid);
                                    LgUtils.w("writeByBluetooth   BleManager.isDeviceBusy(device)  onChanged  111111="
                                            + device.getBleAddress() + "  2222222=" + BleScanConnectDataManager.getInstance()
                                            .getProductInfoBeanByUuid(tempUuid).getAddress()
                                            + "      " + TextUtils.equals(device.getBleAddress(), BleScanConnectDataManager.getInstance()
                                            .getProductInfoBeanByUuid(tempUuid).getAddress())
                                            + "    uuid=" + tempUuid);
                                    LgUtils.w(TAG + "    sendMessage   enableNotify   onChanged -->>>" + ByteUtils.byteArrayToHexString(characteristic.getValue()) + "   uuid=" + tempUuid + "  mConnectStatusListener=" + mConnectStatusListener);
                                    if (mConnectStatusListener != null)
                                        mConnectStatusListener.notifyChanged(tempUuid, characteristic);
                                }

                                @Override
                                public void onNotifySuccess(BleRssiDevice device) {//通知成功，在写入发送消息数据
                                    super.onNotifySuccess(device);
                                    String tempUuid = BleScanConnectDataManager.getInstance().getDeviceUuidByMac(device.getBleAddress(), uuid);
                                    LgUtils.w("writeByBluetooth   BleManager.isDeviceBusy(device)  onNotifySuccess  111111="
                                            + device.getBleAddress() + "  2222222=" + BleScanConnectDataManager.getInstance()
                                            .getProductInfoBeanByUuid(tempUuid).getAddress()
                                            + "      " + TextUtils.equals(device.getBleAddress(), BleScanConnectDataManager.getInstance()
                                            .getProductInfoBeanByUuid(tempUuid).getAddress())
                                            + "    uuid=" + tempUuid);
                                    LgUtils.w(TAG + "    sendMessage   enableNotify  onNotifySuccess" + "   uuid=" + tempUuid);
                                    if (mConnectStatusListener != null) {
                                        mConnectStatusListener.notifySuccess(tempUuid, device);
                                    }
                                }

                                @Override
                                public void onNotifyFailed(BleRssiDevice device, int failedCode) {//通知失败
                                    super.onNotifyFailed(device, failedCode);
                                    String tempUuid = BleScanConnectDataManager.getInstance().getDeviceUuidByMac(device.getBleAddress(), uuid);
                                    LgUtils.w("writeByBluetooth   BleManager.isDeviceBusy(device)  onNotifyFailed  111111="
                                            + device.getBleAddress() + "  2222222=" + BleScanConnectDataManager.getInstance()
                                            .getProductInfoBeanByUuid(tempUuid).getAddress()
                                            + "      " + TextUtils.equals(device.getBleAddress(), BleScanConnectDataManager.getInstance()
                                            .getProductInfoBeanByUuid(tempUuid).getAddress())
                                            + "    uuid=" + tempUuid);
                                    LgUtils.w(TAG + "    sendMessage   enableNotify  onNotifyFailed  failedCode=" + failedCode + "   uuid=" + tempUuid);
                                }

                            });

                        }
                    });
                }
            }
        });
    }

    public interface BleScanListener {
        /**
         * 扫描数据回调
         *
         * @param productInfoBean product info
         * @param devConfigType 设备配网状态 默认
         *          0：未显示在搜索页面，需要查询接口；
         *          1、已显示且继续扫描更新设备为已配网状态，不调接口，需要在搜索页面隐藏
         *          2、已显示且继续扫描更新设备为非已配网状态，不掉接口，更新搜索页面设备状态
         */
        void bleScanResultCallBack(ProductInfoBean productInfoBean, int devConfigType);
    }

    /**
     * 根据发送数据，转化为写入数据
     */
    public static EntityData getEntityData(String address, byte[] bytes) {
        EntityData entityData = new EntityData();
        entityData.setAddress(address);
        entityData.setAutoWriteMode(true);
        entityData.setPackLength(128);
        entityData.setData(bytes);
        entityData.setDelay(800);
        entityData.setLastPackComplete(false);
        return entityData;
    }

    /**
     * 断开连接
     */
    public static void disConnect(String address) {
        BleRssiDevice bleDevice = new BleRssiDevice(address, "RY");
        ble.disconnect(bleDevice);
    }

    /**
     * 断开连接且移除数据
     */
    public static void close(String address) {
        BleRssiDevice bleDevice = new BleRssiDevice(address, "RY");
        ble.disconnect(bleDevice, new BleConnectCallback<BleRssiDevice>() {
            @Override
            public void onConnectionChanged(BleRssiDevice device) {
                if (device.isDisconnected()) {
                    boolean isClear = ble.refreshDeviceCache(address);
                    LgUtils.e(TAG + " released 释放蓝牙数据 isClear=" + isClear + " address=" + address);
                }
                LgUtils.e(TAG + " released 释放蓝牙数据 device=" + device.getConnectionState() + " address=" + address);
            }
        });
    }

    public static void open(String address) {
        ble.turnOnBlueToothNo();
    }

    /**
     * 分包写入蓝牙数据
     */
    public static void writeData(String uuid, BleRssiDevice device, byte[] writeBytes, WriteListener writeListener) {
        ble.write(device, writeBytes, new BleWriteCallback<BleRssiDevice>() {
            @Override
            public void onWriteSuccess(BleRssiDevice device, BluetoothGattCharacteristic characteristic) {
                LgUtils.w(TAG + "    sendMessage   writeEntity   onWriteSuccess=" +
                        " key=  " + (uuid + BleScanConnectDataManager.getInstance().getMessageType(uuid)));
                if (writeListener != null) {
                    writeListener.writeSuccess();
                }
            }

            @Override
            public void onWriteFailed(BleRssiDevice device, int failedCode) {
                super.onWriteFailed(device, failedCode);

                LgUtils.w(TAG + "    sendMessage   writeEntity   onWriteFailed=" + failedCode +
                        " key=  " + (uuid + BleScanConnectDataManager.getInstance().getMessageType(uuid)));
            }
        });
        /// ble.writeEntity(getEntityData(device.getBleAddress(), writeBytes), new BleWriteEntityCallback<BleRssiDevice>() {
        ///     @Override
        ///     public void onWriteSuccess() {
        ///         LgUtils.w(TAG + "    sendMessage   writeEntity   onWriteSuccess=");
        ///     }
        //
        ///     @Override
        ///     public void onWriteFailed() {
        ///         LgUtils.w(TAG + "    sendMessage   writeEntity   onWriteFailed=");
        ///     }
        /// });
    }

    /**
     * 断开所有蓝牙设备连接
     */
    public static void disconnectAll() {
        ble.disconnectAll();
    }

    /**
     * 断开所有蓝牙设备连接
     */
    public static void closeAll() {
        ble.closeAll();
    }

}
