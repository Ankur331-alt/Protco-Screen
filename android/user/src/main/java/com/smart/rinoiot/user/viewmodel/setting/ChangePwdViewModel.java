package com.smart.rinoiot.user.viewmodel.setting;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.manager.UserNetworkManager;

import org.jetbrains.annotations.NotNull;

/**
 * 修改密码modle
 */
public class ChangePwdViewModel extends BaseViewModel {

    public ChangePwdViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    /**
     * 验证码是否获取成功动作数据发送
     */
    private final MutableLiveData<String> codeActionLiveData = new MutableLiveData<>();

    public MutableLiveData<String> getCodeActionLiveData() {
        return codeActionLiveData;
    }

    /**
     * 获取验证码
     *
     * @param account      账号，邮箱或者手机号
     * @param userPushType 验证码类型（1=登录，2=注册,3=重置密码,4=匿名账号升级）
     */
    public void getCode(String account, int userPushType) {
        showLoading();
        UserNetworkManager.getInstance().sendVerifyCode(account, userPushType, new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                hideLoading();
                codeActionLiveData.postValue("");
            }

            @Override
            public void onError(String code, String error) {
                hideLoading();
                codeActionLiveData.postValue(error);
            }
        });
    }

    /**
     * 倒计时数据
     */
    private final MutableLiveData<Integer> countDownLiveData = new MutableLiveData<>();

    public MutableLiveData<Integer> getCountDownLiveData() {
        return countDownLiveData;
    }

    /**
     * 定时器
     */
    private CountDownTimer timer;

    /**
     * 验证码倒计时
     */
    public void countDown() {
        if (timer != null) {
            timer.cancel();
        }

        // 倒计时60秒，一次1秒
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                countDownLiveData.postValue((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                countDownLiveData.postValue(0);
            }
        }.start();
    }

    /**
     * 校验成功回调
     */
    private final MutableLiveData<Boolean> checkCodeLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getCheckCodeLiveData() {
        return checkCodeLiveData;
    }

    /**
     * 校验验证码
     *
     * @param account    账号，邮箱或者手机号
     * @param verifyCode 验证码
     */
    public void checkCode(String account, String verifyCode) {
        showLoading();
        UserNetworkManager.getInstance().checkVerifyCode(account, verifyCode, new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                hideLoading();
                checkCodeLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                hideLoading();
                ToastUtil.showMsg(error);
                checkCodeLiveData.postValue(false);
            }
        });
    }

    /**
     * 设置密码回调
     */
    private final MutableLiveData<Boolean> setPsdLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getSetPsdLiveData() {
        return setPsdLiveData;
    }

    /**
     * 注册
     *
     * @param account    邮箱
     * @param password   密码
     * @param areaCode 区域码
     * @param verifyCode 验证码
     */
    public void resetPassword(String account, String password, String areaCode, String verifyCode) {
        showLoading();
        UserNetworkManager.getInstance().resetPassword(account, password, areaCode, verifyCode, new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                hideLoading();
                setPsdLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                hideLoading();
                ToastUtil.showMsg(error);
                setPsdLiveData.postValue(false);
            }
        });
    }
}
