package com.smart.rinoiot.center.bean.msg;

import java.io.Serializable;

/**
 * @Author : tw
 * @Time : On 2022/9/28 15:29
 * @Description : messageQueryDTO 消息列表请求参数实体类
 */
public class MessageQueryBean implements Serializable {
    private boolean asc;
    private int currentPage;
    private String orderByColumn;
    private int pageSize;
    private QueryConditionBean queryCondition;
    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public QueryConditionBean getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(QueryConditionBean queryCondition) {
        this.queryCondition = queryCondition;
    }
}
