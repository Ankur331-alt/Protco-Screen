package com.smart.rinoiot.scene.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.utils.ImageLoaderUtils;
import com.smart.rinoiot.scene.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SceneDeviceAdapter extends BaseQuickAdapter<DeviceInfoBean, BaseViewHolder> {

    public SceneDeviceAdapter(@Nullable List<DeviceInfoBean> data) {
        super(R.layout.adapter_scene_device_item, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, DeviceInfoBean deviceInfo) {
        ImageLoaderUtils.getInstance().bindImageUrl(deviceInfo.getImageUrl(), helper.getView(R.id.ivIcon), R.drawable.ic_default_circle_image, R.drawable.ic_default_circle_image);

        helper.setText(R.id.tvName, deviceInfo.getName());
        helper.setText(R.id.tvContent, getContext().getString(deviceInfo.getOnlineStatus() == 1 ? R.string.rino_scene_device_online : R.string.rino_scene_device_offline));

//        helper.setGone(com.smart.rinoiot.common.R.id.viewLine, getItemPosition(deviceInfo) == (getItemCount() - 1));
    }
}
