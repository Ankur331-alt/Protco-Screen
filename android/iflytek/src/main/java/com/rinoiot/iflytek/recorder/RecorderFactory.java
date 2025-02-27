package com.rinoiot.iflytek.recorder;

import android.Manifest;

import com.iflytek.alsa.AlsaRecorder;
import com.rinoiot.iflytek.audio.AudioConfig;

import androidx.annotation.RequiresPermission;

/**
 * 初始化不同类型的录音机
 * @author edwin
 */
public class RecorderFactory  {

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public static AudioRecorder getRecorder(AudioConfig audioConfig, AlsaRecorder.PcmListener listener){
        // 多麦，1声卡录音
        return new SingleAlsaRecorder(audioConfig, listener);
    }
}
