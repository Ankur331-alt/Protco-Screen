package com.smart.device.viewmodel;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.dsh.matter.model.CommissioningErrorCode;
import com.dsh.matter.model.scanner.DeviceSharePayload;
import com.dsh.matter.util.scanner.QrCodeGenerator;
import com.smart.device.activity.ShareDeviceStatus;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.matter.MtrDeviceManager;
import com.smart.rinoiot.common.matter.callback.DeviceManagerCallback;
import com.smart.rinoiot.common.utils.StringUtil;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * @author edwin
 */
public class ShareDeviceViewModel extends BaseViewModel {

    private static final String TAG = "ShareDeviceViewModel";

    /**
     * QR Code width
     */
    private static final int QR_CODE_WIDTH=400;

    /**
     * QR Code height
     */
    private static final int QR_CODE_HEIGHT = 400;

    /**
     * The QR code on-boarding payload live data
     */
    private final MutableLiveData<Bitmap> shareQrCodeLiveData = new MutableLiveData<>();

    /**
     * The pairing code live data
     */
    private final MutableLiveData<String> sharePairingCodeLiveData = new MutableLiveData<>();

    /**
     * The current status of a share device task
     */
    private final MutableLiveData<ShareDeviceStatus> shareStatusLiveData = new MutableLiveData<>();

    /**
     * The device set up payload
     */
    private final MutableLiveData<DeviceSharePayload> sharePayloadLiveData = new MutableLiveData<>();

    public ShareDeviceViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Bitmap> getShareQrCodeLiveData() {
        return shareQrCodeLiveData;
    }

    public MutableLiveData<String> getSharePairingCodeLiveData() {
        return sharePairingCodeLiveData;
    }

    public MutableLiveData<ShareDeviceStatus> getShareStatusLiveData() {
        return shareStatusLiveData;
    }

    public MutableLiveData<DeviceSharePayload> getSharePayloadLiveData() {
        return sharePayloadLiveData;
    }

    public void shareDevice(
            @ApplicationContext Context context,
            DeviceInfoBean deviceInfoBean
    ) {
        shareStatusLiveData.postValue(new ShareDeviceStatus.InProgress());
        if(null == deviceInfoBean){
            String errorMessage = "The device was not found";
            shareStatusLiveData.postValue(new ShareDeviceStatus.Failed(
                    CommissioningErrorCode.ShareConfigFailed, errorMessage
            ));
            return;
        }

        // Convert device info to metadata
        Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfoBean.getMetaInfo());
        if(null == metadata || metadata.getDeviceId() <= 0){
            String errorMessage = "The device was not found";
            shareStatusLiveData.postValue(new ShareDeviceStatus.Failed(
                    CommissioningErrorCode.ShareConfigFailed, errorMessage
            ));
            return;
        }

        DeviceManagerCallback<DeviceSharePayload> callback = getDeviceManagerCallback();
        MtrDeviceManager.getInstance(context).startDeviceSharing(metadata, callback);
    }

    /**
     * Returns the device manager callback.
     * @return the device manager callback
     */
    private  DeviceManagerCallback<DeviceSharePayload> getDeviceManagerCallback() {
        return new DeviceManagerCallback<DeviceSharePayload>() {
            @Override
            public void onSuccess(DeviceSharePayload payload) {
                boolean isValid = null != payload &&
                        StringUtil.isNotBlank(payload.getManualCode()) &&
                        StringUtil.isNotBlank(payload.getQrCode());
                if(isValid){
                    sharePayloadLiveData.postValue(payload);
                }else{
                    String errorMessage = "Failed to open the commission window";
                    shareStatusLiveData.postValue(new ShareDeviceStatus.Failed(
                            CommissioningErrorCode.ShareConfigFailed, errorMessage
                    ));
                }
            }

            @Override
            public void onError(Exception error) {
                String errorMessage = String.format(
                        "onError: Failed to open commission window. Cause=%s",
                        error.getLocalizedMessage()
                );
                Log.e(TAG, errorMessage);
                errorMessage = "Failed to open the commission window";
                shareStatusLiveData.postValue(new ShareDeviceStatus.Failed(
                        CommissioningErrorCode.ShareConfigFailed, errorMessage
                ));
            }
        };
    }

    /**
     * Generates Matter QR code from setup payload
     *
     * @param qrCodeContent the QR code
     * @param foregroundColor the QR code's foreground color
     * @param backgroundColor the QR code's background color
     */
    public void generateQrCodePayload(
            String qrCodeContent, int foregroundColor, int backgroundColor
    ){
        try {
            Bitmap qrCodeBitmap = QrCodeGenerator.encodeQrCodeBitmap(
                    qrCodeContent, QR_CODE_WIDTH, QR_CODE_HEIGHT, foregroundColor, backgroundColor
            );
            shareQrCodeLiveData.postValue(qrCodeBitmap);
            shareStatusLiveData.postValue(new ShareDeviceStatus.Completed(
                    "QrCode generated successfully"
            ));
        }catch (Exception exception){
            String errorMessage = String.format(
                    "generateQRCodePayload: Failed to generate Qr code. Cause=%s",
                    exception.getLocalizedMessage()
            );
            Log.e(TAG, errorMessage);
            errorMessage = "Failed to generate QR code";
            shareStatusLiveData.postValue(new ShareDeviceStatus.Failed(
                    CommissioningErrorCode.ShareConfigFailed, errorMessage
            ));
        }
    }

    /**
     * Formats Matter pairing code
     *
     * @param manualEntryCode entry code
     */
    public void formatManualCodePayload(String manualEntryCode) {
        String code = manualEntryCode.substring(0,4) +
                "-" + manualEntryCode.substring(4, 8) +
                "-" + manualEntryCode.substring(8,11);
        sharePairingCodeLiveData.postValue(code);
        shareStatusLiveData.postValue(new ShareDeviceStatus.Completed(
                "Manual paring code generated successfully"
        ));
    }
}
