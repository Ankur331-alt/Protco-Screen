package com.smart.rinoiot.common.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.bean.PopAttachListBean;
import com.smart.rinoiot.common.impl.ImageLoader;

import org.jetbrains.annotations.NotNull;

/**
 * @author tw
 * @time 2022/10/21 11:23
 * @description
 */
public class PopAttachListAdapter extends BaseQuickAdapter<PopAttachListBean, BaseViewHolder> {
    public PopAttachListAdapter() {
        super(R.layout._xpopup_adapter_text_rino);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PopAttachListBean item) {
        baseViewHolder.setText(R.id.tv_text, item.getName());
        ((ImageView)baseViewHolder.getView(R.id.iv_image)).setImageResource(item.getResId());
//        ImageLoader.getInstance().bindImageResource(item.getResId(), baseViewHolder.getView(R.id.iv_image));
    }
}
