package com.smart.group.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.device.R;
import com.smart.rinoiot.common.bean.CreateGroupBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.utils.DpUtils;

import org.jetbrains.annotations.NotNull;

/**
 * @author tw
 * @time 2022/11/11 20:35
 * @description 创建群组
 */
public class AddGroupDeviceAdapter extends BaseQuickAdapter<CreateGroupBean, BaseViewHolder> {

    public AddGroupDeviceAdapter() {
        super(R.layout.item_group_device);
        addChildClickViewIds(R.id.iv_type);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CreateGroupBean createGroupBean) {
        ImageLoader.getInstance().bindImageResource(createGroupBean.isCurrentRoomFlag() ? R.drawable.icon_group_remove : R.drawable.icon_group_add, baseViewHolder.getView(R.id.iv_type));
        baseViewHolder.setGone(R.id.tvDeviceRoom, createGroupBean.isCurrentRoomFlag() || TextUtils.isEmpty(createGroupBean.getRoomName()));
        baseViewHolder.setText(R.id.tvDeviceName, createGroupBean.getDeviceName());
        baseViewHolder.setText(R.id.tvDeviceRoom, createGroupBean.getRoomName());
        ImageLoader.getInstance().bindRoundImageUrl(createGroupBean.getImageUrl(), baseViewHolder.getView(R.id.iv_device_icon), DpUtils.dip2px(4));
//        baseViewHolder.setGone(R.id.view_line, baseViewHolder.getLayoutPosition() == getData().size() - 1);
        baseViewHolder.setGone(R.id.view_line, true);
    }
}
