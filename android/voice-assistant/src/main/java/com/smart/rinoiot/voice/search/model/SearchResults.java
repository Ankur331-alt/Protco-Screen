package com.smart.rinoiot.voice.search.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author edwin
 */
public class SearchResults {
    @SerializedName("status")
    private int status;

    @SerializedName("result")
    private String result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}