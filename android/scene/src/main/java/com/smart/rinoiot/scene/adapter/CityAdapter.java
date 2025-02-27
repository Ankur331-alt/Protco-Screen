package com.smart.rinoiot.scene.adapter;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.CityHeadBean;
import com.smart.rinoiot.scene.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 国家地区列表适配器
 *
 * @className CountryAdapter
 * @date: 2020/6/3 2:22 PM
 * @author: xf
 */
public class CityAdapter extends BaseSectionQuickAdapter<CityHeadBean, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public CityAdapter(List<CityHeadBean> data) {
        super(R.layout.item_city_child, R.layout.item_city_head, data);
        addItemType(CityHeadBean.HEADER_TYPE, R.layout.item_city_head);
        addItemType(CityHeadBean.NORMAL_TYPE, R.layout.item_city_child);
    }

    @Override
    protected void convert(BaseViewHolder helper, CityHeadBean item) {
        helper.setText(R.id.tvCityName, item.getCityBean().getName());
        helper.setText(R.id.tv_code, "");
    }

    @Override
    protected void convertHeader(@NotNull BaseViewHolder baseViewHolder, @NotNull CityHeadBean cityHeadHelp) {
        baseViewHolder.setText(R.id.tvHead, cityHeadHelp.getHeaderStr());
    }
}
