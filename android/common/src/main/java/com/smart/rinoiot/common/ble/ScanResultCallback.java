package com.smart.rinoiot.common.ble;

import com.smart.rinoiot.common.bean.ProductInfoBean;

/**
 *
 */
public interface ScanResultCallback {
    /**
     * 扫描数据回调
     *
     * @param result product info
     * @param devConfigType 设备配网状态 默认
     *          0：未显示在搜索页面，需要查询接口；
     *          1、已显示且继续扫描更新设备为已配网状态，不调接口，需要在搜索页面隐藏
     *          2、已显示且继续扫描更新设备为非已配网状态，不掉接口，更新搜索页面设备状态
     */
    void onScanResult(ProductInfoBean result,int devConfigType);
}
