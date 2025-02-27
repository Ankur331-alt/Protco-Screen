package com.rinoiot.iflytek.recorder;

import android.util.Log;

import com.iflytek.alsa.AlsaRecorder;
import com.rinoiot.iflytek.audio.AudioConfig;
import com.rinoiot.iflytek.common.ConfigStore;

/**
 * 1.android只支持2声道，因此多麦需要直接从alsa录音，
 *     参考文档：<a href="https://www.yuque.com/iflyaiui/zzoolv/zmv0f5">...</a>
 * 2.对1个声卡录音，回采和音频数据都来自一个声卡
 * @author edwin
 */
public class SingleAlsaRecorder implements AudioRecorder {

    private static final String TAG = "SingleAlsaRecorder";

    /**
     * 录音数据信息透传回调监听
     */
    private AlsaRecorder alsaRecorder;

    /**
     * tinyalsa录音音频监听器
     */
    AlsaRecorder.PcmListener alsaListener;

    /**
     * ToDo() ponder on whether this should be an instance or a class. Yeah, I know.
     */
    public SingleAlsaRecorder(AudioConfig audioConfig, AlsaRecorder.PcmListener listener) {
        if(null != alsaRecorder) {
            return;
        }

        // 麦克风采样率，一般16k
        int sampleRate = 16000;

        // 0(16bit-小端格式),1(32bit-小端格式)
        int format = 1;

        // 周期数 一般不修改
        int periodCount = 8;

        // 一次中断的读取的帧数 一般不修改，某些不支持这么大数字时会报错，可以尝试减小。
        // 增大该值可以降低cpu，但是2次数据之间的延迟会增大 PeriodSize 必须是channel的倍数，否则数据异常
        int periodSize = 128 * audioConfig.getChannel();

        // 缓存大小(勿修改)
        int pcmBufferSize = periodSize * periodCount;

        
        //给声卡授权 chmod 777 /dev/snd/pcmCxDx
        alsaRecorder = AlsaRecorder.createInstance(
                audioConfig.getCard(),
                audioConfig.getDevice(),
                audioConfig.getChannel(),
                sampleRate,
                periodSize,
                periodCount,
                format,
                pcmBufferSize
        );
        alsaListener = listener;

        // Cache audio config
        ConfigStore.getInstance().setAudioConfig(audioConfig);

        // Alsa-Jni日志控制 true-开启  false-关闭
        alsaRecorder.setLogShow(false);
    }

    /**
     * Start recording audio
     *
     * @return the status indicating whether the recorder was started correctly or not
     */
    public int start() {
        if(null == alsaRecorder){
            Log.e(TAG, "start: AlsaRecorder is not initialized");
            return -1;
        }

        int ret = alsaRecorder.startRecording(alsaListener);
        Log.e(TAG, "start: ret val=" + ret);
        if (0 == ret) {
            Log.d(TAG, "start: recording");
        } else {
            Log.e(TAG, "start: failed to start recording");
            Log.e(TAG, "错误解决详情参考：https://www.yuque.com/iflyaiui/zzoolv/igbuol ");
        }
        return ret;
    }

    /**
     * 停止录音
     */
    public void stop() {
        alsaRecorder.stopRecording();
        Log.i(TAG, "stop record");
    }

    public void destroy() {
        if(null == alsaRecorder){
            return;
        }

        try {
            alsaRecorder.destroy();
            alsaRecorder = null;
            Log.i(TAG, "trashed the recorder");
        }catch (Exception exception) {
            Log.e(TAG, "destroy: failed to destroy the recorder. " + exception.getLocalizedMessage());
        }
    }
}
