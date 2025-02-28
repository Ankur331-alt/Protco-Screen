package com.smart.device.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.device.R;
import com.smart.device.bean.StockDpInfoBean;
import com.smart.device.manager.StockDpUtils;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.mqtt2.Manager.MqttConvertManager;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.ImageLoaderUtils;
import com.smart.rinoiot.common.utils.LgUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author author
 */
public class DeviceAdapter extends BaseQuickAdapter<DeviceInfoBean, BaseViewHolder> {
    private static final String TAG = "DeviceAdapter";

    private static final String INT_KEY = "int";
    private static final String BOOL_KEY = "bool";
    private static final String ENUM_KEY = "enum";
    private static final String LONG_KEY = "long";
    private static final String FLOAT_KEY = "float";
    private static final String DOUBLE_KEY = "double";
    private static final String MIN_KEY = "min";
    private static final String MAX_KEY = "max";

    public DeviceAdapter(@Nullable List<DeviceInfoBean> data) {
        super(R.layout.adapter_item_device, data);
        addChildClickViewIds(R.id.switchDp);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, DeviceInfoBean deviceInfo) {
        if (deviceInfo == null) {
            return;
        }
        boolean isCommonFun = StockDpUtils.getInstance().isCommonFunc(deviceInfo);
        RelativeLayout rlItem = helper.getView(R.id.rl_item);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, isCommonFun ? DpUtils.dip2px(154) :
                DpUtils.dip2px(220));
        rlItem.setLayoutParams(layoutParams);
        rlItem.setPadding(DpUtils.dip2px(16), 0, 0, DpUtils.dip2px(16));
        ImageLoaderUtils.getInstance().bindImageUrl(deviceInfo.getImageUrl(), helper.getView(R.id.ivIcon));
        helper.setText(R.id.tvTitle, deviceInfo.getName());
        boolean ioOpen = MqttConvertManager.getInstance().getSwitchDpStatus(deviceInfo);
        //快捷开关
        boolean isShortcutSwitch = MqttConvertManager.getInstance().getSwitchDpExit(deviceInfo);
        helper.setImageResource(R.id.switchDp, ioOpen ? R.drawable.icon_switch_open : R.drawable.icon_switch_close);
        if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
            //switchDpData设备没有开关
            helper.setGone(R.id.switchDp, true);
        }else{
            //switchDpData设备没有开关
            helper.setGone(R.id.switchDp, !isShortcutSwitch);
        }
        if (!isCommonFun && !deviceInfo.isCustomGroup() && !MtrDeviceDataUtils.isMatterDevice(deviceInfo)) {
            //不为空
            commonFunctionDeal(deviceInfo, helper);
        }
    }

    /**
     * 获取常见功能前2条数据
     */
    private List<DeviceDpBean> getCommonFunctionData(List<DeviceDpBean> stockDpInfoVOList) {
        List<DeviceDpBean> dpBeanList = new ArrayList<>();
        if (stockDpInfoVOList.size() > 2) {
            for (int i = 0; i < 2; i++) {
                dpBeanList.add(stockDpInfoVOList.get(i));
            }
        } else {
            dpBeanList.addAll(stockDpInfoVOList);
        }
        return dpBeanList;
    }

    /**
     * 获取第一条常用功能的数据类型
     *
     * @return 1:boolean 2:enum 3:number
     */
    private StockDpInfoBean commonFunctionType(DeviceDpBean deviceDpBean, String devId) {
        StockDpInfoBean stockDpInfoBean = new StockDpInfoBean();
        stockDpInfoBean.setDevId(devId);
        if (deviceDpBean == null) {
            return stockDpInfoBean;
        }
        stockDpInfoBean.setName(deviceDpBean.getName());
        stockDpInfoBean.setKey(deviceDpBean.getKey());
        stockDpInfoBean.setValue(deviceDpBean.getValue());
        try {
            if (TextUtils.isEmpty(deviceDpBean.getDpJson())) {
                return stockDpInfoBean;
            }
            StockDpUtils.getInstance().dealStockDpJsonData(deviceDpBean, stockDpInfoBean);
            StockDpUtils.getInstance().dealStockDpImageData(deviceDpBean, stockDpInfoBean);
        } catch (Exception e) {
            LgUtils.w("常用开关数据解析异常:" + e.getMessage());
        }
        return stockDpInfoBean;
    }

    /**
     * 常用功能操作及显示问题
     */
    private void commonFunctionDeal(DeviceInfoBean deviceInfo, BaseViewHolder helper) {
        List<DeviceDpBean> commonFunctionData = getCommonFunctionData(deviceInfo.getStockDpInfoVOList());
        StockDpInfoBean stockDpInfoBean1 = commonFunctionType(commonFunctionData.get(0), deviceInfo.getId());
        if (TextUtils.equals(stockDpInfoBean1.getType(), BOOL_KEY)) {
            //boolean
            switchDataDeal(stockDpInfoBean1, helper, R.id.includeSwitch1);
        } else if (TextUtils.equals(stockDpInfoBean1.getType(), ENUM_KEY)) {
            //enum
            enumDataDeal(stockDpInfoBean1, helper, R.id.includeEnum1);
        } else if (TextUtils.equals(stockDpInfoBean1.getType(), INT_KEY)
                || TextUtils.equals(stockDpInfoBean1.getType(), LONG_KEY)
                || TextUtils.equals(stockDpInfoBean1.getType(), FLOAT_KEY)
                || TextUtils.equals(stockDpInfoBean1.getType(), DOUBLE_KEY)) {
            //number
            numberDataDeal(stockDpInfoBean1, helper, R.id.includeNumber1);
        }
        if (deviceInfo.getStockDpInfoVOList().size() > 1) {
            StockDpInfoBean stockDpInfoBean2 = commonFunctionType(
                    commonFunctionData.get(1), deviceInfo.getId()
            );
            if (TextUtils.equals(stockDpInfoBean2.getType(), BOOL_KEY)) {
                //boolean
                switchDataDeal(stockDpInfoBean2, helper, R.id.includeSwitch2);
            } else if (TextUtils.equals(stockDpInfoBean2.getType(), ENUM_KEY)) {
                //enum
                enumDataDeal(stockDpInfoBean2, helper, R.id.includeEnum2);
            } else if (TextUtils.equals(stockDpInfoBean2.getType(), INT_KEY)
                    || TextUtils.equals(stockDpInfoBean2.getType(), FLOAT_KEY)) {
                //number
                numberDataDeal(stockDpInfoBean2, helper, R.id.includeNumber2);
            }
        }
    }

    /**
     * boolean类型的开关数据处理及控制
     */
    private void switchDataDeal(StockDpInfoBean stockDpInfoBean, BaseViewHolder helper, int resId) {
        /// helper.setGone(resId, false);
        View view = helper.getView(resId);
        ImageView ivFunctionSwitch = view.findViewById(R.id.ivFunctionSwitch);
        TextView tvSwitchName = view.findViewById(R.id.tvSwitchName);
        tvSwitchName.setText(stockDpInfoBean.getName());
        ivFunctionSwitch.setSelected((Boolean) stockDpInfoBean.getValue());
        ivFunctionSwitch.setOnClickListener(v -> StockDpUtils.getInstance().commonPublishData(
                stockDpInfoBean, !ivFunctionSwitch.isSelected())
        );
    }

    /**
     * enum类型的开关数据处理及控制
     */
    private void enumDataDeal(StockDpInfoBean stockDpInfoBean, BaseViewHolder helper, int resId) {
        helper.setGone(resId, false);
        View view = helper.getView(resId);
        LinearLayout llEnum = view.findViewById(R.id.llEnum);
        LinearLayout llEnum2 = view.findViewById(R.id.llEnum2);
        TextView tvEnumMode = view.findViewById(R.id.tvEnumMode);
        ImageView ivEnumIcon = view.findViewById(R.id.ivEnumIcon);
        TextView tvEnumName = view.findViewById(R.id.tvEnumName);
        ImageView ivEnumIcon2 = view.findViewById(R.id.ivEnumIcon2);
        TextView tvEnumName2 = view.findViewById(R.id.tvEnumName2);
        tvEnumMode.setText(stockDpInfoBean.getName());
        if (stockDpInfoBean.getMapDpJson() != null && !stockDpInfoBean.getMapDpJson().isEmpty()
                && stockDpInfoBean.getMapDpImage() != null && !stockDpInfoBean.getMapDpImage().isEmpty()) {
            int index = 0;
            for (String s : stockDpInfoBean.getMapDpJson().keySet()) {
                if (index > 1) {
                    break;
                }
                String enumName = String.valueOf(stockDpInfoBean.getMapDpJson().get(s));
                String enumUrl = "";
                if (stockDpInfoBean.getMapDpImage().containsKey(s)) {
                    enumUrl = String.valueOf(stockDpInfoBean.getMapDpImage().get(s));
                }
                if (index == 0) {
                    tvEnumName.setText(enumName);
                    ImageLoader.getInstance().bindRoundImageUrl(enumUrl, ivEnumIcon, DpUtils.dip2px(4));
                } else if (index == 1) {
                    tvEnumName2.setText(enumName);
                    ImageLoader.getInstance().bindRoundImageUrl(enumUrl, ivEnumIcon2, DpUtils.dip2px(4));
                }
                index++;

            }
        }
        llEnum.setOnClickListener(v -> StockDpUtils.getInstance().commonPublishData(stockDpInfoBean, tvEnumName.getText()));
        llEnum2.setOnClickListener(v -> StockDpUtils.getInstance().commonPublishData(stockDpInfoBean, tvEnumName2.getText()));
    }

    /**
     * number类型的开关数据处理及控制
     */
    @SuppressLint("SetTextI18n")
    private void numberDataDeal(StockDpInfoBean stockDpInfoBean, BaseViewHolder helper, int resId) {
        try {
            helper.setGone(resId, false);
            View view = helper.getView(resId);
            SeekBar progressBarNumber = view.findViewById(R.id.progressBarNumber);
            TextView tvProgressName = view.findViewById(R.id.tvProgressName);
            TextView tvProgress = view.findViewById(R.id.tvProgress);
            Map<String, Object> mapDpJson = stockDpInfoBean.getMapDpJson();
            int max = 0, min = 0;
            if (mapDpJson != null && !mapDpJson.isEmpty()) {
                if (mapDpJson.containsKey(MAX_KEY) && mapDpJson.get(MAX_KEY) instanceof Number) {
                    //最大值
                    max = (int) Float.parseFloat(String.valueOf(mapDpJson.get(MAX_KEY)));
                    progressBarNumber.setMax(max);
                }
                if (mapDpJson.containsKey(MIN_KEY) && mapDpJson.get(MIN_KEY) instanceof Number) {
                    //最大值
                    min = (int) Float.parseFloat(String.valueOf(mapDpJson.get(MIN_KEY)));
                    /// progressBarNumber.setMin(min);
                }
            }
            int value = (int) Float.parseFloat(String.valueOf(stockDpInfoBean.getValue()));
            if (Math.abs(max - min) < Math.abs(value)) {
                return;
            }
            tvProgressName.setText(stockDpInfoBean.getName());
            setCurrentProgress(progressBarNumber, tvProgress, max - min, value);
            int finalMax = max;
            int finalMin = min;
            progressBarNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        StockDpUtils.getInstance().commonPublishData(
                                stockDpInfoBean, (finalMax - finalMin) * progress / 100
                        );
                        tvProgress.setText(progress + "%");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "numberDataDeal: The deal went sideways. Cause="
                    + e.getLocalizedMessage();
            Log.d(TAG, errorMsg);
        }
    }

    /**
     * 返回当前数值对应的百分比进度
     */
    @SuppressLint("SetTextI18n")
    private void setCurrentProgress(SeekBar progressBarNumber, TextView tvProgress, int rang, int current) {
        int progress = Math.abs(current * 100 / rang);
        progressBarNumber.setProgress(progress);
        tvProgress.setText(progress + "%");
    }
}
