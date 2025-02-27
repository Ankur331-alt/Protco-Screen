package com.smart.rinoiot.scene.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.adapter.SingleSelectAdapter;
import com.smart.rinoiot.scene.bean.SingleSelectBean;

import java.util.List;

/**
 * Description: 仿知乎底部评论弹窗
 * Create by dance, at 2018/12/25
 */
public class SingleSelectBottomPopView extends CenterPopupView {
    RecyclerView recyclerView;
    TextView tv_title;

    CharSequence title;
    List<SingleSelectBean> dataArray;

    public SingleSelectBottomPopView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom_single_select;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        recyclerView = findViewById(R.id.recyclerView);
        tv_title = findViewById(com.lxj.xpopup.R.id.tv_title);

        if(tv_title != null) {
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

        SingleSelectAdapter singleSelectAdapter = new SingleSelectAdapter(dataArray);
        recyclerView.setAdapter(singleSelectAdapter);

        singleSelectAdapter.setOnItemClickListener((adapter, view, position) -> {

            for (int i = 0; i < singleSelectAdapter.getData().size(); i++) {
                SingleSelectBean item = singleSelectAdapter.getData().get(i);
                item.setSelect(i == position);
            }
            singleSelectAdapter.notifyDataSetChanged();
        });

        findViewById(R.id.tvCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.tvConfirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callbackListener != null) {
                    int select = -1;
                    for (int i = 0; i < singleSelectAdapter.getData().size(); i++) {
                        SingleSelectBean item = singleSelectAdapter.getData().get(i);
                        if (item.isSelect()) {
                            select = i;
                            break;
                        }
                    }

                    if (select != -1) callbackListener.onCallback(select, singleSelectAdapter.getData().get(select));
                }
                dismiss();
            }
        });
        getPopupImplView().setBackground(XPopupUtils.createDrawable(getResources().getColor(R.color.cen_common_item_bg_color),
                popupInfo.borderRadius, popupInfo.borderRadius, popupInfo.borderRadius,popupInfo.borderRadius));
    }

    public SingleSelectBottomPopView setData(CharSequence title, List<SingleSelectBean> dataArray) {
        this.title = title;
        this.dataArray = dataArray;
        return this;
    }

    private OnCallbackListener callbackListener;

    public SingleSelectBottomPopView setOnSelectListener(OnCallbackListener callbackListener) {
        this.callbackListener = callbackListener;
        return this;
    }

    public interface OnCallbackListener {
        void onCallback(int position, SingleSelectBean item);
    }
}