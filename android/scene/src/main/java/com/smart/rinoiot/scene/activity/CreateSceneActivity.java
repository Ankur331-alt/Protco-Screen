package com.smart.rinoiot.scene.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.SceneConditionBean;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.databinding.ActivityCreateSceneBinding;
import com.smart.rinoiot.scene.viewmodel.SceneViewModel;

/**
 * @author author
 */
public class CreateSceneActivity extends BaseActivity<ActivityCreateSceneBinding, SceneViewModel> implements View.OnClickListener {

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_scene_create);
    }

    @Override
    public void init() {
        /// setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
        /// StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        int sceneType=getIntent().getIntExtra(Constant.SCENE_TYPE, 0);
        binding.viewOneClickExecution.setVisibility(sceneType==1?View.GONE:View.VISIBLE);
        binding.viewOneClickExecution.setOnClickListener(this);
        binding.viewWeatherChange.setOnClickListener(this);
        binding.viewTiming.setOnClickListener(this);
        binding.viewDeviceChange.setOnClickListener(this);
    }

    @Override
    public ActivityCreateSceneBinding getBinding(LayoutInflater inflater) {
        return ActivityCreateSceneBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.viewOneClickExecution) {
            SceneConditionBean conditionBean = new SceneConditionBean();
            conditionBean.setCondType(Constant.SCENE_CONDITION_FOR_ONE_KEY);

            startActivity(new Intent(this, SceneConfigActivity.class)
                    .putExtra("condition_bean", conditionBean));
            finishThis();
        } else if (v.getId() == R.id.viewWeatherChange) {
            startActivity(new Intent(this, SceneListSelectActivity.class)
                    .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_weather_changes_title))
                    .putExtra(Constant.SCENE_LIST_SELECT_TYPE, Constant.SCENE_LIST_SELECT_FOR_WEATHER));
        } else if (v.getId() == R.id.viewTiming) {
            startActivity(new Intent(this, SceneTimingActivity.class));
        } else if (v.getId() == R.id.viewDeviceChange) {
            startActivity(
                    new Intent(this, SceneSelectDeviceActivity.class)
                            .putExtra(
                                    "condition_or_task",
                                    Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION
                            )
            );
        }
    }
}
