package com.smart.rinoiot.user.viewmodel;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.activity.WebViewActivity;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.manager.UserNetworkManager;

import org.jetbrains.annotations.NotNull;

/**
 * alexa语音绑定modle
 */
public class AlexaBindViewModel extends BaseViewModel {
    private static String TAG = AlexaBindViewModel.class.getSimpleName();

    public AlexaBindViewModel(@NonNull @NotNull Application application) {
        super(application);
    }


    /**
     * 第三方语音启用技能
     */
    private MutableLiveData<Boolean> enableSkillLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getEnableSkillLiveData() {
        return enableSkillLiveData;
    }

    public void voiceEnableSkill(String alexaCode) {
        showLoading();
        UserNetworkManager.getInstance().voiceEnableSkill(alexaCode, new CallbackListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                hideLoading();
                enableSkillLiveData.postValue(data);
                LgUtils.w(TAG + "   voiceAuth  onSuccess  data=" + new Gson().toJson(data));
            }

            @Override
            public void onError(String code, String error) {
                hideLoading();
                enableSkillLiveData.postValue(false);
                LgUtils.w(TAG + "   voiceAuth  onError  error=" + error);
            }
        });
    }

    /**
     * 第三方语音是否绑定技能
     */
    private MutableLiveData<Boolean> isBindLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsBindLiveData() {
        return isBindLiveData;
    }

    public void voiceIsBind() {
        UserNetworkManager.getInstance().voiceIsBind(new CallbackListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                isBindLiveData.postValue(data);
                LgUtils.w(TAG + "   voiceAuthStatus  onSuccess  data=" + new Gson().toJson(data));
            }

            @Override
            public void onError(String code, String error) {
                isBindLiveData.postValue(false);
                LgUtils.w(TAG + "   voiceAuthStatus  onError  error=" + error);
            }
        });
    }

    /**
     * 第三方语音禁用技能
     */
    private MutableLiveData<Boolean> disableSkillLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getDisableSkillLiveData() {
        return disableSkillLiveData;
    }

    public void voiceDisableSkill() {
        showLoading();
        UserNetworkManager.getInstance().voiceDisableSkill(new CallbackListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                hideLoading();
                disableSkillLiveData.postValue(data);
                LgUtils.w(TAG + "   voiceAuth  onSuccess  data=" + new Gson().toJson(data));
            }

            @Override
            public void onError(String code, String error) {
                hideLoading();
                LgUtils.w(TAG + "   voiceAuth  onError  error=" + error);
                ToastUtil.showMsg(error);
                disableSkillLiveData.postValue(false);
            }
        });
    }

    public void alexaUnbindDialog(AppCompatActivity activity) {
        DialogUtil.showNormalMsg(activity, getString(R.string.rino_voice_unbind), getString(R.string.rino_voice_unbind_describe), getString(R.string.rino_device_not_support_network_config_cancel), getString(R.string.rino_device_not_support_network_config_confirm), new DialogOnListener() {
            @Override
            public void onCancel() {
                voiceDisableSkill();
            }

            @Override
            public void onConfirm() {
                activity.startActivity(new Intent(activity, WebViewActivity.class)
                        .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_mine_alexa))
                        .putExtra(Constant.WEB_URL, getString(R.string.rino_mine_alexa_voice_guide)));
            }
        });
    }
}
