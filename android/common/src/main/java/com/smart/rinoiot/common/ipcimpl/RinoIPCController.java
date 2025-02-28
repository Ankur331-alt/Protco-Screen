package com.smart.rinoiot.common.ipcimpl;

import android.util.Log;

import com.rino.IIPCBusinessPlugin;
import com.rino.IIPCBusinessResult;
import com.rino.IPCBusinessPluginManager;

import java.util.HashMap;

public class RinoIPCController {

    private static RinoIPCController instance;

    private RinoIPCController() {
    }

    public static RinoIPCController getInstance() {
        if (instance == null) {
            instance = new RinoIPCController();
        }
        return instance;
    }


    public void initIPC(String id, HashMap map, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.init(map, promise);
    }


    public void putDeviceData(String id, String jsonValue, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.sendDeviceData(jsonValue, promise);
    }


    public void pullStream(String id, String jsonValue, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.pull(jsonValue, promise);
    }


    public void enableTalk(String id, boolean enable, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.enableTalk(enable, promise);
    }


    public void enableVideo(String id, boolean enable, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.enableVideo(enable, promise);
    }


    public void enableVideoWithParams(String id, HashMap map, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.enableVideoWithParams(map, promise);
    }


    public void connect(String id, HashMap map, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.connect(map, promise);
    }


    public void queryStatus(String id, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin instanceof AgoraBusinessPlugin) {
            promise.onSuccess(((AgoraBusinessPlugin) ipcBusinessPlugin).status);
        } else {
            promise.onFailed("", new Throwable("queryStatus failed"));
        }

    }


    public void disconnect(String id, HashMap map, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.disconnect(map, promise);
    }


    public void openCloudStorage(String url, HashMap map) {
//        HashMap intentMap = map;
//        Intent mIntent = new Intent(this.getCurrentActivity(), BridgeWebViewActivity.class);
//        mIntent.putExtra(BridgeConstant.WEB_URL, ServiceConfigConstant.BASE_URL_H5 + url);
//        mIntent.putExtra(BridgeConstant.WEB_VIEW_PARAMS, intentMap);
//        this.getCurrentActivity().startActivity(mIntent);
    }


    public void snapshot(String id, HashMap map, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.snapshot(map, promise);
    }


    public void startRecording(String id, HashMap map, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.startRecording(map, promise);
    }


    public void stopRecording(String id, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.stopRecording(promise);
    }


    public void muteLocal(String id, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.muteLocal(promise);
    }


    public void muteRemote(String id, HashMap map, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.muteRemote(map, promise);
    }


    public void setParameters(String id, String parameters, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.e("IIPCBusinessPlugin", "空");
            return;
        }
        ipcBusinessPlugin.setParameters(parameters, promise);
    }


    public void releasePlayView(String id, String role, IIPCBusinessResult promise) {
        try {
            Log.e("AgoraBusinessPlugin", "Plugins 释放前：" + IPCBusinessPluginManager.create().getIPCBusinessPlugins().isEmpty());
            if (!IPCBusinessPluginManager.create().getIPCBusinessPlugins().isEmpty()) {
                for (String key : IPCBusinessPluginManager.create().getIPCBusinessPlugins().keySet()) {
                    IIPCBusinessPlugin mRinoSDK = IPCBusinessPluginManager.create().getIPCBusinessPlugins().get(key);
                    if (mRinoSDK != null) {
                        mRinoSDK.onDestroy();
                    }
                }
                IPCBusinessPluginManager.create().getIPCBusinessPlugins().clear();
            }
            Log.e("AgoraBusinessPlugin", "Plugins释放后：" + IPCBusinessPluginManager.create().getIPCBusinessPlugins().isEmpty());
            if (promise != null) {
                promise.onSuccess(true);
            }
        } catch (Exception e) {
            if (promise != null) {
                promise.onFailed(e.getMessage(), e);
            }
        }

//        IIPCBusinessPlugin ipcBusinessPlugin = IPCBusinessPluginManager.create().getCurrentIPCApi(id);
//        ipcBusinessPlugin.muteRemote(map, new BusinessResult(promise));
    }
}
