package com.smart.rinoiot.common.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author tw
 * @time 2022/11/14 13:53
 * @description 群组实体类
 */
public class GroupBean implements Serializable {
    private String assetId;//资产ID
    private List<GroupDeviceItemBean> deviceList;//群组设备列表
    private long createTime;//创建时间
    private String id;//群组ID
    private String name;//群组名称
    private String userId;//用户ID
    private String productIds;//产品ID列表
    private String imageUrl;//群组图片

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public List<GroupDeviceItemBean> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<GroupDeviceItemBean> deviceList) {
        this.deviceList = deviceList;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductIds() {
        return productIds;
    }

    public void setProductIds(String productIds) {
        this.productIds = productIds;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
