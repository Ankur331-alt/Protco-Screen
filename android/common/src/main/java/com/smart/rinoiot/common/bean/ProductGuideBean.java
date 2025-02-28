package com.smart.rinoiot.common.bean;

import java.io.Serializable;

public class ProductGuideBean implements Serializable {
    /** 删除标识 */
    private int deleted;
    /** 外部url地址（跳转到重置设备的教程页面） */
    private String documentUrl;
    /** 使用类型(1：类型，2：产品) */
    private int forType;
    /** 配网类型（1:蓝牙，2:EZ，3:AP） */
    private int networkType;
    /** 状态（0禁用，1启用） */
    private String status;
    /** 步骤json */
    private String stepJson;

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public int getForType() {
        return forType;
    }

    public void setForType(int forType) {
        this.forType = forType;
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStepJson() {
        return stepJson;
    }

    public void setStepJson(String stepJson) {
        this.stepJson = stepJson;
    }

    public static class StepBean {
        private String title;
        /** ctKey */
        private String keyName;
        /** 资源链接 */
        private String url;
        /**  */
        private int type;
        /** 音频地址 */
        private String radioUrl;
        /** 1 */
        private String copyType;
        /** 文案地址 */
        private String documentUrl;
        /** 内容描述 */
        private String describe;

        public String getKeyName() {
            return keyName;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getRadioUrl() {
            return radioUrl;
        }

        public void setRadioUrl(String radioUrl) {
            this.radioUrl = radioUrl;
        }

        public String getCopyType() {
            return copyType;
        }

        public void setCopyType(String copyType) {
            this.copyType = copyType;
        }

        public String getDocumentUrl() {
            return documentUrl;
        }

        public void setDocumentUrl(String documentUrl) {
            this.documentUrl = documentUrl;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
