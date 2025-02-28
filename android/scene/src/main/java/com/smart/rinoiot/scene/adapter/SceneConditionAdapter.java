package com.smart.rinoiot.scene.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceDpJsonBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.SceneConditionBean;
import com.smart.rinoiot.common.bean.SceneExprBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.scene.ConditionEnum;
import com.smart.rinoiot.common.utils.DateUtils;
import com.smart.rinoiot.scene.R;

import java.util.List;

public class SceneConditionAdapter extends BaseQuickAdapter<SceneConditionBean, BaseViewHolder> {
    public SceneConditionAdapter(@Nullable List<SceneConditionBean> data) {
        super(R.layout.adapter_condition_or_task_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SceneConditionBean item) {
        switch (item.getCondType()) {
            case Constant.SCENE_CONDITION_FOR_ONE_KEY:
                helper.setImageResource(R.id.ivIcon, R.drawable.ic_scene_one_click_execution);
                helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_manual_title));
                helper.setGone(R.id.tvSubName, true);
                helper.setGone(R.id.ivArrow, true);
                break;
            case Constant.SCENE_CONDITION_FOR_CHANGE_WEATHER:
                helper.setGone(R.id.tvSubName, false);
                helper.setGone(R.id.ivArrow, false);
                SceneExprBean sceneExprBean = new GsonBuilder().disableHtmlEscaping().create().fromJson(item.getExpr(), SceneExprBean.class);

                helper.setText(R.id.tvSubName, item.getCityName());

                switch (sceneExprBean.getPropName()) {
                    case Constant.SCENE_SINGLE_CONFIG_FOR_HUMIDITY:
                        helper.setImageResource(R.id.ivIcon, R.drawable.ic_change_weather_humidity_big);
                        helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_weather_change_humidity) + ": " + ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_WEATHER:
                        helper.setImageResource(R.id.ivIcon, R.drawable.ic_change_weather_sun_status_big);
                        helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_weather_change_status) + ": " + ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_AIR_QUALITY:
                        helper.setImageResource(R.id.ivIcon, R.drawable.ic_change_weather_air_quality_big);
                        helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_weather_change_air_quality) + ": " + ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_PM25:
                        helper.setImageResource(R.id.ivIcon, R.drawable.ic_change_weather_pm25_big);
                        helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_weather_change_pm25) + ": " + ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SINGLE_CONFIG_FOR_SUNSET_SUNRISE:
                        helper.setImageResource(R.id.ivIcon, R.drawable.ic_change_weather_sun_rise_set_big);
                        helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_weather_change_sunrise_sunset) + ": " + ConditionEnum.getConditionEnumName(sceneExprBean.getValue().toString()));
                        break;
                    case Constant.SCENE_SEEK_CONFIG_FOR_TEMPERATURE:
                        helper.setImageResource(R.id.ivIcon, R.drawable.ic_change_weather_temp_big);
                        helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_weather_change_temperature) + ": " + getTextBySymbol(sceneExprBean.getExpression()) + sceneExprBean.getValue() + sceneExprBean.getUnit());
                        break;
                    case Constant.SCENE_SEEK_CONFIG_FOR_WIND_SPEED:
                        helper.setImageResource(R.id.ivIcon, R.drawable.ic_change_weather_wind_speed_big);
                        helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_weather_change_wind_speed) + ": " + getTextBySymbol(sceneExprBean.getExpression()) + sceneExprBean.getValue() + sceneExprBean.getUnit());
                        break;
                    default:
                        break;
                }
                break;
            case Constant.SCENE_CONDITION_FOR_TIMING:
                helper.setImageResource(R.id.ivIcon, R.drawable.ic_scene_timing);
                helper.setGone(R.id.tvSubName, false);
                helper.setGone(R.id.ivArrow, false);
                helper.setText(R.id.tvSubName, getWeekData(item.getLoops(), item.getExecuteDate()));
                helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_timing_title) + ": " + (item.isTimeIsAm() ? item.getTime() : DateUtils.get24Time(item.getTime())));
                break;
            case Constant.SCENE_CONDITION_FOR_DEVICE_STATUS_CHANGE:
                DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(item.getTargetId());
                List<DeviceDpBean> deviceDpList = CacheDataManager.getInstance().getDeviceDpList(item.getTargetId());
                if (deviceInfo != null) {
                    ImageLoader.getInstance().bindImageUrl(deviceInfo.getImageUrl(), helper.getView(R.id.ivIcon), R.drawable.ic_scene_device_change, R.drawable.ic_scene_device_change);
                    helper.setText(R.id.tvName, deviceInfo.getName());
                    helper.setGone(R.id.tvSubName, false);
                    helper.setGone(R.id.ivArrow, false);

                    SceneExprBean sceneExprBean1 = new GsonBuilder().disableHtmlEscaping().create().fromJson(item.getExpr(), SceneExprBean.class);
                    for (DeviceDpBean deviceDpBean: deviceDpList) {
                        DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);
                        if (deviceDpJson.getIdentifier().equals(sceneExprBean1.getPropName())) {
                            String value = sceneExprBean1.getValue().toString();
                            if (deviceDpJson.getDataType().getType().equals("bool")) {
                                value = String.valueOf(deviceDpJson.getDataType().getSpecs().get(value));
                            } else if (deviceDpJson.getDataType().getType().equals("int")) {
                                value = getTextBySymbol(sceneExprBean1.getExpression()) + sceneExprBean1.getValue() + sceneExprBean1.getUnit();
                            }
                            helper.setText(R.id.tvSubName, deviceDpJson.getName() + ": " + value);
                            break;
                        }
                    }
                } else {
                    helper.setImageResource(R.id.ivIcon, R.drawable.ic_default_circle_image);
                    helper.setText(R.id.tvName, getContext().getResources().getString(R.string.rino_scene_device_not_exist));
                    helper.setGone(R.id.tvSubName, true);
                }
                break;
            default:
                break;
        }
    }

    private String getTextBySymbol(String symbol) {
        String result = "";
        if (!TextUtils.isEmpty(symbol)) {
            if ("<".equals(symbol)) {
                result = getContext().getString(R.string.rino_scene_value_less_than);
            } else if ("==".equals(symbol)) {
                result = getContext().getString(R.string.rino_scene_value_equal);
            } else if (">".equals(symbol)) {
                result = getContext().getString(R.string.rino_scene_value_more_than);
            }
        }

        return result;
    }

    private String getWeekData(String value, String date) {
        String[] week = {getContext().getString(R.string.rino_scene_time_sunday), getContext().getString(R.string.rino_scene_time_monday), getContext().getString(R.string.rino_scene_time_tuesday),
                getContext().getString(R.string.rino_scene_time_wednesday), getContext().getString(R.string.rino_scene_time_thursday), getContext().getString(R.string.rino_scene_time_friday),
                getContext().getString(R.string.rino_scene_time_saturday)};
        StringBuilder result = new StringBuilder();
        if (!TextUtils.isEmpty(value)) {
            if ("1111111".equals(value)) {
                result.append(getContext().getString(R.string.rino_scene_time_every_day_title));
            } else if ("0000000".equals(value)) {
                result.append(DateUtils.getStringFromDate(DateUtils.getTimeFromString(date, "yyyy-MM-dd"), "MM月dd日"));
            } else {
                if (value.length() == 7) {
                    for (int i = 0; i < value.length(); i++) {
                        char temp = value.charAt(i);
                        if ("1".equals(String.valueOf(temp))) {
                            if (result.length() > 0) {
                                result.append(",");
                            }
                            result.append(week[i]);
                        }
                    }
                }
            }
        }
        return result.toString();
    }
}
