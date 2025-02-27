package com.rinoiot.iflytek.engine.model.asr;

import java.io.Serializable;

/**
 * @author edwin
 */
public class AsrResponse implements Serializable {
    private Data data;
    private String sid;
    private String code;
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
