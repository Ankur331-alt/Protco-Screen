package com.smart.rinoiot.common.ble;

import com.smart.rinoiot.common.bean.ProductInfoBean;

/**NFC 连接回调*/
public interface ConnectCallback {
    void onScanSuccess(ProductInfoBean result);

    void onRegisterCloudResult(String uuid, SignBleConfigBean signBleConfigBean);

    void onConnectFail(String uuid);
}
