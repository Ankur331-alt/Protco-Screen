package com.smart.rinoiot.scene.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceDpJsonBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.BigDecimalUtils;
import com.smart.rinoiot.scene.R;

/**
 * Description: 仿知乎底部评论弹窗
 * Create by dance, at 2018/12/25
 */
public class SeekbarBottomPopView extends CenterPopupView {
    SeekBar seekBar;
    TextView tvSeekValue;
    TextView tvSeekUnit;
    TextView tvSeekMin;
    TextView tvSeekMax;
    TextView tv_title;

    CharSequence title;
    String devId;
    String dpKey;
    String unit = "";
    double value = 0;

    public SeekbarBottomPopView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom_seekbar;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        seekBar = findViewById(R.id.seekBar);
        tvSeekValue = findViewById(R.id.tvSeekValue);
        tvSeekUnit = findViewById(R.id.tvSeekUnit);
        tvSeekMin = findViewById(R.id.tvSeekMin);
        tvSeekMax = findViewById(R.id.tvSeekMax);

        tv_title = findViewById(com.lxj.xpopup.R.id.tv_title);

        if(tv_title != null) {
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

        double select = value;
        double min = 0;
        double max = 0;
        double step = 0;
        int accuracy = 0; // 保留小数点后几位

        DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(devId);
        for (DeviceDpBean deviceDpBean: CacheDataManager.getInstance().getDeviceDpList(deviceInfo.getId())) {
            DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);

            if (deviceDpJson.getIdentifier().equals(dpKey)) {
                unit = deviceDpJson.getDataType().getSpecs().get("unitName").toString();
                min = Double.parseDouble(deviceDpJson.getDataType().getSpecs().get("min").toString());
                max = Double.parseDouble(deviceDpJson.getDataType().getSpecs().get("max").toString());
                step = Double.parseDouble(deviceDpJson.getDataType().getSpecs().get("step").toString());
                try {
                    accuracy = (int) Double.parseDouble(deviceDpJson.getDataType().getSpecs().get("accuracy").toString());
                    if (select < min) select = min;
                    if (select > max) select = max;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        initSeekBar(min, max, step, accuracy, unit, select);

        findViewById(R.id.tvCancel).setOnClickListener(v -> dismiss());

        findViewById(R.id.tvConfirm).setOnClickListener(v -> {
            if (callbackListener != null) callbackListener.onCallback(tvSeekValue.getText().toString() + unit);
            dismiss();
        });
        getPopupImplView().setBackground(XPopupUtils.createDrawable(getResources().getColor(R.color.cen_common_item_bg_color),
                popupInfo.borderRadius, popupInfo.borderRadius, popupInfo.borderRadius,popupInfo.borderRadius));
    }

    public void setData(CharSequence title, String devId, String dpKey, double value) {
        this.title = title;
        this.devId = devId;
        this.dpKey = dpKey;
        this.value = value;
    }

    @SuppressLint("SetTextI18n")
    private void initSeekBar(double seekBarMin, double seekBarMax, double step, int scale, String unit, double select) {
        int max = (int) ((seekBarMax - seekBarMin) / step);
        int value = (int) (select / step);

        tvSeekValue.setText(BigDecimalUtils.round(String.valueOf(select), scale));
        tvSeekUnit.setText(unit);
        tvSeekMin.setText(BigDecimalUtils.round(String.valueOf(seekBarMin), scale) + unit);
        tvSeekMax.setText(BigDecimalUtils.round(String.valueOf(seekBarMax), scale) + unit);
        seekBar.setMax(max);
        seekBar.setProgress(value);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeekValue.setText(BigDecimalUtils.mul(String.valueOf(progress + seekBarMin), String.valueOf(step), scale));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private OnCallbackListener callbackListener;

    public void setOnConfirmListener(OnCallbackListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    public interface OnCallbackListener {
        void onCallback(String value);
    }
}