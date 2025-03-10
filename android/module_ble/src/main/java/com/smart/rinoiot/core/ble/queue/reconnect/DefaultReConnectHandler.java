package com.smart.rinoiot.core.ble.queue.reconnect;

import android.text.TextUtils;
import androidx.annotation.RestrictTo;

import com.smart.rinoiot.core.ble.BleLog;
import com.smart.rinoiot.core.ble.callback.BleConnectCallback;
import com.smart.rinoiot.core.ble.model.BleDevice;
import com.smart.rinoiot.core.ble.queue.ConnectQueue;
import com.smart.rinoiot.core.ble.queue.RequestTask;
import com.smart.rinoiot.core.ble.request.ConnectRequest;
import com.smart.rinoiot.core.ble.request.Rproxy;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * author: jerry
 * date: 20-11-30
 * email: superliu0911@gmail.com
 * des:
 */
public class DefaultReConnectHandler<T extends BleDevice> extends BleConnectCallback<T> {
    private static final String TAG = "DefaultReConnectHandler";
    public static final long DEFAULT_CONNECT_DELAY = 2000L;
    private static DefaultReConnectHandler defaultReConnectHandler;
    private final ArrayList<T> autoDevices = new ArrayList<>();

    private DefaultReConnectHandler() {}

    public static <T extends BleDevice>DefaultReConnectHandler<T> provideReconnectHandler() {
        if (defaultReConnectHandler == null){
            defaultReConnectHandler = new DefaultReConnectHandler();
        }
        return defaultReConnectHandler;
    }

    public boolean reconnect(T device){
        BleLog.e(TAG, "reconnect>>>>>: "+autoDevices.size());
        for (T autoDevice : autoDevices) {
            if (TextUtils.equals(autoDevice.getBleAddress(), device.getBleAddress())){
                ConnectRequest<T> connectRequest = Rproxy.getRequest(ConnectRequest.class);
                return connectRequest.connect(device);
            }
        }
        return false;
    }

    /**
     * Add a disconnected device to the autopool
     *
     * @param device Device object
     */
    private void addAutoPool(T device) {
        if (device == null) return;
        if (device.isAutoConnect()) {
            BleLog.d(TAG, "addAutoPool: "+"Add automatic connection device to the connection pool");
            if (!autoDevices.contains(device)){
                autoDevices.add(device);
            }
            RequestTask requestTask = new RequestTask.Builder()
                    .devices(device)
                    .delay(DEFAULT_CONNECT_DELAY)
                    .build();
            ConnectQueue.getInstance().put(requestTask);
        }
    }

    /**
     * If it is automatically connected device is removed from the automatic connection pool
     *
     * @param device Device object
     */
    private void removeAutoPool(T device) {
        if (device == null) return;
        Iterator<T> iterator = autoDevices.iterator();
        while (iterator.hasNext()) {
            BleDevice item = iterator.next();
            if (device.getBleAddress().equals(item.getBleAddress())) {
                iterator.remove();
            }
        }
    }

    public void resetAutoConnect(T device, boolean autoConnect){
        if (device == null)return;
        device.setAutoConnect(autoConnect);
        if (!autoConnect){
            removeAutoPool(device);
            if (device.isConnecting()){
                ConnectRequest<T> connectRequest = Rproxy.getRequest(ConnectRequest.class);
                connectRequest.disconnect(device);
            }
        }else {//重连
            addAutoPool(device);
        }
    }

    //取消所有需要自动重连的设备
    public void cancelAutoConnect(){
        autoDevices.clear();
    }

    /**
     * 打开蓝牙后,重新连接异常断开时的设备
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public void openBluetooth(){
        BleLog.i(TAG, "auto devices size："+autoDevices.size());
        for (T device: autoDevices) {
            addAutoPool(device);
        }
    }

    /*@Override
    public ReconnectStrategy strategy() {
        return new ReconnectStrategy.Builder()
                .delay(DEFAULT_CONNECT_DELAY)
                .reconnectIfOpenBluetooth(true)
                .times(3)
                .build();
    }*/

    @Override
    public void onConnectionChanged(T device) {
        if (device.isConnected()){
            /*After the success of the connection can be considered automatically reconnect.
            If it is automatically connected device is removed from the automatic connection pool*/
            removeAutoPool(device);
            BleLog.e(TAG, "onConnectionChanged: removeAutoPool");
        }else if (device.isDisconnected()){
            addAutoPool(device);
            BleLog.e(TAG, "onConnectionChanged: addAutoPool");
        }
    }
}
