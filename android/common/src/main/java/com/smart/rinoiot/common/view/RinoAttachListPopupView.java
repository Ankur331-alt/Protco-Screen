package com.smart.rinoiot.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.adapter.PopAttachListAdapter;
import com.smart.rinoiot.common.bean.PopAttachListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: Attach类型的列表弹窗
 * Create by dance, at 2018/12/12
 */
@SuppressLint("ViewConstructor")
public class RinoAttachListPopupView extends AttachPopupView {
    RecyclerView recyclerView;
    protected int bindLayoutId;
    protected int bindItemLayoutId;
    protected int contentGravity = Gravity.CENTER;

    /**
     * @param context
     * @param bindLayoutId     layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView
     * @param bindItemLayoutId itemLayoutId 条目的布局id，要求布局中有id为iv_image的ImageView（非必须），和id为tv_text的TextView
     */
    public RinoAttachListPopupView(@NonNull Context context, int bindLayoutId, int bindItemLayoutId) {
        super(context);
        this.bindLayoutId = bindLayoutId;
        this.bindItemLayoutId = bindItemLayoutId;
        addInnerContent();
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_attach_impl_list_rino : bindLayoutId;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        recyclerView = findViewById(R.id.recyclerView);
        if (bindLayoutId != 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        PopAttachListAdapter popAttachListAdapter = new PopAttachListAdapter();
        popAttachListAdapter.setList(getFormat());
        recyclerView.setAdapter(popAttachListAdapter);
        popAttachListAdapter.setOnItemClickListener((adapter, view, position) -> {
            PopAttachListBean popAttachListBean = (PopAttachListBean) adapter.getData().get(position);
            if (selectListener != null) {
                selectListener.onSelect(position, popAttachListBean.getName());
            }
            if (popupInfo.autoDismiss) dismiss();
        });
        applyTheme();
    }

    protected void applyTheme() {
        if (bindLayoutId == 0) {
            if (popupInfo.isDarkTheme) {
                applyDarkTheme();
            } else {
                applyLightTheme();
            }
            attachPopupContainer.setBackground(XPopupUtils.createDrawable(getResources().getColor(popupInfo.isDarkTheme ? R.color._xpopup_dark_color
                    : R.color._xpopup_title_color), popupInfo.borderRadius));
        }
    }

    @Override
    protected void applyDarkTheme() {
        super.applyDarkTheme();
//        ((VerticalRecyclerView) recyclerView).setupDivider(true);
    }

    @Override
    protected void applyLightTheme() {
        super.applyLightTheme();
//        ((VerticalRecyclerView) recyclerView).setupDivider(false);
    }

    String[] data;
    int[] iconIds;

    public RinoAttachListPopupView setStringData(String[] data, int[] iconIds) {
        this.data = data;
        this.iconIds = iconIds;
        return this;
    }

    public RinoAttachListPopupView setContentGravity(int gravity) {
        this.contentGravity = gravity;
        return this;
    }

    private OnSelectListener selectListener;

    public RinoAttachListPopupView setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    private List<PopAttachListBean> getFormat() {
        List<PopAttachListBean> listBeans = new ArrayList<>();
        if (data.length == iconIds.length) {
            for (int i = 0; i < data.length; i++) {
                PopAttachListBean popAttachListBean = new PopAttachListBean();
                popAttachListBean.setName(data[i]);
                popAttachListBean.setResId(iconIds[i]);
                listBeans.add(popAttachListBean);
            }
        }
        return listBeans;
    }
}
