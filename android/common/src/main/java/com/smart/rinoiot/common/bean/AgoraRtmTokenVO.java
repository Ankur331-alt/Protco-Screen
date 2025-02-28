package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2023/8/11 17:53
 * @description 	rtm token
 */
public class AgoraRtmTokenVO implements Serializable {
    private String account;
    private int expireSecond;
    private String rtmToken;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getExpireSecond() {
        return expireSecond;
    }

    public void setExpireSecond(int expireSecond) {
        this.expireSecond = expireSecond;
    }

    public String getRtmToken() {
        return rtmToken;
    }

    public void setRtmToken(String rtmToken) {
        this.rtmToken = rtmToken;
    }
}
