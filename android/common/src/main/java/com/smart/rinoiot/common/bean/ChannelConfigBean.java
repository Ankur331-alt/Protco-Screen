package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2023/3/24 16:58
 * @description app渠道地址
 */
public class ChannelConfigBean implements Serializable {
    private String channelId;//渠道名或id
    private String channelUrl;//渠道下载地址

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }
}
