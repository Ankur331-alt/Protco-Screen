package com.smart.rinoiot.user.adapter;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.bean.CountryHeadBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 国家地区列表适配器
 *
 * @className CountryAdapter
 * @date: 2020/6/3 2:22 PM
 * @author: xf
 */
public class CountryAdapter extends BaseSectionQuickAdapter<CountryHeadBean, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public CountryAdapter(List<CountryHeadBean> data) {
        super(R.layout.item_country_child, R.layout.item_country_head, data);
        addItemType(CountryHeadBean.HEADER_TYPE, R.layout.item_country_head);
        addItemType(CountryHeadBean.NORMAL_TYPE, R.layout.item_country_child);
    }

    @Override
    protected void convert(BaseViewHolder helper, CountryHeadBean item) {
        helper.setText(R.id.tvCountryName, item.getCountryBean().getCountryName() + " +" + item.getCountryBean().getCountryCode());
        helper.setText(R.id.tv_code, "");
    }

    @Override
    protected void convertHeader(@NotNull BaseViewHolder baseViewHolder, @NotNull CountryHeadBean countryHeadHelp) {
        baseViewHolder.setText(R.id.tvHead, countryHeadHelp.getHeaderStr());
    }
}
