package com.smart.rinoiot.center.manager.msg;

import android.util.Log;

import com.smart.rinoiot.center.api.MineApiService;
import com.smart.rinoiot.center.bean.msg.MessageQueryBean;
import com.smart.rinoiot.center.bean.msg.MsgListBean;
import com.smart.rinoiot.center.bean.msg.MsgRecordItemBean;
import com.smart.rinoiot.center.bean.msg.UnreadBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.network.RetrofitUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * @Author : tw
 * @Time : On 2022/9/28 16:31
 * @Description : MessageNetworkManager
 */
public class MessageNetworkManager {

    private static final String TAG = "MessageNetworkManager";

    private static MessageNetworkManager instance;

    public static MessageNetworkManager getInstance() {
        if (instance == null) {
            instance = new MessageNetworkManager();
        }
        return instance;
    }

    /**
     * 获取消息列表
     * @param messageQueryBean the message query bean.
     * @return the list of messages on success.
     */
    public Observable<List<MsgRecordItemBean>> getMessages(MessageQueryBean messageQueryBean) {
        return Observable.create(emitter -> {
            BaseRequestListener<MsgListBean> listener = new BaseRequestListener<MsgListBean>() {
                @Override
                public void onResult(MsgListBean messages) {
                    if (messages != null && messages.getRecords() != null && !messages.getRecords().isEmpty()) {
                        emitter.onNext(messages.getRecords());
                    }else{
                        emitter.onNext(new ArrayList<>());
                    }
                    emitter.onComplete();
                }

                @Override
                public void onError(String error, String msg) {
                    Log.e(TAG, "onError: error=" + error + " | message=" + msg);
                    emitter.onError(new Exception(msg));
                }
            };
            RetrofitUtils.getService(MineApiService.class)
                    .getMsgList(messageQueryBean).enqueue(listener);
        });
    }

    /**
     * @deprecated 获取未读消息数
     * @return the unread messages count.
     */
    public Observable<UnreadBean> getUnReadMessagesCount() {
        return Observable.create(emitter -> {
            BaseRequestListener<UnreadBean> listener = new BaseRequestListener<UnreadBean>() {
                @Override
                public void onResult(UnreadBean result) {
                    if(null != result) {
                        emitter.onNext(result);
                        emitter.onComplete();
                    }else{
                        emitter.onError(new Exception("Failed to fetch data."));
                    }
                }

                @Override
                public void onError(String error, String msg) {
                    Log.e(TAG, "onError: error=" + error + " | message=" + msg);
                    emitter.onError(new Exception(msg));
                }
            };
            RetrofitUtils.getService(MineApiService.class).getUnreadCount().enqueue(listener);
        });
    }

    /**
     * 获取未读消息数
     * @return the read messages.
     */
    public Observable<Object> getReadMessages() {
        return Observable.create(emitter -> {
            BaseRequestListener<Object> listener = new BaseRequestListener<Object>() {
                @Override
                public void onResult(Object result) {
                    if(null != result) {
                        emitter.onNext(result);
                        emitter.onComplete();
                    }else{
                        emitter.onError(new Exception("Failed to fetch data"));
                    }
                }

                @Override
                public void onError(String error, String msg) {
                    Log.e(TAG, "onError: error=" + error + " | message=" + msg);
                    emitter.onError(new Exception(msg));
                }
            };

            RetrofitUtils.getService(MineApiService.class)
                    .setUserAllMessageReaded().enqueue(listener);
        });
    }

    /**
     * 获取未读消息数
     * @return the unread messages count.
     */
    public Observable<Integer> getUnReadMsgCount() {
        return Observable.create(emitter -> {
            BaseRequestListener<Integer> listener = new BaseRequestListener<Integer>() {
                @Override
                public void onResult(Integer result) {
                    if(null != result) {
                        emitter.onNext(result);
                        emitter.onComplete();
                    }else{
                        emitter.onError(new Exception("Failed to fetch data."));
                    }
                }

                @Override
                public void onError(String error, String msg) {
                    Log.e(TAG, "onError: error=" + error + " | message=" + msg);
                    emitter.onError(new Exception(msg));
                }
            };
            Map<String, Object> params = new HashMap<>();
            params.put("rootAssetId", CacheDataManager.getInstance().getCurrentHomeId());
            RetrofitUtils.getService(MineApiService.class).getUnReadMsgCount(params).enqueue(listener);
        });
    }
}
