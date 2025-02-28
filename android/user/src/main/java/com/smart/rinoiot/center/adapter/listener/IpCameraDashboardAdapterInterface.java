package com.smart.rinoiot.center.adapter.listener;

import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.ipcimpl.RinoIPCContainerView;

/**
 * @author edwin
 */
public interface IpCameraDashboardAdapterInterface {

    interface ItemClickListener{
        void onClick(RinoIPCContainerView ipcContainerView,DeviceInfoBean deviceInfoBean);

        /**
         * Invoked when a device item is clicked.
         * @param position the position of the clicked item
         * @param deviceInfoBean the device info for the clicked view.
         */
        void onChildClick(int position, DeviceInfoBean deviceInfoBean);
    }
}
