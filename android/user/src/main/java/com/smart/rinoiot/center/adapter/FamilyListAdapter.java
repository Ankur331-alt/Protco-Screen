package com.smart.rinoiot.center.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.user.R;

import org.jetbrains.annotations.NotNull;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/8
 */
public class FamilyListAdapter extends BaseQuickAdapter<AssetBean, BaseViewHolder> {
    public Context mContext;
    private int width = (int) ((DpUtils.getScreenHeight() - DpUtils.dip2px(340)) / 2);

    public FamilyListAdapter(Context mContext) {
        super(R.layout.item_family_list);
        this.mContext = mContext;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AssetBean assetBean) {
        RelativeLayout rlItem = baseViewHolder.getView(R.id.rlItem);
        rlItem.setLayoutParams(getItemSize(baseViewHolder.getLayoutPosition() == getData().size() - 1));
        baseViewHolder.setGone(R.id.tvTitle, TextUtils.isEmpty(assetBean.getId()));
        baseViewHolder.setGone(R.id.ivAdd, !TextUtils.isEmpty(assetBean.getId()));
        baseViewHolder.setText(R.id.tvTitle, assetBean.getName());
        rlItem.setBackgroundResource(TextUtils.isEmpty(assetBean.getId()) ? R.drawable.icon_add_family_bg : R.drawable.icon_edit_family);
        if (TextUtils.isEmpty(assetBean.getId())) {
            ImageView ivAdd = baseViewHolder.getView(R.id.ivAdd);
            int size=DpUtils.dip2px(40);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            ivAdd.setLayoutParams(layoutParams);
        }
    }

    private LinearLayout.LayoutParams getItemSize(boolean isLast) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        if (!isLast) {
            layoutParams.rightMargin = DpUtils.dip2px(32);
        }
        return layoutParams;
    }
}
