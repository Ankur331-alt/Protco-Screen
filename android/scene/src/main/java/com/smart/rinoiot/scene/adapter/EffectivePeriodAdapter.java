package com.smart.rinoiot.scene.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.bean.SingleSelectBean;

import java.util.List;

public class EffectivePeriodAdapter extends BaseQuickAdapter<SingleSelectBean, BaseViewHolder> {
    public EffectivePeriodAdapter(@Nullable List<SingleSelectBean> data) {
        super(R.layout.adapter_effective_period, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SingleSelectBean item) {
        helper.setText(R.id.tvName, item.getName());
        helper.setText(R.id.tvValue, item.getValue());
        ImageView ivCheck = helper.getView(R.id.ivCheck);
        ivCheck.setSelected(item.isSelect());
//        helper.setGone(R.id.viewLine, getItemPosition(item) == (getItemCount() - 1));
    }
}
