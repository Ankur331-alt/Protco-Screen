package com.rino;

public interface IRinoIPCEventHandler {
    /**
     * 具体IPC业务的处理ide状态回调
     *
     * @param status
     */
    void onStatusChange(String status);

    /**
     * 具体IPC业务的异常状态回调
     *
     * @param errorCode 状态码
     */
    void onError(int errorCode,String errorMsg);

    /**
     * 第一阶段只封装onStatusChange和onError，因为三方sdk api的不同，暂不做统一。三方业务接口统一通过这个接口封装给到RN业务层。
     * 具体内部实现业务实现和结果转换交由具体 IIPCBusinessPlugin 处理
     *
     * @param business 三方业务处理后的结果，只接收String，不具体是否为json，具体交由三方业务使用情况，动态接入。
     */
    void onOtherBusiness(String business);
}
