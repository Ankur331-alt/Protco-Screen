package com.smart.rinoiot.scene.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.SceneConditionBean;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.DateUtils;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.databinding.ActivitySceneTimingBinding;
import com.smart.rinoiot.scene.viewmodel.SceneConfigViewModel;

import java.util.Calendar;

public class SceneTimingActivity extends BaseActivity<ActivitySceneTimingBinding, SceneConfigViewModel> implements View.OnClickListener {

    public static final int REQUEST_CODE_FROM_REPEAT = 0x1001;

    private String weekData = "0000000";

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_scene_timing_title);
    }

    @Override
    public void init() {
//        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
//        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        setToolBarRightText(getString(R.string.rino_common_next));

        if (getIntent().getBooleanExtra("isNeedResult", false)) {
            SceneConditionBean conditionBean = (SceneConditionBean) getIntent().getSerializableExtra("condition_bean");
            if (conditionBean != null) {
                weekData = conditionBean.getLoops();
                binding.tvTimingRepeat.setText(mViewModel.getWeekData(weekData));

                String time = conditionBean.isTimeIsAm() ? conditionBean.getTime() : DateUtils.get24Time(conditionBean.getTime());

                String[] value = time.split(":");
                initWheelView(Integer.parseInt(value[0]), Integer.parseInt(value[1]));
            } else {
                initWheelView(-1, -1);
            }
        } else {
            initWheelView(-1, -1);
        }
        binding.viewTimingRepeat.setOnClickListener(this);
    }

    private void initWheelView(int hours, int minute) {
        if (hours == -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        binding.hour.setAdapter(new NumericWheelAdapter(0, 23));
        binding.hour.setCyclic(true);
        binding.hour.setItemsVisibleCount(5);
        binding.hour.setCurrentItem(hours);

        // 空白间隔，将小时与分钟隔开
        binding.wvBlank.setAdapter(new NumericWheelAdapter(0, 1));
        binding.wvBlank.setItemsVisibleCount(5);

        binding.min.setAdapter(new NumericWheelAdapter(0, 59));
        binding.min.setCyclic(true);
        binding.min.setItemsVisibleCount(5);
        binding.min.setCurrentItem(minute);
    }

    private String getTime() {
        StringBuilder result = new StringBuilder();
        if (binding.hour.getCurrentItem() < 10) {
            result.append(0);
        }
        result.append(binding.hour.getCurrentItem());
        result.append(":");
        if (binding.min.getCurrentItem() < 10) {
            result.append(0);
        }
        result.append(binding.min.getCurrentItem());
        return result.toString();
    }

    @Override
    public ActivitySceneTimingBinding getBinding(LayoutInflater inflater) {
        return ActivitySceneTimingBinding.inflate(inflater);
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);

        SceneConditionBean conditionBean = new SceneConditionBean();
        String tz = UserInfoManager.getInstance().getUserInfo(SceneTimingActivity.this).tz;
        if (TextUtils.isEmpty(tz)) tz = AppUtil.getSystemTimeZone();
        conditionBean.setTz(tz);
        conditionBean.setCondType(Constant.SCENE_CONDITION_FOR_TIMING);
        conditionBean.setLoops(weekData);
        conditionBean.setTimeIsAm(!DateUtils.is24Time(getTime()));
        conditionBean.setTime(DateUtils.get12Time(getTime()));
        conditionBean.setExecuteDate(DateUtils.getStringFromDate(System.currentTimeMillis(), "yyyy-MM-dd"));

        if (getIntent().getBooleanExtra("isNeedResult", false)) {
            setResult(RESULT_OK, new Intent().putExtra("condition_bean", conditionBean)
                    .putExtra("position", getIntent().getIntExtra("position", -1)));
        } else {
            startActivity(new Intent(this, SceneConfigActivity.class)
                    .putExtra("condition_bean", conditionBean));
            AppManager.getInstance().finishActivity(CreateSceneActivity.class);
        }
        finishThis();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.viewTimingRepeat) {
            startActivityForResult(new Intent(this, SceneListSelectActivity.class)
                    .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_time_repeat_title))
                    .putExtra(Constant.ACTIVITY_SUB_TITLE, getString(R.string.rino_scene_time_executed_once_default))
                    .putExtra("week_select_data", weekData)
                    .putExtra(Constant.SCENE_LIST_SELECT_TYPE, Constant.SCENE_LIST_SELECT_FOR_WEEK), REQUEST_CODE_FROM_REPEAT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE_FROM_REPEAT) {
            weekData = data != null ? data.getStringExtra("week_select_data") : weekData;
            binding.tvTimingRepeat.setText(mViewModel.getWeekData(weekData));
        }
    }
}
