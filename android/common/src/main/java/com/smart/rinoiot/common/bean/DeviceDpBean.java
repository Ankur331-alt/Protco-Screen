package com.smart.rinoiot.common.bean;

import java.io.Serializable;

public class DeviceDpBean implements Serializable {

    /**
     * 功能json
     */
    private String dpJson;
    /**
     * 属性标识符
     */
    private String key;
    /**
     * 属性名称
     */
    private String name;
    /**
     * 功能事件
     */
    private Object value;

    /**
     * 功能点图标
     */
    private String imageJson;

    private String id;//	dp点ID

    public String getImageJson() {
        return imageJson;
    }

    public void setImageJson(String imageJson) {
        this.imageJson = imageJson;
    }

    public String getDpJson() {
        return dpJson;
    }

    public void setDpJson(String dpJson) {
        this.dpJson = dpJson;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

//    @RequiresApi(api = Build.VERSION_CODES.Q)
//    protected DeviceDpBean(Parcel in) {
//        dpJson = in.readString();
//        key = in.readString();
//        name = in.readString();
////        if (value instanceof Integer) {
////            value = in.readInt();
////        } else if (value instanceof Boolean) {
////            value = in.readBoolean();
////        } else {
////            value = in.readString();
////        }
//        in.readValue(getClass().getClassLoader());
//    }
//
//    public static final Creator<DeviceDpBean> CREATOR = new Creator<DeviceDpBean>() {
//        @RequiresApi(api = Build.VERSION_CODES.Q)
//        @Override
//        public DeviceDpBean createFromParcel(Parcel in) {
//            return new DeviceDpBean(in);
//        }
//
//        @Override
//        public DeviceDpBean[] newArray(int size) {
//            return new DeviceDpBean[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(dpJson);
//        dest.writeString(key);
//        dest.writeString(name);
//        dest.writeValue(value);
//    }
}
