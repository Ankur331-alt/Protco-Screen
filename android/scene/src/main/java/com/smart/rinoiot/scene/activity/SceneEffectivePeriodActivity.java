package com.smart.rinoiot.scene.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.SceneTriggerBean;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.adapter.EffectivePeriodAdapter;
import com.smart.rinoiot.scene.bean.SingleSelectBean;
import com.smart.rinoiot.scene.databinding.ActivitySceneEffectivePeriodBinding;
import com.smart.rinoiot.scene.viewmodel.SceneConfigViewModel;

import java.util.ArrayList;
import java.util.List;

public class SceneEffectivePeriodActivity extends BaseActivity<ActivitySceneEffectivePeriodBinding, SceneConfigViewModel> implements View.OnClickListener {

    public static final int REQUEST_CODE_FROM_REPEAT = 0x1001;

    private EffectivePeriodAdapter effectivePeriodAdapter;

    private SceneTriggerBean sceneTriggerBean;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_scene_more_effective_period);
    }

    @Override
    public void init() {
//        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
//        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        setToolBarRightText(getString(R.string.rino_common_next));

        binding.viewRepeat.setOnClickListener(this);

        sceneTriggerBean = (SceneTriggerBean) getIntent().getSerializableExtra("sceneTriggerBean");
        if (sceneTriggerBean==null) return;
        binding.tvCity.setText(sceneTriggerBean.getCityName());
        binding.tvRepeat.setText(mViewModel.getWeekData(sceneTriggerBean.getLoops()));

        List<SingleSelectBean> arrayList = new ArrayList<>();
        SingleSelectBean item = new SingleSelectBean();
        item.setName(getString(R.string.rino_scene_effective_period_allday));
        item.setValue(getString(R.string.rino_scene_effective_period_allday_sub));
        item.setSelect(sceneTriggerBean.getTimeInterval() == Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_ALL_DAY);
        arrayList.add(item);

        item = new SingleSelectBean();
        item.setName(getString(R.string.rino_scene_effective_period_daytime));
        item.setValue(getString(R.string.rino_scene_effective_period_daytime_sub));
        item.setSelect(sceneTriggerBean.getTimeInterval() == Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_DAY);
        arrayList.add(item);

        item = new SingleSelectBean();
        item.setName(getString(R.string.rino_scene_effective_period_atnight));
        item.setValue(getString(R.string.rino_scene_effective_period_atnight_sub));
        item.setSelect(sceneTriggerBean.getTimeInterval() == Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_NIGHT);
        arrayList.add(item);

        item = new SingleSelectBean();
        item.setName(getString(R.string.rino_scene_effective_period_custom));
        if (sceneTriggerBean.getTimeInterval() == Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_CUSTOM) {
            item.setValue(mViewModel.getEffectivePeriodStartTime(sceneTriggerBean) + "-" + mViewModel.getEffectivePeriodEndTime(sceneTriggerBean));
            item.setSelect(true);
        } else {
            item.setValue(getString(R.string.rino_scene_effective_period_custom_sub));
            item.setSelect(false);
        }
        arrayList.add(item);

        initSingleChoice(arrayList);
    }

    private void initSingleChoice(List<SingleSelectBean> array) {
        effectivePeriodAdapter = new EffectivePeriodAdapter(array);
        binding.recyclerView.setAdapter(effectivePeriodAdapter);
        binding.recyclerView.setVisibility(View.VISIBLE);

        effectivePeriodAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (getString(R.string.rino_scene_effective_period_custom).equals(effectivePeriodAdapter.getData().get(position).getName())) {
                mViewModel.showDoubleTimeWheelDialog(view.getContext(),
                        mViewModel.getEffectivePeriodStartTime(sceneTriggerBean),
                        mViewModel.getEffectivePeriodEndTime(sceneTriggerBean),
                        (startTime, endTime) -> {
                            mViewModel.setEffectivePeriod(sceneTriggerBean, startTime, endTime);
                            List<SingleSelectBean> arrayList = effectivePeriodAdapter.getData();
                            SingleSelectBean item;
                            for (int i = 0; i < arrayList.size(); i++) {
                                item = arrayList.get(i);
                                item.setSelect(position == i);
                            }
                            sceneTriggerBean.setTimeInterval(mViewModel.getTimeInterval(arrayList.get(position).getName()));
                            arrayList.get(position).setValue(startTime + "-" + endTime);
                            effectivePeriodAdapter.notifyDataSetChanged();
                        });
            } else {
                List<SingleSelectBean> arrayList = effectivePeriodAdapter.getData();
                SingleSelectBean item;
                for (int i = 0; i < arrayList.size(); i++) {
                    item = arrayList.get(i);
                    item.setSelect(position == i);
                }
                sceneTriggerBean.setTimeInterval(mViewModel.getTimeInterval(arrayList.get(position).getName()));
                effectivePeriodAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        Intent intent = new Intent();
        intent.putExtra("sceneTriggerBean", sceneTriggerBean);
        setResult(RESULT_OK, intent);

        finishThis();
    }

    @Override
    public ActivitySceneEffectivePeriodBinding getBinding(LayoutInflater inflater) {
        return ActivitySceneEffectivePeriodBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (sceneTriggerBean==null) return;
        if (v.getId() == R.id.viewCurrentCity) {
            // TODO
        } else if (v.getId() == R.id.viewRepeat) {
            startActivityForResult(new Intent(this, SceneListSelectActivity.class)
                    .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_time_repeat_title))
                    .putExtra("must_select_one", true)
                    .putExtra("week_select_data", sceneTriggerBean.getLoops())
                    .putExtra(Constant.SCENE_LIST_SELECT_TYPE, Constant.SCENE_LIST_SELECT_FOR_WEEK), REQUEST_CODE_FROM_REPEAT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE_FROM_REPEAT) {
            String result = data != null ? data.getStringExtra("week_select_data") : "";
            sceneTriggerBean.setLoops(result);
            binding.tvRepeat.setText(mViewModel.getWeekData(result));
        }
    }
}
