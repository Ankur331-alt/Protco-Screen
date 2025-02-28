package com.smart.rinoiot.scene.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.utils.ImageLoaderUtils;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.bean.IconItemBean;

import java.util.List;

public class IconSelectAdapter extends BaseQuickAdapter<IconItemBean, BaseViewHolder> {
    public IconSelectAdapter(@Nullable List<IconItemBean> data) {
        super(R.layout.adapter_style_icon, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, IconItemBean item) {
        ImageLoaderUtils.getInstance().bindImageUrl(item.getIconUrl(), helper.getView(R.id.sdv_device));
        helper.setVisible(R.id.view_cover, item.isSelect());
    }
}
