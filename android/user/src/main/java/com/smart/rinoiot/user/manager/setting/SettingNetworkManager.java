package com.smart.rinoiot.user.manager.setting;

import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.center.api.MineApiService;
import com.smart.rinoiot.user.bean.setting.UpdateUserBean;

/**
 * @Author : tw
 * @Time : On 2022/9/29 17:49
 * @Description : SettingNetworkManager
 */
public class SettingNetworkManager {
    private static SettingNetworkManager instance;

    public static SettingNetworkManager getInstance() {
        if (instance == null) {
            instance = new SettingNetworkManager();
        }
        return instance;
    }

    /**
     * 修改用户信息
     */
    public void updateInfo(UpdateUserBean updateUserBean, CallbackListener<Object> callback) {
        RetrofitUtils.getService(MineApiService.class).updateInfo(updateUserBean).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }
}
