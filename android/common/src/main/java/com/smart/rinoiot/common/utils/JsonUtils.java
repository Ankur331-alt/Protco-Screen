package com.smart.rinoiot.common.utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ProjectName: Rino Smart
 * @Package: com.smart.rinoiot.panel_sdk.utils
 * @ClassName: JsonUtils
 * @Description: java类作用描述
 * @Author: ZhangStar
 * @Emali: ZhangStar666@gmali.com
 * @CreateDate: 2023/8/1 20:38
 * @UpdateUser: 更新者：
 * @UpdateDate: 2023/8/1 20:38
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class JsonUtils {
    public static Map<String, Object> jsonToHashMap(String jsonString) throws JSONException {
        return parseJsonObject(new JSONObject(jsonString));
    }

    public static Map<String, Object> parseJsonObject(JSONObject jsonObject) throws JSONException {
        Map<String, Object> params = new HashMap<>();
        for (Iterator<String> mIt = jsonObject.keys(); mIt.hasNext(); ) {
            String key = mIt.next();
            if (jsonObject.get(key) instanceof JSONObject) {
                params.put(key, parseJsonObject((JSONObject) jsonObject.get(key)));
            } else {
                params.put(key, jsonObject.get(key));
            }
        }
        return params;
    }
}
