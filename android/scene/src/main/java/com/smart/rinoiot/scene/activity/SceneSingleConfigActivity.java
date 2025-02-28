package com.smart.rinoiot.scene.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

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
import com.smart.rinoiot.common.scene.ConditionEnum;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.adapter.SingleSelectAdapter;
import com.smart.rinoiot.scene.bean.SingleSelectBean;
import com.smart.rinoiot.scene.databinding.ActivitySceneSingleConfigBinding;
import com.smart.rinoiot.scene.viewmodel.SceneViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SceneSingleConfigActivity extends BaseActivity<ActivitySceneSingleConfigBinding, SceneViewModel> {

    public static final int REQUEST_CODE_FROM_CITY_SELECT = 0x1001;

    private SingleSelectAdapter singleSelectAdapter;

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

        String subTitle = getIntent().getStringExtra(Constant.ACTIVITY_SUB_TITLE);
        if (!TextUtils.isEmpty(subTitle)) {
            binding.tvSubTitle.setText(subTitle);
        } else {
            binding.tvSubTitle.setVisibility(View.GONE);
        }

        mViewModel.getCityLiveData().observe(this, cityBean -> {
            if (cityBean != null) {
                binding.tvItemValue.setText(cityBean.getName());
                binding.tvItemValue.setTag(cityBean.getCityCode());
            }
        });

        SceneConditionBean conditionBean = (SceneConditionBean) getIntent().getSerializableExtra("condition_bean");
        if (getIntent().getBooleanExtra("isNeedResult", false)) {
            if (conditionBean != null) {
                initViewItem(getString(R.string.rino_scene_weather_current_city), conditionBean.getCityName());
                binding.tvItemValue.setTag(conditionBean.getCityCode());
            } else {
                mViewModel.toLocationCity(this);
                initViewItem(getString(R.string.rino_scene_weather_current_city), getString(R.string.rino_common_city_locating));
            }
        } else {
            mViewModel.toLocationCity(this);
            initViewItem(getString(R.string.rino_scene_weather_current_city), getString(R.string.rino_common_city_locating));
        }

        List<SingleSelectBean> arrayList = new ArrayList<>();
        SingleSelectBean item;
        switch (getIntent().getStringExtra(Constant.SCENE_SINGLE_CONFIG_TYPE)) {
            case Constant.SCENE_SINGLE_CONFIG_FOR_HUMIDITY:
                String humiditySelected = "";
                if (conditionBean != null) {
                    SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                    humiditySelected = sceneExprBean.getValue().toString();
                }

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_humidity_for_dry));
                item.setEnum(ConditionEnum.HUMIDITY_DRY);
                item.setSelect(humiditySelected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_humidity_for_comfortable));
                item.setEnum(ConditionEnum.HUMIDITY_COMFORT);
                item.setSelect(humiditySelected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_humidity_for_damp));
                item.setEnum(ConditionEnum.HUMIDITY_WET);
                item.setSelect(humiditySelected.equals(item.getEnum().getValue()));
                arrayList.add(item);
                break;
            case Constant.SCENE_SINGLE_CONFIG_FOR_WEATHER:
                String weatherSelected = "";
                if (conditionBean != null) {
                    SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                    weatherSelected = sceneExprBean.getValue().toString();
                }

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_weather_for_sunny_day));
                item.setEnum(ConditionEnum.WEATHER_SUNNY);
                item.setSelect(weatherSelected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_weather_for_overcast));
                item.setEnum(ConditionEnum.WEATHER_CLOUDY);
                item.setSelect(weatherSelected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_weather_for_rain));
                item.setEnum(ConditionEnum.WEATHER_RAINY);
                item.setSelect(weatherSelected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_weather_for_snowy_day));
                item.setEnum(ConditionEnum.WEATHER_SNOWY);
                item.setSelect(weatherSelected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_weather_for_greasy));
                item.setEnum(ConditionEnum.WEATHER_POLLUTED);
                item.setSelect(weatherSelected.equals(item.getEnum().getValue()));
                arrayList.add(item);
                break;
            case Constant.SCENE_SINGLE_CONFIG_FOR_PM25:
                String pm25Selected = "";
                if (conditionBean != null) {
                    SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                    pm25Selected = sceneExprBean.getValue().toString();
                }

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_excellent));
                item.setEnum(ConditionEnum.GOOD);
                item.setSelect(pm25Selected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_good));
                item.setEnum(ConditionEnum.FINE);
                item.setSelect(pm25Selected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_contaminated));
                item.setEnum(ConditionEnum.POLLUTED);
                item.setSelect(pm25Selected.equals(item.getEnum().getValue()));
                arrayList.add(item);
                break;
            case Constant.SCENE_SINGLE_CONFIG_FOR_AIR_QUALITY:
                String airQualitySelected = "";
                if (conditionBean != null) {
                    SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                    airQualitySelected = sceneExprBean.getValue().toString();
                }

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_excellent));
                item.setEnum(ConditionEnum.GOOD);
                item.setSelect(airQualitySelected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_good));
                item.setEnum(ConditionEnum.FINE);
                item.setSelect(airQualitySelected.equals(item.getEnum().getValue()));
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_contaminated));
                item.setEnum(ConditionEnum.POLLUTED);
                item.setSelect(airQualitySelected.equals(item.getEnum().getValue()));
                arrayList.add(item);
                break;
            case Constant.SCENE_SINGLE_CONFIG_FOR_SUNSET_SUNRISE:
                String sunsetSunriseSelected = "";
                if (conditionBean != null) {
                    SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                    sunsetSunriseSelected = sceneExprBean.getValue().toString();
                }

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_sunset_sunrise_for_sunset));
                item.setEnum(ConditionEnum.SUNSETRISE_SUNRISE);
                item.setSelect(sunsetSunriseSelected.contains(item.getEnum().getValue()));
                /// item.setValue(sunsetSunriseSelected.contains(item.getName()) ? sunsetSunriseSelected : "");
                arrayList.add(item);

                item = new SingleSelectBean();
                item.setName(getString(R.string.rino_scene_sunset_sunrise_for_sunrise));
                item.setEnum(ConditionEnum.SUNSETRISE_SUNSET);
                item.setSelect(sunsetSunriseSelected.contains(item.getEnum().getValue()));
                /// item.setValue(sunsetSunriseSelected.contains(item.getName()) ? sunsetSunriseSelected : "");
                arrayList.add(item);
                break;
            case Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE:
                hideToolBarRightText();
                binding.viewItem.setVisibility(View.GONE);

                String deviceSelected = "";
                String devId = getIntent().getStringExtra("device_id");
                String dpKey = getIntent().getStringExtra("dp_key");
                if (getIntent().getIntExtra("condition_or_task", -1) == Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION) {
                    if (conditionBean != null) {
                        SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                        deviceSelected = sceneExprBean.getValue().toString();
                        devId = conditionBean.getTargetId();
                        dpKey = sceneExprBean.getPropName();
                    }
                } else {
                    List<SceneActionBean> actionArrayList = (List<SceneActionBean>) getIntent().getSerializableExtra("action_array_list");
                    int position = getIntent().getIntExtra("position", -1);
                    SceneActionBean actionBean = actionArrayList.get(position);
                    devId = actionBean.getTargetId();
                    Map<String, Map<String, Object>> actionData = new Gson().fromJson(actionBean.getActionData(), new TypeToken<Map<String, Object>>(){}.getType());
                    Map<String, Object> props = actionData.get("props");
                    if (props != null) {
                        for (String mapKey: props.keySet()) {
                            dpKey = mapKey;
                            deviceSelected = String.valueOf(props.get(mapKey));
                        }
                    }
                }

                DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(devId);
                for (DeviceDpBean deviceDpBean: CacheDataManager.getInstance().getDeviceDpList(deviceInfo.getId())) {
                    DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);

                    if (deviceDpJson.getIdentifier().equals(dpKey)) {
                        setToolBarTitle(deviceDpJson.getName());
                        if (deviceDpJson.getDataType().getType().equals("enum")) {
                            List<String> enumList = (List<String>) deviceDpJson.getDataType().getSpecs().get("enums");
                            for (int i = 0; i < enumList.size(); i++) {
                                item = new SingleSelectBean();
                                item.setName(enumList.get(i));
                                item.setSelect(!TextUtils.isEmpty(deviceSelected) && deviceSelected.equals(item.getName()));
                                arrayList.add(item);
                            }
                        } else if (deviceDpJson.getDataType().getType().equals("bool")) {
                            for (Map.Entry<String, Object> entry : deviceDpJson.getDataType().getSpecs().entrySet()) {
                                item = new SingleSelectBean();
                                item.setName(entry.getValue().toString());
                                item.setSelect(!TextUtils.isEmpty(deviceSelected) && entry.getKey().equals(deviceSelected));
                                arrayList.add(item);
                            }
                        } else {
                            for (int i = 0; i < deviceDpJson.getDataType().getSpecs().size(); i++) {
                                item = new SingleSelectBean();
                                item.setName(String.valueOf(deviceDpJson.getDataType().getSpecs().get(String.valueOf(i))));
                                item.setSelect(!TextUtils.isEmpty(deviceSelected) && deviceSelected.equals(item.getName()));
                                arrayList.add(item);
                            }
                        }
                        break;
                    }
                }
                break;
            default:
                break;
        }
        initSingleChoice(arrayList);
    }

    private void initViewItem(String name, String value) {
        binding.tvItemName.setText(name);
        binding.tvItemValue.setText(value);

        binding.viewItem.setOnClickListener(v -> startActivityForResult(new Intent(getBaseContext(), CityActivity.class)
                .putExtra(Constant.CURRENT_CITY,binding.tvItemValue.getText().toString())
                , REQUEST_CODE_FROM_CITY_SELECT));
    }

    private void initSingleChoice(List<SingleSelectBean> array) {
        singleSelectAdapter = new SingleSelectAdapter(array);
        binding.recyclerView.setAdapter(singleSelectAdapter);
        binding.recyclerView.setVisibility(View.VISIBLE);

        singleSelectAdapter.setOnItemClickListener((adapter, view, position) -> {
//            if (Constant.SCENE_SINGLE_CONFIG_FOR_SUNSET_SUNRISE.equals(getIntent().getStringExtra(Constant.SCENE_SINGLE_CONFIG_TYPE))) {
//                List<String> array1 = new ArrayList<>();
//                String unitMinute = getString(R.string.rino_common_unit_minute);
//                String unitHour = getString(R.string.rino_common_unit_hour);
//                if (position == 0) {
//                    String sunsetBefore = getString(R.string.rino_scene_sunset_sunrise_for_sunset_value2);
//                    String sunsetAfter = getString(R.string.rino_scene_sunset_sunrise_for_sunset_value3);
//                    array1.add(String.format(sunsetBefore, "5" + unitHour));
//                    array1.add(String.format(sunsetBefore, "4" + unitHour));
//                    array1.add(String.format(sunsetBefore, "3" + unitHour));
//                    array1.add(String.format(sunsetBefore, "2" + unitHour));
//                    array1.add(String.format(sunsetBefore, "1" + unitHour));
//                    array1.add(String.format(sunsetBefore, "55" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "50" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "45" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "40" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "35" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "30" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "25" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "20" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "15" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "10" + unitMinute));
//                    array1.add(String.format(sunsetBefore, "5" + unitMinute));
//                    array1.add(getString(R.string.rino_scene_sunset_sunrise_for_sunset_value1));
//                    array1.add(String.format(sunsetAfter, "5" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "10" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "15" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "20" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "25" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "30" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "35" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "40" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "45" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "50" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "55" + unitMinute));
//                    array1.add(String.format(sunsetAfter, "1" + unitHour));
//                    array1.add(String.format(sunsetAfter, "2" + unitHour));
//                    array1.add(String.format(sunsetAfter, "3" + unitHour));
//                    array1.add(String.format(sunsetAfter, "4" + unitHour));
//                    array1.add(String.format(sunsetAfter, "5" + unitHour));
//                } else if (position == 1) {
//                    String sunriseBefore = getString(R.string.rino_scene_sunset_sunrise_for_sunrise_value2);
//                    String sunriseAfter = getString(R.string.rino_scene_sunset_sunrise_for_sunrise_value3);
//
//                    array1.add(String.format(sunriseBefore, "5" + unitHour));
//                    array1.add(String.format(sunriseBefore, "4" + unitHour));
//                    array1.add(String.format(sunriseBefore, "3" + unitHour));
//                    array1.add(String.format(sunriseBefore, "2" + unitHour));
//                    array1.add(String.format(sunriseBefore, "1" + unitHour));
//                    array1.add(String.format(sunriseBefore, "55" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "50" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "45" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "40" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "35" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "30" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "25" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "20" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "15" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "10" + unitMinute));
//                    array1.add(String.format(sunriseBefore, "5" + unitMinute));
//                    array1.add(getString(R.string.rino_scene_sunset_sunrise_for_sunrise_value1));
//                    array1.add(String.format(sunriseAfter, "5" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "10" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "15" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "20" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "25" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "30" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "35" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "40" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "45" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "50" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "55" + unitMinute));
//                    array1.add(String.format(sunriseAfter, "1" + unitHour));
//                    array1.add(String.format(sunriseAfter, "2" + unitHour));
//                    array1.add(String.format(sunriseAfter, "3" + unitHour));
//                    array1.add(String.format(sunriseAfter, "4" + unitHour));
//                    array1.add(String.format(sunriseAfter, "5" + unitHour));
//                }
//                //条件选择器
//                OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(SceneSingleConfigActivity.this, (options1, option2, options3, v) -> {
//                    List<SingleSelectBean> arrayList = singleSelectAdapter.getData();
//                    SingleSelectBean item;
//                    for (int i = 0; i < arrayList.size(); i++) {
//                        item = arrayList.get(i);
//                        item.setSelect(position == i);
//                        item.setValue(position == i ? array1.get(options1) : "");
//                    }
//                    singleSelectAdapter.notifyDataSetChanged();
//                })
//                        .setSubmitText(getString(R.string.rino_common_save))
//                        .setCancelText(getString(R.string.rino_common_cancel))
//                        .setCyclic(false, false, false)
//                        .setSelectOptions(((int) (array1.size() / 2)), 0, 0)
//                        .setLineSpacingMultiplier(3)
//                        .setItemVisibleCount(3)
//                        .build();
//                pvOptions.setPicker(array1);
//                pvOptions.show();
//            } else {
//            }
            if (Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE.equals(getIntent().getStringExtra(Constant.SCENE_SINGLE_CONFIG_TYPE))) {
                String devId = getIntent().getStringExtra("device_id");
                String dpKey = getIntent().getStringExtra("dp_key");

                if (getIntent().getIntExtra("condition_or_task", -1) == Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION) {
                    SceneConditionBean conditionBean1 = (SceneConditionBean) getIntent().getSerializableExtra("condition_bean");
                    if (conditionBean1 != null) {
                        SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean1.getExpr(), SceneExprBean.class);
                        devId = conditionBean1.getTargetId();
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
                            sceneExprBean.setExpression("==");
                            if (deviceDpJson.getDataType().getType().equals("bool")) {
                                String key = "";
                                for (String mapKey: deviceDpJson.getDataType().getSpecs().keySet()) {
                                    if (Objects.equals(deviceDpJson.getDataType().getSpecs().get(mapKey), singleSelectAdapter.getData().get(position).getName())) {
                                        key = mapKey;
                                        break;
                                    }
                                }
                                sceneExprBean.setValue("true".equals(key));
                            } else if (deviceDpJson.getDataType().getType().equals("enum")) {
                                sceneExprBean.setValue(singleSelectAdapter.getData().get(position).getName());
                            }
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
                            if ("bool".equals(deviceDpJson.getDataType().getType())) {
                                String key = "";
                                for (String mapKey: deviceDpJson.getDataType().getSpecs().keySet()) {
                                    if (Objects.equals(deviceDpJson.getDataType().getSpecs().get(mapKey), singleSelectAdapter.getData().get(position).getName())) {
                                        key = mapKey;
                                        break;
                                    }
                                }

                                props.put(deviceDpJson.getIdentifier(), "true".equals(key));
                            } else {
                                props.put(deviceDpJson.getIdentifier(), singleSelectAdapter.getData().get(position).getName());
                            }

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
                List<SingleSelectBean> arrayList = singleSelectAdapter.getData();
                SingleSelectBean item;
                for (int i = 0; i < arrayList.size(); i++) {
                    item = arrayList.get(i);
                    item.setSelect(position == i);
                }
                singleSelectAdapter.notifyDataSetChanged();
            }
        });
    }

    private int getSelectIndex() {
        int result = -1;
        for (int i = 0; i < singleSelectAdapter.getData().size(); i++) {
            if (singleSelectAdapter.getData().get(i).isSelect()) {
                result = i;
                break;
            }
        }

        return result;
    }

    @Override
    public ActivitySceneSingleConfigBinding getBinding(LayoutInflater inflater) {
        return ActivitySceneSingleConfigBinding.inflate(inflater);
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        if ((binding.tvItemValue.getTag() == null || TextUtils.isEmpty(binding.tvItemValue.getTag().toString())) && binding.viewItem.getVisibility() == View.VISIBLE) {
            ToastUtil.showMsg(R.string.rino_scene_please_select_city);
            return;
        }

        int selectIndex = getSelectIndex();
        if (selectIndex == -1) {
            ToastUtil.showMsg(R.string.rino_scene_select_condition_tip);
            return;
        }

        SceneConditionBean conditionBean = new SceneConditionBean();
        conditionBean.setCondType(Constant.SCENE_CONDITION_FOR_CHANGE_WEATHER);
        SceneExprBean sceneExprBean = new SceneExprBean();
        sceneExprBean.setPropName(getIntent().getStringExtra(Constant.SCENE_SINGLE_CONFIG_TYPE));
        sceneExprBean.setExpression("==");
        sceneExprBean.setValue(singleSelectAdapter.getData().get(selectIndex).getEnum().getValue());
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
