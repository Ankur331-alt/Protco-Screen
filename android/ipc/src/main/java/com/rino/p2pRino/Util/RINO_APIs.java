package com.rino.p2pRino.Util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Random;

@SuppressLint("SimpleDateFormat")
public class RINO_APIs {

    static { System.loadLibrary("RINO_API"); }
    public static native int getSocketType(int skt);
    public static native byte[] getStructCmdByteToArray(String jsonStr);
    public static native byte[] getCmdJsonByteToArray(String jsonStr);
    public static native byte[] sendAudioDataByteToArray(int frameSize, long seq, long u64Timestamp, byte[] pDataBuf);
    public static native boolean checkCmdByteToBool(byte []checkHead);
    public static native int getMediaPacketSize();

    public static int mSleepCount = 0;
    public static int mSleepWrongCount = 0;
    public static int mSleepErrorCount = 0;

    public static int iPN_StringEnc(byte[] keystore, byte[] src, byte[] dest,  int maxsize) {
        String tmp = new String(src, 0, getValidLength(src));
        LogUtil.d("EncSrc=" + tmp + ", length=" + tmp.length() + ", valid length=" + getValidLength(src));
        int[] Key = new int[17];
        int i;
        int s, v;
        if(maxsize < src.length * 2 + 3)
        {
            return -1;
        }
        for(i = 0 ; i < 16; i++){
            Key[i] = keystore[i];
        }

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        s = (random.nextInt()&Integer.MAX_VALUE) % 256;
        Arrays.fill(dest,(byte) 0);
        dest[0] = (byte) ('A' + ((s & 0xF0) >> 4));
        dest[1] = (byte) ('a' + (s & 0x0F));
        for(i = 0; i < src.length; i++)
        {
            v = s ^ Key[(i + s * (s % 23))% 16] ^ src[i];
            dest[2 * i + 2] = (byte) ('A' + ((v & 0xF0) >> 4));
            dest[2 * i + 3] = (byte) ('a' + (v & 0x0F));
            s = v;
        }
        tmp = new String(dest, 0, getValidLength(dest));
        LogUtil.d("EncDest=" + tmp + ", length=" + tmp.length() + ", valid length=" + getValidLength(dest));
        return 0;
    }

    public static int iPN_StringDnc(byte[] keystore, byte[] src, byte[] dest,  int maxsize) {
        String tmp = new String(src, 0, getValidLength(src));
        int[] Key = new int[17];
        int i,count=0;
        int s, v;
        for(i = 0;i < src.length;i++){
            if(src[i]==0){
                break;
            }
            count++;
        }
        LogUtil.d("DncSrc=" + tmp + ", count=" + count + ", valid length=" + getValidLength(src));
        if((maxsize < count / 2) || (count  % 2 == 1))
        {
            return -1;
        }
        for(i = 0 ; i < 16; i++){
            Key[i] = keystore[i];
        }
        Arrays.fill(dest,(byte) 0);
        s = ((src[0] - 'A') << 4) + (src[1] - 'a');
        for(i = 0; i < count / 2 - 1; i++)
        {
            v = ((src[i * 2 + 2] - 'A') << 4) + (src[i * 2 + 3] - 'a');
            dest[i] = (byte) (v ^ Key[(i + s * (s % 23))% 16] ^ s);
            if(dest[i] < 32) return -1; // not a valid character string
            s = v;
        }
        tmp = new String(dest, 0, getValidLength(dest));
        LogUtil.d("DncDest=" + tmp + ", count=" + count + ", valid length=" + getValidLength(dest));
        return 0;
    }

    public static int getValidLength(byte[] bytes) {
        int i = 0;
        if (null == bytes || 0 == bytes.length)
            return i;
        for (; i < bytes.length; i++) {
            if ('\0' == bytes[i])
                break;
        }
        return i;
    }

    public static void mSleep(long millis) {

        if (millis < 0) return;

        long start = System.currentTimeMillis();
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
        long stop = System.currentTimeMillis();

        if (millis == 1) {
            if (stop - start > (millis + 10)) {
                mSleepErrorCount++;
            } else if (stop - start > (millis + 2)) {
                mSleepWrongCount++;
            }
            mSleepCount++;
        }
    }

    public static String getTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        return formatter.format(System.currentTimeMillis());
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)(i & 0xFF);
        result[1] = (byte)((i >> 8) & 0xFF);
        result[2] = (byte)((i >> 16) & 0xFF);
        result[3] = (byte)((i >> 24) & 0xFF);
        return result;
    }

    public static byte[] intToByteArray_8(long i) {
        byte[] result = new byte[8];
        result[0] = (byte)(i & 0xFF);
        result[1] = (byte)((i >> 8) & 0xFF);
        result[2] = (byte)((i >> 16) & 0xFF);
        result[3] = (byte)((i >> 24) & 0xFF);
        result[4] = (byte)((i >> 32) & 0xFF);
        result[5] = (byte)((i >> 40) & 0xFF);
        result[6] = (byte)((i >> 48) & 0xFF);
        result[7] = (byte)((i >> 56) & 0xFF);
        return result;
    }

    public static byte[] intToByte(int val) {
        byte[] bytes = new byte[1];
        for (int item = 0; item >= 0; item--) {
            bytes[item] = (byte) (val % 255);
            val = val / 255;
        }
        return bytes;
    }

    // 判断IP地址的合法性，这里采用了正则表达式的方法来判断 return true，合法
    public static boolean ipCheck(String ip) {
        if (ip != null && !ip.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            return ip.matches(regex);
        }
        return false;
    }

}
