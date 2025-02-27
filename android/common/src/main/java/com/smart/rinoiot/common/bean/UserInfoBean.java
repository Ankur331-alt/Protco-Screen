package com.smart.rinoiot.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserInfoBean implements Parcelable {
    /** 头像地址 */
    @SerializedName(value = "avatarUrl")
    public String avatarUrl;
    /** 注册类型（1=邮箱注册，2=手机注册，3=第三方注册） */
    @SerializedName(value = "registryType")
    public int registryType;
    /** 用户类型（1=正常用户，2=匿名用户） */
    @SerializedName(value = "userType")
    public int userType;
    /** 邮箱 */
    @SerializedName(value = "email")
    public String email;
    /** 用户ID */
    @SerializedName(value = "id", alternate = {"userId", "memberId"})
    public String id;
    /** token类型 */
    @SerializedName(value = "token_type")
    public String tokenType;
    /** 手机号 */
    @SerializedName(value = "phone")
    public String phoneNumber;
    /** 访问令牌 */
    @SerializedName(value = "access_token")
    public String accessToken;
    /** 刷新令牌 */
    @SerializedName(value = "refresh_token")
    public String refreshToken;
    /** 昵称 */
    @SerializedName(value = "nickname")
    public String nickname;
    /** 国家编码 */
    @SerializedName(value = "areaCode")
    public String countryCode;
    /** 最近一次登录时间 */
    @SerializedName(value = "latestLoginTime")
    public long latestLoginTime;
    /** 用户设置时区 */
    @SerializedName(value = "tz")
    public String tz;
    /** 用户名 */
    @SerializedName(value = "userName", alternate = "username")
    public String userName;
    /** 过期时间 */
    @SerializedName(value = "expires_in")
    public long expiresIn;
    /** 认证身份标识，默认是 username */
    @SerializedName(value = "authenticationIdentity")
    public String authenticationIdentity;
    /** 温度单位，默认是 username */
    @SerializedName(value = "tempUnit")
    public String tempUnit;
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatarUrl);
        dest.writeInt(registryType);
        dest.writeInt(userType);
        dest.writeString(email);
        dest.writeString(id);
        dest.writeString(tokenType);
        dest.writeString(phoneNumber);
        dest.writeString(accessToken);
        dest.writeString(refreshToken);
        dest.writeString(nickname);
        dest.writeString(countryCode);
        dest.writeLong(latestLoginTime);
        dest.writeString(tz);
        dest.writeString(tempUnit);
        dest.writeString(userName);
        dest.writeLong(expiresIn);
        dest.writeString(authenticationIdentity);
    }

    public static final Creator<UserInfoBean> CREATOR = new Creator<UserInfoBean>(){
        @Override
        public UserInfoBean[] newArray(int size){
            return new UserInfoBean[size];
        }

        @Override
        public UserInfoBean createFromParcel(Parcel in){
            return new UserInfoBean(in);
        }
    };

    public UserInfoBean(Parcel in){
        //如果元素数据是list类型的时候需要： lits = new ArrayList<?> in.readList(list);
        //否则会出现空指针异常.并且读出和写入的数据类型必须相同.如果不想对部分关键字进行序列化,可以使用transient关键字来修饰以及static修饰.
        avatarUrl = in.readString();
        registryType = in.readInt();
        userType = in.readInt();
        email = in.readString();
        id = in.readString();
        tokenType = in.readString();
        phoneNumber = in.readString();
        accessToken = in.readString();
        refreshToken = in.readString();
        nickname = in.readString();
        countryCode = in.readString();
        latestLoginTime = in.readLong();
        tz = in.readString();
        tempUnit = in.readString();
        userName = in.readString();
        expiresIn = in.readLong();
        authenticationIdentity = in.readString();
    }
}
