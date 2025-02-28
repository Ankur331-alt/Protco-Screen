package com.smart.rinoiot.common.bean;

import android.view.Gravity;

public class SelectItemBean {
    private int iconRes;
    private String title;
    private String tips;
    private boolean isClickable;
    private int textGravity = Gravity.START;
    private boolean isHideArrow;
    private boolean isShowLine = true;

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getTips() {
        return tips;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setTextGravity(int textGravity) {
        this.textGravity = textGravity;
    }

    public int getTextGravity() {
        return textGravity;
    }

    public void setHideArrow(boolean hideArrow) {
        isHideArrow = hideArrow;
    }

    public boolean isHideArrow() {
        return isHideArrow;
    }

    public void setShowLine(boolean showLine) {
        isShowLine = showLine;
    }

    public boolean isShowLine() {
        return isShowLine;
    }
}
