package com.smart.rinoiot.scene.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceDpJsonBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.SceneActionBean;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.bean.SceneConditionBean;
import com.smart.rinoiot.common.bean.SceneExprBean;
import com.smart.rinoiot.common.bean.SceneTriggerBean;
import com.smart.rinoiot.common.bean.SelectItemBean;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.SceneManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.scene.ConditionEnum;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.DateUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.view.SelectItemBottomPopView;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.activity.SceneDelayActivity;
import com.smart.rinoiot.scene.activity.SceneMultipleConfigActivity;
import com.smart.rinoiot.scene.activity.SceneSeekConfigActivity;
import com.smart.rinoiot.scene.activity.SceneSingleConfigActivity;
import com.smart.rinoiot.scene.activity.SceneTimingActivity;
import com.smart.rinoiot.scene.manager.SceneNetworkManager;
import com.smart.rinoiot.scene.view.DoubleTimeWheelBottomPopView;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SceneConfigViewModel extends SceneViewModel {

    /**
     * 删除场景回调
     */
    private final MutableLiveData<Boolean> deleteSceneLiveData = new MutableLiveData<>();
    /**
     * 创建以及编辑场景回调
     */
    private final MutableLiveData<SceneBean> editSceneLiveData = new MutableLiveData<>();
    /**
     * 启用-禁用场景回调
     */
    private final MutableLiveData<Boolean> enableSceneLiveData = new MutableLiveData<>();
    /**
     * 定位城市回调
     */
    private final MutableLiveData<CityBean> cityLiveData = new MutableLiveData<>();

    public MutableLiveData<CityBean> getCityLiveData() {
        return cityLiveData;
    }

    public MutableLiveData<Boolean> getDeleteSceneLiveData() {
        return deleteSceneLiveData;
    }

    public MutableLiveData<SceneBean> getEditSceneLiveData() {
        return editSceneLiveData;
    }

    public MutableLiveData<Boolean> getEnableSceneLiveData() {
        return enableSceneLiveData;
    }

    public SceneConfigViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public String getSceneTitle(SceneBean sceneBean, SceneConditionBean conditionBean) {
        StringBuilder result = new StringBuilder();
        switch (conditionBean.getCondType()) {
            case Constant.SCENE_CONDITION_FOR_ONE_KEY:
                sceneBean.setSceneType(Constant.SCENE_TYPE_FOR_MANUAL);
                result.append(getString(R.string.rino_scene_manual_title_new));
                break;
            case Constant.SCENE_CONDITION_FOR_CHANGE_WEATHER:
                sceneBean.setSceneType(Constant.SCENE_TYPE_FOR_AUTO);
                SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                result.append(getString(R.string.rino_scene_if));
                result.append(" ");
                result.append(conditionBean.getCityName());
                result.append(" ");

                switch (sceneExprBean.getPropName()) {
                    case Constant.SCENE_SINGLE_CONFIG_FOR_HUMIDITY:
                        result.append(getString(R.string.rino_scene_weather_change_humidity));
                        result.append(":");
                        result.append(ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_WEATHER:
                        result.append(getString(R.string.rino_scene_weather_change_status));
                        result.append(":");
                        result.append(ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_AIR_QUALITY:
                        result.append(getString(R.string.rino_scene_weather_change_air_quality));
                        result.append(":");
                        result.append(ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_PM25:
                        result.append(getString(R.string.rino_scene_weather_change_pm25));
                        result.append(":");
                        result.append(ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_SUNSET_SUNRISE:
                        result.append(getString(R.string.rino_scene_weather_change_sunrise_sunset));
                        result.append(":");
                        result.append(ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SEEK_CONFIG_FOR_TEMPERATURE:
                        result.append(getString(R.string.rino_scene_weather_change_temperature));
                        result.append(":");
                        result.append(getTextBySymbol(sceneExprBean.getExpression()));
                        result.append(sceneExprBean.getValue());
                        result.append(sceneExprBean.getUnit());
                        break;
                    case Constant.SCENE_SEEK_CONFIG_FOR_WIND_SPEED:
                        result.append(getString(R.string.rino_scene_weather_change_wind_speed));
                        result.append(":");
                        result.append(getTextBySymbol(sceneExprBean.getExpression()));
                        result.append(sceneExprBean.getValue());
                        result.append(sceneExprBean.getUnit());
                        break;
                    default:
                        break;
                }
                break;
            case Constant.SCENE_CONDITION_FOR_TIMING:
                sceneBean.setSceneType(Constant.SCENE_TYPE_FOR_AUTO);
                result.append(getString(R.string.rino_scene_if));
                result.append(" ");
                result.append(getString(R.string.rino_scene_timing_title));
                result.append(":");
                result.append(conditionBean.isTimeIsAm() ? conditionBean.getTime() : DateUtils.get24Time(conditionBean.getTime()));
                result.append(" ");
                String repeatStr = getWeekData(conditionBean.getLoops());
                if (getString(R.string.rino_scene_time_one_time_only).equals(repeatStr)) {
                    repeatStr = DateUtils.getStringFromDate(DateUtils.getTimeFromString(conditionBean.getExecuteDate(), "yyyy-MM-dd"), "MM月dd日");
                }
                result.append(repeatStr);
                break;
            case Constant.SCENE_CONDITION_FOR_DEVICE_STATUS_CHANGE:
                sceneBean.setSceneType(Constant.SCENE_TYPE_FOR_AUTO);

                DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(conditionBean.getTargetId());

                SceneExprBean sceneExprBean1 = new Gson().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                for (DeviceDpBean deviceDpBean : CacheDataManager.getInstance().getDeviceDpList(deviceInfo.getId())) {
                    DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);
                    if (deviceDpJson.getIdentifier().equals(sceneExprBean1.getPropName())) {

                        String value = sceneExprBean1.getValue().toString();
                        if (deviceDpJson.getDataType().getType().equals("bool")) {
                            value = String.valueOf(deviceDpJson.getDataType().getSpecs().get(value));
                        } else if (deviceDpJson.getDataType().getType().equals("int")) {
                            String unit = deviceDpJson.getDataType().getSpecs().get("unitName").toString();
                            value = value + unit;
                        }
                        result.append(getString(R.string.rino_scene_if));
                        result.append(" ");
                        result.append(deviceInfo.getName());
                        result.append(" ");
                        result.append(deviceDpJson.getName());
                        result.append(":");
                        result.append(value);
                        break;
                    }
                }
                break;
            default:
                break;
        }
        return result.toString();
    }

    public void createBottomDialog(Context context, CharSequence title, List<SelectItemBean> dataArray, SelectItemBottomPopView.OnSelectListener selectListener) {
        SelectItemBottomPopView popupView = new SelectItemBottomPopView(context);
        popupView.setData(title, dataArray);
        popupView.setOnSelectListener(selectListener);

        new XPopup.Builder(context)
                .isDarkTheme(false)
                .hasShadowBg(true)
                //对于只使用一次的弹窗，推荐设置这个
                .isDestroyOnDismiss(true)
                .asCustom(popupView)
                .show();
    }

    public void showDoubleTimeWheelDialog(Context context, String startTime, String endTime, DoubleTimeWheelBottomPopView.OnConfirmListener confirmListener) {
        DoubleTimeWheelBottomPopView popupView = new DoubleTimeWheelBottomPopView(context);
        popupView.setEffectivePeriod(startTime, endTime);
        popupView.setOnConfirmListener(confirmListener);

        new XPopup.Builder(context)
                .isDarkTheme(false)
                .hasShadowBg(true)
                 //对于只使用一次的弹窗，推荐设置这个
                .isDestroyOnDismiss(true)
                .asCustom(popupView)
                .show();
    }

    public void editCondition(Activity activity, SceneConditionBean conditionBean, int position, int requestCode) {
        switch (conditionBean.getCondType()) {
            case Constant.SCENE_CONDITION_FOR_ONE_KEY:
                return;
            case Constant.SCENE_CONDITION_FOR_CHANGE_WEATHER:
                SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                switch (sceneExprBean.getPropName()) {
                    case Constant.SCENE_SINGLE_CONFIG_FOR_HUMIDITY:
                        activity.startActivityForResult(new Intent(activity, SceneSingleConfigActivity.class)
                                .putExtra("position", position)
                                .putExtra("condition_bean", conditionBean)
                                .putExtra("isNeedResult", true)
                                .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_weather_change_humidity))
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, sceneExprBean.getPropName()), requestCode);
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_WEATHER:
                        activity.startActivityForResult(new Intent(activity, SceneSingleConfigActivity.class)
                                .putExtra("position", position)
                                .putExtra("condition_bean", conditionBean)
                                .putExtra("isNeedResult", true)
                                .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_weather_change_status))
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, sceneExprBean.getPropName()), requestCode);
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_AIR_QUALITY:
                        activity.startActivityForResult(new Intent(activity, SceneSingleConfigActivity.class)
                                .putExtra("position", position)
                                .putExtra("condition_bean", conditionBean)
                                .putExtra("isNeedResult", true)
                                .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_weather_change_air_quality))
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, sceneExprBean.getPropName()), requestCode);
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_PM25:
                        activity.startActivityForResult(new Intent(activity, SceneSingleConfigActivity.class)
                                .putExtra("position", position)
                                .putExtra("condition_bean", conditionBean)
                                .putExtra("isNeedResult", true)
                                .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_weather_change_pm25))
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, sceneExprBean.getPropName()), requestCode);
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_SUNSET_SUNRISE:
                        activity.startActivityForResult(new Intent(activity, SceneSingleConfigActivity.class)
                                .putExtra("position", position)
                                .putExtra("condition_bean", conditionBean)
                                .putExtra("isNeedResult", true)
                                .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_weather_change_sunrise_sunset))
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, sceneExprBean.getPropName()), requestCode);
                        break;
                    case Constant.SCENE_SEEK_CONFIG_FOR_TEMPERATURE:
                        activity.startActivityForResult(new Intent(activity, SceneSeekConfigActivity.class)
                                .putExtra("position", position)
                                .putExtra("condition_bean", conditionBean)
                                .putExtra("isNeedResult", true)
                                .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_weather_change_temperature))
                                .putExtra(Constant.SCENE_SEEK_CONFIG_TYPE, sceneExprBean.getPropName()), requestCode);
                        break;
                    case Constant.SCENE_SEEK_CONFIG_FOR_WIND_SPEED:
                        activity.startActivityForResult(new Intent(activity, SceneSeekConfigActivity.class)
                                .putExtra("position", position)
                                .putExtra("condition_bean", conditionBean)
                                .putExtra("isNeedResult", true)
                                .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_weather_change_wind_speed))
                                .putExtra(Constant.SCENE_SEEK_CONFIG_TYPE, sceneExprBean.getPropName()), requestCode);
                        break;
                    default:
                        break;
                }
                break;
            case Constant.SCENE_CONDITION_FOR_TIMING:
                activity.startActivityForResult(new Intent(activity, SceneTimingActivity.class)
                        .putExtra("position", position)
                        .putExtra("condition_bean", conditionBean)
                        .putExtra("isNeedResult", true), requestCode);
                break;
            case Constant.SCENE_CONDITION_FOR_DEVICE_STATUS_CHANGE:
                DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(conditionBean.getTargetId());
                if (deviceInfo == null) {
                    return;
                }

                List<DeviceDpBean> deviceDpList = CacheDataManager.getInstance().getDeviceDpList(conditionBean.getTargetId());
                SceneExprBean sceneExprBean1 = new GsonBuilder().disableHtmlEscaping().create().fromJson(conditionBean.getExpr(), SceneExprBean.class);
                for (DeviceDpBean deviceDpBean: deviceDpList) {
                    DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);
                    if (deviceDpJson.getIdentifier().equals(sceneExprBean1.getPropName())) {
                        String value = sceneExprBean1.getValue().toString();
                        if (deviceDpJson.getDataType().getType().equals("int")) {
                            activity.startActivityForResult(new Intent(activity, SceneSeekConfigActivity.class)
                                    .putExtra("value", value)
                                    .putExtra("device_id", conditionBean.getTargetId())
                                    .putExtra("dp_key", sceneExprBean1.getPropName())
                                    .putExtra("position", position)
                                    .putExtra("condition_bean", conditionBean)
                                    .putExtra("isNeedResult", true)
                                    .putExtra("condition_or_task", Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION)
                                    .putExtra(Constant.SCENE_SEEK_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE), requestCode);
                            return;
                        }
                    }
                }

                activity.startActivityForResult(new Intent(activity, SceneSingleConfigActivity.class)
                        .putExtra("position", position)
                        .putExtra("condition_bean", conditionBean)
                        .putExtra("isNeedResult", true)
                        .putExtra("condition_or_task", Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION)
                        .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE), requestCode);
                break;
            default:
                break;
        }
    }

    public void editTask(Activity activity, SceneActionBean actionBean, List<SceneActionBean> actionBeanList, int position, int requestCode) {
        switch (actionBean.getActionType()) {
            case Constant.SCENE_TASK_FOR_OPERATE_DEVICE:
                DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(actionBean.getTargetId());
                if (deviceInfo == null) {
                    return;
                }

                List<DeviceDpBean> deviceDpList = CacheDataManager.getInstance().getDeviceDpList(actionBean.getTargetId());

                Map<String, Map<String, Object>> actionData = new Gson().fromJson(actionBean.getActionData(), new TypeToken<Map<String, Object>>(){}.getType());
                Map<String, Object> props = actionData.get("props");
                for (DeviceDpBean deviceDpBean: deviceDpList) {
                    DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);
                    if (props != null && props.containsKey(deviceDpJson.getIdentifier())) {
                        String value = String.valueOf(props.get(deviceDpJson.getIdentifier()));
                        if (deviceDpJson.getDataType().getType().equals("int")) {
                            activity.startActivityForResult(new Intent(activity, SceneSeekConfigActivity.class)
                                    .putExtra("value", value)
                                    .putExtra("device_id", actionBean.getTargetId())
                                    .putExtra("dp_key", deviceDpJson.getIdentifier())
                                    .putExtra("position", position)
                                    .putExtra("action_array_list", (Serializable) actionBeanList)
                                    .putExtra("isNeedResult", true)
                                    .putExtra("condition_or_task", Constant.SCENE_CHILDREN_TYPE_FOR_TASK)
                                    .putExtra(Constant.SCENE_SEEK_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE), requestCode);
                            return;
                        }
                    }
                }

                activity.startActivityForResult(new Intent(activity, SceneSingleConfigActivity.class)
                        .putExtra("position", position)
                        .putExtra("action_array_list", (Serializable) actionBeanList)
                        .putExtra("isNeedResult", true)
                        .putExtra("condition_or_task", Constant.SCENE_CHILDREN_TYPE_FOR_TASK)
                        .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE), requestCode);
                return;
            case Constant.SCENE_TASK_FOR_ALREADY_SMART:
                activity.startActivityForResult(new Intent(activity, SceneMultipleConfigActivity.class)
                        .putExtra("isNeedResult", true)
                        .putExtra("action_array_list", (Serializable) actionBeanList)
                        .putExtra(Constant.ACTIVITY_TITLE, getString(TextUtils.isEmpty(actionBean.getActionData()) ? R.string.rino_scene_task_manual_list_title : R.string.rino_scene_task_auto_list_title))
                        .putExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE, TextUtils.isEmpty(actionBean.getActionData()) ? Constant.SCENE_MULTIPLE_CONFIG_FOR_MANUAL_SCENE : Constant.SCENE_MULTIPLE_CONFIG_FOR_AUTO_SCENE), requestCode);
                break;
            case Constant.SCENE_TASK_FOR_NOTIFY:
                activity.startActivityForResult(new Intent(activity, SceneMultipleConfigActivity.class)
                        .putExtra("isNeedResult", true)
                        .putExtra("action_array_list", (Serializable) actionBeanList)
                        .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_task_message_way))
                        .putExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE, Constant.SCENE_MULTIPLE_CONFIG_FOR_NOTIFY), requestCode);
                break;
            case Constant.SCENE_TASK_FOR_DELAYED:
                activity.startActivityForResult(new Intent(activity, SceneDelayActivity.class)
                        .putExtra("position", position)
                        .putExtra("task_bean", actionBean)
                        .putExtra("isNeedResult", true), requestCode);
                break;
            default:
                break;
        }
    }

    public SceneBean getSceneBean() {
        SceneBean currentSceneBean = SceneManager.getInstance().getCurrentEditSceneBean();
        if (currentSceneBean == null) {
            currentSceneBean = new SceneBean();
            SceneTriggerBean sceneTriggerBean = new SceneTriggerBean();
            String tz = UserInfoManager.getInstance().getUserInfo(BaseApplication.getApplication()).tz;
            if (TextUtils.isEmpty(tz)) {
                tz = AppUtil.getSystemTimeZone();
            }
            sceneTriggerBean.setTz(tz);
            currentSceneBean.getRuleMetaData().getTriggers().add(sceneTriggerBean);
        }
        return currentSceneBean;
    }

    public int getTimeInterval(String target) {
        if (getString(R.string.rino_scene_effective_period_allday).equals(target)) {
            return Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_ALL_DAY;
        } else if (getString(R.string.rino_scene_effective_period_daytime).equals(target)) {
            return Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_DAY;
        } else if (getString(R.string.rino_scene_effective_period_atnight).equals(target)) {
            return Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_NIGHT;
        } else if (getString(R.string.rino_scene_effective_period_custom).equals(target)) {
            return Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_CUSTOM;
        }
        return Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_ALL_DAY;
    }

    public void setEffectivePeriod(SceneTriggerBean sceneTriggerBean, String startTime, String endTime) {
        sceneTriggerBean.setStartIsAm(!DateUtils.is24Time(startTime));
        sceneTriggerBean.setStart(DateUtils.get12Time(startTime));
        sceneTriggerBean.setEndIsAm(!DateUtils.is24Time(endTime));
        sceneTriggerBean.setEnd(DateUtils.get12Time(endTime));
    }

    public String getEffectivePeriodStartTime(SceneTriggerBean sceneTriggerBean) {
        if (TextUtils.isEmpty(sceneTriggerBean.getStart())) {
            return "00:00";
        }
        return sceneTriggerBean.isStartIsAm() ? sceneTriggerBean.getStart() : DateUtils.get24Time(sceneTriggerBean.getStart());
    }

    public String getEffectivePeriodEndTime(SceneTriggerBean sceneTriggerBean) {
        if (TextUtils.isEmpty(sceneTriggerBean.getEnd())) {
            return "23:59";
        }
        return sceneTriggerBean.isEndIsAm() ? sceneTriggerBean.getEnd() : DateUtils.get24Time(sceneTriggerBean.getEnd());
    }

    public String getWeekData(String value) {
        String[] week = {getString(R.string.rino_scene_time_sunday), getString(R.string.rino_scene_time_monday), getString(R.string.rino_scene_time_tuesday),
                getString(R.string.rino_scene_time_wednesday), getString(R.string.rino_scene_time_thursday), getString(R.string.rino_scene_time_friday),
                getString(R.string.rino_scene_time_saturday)};
        StringBuilder result = new StringBuilder();
        if (!TextUtils.isEmpty(value)) {
            if ("1111111".equals(value)) {
                result.append(getString(R.string.rino_scene_time_every_day_title));
            } else if ("0000000".equals(value)) {
                result.append(getString(R.string.rino_scene_time_one_time_only));
            } else {
                if (value.length() == 7) {
                    for (int i = 0; i < value.length(); i++) {
                        char temp = value.charAt(i);
                        if ("1".equals(String.valueOf(temp))) {
                            if (result.length() > 0) result.append(",");
                            result.append(week[i]);
                        }
                    }
                }
            }
        }
        return result.toString();
    }

    private String getTextBySymbol(String symbol) {
        String result = "";
        if (!TextUtils.isEmpty(symbol)) {
            if ("<".equals(symbol)) {
                result = getString(R.string.rino_scene_value_less_than);
            } else if ("==".equals(symbol)) {
                result = getString(R.string.rino_scene_value_equal);
            } else if (">".equals(symbol)) {
                result = getString(R.string.rino_scene_value_more_than);
            }
        }

        return result;
    }

    public void createScene(SceneBean sceneBean) {
        SceneNetworkManager.getInstance().createScene(sceneBean, new CallbackListener<SceneBean>() {
            @Override
            public void onSuccess(SceneBean data) {
                editSceneLiveData.postValue(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                editSceneLiveData.postValue(null);
            }
        });
    }

    public void updateScene(SceneBean sceneBean) {
        SceneNetworkManager.getInstance().updateScene(sceneBean, new CallbackListener<SceneBean>() {
            @Override
            public void onSuccess(SceneBean data) {
                editSceneLiveData.postValue(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                editSceneLiveData.postValue(null);
            }
        });
    }

    public void deleteScene(String sceneId) {
        SceneNetworkManager.getInstance().deleteSceneById(sceneId, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                deleteSceneLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                deleteSceneLiveData.postValue(false);
            }
        });
    }

    public void changeSceneStatus(String sceneId, int enable) {
        SceneNetworkManager.getInstance().changeSceneStatus(sceneId, enable, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                enableSceneLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                enableSceneLiveData.postValue(false);
            }
        });
    }

    public void toLocationCity(Context context) {
        try {
            AMapLocationClient mLocationClient = new AMapLocationClient(context);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setGeoLanguage(AppUtil.changeMapLanguage(context));
            mLocationOption.setNeedAddress(true);
            mLocationOption.setOnceLocation(true);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(aMapLocation -> {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        String locationCity = aMapLocation.getCity();
                        List<CityBean> cityList = CacheDataManager.getInstance().getCityList();

                        CityBean matchCity = null;
                        for (CityBean cityBean : cityList) {
                            if (cityBean.getName().toLowerCase().contains(locationCity.toLowerCase())) {
                                matchCity = cityBean;
                                break;
                            }
                        }
                        if (matchCity != null) {
                            cityLiveData.postValue(matchCity);
                            CacheDataManager.getInstance().cacheCityInfo(matchCity);
                        }
                    } else {
                        /// cityLiveData.postValue(null);
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        LgUtils.e("AMapError --> location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            });
            mLocationClient.startLocation();
        } catch (Exception e) {
            /// cityLiveData.postValue(null);
            e.printStackTrace();
        }
    }
}
