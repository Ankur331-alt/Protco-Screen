package com.rino;

import java.util.HashMap;

import io.agora.rtc2.LeaveChannelOptions;
import io.agora.rtc2.RtcConnection;

public interface IIPCBusiness {
    /**
     * 初始化对应的IPC SDK
     *
     * @param map
     */
    void init(HashMap map, IIPCBusinessResult result);

    /**
     * 建立IPC连接
     *
     * @param map map参数，为三方IPC SDK预留的一些其他参数
     */
    void connect(HashMap map, IIPCBusinessResult result);

    /**
     * 开始拉流
     */
    void pull(String businessJson, IIPCBusinessResult result);

    /**
     * 断开IPC连接
     */
    void disconnect(HashMap map, IIPCBusinessResult result);

    /**
     * 暂停播放
     */
    void pause();


    /**
     * 是否开启视频（双向对讲）
     */
    void enableVideo(boolean enable, IIPCBusinessResult result);

    /**
     * 是否开启视频（双向对讲） 可以携带多个参数，方便后续分辨率切换
     */
    void enableVideoWithParams(HashMap map, IIPCBusinessResult result);

    /**
     * 是否开启音频（双向对讲）
     */
    void enableTalk(boolean enable, IIPCBusinessResult result);

    void sendDeviceData(String jsonValue, IIPCBusinessResult result);

    /**
     * 截图
     *
     * @param map    方案不同，入参协议不同
     * @param result
     */
    void snapshot(HashMap map, IIPCBusinessResult result);

    /**
     * 开始录制画面
     *
     * @param map
     * @param result
     */
    void startRecording(HashMap map, IIPCBusinessResult result);

    /**
     * 停止录制画面
     *
     * @param result
     */
    void stopRecording(IIPCBusinessResult result);

    void onDestroy();

    void muteLocal(IIPCBusinessResult result);

    void muteRemote(HashMap map, IIPCBusinessResult result);

    void setParameters(String parameters, IIPCBusinessResult result);

    void leaveChannel(RtcConnection connection, LeaveChannelOptions options, IIPCBusinessResult result);
    void leaveChannel(IIPCBusinessResult result);
}

