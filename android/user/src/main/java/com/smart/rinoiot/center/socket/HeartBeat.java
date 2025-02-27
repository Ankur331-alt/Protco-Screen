package com.smart.rinoiot.center.socket;

import android.os.Handler;
import android.text.TextUtils;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.LgUtils;

import org.java_websocket.handshake.ServerHandshake;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

/**
 * 长链接心跳封装类
 */
public class HeartBeat {
    private static final String TAG = HeartBeat.class.getSimpleName();
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    public Handler mHandler = new Handler();
    private static HeartBeat heartBeat;
    public JWebSocketClient webSocketClient;
    private SocketMessageCallBack socketMessageCallBack;
    /**
     * 重连次数
     */
    private int count;

    public static HeartBeat getInstance() {
        if (heartBeat == null) {
            synchronized (HeartBeat.class) {
                if (heartBeat == null) {
                    heartBeat = new HeartBeat();
                }
            }
        }
        return heartBeat;
    }

    /**
     * 第一次初始化webSocket
     */
    public void init() {
        initWebSocketClient();
        //定时对长连接进行心跳检测
        mHandler.postDelayed(heartBeatThread, HEART_BEAT_RATE * 2);
    }

    public void repayConnect() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            return;
        }
        init();
    }

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatThread);
        new Thread() {
            @Override
            public void run() {
                try {
                    //重连
                    if (reconnectWsBack != null) {
                        reconnectWsBack.reconnectBack();
                        count++;
                    }
                    sleep(800);
                    webSocketClient.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private final Runnable heartBeatThread = new Runnable() {
        @Override
        public void run() {
            if (webSocketClient != null) {
                if (webSocketClient.isClosed()) {
                    reconnectWs();
                }
            } else {
                //如果client已为空，重新初始化websocket
                initWebSocketClient();
            }
            //定时对长连接进行心跳检测
            mHandler.postDelayed(heartBeatThread, HEART_BEAT_RATE);
        }
    };

    /**
     * 初始化webSocketClient
     */
    private void initWebSocketClient() {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                webSocketClient = new JWebSocketClient(getSocketUrl()) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {
                        super.onOpen(handshakedata);
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            if (socketMessageCallBack != null) {
                                socketMessageCallBack.onOpen();
                            }
                        });
                    }

                    @Override
                    public void onMessage(ByteBuffer bytes) {//回调消息接受
                        super.onMessage(bytes);
                        count = 0;
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            if (socketMessageCallBack != null) {
                                socketMessageCallBack.messageCallBack(bytes);
                            }
                        });
                    }

                    @Override
                    public void connect() {
                        super.connect();
                        LgUtils.e("connect");
                    }


                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        super.onClose(code, reason, remote);
                        LgUtils.e(reason + "count=" + count);
                        if (code == -1) {
                            if (socketMessageCallBack != null && count >= 2) {
                                AppExecutors.getInstance().mainThread().execute(() -> socketMessageCallBack.onError(new Exception()));
                            }
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        super.onError(ex);
                        LgUtils.e(ex.getMessage());
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            if (socketMessageCallBack != null && count >= 2 && (ex instanceof SocketTimeoutException || ex instanceof TimeoutException)) {
                                socketMessageCallBack.onError(ex);
                            }
                        });
                    }
                };
                try {
                    webSocketClient.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public JWebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    /**
     * 消息回调接口
     */
    public interface SocketMessageCallBack {
        void messageCallBack(ByteBuffer bytes);

        void onError(Exception e);

        void onOpen();
    }

    public void setSocketMessageCallBack(SocketMessageCallBack socketMessageCallBack) {
        this.socketMessageCallBack = socketMessageCallBack;
    }

    /**
     * 组装URI数据
     */
    private URI getSocketUrl() {
        URI websocketURI = null;
        if (TextUtils.isEmpty(Constant.WEB_SOCKET_DATA)) return websocketURI;
//        String webUrl = "wss://" + Constant.WEB_SOCKET_HOST_URL + "/websocket/server/app_qr_login/connection/" + Constant.WEB_SOCKET_DATA;
        String webUrl = "wss://" + Constant.WEB_SOCKET_HOST_URL + "/websocket/server/"+Constant.WEB_SOCKET_DATA;
        LgUtils.w(TAG+"    webUrl="+webUrl);
        try {
            websocketURI =
                    new URI(webUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return websocketURI;
    }

    /**
     * 断开连接
     */
    public void closeConnect() {
        try {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
            if (null != webSocketClient) {
                webSocketClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ReconnectWsBack reconnectWsBack;

    public ReconnectWsBack getReconnectWsBack() {
        return reconnectWsBack;
    }

    public void setReconnectWsBack(ReconnectWsBack reconnectWsBack) {
        this.reconnectWsBack = reconnectWsBack;
    }

    public interface ReconnectWsBack {
        void reconnectBack();
    }
}
