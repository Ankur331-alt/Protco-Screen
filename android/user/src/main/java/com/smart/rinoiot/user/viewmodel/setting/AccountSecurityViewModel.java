package com.smart.rinoiot.user.viewmodel.setting;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.smart.rinoiot.center.api.MineApiService;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.bean.setting.UpdateUserBean;
import com.smart.rinoiot.user.manager.setting.SettingNetworkManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 账号与安全modle
 */
public class AccountSecurityViewModel extends BaseViewModel {
    private static final String TAG = AccountSecurityViewModel.class.getSimpleName();

    public AccountSecurityViewModel(@NonNull @NotNull Application application) {
        super(application);
    }


    public void uploadFile(String path) {
        showLoading();
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RetrofitUtils.getService(MineApiService.class).uploadFile(parts).enqueue(
                new BaseRequestListener<String>() {
                    @Override
                    public void onResult(String result) {
                        Log.e(TAG, "  uploadFile  onResult,result=" + result);
                        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(mContext);
                        if (userInfo != null) {
                            updateInfoData(result, userInfo.nickname);
                        }
                    }

                    @Override
                    public void onError(String error, String msg) {
                        hideLoading();
                        LgUtils.w(TAG + "  uploadFile  onError    code=" + error + "   error=" + error);
                    }
                });
    }

    private void updateInfoData(String filePath, String name) {
        UpdateUserBean updateUserBean = new UpdateUserBean();
        updateUserBean.setAvatarUrl(filePath);
        updateUserBean.setNickname(name);
        updateInfo(updateUserBean);
    }

    /**
     * 修改用户信息
     */
    public MutableLiveData<Boolean> getUpdateLiveData() {
        return updateLiveData;
    }

    private MutableLiveData<Boolean> updateLiveData = new MutableLiveData<>();

    private void updateInfo(UpdateUserBean updateUserBean) {
        SettingNetworkManager.getInstance().updateInfo(updateUserBean, new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                LgUtils.w(TAG + "  updateInfo  data=" + new Gson().toJson(data));
                UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(mContext);
                if (userInfo != null) {
                    userInfo.avatarUrl = updateUserBean.getAvatarUrl();
                    userInfo.nickname = updateUserBean.getNickname();
                    UserInfoManager.getInstance().saveUserInfo(userInfo);
                }
                updateLiveData.postValue(true);
                hideLoading();
            }

            @Override
            public void onError(String code, String error) {
                hideLoading();
                LgUtils.w(TAG + "  updateInfo  onError    code=" + code + "   error=" + error);
                ToastUtil.showMsg(error);
                updateLiveData.postValue(false);
            }
        });
    }

    /**
     * 修改用户昵称
     */
    public void updateRename(AppCompatActivity activity, String oldName) {
        DialogUtil.showInputDialog(activity, getString(R.string.rino_device_create_group_rename), getString(R.string.rino_mine_profile_name), oldName, text -> {
            if (text.length() <= 0) {
                ToastUtil.showMsg(R.string.rino_scene_edit_name_not_empty);
            } else if (text.length() > 20) {
                ToastUtil.showMsg(R.string.rino_mine_nickname_rule2);
            } else {
                UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(mContext);
                if (userInfo != null) {
                    showLoading();
                    updateInfoData(userInfo.avatarUrl, text);
                }
            }
        }, () -> {

        });

    }
}
