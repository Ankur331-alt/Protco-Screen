package com.smart.rinoiot.common.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.services.help.Tip;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.R;

import java.util.List;

public class AddressSearchAdapter extends BaseQuickAdapter<Tip, BaseViewHolder> {
    public AddressSearchAdapter(@Nullable List<Tip> data) {
        super(R.layout.support_simple_spinner_dropdown_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Tip item) {
        helper.setText(android.R.id.text1, item.getName());
    }
}
