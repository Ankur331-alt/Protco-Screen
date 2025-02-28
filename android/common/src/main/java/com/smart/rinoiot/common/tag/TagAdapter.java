package com.smart.rinoiot.common.tag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smart.rinoiot.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HanHailong on 15/10/19.
 * T 必须实现TagBaseBean 基类
 */
@SuppressLint({"ViewHolder", "InflateParams"})
public class TagAdapter<T> extends BaseAdapter implements OnInitSelectedPosition {

    private final Context mContext;
    private final List<T> mDataList;

    public TagAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item, null);

        TextView textView = view.findViewById(R.id.tv_tag);
        T t = mDataList.get(position);

        if (t instanceof String) {
            textView.setText((String) t);
        } else if (t instanceof TagBaseBean) {
            textView.setText(((TagBaseBean) t).getTagName());
//            textView.setBackgroundResource(R.drawable.tag_selector);
//            textView.setPadding(DpUtils.dip2px(18),DpUtils.dip2px(5),DpUtils.dip2px(18),DpUtils.dip2px(5));
        }
        return view;
    }

    public void onlyAddAll(List<T> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<T> datas) {
        mDataList.clear();
        onlyAddAll(datas);
    }

    @Override
    public boolean isSelectedPosition(int position) {
        return position % 2 == 0;
    }
}