package com.smart.rinoiot.common.ipcimpl;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.rino.BaseIPCBusinessPlugin;
import com.rino.IIPCBusinessPlugin;
import com.rino.IPCBusinessPluginManager;
import com.rino.IPCConstant;
import com.rino.IRinoIPCEventHandler;
import com.rino.business.ShangYunBusinessPlugin;

/**
 * @Author: ZhangStar
 */
public class RinoIPCPlayView extends SurfaceView implements SurfaceHolder.Callback, IRinoIPCEventHandler {
    private static final String TAG = RinoIPCPlayView.class.getSimpleName();
    private Context context;

    private IIPCBusinessPlugin ipcBusinessPlugin;
    private String id;

    private int uid;
    private String solution;//ShangYun, Agora
    private String role;

    private String name;

    private IPCPlayCallback mIPCPlayCallback;

    private String channelName;//渠道名称

    private boolean isOpen = false;//是否关闭

    public RinoIPCPlayView(Context context, String id, String role, String solution, IPCPlayCallback mIPCPlayCallback, String name) {
        super(context);
        this.name = name;
        this.id = id;
        this.role = role;
        this.solution = solution;
        this.context = context;
        this.mIPCPlayCallback = mIPCPlayCallback;
        this.onStatusChange(IPCConstant.IPC_CONNECT_STATUS_PREPARING);
        getHolder().addCallback(this);
        Log.e(TAG, "创建RinoIPCPlayView:" + this);
    }

    public RinoIPCPlayView(Context context, int uid, String id, String role, String solution, IPCPlayCallback mIPCPlayCallback, String name, String channelName) {
        super(context);
        isOpen = true;
        this.name = name;
        this.uid = uid;
        this.channelName = channelName;
        this.id = id;
        this.role = role;
        this.solution = solution;
        this.context = context;
        this.mIPCPlayCallback = mIPCPlayCallback;
        this.onStatusChange(IPCConstant.IPC_CONNECT_STATUS_PREPARING);
        getHolder().addCallback(this);
        Log.e(TAG, "创建RinoIPCPlayView:" + this + "    isOpen=" + isOpen);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated:" + this + "    isOpen=" + isOpen);
        this.create(this.id);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed:" + this + "    isOpen=" + isOpen);
        BaseIPCBusinessPlugin mBaseIPCBusinessPlugin = (BaseIPCBusinessPlugin) IPCBusinessPluginManager.create().getCurrentIPCApi(this.id);
        if (mBaseIPCBusinessPlugin != null) {
            mBaseIPCBusinessPlugin.removeView(this.role, this);
        }
        isOpen = false;
    }

    @Override
    public void onStatusChange(String status) {
        mIPCPlayCallback.onStatusChange(status);
//        HashMap params = new HashMap();
//        if (status.equals(IPCConstant.IPC_CONNECT_STATUS_INITIALIZATION)) {
//            params.put("appId", "");
//            RinoIPCController.getInstance().initIPC(this.id, params, null);
//        }
//        if (!this.role.equals("push") && status.equals(IPCConstant.IPC_CONNECT_STATUS_CONNECTING)) {
//            RinoIPCController.getInstance().connect(id,);
//        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {

    }

    @Override
    public void onOtherBusiness(String business) {

    }

    private void create(String id) {
        //必须指定协议类型才可以
        if (this.solution != null && isOpen) {
            Log.e("AgoraBusinessPlugin:" + name, "开始创建" + name + "的Plugin");
            Log.e("AgoraBusinessPlugin:" + name, "Name:" + name + ",id:" + id);
            ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
            if (ipcBusinessPlugin == null) {
                Log.e("AgoraBusinessPlugin:" + name, "没有命中缓存插件，重新创建");
                ipcBusinessPlugin = getPluginWithType(id, solution, name);
                IPCBusinessPluginManager.create().registerIPCApi(id, uid, channelName, ipcBusinessPlugin);
            }
            //存在的话，则代表当前通道存在多个View，通过key管理起来
            ipcBusinessPlugin.putSurfaceView(role, this, this);
            //因为插件不会随着view的创建而销毁，所以直接取plugin的状态
            this.onStatusChange(((BaseIPCBusinessPlugin) ipcBusinessPlugin).status);
        }
    }


    private BaseIPCBusinessPlugin getPluginWithType(String id, String type, String name) {
        if (type.equals("ShangYun")) {
            return new ShangYunBusinessPlugin(context, this, this);
        }
        if (type.equals("Agora")) {
            return new AgoraBusinessPlugin(context, id, name);
        }
        return null;
    }


    public void setRole(String role) {
        this.role = role;
        if (this.id != null && this.role != null) {
            this.create(id);
        }
    }

    public void setId(String id) {
        if (this.id != null && !this.id.equals(id)) {
            //React 上层修改设备ID，上一个ID对应的相关组件和IPC类要准备释放，并且重新创建一个流（ID变了，流的地址也变了）
            this.release(this.id);
            this.create(id);
        }
        this.id = id;
        if (this.id != null && this.role != null) {
            this.create(id);
        }
    }

    public void setSolution(String type) {
        if (this.solution != null && !this.solution.equals(type)) {
            //React 上层修改IPC type，上一个type对应的相关组件和IPC类要准备释放，并且重新创建一个流（type变了，流的地址也变了,协议也变了）
            this.release(this.id);
            this.create(id);
        }
        this.solution = type;
    }

    private void release(String id) {
        IPCBusinessPluginManager.create().unregisterIPCApi(id, true);
    }

    public interface IPCPlayCallback {
        void onStatusChange(String status);
    }
}
