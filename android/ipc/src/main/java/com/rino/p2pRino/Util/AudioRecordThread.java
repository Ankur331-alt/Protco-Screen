package com.rino.p2pRino.Util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * 录音线程
 */

public class AudioRecordThread extends Thread implements AACEncoderUtil.DataEvents {

    private AudioRecord audioRecord;
    private int m_in_buf_size;
    private boolean isRecording = false;
    private boolean isSend = false;
    private static int sampleRateInHz = 16000;
    private AACEncoderUtil mAudioEnUtil;
    //回调事件
    private OnRecordDataEvents events = null;

    //接口
    public interface OnRecordDataEvents {
        void OnRecordData(byte[] outData);
    }

    public AudioRecordThread(OnRecordDataEvents events, String path) {
        this.events = events;
        mAudioEnUtil = new AACEncoderUtil(this, path);
        mAudioEnUtil.start();
    }

    @Override
    public void run() {
        super.run();
        isRecording = true;
        byte[] audio;
        prepare();
        audio = new byte[m_in_buf_size];
        while (isRecording) {
            if (isSend) {
                //采集
                int length = audioRecord.read(audio, 0, m_in_buf_size);
                //编码音频数据
                if (mAudioEnUtil != null && length > 0) {
                    mAudioEnUtil.encode(audio, 0, length);
                }
            }

        }

    }

    private void prepare() {
        //初始化采集
        m_in_buf_size = AudioRecord.getMinBufferSize(sampleRateInHz,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRateInHz,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);
        audioRecord.startRecording();
    }

    public void releaseRecord() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }
        audioRecord = null;
        isRecording = false;
        isSend = false;
        if (mAudioEnUtil != null) {
            mAudioEnUtil.stop();
            mAudioEnUtil = null;
        }
    }


    public void stopRecord() {
        isRecording = false;
    }

    public void setAudiSend(boolean status) {
        isSend = status;
    }

    @Override
    public void OnData(byte[] outData) {
        if (events != null) {
            events.OnRecordData(outData);
        }
    }
}
