package com.smart.rinoiot.common.manager;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Base64;

import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;

public class UserInfoManager {
    private static UserInfoManager instance;
    private UserInfoBean userInfo;

    private UserInfoManager() {
    }

    public static UserInfoManager getInstance() {
        if (instance == null) {
            instance = new UserInfoManager();
        }
        return instance;
    }

    public void saveUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
        save(marshall(userInfo));
    }

    public UserInfoBean getUserInfo(Context context) {
        if (userInfo == null) {
            userInfo = (UserInfoBean) getParcel(context);
        }
        return userInfo;
    }

    public void clear() {
        userInfo = null;
        SharedPreferenceUtil.getInstance().put("user_info_bean", "");
        SharedPreferenceUtil.getInstance().remove("user_info_bean");
    }

    public boolean isLogin(Context context) {
        if (userInfo == null) {
            userInfo = getUserInfo(context);
            if (userInfo == null) {
                return false;
            }
        }
        return !TextUtils.isEmpty(userInfo.accessToken);
    }

    /**
     * 将bytes经过base64转换成字符串并存储到sp中
     * @param bytes the bytes
     */
    private static void save(byte[] bytes) {
        String saveStr = Base64.encodeToString(bytes, 0);
        SharedPreferenceUtil.getInstance().put("user_info_bean", saveStr);
    }

    /**
     * 从sp中取出字符串并转换成bytes 然后bytes->Parcel->Object
     * @param context the context
     * @return the data parcel
     */
    private static Object getParcel(Context context) {
        byte[] bytes = Base64.decode(SharedPreferenceUtil.getInstance().get(
                "user_info_bean", ""
        ).getBytes(), Base64.DEFAULT);
        //从bytes中获取Parcel
        Parcel parcel = unmarshall(bytes);
        return parcel.readValue(context.getClassLoader());
    }

    /**
     * marshall Parcel将自身保存的数据以byte数组形式返回
     * @param parcelable the parcel
     * @return the marshalled data
     */
    private static byte[] marshall(Parcelable parcelable) {
        Parcel parcel = Parcel.obtain();
        parcel.setDataPosition(0);
        parcel.writeValue(parcelable);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    /**
     * 从byte数组中获取数据，存入自身的Parcel中
     * @param bytes the data bytes
     * @return the parcel
     */
    private static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return parcel;
    }
}
