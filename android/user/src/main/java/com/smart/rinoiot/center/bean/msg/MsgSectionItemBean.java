package com.smart.rinoiot.center.bean.msg;

import com.chad.library.adapter.base.entity.SectionEntity;

import java.io.Serializable;

/**
 * @Author : tw
 * @Time : On 2022/9/28 17:21
 * @Description : MsgSectionItemBean 消息列表转化数据实体类
 */
public class MsgSectionItemBean implements Serializable, SectionEntity {
    private MsgRecordItemBean msgRecordItemBean;
    private long msgCreateTime;//用于判断相同加载更多数据时，数据重置

    public long getMsgCreateTime() {
        return msgCreateTime;
    }

    public void setMsgCreateTime(long msgCreateTime) {
        this.msgCreateTime = msgCreateTime;
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
        if (this.isHeader()) {
            return 1;
        } else {
            return 2;
        }
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

    public MsgRecordItemBean getMsgRecordItemBean() {
        return msgRecordItemBean;
    }

    public void setMsgRecordItemBean(MsgRecordItemBean msgRecordItemBean) {
        this.msgRecordItemBean = msgRecordItemBean;
    }
}
