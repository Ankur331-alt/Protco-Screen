package com.smart.rinoiot.center.bean.msg;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/12/8 11:02
 * @description 未读消息
 */
public class UnreadBean implements Serializable {
    private Integer eventCount;
    private Integer noticeCount;

    public Integer getEventCount() {
        return eventCount;
    }

    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    public Integer getNoticeCount() {
        return noticeCount;
    }

    public void setNoticeCount(Integer noticeCount) {
        this.noticeCount = noticeCount;
    }
}
