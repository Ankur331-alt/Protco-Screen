package com.smart.rinoiot.family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.MemberBean;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.ImageLoaderUtils;
import com.smart.rinoiot.family.R;

import org.jetbrains.annotations.NotNull;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/6
 */
public class MemberAdapter extends BaseQuickAdapter<MemberBean, BaseViewHolder> {
    private int size = DpUtils.dip2px(150);
    public Context mContext;

    public MemberAdapter(Context mContext) {
        super(R.layout.item_member_info);
        this.mContext = mContext;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MemberBean memberBean) {
        RelativeLayout rl_item = baseViewHolder.getView(R.id.rl_item);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        layoutParams.rightMargin  = DpUtils.dip2px(15);
        rl_item.setLayoutParams(layoutParams);
        ImageLoaderUtils.getInstance().bindCircleImageUrl(memberBean.getAvatarUrl(), baseViewHolder.findView(R.id.ivUser), R.drawable.icon_default_avatar);

        baseViewHolder.setText(R.id.tvTitle, TextUtils.isEmpty(memberBean.getName())?memberBean.getUserName():memberBean.getName());
        baseViewHolder.setText(R.id.tvSubTitle, memberBean.getUserName());
        String roleStr;
        if (memberBean.getMemberRole() == 1) {
            roleStr = mContext.getString(R.string.rino_family_role_owner);
        } else if (memberBean.getMemberRole() == 2) {
            roleStr = mContext.getString(R.string.rino_family_role_admin);
        } else {
            roleStr = mContext.getString(R.string.rino_family_role_member);
        }
        baseViewHolder.setText(R.id.tvUserPermission, roleStr);
    }
}
