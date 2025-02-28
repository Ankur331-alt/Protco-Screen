package com.rinoiot.iflytek.engine.model.asr;

import java.io.Serializable;
import java.util.List;

public class WordSegment implements Serializable {
    private int bg;
    private List<Word> cw;

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public List<Word> getCw() {
        return cw;
    }

    public void setCw(List<Word> cw) {
        this.cw = cw;
    }
}
