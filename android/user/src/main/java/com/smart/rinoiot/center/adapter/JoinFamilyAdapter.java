package com.smart.rinoiot.center.adapter;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.InviteMemberBean;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.user.R;

import org.jetbrains.annotations.NotNull;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/9
 */
public class JoinFamilyAdapter extends BaseQuickAdapter<InviteMemberBean, BaseViewHolder> {
    private Context mContext;
    private int width = (int) ((DpUtils.getScreenHeight() - DpUtils.dip2px(340)) / 2);

    public JoinFamilyAdapter(Context context) {
        super(R.layout.item_family_list);
        this.mContext = context;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, InviteMemberBean inviteMemberBean) {
        baseViewHolder.setText(R.id.tvTitle, inviteMemberBean.getAssetName());
        RelativeLayout rlItem = baseViewHolder.getView(R.id.rlItem);
        rlItem.setLayoutParams(getItemSize(baseViewHolder.getLayoutPosition() == getData().size() - 1));
    }

    private LinearLayout.LayoutParams getItemSize(boolean isLast) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        if (!isLast) {
            layoutParams.rightMargin = DpUtils.dip2px(32);
        }
        return layoutParams;
    }
}
