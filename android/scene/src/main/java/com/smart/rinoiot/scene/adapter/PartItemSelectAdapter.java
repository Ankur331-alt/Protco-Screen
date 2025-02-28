package com.smart.rinoiot.scene.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.bean.ItemSelectBean;

import java.util.List;

public class PartItemSelectAdapter extends BaseQuickAdapter<ItemSelectBean, BaseViewHolder> {
    public PartItemSelectAdapter(@Nullable List<ItemSelectBean> data) {
        super(R.layout.adapter_part_item_select_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ItemSelectBean item) {
        helper.setText(R.id.tvName, item.getName());
        helper.setText(R.id.tvValue, item.getValue());
        helper.setGone(R.id.tvValue, TextUtils.isEmpty(item.getValue()));
        if (item.getIconRes() == -1) {
            helper.setGone(R.id.ivIcon, true);
        } else {
            helper.setGone(R.id.ivIcon, false);
            helper.setImageResource(R.id.ivIcon, item.getIconRes());
        }

        if (item.isClick()) {
            helper.setGone(R.id.ivCheck, false);
            helper.setGone(R.id.tvTip, true);
            ImageView ivCheck = helper.getView(R.id.ivCheck);
            ivCheck.setSelected(item.isSelect());
        } else {
            helper.setText(R.id.tvTip, item.getTip());
            helper.setGone(R.id.ivCheck, true);
            helper.setGone(R.id.tvTip, false);
        }
    }
}
