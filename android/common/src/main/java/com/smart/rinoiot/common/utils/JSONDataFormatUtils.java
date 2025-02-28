package com.smart.rinoiot.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

/**
 * @author tw
 * @time 2023/4/11 18:48
 * @description JSONObject to JsonObject
 */
public class JSONDataFormatUtils {
    private static final String TAG = JSONDataFormatUtils.class.getSimpleName();

    /**
     * JSONObject 转化为JsonObject
     */
    public static JsonElement orgJsonToGoogleJson(JSONObject propertiesJson) throws Exception {
        return new JsonParser().parse(propertiesJson.toString());
    }

    /**
     * mqtt 1.0 数据格式
     * [{"properties":{"switch":false},"deviceId":"1630469334162354176"},
     * <p>
     * mqtt 2.0 数据格式
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * ......
     * }
     *
     * @param dataType 1:mqtt 1.0   2:mqtt  2.0
     */
    public static JSONObject specialJsonFormatData(int dataType, JSONObject jsonData) {
        JSONObject jsonObject = null;
        try {
            if (jsonData != null) {
                if (dataType == 1) {
                    jsonObject = jsonData.getJSONObject("properties");
                } else if (dataType == 2) {
                    jsonObject = jsonData.getJSONObject("data");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LgUtils.w(TAG + "   specialJsonData   Exception e=" + e.getMessage());
        } finally {
            return jsonObject;
        }
    }
}
