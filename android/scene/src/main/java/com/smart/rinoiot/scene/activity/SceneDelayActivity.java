package com.smart.rinoiot.scene.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.SceneActionBean;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.databinding.ActivitySceneDelayBinding;
import com.smart.rinoiot.scene.viewmodel.SceneViewModel;

public class SceneDelayActivity extends BaseActivity<ActivitySceneDelayBinding, SceneViewModel> {

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_scene_task_delay);
    }

    @Override
    public void init() {
//        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
//        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        setToolBarRightText(getString(R.string.rino_common_next));

        if (getIntent().getBooleanExtra("isNeedResult", false)) {
            SceneActionBean actionBean = (SceneActionBean) getIntent().getSerializableExtra("task_bean");
            if (actionBean != null) {
                int[] value = changeToHMS(actionBean.getDelay());
                initWheelView(value[0], value[1], value[2]);
            } else {
                initWheelView(0, 1, 0);
            }
        } else {
            initWheelView(0, 1, 0);
        }
    }

    private void initWheelView(int hours, int minute, int second) {
        binding.hour.setAdapter(new NumericWheelAdapter(0, 5));
        binding.hour.setCyclic(true);
        binding.hour.setItemsVisibleCount(5);
        binding.hour.setCurrentItem(hours);
        binding.hour.setLabel(getString(R.string.rino_common_hour_unit));

        binding.min.setAdapter(new NumericWheelAdapter(0, 59));
        binding.min.setCyclic(true);
        binding.min.setItemsVisibleCount(5);
        binding.min.setCurrentItem(minute);
        binding.min.setLabel(getString(R.string.rino_common_minutes_unit));

        binding.second.setAdapter(new NumericWheelAdapter(0, 59));
        binding.second.setCyclic(true);
        binding.second.setItemsVisibleCount(5);
        binding.second.setCurrentItem(second);
        binding.second.setLabel(getString(R.string.rino_common_second_unit));
    }

    private long getSecond() {
        return binding.hour.getCurrentItem() * 3600 + binding.min.getCurrentItem() * 60 + binding.second.getCurrentItem();
    }

    private int[] changeToHMS(long second) {
        long remainder = second;
        int[] result = new int[3];
        result[0] = (int) (remainder / 3600);
        remainder = remainder % 3600;
        result[1] = (int) (remainder / 60);
        remainder = remainder % 60;
        result[2] = (int) remainder;
        return result;
    }

    @Override
    public ActivitySceneDelayBinding getBinding(LayoutInflater inflater) {
        return ActivitySceneDelayBinding.inflate(inflater);
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        SceneActionBean actionBean = new SceneActionBean();
        actionBean.setActionType(Constant.SCENE_TASK_FOR_DELAYED);
        actionBean.setDelay(getSecond());

        if (getIntent().getBooleanExtra("isNeedResult", false)) {
            setResult(RESULT_OK, new Intent().putExtra("task_bean", actionBean)
                    .putExtra("position", getIntent().getIntExtra("position", -1)));
        } else {
            startActivity(new Intent(this, SceneConfigActivity.class)
                    .putExtra("task_bean", actionBean));
            AppManager.getInstance().finishActivity(CreateSceneActivity.class);
        }
        finishThis();
    }
}
