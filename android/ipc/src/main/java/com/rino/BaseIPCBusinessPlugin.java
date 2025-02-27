package com.rino;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import java.util.HashMap;
import java.util.Map;

import static com.rino.IPCConstant.IPC_CONNECT_STATUS_INITIALIZATION;
import static com.rino.IPCConstant.IPC_CONNECT_STATUS_PREPARING;

public abstract class BaseIPCBusinessPlugin implements IIPCBusinessPlugin {
    public Context context;
    public SurfaceView surfaceView;
    public IRinoIPCEventHandler eventHandler;

    public Map<String, SurfaceView> mSurfaceViewMap;
    public Map<String, IRinoIPCEventHandler> mEventHandlerMap;
    //插件对应状态，默认为准备中
    public String status = IPC_CONNECT_STATUS_INITIALIZATION;

    public BaseIPCBusinessPlugin(Context context, SurfaceView surfaceView, IRinoIPCEventHandler eventHandler) {
        this.context = context;
//        this.surfaceView = surfaceView;
//        this.eventHandler = eventHandler;
        mSurfaceViewMap = new HashMap<>();
        mEventHandlerMap = new HashMap<>();
        status = IPC_CONNECT_STATUS_INITIALIZATION;
    }

    public BaseIPCBusinessPlugin(Context context) {
        this.context = context;
        mSurfaceViewMap = new HashMap<>();
        mEventHandlerMap = new HashMap<>();
    }

    @Override
    public void putSurfaceView(String key, SurfaceView surfaceView, IRinoIPCEventHandler eventHandler) {
        mSurfaceViewMap.put(key, surfaceView);
        mEventHandlerMap.put(key, eventHandler);
    }

    public void onStatusChange(String key, String status) {
        Log.e("AgoraBusinessPlugin", "onStatusChange this:" + this);
        Log.e("AgoraBusinessPlugin", "key:" + key + ",status" + status);
        Log.e("AgoraBusinessPlugin", "mEventHandlerMap:" + mEventHandlerMap);
        if (key == null) {
            for (String mapKey : mEventHandlerMap.keySet()) {
                IRinoIPCEventHandler mIRinoIPCEventHandler = mEventHandlerMap.get(mapKey);
                mIRinoIPCEventHandler.onStatusChange(status);
            }
        } else {
            IRinoIPCEventHandler mIRinoIPCEventHandler = mEventHandlerMap.get(key);
            mIRinoIPCEventHandler.onStatusChange(status);
        }
    }

    public void onError(String key, int errorCode, String errorMsg) {
        if (key == null) {
            for (String mapKey : mEventHandlerMap.keySet()) {
                IRinoIPCEventHandler mIRinoIPCEventHandler = mEventHandlerMap.get(mapKey);
                mIRinoIPCEventHandler.onError(errorCode, errorMsg);
            }
        } else {
            IRinoIPCEventHandler mIRinoIPCEventHandler = mEventHandlerMap.get(key);
            mIRinoIPCEventHandler.onError(errorCode, errorMsg);
        }
    }

    public void onOtherBusiness(String key, String business) {
        if (key == null) {
            for (String mapKey : mEventHandlerMap.keySet()) {
                IRinoIPCEventHandler mIRinoIPCEventHandler = mEventHandlerMap.get(mapKey);
                mIRinoIPCEventHandler.onOtherBusiness(business);
            }
        } else {
            IRinoIPCEventHandler mIRinoIPCEventHandler = mEventHandlerMap.get(key);
            if (mIRinoIPCEventHandler != null) {
                mIRinoIPCEventHandler.onOtherBusiness(business);
            } else {
                Log.w("BaseIPCBusinessPlugin", "没有找到对应的处理回调类");
            }

        }
    }


    public void removeView(String key, SurfaceView view) {
        mSurfaceViewMap.remove(key);
        mEventHandlerMap.remove(key);
    }
}
