package com.smart.rinoiot.common.page;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.entity.SectionEntity;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class BaseSectionAdapter<V extends SectionEntity> extends BaseSectionQuickAdapter<V, BaseViewHolder>implements LoadMoreModule {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public BaseSectionAdapter(int header, int convert, List<V> data) {
        super(header, convert, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, V item) {
    }

    @Override
    protected void convertHeader(@NotNull BaseViewHolder baseViewHolder, @NotNull V v) {

    }
}
