package com.rinoiot.iflytek.engine.model.asr;

import java.io.Serializable;

public class Word implements Serializable {
    private int sc;
    private String w;

    public int getSc() {
        return sc;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }
}
