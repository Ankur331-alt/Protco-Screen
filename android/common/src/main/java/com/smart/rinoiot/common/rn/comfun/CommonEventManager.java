package com.smart.rinoiot.common.rn.comfun;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.smart.rinoiot.common.listener.EventListener;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.LgUtils;

/**
 * 事件分发管理类
 *
 * @Package: com.renpho.rn.modules.event
 * @ClassName: EventManager
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2021/7/8 2:15 下午
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/7/8 2:15 下午
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CommonEventManager {
    private static CommonEventManager instance;
    private EventListener eventListener;

    public static synchronized CommonEventManager getInstance() {
        if (instance == null) {
            instance = new CommonEventManager();
        }
        return instance;
    }

    /**
     * 设置监听
     *
     * @param eventListener 监听
     */
    public void setOnEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * 删除监听
     */
    public void removeListener() {
        this.eventListener = null;
    }

    /**
     * 发送数据给rn
     *
     * @param eventName
     * @param writableArray
     */
    public void sendData(String eventName, WritableArray writableArray) {
        AppExecutors.getInstance().mainThread().execute(() -> {
            if (eventListener == null) {
                return;
            }
            eventListener.onDataChange(eventName, writableArray);
        });
    }


    /**
     * 发送数据给rn
     *
     * @param eventName
     * @param writableMap
     */
    public void sendData(String eventName, WritableMap writableMap) {
//        if (eventListener == null) {
//            return;
//        }
        AppExecutors.getInstance().mainThread().execute(() -> {
            if (eventListener == null) {
                LgUtils.w("CommonEventManager   eventListener=null");
                return;
            }
            eventListener.onDataChange(eventName, writableMap);
        });
    }
}
