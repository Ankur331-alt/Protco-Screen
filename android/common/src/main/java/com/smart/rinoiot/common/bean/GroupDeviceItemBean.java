package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/11/14 13:54
 * @description 群组下设备列表bean
 */
public class GroupDeviceItemBean implements Serializable {
    private String id;
    private String name;//设备名称名称
    private String productId;//产品ID
    private String imageUrl;//	产品图片

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
