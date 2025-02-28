package com.smart.rinoiot.common.ipcimpl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rino.IIPCBusinessResult;
import com.rino.IPCConstant;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.api.IPCService;
import com.smart.rinoiot.common.bean.AgoraRtcTokenVO;
import com.smart.rinoiot.common.bean.AgoraUserTokenVO;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.LgUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RinoIPCContainerView extends FrameLayout implements RinoIPCPlayView.IPCPlayCallback {
    private static final String TAG = "AgoraBusinessPlugin";
    private TextView statusText;
    private View mStatusView;

    private ProgressBar mStatusProgressBar;
    private AgoraUserTokenVO mAgoraUserTokenVO;

    private String id;

    private String role = "1";
    private RinoIPCPlayView mRinoIPCPlayView;

    private String deviceName;

    private String parameters;
    private boolean enableAudio;


    public interface OnUIDCallBack {
        void call(String uid);
    }

    public RinoIPCContainerView(@NonNull Context context) {
        super(context);
        init(null);
    }


    public RinoIPCContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RinoIPCContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public RinoIPCContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        mStatusView = LayoutInflater.from(getContext()).inflate(R.layout.layout_ipc_container_status, this);
        statusText = mStatusView.findViewById(R.id.ipc_container_status_text);
        mStatusProgressBar = mStatusView.findViewById(R.id.ipc_container_status_progress_bar);
        statusText.setText("加载中");
    }


    public void fetchToken(String deviceId, String devName, String parameters, boolean enableAudio, OnUIDCallBack callBack) {
        DeviceInfoBean currentDev = CacheDataManager.getInstance().getDeviceInfo(deviceId);
        if (currentDev.getOnlineStatus() == 0) {
            mStatusProgressBar.setVisibility(View.GONE);
            statusText.setText("设备已离线");
            return;
        }
        this.deviceName = devName;
        this.id = deviceId;
        this.parameters = parameters;
        this.enableAudio = enableAudio;
        Map<String, Object> params = new HashMap<>();
        params.put("agoraModel", "live");
        params.put("deviceId", deviceId);
        RetrofitUtils.getService(IPCService.class).getAgoraToken(params).enqueue(new BaseRequestListener<AgoraUserTokenVO>() {
            @Override
            public void onResult(AgoraUserTokenVO result) {
                mAgoraUserTokenVO = result;
                if (callBack != null) {
                    callBack.call(result.getRtcToken().getUid());
                }
                notifyDeviceJoin(getAgoraJoinParams(deviceId, mAgoraUserTokenVO), Integer.parseInt(mAgoraUserTokenVO.getRtcToken().getUid()),mAgoraUserTokenVO.getRtcToken().getChannelName());
            }

            @Override
            public void onError(String error, String msg) {
            }
        });
    }

    /**
     * * 通知设备加入频道
     * <p>
     * channelName	通道名称		string
     * connectModel	连接模式(1-预连接,2-直播推流,默认直播推流)		integer
     * deviceId	设备ID		string
     * rawData	透传数据		string
     * timeoutMilliSecond	超时时间(毫秒级,默认60秒)		integer
     */
    private void notifyDeviceJoin(Map<String, Object> params, int uid,String channelName) {
        RetrofitUtils.getService(IPCService.class).notifyDeviceJoin(params).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                //通知设备加入成功，开始推流
                //展示play view 播放
                mRinoIPCPlayView = new RinoIPCPlayView(getContext(),uid, id, role, "Agora", RinoIPCContainerView.this, deviceName,channelName);
                RinoIPCContainerView.this.removeAllViews();
                RinoIPCContainerView.this.addView(mRinoIPCPlayView);
                mStatusView = LayoutInflater.from(getContext()).inflate(R.layout.layout_ipc_container_status, null);
                statusText = mStatusView.findViewById(R.id.ipc_container_status_text);
                statusText.setText("加载中");
                RinoIPCContainerView.this.addView(mStatusView);
            }

            @Override
            public void onError(String error, String msg) {

            }
        });
    }


    /**
     * 获取通知设备加入频道 参数
     */
    private Map<String, Object> getAgoraJoinParams(String devId, AgoraUserTokenVO agoraUserTokenVO) {
        Map<String, Object> params = new HashMap<>();
        AgoraRtcTokenVO rtcToken = agoraUserTokenVO.getRtcToken();
        if (agoraUserTokenVO != null) {
            if (rtcToken != null) {
                params.put("channelName", rtcToken.getChannelName());
                params.put("rawData", getRawData(rtcToken));
            }
        }
        params.put("connectModel", 2);
        params.put("deviceId", devId);
        params.put("timeoutMilliSecond", 60000);
        return params;
    }


    /**
     * 获取透传数据
     * {
     * agoraModel: 'live',
     * deviceId: RinoSDK.device.deviceInfo.devId,
     * channel: 'ALL',
     * channelName: channelName,
     * rawData: JSON.stringify({
     * uid: uid,
     * cmdType: 'start_liveplay',
     * ack: 1,
     * streamtype: 0, //码流选择
     * ChannelName: channelName,
     * data: {
     * width: 1920,
     * height: 1080,
     * format: 1,
     * },
     * }),
     * }
     */
    private String getRawData(AgoraRtcTokenVO rtcToken) {
        JSONObject rawData = new JSONObject();
        try {
            rawData.put("uid", rtcToken.getUid());
            rawData.put("cmdType", "start_liveplay");
            rawData.put("ack", 1);
            rawData.put("streamtype", 0);
            rawData.put("ChannelName", rtcToken.getChannelName());
            JSONObject data = new JSONObject();
            data.put("width", 1920);
            data.put("height", 1080);
            data.put("format", 1);
            rawData.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.w(TAG, "    getRawData   e=" + e.getMessage());
        } finally {
            return rawData.toString();
        }
    }

    private void connect(IIPCBusinessResult result){
        HashMap params = new HashMap();
        AppExecutors.getInstance().mainThread().execute(() -> statusText.setText("连接中"));
        params.put("rtmToken", mAgoraUserTokenVO.getRtmToken().getRtmToken());
        params.put("rtcToken", mAgoraUserTokenVO.getRtcToken().getRtcToken());
        params.put("channelName", mAgoraUserTokenVO.getRtcToken().getChannelName());
        params.put("uid", mAgoraUserTokenVO.getRtcToken().getUid());
        params.put("multipleChannel", true);
        RinoIPCController.getInstance().connect(id, params, result);
    }

    @Override
    public void onStatusChange(String status) {
        LgUtils.e(TAG+" onStatusChange: "+status);
        HashMap params = new HashMap();
        if (status.equals(IPCConstant.IPC_CONNECT_STATUS_INITIALIZATION)) {
            params.put("appId", mAgoraUserTokenVO.getAgoraAppId());
            params.put("enableAudio", enableAudio);
            if (parameters != null) {
                params.put("parameters", parameters);
            }
            RinoIPCController.getInstance().initIPC(this.id, params, null);
        }
        if (!this.role.equals("push") && status.equals(IPCConstant.IPC_CONNECT_STATUS_CONNECTING)) {
            connect(new IIPCBusinessResult() {
                @Override
                public void onSuccess(Object result) {
                    Log.e(TAG, "发送连接指令成功");
                }

                @Override
                public void onFailed(String cause, Throwable e) {
                    Log.e(TAG, "发送连接指令失败：" + e);
                }
            });
        }
        if (status.equals(IPCConstant.IPC_CONNECT_STATUS_CONNECTED)) {
            Log.e("AgoraBusinessPlugin:" + deviceName, "status : " + status + ",开始拉流");
            JSONObject business = new JSONObject();
            try {
                business.put("remoteUid", Integer.valueOf(this.role));
                RinoIPCController.getInstance().pullStream(id, business.toString(), new IIPCBusinessResult() {
                    @Override
                    public void onSuccess(Object result) {
                    }

                    @Override
                    public void onFailed(String cause, Throwable e) {
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        if (status.equals(IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING)) {
            AppExecutors.getInstance().mainThread().execute(() -> {
                Log.e(TAG, "设置设备名字" + deviceName);
                mStatusView.setVisibility(View.GONE);
//                mStatusView.setBackgroundColor(Color.RED);
//                mStatusProgressBar.setVisibility(View.GONE);
//                statusText.setText(deviceName);
            });
        }
    }

    /**
     * 最后一帧拍照
     */
    public void snapshot() {
        LgUtils.w("3333333333333  snapshot    ");
        if (mAgoraUserTokenVO == null || mAgoraUserTokenVO.getRtcToken() == null) return;
        String channelName = mAgoraUserTokenVO.getRtcToken().getChannelName();
        String filePath = getContext().getExternalCacheDir().getAbsolutePath() + "/ipc/center/" + id;
        Log.w(TAG, "3333333333333  snapshot  进入截图  id=" + id + "   channelName=" + channelName+"  filePath="+filePath);
        File file = new File(filePath);
        if (!file.exists()) file.mkdirs();
        HashMap map = new HashMap<>();
        map.put("uid", 1);
        map.put("filePath", filePath + "/1.jpg");
        map.put("channelName", channelName);
        RinoIPCController.getInstance().snapshot(this.id, map, null);
    }

}
