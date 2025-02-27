package com.smart.rinoiot.common.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author tw
 * @time 2022/10/14 17:24
 * @description 进入蓝牙搜索时，实时监听广播
 */
public class BluetoothMonitorReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
//                        case BluetoothAdapter.STATE_TURNING_ON:
//                            Toast.makeText(context,"蓝牙正在打开",Toast.LENGTH_SHORT).show();
//                            break;
                    case BluetoothAdapter.STATE_ON:
                        if (bleStatusListener != null) {
                            bleStatusListener.callBack(true);
                        }
//                            Toast.makeText(context,"蓝牙已经打开",Toast.LENGTH_SHORT).show();
                        break;
//                        case BluetoothAdapter.STATE_TURNING_OFF:
//                            Toast.makeText(context,"蓝牙正在关闭",Toast.LENGTH_SHORT).show();
//                            break;
                    case BluetoothAdapter.STATE_OFF:
                        if (bleStatusListener != null) {
                            bleStatusListener.callBack(false);
                        }
//                            Toast.makeText(context,"蓝牙已经关闭",Toast.LENGTH_SHORT).show();
                        break;
                }

//                case BluetoothDevice.ACTION_ACL_CONNECTED:
//                    Toast.makeText(context,"蓝牙设备已连接",Toast.LENGTH_SHORT).show();
//                    break;
//
//                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
//                    Toast.makeText(context,"蓝牙设备已断开",Toast.LENGTH_SHORT).show();
//                    break;
            }

        }
    }

    private BleStatusListener bleStatusListener;

    public void setBleStatusListener(BleStatusListener bleStatusListener) {
        this.bleStatusListener = bleStatusListener;
    }

    public interface BleStatusListener {
        void callBack(boolean open);
    }
}
