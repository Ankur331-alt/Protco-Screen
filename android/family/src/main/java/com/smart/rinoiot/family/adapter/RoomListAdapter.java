package com.smart.rinoiot.family.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.family.R;

import org.jetbrains.annotations.NotNull;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/8
 */
@SuppressLint("StringFormatMatches")
public class RoomListAdapter extends BaseQuickAdapter<AssetBean, BaseViewHolder> {
    public Context mContext;
    private boolean isEdit;

    public RoomListAdapter(Context mContext) {
        super(R.layout.item_room_list);
        this.mContext = mContext;
        addChildClickViewIds(R.id.ivDelete);
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AssetBean assetBean) {

        TextView tvTitle = baseViewHolder.findView(R.id.tvTitle);
        TextView tvSubtitle = baseViewHolder.findView(R.id.tvSubTitle);

        tvTitle.setText(assetBean.getName());
        tvSubtitle.setText(String.format(mContext.getString(R.string.rino_family_device_count), assetBean.getDeviceInfoBeans().size()));

        ImageView ivDelete = baseViewHolder.findView(R.id.ivDelete);
        ivDelete.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        baseViewHolder.setGone(R.id.ivArrow,isEdit);
    }
}
