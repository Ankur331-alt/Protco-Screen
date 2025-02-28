package com.smart.rinoiot.common.utils;

import android.annotation.SuppressLint;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@SuppressLint("GetInstance")
public class AESUtil {

    private static final String KEY_ALGORITHM = "AES";
    private static final String CHARTSET = "UTF-8";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";// 默认的加密算法
    public static final String VIPARA = "2020051110013300";

    /**
     * AES 加密
     *
     * @param content  明文
     * @param password 生成秘钥的关键字
     */
    public static String encrypt(String content, String password) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(content.getBytes(CHARTSET));
            return Base64.encode(encryptedData);
        } catch (Exception e) {
            LgUtils.e("加密失败: {}" + Objects.requireNonNull(e.getMessage()));
        }
        return null;
    }

    /**
     * AES 解密
     *
     * @param content  密文
     * @param password 生成秘钥的关键字
     */
    public static String decrypt(String content, String password) {
        try {
            byte[] byteMi = Base64.decode(content);
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, CHARTSET);
        } catch (Exception e) {
            LgUtils.e("解密失败: {}" + Objects.requireNonNull(e.getMessage()));
        }
        return null;
    }

    public static void main(String[] args) {
        String content = "{\"userName\":\"abner_yc@znkit.com\",\"email\":\"abner_yc@znkit.com\"}";
        String password = "ghH6m16ahybpdOUs5B9PuenKWVOAhDO5";
        // 加密
        System.out.println("加密前：" + content);
        String encryptResult = encrypt(content, password);
        System.out.println("加密后：" + encryptResult);
        System.out.println("解密后：" + decrypt(encryptResult, password));
    }

    /**
     * 域名解密
     *
     * @param src 解密字符串
     * @param key 密钥
     * @return 解密后的字符串
     * AES/ECB/PKCS7Padding 解密
     */
    //AES/ECB/PKCS7Padding 密钥
    public static final String decryptKey = "RinoIotIsNumOne8";

    public static String decryptECB(String src, String key) {
        try {
            byte[] decodeBase64 = android.util.Base64.decode(src, android.util.Base64.NO_WRAP);

            //设置Cipher对象
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), KEY_ALGORITHM));

            //调用doFinal解密
            byte[] decryptBytes = cipher.doFinal(decodeBase64);
            return new String(decryptBytes);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    /**
     * AES/ECB/PKCS7Padding 加密
     *
     * @param content    加密的字符串
     * @param encryptKey key值
     */
    public static String encryptECB(String content, String encryptKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), KEY_ALGORITHM));
            //调用doFinal
            byte[] b = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

            // 转base64
            return new String(android.util.Base64.encode(b, android.util.Base64.NO_WRAP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] toByteArray(String hexString) {
        if (hexString.isEmpty())
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

}
