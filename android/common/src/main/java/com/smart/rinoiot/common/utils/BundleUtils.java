package com.smart.rinoiot.common.utils;

public class BundleUtils {

    /** 为物模型设置默认值 */
//    public static Bundle fromArray(List list) {
//        Bundle result = new Bundle();
//        Iterator var2 = list.iterator();
//
//        while(var2.hasNext()) {
//            Object obj = var2.next();
//            if (obj == null) {
//                result.pushNull();
//            } else if (obj.getClass().isArray()) {
//                catalystArray.pushArray(fromArray(obj));
//            } else if (obj instanceof Bundle) {
//                catalystArray.pushMap(fromBundle((Bundle)obj));
//            } else if (obj instanceof List) {
//                catalystArray.pushArray(fromList((List)obj));
//            } else if (obj instanceof String) {
//                catalystArray.pushString((String)obj);
//            } else if (obj instanceof Integer) {
//                catalystArray.pushInt((Integer)obj);
//            } else if (obj instanceof Number) {
//                catalystArray.pushDouble(((Number)obj).doubleValue());
//            } else {
//                if (!(obj instanceof Boolean)) {
//                    throw new IllegalArgumentException("Unknown value type " + obj.getClass());
//                }
//
//                catalystArray.pushBoolean((Boolean)obj);
//            }
//        }
//        return result;
//    }
}
