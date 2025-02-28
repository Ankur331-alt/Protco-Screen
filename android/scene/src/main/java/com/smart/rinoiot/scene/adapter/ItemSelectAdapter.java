package com.smart.rinoiot.scene.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.bean.ItemSelectBean;

import java.util.List;

public class ItemSelectAdapter extends BaseQuickAdapter<ItemSelectBean, BaseViewHolder> {
    public ItemSelectAdapter(@Nullable List<ItemSelectBean> data) {
        super(R.layout.adapter_item_select_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ItemSelectBean item) {
        helper.setText(R.id.tvName, item.getName());
        helper.setText(R.id.tvValue, item.getValue());
//        helper.setGone(R.id.viewLine, getItemPosition(item) == (getItemCount() - 1));

        if (item.isClick()) {
            helper.setTextColor(R.id.tvName, getContext().getResources().getColor(R.color.white));
            helper.setGone(R.id.ivArrow, false);
            helper.setGone(R.id.ivTip, true);
        } else {
            helper.setTextColor(R.id.tvName, getContext().getResources().getColor(R.color.f_c2c2c2));
            helper.setGone(R.id.ivArrow, true);
            helper.setGone(R.id.ivTip, false);
        }
    }
}
