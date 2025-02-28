package com.smart.rinoiot.center.socket;

import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import javax.net.ssl.SSLParameters;

public class JWebSocketClient extends WebSocketClient {
    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
        LogUtils.e("JWebSocketClient:" + "serverUri==" + serverUri);
    }

    /**
     * websocket连接开启时调用
     */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        LogUtils.e("JWebSocketClient:" + "onOpen()");
    }

    @Override
    protected void onSetSSLParameters(SSLParameters sslParameters) {
    }

    /**
     * 在接收到消息时调用
     */
    @Override
    public void onMessage(String message) {
        if (messageListener != null && !TextUtils.isEmpty(message)) {
            messageListener.onReceiveMessage(message);
        }
        LogUtils.e("JWebSocketClient:" + "onMessage()="+message);
    }

    /**
     * 连接断开时调用
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtils.e("JWebSocketClient:" + "onClose()==" + reason + "   code==" + code);
    }

    /**
     * 连接出错时调用
     */
    @Override
    public void onError(Exception ex) {
        LogUtils.w("JWebSocketClient", "onError()==" + ex.getMessage());
    }

    public static MessageListener messageListener;

    public static void setMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    public interface MessageListener {
        void onReceiveMessage(String msg);
    }
}
