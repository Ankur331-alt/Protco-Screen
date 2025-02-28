package com.smart.rinoiot.common.page;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

public class BaseMultiAdapter<V extends MultiItemEntity> extends BaseMultiItemQuickAdapter<V, BaseViewHolder> implements LoadMoreModule {

    public BaseMultiAdapter(@Nullable List<V> data) {
        super(data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, V item) {

    }
}
