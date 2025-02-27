package com.rinoiot.iflytek.recorder;


/**
 * @author edwin
 */
public interface AudioRecorder {
    /**
     * 开始录音
     *
     * @return the recording status
     */
    int start();

    /**
     * 停止录音
     */
    void stop();

    /**
     *  销毁录音机
     */
    void destroy();
}
