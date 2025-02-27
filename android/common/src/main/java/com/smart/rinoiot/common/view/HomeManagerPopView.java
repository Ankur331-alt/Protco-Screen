package com.smart.rinoiot.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.core.AttachPopupView;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.adapter.HomeManagerAdapter;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.listener.OnHomeManagerClickLister;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.ScreenUtils;

import java.util.List;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/6
 */
@SuppressLint("ViewConstructor")
public class HomeManagerPopView extends AttachPopupView {
    private List<AssetBean> assetBeans;
    private final Context mContext;
    private final OnHomeManagerClickLister onHomeManagerClickLister;

    public HomeManagerPopView(@NonNull Context context, List<AssetBean> assetBeans, OnHomeManagerClickLister onHomeManagerClickLister) {
        super(context);
        this.mContext = context;
        this.assetBeans = assetBeans;
        this.onHomeManagerClickLister = onHomeManagerClickLister;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout._xpopup_home_manager;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        RecyclerView rv = findViewById(R.id.rv);
        LinearLayout rlContainer = findViewById(R.id.rlContainer);
        HomeManagerAdapter adapter = new HomeManagerAdapter();
        adapter.setNewInstance(assetBeans);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ScreenUtils.getScreenWidth(mContext) / 2, LayoutParams.WRAP_CONTENT);
        rlContainer.setLayoutParams(layoutParams);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            dismiss();
            if (onHomeManagerClickLister != null) {
                onHomeManagerClickLister.onClick(false, assetBeans.get(position));
            }
        });

        rlContainer.setOnClickListener(view -> {
            rv.setVisibility(GONE);
            dismiss();
        });

        rv.setAdapter(adapter);
        findViewById(R.id.tvFamilyName).setOnClickListener(v -> {
            if (onHomeManagerClickLister != null) {
                onHomeManagerClickLister.onClick(true, new AssetBean());
            }
            dismiss();
        });
    }

    @Override
    protected int getMaxHeight() {
        return DpUtils.dip2px(240);
    }
}
