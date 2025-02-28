package com.smart.device.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.smart.device.databinding.DialogShareDeviceMothodsBinding;
import com.smart.device.listener.OnPinCodeListener;
import com.smart.device.listener.OnQrCodeListener;

import java.util.Objects;

/**
 * @author edwin
 */
public class ShareDeviceMethodsDialog extends AlertDialog {

    private OnQrCodeListener qrCodeListener;

    private OnPinCodeListener pinCodeListener;

    private DialogShareDeviceMothodsBinding binding;

    protected ShareDeviceMethodsDialog(Context context) {
        super(context);
        this.binding = DialogShareDeviceMothodsBinding.inflate(getLayoutInflater());
    }

    public void setQrCodeListener(OnQrCodeListener qrCodeListener) {
        this.qrCodeListener = qrCodeListener;
        binding.llQrCode.setOnClickListener(view -> {
            this.qrCodeListener.onQrCode(view);
            this.dismiss();
        });
    }

    public void setPinCodeListener(OnPinCodeListener pinCodeListener) {
        this.pinCodeListener = pinCodeListener;
        binding.llPairingCode.setOnClickListener(view -> {
            this.pinCodeListener.onPinCode(view);
            this.dismiss();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        Objects.requireNonNull(this.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(binding.getRoot());
        // set on cancel listener
        binding.btnCancel.setOnClickListener(view -> this.dismiss());
    }
}
