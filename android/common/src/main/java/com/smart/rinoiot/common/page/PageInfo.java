package com.smart.rinoiot.common.page;

/**
 * 适配器加载分页
 */
public class PageInfo {

    private int page = 1;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void nextPage() {
        page++;
    }

    public void reset() {
        page = 1;
    }

    public boolean isFirstPage() {
        return page == 1;
    }

    public void cloudReset() {
        page = 0;
    }
}
