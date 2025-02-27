package com.smart.rinoiot.center.bean.msg;

import java.io.Serializable;

/**
 * @Author : tw
 * @Time : On 2022/9/28 16:45
 * @Description : QueryConditionBean 消息列表 消息类型
 */
public class QueryConditionBean implements Serializable {
    private int msgType;//	消息类型（1=事件，2=通知，3=资产状态）

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}
