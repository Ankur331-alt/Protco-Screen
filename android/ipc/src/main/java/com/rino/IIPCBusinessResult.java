package com.rino;

/**
 * @ProjectName: Rino Smart
 * @Package: com.smart.rinoiot.common.rn.view.ipc
 * @ClassName: IIPCBusinessResult
 * @Description: java类作用描述
 * @Author: ZhangStar
 * @Emali: ZhangStar666@gmali.com
 * @CreateDate: 2023/6/13 15:18
 * @UpdateUser: 更新者：
 * @UpdateDate: 2023/6/13 15:18
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface IIPCBusinessResult {
    void onSuccess(Object result);

    void onFailed(String cause, Throwable e);
}
