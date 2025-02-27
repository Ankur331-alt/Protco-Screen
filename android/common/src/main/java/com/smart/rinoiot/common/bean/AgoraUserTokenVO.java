package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2023/8/11 17:49
 * @description 声网token 实体类
 */
public class AgoraUserTokenVO implements Serializable {
    private String agoraAppId;//	声网appId
    private AgoraRtcTokenVO rtcToken;//
    private AgoraRtmTokenVO rtmToken;//
    private String userId;//用户id

    public String getAgoraAppId() {
        return agoraAppId;
    }

    public void setAgoraAppId(String agoraAppId) {
        this.agoraAppId = agoraAppId;
    }

    public AgoraRtcTokenVO getRtcToken() {
        return rtcToken;
    }

    public void setRtcToken(AgoraRtcTokenVO rtcToken) {
        this.rtcToken = rtcToken;
    }

    public AgoraRtmTokenVO getRtmToken() {
        return rtmToken;
    }

    public void setRtmToken(AgoraRtmTokenVO rtmToken) {
        this.rtmToken = rtmToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
