package com.smart.rinoiot.family.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.bean.RoomDeviceBean;

import org.jetbrains.annotations.NotNull;

/**
 * 房间设置
 */
public class RoomDeviceAdapter extends BaseQuickAdapter<RoomDeviceBean, BaseViewHolder> {

    public RoomDeviceAdapter() {
        super(R.layout.item_room_device);
        addChildClickViewIds(R.id.iv_type);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, RoomDeviceBean roomDeviceBean) {
        if (roomDeviceBean == null || roomDeviceBean.getDeviceInfoBean() == null) return;
        DeviceInfoBean deviceInfoBean = roomDeviceBean.getDeviceInfoBean();
        baseViewHolder.setImageResource(R.id.iv_type, roomDeviceBean.isCurrentRoomFlag() ? R.drawable.icon_room_delete : R.drawable.icon_add_device);
        baseViewHolder.setGone(R.id.tvDeviceRoom, roomDeviceBean.isCurrentRoomFlag());
        baseViewHolder.setText(R.id.tvDeviceName, deviceInfoBean.getName());
        baseViewHolder.setText(R.id.tvDeviceRoom, TextUtils.isEmpty(roomDeviceBean.getRoomName())?getContext().getString(R.string.rino_device_all):roomDeviceBean.getRoomName());
        ImageLoader.getInstance().bindRoundImageUrl(deviceInfoBean.getImageUrl(), baseViewHolder.getView(R.id.iv_device_icon), DpUtils.dip2px(4));
    }
}
