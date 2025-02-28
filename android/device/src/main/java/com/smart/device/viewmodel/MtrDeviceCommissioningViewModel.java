package com.smart.device.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.dsh.matter.management.device.DeviceCommissionCallback;
import com.dsh.matter.model.CommissioningErrorCode;
import com.dsh.matter.model.device.MtrDevice;
import com.google.gson.Gson;
import com.smart.device.manager.DeviceNetworkManager;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.matter.MtrDeviceManager;
import com.smart.rinoiot.common.matter.model.BatchDeviceBindingStatus;
import com.smart.rinoiot.common.matter.model.DeviceBindingErrorCode;
import com.smart.rinoiot.common.matter.model.DeviceBindingStatus;
import com.smart.rinoiot.common.matter.model.MtrDiscoverableDevice;
import com.smart.rinoiot.common.matter.model.MtrDiscoverableNode;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author edwin
 */
public class MtrDeviceCommissioningViewModel extends BaseViewModel {

    private static final String TAG = "MtrDeviceCommissioningViewModel";
    private Disposable devicePairingDisposableJob;
    private final AtomicInteger batchPosition = new AtomicInteger(0);
    private final List<MtrDiscoverableDevice> mtrDiscoverableDeviceList = new ArrayList<>();
    private final MutableLiveData<BatchDeviceBindingStatus> batchCommissioningStatus =
            new MutableLiveData<>();
    private final MutableLiveData<List<MtrDiscoverableDevice>> discoverableDevicesLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> deviceStatusChangeLiveData =  new MutableLiveData<>();
    private final MutableLiveData<Integer> nextDevicePairingJobLiveData = new MutableLiveData<>();
    public MtrDeviceCommissioningViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<MtrDiscoverableDevice>> getDiscoverableDevicesLiveData() {
        return discoverableDevicesLiveData;
    }

    public MutableLiveData<Integer> getBindingStatusChangeLiveData() {
        return deviceStatusChangeLiveData;
    }

    public MutableLiveData<BatchDeviceBindingStatus> getBatchCommissioningStatus() {
        return batchCommissioningStatus;
    }

    public MutableLiveData<Integer> getNextDevicePairingJobLiveData() {
        return nextDevicePairingJobLiveData;
    }

    /**
     * Request matter device discovery.
     * @param homeId home identifier
     */
    public void requestDeviceDiscovery(String homeId) {
        if(StringUtil.isBlank(homeId)) {
            return;
        }

        batchCommissioningStatus.postValue(new BatchDeviceBindingStatus.Discovering());
        CallbackListener<Object> callback = new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                Log.d(TAG, "onSuccess: data=" + new Gson().toJson(data));

            }

            @Override
            public void onError(String code, String error) {
                batchCommissioningStatus.postValue(
                        new BatchDeviceBindingStatus.Error()
                );
                Log.e(TAG, "onError: code=" + code + " | error=" + error);
                ToastUtil.showErrorMsg(error);
            }
        };
        DeviceNetworkManager.getInstance().matterFind(homeId, callback);
    }

    /**
     * Processes the matter discoverable nodes
     * @param homeId home identifier
     * @param nodes the nodes
     */
    public void processDiscoverableNodes(String homeId, List<MtrDiscoverableNode> nodes) {
        if(StringUtil.isBlank(homeId) || null == nodes || nodes.isEmpty()){
            Log.e(TAG, "processDiscoverableNodes: missing args");
            batchCommissioningStatus.postValue(
                    new BatchDeviceBindingStatus.Error()
            );
            return;
        }

        List<DeviceInfoBean> deviceList = CacheDataManager.getInstance().getAllDeviceList(homeId);
        if(null == deviceList || deviceList.isEmpty()){
            mtrDiscoverableDeviceList.clear();
            discoverableDevicesLiveData.postValue(mtrDiscoverableDeviceList);
            batchCommissioningStatus.postValue(
                    new BatchDeviceBindingStatus.Error()
            );
            return;
        }

        Predicate<DeviceInfoBean> mtrDevicePredicate = MtrDeviceDataUtils::isMatterDevice;
        List<MtrDiscoverableDevice> discoverableDevices = deviceList.stream().filter(
                mtrDevicePredicate
        ).map(deviceInfo -> {
            if (StringUtil.isBlank(deviceInfo.getId())) {
                return null;
            }

            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
            if (MtrDeviceDataUtils.isNotEmpty(metadata)) {
                return null;
            }

            // find the device in nodes
            Predicate<MtrDiscoverableNode> nodePredicate =
                    node -> node.getDeviceId().contentEquals(deviceInfo.getId());
            Optional<MtrDiscoverableNode> findFirstResult = nodes.stream().filter(
                    nodePredicate
            ).findFirst();
            if(!findFirstResult.isPresent()){
                return null;
            }

            MtrDiscoverableNode discoverableNode = findFirstResult.get();
            return new MtrDiscoverableDevice(
                    deviceInfo.getId(),
                    deviceInfo.getName(), deviceInfo.getImageUrl(),
                    discoverableNode.getPayload()
            );
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if(discoverableDevices.isEmpty()){
            batchCommissioningStatus.postValue(new BatchDeviceBindingStatus.Error());
        }else{
            mtrDiscoverableDeviceList.clear();
            mtrDiscoverableDeviceList.addAll(discoverableDevices);
            discoverableDevicesLiveData.postValue(mtrDiscoverableDeviceList);
        }
    }

    /**
     * Clears the list of discoverable devices
     */
    public void clearDiscoverableDevices() {
        if(mtrDiscoverableDeviceList.isEmpty()){
            return;
        }

        // cancel any running job
        if(null != devicePairingDisposableJob){
            devicePairingDisposableJob.dispose();
        }

        // stop matter device pairing
        MtrDeviceManager.getInstance(mContext.getApplicationContext()).stopDevicePairing();

        // delete pair matter devices.
        mtrDiscoverableDeviceList.forEach(mtrDiscoverableDevice -> {
            try {
                if(!(mtrDiscoverableDevice.getStatus() instanceof DeviceBindingStatus.Success)){
                    return;
                }

                if (MtrDeviceDataUtils.isEmpty(mtrDiscoverableDevice.getMetadata())) {
                    return;
                }

                Metadata metadata = mtrDiscoverableDevice.getMetadata();
                MtrDeviceManager.getInstance(mContext.getApplicationContext())
                        .remove(metadata.getDeviceId());
            }catch (Exception exception) {
                Log.e(TAG, "Failed to delete. Cause=" + exception.getLocalizedMessage());
            }
        });
        mtrDiscoverableDeviceList.clear();
    }

    /**
     * Pairs the next device.
     */
    public void pairNextDevice() {
        batchCommissioningStatus.postValue(new BatchDeviceBindingStatus.Pairing());
        if(batchPosition.get() >= mtrDiscoverableDeviceList.size()){
            boolean status = getDevicePairingJobStatus();
            Log.d(TAG, "pairNextDevice: status = " + status);
            batchCommissioningStatus.postValue(
                    new BatchDeviceBindingStatus.Paired(status)
            );
            return;
        }

        // get the device to be commissioned
        MtrDiscoverableDevice mtrDiscoverableDevice = mtrDiscoverableDeviceList.get(
                batchPosition.get()
        );

        if(null != devicePairingDisposableJob){
            devicePairingDisposableJob.dispose();
        }

        devicePairingDisposableJob = Completable.create(emitter -> {
                DeviceCommissionCallback callback = new DeviceCommissionCallback() {
                    @Override
                    public void onWiFiCredentialsRequired(long devicePtr) {
                        Log.d(TAG, "Requesting wifi credentials devicePtr=" + devicePtr);
                        updateBindingStatus(new DeviceBindingStatus.Failed(
                                DeviceBindingErrorCode.NotFound, ""
                        ));
                        emitter.onComplete();
                    }

                    @Override
                    public void onSuccess(@NonNull MtrDevice mtrDevice) {
                        Log.e(TAG, "Device commissioned: " + new Gson().toJson(mtrDevice));
                        Metadata metadata = MtrDeviceDataUtils.toMetadata(mtrDevice);
                        updateBindingStatus(new DeviceBindingStatus.Success<>(metadata));
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(@NonNull CommissioningErrorCode code, @Nullable String msg) {
                        Log.e(TAG, "Cause: " + new Gson().toJson(code));
                        updateBindingStatus(new DeviceBindingStatus.Failed(
                                DeviceBindingErrorCode.NotFound, ""
                        ));
                        emitter.onComplete();
                    }

                    @Override
                    public void onAttestationFailure(
                            @NonNull CommissioningErrorCode code, long devicePtr
                    ) {
                        Log.w(TAG, "code=" + code + " | devicePtr=" + devicePtr);
                        continueDevicePairing(devicePtr, true);
                        emitter.onComplete();
                    }
                };

                String qrCode = mtrDiscoverableDevice.getSharePayload().getQrCode();
                MtrDeviceManager.getInstance(mContext.getApplicationContext())
                        .startDevicePairing(qrCode, callback);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    () -> {
                        Log.d(TAG, "pairNextDevice: completed commissioning with success");
                        nextDevicePairingJobLiveData.postValue(batchPosition.getAndIncrement());
                    },
                    throwable -> {
                        Log.e(TAG, "pairNextDevice: completed commissioning with error");
                        nextDevicePairingJobLiveData.postValue(batchPosition.getAndIncrement());
                    }
            );
    }

    /**
     * Update the device binding status
     * @param status the binding status
     */
    private void updateBindingStatus(DeviceBindingStatus status) {
        int position = batchPosition.get();
        Log.d(TAG, "updateBindingStatus: position=" + position);
        if(position >= mtrDiscoverableDeviceList.size()){
            return;
        }

        // update device status
        mtrDiscoverableDeviceList.get(position).setStatus(status);
        deviceStatusChangeLiveData.postValue(position);
    }

    /**
     * Update the device binding status
     * @param status the binding status
     */
    private void updateBindingStatus(DeviceBindingStatus.Success<Metadata> status) {
        int position = batchPosition.get();
        Log.d(TAG, "updateBindingStatus: position=" + position);
        if(position >= mtrDiscoverableDeviceList.size()){
            return;
        }

        // update device status
        mtrDiscoverableDeviceList.get(position).setStatus(status);
        mtrDiscoverableDeviceList.get(position).setMetadata(status.getData());
        deviceStatusChangeLiveData.postValue(position);
    }

    /**
     * Continues device commissioning
     *
     * @param devicePtr   device pointer.
     * @param ignoreError whether to ignore device attestation failure or not.
     * @apiNote This might be overkill. This method might not be necessary
     */
    public void continueDevicePairing(long devicePtr, boolean ignoreError) {
        MtrDeviceManager.getInstance(mContext.getApplicationContext())
                .continueDevicePairing(devicePtr, ignoreError);
    }

    public boolean getDevicePairingJobStatus() {
        Predicate<MtrDiscoverableDevice> paringPredicate =
                device -> device.getStatus() instanceof DeviceBindingStatus.Success;

        Optional<MtrDiscoverableDevice> anySuccess = mtrDiscoverableDeviceList.stream()
                .filter(paringPredicate).findAny();
        return anySuccess.isPresent();
    }

    /**
     * Uploads matter device to cloud
     *
     * @param homeId home identifier
     */
    public void uploadCommissionedDevices(String homeId) {
        if (mtrDiscoverableDeviceList.isEmpty()) {
            Log.e(TAG, "uploadCommissionedDevices: matter device list is empty");
            batchCommissioningStatus.postValue(
                    new BatchDeviceBindingStatus.Error()
            );
            return;
        }

        // notify
        batchCommissioningStatus.postValue(new BatchDeviceBindingStatus.Uploading());
        if(null != devicePairingDisposableJob) {
            devicePairingDisposableJob.dispose();
        }

        // start uploading devices
        devicePairingDisposableJob = Observable.fromIterable(mtrDiscoverableDeviceList)
                .flatMapCompletable(mtrDiscoverableDevice -> Completable.create(emitter -> {
                    DeviceBindingStatus status = mtrDiscoverableDevice.getStatus();
                    Log.d(TAG, "uploadCommissionedDevices: status =" + new Gson().toJson(status));
                    if(!(status instanceof DeviceBindingStatus.Success)){
                        emitter.onComplete();
                        return;
                    }

                    Metadata metadata = mtrDiscoverableDevice.getMetadata();
                    uploadMatterDevice(homeId, metadata, emitter);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Log.d(TAG, "uploadCommissionedDevices: well");
                            batchCommissioningStatus.postValue(
                                new BatchDeviceBindingStatus.Completed(true)
                            );
                        },
                        throwable -> {
                            Log.e(TAG, "uploadCommissionedDevices: failed");
                            batchCommissioningStatus.postValue(
                                    new BatchDeviceBindingStatus.Completed(false)
                            );
                        }
                );
    }

    /**
     * Uploads matter devices to cloud
     * @param homeId the home identifier
     * @param metadata the metadata
     */
    private void uploadMatterDevice(String homeId, Metadata metadata, CompletableEmitter emitter){
        String deviceUuid = MtrDeviceDataUtils.toRinoDeviceId(metadata);
        Map<String, Object> map = new HashMap<>(6);
        map.put("assetId", homeId);
        map.put("deviceUuid", deviceUuid);
        map.put("networkType", 0);
        map.put("productId", MtrDeviceDataUtils.toRinoProductId(metadata));
        map.put("productType", "rino");
        map.put("metaInfo", MtrDeviceDataUtils.toMetaInfo(metadata));

        DeviceNetworkManager.getInstance().bindMatter(map, new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                Log.d(TAG, "onSuccess: upload data=" + data);
                emitter.onComplete();
            }

            @Override
            public void onError(String code, String error) {
                Log.e(TAG, "onError: Failed to add matter device to cloud. Cause: " + error);
                ToastUtil.showErrorMsg(error);
                try {
                    MtrDeviceManager.getInstance(mContext.getApplicationContext())
                            .remove(metadata.getDeviceId());
                }catch (Exception exception){
                    Log.d(TAG, "onError: Matter device clean up failed. Cause= "+ exception.getLocalizedMessage());
                }
                emitter.onComplete();
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(null != devicePairingDisposableJob){
            devicePairingDisposableJob.dispose();
        }
    }
}
