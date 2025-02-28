package com.rinoiot.iflytek.engine.model.asr;


import java.io.Serializable;

/**
 * @author edwin
 */
public class Data implements Serializable {
    private Result result;
    private int status;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}