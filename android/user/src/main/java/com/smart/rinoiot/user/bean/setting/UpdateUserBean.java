package com.smart.rinoiot.user.bean.setting;

import java.io.Serializable;

/**
 * @Author : tw
 * @Time : On 2022/9/29 17:47
 * @Description : updateUserDTBean 修改用户头像及昵称
 */
public class UpdateUserBean implements Serializable {
    private  String avatarUrl;//头像地址
    private  String nickname;//昵称
    private  String tz;//时区

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }
}
