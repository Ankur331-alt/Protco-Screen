package com.smart.rinoiot.center.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.smart.rinoiot.center.socket.HeartBeat;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.manager.UserNetworkManager;

import org.jetbrains.annotations.NotNull;

/**
 * 中控屏wifi连接--》扫码登录 --》绑定成功
 */
public class CenLoginViewModel extends BaseViewModel {
    private static String TAG = CenLoginViewModel.class.getSimpleName();
    public static final int NETWORK_LIST_TYPE = 10001;

    public CenLoginViewModel(@NonNull @NotNull Application application) {
        super(application);
    }


    /**
     * 中控屏授权
     */
    private MutableLiveData<String> tempCredentialsLiveData = new MutableLiveData<>();

    public MutableLiveData<String> getTempCredentialsLiveData() {
        return tempCredentialsLiveData;
    }

    public void getTempCredentials() {
        HeartBeat.getInstance().closeConnect();
        UserNetworkManager.getInstance().getTempCredentials(new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                hideLoading();
                if (data != null) {
                    tempCredentialsLiveData.postValue(data.toString());
                }
                LgUtils.w(TAG + "   getTempCredentials  onSuccess  data=" + new Gson().toJson(data));
            }

            @Override
            public void onError(String code, String error) {
                hideLoading();
                LgUtils.w(TAG + "   getTempCredentials  onError  error=" + error);
            }
        });
    }

    /**
     * 检测wifi是否打开，且wifi是否可用
     */
    @SuppressLint("StaticFieldLeak")
    public ConfirmPopupView confirmPopupView;

    public void checkoutWifiStatus(Fragment fragment, int requestCode) {
        if (confirmPopupView != null && confirmPopupView.isDismiss()) {
            confirmPopupView.show();
            return;
        }
        confirmPopupView = DialogUtil.showNormalMsg(fragment.getActivity(), "",
                getString(R.string.rino_device_current_no_wifi),
                getString(com.smart.rinoiot.common.R.string.rino_common_cancel),
                getString(com.smart.rinoiot.common.R.string.rino_device_to_connect),
                new DialogOnListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirm() {
                        Intent intent = new Intent();
                        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                        fragment.startActivityForResult(intent, requestCode);
                    }
                });
    }


    /**
     * app授权成功
     */
    private MutableLiveData<UserInfoBean> verityLiveData = new MutableLiveData<>();

    public MutableLiveData<UserInfoBean> getVerityLiveData() {
        return verityLiveData;
    }

    public void getUserInfo() {
        UserNetworkManager.getInstance().getUserInfo(new CallbackListener<UserInfoBean>() {
            @Override
            public void onSuccess(UserInfoBean data) {
                UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(getApplication());
                userInfo.userName = data.userName;
                userInfo.nickname = data.nickname;
                userInfo.tz = data.tz;
                userInfo.countryCode = data.countryCode;
                userInfo.avatarUrl = data.avatarUrl;
                userInfo.email = data.email;
                userInfo.phoneNumber = data.phoneNumber;
                userInfo.latestLoginTime = data.latestLoginTime;
                userInfo.userType = data.userType;
                userInfo.registryType = data.registryType;
                userInfo.tempUnit = data.tempUnit;
                verityLiveData.postValue(userInfo);

                MqttManager.getInstance().mqttConnectInit(userInfo, getApplication());
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                verityLiveData.postValue(null);
            }
        });
    }
}
