package com.smart.rinoiot.upush.tester;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.umeng.commonsdk.service.UMGlobalContext;
import com.umeng.message.PushAgent;
import com.smart.rinoiot.upush.helper.PushConstants;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class UPushNotification {

    /**
     * 发送透传消息
     */
    public static void send(String ticker, String title, String content) {
        try {
            Context context = UMGlobalContext.getAppContext();
            final NotificationModel msg;
            msg = new NotificationModel(PushConstants.APP_KEY, PushConstants.APP_MASTER_SECRET);
            msg.setDeviceToken(PushAgent.getInstance(context).getRegistrationId());
            msg.setTicker(ticker);
            msg.setTitle(title);
            msg.setText(content);
            msg.goAppAfterOpen();
            msg.setTestMode();
            msg.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
            msg.setProductionMode();
            Runnable runnable = () -> {
                try {
                    sendImpl(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            new Thread(runnable).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendImpl(BaseNotification msg) throws Exception {
        String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
        msg.setPredefinedKeyValue("timestamp", timestamp);

        String url = "http://msg.umeng.com/api/send";
        String postBody = msg.getPostBody();

        String p_sign = "POST" + url + postBody + msg.getAppMasterSecret();
        String sign = md5(p_sign);
        url = url + "?sign=" + sign;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(postBody.getBytes());
        outputStream.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream inputStream = connection.getInputStream();
        if (inputStream != null) {
            byte[] buffer = new byte[8 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        }

        connection.disconnect();

        JSONObject responseJson = new JSONObject(out.toString());
        String ret = responseJson.getString("ret");
        Runnable runnable;
        if (!ret.equalsIgnoreCase("SUCCESS")) {
            runnable = () -> Toast.makeText(UMGlobalContext.getAppContext(), "发送失败", Toast.LENGTH_LONG).show();
        } else {
            runnable = () -> Toast.makeText(UMGlobalContext.getAppContext(), "发送成功", Toast.LENGTH_LONG).show();
        }
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    private static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
    }
}
