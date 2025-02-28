package com.smart.rinoiot.center.viewmodel.msg;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.center.bean.msg.MessageQueryBean;
import com.smart.rinoiot.center.bean.msg.MsgRecordItemBean;
import com.smart.rinoiot.center.bean.msg.UnreadBean;
import com.smart.rinoiot.center.manager.msg.MessageNetworkManager;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.utils.DateUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author author
 * 消息模块 model
 */
public class MsgViewModel extends BaseViewModel {
    private static final String TAG = MsgViewModel.class.getSimpleName();

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    /**
     * @deprecated 是否存在未读消息
     */
    private final MutableLiveData<UnreadBean> unreadLiveData = new MutableLiveData<>();

    /**
     * 获取消息列表
     */
    private final MutableLiveData<List<MsgRecordItemBean>> msgListLiveData = new MutableLiveData<>();

    public MsgViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public MutableLiveData<UnreadBean> getUnreadLiveData() {
        return unreadLiveData;
    }

    public MutableLiveData<List<MsgRecordItemBean>> getMsgListLiveData() {
        return msgListLiveData;
    }

    public void getMsgList(int page) {
        MessageQueryBean messageQueryBean = new MessageQueryBean();
        messageQueryBean.setCurrentPage(page);
        messageQueryBean.setPageSize(Constant.PAGE_SIZE);
        Disposable disposable = MessageNetworkManager.getInstance().getMessages(messageQueryBean)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        msgListLiveData::postValue,
                        throwable -> msgListLiveData.postValue(new ArrayList<>())
                );
        mCompositeDisposable.add(disposable);
    }

    /**
     * 消息数据转化
     *
     * @param msgRecordItemBeans       当前接口回调
     * @param beforeMsgRecordItemBeans 适配器之前的所有数据
     */
    public List<MsgRecordItemBean> formatMsgData(List<MsgRecordItemBean> msgRecordItemBeans, List<MsgRecordItemBean> beforeMsgRecordItemBeans) {
        List<MsgRecordItemBean> msgRecordItemBeanList = new ArrayList<>();
        List<MsgRecordItemBean> tempArray = new ArrayList<>();
        //设备配之前显示的数据。
        if (beforeMsgRecordItemBeans != null && !beforeMsgRecordItemBeans.isEmpty()) {
            for (MsgRecordItemBean item : beforeMsgRecordItemBeans) {
                //过滤头部数据
                if (!item.isHeader()) {
                    tempArray.add(item);
                }
            }
        }

        //新添加的接口数据，全部整合到一个集合中
        tempArray.addAll(msgRecordItemBeans);
        //根据时间倒序展示消息
        List<String> listTime = new ArrayList<>();
        Iterator var7 = tempArray.iterator();
        while (var7.hasNext()) {
            MsgRecordItemBean item = (MsgRecordItemBean) var7.next();
            if (!this.isAdd(listTime, item.getCreateTime())) {
                listTime.add(String.valueOf(item.getCreateTime()));
            }
        }
        Collections.sort(listTime);
        Collections.reverse(listTime);
        var7 = listTime.iterator();
        //重新整合所有的数据，根据时间去匹配，添加到对应的时间下面
        while (var7.hasNext()) {
            String str = (String) var7.next();
            List<MsgRecordItemBean> tempRowsBeans = new ArrayList<>();
            for (MsgRecordItemBean item : tempArray) {
                if (DateUtils.isSameDay(item.getCreateTime(), Long.parseLong(str))) {
                    if (tempRowsBeans.size() == 0) {
                        MsgRecordItemBean headBean = new MsgRecordItemBean();
                        headBean.setIsHeader(true);
                        headBean.setHeader(DateUtils.getDateTimeYMD(String.valueOf(item.getCreateTime())));
                        msgRecordItemBeanList.add(headBean);
                        //添加到临时集合中，根据这个结合是否为空，来判断是否添加头部信息
                        tempRowsBeans.add(headBean);
                    }

                    msgRecordItemBeanList.add(item);
                }
            }
        }
        return msgRecordItemBeanList;
    }

    /**
     * 根据上一个时间和当前时间对比，在一天范围内，不添加，否则添加一个新的头部数据
     */
    private boolean isAdd(List<String> listTime, long currentTime) {
        boolean isAddFlag = false;
        for (String str : listTime) {
            if (DateUtils.isSameDay(Long.parseLong(str), currentTime)) {
                isAddFlag = true;
                break;
            }
        }

        return isAddFlag;
    }

    public void getUnreadCount() {
        Disposable disposable = MessageNetworkManager.getInstance()
                .getUnReadMessagesCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        unreadLiveData::postValue,
                        throwable -> unreadLiveData.postValue(null)
                );
        mCompositeDisposable.add(disposable);
    }

    public void setUserAllReadMessages() {
        Disposable disposable = MessageNetworkManager.getInstance()
                .getReadMessages().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                        },
                        throwable -> Log.e(TAG, "setUserAllReadMessages: " +
                                throwable.getLocalizedMessage()
                        )
                );
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear();
    }

    /**
     * 是否存在未读消息
     */
    private final MutableLiveData<Integer> unreadV2LiveData = new MutableLiveData<>();

    public MutableLiveData<Integer> getUnreadV2LiveData() {
        return unreadV2LiveData;
    }

    public void getUnReadMsgCount() {
        Disposable disposable = MessageNetworkManager.getInstance()
                .getUnReadMsgCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        unreadV2LiveData::postValue,
                        throwable -> unreadV2LiveData.postValue(null)
                );
        mCompositeDisposable.add(disposable);
    }
}
