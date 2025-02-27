package com.smart.rinoiot.user.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.TimeZoneBean;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.adapter.TimeZoneAdapter;
import com.smart.rinoiot.user.bean.TimeZoneHeadBean;
import com.smart.rinoiot.user.databinding.ActivityCountryBinding;
import com.smart.rinoiot.user.viewmodel.LoginViewModel;

import java.util.List;

public class TimeZoneActivity extends BaseActivity<ActivityCountryBinding, LoginViewModel> {

    /** 适配器 */
    private TimeZoneAdapter timeZoneAdapter;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_mine_time_zone);
    }

    @Override
    public void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        timeZoneAdapter = new TimeZoneAdapter(null);
        binding.recyclerView.setAdapter(timeZoneAdapter);
        mViewModel.showLoading();
        mViewModel.getNetTimeZoneList();
        mViewModel.getTimeZoneLiveData().observe(this, timeZoneList -> {
            mViewModel.hideLoading();
            if (timeZoneList != null && !timeZoneList.isEmpty()) {
                timeZoneAdapter.setNewInstance(timeZoneList);
                timeZoneAdapter.notifyDataSetChanged();
            }
        });
        binding.sideBar.setOnSelectIndexItemListener(index -> {
            if (mViewModel.getIndexMap() != null && mViewModel.getIndexMap().get(index) != null) {
                linearLayoutManager.scrollToPositionWithOffset(mViewModel.getIndexMap().get(index), 0);
            }
        });
        timeZoneAdapter.setOnItemClickListener((adapter, view, position) -> {
            TimeZoneBean bean;
            List<TimeZoneHeadBean> searchData = mViewModel.getSearchTimeZoneData().getValue();
            if (searchData != null && searchData.size() > 0) {
                bean = searchData.get(position).getTimeZoneBean();
            } else {
                bean = mViewModel.getTimeZoneList().get(position).getTimeZoneBean();
            }
            if (bean != null) {
                Intent intent = new Intent();
                intent.putExtra("tz", bean.getTz());
                setResult(RESULT_OK, intent);
                finishThis();
            }
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.searchTimeZone(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mViewModel.getSearchTimeZoneData().observe(this, timeZoneHeadHelps -> {
            timeZoneAdapter.setNewInstance(timeZoneHeadHelps);
            timeZoneAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public ActivityCountryBinding getBinding(LayoutInflater inflater) {
        return ActivityCountryBinding.inflate(inflater);
    }
}
