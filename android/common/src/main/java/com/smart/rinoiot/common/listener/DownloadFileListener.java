package com.smart.rinoiot.common.listener;

/**
 * 下载监听
 *
 * @Package: com.znkit.smart.listener
 * @ClassName: DownloadApkListener
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2021/3/29 2:26 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/3/29 2:26 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface DownloadFileListener {
    void onStart();

    void onProgress(int progress);

    void onFinish(String path);

    void onError(String msg);
}
