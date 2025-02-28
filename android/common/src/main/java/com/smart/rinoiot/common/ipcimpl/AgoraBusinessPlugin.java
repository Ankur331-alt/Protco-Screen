package com.smart.rinoiot.common.ipcimpl;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.rino.BaseIPCBusinessPlugin;
import com.rino.IIPCBusinessResult;
import com.rino.IPCConstant;
import com.rino.IRinoIPCEventHandler;
import com.rino.p2pRino.Util.LogUtil;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.LgUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.agora.rtc2.AgoraMediaRecorder;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaRecorderCallback;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.LeaveChannelOptions;
import io.agora.rtc2.RecorderInfo;
import io.agora.rtc2.RecorderStreamInfo;
import io.agora.rtc2.RtcConnection;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

/**
 * @ProjectName: Rino Smart
 * @Package: com.smart.rinoiot.common.ipcimpl
 * @ClassName: AgoraBusinessPlugin
 * @Description: java类作用描述
 * @Author: ZhangStar
 * @Emali: ZhangStar666@gmali.com
 * @CreateDate: 2023/11/7 15:29
 * @UpdateUser: 更新者：
 * @UpdateDate: 2023/11/7 15:29
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
class AgoraBusinessPlugin extends BaseIPCBusinessPlugin implements IMediaRecorderCallback {
    private static final String TAG = "AgoraBusinessPlugin";
    private HashMap<String, IIPCBusinessResult> businessResultMap = new HashMap<>();
    //根据频道名称存储当前的录制视频信息
    private AgoraRecordBean[] agoraRecordBeans = new AgoraRecordBean[1];
    private AgoraMediaRecorder mediaRecorder;

    private HashMap<String, ArrayList<SurfaceView>> mIPCPlayViewMap = new HashMap<>();
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

            AppExecutors.getInstance().mainThread().execute(() -> {
                Log.e(TAG + ":" + name, "加入频道成功--->:" + "channel:" + channel + ",uid:" + uid);
                onStatusChange(null, IPCConstant.IPC_CONNECT_STATUS_CONNECTED);
                status = IPCConstant.IPC_CONNECT_STATUS_CONNECTED;
            });
        }

        @Override
        public void onTokenPrivilegeWillExpire(String token) {
            super.onTokenPrivilegeWillExpire(token);
            JSONObject mJSONObject = new JSONObject();
            try {
                mJSONObject.put("key", "tokenPrivilegeWillExpire");
                mJSONObject.put("businessData", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            onOtherBusiness(null, mJSONObject.toString());
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
        }

        @Override
        public void onSnapshotTaken(int uid, String filePath, int width, int height, int errCode) {
            IIPCBusinessResult result = businessResultMap.get(filePath);
            if (result != null) {
                if (errCode >= 0) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("filePath", filePath);
                        jsonObject.put("width", width);
                        jsonObject.put("height", height);
                        jsonObject.put("uid", uid);
                        result.onSuccess(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.onFailed("Failed to snapshot for Agora! ", e);
                    }
                } else {
                    result.onFailed("Failed to snapshot for Agora! errCode: " + errCode, new Exception(errCode + ""));
                }

                businessResultMap.remove(filePath);
            }
        }

        //渲染器已接收首帧远端视频回调。 即播放中
        @Override
        public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
            Log.e(TAG + ":" + name, "onFirstRemoteVideoFrame--->:" + uid);
            Log.e(TAG + ":" + name, "onFirstRemoteVideoFrame--->:" + mEventHandlerMap.get(uid + "") + ",uid" + uid + ",status:" + IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING);
            onStatusChange(uid + "", IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING);
            status = IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING;
        }

        @Override
        public void onFirstLocalVideoFrame(Constants.VideoSourceType source, int width, int height, int elapsed) {
            onStatusChange("push", IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING);
        }

        @Override
        public void onError(int err) {
            Log.e(TAG + ":" + name, "onError--->:" + err);
            AgoraBusinessPlugin.this.onError(null, IPCConstant.IPC_CONNECT_ERROR_BUSINESS, err + "");
        }

        @Override
        public void onLocalAudioStateChanged(int state, int error) {
            Log.e(TAG + ":" + name, "onLocalAudioStateChanged--->state:" + state + ",error" + error);
        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            Log.e(TAG + ":" + name, "onConnectionStateChanged state--->:" + state+"    reason="+reason);
        }


        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("key", "onUserOffline");
                jsonObject.put("uid", uid);
                jsonObject.put("reason", reason);
                AgoraBusinessPlugin.this.onOtherBusiness(uid + "", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e(TAG + ":" + name, "onUserOffline--->:" + uid + "," + reason);
        }
    };
    private RtcEngine mRtcEngine;
    private boolean multipleChannel = false;
    private String rtmToken;

    String id;
    String name;

    public AgoraBusinessPlugin(Context context, String id, String name) {
        super(context);
        LgUtils.e(TAG+" new AgoraBusinessPlugin"+ id);
        this.id = id;
        this.name = name;
    }

    public void onStatusChange(String key, String status) {
        Log.e(TAG + ":" + name, "状态改变 当前插件地址:" + this);
        Log.e(TAG + ":" + name, "当前插件的所有回调:" + mEventHandlerMap);
        if (key == null) {
            Log.e(TAG + ":" + name, "所有回调都通知：status" + status);
            for (String mapKey : mEventHandlerMap.keySet()) {
                IRinoIPCEventHandler mIRinoIPCEventHandler = mEventHandlerMap.get(mapKey);
                mIRinoIPCEventHandler.onStatusChange(status);
            }
        } else {
            Log.e(TAG + ":" + name, "某一个role回调通知：status" + status);
            IRinoIPCEventHandler mIRinoIPCEventHandler = mEventHandlerMap.get(key);
            mIRinoIPCEventHandler.onStatusChange(status);
        }
    }

    private void setupRemoteVideo(int uid) {
        if (mRtcEngine != null) {
            Log.e(TAG + ":" + name, "当前可渲染的视图集合: " + mIPCPlayViewMap.get(uid + ""));
            if (mIPCPlayViewMap.get(uid + "") != null && mIPCPlayViewMap.get(uid + "").size() > 0) {
                Log.e(TAG + ":" + name, "开始把 " + uid + " 的数据渲染关联到视图: " + mIPCPlayViewMap.get(uid + "").get(0));
                mRtcEngine.setupRemoteVideo(new VideoCanvas(mIPCPlayViewMap.get(uid + "").get(0), VideoCanvas.RENDER_MODE_FIT, uid));
            }
        }
    }

    private void removeRemoteVideo(int uid) {
        if (mRtcEngine != null) {
            Log.e(TAG + ":" + name, "当前可渲染的视图集合: " + mIPCPlayViewMap.get(uid + ""));
            Log.e(TAG + ":" + name, "取消 " + uid + " 的数据渲染到视图: ");
            mRtcEngine.setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_FIT, uid));
        }
    }


    @Override
    public void removeView(String key, SurfaceView view) {
//        super.removeView(key);
//        removeRemoteVideo(Integer.parseInt(key));
        ArrayList<SurfaceView> mPlayViews = mIPCPlayViewMap.get(key);
        Log.e(TAG + ":" + name, "开始删除：" + view);
        if (mPlayViews != null) {
            Log.e(TAG + ":" + name, "删除View之前，存在的PlayViews:" + mPlayViews + ",current:" + view);
            if (mPlayViews.contains(view) /*&& mPlayViews.size() > 1*/) {
                mPlayViews.remove(view);
            }
            Log.e(TAG + ":" + name, "删除View之后，存在的PlayViews:" + mPlayViews);
            removeRemoteVideo(Integer.parseInt(key));
            setupRemoteVideo(Integer.parseInt(key));
        }

        status = IPCConstant.IPC_CONNECT_STATUS_CONNECTED;
    }

    @Override
    public void putSurfaceView(String key, SurfaceView surfaceView, IRinoIPCEventHandler eventHandler) {
        Log.e(TAG + ":" + name, "putSurfaceView begin: " + this);
        Log.e(TAG + ":" + name, "putSurfaceView before: " + surfaceView + ",eventHandler" + eventHandler);
        super.putSurfaceView(key, surfaceView, eventHandler);
        Log.e(TAG + ":" + name, "putSurfaceView after: " + mEventHandlerMap);
        ArrayList<SurfaceView> mPlayViews = mIPCPlayViewMap.get(key);
        if (mPlayViews == null) {
            mPlayViews = new ArrayList<SurfaceView>();
            mIPCPlayViewMap.put(key, mPlayViews);
        }
        if (!mPlayViews.contains(surfaceView)) {
            mPlayViews.add((SurfaceView) surfaceView);
        }
        if (!key.equals("push") && !multipleChannel) {
            setupRemoteVideo(Integer.parseInt(key));
        }

    }

    @Override
    public void init(HashMap map, IIPCBusinessResult result) {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = context;
            config.mAppId = (String) map.get("appId");
            config.mEventHandler = mRtcEventHandler;
            mRtcEngine = AgoraEngineManager.create(config);
            if (map.containsKey("parameters")) {
                mRtcEngine.setParameters((String) map.get("parameters"));
            }
            mRtcEngine.enableVideo();
            mRtcEngine.enableLocalAudio(false);
            mRtcEngine.enableLocalVideo(false);
            if ((Boolean) map.get("enableAudio")) {
//                mRtcEngine.enableLocalAudio(true);
            } else {
                mRtcEngine.disableAudio();
            }

//          mRtcEngine.setAudioProfile(1);
            onStatusChange(null, IPCConstant.IPC_CONNECT_STATUS_CONNECTING);
            status = IPCConstant.IPC_CONNECT_STATUS_CONNECTING;
            Log.e(TAG + ":" + name, "init");
            if (result != null) {
                result.onSuccess(true);
            }
        } catch (Exception e) {
            if (result != null) {
                result.onFailed("Failed to create or initialize RtcEngine for Agora!", e);
            }
            onError(null, IPCConstant.IPC_CONNECT_ERROR_INITIALIZATION, "Failed to create or initialize RtcEngine for Agora! Error:  " + e.getMessage());
        }
    }


    /**
     * map:
     * rtmToken 信令SDK的token
     * rtcToken 流媒体的token
     * renewToken 更新token
     * channelName 频道名称
     * uid 用户ID，当前频道内唯一
     *
     * @param map map参数，为三方IPC SDK预留的一些其他参数z
     */

    String channelName;
    int uid;

    @Override
    public void connect(HashMap map, IIPCBusinessResult result) {
        try {
            rtmToken = (String) map.get("rtmToken");
            String rtcToken = (String) map.get("rtcToken");
            if (map.containsKey("multipleChannel")) {
                multipleChannel = map.containsKey("multipleChannel");
            }
            if (map.containsKey("renewToken")) {
                //更新token
                mRtcEngine.renewToken(rtmToken);
            } else {
                channelName = (String) map.get("channelName");
                uid = Double.valueOf(String.valueOf(map.get("uid"))).intValue();
                VideoEncoderConfiguration v = new VideoEncoderConfiguration();
                mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration());
                ChannelMediaOptions options = new ChannelMediaOptions();
                // 视频通话场景下，设置频道场景为 BROADCASTING。
                options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
                // 将用户角色设置为 BROADCASTER。
                options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
                Log.e(TAG + ":" + name, "开始加入频道，channelName：" + channelName);
                if (multipleChannel) {
                    mRtcConnection = new RtcConnection(channelName, uid);
                    ((RtcEngineEx) mRtcEngine).joinChannelEx(rtcToken, new RtcConnection(channelName, uid), options, mRtcEventHandler);
                } else {
                    mRtcEngine.joinChannel(rtcToken, channelName, uid, options);
                }
            }
            if (result != null) {
                result.onSuccess(true);
            }
        } catch (Exception e) {
            if (result != null) {
                result.onFailed("Connect failed for Agora! ", e);
            }
            onError(null, IPCConstant.IPC_CONNECT_ERROR_CONNECT, "Connect failed for Agora! Error:  " + e.getMessage());
        }
    }

    private RtcConnection mRtcConnection;

    @Override
    public void pull(String businessJson, IIPCBusinessResult result) {
        try {
            JSONObject business = new JSONObject(businessJson);
            int remoteUid = business.getInt("remoteUid");
            if (status.equals(IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING)) {
                onStatusChange(remoteUid + "", IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING);
                if (result != null) {
                    result.onSuccess(true);
                }
                return;
            }
            if (multipleChannel) {
                AppExecutors.getInstance().delayedThread().schedule(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG + ":" + name, "multipleChannel");
                        Log.e(TAG + ":" + name, "当前可渲染的视图集合: " + mIPCPlayViewMap.get(remoteUid + ""));
                        Log.e(TAG + ":" + name, "开始把 " + remoteUid + " 的数据渲染关联到视图: " + mIPCPlayViewMap.get(remoteUid + "").get(0));
                        int res = ((RtcEngineEx) mRtcEngine).setupRemoteVideoEx(new VideoCanvas(mIPCPlayViewMap.get(remoteUid + "").get(0), VideoCanvas.RENDER_MODE_FIT, remoteUid), mRtcConnection);
                        Log.e(TAG + ":" + name, "setupRemoteVideoEx:" + res );
                    }
                }, 500, TimeUnit.MILLISECONDS);
            } else {
                setupRemoteVideo(remoteUid);
            }
            Log.e(TAG + ":" + name, "remoteUid prevStatus:" + status + ",Next Status:" + IPCConstant.IPC_CONNECT_STATUS_PULL_STREAM);
            //setupRemoteVideo肯定是异步关联渲染的 這裏要做校準一次
            String nextStatus = status.equals(IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING) ? IPCConstant.IPC_CONNECT_STATUS_PULL_PLAYING : IPCConstant.IPC_CONNECT_STATUS_PULL_STREAM;
            onStatusChange(remoteUid + "", nextStatus);
            status = nextStatus;
            if (result != null) {
                result.onSuccess(true);
            }
        } catch (Exception e) {
            if (result != null) {
                result.onFailed("Pull stream failed for Agora! ", e);
            }
            onError(null, IPCConstant.IPC_CONNECT_ERROR_PULL_STREAM, "Pull stream failed for Agora! Error:  " + e.getMessage() + ",businessJson:" + businessJson);
        }

    }


    @Override
    public void disconnect(HashMap map, IIPCBusinessResult result) {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void enableVideo(boolean enable, IIPCBusinessResult result) {
        if (mRtcEngine != null) {
            mRtcEngine.enableLocalVideo(enable);
            mRtcEngine.enableLocalAudio(enable);
            if (mSurfaceViewMap.get("push") != null) {
                if (enable) {
                    mRtcEngine.startPreview();
                    mRtcEngine.setupLocalVideo(new VideoCanvas(mSurfaceViewMap.get("push")));
                } else {
                    mRtcEngine.stopPreview();
                    mRtcEngine.setupLocalVideo(null);
                }
            }
        }
    }

    @Override
    public void enableVideoWithParams(HashMap map, IIPCBusinessResult result) {
        try {
            if (mRtcEngine != null) {
                boolean enable = (boolean) map.get("enable");
                String orientationMode = "portrait";
                if (map.containsKey("orientationMode")) {
                    orientationMode = (String) map.get("orientationMode");
                }
                int width = Double.valueOf(String.valueOf(map.get("width"))).intValue();
                int height = Double.valueOf(String.valueOf(map.get("height"))).intValue();
                mRtcEngine.enableLocalVideo(enable);
                mRtcEngine.enableLocalAudio(enable);
                mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                        new VideoEncoderConfiguration.VideoDimensions(width, height),
                        VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                        0,
                        getOrientationMode(orientationMode))
                );
                if (mSurfaceViewMap.get("push") != null) {
                    if (enable) {
                        mRtcEngine.startPreview();
                        mRtcEngine.setupLocalVideo(new VideoCanvas(mSurfaceViewMap.get("push")));
                    } else {
                        mRtcEngine.stopPreview();
                        mRtcEngine.setupLocalVideo(null);
                    }

                }
            }
        } catch (Exception e) {

        }

    }

    public VideoEncoderConfiguration.ORIENTATION_MODE getOrientationMode(String mode) {
        if (mode.equals("portrait")) {
            return VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
        } else if (mode.equals("landscape")) {
            return VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE;
        } else {
            return VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
        }
    }


    @Override
    public void enableTalk(boolean enable, IIPCBusinessResult result) {
//        if (mRtcEngine != null) {
//            mRtcEngine.enableLocalAudio(enable);
////            if (multipleChannel) {
////                ((RtcEngineEx) mRtcEngine).muteLocalAudioStreamEx(!enable, new RtcConnection(channelName, uid));
////            }
//            if (result != null) {
//                result.onSuccess(true);
//            }
//
//        }
        if (mRtcEngine != null) {
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    mRtcEngine.enableLocalAudio(enable);
                    if (multipleChannel) {
                        ChannelMediaOptions options = new ChannelMediaOptions();
                        // 视频通话场景下，设置频道场景为 BROADCASTING。
                        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
                        // 将用户角色设置为 BROADCASTER。
                        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
                        options.publishMicrophoneTrack = enable;
                        ((RtcEngineEx) mRtcEngine).updateChannelMediaOptionsEx(options, mRtcConnection);
                        ((RtcEngineEx) mRtcEngine).muteLocalAudioStreamEx(!enable, mRtcConnection);
                    } else {
                        mRtcEngine.muteLocalAudioStream(!enable);
                    }
                    if (result != null) {
                        result.onSuccess(true);
                    }
                }
            });
        }


    }


    @Override
    public void sendDeviceData(String jsonValue, IIPCBusinessResult result) {
        //对接信令SDK
    }

    @Override
    public void snapshot(HashMap map, IIPCBusinessResult result) {
        if (mRtcEngine != null) {
            int uid = Double.valueOf(String.valueOf(map.get("uid"))).intValue();
            String filePath = (String) map.get("filePath");
            businessResultMap.put(filePath, result);
            if (multipleChannel && map.containsKey("channelName")) {
                int res = ((RtcEngineEx) mRtcEngine).takeSnapshotEx(new RtcConnection((String) map.get("channelName"), this.uid), uid, filePath);
                LgUtils.w("333333333333333 takeSnapshotEx " + res);
            } else {

                int res = mRtcEngine.takeSnapshot(uid, filePath);
                LgUtils.w("333333333333333 takeSnapshot " + res);
            }
        }
    }

    @Override
    public void startRecording(HashMap map, IIPCBusinessResult result) {
        try {
            destroyMediaRecorder();
            String channelName = (String) map.get("channelName");
            String storagePath = (String) map.get("storagePath");//录制文件存储路径
            int uid = Double.valueOf(String.valueOf(map.get("uid"))).intValue();
            int streamType = Double.valueOf(String.valueOf(map.get("streamType"))).intValue();//1 2 3
            int maxDuration = Double.valueOf(String.valueOf(map.get("maxDuration"))).intValue();//最大超时时间，毫秒
            agoraRecordBeans[0] = new AgoraRecordBean(storagePath, channelName, uid, null);
            mediaRecorder = mRtcEngine.createMediaRecorder(new RecorderStreamInfo(channelName, uid));
//            mediaRecorder.setMediaRecorderObserver(new IMediaRecorderCallback() {
//                @Override
//                public void onRecorderStateChanged(String channelId, int uid, int state, int error) {
//                    Log.e(TAG+":"+name, "uid: " + uid + ",state: " + state + ",error " + error);
//                }
//
//                @Override
//                public void onRecorderInfoUpdated(String channelId, int uid, RecorderInfo info) {
//
//                }
//            });
            mediaRecorder.setMediaRecorderObserver(this);
            mediaRecorder.startRecording(new AgoraMediaRecorder.MediaRecorderConfiguration(storagePath, 1, streamType, maxDuration, 0));
            result.onSuccess(true);
        } catch (Exception e) {
            result.onFailed(e.getMessage(), e);
        }
    }

    @Override
    public void stopRecording(IIPCBusinessResult result) {
        if (agoraRecordBeans[0] != null) {
            agoraRecordBeans[0].setResult(result);
        }
        destroyMediaRecorder();
    }


    public void destroyMediaRecorder() {
        if (mediaRecorder != null && mRtcEngine != null) {
            mediaRecorder.stopRecording();
            mediaRecorder.setMediaRecorderObserver(null);
            mRtcEngine.destroyMediaRecorder(mediaRecorder);
            mediaRecorder = null;
        }
    }

    @Override
    public void onDestroy() {
        mIPCPlayViewMap.clear();
        AgoraEngineManager.detroy();
//        if (mRtcEngine != null) {
//            mRtcEngine.leaveChannel();
//            RtcEngine.destroy();
//        }

    }

    @Override
    public void muteLocal(IIPCBusinessResult result) {

    }

    @Override
    public void muteRemote(HashMap map, IIPCBusinessResult result) {
        if (mRtcEngine == null) {
            result.onFailed("Agora engine 不存在", new Throwable("Agora engine 不存在"));
            return;
        }
        if (multipleChannel) {
//            try {
//                String type = (String) map.get("type");//all or part
//                boolean muted = (boolean) map.get("muted");//all or part
//                String channelName = (String) map.get("channelName");//all or part
//                if (type.equals("part")) {
//                    if (map.containsKey("remoteIds")) {
//                        ArrayList list = (ArrayList) map.get("type");
//                        for (Object o : list) {
//                            int remoteId = Double.valueOf(String.valueOf(o)).intValue();
//                            ((RtcEngineEx) mRtcEngine).muteAllRemoteAudioStreamsEx(muted,new RtcConnection().channelId);
//                        }
//                    } else {
//                        result.onFailed("缺少参数：remoteIds", new Throwable("缺少参数：remoteIds"));
//                    }
//                } else {
//                    ((RtcEngineEx) mRtcEngine).muteAllRemoteAudioStreams(muted);
//                }
//                result.onSuccess(true);
//            } catch (Exception e) {
//                result.onFailed(e.getMessage(), e);
//            }
        } else {
            try {
                String type = (String) map.get("type");//all or part
                boolean muted = (boolean) map.get("muted");//all or part
                if (type.equals("part")) {
                    if (map.containsKey("remoteIds")) {
                        ArrayList list = (ArrayList) map.get("remoteIds");
                        for (Object o : list) {
                            int remoteId = Double.valueOf(String.valueOf(o)).intValue();
                            mRtcEngine.muteRemoteAudioStream(remoteId, muted);
                        }
                    } else {
                        result.onFailed("缺少参数：remoteIds", new Throwable("缺少参数：remoteIds"));
                    }
                } else {
                    mRtcEngine.muteAllRemoteAudioStreams(muted);
                }
                if (result != null) {
                    result.onSuccess(true);
                }

            } catch (Exception e) {
                if (result != null) {
                    result.onFailed(e.getMessage(), e);
                }

            }
        }
    }

    @Override
    public void setParameters(String parameters, IIPCBusinessResult result) {
        if (!parameters.isEmpty() && mRtcEngine != null) {
            mRtcEngine.setParameters((String) parameters);
            if (result != null)
                result.onSuccess(true);
        } else {
            if (result != null)
                result.onFailed("Parameters不能为空", new Throwable("Parameters不能为空"));
        }

    }


    private void sendRecordResultToRN(AgoraRecordBean curBean, int uid) {
//        //curBean 里面的result 回调，必须是上层调用stopRecording的Promise result回调，否则不会执行
//        JSONObject jsonObject = new JSONObject();
//        AgoraRecordStateBean<String> notifyData = new AgoraRecordStateBean<>();
//        try {
//            jsonObject.put("filePath", curBean.storagePath);
//            jsonObject.put("uid", uid);
//            jsonObject.put("channelName", curBean.channelName);
//
//            notifyData.setSuccess(true);
//            notifyData.setErrorCode(0);
//            notifyData.setData(jsonObject.toString());
//            String notifyResult = new Gson().toJson(notifyData);
//            if (curBean.result != null) {
//                curBean.result.onSuccess(notifyResult);
//            }
//            EventManager.getInstance().sendDataO(RNNotifyConstant.RECORD_STATE_CHANGE, notifyResult);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            if (curBean.result != null) {
//                curBean.result.onFailed("Failed to onRecorderStateChanged for Agora! ", e);
//            }
//            notifyData.setSuccess(false);
//            notifyData.setError("Failed to onRecorderStateChanged for Agora! " + e.getMessage());
//            notifyData.setErrorCode(-1);
//            String notifyResult = new Gson().toJson(notifyData);
//            EventManager.getInstance().sendData(RNNotifyConstant.RECORD_STATE_CHANGE, notifyResult);
//        }
    }

    @Override
    public void onRecorderStateChanged(String channelId, int uid, int state, int error) {
//        Log.e(TAG+":"+name, "uid: " + uid + ",state: " + state + ",error " + error);
//        if (agoraRecordBeans[0] == null) {
//            return;
//        }
//        AgoraRecordBean curBean = agoraRecordBeans[0];
//        if (error == 0) {
//            //没有异常
//            if (state == 3) {
//                //录像正常停止
//                sendRecordResultToRN(curBean, uid);
//                agoraRecordBeans[0] = null;
//            }
//        } else if (error == 3) {
//            if (state == -1) {
//                //到达超时时间，自动停止，但是文件也更新成功
//                sendRecordResultToRN(curBean, uid);
//                agoraRecordBeans[0] = null;
//            }
//        } else {
//            agoraRecordBeans[0] = null;
//            AgoraRecordStateBean<String> notifyData = new AgoraRecordStateBean<>();
//            notifyData.setSuccess(false);
//            notifyData.setError("Failed to onRecorderStateChanged for Agora!  Agora Error code: " + error);
//            notifyData.setErrorCode(error);
//            String notifyResult = new Gson().toJson(notifyData);
//            EventManager.getInstance().sendData(RNNotifyConstant.RECORD_STATE_CHANGE, notifyResult);
//        }

    }

    @Override
    public void onRecorderInfoUpdated(String channelId, int uid, RecorderInfo info) {
        Log.e(TAG + ":" + name, "onRecorderInfoUpdated");
    }

    @Override
    public void leaveChannel(RtcConnection connection, LeaveChannelOptions options, IIPCBusinessResult result) {
        ((RtcEngineEx) mRtcEngine).leaveChannelEx(connection, options);
    }

    @Override
    public void leaveChannel(IIPCBusinessResult result) {
        mRtcEngine.leaveChannel();
    }

    static class AgoraRecordBean {
        String storagePath;
        String channelName;
        int uid;//要录制的远程用户ID，即role或remoteUid

        IIPCBusinessResult result;

        public AgoraRecordBean(String storagePath, String channelName, int uid, IIPCBusinessResult result) {
            this.storagePath = storagePath;
            this.channelName = channelName;
            this.uid = uid;
            this.result = result;
        }

        public String getStoragePath() {
            return storagePath;
        }

        public void setStoragePath(String storagePath) {
            this.storagePath = storagePath;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public IIPCBusinessResult getResult() {
            return result;
        }

        public void setResult(IIPCBusinessResult result) {
            this.result = result;
        }
    }


    static class AgoraRecordStateBean<T> {
        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        boolean isSuccess;
        String error;
        int errorCode;
        T data;
    }
}
