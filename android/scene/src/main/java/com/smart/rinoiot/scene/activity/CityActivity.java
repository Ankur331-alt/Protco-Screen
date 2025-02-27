package com.smart.rinoiot.scene.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.CityHeadBean;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.adapter.CityAdapter;
import com.smart.rinoiot.scene.databinding.ActivityCityBinding;
import com.smart.rinoiot.scene.viewmodel.CityViewModel;

import java.util.List;

public class CityActivity extends BaseActivity<ActivityCityBinding, CityViewModel> {
    /**
     * 适配器1
     */
    private CityAdapter cityAdapter;
    private CityBean locationCity;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_scene_please_select_city);
    }

    @Override
    public void init() {
        binding.viewLocationCity.setOnClickListener(v -> {
            if (locationCity != null) {
                Intent result = new Intent();
                result.putExtra("city_bean", locationCity);
                setResult(RESULT_OK, result);
                finishThis();
            }
        });

        binding.tvReLocation.setOnClickListener(v -> {
            binding.tvCurrentCity.setText(getString(R.string.rino_common_city_locating));
            mViewModel.toLocationCity(v.getContext());
        });

        binding.tvCurrentCity.setText(getIntent().getStringExtra(Constant.CURRENT_CITY));
        /// mViewModel.toLocationCity(this);

        mViewModel.getCityLiveData().observe(this, cityBean -> {
            locationCity = cityBean;
            binding.tvCurrentCity.setText(cityBean.getName());
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        cityAdapter = new CityAdapter(null);
        binding.recyclerView.setAdapter(cityAdapter);

        mViewModel.getCityData();
        mViewModel.getCityListLiveData().observe(this, data -> {
            cityAdapter.setNewInstance(data);
            cityAdapter.notifyDataSetChanged();
        });

        binding.sideBar.setOnSelectIndexItemListener(index -> {
            if (mViewModel.getIndexMap() != null && mViewModel.getIndexMap().get(index) != null) {
                linearLayoutManager.scrollToPositionWithOffset(mViewModel.getIndexMap().get(index), 0);
            }
        });
        cityAdapter.setOnItemClickListener((adapter, view, position) -> {
            CityBean bean;
            List<CityHeadBean> searchData = mViewModel.getSearchData().getValue();
            if (searchData != null && searchData.size() > 0) {
                bean = searchData.get(position).getCityBean();
            } else {
                bean = mViewModel.getCityList().get(position).getCityBean();
            }
            if (bean != null) {
                Intent result = new Intent();
                result.putExtra("city_bean", bean);
                setResult(RESULT_OK, result);
                finishThis();
            }
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.searchCity(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mViewModel.getSearchData().observe(this, countryHeadHelps -> {
            cityAdapter.setNewInstance(countryHeadHelps);
            cityAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public ActivityCityBinding getBinding(LayoutInflater inflater) {
        return ActivityCityBinding.inflate(inflater);
    }

    @Override
    public void onBack(View view) {
        super.onBack(view);
//            if (locationCity != null) {
//            Intent result = new Intent();
//            result.putExtra("city_bean", locationCity);
//            setResult(RESULT_OK, result);
//            finishThis();
//        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (locationCity != null) {
                Intent result = new Intent();
                result.putExtra("city_bean", locationCity);
                setResult(RESULT_OK, result);
                finishThis();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
