package com.smart.rinoiot.center.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;

/**
 * @author edwin
 */
public class IpCameraFlexLayoutManager extends FlexboxLayoutManager {

    public IpCameraFlexLayoutManager(Context context) {
        super(context);
    }

    public IpCameraFlexLayoutManager(Context context, int flexDirection) {
        super(context, flexDirection);
    }

    public IpCameraFlexLayoutManager(Context context, int flexDirection, int flexWrap) {
        super(context, flexDirection, flexWrap);
    }

    public IpCameraFlexLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new FlexboxLayoutManager.LayoutParams(lp);
    }
}