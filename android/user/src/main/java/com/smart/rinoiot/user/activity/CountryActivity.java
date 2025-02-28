package com.smart.rinoiot.user.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.CountryBean;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.adapter.CountryAdapter;
import com.smart.rinoiot.user.bean.CountryHeadBean;
import com.smart.rinoiot.user.databinding.ActivityCountryBinding;
import com.smart.rinoiot.user.viewmodel.LoginViewModel;

import java.util.List;

public class CountryActivity extends BaseActivity<ActivityCountryBinding, LoginViewModel> {

    /** 适配器 */
    private CountryAdapter countryAdapter;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_user_select_country);
    }

    @Override
    public void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        countryAdapter = new CountryAdapter(null);
        binding.recyclerView.setAdapter(countryAdapter);
        mViewModel.getNetCountryList();
        mViewModel.getCountryLiveData().observe(this, countryList -> {
            countryAdapter.setNewInstance(countryList);
            countryAdapter.notifyDataSetChanged();
        });
        binding.sideBar.setOnSelectIndexItemListener(index -> {
            if (mViewModel.getIndexMap() != null && mViewModel.getIndexMap().get(index) != null) {
                linearLayoutManager.scrollToPositionWithOffset(mViewModel.getIndexMap().get(index), 0);
            }
        });
        countryAdapter.setOnItemClickListener((adapter, view, position) -> {
            CountryBean bean;
            List<CountryHeadBean> searchData = mViewModel.getSearchData().getValue();
            if (searchData != null && searchData.size() > 0) {
                bean = searchData.get(position).getCountryBean();
            } else {
                bean = mViewModel.getCountryList().get(position).getCountryBean();
            }
            if (bean != null) {
//                SharedPreferenceUtil.getInstance().put(Constant.COUNTRY_CODE, bean.getCountryCode());
//                SharedPreferenceUtil.getInstance().put(Constant.COUNTRY_NAME, bean.getCountryName());

                CacheDataManager.getInstance().saveCurrentCountry(bean);
                setResult(RESULT_OK);
                finishThis();
            }
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.searchCountry(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mViewModel.getSearchData().observe(this, countryHeadHelps -> {
            countryAdapter.setNewInstance(countryHeadHelps);
            countryAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public ActivityCountryBinding getBinding(LayoutInflater inflater) {
        return ActivityCountryBinding.inflate(inflater);
    }
}
