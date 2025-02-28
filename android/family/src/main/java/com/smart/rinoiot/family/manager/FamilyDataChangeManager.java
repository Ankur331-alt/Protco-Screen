package com.smart.rinoiot.family.manager;

import com.smart.rinoiot.family.listener.OnApiDataChangeObserver;
import com.smart.rinoiot.family.listener.OnViewDataChangeObserver;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/8
 */
public class FamilyDataChangeManager {
    public static FamilyDataChangeManager instance;

    private final CopyOnWriteArrayList<OnViewDataChangeObserver> onViewDataChangeObservers = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<OnApiDataChangeObserver> onApiDataChangeObservers = new CopyOnWriteArrayList<>();

    public FamilyDataChangeManager() {
    }

    public static FamilyDataChangeManager getInstance() {
        if (instance == null){
            instance = new FamilyDataChangeManager();
        }
        return instance;
    }

    public void onDestroy() {
        onViewDataChangeObservers.clear();
        onApiDataChangeObservers.clear();
    }

    public void setOnViewDataChangeObserver(OnViewDataChangeObserver onDataChangeObserver) {
        onViewDataChangeObservers.add(onDataChangeObserver);
    }

    public void removeOnViewDataChangeListener(OnViewDataChangeObserver onDataChangeObserver) {
        if (onDataChangeObserver == null) {
            return;
        }
        onViewDataChangeObservers.remove(onDataChangeObserver);
    }


    /**
     * 新增api监听
     * @param onDataChangeObserver
     */
    public void setOnApiDataChangeObserver(OnApiDataChangeObserver onDataChangeObserver) {
        onApiDataChangeObservers.add(onDataChangeObserver);
    }

    /**
     * 移除api监听
     * @param onDataChangeObserver
     */
    public void removeOnApiDataChangeListener(OnApiDataChangeObserver onDataChangeObserver) {
        if (onDataChangeObserver == null) {
            return;
        }
        onApiDataChangeObservers.remove(onDataChangeObserver);
    }

    /**
     * 修改view成功了调用
     *
     * @method
     * @date: 2020/12/12 3:57 PM
     * @author: xf
     */
    public void changeViewDataSuccess() {
        for (OnViewDataChangeObserver dataChangeObserver : onViewDataChangeObservers) {
            dataChangeObserver.onViewDataChange();
        }
    }


    /**
     * api数据成功了调用
     *
     * @method
     * @date: 2020/12/12 3:57 PM
     * @author: xf
     */
    public void changeApiDataSuccess() {
//        for (int i=0;i<onApiDataChangeObservers.size();i++){
//            OnApiDataChangeObserver observer = onApiDataChangeObservers.get(i);
//            observer.onApiDataChange();
//        }
        for (OnApiDataChangeObserver dataChangeObserver : onApiDataChangeObservers) {
            dataChangeObserver.onApiDataChange();
        }
    }
}
