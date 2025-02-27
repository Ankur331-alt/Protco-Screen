package com.smart.rinoiot.common.matter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dsh.matter.management.device.DeviceController;
import com.dsh.matter.management.device.DeviceControllerCallback;
import com.dsh.matter.management.device.OperationFailureException;
import com.dsh.matter.model.color.HSVColor;
import com.dsh.matter.model.device.StateAttribute;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.MatterLocalBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.manager.BleDeviceReportSendManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.rn.BundleJSONConverter;
import com.smart.rinoiot.common.utils.JSONDataFormatUtils;
import com.smart.rinoiot.common.utils.LgUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * @author edwin
 */
public class MtrDeviceControlManager {
    private static final String TAG = MtrDeviceControlManager.class.getSimpleName();
    /**
     * An instance of device mtr device control manager
     */
    private static MtrDeviceControlManager instance;

    /**
     * Mtr device controller
     */
    private final DeviceController mDeviceController;

    /**
     * matter设备在线状态是否更新
     */
    private static final Map<String, Boolean> queryMatterDeviceOnlineStatusList = new HashMap<>();

    private MtrDeviceControlManager(@ApplicationContext Context context) {
        mDeviceController = new DeviceController(context);
    }

    /**
     * Retrieves an instance of the device control manager
     *
     * @param context application context
     * @return an instance of the device control manager
     */
    public static MtrDeviceControlManager getInstance(@ApplicationContext Context context) {
        if (instance == null) {
            instance = new MtrDeviceControlManager(context);
        }
        return instance;
    }

    /**
     * Returns the continuation
     * @return the continuation
     */
    private Continuation<Unit> getContinuation(){
        return new Continuation<Unit>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {
                Log.e(TAG, "resumeWith: " + new Gson().toJson(o));
            }
        };
    }

    /**
     * Publishes device's power/switch control values
     *
     * @param deviceId device identifier
     * @param status   power status
     */
    public void power(long deviceId, boolean status) throws OperationFailureException {
        Continuation<Unit> continuation = getContinuation();
        mDeviceController.power(deviceId, status, continuation);
    }

    /**
     * Control power
     * @param metadata device's metadata
     * @param power the power value
     * @return true on success false otherwise
     */
    public boolean power(Metadata metadata, boolean power){
        Log.d(TAG, "onDevicePowerControl: deviceId= " + metadata.getDeviceId() + " | power= "+power);
        try{
            boolean supportsCommand = MtrDeviceTypeUtils.supportsCommand(
                    metadata.getDeviceType(), StateAttribute.Switch
            );
            if(!supportsCommand){
                return false;
            }
            Log.d(TAG, "onDevicePowerControl: metadata=" + new Gson().toJson(metadata));
            power(metadata.getDeviceId(), power);
            return true;
        }catch (Exception ex) {
            Log.e(TAG, "onDevicePowerControl: failed. Cause= " + ex.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Publishes device's power/switch control values
     *
     * @param metadata metadata
     * @param status   power status
     * @param callback device ctrl callback
     */
    public void power(
            Metadata metadata, boolean status, @NonNull DeviceControllerCallback callback
    ) {
        try{
            boolean supportsCommand = MtrDeviceTypeUtils.supportsCommand(
                    metadata.getDeviceType(), StateAttribute.Switch
            );
            if(!supportsCommand){
                callback.onError(new UnsupportedOperationException());
                return;
            }

            Log.d(TAG, "onDevicePowerControl: metadata=" + new Gson().toJson(metadata));
            Continuation<Unit> continuation = getContinuation();
            mDeviceController.power(metadata.getDeviceId(), status, callback, continuation);
        }catch (Exception exception){
            Log.d(TAG, "power: failed. Cause=" + exception.getLocalizedMessage());
            callback.onError(exception);
        }
    }

    /**
     * Publishes device's brightness control values
     *
     * @param deviceId   device identifier
     * @param brightness brightness
     */
    public void brightness(long deviceId, int brightness) throws OperationFailureException {
        Continuation<Unit> continuation = getContinuation();
        mDeviceController.brightness(deviceId, brightness, continuation);
    }

    /**
     * Controls the brightness
     *
     * @param metadata device's metadata
     * @param brightness the brightness
     * @return true on success, false otherwise
     */
    public boolean brightness(Metadata metadata, int brightness) {
        Log.d(TAG, "brightness: deviceId=" + metadata.getDeviceId() + " | brightness="+brightness);
        try{
            boolean supportsCommand = MtrDeviceTypeUtils.supportsCommand(
                    metadata.getDeviceType(), StateAttribute.Brightness
            );
            if(!supportsCommand){
                return false;
            }
            Log.d(TAG, "brightness: metadata=" + new Gson().toJson(metadata));
            brightness(metadata.getDeviceId(), brightness);
            return true;
        }catch (Exception ex) {
            Log.e(TAG, "brightness: failed. Cause= " + ex.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Controls the brightness
     *
     * @param metadata device's metadata
     * @param brightness the brightness
     * @param callback operation callback
     */
    public void brightness(
            Metadata metadata, int brightness, @NonNull DeviceControllerCallback callback
    ) {
        Log.d(TAG, "brightness: deviceId="+ metadata.getDeviceId() +" | brightness="+brightness);
        try{
            boolean supportsCommand = MtrDeviceTypeUtils.supportsCommand(
                    metadata.getDeviceType(), StateAttribute.Brightness
            );
            if(!supportsCommand){
                callback.onError(new UnsupportedOperationException());
                return;
            }

            Log.d(TAG, "brightness: metadata=" + new Gson().toJson(metadata));
            Continuation<Unit> continuation = getContinuation();
            mDeviceController.brightness(metadata.getDeviceId(), brightness, callback, continuation);
        }catch (Exception ex) {
            Log.e(TAG, "brightness: failed. Cause= " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    /**
     * Publishes device's brightness control values
     *
     * @param deviceId device identifier
     * @param color    HSV color.
     * @apiNote The HSV model takes the standard values for hue [0-100], saturation [0-100] and
     * value [0-100]. NOTE: the value of V will be ignored, thus cannot be used to control the
     * brightness of the device.  For brightness control, please use the brightness function
     */
    public void color(long deviceId, HSVColor color) throws OperationFailureException {
        Continuation<Unit> continuation = getContinuation();
        mDeviceController.color(deviceId, color, continuation);
    }

    /**
     * Controls the color
     *
     * @param metadata device's metadata
     * @param hue the hue
     * @param saturation the saturation
     * @param value the value
     * @return true on success false otherwise
     */
    public boolean color(Metadata metadata, int hue, int saturation, int value) {
        try{
            if(!MtrDeviceTypeUtils.supportsCommand(metadata.getDeviceType(), StateAttribute.Color)){
                return false;
            }
            Log.d(TAG, "controlColor: metadata=" + new Gson().toJson(metadata));
            color(metadata.getDeviceId(), new HSVColor(hue, saturation, value));
            return true;
        }catch (Exception ex) {
            Log.e(TAG, "controlColor: failed. Cause= " + ex.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Controls the color
     *
     * @param metadata device's metadata
     * @param hue the hue
     * @param saturation the saturation
     * @param value the value
     * @param callback operation callback
     */
    public void color(
            Metadata metadata,
            int hue, int saturation, int value,
            @NonNull DeviceControllerCallback callback
    ) {
        try{
            boolean supportsCommand = MtrDeviceTypeUtils.supportsCommand(
                    metadata.getDeviceType(), StateAttribute.Color
            );
            if(!supportsCommand){
                callback.onError(new UnsupportedOperationException());
                return;
            }

            Log.d(TAG, "color: metadata=" + new Gson().toJson(metadata));
            Continuation<Unit> continuation = getContinuation();
            mDeviceController.color(
                    metadata.getDeviceId(),
                    new HSVColor(hue, saturation, value),
                    callback, continuation
            );
        }catch (Exception ex) {
            Log.e(TAG, "controlColor: failed. Cause= " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    /**
     * Publishes device's color temperature control values
     *
     * @param deviceId device identifier
     * @param temperature  color temperature
     */
    public void colorTemperature(long deviceId, int temperature) throws OperationFailureException {
        Continuation<Unit> continuation = getContinuation();
        mDeviceController.colorTemperature(deviceId, temperature, continuation);
    }

    /**
     * Controls the color temperature
     * @param metadata device's metadata
     * @param temperature the color temperature value
     * @return true on success false otherwise
     */
    public boolean colorTemperature(Metadata metadata, int temperature) {

        Log.d(TAG, "controlColorTemp: deviceId= "+metadata.getDeviceId()+" | colorTemp= "+temperature);
        try{
            boolean supportsCommand = MtrDeviceTypeUtils.supportsCommand(
                    metadata.getDeviceType(), StateAttribute.ColorTemperature
            );
            if(!supportsCommand){
                return false;
            }
            Log.d(TAG, "controlColorTemp: metadata=" + new Gson().toJson(metadata));
            colorTemperature(metadata.getDeviceId(), temperature);
            return true;
        }catch (Exception ex) {
            Log.e(TAG, "controlColorTemp: failed. Cause= " + ex.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Controls the color temperature
     * @param metadata device's metadata
     * @param temperature the color temperature value
     * @param callback operation callback
     */
    public void colorTemperature(
            Metadata metadata, int temperature,
            @NonNull DeviceControllerCallback callback
    ) {
        Log.d(TAG, "colorTemperature: deviceId= "+metadata.getDeviceId()+" | temperature= "+temperature);
        try{
            boolean supportsCommand = MtrDeviceTypeUtils.supportsCommand(
                    metadata.getDeviceType(), StateAttribute.ColorTemperature
            );
            if(!supportsCommand){
                callback.onError(new UnsupportedOperationException());
                return;
            }

            Log.d(TAG, "controlColorTemp: metadata=" + new Gson().toJson(metadata));
            Continuation<Unit> continuation = getContinuation();
            mDeviceController.colorTemperature(
                    metadata.getDeviceId(), temperature, callback, continuation
            );
        }catch (Exception ex) {
            Log.e(TAG, "controlColorTemp: failed. Cause= " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    /**
     * matter设备与面板交互入口
     * mqtt 1.0
     * [{"deviceId":"1623580479275642880","properties":{"matter_switch":false}}]
     * * [{"deviceId":"1643915916117426176","properties":[{"switch":{"ts":1.680868979E9,"value":true}}]}]
     * <p>
     * mqtt 2.0
     * <p>
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * }
     * }
     * * WritableArray writableArray = BundleJSONConverter.jsonToReact(new JSONArray(json));
     */
    public void matterControlEntrance(Object object) {
        try {
            if (object instanceof ReadableArray) {
                JSONArray jsonArray = BundleJSONConverter.reactToJSON((ReadableArray) object);
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        matterOperate(jsonArray.getJSONObject(i), 1);
                    }
                }
            } else if (object instanceof ReadableMap) {
                matterOperate(BundleJSONConverter.reactToJSON((ReadableMap) object), 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * mqtt 1.0 和mqtt 2.0 matter设备处理逻辑
     *
     * @param dataType 1:mqtt 1.0   2:mqtt  2.0
     */
    public void matterOperate(JSONObject propertiesItem, int dataType) throws Exception {
        Log.d(TAG, "matterOperate: properties = " + propertiesItem.toString() + " | dataType = " +  dataType);
        String devId = "";
        JSONObject jsonObject = null;
        if (propertiesItem != null && propertiesItem.has("deviceId")) {
            devId = propertiesItem.getString("deviceId");
        }
        jsonObject = JSONDataFormatUtils.specialJsonFormatData(dataType, propertiesItem);
        if (TextUtils.isEmpty(devId) || jsonObject == null) {
            LgUtils.w(TAG + "   matterControlEntrance   devId为空或者properties为空");
            return;
        }
        DeviceInfoBean info = CacheDataManager.getInstance().getDeviceInfo(devId);
        Metadata metadata = MtrDeviceDataUtils.toMetadata(info.getMetaInfo());
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            if (key.contains(MtrPanelControlManager.MATTER_SWITCH)
                    && jsonObject.get(key) instanceof Boolean) {
                //开关
                boolean switchStatus = jsonObject.getBoolean(key);
                if (metadata != null) {
                    power(dataType, metadata.getDeviceId(), switchStatus, propertiesItem);
                }
            } else if (key.contains(MtrPanelControlManager.MATTER_COLOR_TEMP)
                    && jsonObject.get(key) instanceof Number) {
                //色温
                int colorTemperature = jsonObject.getInt(key);
                if (metadata != null) {
                    colorTemperature(dataType, metadata.getDeviceId(), colorTemperature, propertiesItem);
                }
            } else if (key.contains(MtrPanelControlManager.MATTER_COLOR)
                    && jsonObject.get(key) instanceof String) {
                //颜色
                String color = jsonObject.getString(key);
                if (!TextUtils.isEmpty(color) && color.length() == 8) {
                    int hue = Integer.parseInt(color.substring(0, 4), 16);
                    int saturation = Integer.parseInt(color.substring(4, 6), 16);
                    int value = Integer.parseInt(color.substring(6, 8), 16);
                    if (metadata != null) {
                        HSVColor hsvColor = new HSVColor(hue, saturation, value);
                        color(dataType, metadata.getDeviceId(), hsvColor, propertiesItem);
                    }
                }
            } else if (key.contains(MtrPanelControlManager.MATTER_BRIGHTNESS)
                    && jsonObject.get(key) instanceof Number) {//亮度
                int brightness = jsonObject.getInt(key);
                if (metadata != null) {
                    brightness(dataType, metadata.getDeviceId(), brightness, propertiesItem);
                }
            }

        }
    }

    /**
     * matter设备操作成功，发送通知，通知面板
     *
     * @param dataType 1:mqtt 1.0   2:mqtt  2.0
     */
    private void matterUpdateNotifyPanel(int dataType, JSONObject item) {
        updateMatterLocalData(dataType, item);
        String devId = "";
        JSONObject tempData = null;
        try {
            if (item != null && item.has("deviceId")) {
                devId = item.getString("deviceId");
            }
            tempData = JSONDataFormatUtils.specialJsonFormatData(dataType, item);
            if (TextUtils.isEmpty(devId) || tempData == null) {
                LgUtils.w(TAG + "   matterUpdateNotifyPanel   devId为空或者properties为空");
                return;
            }
            LgUtils.w(TAG + "   matterUpdateNotifyPanel   tempData=" + new Gson().toJson(tempData));
            BleDeviceReportSendManager.getInstance().matterOperateSuccessNotify(dataType, devId, tempData);
        } catch (Exception e) {
            LgUtils.w(TAG + "matterUpdateNotifyPanel 异常  e=" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Publishes device's power/switch control values
     *
     * @param deviceId device identifier
     * @param status   power status
     * @param dataType 1:mqtt 1.0   2:mqtt  2.0
     */
    public void power(int dataType, long deviceId, boolean status, JSONObject item) {
        DeviceControllerCallback callback = new DeviceControllerCallback() {
            @Override
            public void onSuccess() {
                matterUpdateNotifyPanel(dataType, item);
            }

            @Override
            public void onError(@Nullable Exception e) {

            }
        };
        Continuation<Unit> continuation = getContinuation();
        mDeviceController.power(deviceId, status, callback, continuation);
    }

    /**
     * Publishes device's brightness control values
     *
     * @param deviceId device identifier
     * @param color    HSV color.
     * @param dataType 1:mqtt 1.0   2:mqtt  2.0
     * @apiNote The HSV model takes the standard values for hue [0-100], saturation [0-100] and
     * value [0-100]. NOTE: the value of V will be ignored, thus cannot be used to control the
     * brightness of the device.  For brightness control, please use the brightness function
     */
    public void color(int dataType, long deviceId, HSVColor color, JSONObject item) {
        DeviceControllerCallback callback = new DeviceControllerCallback() {
            @Override
            public void onSuccess() {
                matterUpdateNotifyPanel(dataType, item);
            }

            @Override
            public void onError(@Nullable Exception e) {

            }
        };
        try {
            Continuation<Unit> continuation = getContinuation();
            mDeviceController.color(deviceId, color, callback, continuation);
        }catch (Exception exception) {
            Log.e(TAG, "color: " + exception.getLocalizedMessage());
        }
    }


    /**
     * 色温控制
     *
     * @param dataType 1:mqtt 1.0   2:mqtt  2.0
     */
    public void colorTemperature(int dataType, long deviceId, int color, JSONObject item) {
        Log.d(TAG, "colorTemperature: deviceId = " + deviceId + " | temperature = " + color);
        DeviceControllerCallback callback = new DeviceControllerCallback() {
            @Override
            public void onSuccess() {
                matterUpdateNotifyPanel(dataType, item);
            }

            @Override
            public void onError(@Nullable Exception e) {

            }
        };

        try {
            Continuation<Unit> continuation = getContinuation();
            mDeviceController.colorTemperature(deviceId, color, callback, continuation);
        }catch (Exception exception) {
            Log.e(TAG, "colorTemperature: " + exception.getLocalizedMessage());
        }
    }

    /**
     * Publishes device's brightness control values
     *
     * @param deviceId   device identifier
     * @param brightness brightness
     * @param dataType   1:mqtt 1.0   2:mqtt  2.0
     */
    public void brightness(int dataType, long deviceId, int brightness, JSONObject item) {
        DeviceControllerCallback callback = new DeviceControllerCallback() {
            @Override
            public void onSuccess() {
                matterUpdateNotifyPanel(dataType, item);
            }

            @Override
            public void onError(@Nullable Exception e) {

            }
        };

        try {
            Continuation<Unit> continuation = getContinuation();
            mDeviceController.brightness(deviceId, brightness, callback, continuation);
        }catch (Exception exception){
            Log.e(TAG, "brightness: " + exception.getLocalizedMessage());
        }
    }

    /**
     * 更新matter本地数据
     *
     * @param dataType 1:mqtt 1.0   2:mqtt  2.0
     */
    private void updateMatterLocalData(int dataType, JSONObject item) {
        MatterLocalBean matterDeviceData = null;
        String devId = "";
        JSONObject jsonObject = null;
        try {
            if (item != null && item.has("deviceId")) {
                devId = item.getString("deviceId");
            }
            jsonObject = JSONDataFormatUtils.specialJsonFormatData(dataType, item);
            if (TextUtils.isEmpty(devId) || jsonObject == null) {
                LgUtils.w(TAG + "   matterControlEntrance   devId为空或者properties为空");
                return;
            }
            matterDeviceData = CacheDataManager.getInstance().getMatterDeviceData(devId);
            if (matterDeviceData == null) {
                matterDeviceData = new MatterLocalBean();
            }
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                if (key.contains(MtrPanelControlManager.MATTER_SWITCH)
                        && jsonObject.get(key) instanceof Boolean) {//开关
                    boolean switchStatus = jsonObject.getBoolean(key);
                    matterDeviceData.setSwitch(switchStatus);
                } else if (key.contains(MtrPanelControlManager.MATTER_COLOR_TEMP)
                        && jsonObject.get(key) instanceof Number) {
                    //色温
                    int colorTemperature = jsonObject.getInt(key);
                    matterDeviceData.setColorTemperature(colorTemperature);
                } else if (key.contains(MtrPanelControlManager.MATTER_COLOR)
                        && jsonObject.get(key) instanceof String) {
                    //颜色
                    String color = jsonObject.getString(key);
                    if (!TextUtils.isEmpty(color) && color.length() == 8) {
                        int hue = Integer.parseInt(color.substring(0, 4), 16);
                        int saturation = Integer.parseInt(color.substring(4, 6), 16);
                        int value = Integer.parseInt(color.substring(6, 8), 16);
                        MatterLocalBean.MatterColor matterColor = new MatterLocalBean.MatterColor();
                        matterColor.setHue(hue);
                        matterColor.setValue(value);
                        matterColor.setSaturation(saturation);
                        matterDeviceData.setColor(matterColor);
                    }
                } else if (key.contains(MtrPanelControlManager.MATTER_BRIGHTNESS)
                        && jsonObject.get(key) instanceof Number) {
                    //亮度
                    int brightness = jsonObject.getInt(key);
                    matterDeviceData.setBrightness(brightness);
                }

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally {
            LgUtils.w(TAG + "   saveMatterDeviceData   item.getDeviceId()=" + devId
                    + "  matterDeviceData=" + new Gson().toJson(matterDeviceData));
            if (!TextUtils.isEmpty(devId)) {
                CacheDataManager.getInstance().saveMatterDeviceData(devId, matterDeviceData);
            }
        }
    }

    /**
     * matter更新开关时，更新跟开关相关的功能
     */
    public static void updateSwitchByDeviceAllSwitch(String devId) {
        boolean isDataUpdate = false;
        DeviceInfoBean deviceInfo = null;
        if (!TextUtils.isEmpty(devId)) {
            deviceInfo = CacheDataManager.getInstance().getDeviceInfo(devId);
            MatterLocalBean matterDeviceData = CacheDataManager.getInstance().getMatterDeviceData(devId);
            if (deviceInfo != null && matterDeviceData != null) {
                //快捷开关
                List<DeviceDpBean> switchDpInfoVOList = deviceInfo.getSwitchDpInfoVOList();
                //常用功能
                List<DeviceDpBean> stockDpInfoVOList = deviceInfo.getStockDpInfoVOList();
                if (switchDpInfoVOList != null && !switchDpInfoVOList.isEmpty()) {
                    for (DeviceDpBean deviceDpBean : switchDpInfoVOList) {
                        if (deviceDpBean.getValue() instanceof Boolean) {
                            isDataUpdate = true;
                            deviceDpBean.setValue(matterDeviceData.isSwitch());
                        }
                    }
                }
                //常用功能
                if (stockDpInfoVOList != null && !stockDpInfoVOList.isEmpty()) {
                    for (DeviceDpBean item : switchDpInfoVOList) {
                        if (item.getKey().contains(MtrPanelControlManager.MATTER_SWITCH)) {
                            //开关
                            item.setValue(matterDeviceData.isSwitch());
                            isDataUpdate = true;
                        } else if (item.getKey().contains(MtrPanelControlManager.MATTER_COLOR_TEMP)) {
                            //色温
                            MatterLocalBean.MatterColor color = matterDeviceData.getColor();
                            if (color != null) {
                                item.setValue(color.getValue());
                                isDataUpdate = true;
                            }
                        } else if (item.getKey().contains(MtrPanelControlManager.MATTER_COLOR)) {
                            //颜色
                            MatterLocalBean.MatterColor color = matterDeviceData.getColor();
                            if (color != null) {
                                String stringBuffer = MtrPanelControlManager.getInstance().temToHexString(color.getHue(), 4) +
                                        MtrPanelControlManager.getInstance().temToHexString(color.getSaturation(), 0) +
                                        MtrPanelControlManager.getInstance().temToHexString(100, 0);
                                item.setValue(stringBuffer);
                            } else {
                                item.setValue("00006464");
                            }
                            isDataUpdate = true;
                        } else if (item.getKey().contains(MtrPanelControlManager.MATTER_BRIGHTNESS)) {
                            //亮度
                            item.setValue(matterDeviceData.getBrightness());
                            isDataUpdate = true;
                        }
                    }
                }
            }
        }
        if (TextUtils.isEmpty(devId) || !isDataUpdate) {
            return;
        }
        List<DeviceInfoBean> temp = new ArrayList<>();
        List<DeviceInfoBean> allDeviceList = CacheDataManager.getInstance().getAllDeviceList(CacheDataManager.getInstance().getCurrentHomeId());
        for (DeviceInfoBean item : allDeviceList) {
            if (TextUtils.isEmpty(item.getGroupId()) && TextUtils.equals(item.getId(), devId)) {
                temp.add(deviceInfo);
                continue;
            }
            temp.add(item);
        }
        CacheDataManager.getInstance().saveAllDeviceList(CacheDataManager.getInstance().getCurrentHomeId(), temp);

    }

    /**
     * matter设备更新在线离线状态
     */
    public void addQueryMatterDeviceOnline(Context context, DeviceInfoBean deviceInfoBean) {
        if (context == null || !MtrDeviceDataUtils.isMatterDevice(deviceInfoBean)) {
            return;
        }
        MtrDeviceStatesManager.getInstance(context).queryDeviceStatesAsync(deviceInfoBean);
    }



    public static Map<String, Boolean> getQueryMatterDeviceOnlineStatusList() {
        return queryMatterDeviceOnlineStatusList;
    }

    public static void addQueryMatterDeviceOnlineStatus(String devId) {
        queryMatterDeviceOnlineStatusList.put(devId, true);
        if (matterDeviceUpdateStatusListener != null) {
            matterDeviceUpdateStatusListener.updateStatus();
        }
    }

    /**
     * matter设备获取在线状态回调，刷新设备列表数据
     */
    public static MatterDeviceUpdateStatusListener matterDeviceUpdateStatusListener;

    public static void setMatterDeviceUpdateStatusListener(MatterDeviceUpdateStatusListener listener) {
        matterDeviceUpdateStatusListener = listener;
    }

    public interface MatterDeviceUpdateStatusListener {

        /**
         * invoked to notify status update
         */
        void updateStatus();
    }

    /**
     * 快捷开关和常用功能
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * }
     * }
     */
    public static ReadableMap getSwitchFormatData(DeviceInfoBean deviceInfoBean) {
        WritableMap writableMap = Arguments.createMap();
        if (deviceInfoBean == null) {
            return  writableMap;
        }

        writableMap.putString("deviceId", deviceInfoBean.getId());
        MatterLocalBean matterDeviceData = CacheDataManager.getInstance().getMatterDeviceData(deviceInfoBean.getId());
        if(null == matterDeviceData){
            return writableMap;
        }

        List<DeviceDpBean> switchDpInfoVOList = deviceInfoBean.getSwitchDpInfoVOList();
        if (null == switchDpInfoVOList || switchDpInfoVOList.isEmpty()) {
            return  writableMap;
        }

        for (DeviceDpBean item : switchDpInfoVOList) {
            if (!(item.getValue() instanceof Boolean)) {
                continue;
            }

            WritableMap writableMapData = Arguments.createMap();
            writableMapData.putBoolean(item.getKey(), matterDeviceData.isSwitch());
            writableMap.putMap("data", writableMapData);
        }
        return writableMap;
    }
}
