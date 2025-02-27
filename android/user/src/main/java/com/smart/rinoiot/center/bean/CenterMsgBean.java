package com.smart.rinoiot.center.bean;

import com.smart.rinoiot.common.bean.UserInfoBean;

/**
 * @author tw
 * @time 2022/12/22 15:12
 * @description
 */
public class CenterMsgBean {
    private String webSocketClientId;
    private UserInfoBean userInfoBean;

    private String currentAssetId;

    public String getCurrentAssetId() {
        return currentAssetId;
    }

    public void setCurrentAssetId(String currentAssetId) {
        this.currentAssetId = currentAssetId;
    }

    public String getWebSocketClientId() {
        return webSocketClientId;
    }

    public void setWebSocketClientId(String webSocketClientId) {
        this.webSocketClientId = webSocketClientId;
    }

    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }

    public void setUserInfoBean(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
    }
}
