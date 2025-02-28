package com.smart.rinoiot.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.utils.WifiUtil;

/**
 * Description: 录入wifi信息的底部提示弹窗
 * Create by dance, at 2018/12/25
 */
@SuppressLint("SetTextI18n")
public class InputWIFIBottomPopView extends BottomPopupView implements View.OnClickListener {

    private AppCompatActivity activity;

    private String wifiName = "";

    private EditText etWiFiName;
    private CustomEditText etPwd;
    private TextView tvErrorTip;

    private OnConfirmListener confirmListener;

    private WifiUtil wifiUtil;

    private int requestCode;

    public InputWIFIBottomPopView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom_input_wifi;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        getPopupImplView().setBackground(XPopupUtils.createDrawable(getResources().getColor(com.lxj.xpopup.R.color._xpopup_light_color),
                popupInfo.borderRadius, popupInfo.borderRadius, 0,0));

        wifiUtil = new WifiUtil();

        etWiFiName = findViewById(R.id.etWiFiName);
        etPwd = findViewById(R.id.etPwd);
        tvErrorTip = findViewById(R.id.tvErrorTip);

        findViewById(R.id.tvSkip).setOnClickListener(this);
        findViewById(R.id.ivChangeWifi).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);

        if (!TextUtils.isEmpty(wifiName)) {
            setTextForWifiName(wifiName);
        }

        checkWifiStatus();
    }

    public InputWIFIBottomPopView setOnConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public InputWIFIBottomPopView setWifiName(String wifiName) {
        this.wifiName = wifiName;
        return this;
    }

    public InputWIFIBottomPopView setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public InputWIFIBottomPopView setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public void setTextForWifiName(String wifiName) {
        if (!TextUtils.isEmpty(wifiName)) {
            tvErrorTip.setVisibility(View.GONE);
            etWiFiName.setText(wifiName);
            String password = SharedPreferenceUtil.getInstance().get(wifiName, "");
            if (!TextUtils.isEmpty(password)) {
                etPwd.setText(password);
            }
            checkWifiStatus();
        } else {
            etWiFiName.setText("");
        }
    }

    private void checkWifiStatus() {
        if (!wifiUtil.isWifiEnabled() || !WifiUtil.isWifiConnect(getContext())) {
            tvErrorTip.setVisibility(View.VISIBLE);
            tvErrorTip.setText(R.string.rino_device_current_no_wifi);
            DialogUtil.showNormalMsg(activity, "", getContext().getString(R.string.rino_device_current_no_wifi), getContext().getString(R.string.rino_common_cancel), getContext().getString(R.string.rino_device_to_connect), new DialogOnListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onConfirm() {
                    findViewById(R.id.ivChangeWifi).performClick();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvConfirm) {
            if (TextUtils.isEmpty(etWiFiName.getText().toString())) {
                ToastUtil.showMsg(R.string.rino_device_current_no_wifi);
                checkWifiStatus();
                return;
            } else if (wifiUtil.isWifi5G()) {
                DialogUtil.showNormalMsg(activity, "", getContext().getString(R.string.rino_device_5g_wifi_tips_content), getContext().getString(R.string.rino_common_continue), getContext().getString(R.string.rino_device_change_wifi), new DialogOnListener() {
                    @Override
                    public void onCancel() {
                        SharedPreferenceUtil.getInstance().put(wifiName, etPwd.getText());
                        if (confirmListener != null) confirmListener.onConfirm(Constant.PROTOCOL_TYPE_FOR_WIFI_BLE);
                        dismiss();
                    }

                    @Override
                    public void onConfirm() {
                        findViewById(R.id.ivChangeWifi).performClick();
                    }
                });
                return;
            }

            SharedPreferenceUtil.getInstance().put(wifiName, etPwd.getText());
            if (confirmListener != null) confirmListener.onConfirm(Constant.PROTOCOL_TYPE_FOR_WIFI_BLE);
            dismiss();
        } else if (v.getId() == R.id.tvSkip) {
            if (confirmListener != null) confirmListener.onConfirm(Constant.PROTOCOL_TYPE_FOR_SINGLE_BLUETOOTH);
            dismiss();
        } else if (v.getId() == R.id.ivChangeWifi) {
            Intent intent = new Intent();
            intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public interface OnConfirmListener {
        void onConfirm(String protocol);
    }
}