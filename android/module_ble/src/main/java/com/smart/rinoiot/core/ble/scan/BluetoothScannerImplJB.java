package com.smart.rinoiot.core.ble.scan;

import android.bluetooth.BluetoothAdapter;

import com.smart.rinoiot.core.ble.callback.wrapper.ScanWrapperCallback;


class BluetoothScannerImplJB extends BleScannerCompat {

    @Override
    public void startScan(ScanWrapperCallback scanWrapperCallback) {
        super.startScan(scanWrapperCallback);
        bluetoothAdapter.startLeScan(leScanCallback);
    }

    @Override
    public void stopScan() {
        super.stopScan();
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    private final BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> scanWrapperCallback.onLeScan(device, rssi, scanRecord);
}
