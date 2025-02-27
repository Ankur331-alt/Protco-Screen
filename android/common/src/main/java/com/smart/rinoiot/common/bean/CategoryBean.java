package com.smart.rinoiot.common.bean;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CategoryBean {
    /**
     * 子节点
     */
    private List<CategoryBean> childrens;
    /**
     * 产品分类id
     */
    private String id;
    /**
     * 图片链接
     */
    private String imageUrl;
    /**
     * 产品类型名称
     */
    private String name;
    /**
     * 产品id
     */
    private String productId;
    /**
     * 父id
     */
    private String pid;
    /**
     * 通讯协议（1：WIFI+BLE，2：蓝牙Mesh（SIG），3：Zigbee，4：PLC，5：传统BLE，6：NB-IOT）
     */
    private int protocolType;
    /**
     * 支持的协议
     */
    private List<SupportProtocol> supportProtocolList;

    private boolean isSelect;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }

    public void setChildrens(List<CategoryBean> childrens) {
        this.childrens = childrens;
    }

    public List<CategoryBean> getChildrens() {
        return childrens;
    }

    public void setSupportProtocolList(List<SupportProtocol> supportProtocolList) {
        this.supportProtocolList = supportProtocolList;
    }

    public List<SupportProtocol> getSupportProtocolList() {
        return supportProtocolList;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public static class SupportProtocol implements SectionEntity, Serializable {

        private String id;
        /**
         * 产品ID
         */
        private String productId;
        /**
         * 协议名称
         */
        @SerializedName(value = "protocolName", alternate = "name")
        private String protocolName;
        /**
         * 通讯协议（1：WIFI+BLE，2：蓝牙Mesh（SIG），3：Zigbee，4：PLC，5：传统BLE，6：NB-IOT）
         */
        private String protocolType;
        /**
         * 图片链接
         */
        @SerializedName(value = "protocolImageUrl", alternate = "icon")
        private String protocolImageUrl;

        private boolean isHeader;

        private boolean isSelect;
        /**
         * status 配网状态 1: 不在配网状态；0: 在配网状态
         */
        private boolean canNetStatus;

        /**
         * 二级头部数据
         */
        private String towHeaserName;
        private String protocolTypeName;//协议名称

        public String getProtocolTypeName() {
            return protocolTypeName;
        }

        public void setProtocolTypeName(String protocolTypeName) {
            this.protocolTypeName = protocolTypeName;
        }

        public String getTowHeaserName() {
            return towHeaserName;
        }

        public void setTowHeaserName(String towHeaserName) {
            this.towHeaserName = towHeaserName;
        }

        public String getProtocolType() {
            return protocolType;
        }

        public void setProtocolType(String protocolType) {
            this.protocolType = protocolType;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProtocolName() {
            return protocolName;
        }

        public void setProtocolName(String protocolName) {
            this.protocolName = protocolName;
        }

        public String getProtocolImageUrl() {
            return protocolImageUrl;
        }

        public void setProtocolImageUrl(String protocolImageUrl) {
            this.protocolImageUrl = protocolImageUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public boolean isHeader() {
            return isHeader;
        }

        public void setHeader(boolean header) {
            isHeader = header;
        }

        @Override
        public int getItemType() {
            if (isHeader()) {
                return HEADER_TYPE;
            } else {
                // 拷贝 重写此处，返回自己的多布局类型
                return NORMAL_TYPE;
            }
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public boolean isCanNetStatus() {
            return canNetStatus;
        }

        public void setCanNetStatus(boolean canNetStatus) {
            this.canNetStatus = canNetStatus;
        }
    }
}
