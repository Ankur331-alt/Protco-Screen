package com.rino;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.agora.rtc2.LeaveChannelOptions;
import io.agora.rtc2.RtcConnection;

/**
 * @ProjectName: Rino Smart
 * @Package: com.rino
 * @ClassName: IPCBusinessPluginManager
 * @Description: java类作用描述
 * @Author: ZhangStar
 * @Emali: ZhangStar666@gmali.com
 * @CreateDate: 2023/8/12 14:47
 * @UpdateUser: 更新者：
 * @UpdateDate: 2023/8/12 14:47
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class IPCBusinessPluginManager {
    private static final String TAG = IPCBusinessPluginManager.class.getSimpleName();

    static IPCBusinessPluginManager sIPCBusinessPluginManager;

    public Map<String, IIPCBusinessPlugin> getIPCBusinessPlugins() {
        return mRinoIPCApiMap;
    }

    private Map<String, IIPCBusinessPlugin> mRinoIPCApiMap = new HashMap<>();
    public Map<String, Integer> uidMap = new HashMap<>();

    public Map<String, String> channelMap = new HashMap<>();

    public static IPCBusinessPluginManager create() {
        if (sIPCBusinessPluginManager == null) {
            sIPCBusinessPluginManager = new IPCBusinessPluginManager();
        }
        return sIPCBusinessPluginManager;
    }


    public IIPCBusinessPlugin getCurrentIPCApi(String id) {
        return mRinoIPCApiMap.get(id);
    }

    public void registerIPCApi(String id, IIPCBusinessPlugin rinoIPCApi) {
        assert id != null : "Param 'id' cannot be empty";
        mRinoIPCApiMap.put(id, rinoIPCApi);
    }

    public void registerIPCApi(String id, int uid, String channelName, IIPCBusinessPlugin rinoIPCApi) {
        assert id != null : "Param 'id' cannot be empty";
        mRinoIPCApiMap.put(id, rinoIPCApi);
        uidMap.put(id, uid);
        channelMap.put(id, channelName);
    }

    /**
     * 移除单个视频播放
     *
     * @param isSingle true：单个
     */
    public void unregisterIPCApi(String id, boolean isSingle) {
        IIPCBusinessPlugin removeItem = mRinoIPCApiMap.get(id);
        if (removeItem != null) {
            IIPCBusinessResult result = new IIPCBusinessResult() {
                @Override
                public void onSuccess(Object result) {

                }

                @Override
                public void onFailed(String cause, Throwable e) {

                }
            };
            if (uidMap.get(id) != null) {
                removeItem.leaveChannel(new RtcConnection(id, uidMap.get(id)), new LeaveChannelOptions(), result);
            } else {
                removeItem.leaveChannel(result);
            }
            if (isSingle) {
                if (mRinoIPCApiMap.size() == 1) {
                    removeItem.onDestroy();
                }
                mRinoIPCApiMap.remove(id);
                uidMap.remove(id);
                channelMap.remove(id);
            }

        }
    }

    public void unregisterAllIPCApi() {
        IIPCBusinessPlugin removeItem = null;
        for (String id : mRinoIPCApiMap.keySet()) {
            removeItem = mRinoIPCApiMap.get(id);
            unregisterIPCApi(id, false);
        }
        if (removeItem != null) {
            removeItem.onDestroy();
        }
        uidMap.clear();
        mRinoIPCApiMap.clear();
        channelMap.clear();
    }


    /**
     * 最后一帧拍照
     */
    public void snapshot(Context context, String id) {
        String channelName = channelMap.get(id);
        String filePath = context.getExternalCacheDir().getAbsolutePath() + "/ipc/center/" + id;
        Log.w(TAG, "3333333333333  snapshot    id=" + id + "   channelName=" + channelName+"  filePath="+filePath);
        File file = new File(filePath);
        if (!file.exists()) file.mkdirs();
        HashMap map = new HashMap<>();
        map.put("uid", 1);
        map.put("filePath", filePath + "/1.jpg");
        map.put("channelName", channelName);
        snapshot(id, map, null);
    }

    public void snapshot(String id, HashMap map, IIPCBusinessResult promise) {
        IIPCBusinessPlugin ipcBusinessPlugin = getCurrentIPCApi(id);
        if (ipcBusinessPlugin == null) {
            Log.w(TAG, "   IIPCBusinessPlugin   空");
            return;
        }
        ipcBusinessPlugin.snapshot(map, promise);
    }
}
