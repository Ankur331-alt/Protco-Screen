package com.smart.rinoiot.common.page;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

public class BaseAdapter<V> extends BaseQuickAdapter<V, BaseViewHolder> implements LoadMoreModule {

    public BaseAdapter(int layoutResId, @Nullable List<V> data) {
        super(layoutResId, data);
    }

    public BaseAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, V item) {

    }
}
