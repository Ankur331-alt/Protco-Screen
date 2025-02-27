package com.rino.p2pRino;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.p2p.P2PSDK;
import com.p2p.pppp_api.PPCS_APIs;
import com.rino.p2pRino.Model.SessionModel;
import com.rino.p2pRino.Util.AACDecoderUtil;
import com.rino.p2pRino.Util.AudioRecordThread;
import com.rino.p2pRino.Util.LogUtil;
import com.rino.p2pRino.Util.MediaCodecUtil;
import com.rino.p2pRino.Util.RINO_APIs;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class RinoSDK implements AudioRecordThread.OnRecordDataEvents {

    public final Context mContext;
    public final Handler mHandle;
    public static final int MSG_INIT_FAILED = 1001;  //初始化失败
    public static final int MSG_CONNECT_SUCCESSFUL = 1002;  //连接成功
    public static final int MSG_CONNECT_FAILED = 1003;  //连接失败
    public static final int MSG_CONNECTING = 10021;  //连接失败
    public static final int MSG_SEND_CMD_FAILED = 1004;  //发送CMD失败
    public static final int MSG_SEND_BODY_FAILED = 1005;  //发送BODY失败
    public static final int MSG_SEND_CHECK_SUCCESSFUL = 1006;  //检验成功
    public static final int MSG_DISCONNECT_SUCCESSFUL = 1007;  //断开连接成功
    public static final int MSG_PLAY_SUCCESSFUL = 1008;  //播放第一帧
    public final P2PSDK mP2PSDK;
    public int mSession;
    private boolean isSendPullCMD = false;
    public String TAG = "RinoApi";
    public final byte CH_CMD = 0;  //信令通道
    public final byte CH_DATA = 1;  //媒体数据通道
    private static boolean waitFlag = false;  //等候读取标志
    private static boolean rawFlag = false;  //读取音视频标志
    private boolean sps_pps = false; //首帧提取sps pps
    //    private MediaCodecUtil mMediaCodecUtil;  //视频解码工具类
//    private AACDecoderUtil mAudioDeUtil; //音频解码工具类
    private AudioRecordThread mAudioRecord; //录音
    //播放+渲染标志
    private static boolean playShowFlag = false;
    //    //音频
//    private final LinkedBlockingQueue<byte[]> audioEncodedLinkQueue = new LinkedBlockingQueue<>();
//    //视频
//    private final LinkedBlockingQueue<byte[]> videoEncodedLinkQueue = new LinkedBlockingQueue<>();
    //音频buffer
    private static final int audioSize = 50;
    //视频buffer
    private static final int videoSize = 15 * 2;  //2s
    private int seq_audio = 0;  //录音索引
    private Map<Integer, MediaCodecUtil> mMediaCodecUtilMap;
    private Map<Integer, AACDecoderUtil> mAACDecoderUtilMap;
    private Map<Integer, LinkedBlockingQueue<byte[]>> mAudioEncodedLinkQueueMap;
    private Map<Integer, LinkedBlockingQueue<byte[]>> mVideoEncodedLinkQueueMap;
    private Map<Integer, Boolean> mRawFlagMap;
    private Map<Integer, Boolean> mWaitFlagMap;
    private Map<Integer, Boolean> mPlayShowFlagMap;

    public RinoSDK(Context mContext, Handler mHandle, SurfaceHolder mHolder) {
        this.mContext = mContext;
        this.mHandle = mHandle;
//        this.mMediaCodecUtil = new MediaCodecUtil(mHolder);
//        this.mAudioDeUtil = new AACDecoderUtil();
        this.mAudioRecord = new AudioRecordThread(this, mContext.getApplicationContext().getFilesDir().getPath() + File.separator);
        mP2PSDK = P2PSDK.getInstance();
        mP2PSDK.configP2PSDK(mContext);
        mMediaCodecUtilMap = new HashMap<>();
        mAACDecoderUtilMap = new HashMap<>();
        mAudioEncodedLinkQueueMap = new HashMap<>();
        mVideoEncodedLinkQueueMap = new HashMap<>();
        mRawFlagMap = new HashMap<>();
        mWaitFlagMap = new HashMap<>();
        mPlayShowFlagMap = new HashMap<>();
    }

    public void putCodec(String role, MediaCodecUtil mediaCodecUtil, AACDecoderUtil aacDecoderUtil) {
        mMediaCodecUtilMap.put(Integer.valueOf(role), mediaCodecUtil);
        mAACDecoderUtilMap.put(Integer.valueOf(role), aacDecoderUtil);
        mAudioEncodedLinkQueueMap.put(Integer.valueOf(role), new LinkedBlockingQueue<>());
        mVideoEncodedLinkQueueMap.put(Integer.valueOf(role), new LinkedBlockingQueue<>());
        mRawFlagMap.put(Integer.valueOf(role), false);
        mWaitFlagMap.put(Integer.valueOf(role), false);
        mPlayShowFlagMap.put(Integer.valueOf(role), false);
    }


    @Override
    public void OnRecordData(byte[] outData) {
        //录音数据

        if (mP2PSDK != null) {
            Log.e(TAG, "OnRecordData old data size:" + outData.length);
            seq_audio++;
            long timeStamp = System.currentTimeMillis();

            byte[] sendHeadData = sendHeadDataByteToArray(outData.length, P2PSDK.P2P_AUDIO_FRAME, seq_audio, timeStamp);
            int gRet = mP2PSDK.sendData(mSession, CH_DATA, sendHeadData, sendHeadData.length);
            if (gRet > 0) {
                //发送音频Data
                gRet = mP2PSDK.sendData(mSession, CH_DATA, outData, outData.length);
            }
        }
    }

    /*
     *构造发送Head
     * rawData  音视频数据
     * rawType  true: 视频 false: 音频
     * */
    private byte[] sendHeadDataByteToArray(int u32BufSiz, int eType, int u32Seq, long u64Timestamp) {
        byte[] sendHead = new byte[40]; //40字节
        //u32BufSiz
        byte[] u32BufSizeLittle = RINO_APIs.intToByteArray(u32BufSiz); //4字节-小端
        Log.e("sendAudioData", "length" + u32BufSizeLittle.length + "u32BufSizeLittle:" + byte2hex(u32BufSizeLittle));
        System.arraycopy(u32BufSizeLittle, 0, sendHead, 4, u32BufSizeLittle.length);
        Log.e("sendAudioData", "length" + sendHead.length + "sendHead:" + byte2hex(sendHead));

        //eType
        byte[] eTypeLittle = RINO_APIs.intToByteArray(eType); //4字节-小端
        Log.e("sendAudioData", "length" + eTypeLittle.length + "eTypeLittle:" + byte2hex(eTypeLittle));
        System.arraycopy(eTypeLittle, 0, sendHead, 8, eTypeLittle.length);
        Log.e("sendAudioData", "length" + sendHead.length + "sendHead:" + byte2hex(sendHead));

        //u32Seq
        byte[] u32SeqLittle = RINO_APIs.intToByteArray(u32Seq); //4字节-小端
        Log.e("sendAudioData", "length" + u32SeqLittle.length + "u32SeqLittle:" + byte2hex(u32SeqLittle));
        System.arraycopy(u32SeqLittle, 0, sendHead, 12, u32SeqLittle.length);
        Log.e("sendAudioData", "length" + sendHead.length + "sendHead:" + byte2hex(sendHead));

        //u64Timestamp
        byte[] u64TimestampLittle = RINO_APIs.intToByteArray_8(u64Timestamp); //8字节-小端
        Log.e("sendAudioData", "u64Timestamp" + u64Timestamp + "length" + u64TimestampLittle.length + "u64TimestampLittle:" + byte2hex(u64TimestampLittle));
        System.arraycopy(u64TimestampLittle, 0, sendHead, 16, u64TimestampLittle.length);
        Log.e("sendAudioData", "length" + sendHead.length + "sendHead:" + byte2hex(sendHead));

        //video
        if (eType == P2PSDK.P2P_VIDEO_PFRAME || eType == P2PSDK.P2P_VIDEO_IFRAME) {

            int u32Width = 1920;
            int u32Height = 1080;
            //u32Width
            byte[] u32WidthLittle = RINO_APIs.intToByteArray(u32Width); //4字节-小端
            Log.e("sendAudioData", "length" + u32WidthLittle.length + "u32WidthLittle:" + byte2hex(u32WidthLittle));
            System.arraycopy(u32WidthLittle, 0, sendHead, 24, u32WidthLittle.length);
            Log.e("sendAudioData", "length" + sendHead.length + "sendHead:" + byte2hex(sendHead));

            //u32Height
            byte[] u32HeightLittle = RINO_APIs.intToByteArray(u32Height); //4字节-小端
            Log.e("sendAudioData", "length" + u32HeightLittle.length + "u32HeightLittle:" + byte2hex(u32HeightLittle));
            System.arraycopy(u32HeightLittle, 0, sendHead, 28, u32HeightLittle.length);
            Log.e("sendAudioData", "length" + sendHead.length + "sendHead:" + byte2hex(sendHead));

            //v_eFormat
            byte[] v_eFormatLittle = RINO_APIs.intToByteArray(P2PSDK.P2P_VIDEO_H264); //4字节-小端
            Log.e("sendAudioData", "length" + v_eFormatLittle.length + "v_eFormatLittle:" + byte2hex(v_eFormatLittle));
            System.arraycopy(v_eFormatLittle, 0, sendHead, 32, v_eFormatLittle.length);
            Log.e("sendAudioData", "length" + sendHead.length + "sendHead:" + byte2hex(sendHead));

            //audio
        } else if (eType == P2PSDK.P2P_AUDIO_FRAME) {

            //u32Samplerate
            byte[] u32SamplerateLittle = RINO_APIs.intToByteArray(P2PSDK.P2P_SAMPLE_RATE); //4字节-小端
            Log.e("sendAudioData", "length" + u32SamplerateLittle.length + "u32SamplerateLittle:" + byte2hex(u32SamplerateLittle));
            System.arraycopy(u32SamplerateLittle, 0, sendHead, 24, u32SamplerateLittle.length);
            Log.e("sendAudioData", "length" + sendHead.length + "sendHead:" + byte2hex(sendHead));

            //a_eFormat
            byte[] a_eFormatLittle = RINO_APIs.intToByteArray(P2PSDK.P2P_AUDIO_AAC); //4字节-小端
            Log.e("sendAudioData", "length" + a_eFormatLittle.length + "a_eFormatLittle:" + byte2hex(a_eFormatLittle));
            System.arraycopy(a_eFormatLittle, 0, sendHead, 28, a_eFormatLittle.length);
            Log.e("sendAudioData", "length" + sendHead.length + "sendHead:" + byte2hex(sendHead));

        }

        return sendHead;
    }


    /**
     * 消息头
     */
    public static class P2P_head implements Serializable {
        int u32HeadTag;  //消息头
        short u16CmdId;    //命令字
        int u32MsgBodyLen; //命令参数长度
        char u8Version;     //通信协议版本
        char u8segment;     //视频buf是否分段
        char u8SignCode;    //标识码
        char u8IsResponse;   //是否应答
        char u8DevType;      //设备类型
    }

    public static class P2P_command_data implements Serializable {
        P2P_head head;
        char[] pJsonMsgbody;
    }


    private boolean isInitLock = false;

    /**
     * 同一个ID，只有一个插件，且因为一个设备只有一个did，所有之对应一个session
     * 初始化-连接
     * initString 为initString + ":" + p2pKey
     */
    public int initRinoSDK(String DID, String initString) {
        if (mSession > 0 || isInitLock) {
            mHandle.sendEmptyMessage(MSG_CONNECTING);
            return 0;
        }
        isInitLock = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int gRet = mP2PSDK.initializeP2P(initString);  //初始化
                mHandle.sendEmptyMessage(MSG_CONNECTING);
                if (gRet == PPCS_APIs.ERROR_PPCS_SUCCESSFUL) {

                    int gDet = mP2PSDK.networkDetect(initString); //检测⽹络相关信息

                    if (gDet == PPCS_APIs.ERROR_PPCS_SUCCESSFUL) {
                        int connect_time = 0;
                        boolean useByServer = false;
                        long read_time, max_time = 0, min_time = 0, time_count = 0, success_time = 0, p2p_count = 0,
                                lan_count = 0, rly_count = 0, tcp_count = 0;
                        int gDelaySec = 3;
                        String logStr;
                        while (connect_time < P2PSDK.REPEAT) {

                            connect_time += 1;
                            long start_time = System.currentTimeMillis();
                            if (useByServer) {
                                mSession = mP2PSDK.connectByServer(DID, P2PSDK.BENABLE_LAN_SEARCH, 0, initString);
                            } else {
                                mSession = mP2PSDK.connect(DID, P2PSDK.BENABLE_LAN_SEARCH, 0);
                            }

                            long stop_time = System.currentTimeMillis();
                            double time_use = (stop_time - start_time) / 1000f;

                            String wakeupStr = "";

                            if (mSession < 0) {
                                LogUtil.d(String.format("[%s] %02d-%sConnect failed Time=%.2f sec ret=%d[%s]\n", RINO_APIs.getTimeString(),
                                        connect_time, wakeupStr, (stop_time - start_time) / 1000.0, mSession, P2PSDK.getP2PErrorMessage(mSession)));
                                if (connect_time + 1 >= P2PSDK.REPEAT) {
                                    mHandle.sendEmptyMessage(MSG_CONNECT_FAILED);
                                    isInitLock = false;
                                }
                                RINO_APIs.mSleep(300);
                                continue;
                            }

                            if (connect_time == 1) min_time = stop_time - start_time;
                            success_time++;
                            time_count += (stop_time - start_time);
                            max_time = Math.max((stop_time - start_time), max_time);
                            min_time = Math.min((stop_time - start_time), min_time);

                            // check session info
                            SessionModel info = mP2PSDK.checkSession(mSession);
                            if (info.mode.contains("LAN")) {
                                lan_count++;
                            } else if (info.mode.contentEquals("P2P")) {
                                p2p_count++;
                            } else if (info.mode.contentEquals("RLY")) {
                                rly_count++;
                            } else {
                                tcp_count++;
                                gDelaySec = Math.max(3, 3);
                            }

                            logStr = String.format("%02d-%sSession=%d, Mode=%s, Skt=%d, RemoteAddr=%s:%d, Time=%.2f sec",
                                    connect_time, wakeupStr, mSession, info.mode, info.skt,
                                    info.remote_ip, info.remote_port, (stop_time - start_time) / 1000.0);

                            LogUtil.d(logStr);

                            byte[] readBuff = new byte[1];
                            int[] readSize = new int[1];
                            int timeout = (info.mode.equals("TCP")) ? 3500 : 1000;
                            start_time = System.currentTimeMillis();
                            for (int item = 0; item < 2; item++) {

                                readSize[0] = 1;
                                gRet = mP2PSDK.recvData(mSession, CH_CMD, readBuff, readSize, timeout);
                                if (gRet < 0 && readSize[0] == 0) {
                                    if (gRet == PPCS_APIs.ERROR_PPCS_TIME_OUT) {
                                        continue;
                                    } else if (gRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_TIMEOUT) {
                                        LogUtil.d(String.format("%02d-Session Close TimeOut!!", connect_time));
                                    } else if (gRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_REMOTE) {
                                        LogUtil.d(String.format("%02d-Session Remote Close!!", connect_time));
                                    } else {
                                        LogUtil.d(String.format("%02d-PPCS_Read failed ret=%d[%s]",
                                                connect_time, gRet, P2PSDK.getP2PErrorMessage(gRet)));
                                    }
                                    break;
                                } else if (gRet != PPCS_APIs.ERROR_PPCS_INVALID_PARAMETER &&
                                        gRet != PPCS_APIs.ERROR_PPCS_INVALID_SESSION_HANDLE &&
                                        readSize[0] > 0) {

                                    LogUtil.d(String.format("%02d PPCS_Read: ret=%d,Session=%d,CH=%d,ReadSize=%d => [%d]",
                                            connect_time, gRet, mSession, CH_CMD, readSize[0], readBuff[0]));

                                    int sendData = readBuff[0];
                                    sendData = sendData & 0xFE;
                                    byte[] bytes = RINO_APIs.intToByteArray(sendData);

                                    gRet = mP2PSDK.sendData(mSession, CH_CMD, bytes, bytes.length);
                                    if (gRet < 0) {
                                        LogUtil.e("PPCS_Write failed ret=" + gRet + " " + P2PSDK.getP2PErrorMessage(gRet));
                                    } else {
                                        LogUtil.d(String.format("%02d PPCS_Write: ret=%d,session=%d,ch=%d, sendSize=%d ==>[%d]",
                                                connect_time, gRet, mSession, CH_CMD, bytes.length, sendData));
                                    }
                                    break;
                                } else {
                                    LogUtil.d(String.format("%02d PPCS_Read: Session=%d,CH=%d,ReadSize=%d,ret=%d [%s]",
                                            connect_time, mSession, CH_CMD, readSize[0], gRet, P2PSDK.getP2PErrorMessage(gRet)));
                                }
                            }

                            stop_time = System.currentTimeMillis();
                            read_time = stop_time - start_time;

                            if (connect_time < P2PSDK.REPEAT) {
                                long sleepTime = gDelaySec * 1000L - read_time;
                                LogUtil.d(String.format("%02d-sleep time(ms): %d", connect_time, sleepTime));
                                while (sleepTime > 0) {
                                    RINO_APIs.mSleep(sleepTime > 100 ? 100 : sleepTime);
                                    sleepTime -= 100;
                                }
                                LogUtil.d(String.format("%02d-sleep done.", connect_time));
                            }

                            if (success_time > 0) {
                                logStr = String.format("%s(%.2f%%, max=%.2f sec average=%.2f sec min=%.2f sec)\n", logStr,
                                        success_time * 100.0 / connect_time, max_time / 1000.0, (time_count / 1000.0) / connect_time, min_time / 1000.0);
                                logStr = String.format("%sLAN: %d (%.2f%%), P2P: %d (%.2f%%), RLY: %d (%.2f%%), TCP: %d (%.2f%%)\n", logStr,
                                        lan_count, lan_count * 100.0 / success_time,
                                        p2p_count, p2p_count * 100.0 / success_time,
                                        rly_count, rly_count * 100.0 / success_time,
                                        tcp_count, tcp_count * 100.0 / success_time);


                                mHandle.sendEmptyMessage(MSG_CONNECT_SUCCESSFUL);
                                isInitLock = false;
                            } else {
                                mHandle.sendEmptyMessage(MSG_CONNECT_FAILED);
                                isInitLock = false;
                                logStr = logStr + "\nLAN: 0 (0.00%), P2P: 0 (0.00%), RLY: 0 (0.00%), TCP: 0 (0.00%)\n";
                            }

                            LogUtil.d(logStr);
                        }
                    }

                } else {

                    mHandle.sendEmptyMessage(MSG_INIT_FAILED);
                    isInitLock = false;
                }
            }
        }).start();

        return 0;
    }


    /*
     * 等候读取数据校验
     * */
    private void waitReadData() {
        waitFlag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (waitFlag && count <= 5) {
                    byte[] pRecvP2PBuf = new byte[4];
                    int[] readSize = new int[1];
                    readSize[0] = 4;
                    int gRet = mP2PSDK.recvData(mSession, CH_CMD, pRecvP2PBuf, readSize, 1000);
                    if (gRet < 0) {
                        count++;
                    } else {
                        byte[] vsnFileByte = new byte[pRecvP2PBuf.length];
                        System.arraycopy(pRecvP2PBuf, 0, vsnFileByte, 0, vsnFileByte.length);
                        byte[] vsnFileByteLimt = new byte[4];
                        vsnFileByteLimt[3] = vsnFileByte[0];
                        vsnFileByteLimt[2] = vsnFileByte[1];
                        vsnFileByteLimt[1] = vsnFileByte[2];
                        vsnFileByteLimt[0] = vsnFileByte[3];

                        //校验头
                        if (RINO_APIs.checkCmdByteToBool(vsnFileByteLimt)) {
                            /*
                            waitFlag = false;
                            mHandle.sendEmptyMessage(MSG_SEND_CHECK_SUCCESSFUL);

                             */
                        }
                    }

                    waitFlag = false;


                }
                waitFlag = false;
            }
        }).start();

        mHandle.sendEmptyMessage(MSG_SEND_CHECK_SUCCESSFUL);

        //播放媒体
//        startPlayMedia();
    }

    /**
     * 播放+渲染
     * */
    /**
     * aac播放
     */
    private void rawDataPlayOrShow(int role, MediaCodecUtil mMediaCodecUtil, AACDecoderUtil mAudioDeUtil, LinkedBlockingQueue<byte[]> audioEncodedLinkQueue, LinkedBlockingQueue<byte[]> videoEncodedLinkQueue) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mPlayShowFlagMap.get(role)) {
                    if (audioEncodedLinkQueue.size() > audioSize) {
                        try {
                            if (mAudioDeUtil != null) {
                                byte[] audioData = audioEncodedLinkQueue.take();
                                mAudioDeUtil.decode(audioData, 0, audioData.length);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (videoEncodedLinkQueue.size() > videoSize) {
                        try {
                            if (mMediaCodecUtil != null) {
                                byte[] videoData = videoEncodedLinkQueue.take();
                                mMediaCodecUtil.onFrame(videoData, 0, videoData.length);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //延时较大
                    if (videoEncodedLinkQueue.size() > videoSize + 15 * 2) {
                        videoEncodedLinkQueue.clear();
                    }

                    if (audioEncodedLinkQueue.size() > audioSize + 20) {
                        videoEncodedLinkQueue.clear();
                    }

                }
                //取空数据
                while (audioEncodedLinkQueue.size() > 0 || videoEncodedLinkQueue.size() > 0) {
                    try {
                        if (mAudioDeUtil != null && audioEncodedLinkQueue.size() > 0) {
                            byte[] audioData = audioEncodedLinkQueue.take();
                            mAudioDeUtil.decode(audioData, 0, audioData.length);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (mMediaCodecUtil != null && videoEncodedLinkQueue.size() > 0) {
                            byte[] videoData = videoEncodedLinkQueue.take();
                            mMediaCodecUtil.onFrame(videoData, 0, videoData.length);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }


    /***
     * 读取音视频数据
     * */
    /*
    private FileOutputStream fops;
    private String SD_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    */
    public void startPlayMedia(int packageSize, int role) {
        /*
        String savePath;
        String fileName = "recv.aac";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            Toast.makeText(mContext, "保存失败！", Toast.LENGTH_SHORT).show();
            return ;
        }
        File file = new File(savePath + fileName);
        try {
            fops = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        */
//        rawFlag = true;
//        playShowFlag = true;
        mRawFlagMap.put(role, true);
        mPlayShowFlagMap.put(role, true);
        MediaCodecUtil mMediaCodecUtil = mMediaCodecUtilMap.get(role);
        AACDecoderUtil mAudioDeUtil = mAACDecoderUtilMap.get(role);
        LinkedBlockingQueue<byte[]> audioEncodedLinkQueue = mAudioEncodedLinkQueueMap.get(role);
        LinkedBlockingQueue<byte[]> videoEncodedLinkQueue = mVideoEncodedLinkQueueMap.get(role);
        if (mMediaCodecUtil != null) {
            mMediaCodecUtil.startCodec(); //开启解码
        }
        if (mAudioDeUtil != null) {
            mAudioDeUtil.start();
        }
        if (mAudioRecord != null && !mAudioRecord.isAlive()) {
            mAudioRecord.start();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //清除缓冲区
                audioEncodedLinkQueue.clear();
                videoEncodedLinkQueue.clear();
                //等待解码
                rawDataPlayOrShow(role, mMediaCodecUtil, mAudioDeUtil, audioEncodedLinkQueue, videoEncodedLinkQueue);
                //获取head大小
//                int len = RINO_APIs.getMediaPacketSize();
                int len = packageSize;
                byte[] readBuff = new byte[len];
                while (mRawFlagMap.get(role)) {
                        /*
                        Arrays.fill(readBuff, (byte) 0);
                         */
                    int[] readSize = new int[1];
                    readSize[0] = len;
                    Log.e("recvRawData", "开始读channel:" + role);
                    int s32Ret = mP2PSDK.recvData(mSession, (byte) role, readBuff, readSize, 1000);
                    Log.e("recvRawData", "开始读channel:" + role + "，结果：" + +s32Ret);
//                    Log.e("recvRawData", "s32Ret:" + byte2hex(readBuff));
                    int repairLen = 0;
                    if (len > 40) {
                        repairLen = 4;
                    }
                    if (s32Ret >= 0) {
                        //帧类型
                        byte[] seqHex = new byte[4];
                        System.arraycopy(readBuff, 12 + repairLen, seqHex, 0, 4);
                        int seq = ByteBuffer.wrap(seqHex).order(ByteOrder.LITTLE_ENDIAN).getInt(); //按照小端顺序读取数据
                        //帧类型
                        byte[] eTypeHex = new byte[4];
                        System.arraycopy(readBuff, 8 + repairLen, eTypeHex, 0, 4);
                        int eType = ByteBuffer.wrap(eTypeHex).order(ByteOrder.LITTLE_ENDIAN).getInt(); //按照小端顺序读取数据
                        //帧大小
                        byte[] u32BufSize = new byte[4]; //u32BufSize pDataBuf数据大小
                        System.arraycopy(readBuff, 4 + repairLen, u32BufSize, 0, 4);
                        int sizeBuf = ByteBuffer.wrap(u32BufSize).order(ByteOrder.LITTLE_ENDIAN).getInt(); //按照小端顺序读取数据
                        Log.e("recvRawData", "Role:" + role + "sizeBuf:" + sizeBuf + "eType:" + eType + "seq:" + seq);
                                /*
                                Log.e("recvRawData", "s32Ret:" + byte2hex(u32BufSize));
                                Log.e("recvRawData", "s32Ret:" + byte2hex(changeBytesEndian(u32BufSize)));
                                 */
                        if (sizeBuf > 0 && seq >= 0 && (eType == P2PSDK.P2P_AUDIO_FRAME || eType == P2PSDK.P2P_VIDEO_IFRAME || eType == P2PSDK.P2P_VIDEO_PFRAME)) {
                            int[] readPDataBufSzie = new int[1];
                            readPDataBufSzie[0] = sizeBuf;
                            byte[] pstDataBuf = new byte[sizeBuf];
                            s32Ret = mP2PSDK.recvData(mSession, (byte) role, pstDataBuf, readPDataBufSzie, 1000);
                            if (eType == P2PSDK.P2P_AUDIO_FRAME) {  //音频帧
                                if (mAudioDeUtil != null) {
                                    Log.e("recvRawData", "Role:" + role + "aac audio");
                                    audioEncodedLinkQueue.add(pstDataBuf);
                                            /*
                                            mAudioUtil.decode(pstDataBuf, 0, sizeBuf);
                                             */
                                }
                            } else if (eType == P2PSDK.P2P_VIDEO_IFRAME) { //I视频帧
                                if (mMediaCodecUtil != null) {
                                    Log.e("recvRawData", "Role:" + role + "h264 video I视频帧");
                                    videoEncodedLinkQueue.add(pstDataBuf);
                                            /*
                                            mMediaCodecUtil.onFrame(pstDataBuf, 0, sizeBuf);
                                            */
                                }
                            } else if (eType == P2PSDK.P2P_VIDEO_PFRAME) { //P视频帧
                                if (mMediaCodecUtil != null) {
                                    Log.e("recvRawData", "Role:" + role + "h264 video P视频帧");
                                    if (videoEncodedLinkQueue.size() > 0) {  //确保第一帧为I帧
                                        videoEncodedLinkQueue.add(pstDataBuf);
                                    }
                                            /*
                                            mMediaCodecUtil.onFrame(pstDataBuf, 0, sizeBuf);
                                            */
                                }
                            }
                                    /*
                                    Log.e("pstDataBuf", "s32Ret:" + s32Ret);
                                     */
                            if (s32Ret >= 0) {
                                        /*
                                        try {
                                            fops.write(pstDataBuf);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        */
                                        /*
                                        if(!sps_pps){
                                            int sps_index_end = 0;
                                            int pps_index_end = 0;
                                            for (int i = 0; i <= sizeBuf; i ++) {
                                                if (pstDataBuf[i] == 0x67) { //sps
                                                    Log.e("帧NAL","SPS:" + i);
                                                }else if(pstDataBuf[i] == 0x68){  //pps
                                                    sps_index_end = i - 5;
                                                    Log.e("帧NAL","PPS:" + i);
                                                }else if(pstDataBuf[i] == 0x65){  //I
                                                    pps_index_end = i - 5;
                                                    Log.e("帧NAL","I:" + i);
                                                    break;
                                                }
                                            }

                                            //sps
                                            int sps_count = sps_index_end + 1;
                                            byte []sps_frame = new byte[sps_count];
                                            System.arraycopy(pstDataBuf, 0, sps_frame, 0, sps_count);
                                            Log.e("recvframeData", "sps_frame:" + byte2hex(sps_frame));
                                            //pps
                                            int pps_count = pps_index_end - sps_index_end;
                                            byte []pps_frame =  new byte[pps_count];
                                            System.arraycopy(pstDataBuf, sps_count, pps_frame, 0, pps_count);
                                            Log.e("recvframeData", "pps_frame:" + byte2hex(pps_frame));
                                            //I
                                            int i_count = sizeBuf - (pps_index_end + 1);
                                            byte []i_frame = new byte[i_count];
                                            System.arraycopy(pstDataBuf, pps_index_end + 1, i_frame, 0, i_count);
                                            Log.e("recvframeData", "i_frame:" + byte2hex(i_frame));

                                            sps_pps = true;

                                        }else{
                                            //其他帧 //包括I帧

                                        }
                                        */

                            }

                        }
                    }
                }
            }
        }).start();


    }


    /**
     * 切换大小端
     */
    private static byte[] changeBytesEndian(byte[] x) {
        byte[] y = new byte[x.length];
        for (int i = 0; i < y.length; i++) {
            y[i] = x[y.length - i - 1];
        }
        return y;
    }

    //开启-停止对讲
    public int startRinoTalkControl(boolean status) {
        seq_audio = 0;
        if (mAudioRecord != null) {
            mAudioRecord.setAudiSend(status);
        }
        return 0;
    }


    /*
     * 释放SDK
     * */
    public int releaseRinoSDK() {
        /*
        try {
            fops.flush();
            fops.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
//        rawFlag = false;  //退出接收
//        waitFlag = false; //终止等待
//        playShowFlag = false; //结束渲染
        seq_audio = 0;


        try {
            for (int key : mMediaCodecUtilMap.keySet()) {
                MediaCodecUtil mMediaCodecUtil = mMediaCodecUtilMap.get(key);
                AACDecoderUtil mAudioDeUtil = mAACDecoderUtilMap.get(key);
                LinkedBlockingQueue<byte[]> audioEncodedLinkQueue = mAudioEncodedLinkQueueMap.get(key);
                LinkedBlockingQueue<byte[]> videoEncodedLinkQueue = mVideoEncodedLinkQueueMap.get(key);
                if (mMediaCodecUtil != null) {
                    mMediaCodecUtil.stopCodec();
                    mMediaCodecUtil = null;
                }
                if (mAudioDeUtil != null) {
                    mAudioDeUtil.stop();
                    mAudioDeUtil = null;
                }
                audioEncodedLinkQueue.clear();
                videoEncodedLinkQueue.clear();
                mRawFlagMap.put(key, false);
                mPlayShowFlagMap.put(key, false);
            }
//            if (mMediaCodecUtil != null) {
//                mMediaCodecUtil.stopCodec();
//                mMediaCodecUtil = null;
//            }
//            if (mAudioDeUtil != null) {
//                mAudioDeUtil.stop();
//                mAudioDeUtil = null;
//            }

            if (mAudioRecord != null) {
                mAudioRecord.setAudiSend(false);
                mAudioRecord.stopRecord();
                mAudioRecord.releaseRecord();
                mAudioRecord = null;
            }
        } catch (Exception e) {
            Log.e("releaseRinoSDK", e.toString());
        }

        int gRet = mP2PSDK.deInitializeP2P(mSession);
        if (gRet >= 0) {
            mHandle.sendEmptyMessage(MSG_DISCONNECT_SUCCESSFUL);
        }
        mSession = -1;

        return gRet;
    }

    public String byte2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            //将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

    public static byte[] getBytes(char[] chars) {
        if (chars == null || chars.length <= 0) {

            return null;
        }
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    // convert internal Java String format to UTF-8
    public static String convertStringToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public interface SendDataCallBack {
        void success();

        void failed();
    }

    public void sendData(JSONObject jsonObject, SendDataCallBack sendDataCallBack) {
        int gRet = -1;
        byte[] sendMsgCmd = RINO_APIs.getStructCmdByteToArray(jsonObject.toString());
        gRet = mP2PSDK.sendData(mSession, CH_CMD, sendMsgCmd, sendMsgCmd.length);
        if (gRet < 0) {
            sendDataCallBack.failed();
            return;
        }

        byte[] sendJsonMsgCmd = RINO_APIs.getCmdJsonByteToArray(jsonObject.toString());
        gRet = mP2PSDK.sendData(mSession, CH_CMD, sendJsonMsgCmd, sendJsonMsgCmd.length);
        if (gRet < 0) {
            sendDataCallBack.failed();
            return;
        }
        sendDataCallBack.success();
    }


    /**
     * 发送开流指令
     */
    private boolean isSendLock = false;

    public int startRinoMedia(JSONObject item, SendDataCallBack sendDataCallBack) {
        if (isSendPullCMD || isSendLock) {
            sendDataCallBack.success();
            return 0;
        }
        isSendLock = true;
        waitReadData();  //等候读取数据校验

        new Thread(new Runnable() {
            @Override
            public void run() {

//                JSONObject item = new JSONObject();
//                try {
//                    item.put("width",1920);
//                    item.put("height",1080);
//                    item.put("vformat",0);
//                    item.put("samplerate",16000);
//                    item.put("afeormat",5);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                int gRet = -1;
                byte[] sendMsgCmd = RINO_APIs.getStructCmdByteToArray(item.toString());
                gRet = mP2PSDK.sendData(mSession, CH_CMD, sendMsgCmd, sendMsgCmd.length);
                if (gRet < 0) {
                    mHandle.sendEmptyMessage(MSG_SEND_CMD_FAILED);
                    sendDataCallBack.failed();
                    isSendLock = false;
                    return;
                }

                byte[] sendJsonMsgCmd = RINO_APIs.getCmdJsonByteToArray(item.toString());

                gRet = mP2PSDK.sendData(mSession, CH_CMD, sendJsonMsgCmd, sendJsonMsgCmd.length);
                if (gRet < 0) {
                    mHandle.sendEmptyMessage(MSG_SEND_BODY_FAILED);
                    sendDataCallBack.failed();
                    isSendLock = false;
                    return;
                }
                sendDataCallBack.success();
                isSendPullCMD = true;
                isSendLock = false;
                int objlen = 1024;
                byte[] pRecvP2PBuf = new byte[objlen];
                int[] readSize = new int[1];
                readSize[0] = objlen;
                int count = 0;
                while (count++ < 20) {
                    gRet = mP2PSDK.recvData(mSession, CH_CMD, pRecvP2PBuf, readSize, 1000);
                    if (gRet < 0) {
                        continue;
                    }

                    Log.e("PPCS_APIs_Read", "gRet:" + gRet);

                    RINO_APIs.mSleep(1000);

                    break;
                }

            }
        }).start();
        return 0;
    }

    /*
     * 序列化数组
     * */
    public byte[] objectToByte(Object obj) throws IOException {
        byte[] bytes;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception ae) {
            throw ae;
        }
        return (bytes);
    }

    private static String hexStr = "0123456789ABCDEF";

    public static byte[] HexStringToByteArray(String hexString) {
        //hexString的长度对2取整，作为bytes的长度
        int len = hexString.length() / 2;
        byte[] bytes = new byte[len];
        byte high = 0;//字节高四位
        byte low = 0;//字节低四位

        for (int i = 0; i < len; i++) {
            //右移四位得到高位
            high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
            low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
            bytes[i] = (byte) (high | low);//高地位做或运算
        }
        return bytes;

    }


    /*
     * 构造头
     * */
    private P2P_head P2PHead() {

        String u8Version = String.format("%02X", 11);
        String usegment = String.format("%02X", 0);
        String u8SignCode = String.format("%02X", 11);
        String u8IsResponse = String.format("%02x", 1);
        String u8DevType = String.format("%02X", 12);


        byte[] u8VersionBytes = HexStringToByteArray(u8Version);
        byte[] u8segmentBytes = HexStringToByteArray(usegment);
        byte[] u8SignCodeBytes = HexStringToByteArray(u8SignCode);
        byte[] u8IsResponseBytes = HexStringToByteArray(u8IsResponse);
        byte[] u8DevTypeBytes = HexStringToByteArray(u8DevType);

        P2P_head head = new P2P_head();
        head.u32HeadTag = P2PSDK.RinP2PHeadTag;
        head.u8Version = (char) (u8VersionBytes[0] & 0xff);
        head.u8segment = (char) (u8segmentBytes[0] & 0xff);
        head.u8SignCode = (char) (u8SignCodeBytes[0] & 0xff);
        head.u8IsResponse = (char) (u8IsResponseBytes[0] & 0xff);
        head.u8DevType = (char) (u8DevTypeBytes[0] & 0xff);


        return head;
    }

    /**
     * 字符串补0
     *
     * @param str
     * @param strLength
     * @return
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                // sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        str = String.format(str).toUpperCase();//转为大写
        return str;
    }

}
