package com.smart.rinoiot.common.mqtt2.Manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.payload.EventPayload;
import com.smart.rinoiot.common.utils.HMACSHA256;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.StringUtil;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

/**
 * mqtt 管理类
 * @author author
 */
@SuppressLint("MissingPermission")
public class MqttManager {
    private static final String TAG = MqttManager.class.getSimpleName();
    private static MqttManager instance;

    @SuppressLint("StaticFieldLeak")
    private static MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;

    private MqttMessageHandler mqttMessageHandler;

    private final Set<String> topicSet = new HashSet<>();

    private static final String QR_CODE_LOGIN_TEMP_CREDENTIALS_PREFIX = "app_qr_login:";

    public static MqttManager getInstance() {
        if (instance == null) {
            instance = new MqttManager();
        }
        return instance;
    }

    /**
     * Adds a subscription topic to the list
     *
     * @param topic mqtt topic
     */
    public void addSubscriptionTopic(String topic) {
        Log.d(TAG, "addSubscriptionTopic: topics=" + new Gson().toJson(topicSet));
        topicSet.add(topic);
    }

    /**
     * Adds a subscription topic to the list
     *
     * @param topics mqtt topic
     */
    public void addSubscriptionTopics(String[] topics) {
        for (String topic : topics) {
            addSubscriptionTopic(topic);
        }
    }

    /**
     * Remove a topic from the list
     * @param topic topic
     */
    public void removeSubscriptionTopic(String topic) {
        if(topicSet.isEmpty()){
            return;
        }
        topicSet.remove(topic);
    }

    /**
     * Remove a topic from the list
     * @param topics topic
     */
    public void removeSubscriptionTopics(String[] topics) {
        if(topicSet.isEmpty()){
            return;
        }

        for (String topic: topics) {
            topicSet.remove(topic);
        }
    }

    /**
     * 订阅单个topic
     */
    public void subscribe(String topic) {
        addSubscriptionTopic(topic);
        if (mqttAndroidClient == null) {
            return;
        }

        try {
            //订阅主题，参数：主题、服务质量
            mqttAndroidClient.subscribe(topic, 1);
            LgUtils.i(TAG + "    mqtt --> (" + topic + ")订阅成功");
        } catch (Exception e) {
            e.printStackTrace();
            LgUtils.e(TAG + "    mqtt --> 订阅异常 --> " + e.getMessage());
        }
    }

    /**
     * 订阅多个topic
     */
    public void subscribe(String[] topics) {
        addSubscriptionTopics(topics);
        if (mqttAndroidClient == null) {
            return;
        }

        try {
            int[] iss = new int[topics.length];
            //订阅主题，参数：主题、服务质量
            mqttAndroidClient.subscribe(topics, iss);
            LgUtils.i(TAG + "mqtt 批量订阅成功");
        } catch (Exception e) {
            LgUtils.e(TAG + "mqtt 批量订阅异常: " + e.getMessage());
        }
    }

    /**
     * 取消订阅
     */
    public void unSubscribe(String topic) {
        if (mqttAndroidClient == null) {
            removeSubscriptionTopic(topic);
            return;
        }
        try {
            mqttAndroidClient.unsubscribe(topic);
            LgUtils.i(TAG + " mqtt --> 取消订阅成功");
            removeSubscriptionTopic(topic);
        } catch (Exception e) {
            LgUtils.e(TAG + " mqtt --> 取消订阅异常 --> " + e.getMessage());
        }
    }

    /**
     * 取消多喝订阅
     */
    public void unSubscribe(String[] topics) {
        if (mqttAndroidClient == null) {
            removeSubscriptionTopics(topics);
            return;
        }
        try {
            mqttAndroidClient.unsubscribe(topics);
            removeSubscriptionTopics(topics);
            LgUtils.i(TAG + "mqtt 批量取消订阅成功");
        } catch (Exception e) {
            LgUtils.e(TAG + "mqtt 批量取消订阅异常: " + e.getMessage());
        }
    }

    /**
     * 下发topic数据
     */
    public void publish(String topic, byte[] data) {
        if (mqttAndroidClient == null) {
            return;
        }
        try {
            LgUtils.i(TAG + "mqtt 发送数据: topic=" + topic + " | data = " + new String(data));
            IMqttDeliveryToken result = mqttAndroidClient.publish(topic, data, 0, false);
        } catch (Exception e) {
            LgUtils.e(TAG + "mqtt 发送数据异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private IMqttDeliveryToken publish(String topicId, byte[] data, int qos, boolean retained) {
        if (mqttAndroidClient == null) {
            return null;
        }
        try {
            return mqttAndroidClient.publish(topicId, data, qos, retained);
        } catch (Exception e) {
            LgUtils.e(TAG + "mqtt 批量发送数据异常: " + e.getMessage());
        }
        return null;
    }

    /**
     * 初始化
     */
    private void init(String userName, String password, String clientId) {
        mqttMessageHandler = new MqttMessageHandler();
        LgUtils.w(TAG + " Constant.MQTT_HOST=" + Constant.MQTT_HOST);
        mqttAndroidClient = new MqttAndroidClient(BaseApplication.getApplication().getApplicationContext(), Constant.MQTT_HOST, clientId, Ack.AUTO_ACK);
        //设置监听订阅消息的回调
        mqttAndroidClient.setCallback(mqttCallback);
        mMqttConnectOptions = new MqttConnectOptions();
        //设置是否清除缓存
        mMqttConnectOptions.setCleanSession(true);
        //设置超时时间，单位：秒
        mMqttConnectOptions.setConnectionTimeout(50);
        //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(50);
        //设置用户名
        mMqttConnectOptions.setUserName(userName);
        //设置密码
        mMqttConnectOptions.setPassword(password.toCharArray());
        doClientConnection();
    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        LgUtils.i(TAG + " mqtt --> 开始连接");
        if (!mqttAndroidClient.isConnected() && isConnectIsNormal()) {
            try {
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (Exception e) {
                LgUtils.i(TAG + " mqtt --> 连接报错 --> " + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return true;
        } else {
            LgUtils.i(TAG + " mqtt --> 没有可用网络");
            /*没有可用网络的时候，延迟60秒再尝试重连*/
            new Handler().postDelayed(this::doClientConnection, 60000);
            return false;
        }
    }

    /**
     * MQTT是否连接成功的监听
     */
    private final IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken arg0) {
            LgUtils.i(TAG + " mqtt --> 连接成功: " + mqttAndroidClient.isConnected());
            for (String topic : topicSet) {
                Log.d(TAG, "onSuccess: mqtt topic=" + topic);
                subscribe(topic);
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            LgUtils.i(TAG + " mqtt --> 连接失败");
            /*连接失败的时候，延迟60秒再尝试重连*/
            new Handler().postDelayed(() -> doClientConnection(), 60000);
        }
    };

    /**
     * 订阅主题的回调
     */
    private final MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            Log.d(TAG, "messageArrived: topic=" + topic);
            mqttMessageHandler.handlerMsg(topic, message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            LgUtils.d(TAG + " mqtt -->deliveryComplete");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            LgUtils.i(TAG + " mqtt --> 连接断开");
            //连接断开，重连
            doClientConnection();
        }
    };

    public void disconnect() {
        try {
            //断开连接
            mqttAndroidClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * mqtt连接参数统一管理
     */
    public void mqttConnectInit(UserInfoBean userInfo, Context context) {
        if (!UserInfoManager.getInstance().isLogin(context)) {
            return;
        }

        long currentTime = System.currentTimeMillis() / 1000;
        if (userInfo == null) {
            userInfo = UserInfoManager.getInstance().getUserInfo(context);
        }
        if (userInfo == null) {
            LgUtils.w(TAG + " mqttConnectInit  userInfo为空 ");
            return;
        }
        String userId = userInfo.id;
        String username = userId + "|signMethod=hmacSha256,ts=" + currentTime;
        String password = HMACSHA256.hmacSha256("uuid=" + userId + ",ts=" + currentTime, Constant.AES_APPID);
        String clientId = "app_" + userId + "|" + currentTime;
        init(username, password, clientId);
    }

    /**
     * Initializes an MQTT client with temp credentials for QR code login
     *
     * @param tempCredentials the temporary credentials
     */
    public void tempMqttClientConnect(String tempCredentials) {
        if(StringUtil.strIsNull(tempCredentials)){
            Log.e(TAG, "tempMqttClientConnect: credentials cannot be blank");
            return;
        }

        if(!tempCredentials.startsWith(QR_CODE_LOGIN_TEMP_CREDENTIALS_PREFIX)) {
            Log.e(TAG, "tempMqttClientConnect: invalid credentials.");
            return;
        }

        // extract token
        String token = extractToken(tempCredentials);
        if(StringUtil.strIsNull(token)){
            Log.e(TAG, "tempMqttClientConnect: failed to extract token from credentials");
            return;
        }

        // note the time
        long timestamp = System.currentTimeMillis() / 1000;

        // clientId format: temp_${token}|${ts} , example: temp_xxx23424xxx|1626197189
        String clientId = "temp_" + token + "|" + timestamp;

        // username format: ${token}|signMethod=${signMethod},ts=${ts}
        String username = token + "|signMethod=hmacSha256,ts=" + timestamp;

        // content format: uuid=$(token},ts=S{ts}, secret format: ${token}
        String content = "uuid=" + token + ",ts=" + timestamp;
        String password = HMACSHA256.hmacSha256(content, token);

        // let it rip
        init(username, password, clientId);

        Log.d(TAG, "tempMqttClientConnect: is connected: " + mqttAndroidClient.isConnected());
    }

    /**
     * Extracts the token from temporary login credentials
     *
     * @param tempCredentials temporary credentials
     * @return the token
     */
    private String extractToken(String tempCredentials) {
        // tokenize the temp credentials
        List<String> chunks = Arrays.asList(
                tempCredentials.split(QR_CODE_LOGIN_TEMP_CREDENTIALS_PREFIX)
        );

        // check the chunks
        int expectedChunks = 2;
        if(chunks.size() != expectedChunks){
            return null;
        }

        return chunks.get(1);
    }

    public byte[] getCloudWifiData(List<EventPayload.Device> devices) {
        // ToDo() implement method
        return new byte[0];
    }
}
