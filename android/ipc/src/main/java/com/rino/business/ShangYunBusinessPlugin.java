package com.rino.business;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.rino.p2pRino.RinoSDK;
import com.rino.p2pRino.Util.AACDecoderUtil;
import com.rino.p2pRino.Util.LogUtil;
import com.rino.p2pRino.Util.MediaCodecUtil;
import com.rino.BaseIPCBusinessPlugin;
import com.rino.IIPCBusinessResult;
import com.rino.IPCConstant;
import com.rino.IRinoIPCEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.agora.rtc2.LeaveChannelOptions;
import io.agora.rtc2.RtcConnection;

public class ShangYunBusinessPlugin extends BaseIPCBusinessPlugin {

    private RinoSDK shangyunSDK;

    private ShangYunHandler mHandler;

    private static final String TAG = "ShangYunBusinessPlugin";

    @Override
    public void putSurfaceView(String key, SurfaceView surfaceView, IRinoIPCEventHandler eventHandler) {
        super.putSurfaceView(key, surfaceView, eventHandler);
        MediaCodecUtil mMediaCodecUtil = new MediaCodecUtil(surfaceView.getHolder());
        AACDecoderUtil mAudioDeUtil = new AACDecoderUtil();
        shangyunSDK.putCodec(key, mMediaCodecUtil, mAudioDeUtil);
    }

    public ShangYunBusinessPlugin(Context context, SurfaceView surfaceView, IRinoIPCEventHandler eventHandler) {
        super(context, surfaceView, eventHandler);
        mHandler = new ShangYunHandler(eventHandler);
        shangyunSDK = new RinoSDK(context, mHandler, surfaceView.getHolder());
    }


    @Override
    public void init(HashMap map, IIPCBusinessResult result) {
        try {
            String initString = (String) map.get("initString");
            String p2pKey = (String) map.get("p2pKey");
            String did = (String) map.get("did");
            shangyunSDK.initRinoSDK(did, initString + ":" + p2pKey);
            if (result != null)
                result.onSuccess(true);
        } catch (Exception e) {
            if (result != null) {
                result.onFailed("Failed to create or initialize SDK for ShangYun!", e);
            }
            onError(null, IPCConstant.IPC_CONNECT_ERROR_INITIALIZATION, "Failed to create or initialize P2PSdk for ShangYun! Error:  " + e.getMessage());
        }
    }

    @Override
    public void connect(HashMap map, IIPCBusinessResult result) {
    }

    @Override
    public void pull(String businessJson, IIPCBusinessResult result) {
        try {
            JSONObject business = new JSONObject(businessJson);
            int packageSize = (int) business.get("packageSize");
            business.remove("packageSize");
            int role = (int) business.get("role");
            business.remove("role");
            shangyunSDK.startRinoMedia(business, new RinoSDK.SendDataCallBack() {
                @Override
                public void success() {
                    Log.e(TAG, "开始拉流：Role--->:" + role);
                    shangyunSDK.startPlayMedia(packageSize, role);
                    if (result != null) {
                        result.onSuccess(true);
                    }
                }

                @Override
                public void failed() {
                    if (result != null) {
                        result.onFailed("Pull stream failed!", new Exception("false"));
                    }
                }
            });
            //发送指令成功之后，API内部检验指令，校验通过之后开始读流、解码、播放
        } catch (Exception e) {
            if (result != null) {
                result.onFailed("Pull stream failed!", e);
            }
            onError(null, IPCConstant.IPC_CONNECT_ERROR_PULL_STREAM, "Pull stream failed!! Error:  " + e.getMessage());
        }
    }

    @Override
    public void disconnect(HashMap map, IIPCBusinessResult result) {
        shangyunSDK.releaseRinoSDK();
    }

    @Override
    public void pause() {

    }

    @Override
    public void enableVideo(boolean enable, IIPCBusinessResult result) {

    }

    @Override
    public void enableVideoWithParams(HashMap map, IIPCBusinessResult result) {


    }

    @Override
    public void enableTalk(boolean enable, IIPCBusinessResult result) {
        shangyunSDK.startRinoTalkControl(enable);
        result.onSuccess(true);
    }


    @Override
    public void sendDeviceData(String jsonValue, IIPCBusinessResult result) {
        try {
            shangyunSDK.sendData(new JSONObject(jsonValue), new RinoSDK.SendDataCallBack() {
                @Override
                public void success() {
                    result.onSuccess(true);
                }

                @Override
                public void failed() {
                    result.onFailed("Send data to p2p--->Failed", new Exception("Send data to p2p failed"));
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void snapshot(HashMap map, IIPCBusinessResult result) {

    }

    @Override
    public void startRecording(HashMap map, IIPCBusinessResult result) {

    }

    @Override
    public void stopRecording(IIPCBusinessResult result) {

    }

    @Override
    public void onDestroy() {
        shangyunSDK.releaseRinoSDK();
    }

    @Override
    public void muteLocal(IIPCBusinessResult result) {

    }

    @Override
    public void muteRemote(HashMap map, IIPCBusinessResult result) {

    }

    @Override
    public void setParameters(String parameters, IIPCBusinessResult result) {

    }

    @Override
    public void leaveChannel(RtcConnection connection, LeaveChannelOptions options, IIPCBusinessResult result) {

    }

    @Override
    public void leaveChannel(IIPCBusinessResult result) {

    }


    private class ShangYunHandler extends Handler {
        private IRinoIPCEventHandler mRinoRCTIPCPlayViewCallback;

        public ShangYunHandler(IRinoIPCEventHandler rinoRCTIPCPlayViewCallback) {
            this.mRinoRCTIPCPlayViewCallback = rinoRCTIPCPlayViewCallback;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case RinoSDK.MSG_INIT_FAILED:
                    LogUtil.e("初始化失败！");
                    onStatusChange(null, IPCConstant.IPC_CONNECT_STATUS_INITIALIZATION_FAILED);
                    break;
                case RinoSDK.MSG_CONNECTING:
                    LogUtil.e("初始化成功，开始连接P2P服务！");
                    onStatusChange(null, IPCConstant.IPC_CONNECT_STATUS_CONNECTING);
                    break;

                case RinoSDK.MSG_CONNECT_SUCCESSFUL:
                    LogUtil.e("连接成功！");
                    onStatusChange(null, IPCConstant.IPC_CONNECT_STATUS_CONNECTED);
                    break;

                case RinoSDK.MSG_CONNECT_FAILED:
                    LogUtil.e("连接失败！");
                    onStatusChange(null, IPCConstant.IPC_CONNECT_STATUS_CONNECTING_FAILED);
                    break;

                case RinoSDK.MSG_SEND_CMD_FAILED:
                    LogUtil.e("发送CMD失败！");
                    break;

                case RinoSDK.MSG_SEND_BODY_FAILED:
                    LogUtil.e("发送BODY失败！");
                    break;

                case RinoSDK.MSG_SEND_CHECK_SUCCESSFUL:
                    LogUtil.e("发送CHECK成功！");
//                    RinoRCTIPCPlayView.this.checkAndSetModule();
//                    mRinoIPCModule.play(deviceId, null);
                    break;

                case RinoSDK.MSG_DISCONNECT_SUCCESSFUL:
                    onStatusChange(null, IPCConstant.IPC_CONNECT_STATUS_DISCONNECT);
                    LogUtil.e("断开DISCONNECT成功！");
                    break;
                case RinoSDK.MSG_PLAY_SUCCESSFUL:
                    onStatusChange(null, IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING);
                    break;

                default:
                    LogUtil.d("msg(" + msg.what + "): " + msg.obj.toString());
                    break;
            }
        }
    }
}
