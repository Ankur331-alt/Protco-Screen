package com.smart.rinoiot.common.listener;

/**
 * 列表选中，确定返回选中结果
 */

public interface DialogListener {
    void onConfirm(String text,int position);
}
