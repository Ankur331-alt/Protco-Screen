package com.smart.rinoiot.core.ble.request;


import com.smart.rinoiot.core.ble.Ble;
import com.smart.rinoiot.core.ble.BleRequestImpl;
import com.smart.rinoiot.core.ble.annotation.Implement;
import com.smart.rinoiot.core.ble.callback.BleMtuCallback;
import com.smart.rinoiot.core.ble.callback.wrapper.BleWrapperCallback;
import com.smart.rinoiot.core.ble.callback.wrapper.MtuWrapperCallback;
import com.smart.rinoiot.core.ble.model.BleDevice;

/**
 *
 * Created by LiuLei on 2017/10/23.
 */
@Implement(MtuRequest.class)
public class MtuRequest<T extends BleDevice> implements MtuWrapperCallback<T> {

    private BleMtuCallback<T> bleMtuCallback;
    private final BleWrapperCallback<T> bleWrapperCallback = Ble.options().getBleWrapperCallback();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public boolean setMtu(String address, int mtu, BleMtuCallback<T> callback){
        this.bleMtuCallback = callback;
        return bleRequest.setMtu(address, mtu);
    }

    @Override
    public void onMtuChanged(T device, int mtu, int status) {
        if(null != bleMtuCallback){
            bleMtuCallback.onMtuChanged(device, mtu, status);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onMtuChanged(device, mtu, status);
        }
    }
}
