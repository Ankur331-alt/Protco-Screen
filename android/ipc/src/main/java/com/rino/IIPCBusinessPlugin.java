package com.rino;

import android.view.SurfaceView;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: Rino Smart
 * @Package: com.smart.rinoiot.common.rn.view
 * @ClassName: IIPCPlugin
 * @Description: IPC业务接口定义，主要用于横向关联三方IPC SDK。
 * @Author: ZhangStar
 * @Emali: ZhangStar666@gmali.com
 * @CreateDate: 2023/5/27 16:51
 * @UpdateUser: 更新者：
 * @UpdateDate: 2023/5/27 16:51
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */


public interface IIPCBusinessPlugin extends IIPCBusiness {
    void putSurfaceView(String key, SurfaceView surfaceView, IRinoIPCEventHandler eventHandler);
}
