package com.smart.rinoiot.common.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.bean.AssetBean;

import org.jetbrains.annotations.NotNull;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/6
 */
public class HomeManagerAdapter extends BaseQuickAdapter<AssetBean, BaseViewHolder> {

    public HomeManagerAdapter() {
        super(R.layout.adapter_home_manager);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AssetBean assetBean) {
        baseViewHolder.getView(R.id.ll_item).setBackgroundResource(assetBean.isCurrentSelected() ? R.drawable.shape_change_selected_family_bg : R.drawable.shape_change_family_defualt);
        baseViewHolder.setText(R.id.tvTitle, assetBean.getName());
        ImageView imageView = baseViewHolder.findView(R.id.ivImage);
        imageView.setVisibility(assetBean.isCurrentSelected() ? View.VISIBLE : View.INVISIBLE);
    }
}
