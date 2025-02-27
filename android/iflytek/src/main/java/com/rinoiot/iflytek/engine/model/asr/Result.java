package com.rinoiot.iflytek.engine.model.asr;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
    private int sn;
    private boolean ls;
    private int bg;
    private int ed;
    private List<WordSegment> ws;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean isLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public List<WordSegment> getWs() {
        return ws;
    }

    public void setWs(List<WordSegment> ws) {
        this.ws = ws;
    }
}
