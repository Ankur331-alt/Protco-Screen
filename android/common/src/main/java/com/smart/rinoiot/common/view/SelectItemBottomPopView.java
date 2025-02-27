package com.smart.rinoiot.common.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.adapter.SelectItemAdapter;
import com.smart.rinoiot.common.bean.SelectItemBean;
import com.smart.rinoiot.common.utils.ToastUtil;

import java.util.List;

/**
 * Description: 仿知乎底部评论弹窗
 * Create by dance, at 2018/12/25
 */
public class SelectItemBottomPopView extends CenterPopupView {
    RecyclerView recyclerView;
    TextView tv_title;

    CharSequence title;
    List<SelectItemBean> dataArray;

    public SelectItemBottomPopView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom_list_select_item;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        recyclerView = findViewById(R.id.recyclerView);
        tv_title = findViewById(com.lxj.xpopup.R.id.tv_title);

        if(tv_title != null) {
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
                if (findViewById(com.lxj.xpopup.R.id.xpopup_divider) != null)
                    findViewById(com.lxj.xpopup.R.id.xpopup_divider).setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

        SelectItemAdapter selectItemAdapter = new SelectItemAdapter(dataArray);
        selectItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!selectItemAdapter.getData().get(position).isClickable()) {
                ToastUtil.showMsg(selectItemAdapter.getData().get(position).getTips());
                return;
            }

            if (selectListener != null) {
                selectListener.onSelect(position, selectItemAdapter.getData().get(position));
            }
            if (popupInfo.autoDismiss) dismiss();
        });

        recyclerView.setAdapter(selectItemAdapter);
//        getPopupImplView().setBackground(XPopupUtils.createDrawable(getResources().getColor(com.lxj.xpopup.R.color._xpopup_light_color),
//                popupInfo.borderRadius, popupInfo.borderRadius, 0,0));
    }

    public SelectItemBottomPopView setData(CharSequence title, List<SelectItemBean> dataArray) {
        this.title = title;
        this.dataArray = dataArray;
        return this;
    }

    private OnSelectListener selectListener;

    public SelectItemBottomPopView setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    public interface OnSelectListener {
        void onSelect(int position, SelectItemBean item);
    }
}