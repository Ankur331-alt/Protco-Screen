package com.smart.rinoiot.common.listener;

/**
 * 回调数据给rn监听
 *
 * @Package: com.renpho.rn.api
 * @ClassName: EventListener
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2021/7/8 2:10 下午
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/7/8 2:10 下午
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface EventListener {
    /**
     * 数据监听回调
     *
     * @param data 数据集合
     */
    void onDataChange(String eventName,Object data);
}
