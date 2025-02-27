package com.smart.rinoiot.common.permission;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.permission.bean.PermissionListBean;
import com.smart.rinoiot.common.utils.TextUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 权限列表
 */
public class PermissionAdapter extends BaseQuickAdapter<PermissionListBean, BaseViewHolder> {
    private int type;//0:蓝牙权限；1、定位、媒体权限

    public PermissionAdapter(@Nullable List<PermissionListBean> data) {
        super(R.layout.adapter_popup_bottom_permission, data);
        addChildClickViewIds(R.id.tvOpenPermissionStatus);
    }

    public void setPermissionType(int type) {
        this.type = type;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, PermissionListBean item) {
        helper.setGone(R.id.llBlePermission, type != 0);
        helper.setGone(R.id.llLocationPermission, type == 0);
        //蓝牙系统权限
        TextView tvOpenPermissionStatus = helper.getView(R.id.tvOpenPermissionStatus);
        tvOpenPermissionStatus.setVisibility(item.isBleSystemStatus()? View.GONE:View.VISIBLE);
        TextUtil.setTvDrawableRight(getContext(),
                item.isPermissionOpenStatus() ? R.drawable.icon_permission_opened
                        : R.drawable.icon_permission_go_to_open, tvOpenPermissionStatus);
        tvOpenPermissionStatus.setText(getContext().getString(item.isPermissionOpenStatus() ?
                R.string.rino_common_permission_opened : R.string.rino_common_permission_go_to_open));
        tvOpenPermissionStatus.setTextColor(getContext().getResources().getColor(item.isPermissionOpenStatus() ?
                R.color.c_888888 : R.color.main_theme_color));
        helper.setImageResource(R.id.ivPermission, item.getPermissionResId());
        helper.setText(R.id.tvPermissionDesc, item.getPermissionTitle());
        //定位、媒体文件等权限
        helper.setText(R.id.tvPermissionName, item.getPermissionTitle());
        helper.setText(R.id.tvLocationPermissionDesc, item.getPermissionDesc());
        helper.setImageResource(R.id.ivLocationPermission, item.getPermissionResId());

        helper.setGone(R.id.viewLine, getItemPosition(item) == (getItemCount() - 1));
    }
}
