package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author edwin
 */
public class AppQrLogInMsgBean implements Serializable {

    /**
     * The user identifier.
     */
    private String userId;

    /**
     * The current asset identifier.
     */
    private String currentAssetId;

    /**
     * The user's nickname.
     */
    private String nickname;

    /**
     * The user's avatar.
     */
    private String avatarUrl;

    /**
     * The application identifier.
     */
    private String appId;

    /**
     * The client identifier.
     */
    private String clientId;

    /**
     *  The access token.
     */
    private String accessToken;

    /**
     * The refresh token.
     */
    private String refreshToken;

    /**
     * The user account's timezone.
     */
    private String tz;

    /**
     * The user account's area code.
     */
    private String areaCode;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrentAssetId() {
        return currentAssetId;
    }

    public void setCurrentAssetId(String currentAssetId) {
        this.currentAssetId = currentAssetId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}
