package com.smart.rinoiot.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.MultiItemTypeAdapter;
import com.lxj.easyadapter.ViewHolder;
import com.lxj.xpopup.core.CenterPopupView;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.listener.DialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Description: 在中间的列表对话框,图片为选择和未选中
 * Create by dance, at 2018/12/16
 */
@SuppressLint("ViewConstructor")
public class CenterListSelectedPopupView extends CenterPopupView {
    RecyclerView recyclerView;
    TextView tv_title;

    /**
     * @param context
     * @param bindLayoutId     要求layoutId中必须有一个id为recyclerView的RecyclerView，如果你需要显示标题，则必须有一个id为tv_title的TextView
     * @param bindItemLayoutId 条目的布局id，要求布局中有id为iv_image的ImageView（非必须），和id为tv_text的TextView
     */
    public CenterListSelectedPopupView(@NonNull Context context, int bindLayoutId, int bindItemLayoutId) {
        super(context);
        this.bindLayoutId = bindLayoutId;
        this.bindItemLayoutId = bindItemLayoutId;
        addInnerContent();
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_center_impl_set_more : bindLayoutId;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        recyclerView = findViewById(R.id.recyclerView);
        if (bindLayoutId != 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        tv_title = findViewById(R.id.tv_title);
        if (tv_title != null) {
            if (TextUtils.isEmpty(title)) {
                tv_title.setVisibility(GONE);
            } else {
                tv_title.setText(title);
            }
        }

        final EasyAdapter<String> adapter = new EasyAdapter<String>(Arrays.asList(data), bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text_set_more : bindItemLayoutId) {
            @Override
            protected void bind(@NonNull ViewHolder holder, @NonNull String s, int position) {
                holder.setText(R.id.tv_text, s);
                ImageView imageView = holder.getViewOrNull(R.id.iv_image);
                imageView.setImageResource(checkedPosition == position ? iconIds[0] : iconIds[1]);
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.SimpleOnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, RecyclerView.@NotNull ViewHolder holder, int position) {
                if (checkedPosition != -1) {
                    checkedPosition = position;
                    adapter.notifyDataSetChanged();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        applyTheme();

        findViewById(R.id.tvCancel).setOnClickListener(v -> dismiss());
        findViewById(R.id.tvConfirm).setOnClickListener(v -> {
            dismiss();
            if (dialogListener != null && adapter != null && adapter.getData() != null && adapter.getData().size() > checkedPosition)
                dialogListener.onConfirm(adapter.getData().get(checkedPosition),checkedPosition);
        });
    }

    @Override
    protected void applyDarkTheme() {
        super.applyDarkTheme();
    }

    @Override
    protected void applyLightTheme() {
        super.applyLightTheme();
    }

    CharSequence title;
    String[] data;
    int[] iconIds;

    public CenterListSelectedPopupView setStringData(CharSequence title, String[] data, int[] iconIds) {
        this.title = title;
        this.data = data;
        this.iconIds = iconIds;
        return this;
    }

    DialogListener dialogListener;

    public CenterListSelectedPopupView setOnSelectListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
        return this;
    }

    int checkedPosition = -1;

    /**
     * 设置默认选中的位置
     *
     * @param position
     * @return
     */
    public CenterListSelectedPopupView setCheckedPosition(int position) {
        this.checkedPosition = position;
        return this;
    }

    protected int getMaxWidth() {
        if (popupInfo == null) return 0;
        return popupInfo.maxWidth == 0 ? super.getMaxWidth() : popupInfo.maxWidth;
    }
}
