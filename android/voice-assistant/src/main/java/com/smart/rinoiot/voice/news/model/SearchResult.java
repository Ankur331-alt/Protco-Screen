package com.smart.rinoiot.voice.news.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author edwin
 */
public class SearchResult {
    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    @SerializedName("href")
    private String href;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}