package com.smart.rinoiot.common.listener;

/**
 * @Package: com.wq.lib.base.api
 * @ClassName: ImageLoadListener.java
 * @Description: 图片加载监听
 * @Author: xf
 * @CreateDate: 2020/3/29 17:49
 * @UpdateUser: 更新者：xf
 * @UpdateDate: 2020/3/29 17:49
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface ImageLoadListener<T> {
    /**
     * 图片下载成功
     *
     * @param t 加载成功返回数据
     * @return void
     * @method onSuccess
     * @description 加载成功
     * @date: 2020/3/29 17:49
     * @author: xf
     */
    void onSuccess(T t);

    /**
     * * 图片下载失败
     *
     * @param e 返回错误异常
     * @return void
     * @method onError
     * @description 加载失败
     * @date: 2020/3/29 17:50
     * @author: xf
     */
    void onError(Exception e);
}
