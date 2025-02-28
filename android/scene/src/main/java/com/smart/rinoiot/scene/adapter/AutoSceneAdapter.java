package com.smart.rinoiot.scene.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.scene.R;

import java.util.List;

/**
 * @author author
 */
public class AutoSceneAdapter extends BaseQuickAdapter<SceneBean, BaseViewHolder> {
    public AutoSceneAdapter(@Nullable List<SceneBean> data) {
        super(R.layout.adapter_auto_scene, data);
        addChildClickViewIds(R.id.iv_status_toggle);
        addChildLongClickViewIds(R.id.ll_auto_scene);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SceneBean item) {
        helper.setText(R.id.tv_scene_name, item.getName());
        ImageView toggleSwitch = helper.getView(R.id.iv_status_toggle);
        int toggleSwitchResId = item.getEnabled() == 1 ?
                R.drawable.icon_scene_auto_open : R.drawable.icon_scene_auto_close;
        toggleSwitch.setImageResource(toggleSwitchResId);

        int backgroundResId = item.getEnabled() == 1 ?
                R.drawable.shape_auto_scene_enabled_bg : R.drawable.shape_auto_scene_disabled_bg;
        Drawable background =  AppCompatResources.getDrawable(getContext(), backgroundResId);
        LinearLayout view = helper.getView(R.id.root);
        view.setBackground(background);

        if (item.getRuleMetaData() == null || item.getRuleMetaData().getActions() == null) {
            return;
        }

        int tasks = item.getRuleMetaData().getActions().size();
        helper.setText(
                R.id.tv_task_count,
                String.format(getContext().getString(R.string.rino_scene_auto_action_num), tasks)
        );
    }
}
