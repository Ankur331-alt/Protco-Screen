package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2023/8/11 17:52
 * @description
 */
public class AgoraRtcTokenVO implements Serializable {
    private String channelName;//通道名
    private int expireSecond;//过期时间(秒)
    private String rtcToken;//rtc token
    private String uid;//声网uid

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getExpireSecond() {
        return expireSecond;
    }

    public void setExpireSecond(int expireSecond) {
        this.expireSecond = expireSecond;
    }

    public String getRtcToken() {
        return rtcToken;
    }

    public void setRtcToken(String rtcToken) {
        this.rtcToken = rtcToken;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
