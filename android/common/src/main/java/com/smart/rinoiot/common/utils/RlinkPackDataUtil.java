package com.smart.rinoiot.common.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class RlinkPackDataUtil {

    /**
     * 分包大小，BK芯片最大128
     */
    public final static int BK_MTU_SIZE = 128;

    /**
     * 类型  元数据  1代表普通命令， 2代表OTA
     */
    public final static int RN_TYPE_SIZE = 1;

    /**
     * 序号  元数据
     */
    public final static int RN_INDEX_SIZE = 2;

    /**
     * 字节数  元数据
     */
    public final static int RN_ALLHEX_SIZE = 2;

    /**
     * 总包数  元数据
     */
    public final static int RN_PAK_SIZE = 2;

    /**
     * 单字节数据长度  元数据
     */
    public final static int RN_IDEXHEX_SIZE = 1;

    /**
     * 校验  元数据
     */
    public final static int RN_CRC_SIZE = 1;

    //后续传输的大小数据包
    public final static int PN_PACK_LOOP_SIZE = BK_MTU_SIZE - RN_INDEX_SIZE - RN_ALLHEX_SIZE - RN_PAK_SIZE - RN_TYPE_SIZE - RN_CRC_SIZE - RN_IDEXHEX_SIZE - RN_CRC_SIZE;


    public final static String RN_DATA_BEGIN = "FF";

    public final static String RN_DATA_TYPE_CMD = "01";

    /**
     * 得到分包大小
     *
     * @param json
     * @return
     */
    public static int getRnLinkDataPackingSize(String json) {
        int packHexSize = json.length();

        if (packHexSize % PN_PACK_LOOP_SIZE == 0) {
            return packHexSize / PN_PACK_LOOP_SIZE;
        } else {
            return packHexSize / PN_PACK_LOOP_SIZE + 1;
        }
    }


    /**
     * 将字符串按照指定长度分割成字符串数组
     *
     * @param src
     * @param length
     * @return
     */
    public static String[] stringToStringArray(String src, int length) {
        //检查参数是否合法
        if (null == src || src.equals("")) {
            return null;
        }

        if (length <= 0) {
            return null;
        }
        int n = (src.length() + length - 1) / length; //获取整个字符串可以被切割成字符子串的个数
        String[] split = new String[n];
        for (int i = 0; i < n; i++) {
            if (i < (n - 1)) {
                split[i] = src.substring(i * length, (i + 1) * length);
            } else {
                split[i] = src.substring(i * length);
            }
        }
        return split;
    }

    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);

            String s4 = Integer.toHexString(ch);
            if (s4.length() < 2) {
                str = str + "0" + s4;
            } else {
                str = str + s4;
            }
        }
        return str;
    }

    public static String rnLinkCRC(String hex) {

        int size = hex.length() / 2;
        System.out.println("HEX : " + hex);
        int sumValue = 0;
        for (int i = 0; i < size; i++) {
            String subHex = hex.substring(i * 2, i * 2 + 2);

            int value = Integer.parseInt(subHex, 16);
            sumValue += value;
        }
        int crc = sumValue & 0xff;

        return String.format("%02X", crc);
    }

    /**
     * 数据打包，分包 ，根据MTU大小分包，芯片的处理能力有区别
     *
     * @param json
//     * @param mtu  芯片不一样，MTU会有区别
     * @return
     */
    public static List<String> rnLinkDataPacking(String json) {

        List<String> hexList = new ArrayList<String>();

        String[] packList = stringToStringArray(json, PN_PACK_LOOP_SIZE);
        //总长度  2byte
        String hexAllSize = String.format("%04X", json.length());

        //总包数   2byte
        String hexAllIndex = String.format("%04X", getRnLinkDataPackingSize(json));

        for (int i = 0; i < packList.length; i++) {
            String packStr = packList[i];
            //序号  2byte
            String hexIndex = String.format("%04X", i);

            //数据长度   1 byte
            String hexSize = String.format("%02X", packStr.length());

            //数据   n byte
            String hex = stringToHexString(packStr);

            String packHex = RN_DATA_TYPE_CMD + hexIndex + hexAllIndex + hexAllSize + hexSize + hex;

            packHex = RN_DATA_BEGIN + packHex + rnLinkCRC(packHex);

            hexList.add(packHex);
        }

        return hexList;
    }

    public static byte[] toByteArray(String hexString) {
        if (TextUtils.isEmpty(hexString))
            return null;
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index > hexString.length() - 1)
                return byteArray;
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }
}
