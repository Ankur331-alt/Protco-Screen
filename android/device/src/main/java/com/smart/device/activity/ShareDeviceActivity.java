package com.smart.device.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;

import com.google.gson.Gson;
import com.smart.device.R;
import com.smart.device.databinding.ActivityShareDeviceBinding;
import com.smart.device.viewmodel.ShareDeviceViewModel;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.StatusBarUtil;


/**
 * @author edwin
 */
public class ShareDeviceActivity
        extends BaseActivity<ActivityShareDeviceBinding, ShareDeviceViewModel>
{

    private ShareDeviceMethod shareDeviceMethod = ShareDeviceMethod.QR_CODE;

    /**
     * 获取toolbar标题
     */
    @Override
    public String getToolBarTitle() {
        return null;
    }

    /**
     * 初始化数据
     */
    @Override
    public void init() {
        StatusBarUtil.setTransparentFullTopWindStatusBar(this);
        String shareMethod = getIntent().getStringExtra(ShareDeviceMethod.SHARE_METHOD_KEY);
        Log.d(TAG, "init: share method: " + shareMethod);
        if(null != shareMethod && ShareDeviceMethod.PIN_CODE.getValue().contentEquals(shareMethod)){
            shareDeviceMethod = ShareDeviceMethod.PIN_CODE;
        }

        // Setup observers
        setupObservers();

        // start device sharing
        String deviceId = getIntent().getStringExtra(ShareDeviceMethod.DEVICE_ID_KEY);
        DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(deviceId);
        mViewModel.shareDevice(getApplicationContext(), deviceInfo);
    }

    private void setupObservers() {
        // observe status
        mViewModel.getShareStatusLiveData().observe(this, status -> {
            Log.d(TAG, "setupObservers: status=" + new Gson().toJson(status));
            if(status instanceof  ShareDeviceStatus.InProgress) {
                mViewModel.showLoading();
            }else {
                mViewModel.hideLoading();
                if(status instanceof  ShareDeviceStatus.Failed){
                    displayError();
                }
            }
        });

        // observe share payload data
        mViewModel.getSharePayloadLiveData().observe(this, payload -> {
            Log.d(TAG, "setupObservers: share payload=" + new Gson().toJson(payload));
            if(shareDeviceMethod.equals(ShareDeviceMethod.QR_CODE)){
                int foregroundColor = getColor(R.color.black);
                int backgroundColor = getColor(R.color.white);
                mViewModel.generateQrCodePayload(
                        payload.getQrCode(), foregroundColor, backgroundColor
                );
            }else {
                mViewModel.formatManualCodePayload(payload.getManualCode());
            }
        });

        // observe qr code
        mViewModel.getShareQrCodeLiveData().observe(this, this::displayQrCode);

        // observe manual entry code
        mViewModel.getSharePairingCodeLiveData().observe(this, this::displayPinCode);
    }

    private void displayQrCode(Bitmap qrCode){
        binding.ivQrCode.setVisibility(View.VISIBLE);
        binding.ivQrCode.setImageBitmap(qrCode);
        binding.llParingCode.setVisibility(View.GONE);
        binding.tvTitle.setText(getString(R.string.share_with_qr_code_title));
        binding.tvShareDeviceHint.setText(getString(R.string.share_with_qr_code_hint));
    }

    private void displayPinCode(String pinCode){
        binding.ivQrCode.setVisibility(View.GONE);
        binding.llParingCode.setVisibility(View.VISIBLE);
        binding.tvParingCode.setText(pinCode);
        binding.btnCopyParingCode.setOnClickListener(view -> {
            // put the contents on clip board
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });
        binding.tvTitle.setText(getString(R.string.share_with_pairing_code_title));
        binding.tvShareDeviceHint.setText(getString(R.string.share_with_pairing_code_hint));
    }

    private void displayError(){
        binding.llParingCode.setVisibility(View.GONE);
        binding.tvTitle.setText(getString(R.string.share_device_error_title));
        binding.tvShareDeviceHint.setText(getString(R.string.share_device_error_hint));
        binding.tvShareDeviceHint.setVisibility(View.VISIBLE);
        Drawable drawable = AppCompatResources.getDrawable(this, R.drawable.ic_disable_tip);
        if(null == drawable){
            return;
        }
        drawable.setTint(getResources().getColor(R.color.c_EF5641, null));
        binding.ivQrCode.setImageDrawable(drawable);
        binding.ivQrCode.setVisibility(View.VISIBLE);
    }

    /**
     * 获取控件使用的binding
     *
     * @param inflater the layout inflater
     */
    @Override
    public ActivityShareDeviceBinding getBinding(LayoutInflater inflater) {
        return ActivityShareDeviceBinding.inflate(inflater);
    }
}
