package com.smart.rinoiot.voice.automation;

import android.util.Log;

import com.dsh.openai.home.model.ControlIntent;
import com.dsh.openai.home.model.automation.AirQuality;
import com.dsh.openai.home.model.automation.AutomationInfo;
import com.dsh.openai.home.model.automation.Condition;
import com.dsh.openai.home.model.automation.DeviceStatusChangeValue;
import com.dsh.openai.home.model.automation.Humidity;
import com.dsh.openai.home.model.automation.MatchType;
import com.dsh.openai.home.model.automation.PropertyName;
import com.dsh.openai.home.model.automation.Task;
import com.dsh.openai.home.model.automation.WeatherCondition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.SceneActionBean;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.bean.SceneConditionBean;
import com.smart.rinoiot.common.bean.SceneExprBean;
import com.smart.rinoiot.common.bean.SceneRuleBean;
import com.smart.rinoiot.common.bean.SceneTriggerBean;
import com.smart.rinoiot.common.device.DeviceCmdConverterUtils;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.scene.ConditionEnum;
import com.smart.rinoiot.common.utils.DateUtils;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.voice.devices.DeviceDataUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author edwin
 */
public class AutomationDataUtils {

    private static final String TAG = "AutomationDataUtils";

    /**
     * Infers the condition type from property name
     * @param propertyName the property name
     * @return the condition type
     */
    private static int inferConditionType(String propertyName){
        if(
            PropertyName.getWeather().getName().contentEquals(propertyName) ||
            PropertyName.getHumidity().getName().contentEquals(propertyName) ||
            PropertyName.getWindSpeed().getName().contentEquals(propertyName) ||
            PropertyName.getAirQuality().getName().contentEquals(propertyName) ||
            PropertyName.getTemperature().getName().contentEquals(propertyName) ||
            PropertyName.getSunsetSunrise().getName().contentEquals(propertyName)
        ){
            return Constant.SCENE_CONDITION_FOR_CHANGE_WEATHER;
        }else if(PropertyName.getSchedule().getName().contentEquals(propertyName)){
            return Constant.SCENE_CONDITION_FOR_TIMING;
        }else if(PropertyName.getDeviceStatusChange().getName().contentEquals(propertyName)) {
            return Constant.SCENE_CONDITION_FOR_DEVICE_STATUS_CHANGE;
        } else {
            return Constant.SCENE_CONDITION_FOR_ONE_KEY;
        }
    }

    /**
     * Convert property name to supported name values
     * @param condition the condition
     * @return the supported property name
     */
    private static String convertPropertyName(Condition condition) {
        String propertyName = condition.getPropertyName();
        if(PropertyName.getWeather().getName().contentEquals(propertyName)){
            return Constant.SCENE_SINGLE_CONFIG_FOR_WEATHER;
        }else if(PropertyName.getHumidity().getName().contentEquals(propertyName)) {
            return Constant.SCENE_SINGLE_CONFIG_FOR_HUMIDITY;
        }else if(PropertyName.getWindSpeed().getName().contentEquals(propertyName)) {
            return Constant.SCENE_SEEK_CONFIG_FOR_WIND_SPEED;
        }else if(PropertyName.getAirQuality().getName().contentEquals(propertyName)){
            return Constant.SCENE_SINGLE_CONFIG_FOR_AIR_QUALITY;
        }else if(PropertyName.getTemperature().getName().contentEquals(propertyName)){
            return Constant.SCENE_SEEK_CONFIG_FOR_TEMPERATURE;
        }else if(PropertyName.getSunsetSunrise().getName().contentEquals(propertyName)) {
            return Constant.SCENE_SINGLE_CONFIG_FOR_SUNSET_SUNRISE;
        }else if(PropertyName.getSchedule().getName().contentEquals(propertyName)){
            return PropertyName.getSchedule().getName();
        }else if(PropertyName.getDeviceStatusChange().getName().contentEquals(propertyName)) {
            return toDeviceStatusPropertyName(condition.getValue());
        }
        throw new RuntimeException("Unsupported property name " + propertyName);
    }

    /**
     * Handles the conversion of the device status change property name
     * @param value the value
     * @return the converted value
     */
    private static String toDeviceStatusPropertyName(String value){
        Type type = new TypeToken<DeviceStatusChangeValue>(){}.getType();
        DeviceStatusChangeValue statusValue = new Gson().fromJson(value, type);
        Map<String, Object> commands = buildSceneCommands(
                statusValue.getControlIntent(),
                statusValue.getDeviceId(),
                statusValue.getTargetValue()
        );
        return String.valueOf(commands.keySet().toArray()[0]);
    }

    /**
     * Converts condition expression to scene expr bean
     * @param condition condition data
     * @return the scene expr bean
     */
    private static String toSceneExprBean(Condition condition) {
        SceneExprBean sceneExprBean = new SceneExprBean();
        String name = convertPropertyName(condition);
        sceneExprBean.setPropName(name);
        sceneExprBean.setExpression(condition.getExpression());
        if(
            Constant.SCENE_SEEK_CONFIG_FOR_TEMPERATURE.contentEquals(name) ||
            Constant.SCENE_SEEK_CONFIG_FOR_WIND_SPEED.contentEquals(name)
        ){
            sceneExprBean.setUnit(condition.getUnits());
        }
        sceneExprBean.setValue(convertValue(condition.getPropertyName(), condition.getValue()));
        return new GsonBuilder().disableHtmlEscaping().create().toJson(sceneExprBean);
    }

    /**
     * Converts the weather condition values
     * @param value the weather condition value
     * @return the supported weather condition
     */
    private static String toWeatherEnum(String value) {
        if(WeatherCondition.Sunny.getValue().contentEquals(value)){
            return ConditionEnum.WEATHER_SUNNY.getValue();
        }else if(WeatherCondition.Hazy.getValue().contentEquals(value)) {
            return ConditionEnum.WEATHER_POLLUTED.getValue();
        }else if(WeatherCondition.Rainy.getValue().contentEquals(value)) {
            return ConditionEnum.WEATHER_RAINY.getValue();
        }else if(WeatherCondition.Snowy.getValue().contentEquals(value)) {
            return ConditionEnum.WEATHER_SNOWY.getValue();
        }else if(WeatherCondition.Cloudy.getValue().contentEquals(value)) {
            return ConditionEnum.WEATHER_CLOUDY.getValue();
        }
        throw new RuntimeException("Unsupported weather value | " + value);
    }

    /**
     * Converts the humidity values
     * @param value the humidity value
     * @return the supported value
     */
    private static String toHumidityEnum(String value) {
        if(Humidity.High.getValue().contentEquals(value)) {
            return ConditionEnum.HUMIDITY_WET.getValue();
        }else if(Humidity.Medium.getValue().contentEquals(value)){
            return ConditionEnum.HUMIDITY_COMFORT.getValue();
        }else if(Humidity.Low.getValue().contentEquals(value)){
            return  ConditionEnum.HUMIDITY_DRY.getValue();
        }
        throw new RuntimeException("Unsupported humidity value | " + value);
    }

    /**
     * Converts the air quality values
     * @param value the air quality value
     * @return the supported value
     */
    private static String toAirQualityEnum(String value) {
        if(AirQuality.Excellent.getValue().contentEquals(value)) {
            return ConditionEnum.GOOD.getValue();
        }else if(AirQuality.Fair.getValue().contentEquals(value)) {
            return ConditionEnum.FINE.getValue();
        }else if(AirQuality.Poor.getValue().contentEquals(value)){
            return ConditionEnum.POLLUTED.getValue();
        }
        throw new RuntimeException("Unsupported air-quality value | " + value);
    }

    /**
     * Converts the values to supported range, types and format
     *
     * @param propertyName the name of the property
     * @param value the value
     * @return the converted value
     */
    private static Object convertValue(String propertyName, String value) {
        Object conditionValue;
        if(PropertyName.getWeather().getName().contentEquals(propertyName)){
            conditionValue = toWeatherEnum(value);
        }else if(PropertyName.getHumidity().getName().contentEquals(propertyName)) {
            conditionValue = toHumidityEnum(value);
        }else if(
            PropertyName.getWindSpeed().getName().contentEquals(propertyName) ||
            PropertyName.getTemperature().getName().contentEquals(propertyName)
        ) {
            conditionValue = (int) Double.parseDouble(value);
        }else if(PropertyName.getAirQuality().getName().contentEquals(propertyName)){
            conditionValue = toAirQualityEnum(value);
        }else if(PropertyName.getDeviceStatusChange().getName().contentEquals(propertyName)){
            conditionValue = toDeviceStatusValue(value);
        }else{
            throw new RuntimeException("Unsupported property name");
        }
        return conditionValue;
    }

    /**
     * Converts the device status values
     * @param value the value
     * @return converted value
     */
    private static Object toDeviceStatusValue(String value) {
        Type type = new TypeToken<DeviceStatusChangeValue>(){}.getType();
        DeviceStatusChangeValue statusValue = new Gson().fromJson(value, type);
        Map<String, Object> commands = buildSceneCommands(
            statusValue.getControlIntent(),
            statusValue.getDeviceId(),
            statusValue.getTargetValue()
        );
        return commands.values().toArray()[0];
    }

    /**
     * Builds a device status scene condition bean
     * @param condition the scene condition
     * @param loops the loops
     * @return the scene bean condition
     */
    private static SceneConditionBean toDeviceStatusConditionBean(
        Condition condition, String loops
    ) {
        Log.d(TAG, "toDeviceStatusConditionBean: loops=" + loops);
        Log.d(TAG, "toDeviceStatusConditionBean: condition=" + new Gson().toJson(condition));
        Type type = new TypeToken<DeviceStatusChangeValue>(){}.getType();
        DeviceStatusChangeValue statusValue = new Gson().fromJson(condition.getValue(), type);
        SceneConditionBean sceneConditionBean = new SceneConditionBean();
        sceneConditionBean.setCondType(Constant.SCENE_CONDITION_FOR_DEVICE_STATUS_CHANGE);
        String sceneExpr = toSceneExprBean(condition);
        Log.d(TAG, "toDeviceStatusConditionBean: " + sceneExpr);
        sceneConditionBean.setExpr(sceneExpr);
        sceneConditionBean.setLoops(loops);
        sceneConditionBean.setCondMode(1);
        sceneConditionBean.setTargetId(statusValue.getDeviceId());
        Log.d(TAG, "toDeviceStatusConditionBean: condition=" + new Gson().toJson(sceneConditionBean));
        return sceneConditionBean;
    }

    /**
     * Builds a weather scene condition bean
     * @param condition the scene condition
     * @param loops the loops
     * @param timezoneId the timezone identifier
     * @return the scene bean condition
     */
    private static SceneConditionBean toWeatherConditionBean(
            Condition condition, String loops, String timezoneId
    ) {
        CityBean cityBean = CacheDataManager.getInstance().getCityInfo();
        if(cityBean == null){
            cityBean = new CityBean();
            cityBean.setName("");
            cityBean.setCityCode("");
        }
        SceneConditionBean sceneConditionBean = new SceneConditionBean();
        sceneConditionBean.setCityName(cityBean.getName());
        sceneConditionBean.setCityCode(cityBean.getCityCode());
        String sceneExpr = toSceneExprBean(condition);

        sceneConditionBean.setExpr(sceneExpr);
        sceneConditionBean.setLoops(loops);
        sceneConditionBean.setTz(timezoneId);
        sceneConditionBean.setCondMode(1);
        sceneConditionBean.setCondType(Constant.SCENE_CONDITION_FOR_CHANGE_WEATHER);
        return sceneConditionBean;
    }

    /**
     * Builds a schedule scene condition bean
     * @param condition the scene condition
     * @param loops the loops
     * @param timezoneId the timezone identifier
     * @return the scene bean condition
     */
    private static SceneConditionBean toScheduleConditionBean(
            Condition condition, String loops, String timezoneId
    ) {
        SceneConditionBean sceneConditionBean = new SceneConditionBean();
        sceneConditionBean.setLoops(loops);
        sceneConditionBean.setTz(timezoneId);
        sceneConditionBean.setCondMode(1);
        sceneConditionBean.setCondType(Constant.SCENE_CONDITION_FOR_TIMING);
        String executeDate = DateUtils.getStringFromDate(
                System.currentTimeMillis(), "yyyy-MM-dd"
        );
        sceneConditionBean.setExecuteDate(executeDate);
        sceneConditionBean.setTimeIsAm(!DateUtils.is24Time(condition.getValue()));
        sceneConditionBean.setTime(DateUtils.get12Time(condition.getValue()));
        return sceneConditionBean;
    }

    /**
     * Builds scene condition beans
     * @param loops the loops
     * @param timezoneId the timezone identifier
     * @param conditions the list of conditions
     * @return the list of converted conditions
     */
    private static List<SceneConditionBean> toSceneConditionBeans(
        String loops, String timezoneId, List<Condition> conditions
    ) {
        if(conditions.isEmpty()){
            new ArrayList<>();
        }

        List<SceneConditionBean> conditionBeans = new ArrayList<>();
        AtomicInteger orderNum = new AtomicInteger(0);
        conditions.forEach(condition -> {
            int condType = inferConditionType(condition.getPropertyName());
            if(Constant.SCENE_CONDITION_FOR_ONE_KEY == condType) {
                return;
            }
            try{
                if(Constant.SCENE_CONDITION_FOR_CHANGE_WEATHER == condType){
                    SceneConditionBean bean = toWeatherConditionBean(condition, loops, timezoneId);
                    bean.setOrderNum(orderNum.incrementAndGet());
                    conditionBeans.add(bean);
                }else if(Constant.SCENE_CONDITION_FOR_TIMING == condType) {
                    SceneConditionBean bean = toScheduleConditionBean(condition, loops, timezoneId);
                    bean.setOrderNum(orderNum.incrementAndGet());
                    conditionBeans.add(bean);
                }else if(Constant.SCENE_CONDITION_FOR_DEVICE_STATUS_CHANGE == condType) {
                    SceneConditionBean bean = toDeviceStatusConditionBean(condition, loops);
                    bean.setOrderNum(orderNum.incrementAndGet());
                    conditionBeans.add(bean);
                }
            }catch (Exception exception){
                Log.d(TAG, "toSceneConditionBeans: Failed cause=" + exception.getLocalizedMessage());
            }
        });

        return conditionBeans;
    }

    /**
     * Build the device commands to be used in the scene actions and conditions
     * @param intent the intent
     * @param targetId the target identifier
     * @param value  the value
     * @return the commands
     */
    private static Map<String, Object> buildSceneCommands(String intent, String targetId, Object value) {
        Map<String, Object> commands;
        String commandValue = String.valueOf(value);
        if(ControlIntent.ColorTemperature.getIntent().contentEquals(intent)){
            int temperature = (int) Double.parseDouble(commandValue);
            int colorTemp = DeviceCmdConverterUtils.kelvinToPercentage(temperature);
            commands = DeviceCmdConverterUtils.toColorTemperatureCmd(
                    targetId, colorTemp
            );
        }else if(ControlIntent.Brightness.getIntent().contentEquals(intent)){
            int brightness = (int) Double.parseDouble(commandValue);
            commands = DeviceCmdConverterUtils.toBrightnessCmd(
                    targetId, brightness
            );
        }else if(ControlIntent.Power.getIntent().contentEquals(intent)) {
            boolean power = Boolean.parseBoolean(commandValue);
            commands = DeviceCmdConverterUtils.toPowerCmd(
                    targetId, power
            );
        }else if(ControlIntent.Color.getIntent().contentEquals(intent)){
            String[] colorArray = commandValue.split(",");
            int hue = (int) Double.parseDouble(colorArray[0]);
            int saturation = (int) Double.parseDouble(colorArray[1]);
            int brightness = (int) Double.parseDouble(colorArray[2]);
            Log.d(TAG, "buildSceneActionData: color=hsv("
                    + hue + ", "
                    + saturation + " ,"
                    + brightness + ")"
            );
            commands = DeviceCmdConverterUtils.toColorCmd(
                    targetId, hue, saturation, brightness
            );
        }else{
            throw new RuntimeException("Unsupported control intent | " + intent);
        }
        return commands;
    }

    /**
     * Build the scene action for the scene/automation
     * @param task the automation task
     * @return the scene action
     */
    private static String buildSceneActionData(Task task){
        Map<String, Object> commands = buildSceneCommands(
            task.getControlIntent(),task.getTargetId(), task.getValue()
        );
        Log.d(TAG, "buildSceneActionData: value=" + task.getValue());
        return commands.isEmpty() ? "" : "{\"props\":" + new Gson().toJson(commands) + "}";
    }

    /**
     * Builds the scene action bean
     * @param targetId the target identifier
     * @param data the action data
     * @return the scene action bean
     */
    private static SceneActionBean toSceneActionBean(String targetId ,String data) {
        SceneActionBean actionBean = new SceneActionBean();
        actionBean.setActionData(data);
        actionBean.setActionType(1);
        actionBean.setTargetId(targetId);
        return actionBean;
    }

    /**
     * Converts the tasks to scene actions
     *
     * @param tasks the automation tasks
     * @return the list of scene actions
     */
    private static List<SceneActionBean> toSceneActionBeans(List<Task> tasks) {
        // 1. get cached devices
        Map<String, DeviceInfoBean> deviceMap = DeviceDataUtils.getCachedDevicesMap();
        if(deviceMap.isEmpty()){
            return new ArrayList<>();
        }

        // 2. filter devices
        List<SceneActionBean> actionBeans = new ArrayList<>();
        AtomicInteger orderNum = new AtomicInteger(0);
        tasks.forEach(task -> {
            try{
                DeviceInfoBean deviceInfo = deviceMap.get(task.getTargetId());
                if(null == deviceInfo){
                    return;
                }

                // 3a. get props for WiFi Devices
                // 3b. infer props for Matter Devices
                //     (Since there is no cloud support, just remove all the matter devices)
                if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
                    return;
                }

                String actionData = buildSceneActionData(task);
                if(actionData.isEmpty()){
                    return;
                }

                // 4. tie everything up nicely
                SceneActionBean actionBean = toSceneActionBean(task.getTargetId(), actionData);
                actionBean.setOrderNum(orderNum.incrementAndGet());
                actionBeans.add(actionBean);
            }catch (Exception exception) {
                String message = "toSceneActionBeans: Failed to convert task. Cause="
                        + exception.getLocalizedMessage();
                Log.d(TAG, message);
            }
        });
        return actionBeans;
    }

    /**
     * Builds the trigger bean for the automation
     * @param timezoneId the timezone identifier
     * @return the trigger bean
     */
    private static SceneTriggerBean buildSceneTriggerBean(String timezoneId) {
        CityBean cityBean = CacheDataManager.getInstance().getCityInfo();
        if(cityBean == null){
            cityBean = new CityBean();
            cityBean.setName("");
            cityBean.setCityCode("");
        }

        SceneTriggerBean triggerBean = new SceneTriggerBean();
        triggerBean.setTz(timezoneId);
        triggerBean.setTimeInterval(1);
        triggerBean.setLoops("1111111");
        triggerBean.setCityName(cityBean.getName());
        triggerBean.setCityCode(cityBean.getCityCode());
        triggerBean.setCondType(Constant.SCENE_CONDITION_FOR_CHANGE_WEATHER);
        return triggerBean;
    }

    private static SceneRuleBean buildSceneRule(
        MatchType matchType, SceneTriggerBean trigger,
        List<SceneConditionBean> conditionBeans, List<SceneActionBean> tasks
    ) {
        String type = matchType.getType();
        SceneRuleBean sceneRuleBean = new SceneRuleBean();
        sceneRuleBean.setConditions(conditionBeans);
        sceneRuleBean.setActions(tasks);
        sceneRuleBean.setTriggers(Collections.singletonList(trigger));
        if(MatchType.getAny().getType().contentEquals(type)){
            sceneRuleBean.setMatchType(1);
        }
        return sceneRuleBean;
    }

    /**
     * Builds the scene bean
     * @param name the name of the scene/automation
     * @param loops the loops
     * @param timezoneId the timezone identifier
     * @param matchType the match type
     * @param tasks the automation/scene task
     * @param conditions the automation/scene conditions
     * @return the scene data
     */
    public static SceneBean buildSceneBean(
        String name, String loops, String timezoneId,
        MatchType matchType, List<Task> tasks, List<Condition> conditions
    ) {
        Log.d(TAG, "buildSceneBean: conditions="+ new Gson().toJson(conditions));
        // convert conditions
        List<SceneConditionBean> sceneConditions = toSceneConditionBeans(
            loops, timezoneId,conditions
        );
        if(sceneConditions.isEmpty() && (conditions.size() > 0)){
            throw new RuntimeException("Failed to build automation conditions");
        }

        Log.d(TAG, "buildSceneBean: tasks="+ new Gson().toJson(tasks));
        // convert actions
        List<SceneActionBean> sceneActions = toSceneActionBeans(tasks);
        if(sceneActions.isEmpty()){
            throw new RuntimeException("Failed to build automation tasks");
        }

        // build the trigger
        SceneTriggerBean trigger = buildSceneTriggerBean(timezoneId);
        // build rule
        SceneRuleBean ruleBean = buildSceneRule(matchType, trigger, sceneConditions, sceneActions);
        // infer scene type
        int sceneType = conditions.isEmpty() ?
                Constant.SCENE_TYPE_FOR_MANUAL : Constant.SCENE_TYPE_FOR_AUTO;

        // tie everything up nicely
        SceneBean sceneBean = new SceneBean();
        sceneBean.setName(name);
        sceneBean.setBgColor("#DCDCF4");
        sceneBean.setSceneType(sceneType);
        sceneBean.setRuleMetaData(ruleBean);
        sceneBean.setEnabled(1);
        sceneBean.setCoverUrl("https://storage-app.rinoiot.com/scene/icon/default-icon.png");
        return sceneBean;
    }

    /**
     * Builds the automation info list from the cached data
     * @return the list of automation list
     */
    public static List<AutomationInfo> getCachedAutomations() {
        // get the home identifier or the asset the identifier
        String homeId = CacheDataManager.getInstance().getCurrentHomeId();
        if(StringUtil.isBlank(homeId)) {
            return new ArrayList<>();
        }

        // get the list of cached scene data
        List<SceneBean> sceneBeans = CacheDataManager.getInstance().getAllSceneList(homeId);
        if(null == sceneBeans || sceneBeans.isEmpty()){
            return new ArrayList<>();
        }

        // convert the scene data
        return sceneBeans.stream().map(
                sceneBean -> new AutomationInfo(sceneBean.getId(), sceneBean.getName())
        ).collect(Collectors.toList());
    }
}
