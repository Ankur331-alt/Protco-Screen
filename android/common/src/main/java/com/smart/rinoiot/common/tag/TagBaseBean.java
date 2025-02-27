package com.smart.rinoiot.common.tag;

import java.io.Serializable;

/**TAGAdapter 设置一个基类，所有自定义的类必须继承这个基类*/
public class TagBaseBean implements Serializable {
    private String tagName;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
