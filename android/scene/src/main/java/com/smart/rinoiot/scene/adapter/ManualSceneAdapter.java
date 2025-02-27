package com.smart.rinoiot.scene.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.scene.R;

import java.util.List;

/**
 * @author author
 */
public class ManualSceneAdapter extends BaseQuickAdapter<SceneBean, BaseViewHolder> {
    public ManualSceneAdapter(@Nullable List<SceneBean> data) {
        super(R.layout.adapter_manual_scene, data);
        addChildClickViewIds(R.id.ll_manual_scene);
        addChildLongClickViewIds(R.id.ll_manual_scene);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, SceneBean item) {
        helper.setText(R.id.tv_scene_name, item.getName());
    }
}
