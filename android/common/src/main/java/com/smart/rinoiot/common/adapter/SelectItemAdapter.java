package com.smart.rinoiot.common.adapter;

import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.bean.SelectItemBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SelectItemAdapter extends BaseQuickAdapter<SelectItemBean, BaseViewHolder> {
    public SelectItemAdapter(@Nullable List<SelectItemBean> data) {
        super(R.layout.adapter_popup_bottom_list_select_item, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, SelectItemBean item) {
        if (item.getIconRes() != -1) {
            helper.setImageResource(R.id.ivIcon, item.getIconRes());
            helper.setGone(R.id.ivIcon, false);
        } else {
            helper.setGone(R.id.ivIcon, true);
        }
        helper.setText(R.id.tvTitle, item.getTitle());

        ((TextView) helper.getView(R.id.tvTitle)).setGravity(item.getTextGravity());

        if (item.isClickable()) {
            helper.setTextColor(R.id.tvTitle, getContext().getResources().getColor(R.color.white));
            helper.setGone(R.id.ivArrow, item.isHideArrow());
            helper.setGone(R.id.ivTip, true);
        } else {
            helper.setTextColor(R.id.tvTitle, getContext().getResources().getColor(R.color.f_c2c2c2));
            helper.setGone(R.id.ivArrow, true);
            helper.setGone(R.id.ivTip, false);
        }

//        helper.setGone(R.id.viewLine, getItemPosition(item) == (getItemCount() - 1) || !item.isShowLine());
    }
}
