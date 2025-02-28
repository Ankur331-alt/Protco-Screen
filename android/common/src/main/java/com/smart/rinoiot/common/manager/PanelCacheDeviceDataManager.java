package com.smart.rinoiot.common.manager;

import com.smart.rinoiot.common.bean.DeviceInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tw
 * @time 2023/4/12 18:47
 * @description 当前所在面板容器中设备数据缓存
 */
public class PanelCacheDeviceDataManager {
    private static PanelCacheDeviceDataManager instance;

    public static PanelCacheDeviceDataManager getInstance() {
        if (instance == null) {
            instance = new PanelCacheDeviceDataManager();
        }
        return instance;
    }

    private List<DeviceInfoBean> panelDeviceDataList = new ArrayList<>();

    public List<DeviceInfoBean> getPanelDeviceDataList() {
        return panelDeviceDataList;
    }

    /**
     * 添加面板数据
     */
    public void addPanelDeviceData(DeviceInfoBean deviceInfoBean) {
        panelDeviceDataList.add(deviceInfoBean);
    }

    /**
     * 添加面板数据
     */
    public void removePanelDeviceData(DeviceInfoBean deviceInfoBean) {
        if (!panelDeviceDataList.isEmpty() && panelDeviceDataList.contains(deviceInfoBean)) {
            panelDeviceDataList.remove(deviceInfoBean);
        }
    }

    /**
     * 清除所有面板数据
     */
    public void clearPanelDeviceData() {
        panelDeviceDataList.clear();
    }
}
