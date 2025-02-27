package com.smart.device.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.device.R;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.utils.ImageLoaderUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 设备管理
 */
public class DeviceManagerAdapter extends BaseQuickAdapter<DeviceInfoBean, BaseViewHolder> {


    public DeviceManagerAdapter(@Nullable List<DeviceInfoBean> data) {
        super(R.layout.adapter_item_device_manager, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, DeviceInfoBean deviceInfo) {
        if (deviceInfo==null) return;
        helper.setText(R.id.tvTitle, deviceInfo.getName());
        helper.getView(R.id.ivSelect).setSelected(deviceInfo.isSelect());
        ImageLoaderUtils.getInstance().bindImageUrl(deviceInfo.getImageUrl(), helper.getView(R.id.ivIcon));
    }
}
