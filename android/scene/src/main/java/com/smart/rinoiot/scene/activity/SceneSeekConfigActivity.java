package com.smart.rinoiot.scene.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceDpJsonBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.SceneActionBean;
import com.smart.rinoiot.common.bean.SceneConditionBean;
import com.smart.rinoiot.common.bean.SceneExprBean;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.BigDecimalUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.databinding.ActivitySceneSeekConfigBinding;
import com.smart.rinoiot.scene.viewmodel.SceneViewModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneSeekConfigActivity extends BaseActivity<ActivitySceneSeekConfigBinding, SceneViewModel> implements View.OnClickListener {

    public static final int REQUEST_CODE_FROM_CITY_SELECT = 0x1001;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
        /// setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
        /// StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        setToolBarRightText(getString(R.string.rino_common_next));

        String title = getIntent().getStringExtra(Constant.ACTIVITY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setToolBarTitle(title);
        }

        mViewModel.getCityLiveData().observe(this, cityBean -> {
            binding.tvItemValue.setText(cityBean.getName());
            binding.tvItemValue.setTag(cityBean.getCityCode());
        });

        switch (getIntent().getStringExtra(Constant.SCENE_SEEK_CONFIG_TYPE)) {
            case Constant.SCENE_SEEK_CONFIG_FOR_TEMPERATURE: {
                String selectText = getString(R.string.rino_scene_value_less_than);
                String unit = "℃";
                int select = -40;

                SceneConditionBean conditionBean = (SceneConditionBean) getIntent().getSerializableExtra("condition_bean");
                if (conditionBean != null) {
                    SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                    selectText = sceneExprBean.getExpression();
                    select = Integer.parseInt(sceneExprBean.getValue().toString());
                    unit = sceneExprBean.getUnit();
                    initViewItem(getString(R.string.rino_scene_weather_current_city), conditionBean.getCityName());
                    binding.tvItemValue.setTag(conditionBean.getCityCode());
                } else {
                    initViewItem(getString(R.string.rino_scene_weather_current_city), getString(R.string.rino_common_city_locating));
                    mViewModel.toLocationCity(this);
                }
                initSingleChoice(getString(R.string.rino_scene_value_less_than), getString(R.string.rino_scene_value_equal), getString(R.string.rino_scene_value_more_than), selectText);
                initSeekBar(-40, 40, 1, 0, unit, select);
                break;
            }
            case Constant.SCENE_SEEK_CONFIG_FOR_WIND_SPEED: {
                String selectText = getString(R.string.rino_scene_value_less_than);
                String unit = "m/s";
                int select = 0;

                SceneConditionBean conditionBean = (SceneConditionBean) getIntent().getSerializableExtra("condition_bean");
                if (conditionBean != null) {
                    SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                    selectText = sceneExprBean.getExpression();
                    select = Integer.parseInt(sceneExprBean.getValue().toString());
                    unit = sceneExprBean.getUnit();
                    initViewItem(getString(R.string.rino_scene_weather_current_city), conditionBean.getCityName());
                    binding.tvItemValue.setTag(conditionBean.getCityCode());
                } else {
                    initViewItem(getString(R.string.rino_scene_weather_current_city), getString(R.string.rino_common_city_locating));
                    mViewModel.toLocationCity(this);
                }
                initSingleChoice(getString(R.string.rino_scene_value_less_than), getString(R.string.rino_scene_value_equal), getString(R.string.rino_scene_value_more_than), selectText);
                initSeekBar(0, 62, 1, 0, unit, select);
                break;
            }
            case Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE:
                binding.viewItem.setVisibility(View.GONE);

                String devId = getIntent().getStringExtra("device_id");
                String dpKey = getIntent().getStringExtra("dp_key");
                String value = getIntent().getStringExtra("value");

                String selectText = getString(R.string.rino_scene_value_less_than);
                String unit = "";
                double select = 0;
                if (!TextUtils.isEmpty(value)) {
                    select = Double.parseDouble(value);
                }
                double min = 0;
                double max = 0;
                double step = 0;
                // 保留小数点后几位
                int accuracy = 0;

                DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(devId);
                for (DeviceDpBean deviceDpBean: CacheDataManager.getInstance().getDeviceDpList(deviceInfo.getId())) {
                    DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);

                    if (deviceDpJson.getIdentifier().equals(dpKey)) {
                        setToolBarTitle(deviceDpJson.getName());

                        unit = deviceDpJson.getDataType().getSpecs().get("unitName").toString();
                        min = Double.parseDouble(deviceDpJson.getDataType().getSpecs().get("min").toString());
                        max = Double.parseDouble(deviceDpJson.getDataType().getSpecs().get("max").toString());
                        step = Double.parseDouble(deviceDpJson.getDataType().getSpecs().get("step").toString());
                        try {
                            accuracy = (int) Double.parseDouble(deviceDpJson.getDataType().getSpecs().get("accuracy").toString());
                            if (select < min) {
                                select = min;
                            }
                            if (select > max) {
                                select = max;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

                if (getIntent().getIntExtra("condition_or_task", -1) == Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION) {
                    SceneConditionBean conditionBean = (SceneConditionBean) getIntent().getSerializableExtra("condition_bean");
                    if (conditionBean != null) {
                        SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                        selectText = sceneExprBean.getExpression();
                        select = Double.parseDouble(sceneExprBean.getValue().toString());
                        unit = sceneExprBean.getUnit();
                    }
                } else {
                    binding.viewValue.setVisibility(View.GONE);
                    binding.lineValue.setVisibility(View.GONE);
                    List<SceneActionBean> actionArrayList = (List<SceneActionBean>) getIntent().getSerializableExtra("action_array_list");
                    int position = getIntent().getIntExtra("position", -1);
                    SceneActionBean actionBean = actionArrayList.get(position);
                    Map<String, Map<String, Object>> actionData = new Gson().fromJson(actionBean.getActionData(), new TypeToken<Map<String, Object>>(){}.getType());
                    Map<String, Object> props = actionData.get("props");
                    if (props != null) {
                        select = Double.parseDouble(BigDecimalUtils.round(props.get(dpKey).toString(), accuracy));
                    }
                }

                initSingleChoice(getString(R.string.rino_scene_value_less_than), getString(R.string.rino_scene_value_equal), getString(R.string.rino_scene_value_more_than), selectText);
                initSeekBar(min, max, step, accuracy, unit, select);
                break;
            default:
                break;
        }
    }

    private void initViewItem(String name, String value) {
        binding.tvItemName.setText(name);
        binding.tvItemValue.setText(value);

        binding.viewItem.setOnClickListener(this);
    }

    private void initSingleChoice(String min, String middle, String max, String select) {
        binding.tvValue1.setText(min);
        binding.tvValue2.setText(middle);
        binding.tvValue3.setText(max);

        if (TextUtils.isEmpty(select) || min.equals(select) || "<".equals(select)) {
            binding.tvValue1.setSelected(true);
        } else if (middle.equals(select) || "==".equals(select)) {
            binding.tvValue2.setSelected(true);
        } else if (max.equals(select) || ">".equals(select)) {
            binding.tvValue3.setSelected(true);
        }

        binding.tvValue1.setOnClickListener(this);
        binding.tvValue2.setOnClickListener(this);
        binding.tvValue3.setOnClickListener(this);
    }

    private String getJudgeCondition() {
        String result = "";
        if (binding.tvValue1.isSelected()) {
            result = "<";
        } else if (binding.tvValue2.isSelected()) {
            result = "==";
        } else if (binding.tvValue3.isSelected()) {
            result = ">";
        }

        return result;
    }

    @SuppressLint("SetTextI18n")
    private void initSeekBar(double seekBarMin, double seekBarMax, double step, int scale, String unit, double select) {
        int max = (int) ((seekBarMax - seekBarMin) / step);
        int value = (int) (select / step);

        binding.tvSeekValue.setText(BigDecimalUtils.round(String.valueOf(select), scale));
        binding.tvSeekUnit.setText(unit);
        binding.tvSeekMin.setText(BigDecimalUtils.round(String.valueOf(seekBarMin), scale) + unit);
        binding.tvSeekMax.setText(BigDecimalUtils.round(String.valueOf(seekBarMax), scale) + unit);
        binding.seekBar.setMax(max);
        binding.seekBar.setProgress(value);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tvSeekValue.setText(BigDecimalUtils.mul(String.valueOf(progress + seekBarMin), String.valueOf(step), scale));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public ActivitySceneSeekConfigBinding getBinding(LayoutInflater inflater) {
        return ActivitySceneSeekConfigBinding.inflate(inflater);
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        if ((binding.tvItemValue.getTag() == null || TextUtils.isEmpty(binding.tvItemValue.getTag().toString())) && binding.viewItem.getVisibility() == View.VISIBLE) {
            ToastUtil.showMsg(R.string.rino_scene_please_select_city);
            return;
        }

        if (Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE.equals(getIntent().getStringExtra(Constant.SCENE_SEEK_CONFIG_TYPE))) {
            String devId = getIntent().getStringExtra("device_id");
            String dpKey = getIntent().getStringExtra("dp_key");

            if (getIntent().getIntExtra("condition_or_task", -1) == Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION) {
                SceneConditionBean conditionBean = (SceneConditionBean) getIntent().getSerializableExtra("condition_bean");
                if (conditionBean != null) {
                    SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                    devId = conditionBean.getTargetId();
                    dpKey = sceneExprBean.getPropName();
                }
            } else {
                List<SceneActionBean> actionArrayList = (List<SceneActionBean>) getIntent().getSerializableExtra("action_array_list");
                int selPosition = getIntent().getIntExtra("position", -1);
                SceneActionBean actionBean = actionArrayList.get(selPosition);
                devId = actionBean.getTargetId();
                Map<String, Map<String, Object>> actionData = new Gson().fromJson(actionBean.getActionData(), new TypeToken<Map<String, Object>>(){}.getType());
                Map<String, Object> props = actionData.get("props");
                if (props != null) {
                    for (String mapKey: props.keySet()) {
                        dpKey = mapKey;
                    }
                }
            }

            DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(devId);
            for (DeviceDpBean deviceDpBean: CacheDataManager.getInstance().getDeviceDpList(deviceInfo.getId())) {
                DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);

                if (deviceDpJson.getIdentifier().equals(dpKey)) {
                    if (getIntent().getIntExtra("condition_or_task", -1) == Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION) {
                        SceneConditionBean conditionBean = new SceneConditionBean();
                        conditionBean.setCondType(Constant.SCENE_CONDITION_FOR_DEVICE_STATUS_CHANGE);
                        SceneExprBean sceneExprBean = new SceneExprBean();
                        sceneExprBean.setPropName(deviceDpJson.getIdentifier());
                        sceneExprBean.setExpression(getJudgeCondition());
                        sceneExprBean.setValue(binding.tvSeekValue.getText().toString());
                        sceneExprBean.setUnit(binding.tvSeekUnit.getText().toString());
                        conditionBean.setExpr(new GsonBuilder().disableHtmlEscaping().create().toJson(sceneExprBean));
                        conditionBean.setTargetId(devId);

                        if (getIntent().getBooleanExtra("isNeedResult", false)) {
                            setResult(RESULT_OK, new Intent().putExtra("condition_bean", conditionBean)
                                    .putExtra("position", getIntent().getIntExtra("position", -1)));
                        } else {
                            startActivity(new Intent(this, SceneConfigActivity.class)
                                    .putExtra("condition_bean", conditionBean));
                            AppManager.getInstance().finishActivity(SceneListSelectActivity.class);
                            AppManager.getInstance().finishActivity(SceneSelectDeviceActivity.class);
                            AppManager.getInstance().finishActivity(CreateSceneActivity.class);
                        }

                        finishThis();
                        break;
                    } else {
                        List<SceneActionBean> actionArrayList = (List<SceneActionBean>) getIntent().getSerializableExtra("action_array_list");
                        int selPosition = getIntent().getIntExtra("position", -1);
                        SceneActionBean actionBean = actionArrayList.get(selPosition);

                        Map<String, Object> props = new HashMap<>();
                        props.put(deviceDpJson.getIdentifier(), binding.tvSeekValue.getText().toString());

                        actionBean.setActionData("{\"props\":" + new Gson().toJson(props) + "}");
                        actionArrayList.set(selPosition, actionBean);

                        if (getIntent().getBooleanExtra("isNeedResult", false)) {
                            setResult(RESULT_OK, new Intent().putExtra("action_array_list", (Serializable) actionArrayList));
                        }

                        finishThis();
                    }
                }
            }
        } else {
            SceneConditionBean conditionBean = new SceneConditionBean();
            conditionBean.setCondType(Constant.SCENE_CONDITION_FOR_CHANGE_WEATHER);
            SceneExprBean sceneExprBean = new SceneExprBean();
            sceneExprBean.setPropName(getIntent().getStringExtra(Constant.SCENE_SEEK_CONFIG_TYPE));
            sceneExprBean.setExpression(getJudgeCondition());
            sceneExprBean.setValue(binding.tvSeekValue.getText().toString());
            sceneExprBean.setUnit(binding.tvSeekUnit.getText().toString());
            conditionBean.setExpr(new GsonBuilder().disableHtmlEscaping().create().toJson(sceneExprBean));
            if (binding.viewItem.getVisibility() == View.VISIBLE) {
                conditionBean.setCityName(binding.tvItemValue.getText().toString());
                conditionBean.setCityCode(binding.tvItemValue.getTag() != null?binding.tvItemValue.getTag().toString():"");
            }

            if (getIntent().getBooleanExtra("isNeedResult", false)) {
                setResult(RESULT_OK, new Intent().putExtra("condition_bean", conditionBean)
                        .putExtra("position", getIntent().getIntExtra("position", -1)));
            } else {
                startActivity(new Intent(this, SceneConfigActivity.class)
                        .putExtra("condition_bean", conditionBean));
                AppManager.getInstance().finishActivity(SceneListSelectActivity.class);
                AppManager.getInstance().finishActivity(CreateSceneActivity.class);
            }
            finishThis();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.viewItem) {
            startActivityForResult(new Intent(getBaseContext(), CityActivity.class)
                            .putExtra(Constant.CURRENT_CITY, binding.tvItemValue.getText().toString())
                    , REQUEST_CODE_FROM_CITY_SELECT);
        } else if (v.getId() == R.id.tvValue1 || v.getId() == R.id.tvValue2 || v.getId() == R.id.tvValue3) {
            if (v.getId() == R.id.tvValue1) {
                binding.tvValue2.setSelected(false);
                binding.tvValue3.setSelected(false);
            } else if (v.getId() == R.id.tvValue2) {
                binding.tvValue1.setSelected(false);
                binding.tvValue3.setSelected(false);
            } else if (v.getId() == R.id.tvValue3) {
                binding.tvValue1.setSelected(false);
                binding.tvValue2.setSelected(false);
            }
            v.setSelected(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_FROM_CITY_SELECT) {
            CityBean cityBean = (CityBean) data.getSerializableExtra("city_bean");
            binding.tvItemValue.setText(cityBean.getName());
            binding.tvItemValue.setTag(cityBean.getCityCode());
        }
    }
}
