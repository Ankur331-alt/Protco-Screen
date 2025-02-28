package com.rino.p2pRino.Util;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 用于aac音频解码
 */

public class AACEncoderUtil {
    private static final String TAG = "AACEncodedUtil";
    //声道数
    private static final int KEY_CHANNEL_COUNT = 1;
    //采样率
    private static final int KEY_SAMPLE_RATE = 16000;
    //编码器
    private MediaCodec mEncoder;
    //输入输出流
    private FileOutputStream fops;
    //保存路径
    private String SD_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    //回调事件
    private DataEvents events = null;

    //接口
    public interface DataEvents {
        void OnData(byte[] outData);
    }

    //应用缓存路径
    private String path;

    public AACEncoderUtil(DataEvents events, String path) {
        this.events = events;
        this.path = path;
    }

    /**
     * 初始化所有变量
     */
    public void start() {
//        String savePath;
        String fileName = "record.aac";
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            savePath = SD_PATH;
//        } else {
//
//            return;
//        }
        File file = new File(path + fileName);
        try {
            fops = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        prepare();
    }

    /**
     * 初始化解码器
     *
     * @return 初始化失败返回false，成功返回true
     */
    public boolean prepare() {
        try {
            //需要解码数据的类型
            String mine = "audio/mp4a-latm";
            //初始化编码器
            mEncoder = MediaCodec.createEncoderByType(mine);
            //媒体格式
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(mine, KEY_SAMPLE_RATE, KEY_CHANNEL_COUNT);
            //指定比特率
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 32000);
            //指定采样率
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, KEY_SAMPLE_RATE);
            //指定通道个数
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, KEY_CHANNEL_COUNT);
            //指定PROFILE
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectERLC);
            //指定缓冲区最大长度
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024);
            //应用配置
            mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (mEncoder == null) {
            return false;
        }
        mEncoder.start();
        return true;
    }

    /**
     * aac编码
     */
    public void encode(byte[] buf, int offset, int length) {

        ByteBuffer[] inputBuffers = mEncoder.getInputBuffers(); //所有的输入缓冲区
        ByteBuffer[] outputBuffers = mEncoder.getOutputBuffers();//所有的输出缓冲区
        //注：一次编码，只使用一个缓冲区，所以需要获取缓冲区的索引
        //获取可用的输入缓冲区索引
        int inputBufferIndex = mEncoder.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            //写原始数据到输入缓冲区
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buf);
            mEncoder.queueInputBuffer(inputBufferIndex, 0, length, System.nanoTime(), 0);
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        //获取可用输出缓冲区的索引
        int outputBufferIndex = mEncoder.dequeueOutputBuffer(bufferInfo, 0);
        while (outputBufferIndex >= 0) { //循环读取完输出缓冲区的数据
            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            int outPacketSize = bufferInfo.size + 7;// 7为ADTS头部的大小
            outputBuffer.position(bufferInfo.offset);
            outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
            byte[] outData = new byte[outPacketSize];
            addADTStoPacket(outData, outPacketSize);//添加ADTS
            outputBuffer.get(outData, 7, bufferInfo.size);//将编码得到的AAC数据 取出到byte[]中 偏移量offset=7
            //回调
            if (events != null) {
                events.OnData(outData);
                Log.e(TAG, "OnData size:" + outData.length);
            }
            //写文件
            try {
                fops.write(outData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputBuffer.position(bufferInfo.offset);
            mEncoder.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mEncoder.dequeueOutputBuffer(bufferInfo, 0);
        }
    }

    private static void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; //aac-LC
        int freqIdx = 8; //16k
        int chanCfg = 1; //单通道
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }


    /**
     * 释放资源
     */
    public void stop() {
        try {
            fops.flush();
            fops.close();

            if (mEncoder != null) {
                mEncoder.stop();
                mEncoder.release();
                mEncoder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
