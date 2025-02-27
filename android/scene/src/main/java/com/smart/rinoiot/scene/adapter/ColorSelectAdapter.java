package com.smart.rinoiot.scene.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.bean.ColorBean;

import java.util.List;

public class ColorSelectAdapter extends BaseQuickAdapter<ColorBean, BaseViewHolder> {
    public ColorSelectAdapter(@Nullable List<ColorBean> data) {
        super(R.layout.adapter_style_color, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ColorBean item) {
        if (!item.getColorRes().contains("#")) {
            ((ImageView) helper.getView(R.id.iv_fill)).setColorFilter(Color.parseColor("#" + item.getColorRes().trim()));
        } else {
            ((ImageView) helper.getView(R.id.iv_fill)).setColorFilter(Color.parseColor(item.getColorRes().trim()));
        }

        if (item.isSelect()) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setStroke(DpUtils.dip2px(2.0F), Color.parseColor(item.getColorRes()));
            helper.setImageDrawable(R.id.iv_stroke, drawable);
            helper.setVisible(R.id.iv_stroke, true);
        } else {
            helper.setVisible(R.id.iv_stroke, false);
        }
    }
}
