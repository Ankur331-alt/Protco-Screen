package com.smart.rinoiot.common.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.ToastUtil;

public class BaseViewModel extends AndroidViewModel {

    /**
     * 加载数据等操作的action，在baseactivity中相对应
     */
    private final MutableLiveData<String> actonLiveData = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getActonLiveData() {
        return actonLiveData;
    }

    @SuppressLint("StaticFieldLeak")
    public Context mContext;

    /**
     * 显示加载动画
     *
     * @param
     * @return
     * @method showLoading
     * @date: 2020/5/7 3:14 PM
     * @author: xf
     */
    public void showLoading() {
        actonLiveData.postValue(Constant.SHOW_LOADING);
    }

    /**
     * 加载成功隐藏动画
     *
     * @param
     * @return
     * @method hideLoading
     * @date: 2020/5/7 3:14 PM
     * @author: xf
     */
    public void hideLoading() {
        AppExecutors.getInstance().mainThread().execute(() -> actonLiveData.setValue(Constant.LOADING_SUCCESS));
    }

    /**
     * 加载失败显示失败view
     *
     * @param
     * @return
     * @method loadingFailed
     * @date: 2020/5/7 3:14 PM
     * @author: xf
     */
    public void loadingFailed() {
        actonLiveData.postValue(Constant.LOADING_FAILED);
    }

    /**
     * 加载失败显示失败view
     *
     * @param
     * @return
     * @method loadingFailed
     * @date: 2020/5/7 3:14 PM
     * @author: xf
     */
    public void hasData() {
        actonLiveData.postValue(Constant.HAS_DATA);
    }

    /**
     * 没有数据显示空view
     *
     * @param
     * @return
     * @method loadingFailed
     * @date: 2020/5/7 3:14 PM
     * @author: xf
     */
    public void emptyData() {
        actonLiveData.postValue(Constant.EMPTY_DATA);
    }

    public void showToast(String msg) {
        ToastUtil.showMsg(msg);
    }

    public void showLongToast(String msg) {
        ToastUtil.showLongMsg(msg);
    }

    /**
     * 获取资源字符串
     *
     * @param resId 资源id
     * @return String 返回字符串
     * @method getString
     * @date: 2020/11/25 6:49 PM
     * @author: xf
     */
    public String getString(@StringRes int resId) {
        return getApplication().getString(resId);
    }

    /**
     * 获取资源中颜色
     *
     * @param resId 资源id
     * @return String 返回字符串
     * @method getString
     * @date: 2020/11/25 6:49 PM
     * @author: xf
     */
    public int getColor(@ColorRes int resId) {
        return getApplication().getResources().getColor(resId);
    }

    public void onCreate(Context context) {
        this.mContext = context;
    }

    public void onDestroy() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onNewIntent(Intent intent) {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.mContext = null;
    }

    public String getCountry() {
        return AppUtil.getCountryName(getApplication());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
