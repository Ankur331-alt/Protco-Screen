package com.smart.rinoiot.center.bean.msg;

import com.chad.library.adapter.base.entity.SectionEntity;

import java.io.Serializable;

/**
 * @Author : tw
 * @Time : On 2022/9/28 16:38
 * @Description : MsgRecordItemBean 消息信息
 */
public class MsgRecordItemBean implements Serializable, SectionEntity {
    private String id;//消息id
    private int msgType;//消息类型（1=事件，2=通知，3=资产状态）
    private String title;//消息标题
    private String content;//消息内容
    private long createTime;//创建时间
    private boolean isReaded;//是否已读
    private String icon;//消息图标
    private String assetName;//家庭名称
    private int actionSource;//动作源(1=问题反馈，2=资产变化，3=自动化，4=一键执行，5=设备告警，6=设备操作，7=云定时)

    public int getActionSource() {
        return actionSource;
    }

    public void setActionSource(int actionSource) {
        this.actionSource = actionSource;
    }

    /**
     * 是否为头部bean
     */
    private boolean isHeader;
    /**
     * 头部文字
     */
    private String header;

    @Override
    public boolean isHeader() {
        return isHeader;
    }

    @Override
    public int getItemType() {
        return isHeader ? HEADER_TYPE : NORMAL_TYPE;
    }

    public void setIsHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
}
