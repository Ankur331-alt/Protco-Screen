package com.smart.rinoiot.user.viewmodel.setting;

import android.app.Application;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.DataCleanManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.bean.setting.UpdateUserBean;
import com.smart.rinoiot.user.manager.UserNetworkManager;
import com.smart.rinoiot.user.manager.setting.SettingNetworkManager;

import org.jetbrains.annotations.NotNull;

/**
 * 个人设置modle
 */
public class SettingViewModel extends BaseViewModel {
    private static final String TAG = SettingViewModel.class.getSimpleName();

    public SettingViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    /**
     * 退出登录回调
     */
    private final MutableLiveData<Boolean> logoutLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLogoutLiveData() {
        return logoutLiveData;
    }

    public void logout() {
        UserNetworkManager.getInstance().logout(new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                MqttManager.getInstance().disconnect();
                logoutLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                if ("11013".equals(code) || "110013".equals(code)) {
                    MqttManager.getInstance().disconnect();
                    logoutLiveData.postValue(true);
                } else {
                    ToastUtil.showMsg(error);
                    logoutLiveData.postValue(false);
                }
            }
        });
    }

    /**
     * 是否清除缓存
     */
    public void showCacheDialog(AppCompatActivity activity, TextView tvClearCache) {
        DialogUtil.showNormalMsg(activity, getString(R.string.rino_common_alert_title), getString(R.string.rino_mine_clear_cache_tips), new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                DataCleanManager.cleanCacheData(activity);
                tvClearCache.setText("");
            }
        });
    }

    /**
     * 切换温度单位
     */
    public void showChangeUnit(AppCompatActivity activity, TextView tvTempUnit) {
        DialogUtil.showBottomSheet(activity, "", new String[]{"℃", "℉"}, (position, text) -> {
            tvTempUnit.setText(text);
            UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(activity);
            userInfo.tempUnit = text;
            UserInfoManager.getInstance().saveUserInfo(userInfo);
        });
    }

    public void updateInfoData(String tz) {
        UpdateUserBean updateUserBean = new UpdateUserBean();
        updateUserBean.setTz(tz);
        updateInfo(updateUserBean);
    }

    private void updateInfo(UpdateUserBean updateUserBean) {
        showLoading();
        SettingNetworkManager.getInstance().updateInfo(updateUserBean, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                LgUtils.w(TAG + "  updateInfo  data=" + new Gson().toJson(data));
                UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(mContext);
                if (userInfo != null) {
                    userInfo.tz = updateUserBean.getTz();
                    UserInfoManager.getInstance().saveUserInfo(userInfo);
                }
                hideLoading();
            }

            @Override
            public void onError(String code, String error) {
                hideLoading();
                LgUtils.w(TAG + "  updateInfo  onError    code=" + code + "   error=" + error);
            }
        });
    }

}
