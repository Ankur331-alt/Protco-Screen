package com.smart.rinoiot.common.listener;

import android.content.Intent;
import android.text.TextUtils;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.api.CommonApiService;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.PageActivityPathUtils;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public abstract class BaseRequestListener<T> implements Callback<BaseResponse<T>> {

    public static final String SERVICE_UNAVAILABLE = "11014";//token刷新异常
    public static final String USER_NOT_LOGIN_ERROR = "11013";//用户未登录或已过期

    @Override
    public void onResponse(@NotNull Call<BaseResponse<T>> call, Response<BaseResponse<T>> response) {
        if (response.isSuccessful() && response.body() != null) {
            if (TextUtils.equals(response.body().getCode(), "200")) {
                onResult(response.body().getData());
            } else {
                if (response != null && response.body() != null) {
                    if (TextUtils.equals(SERVICE_UNAVAILABLE, response.body().getCode()) || TextUtils.equals(USER_NOT_LOGIN_ERROR, response.body().getCode())) {
                        dealTokenExpired(call, response.body().getCode(), response.body().getMsg());
                        return;
                    }
                    onError(response.body().getCode(), response.body().getMsg());
                }
            }
        } else {
            String code = String.valueOf(response.code());
            String message = response.message();
            try {
                if (response.code() == 500) {
                    if (response.errorBody() != null) {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        code = jsonObject.getString("code");
                        message = jsonObject.getString("message");
                        if (TextUtils.equals(SERVICE_UNAVAILABLE, code) || TextUtils.equals(USER_NOT_LOGIN_ERROR, code)) {
                            dealTokenExpired(call, code, message);
                            return;
                        }
                    }
                    onError(code, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError(code, message);
            }
        }
    }

    @Override
    public void onFailure(@NotNull Call<BaseResponse<T>> call, Throwable t) {
        if (TextUtils.equals(t.getMessage(), "当前网络不可用，请检查手机网络")) {
            ToastUtil.showMsg("当前网络不可用，请检查手机网络");
        }
        onError("400", t.getMessage());
    }

    public abstract void onResult(T result);

    public abstract void onError(String error, String msg);


    /**
     * 刷新token接口
     */
    public void getNewToken(Call<BaseResponse<T>> call) {
        Map<String, Object> map = new HashMap<>();
        map.put("grant_type", "refresh_token");
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(BaseApplication.getApplication().getApplicationContext());
        if (userInfo != null) {
            map.put("refresh_token", userInfo.refreshToken);
        }
        RetrofitUtils.getService(CommonApiService.class).refreshToken(map).enqueue(
                new Callback<BaseResponse<UserInfoBean>>() {
                    @Override
                    public void onResponse(@NotNull Call<BaseResponse<UserInfoBean>> call1, @NotNull Response<BaseResponse<UserInfoBean>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            SharedPreferenceUtil.getInstance().put(Constant.USER_TOKEN_EXPIRED, false);
                            UserInfoManager.getInstance().saveUserInfo(response.body().getData());
                            //刷新token成功，重新调用当前接口
//                            Call<BaseResponse<T>> clone = call.clone();
//                            clone.request().newBuilder().addHeader("Authorization", "Bearer " + response.body().getData().accessToken);
                            call.clone().enqueue(BaseRequestListener.this);
                        } else {
                            pageTurnLoginOut();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<BaseResponse<UserInfoBean>> call, @NotNull Throwable t) {
                        ToastUtil.showErrorMsg(t.getMessage());
                        pageTurnLoginOut();
                    }
                });
    }

    /**
     * 退回到登录注册页面
     */
    private void pageTurnLoginOut() {
        MqttManager.getInstance().disconnect();
        SharedPreferenceUtil.getInstance().put(Constant.USER_TOKEN_EXPIRED, true);
        UserInfoManager.getInstance().clear();
        SharedPreferenceUtil.getInstance().remove(Constant.COUNTRY_CODE);
        AppManager.getInstance().finishAllActivity();
        Intent intent = new Intent();
        intent.setAction(PageActivityPathUtils.FLASH_ACTIVITY);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        BaseApplication.getApplication().getApplicationContext().startActivity(intent);
    }


    /**
     * Token过期 处理逻辑
     */
    private void dealTokenExpired(Call<BaseResponse<T>> call, String code, String msg) {
        if (!SharedPreferenceUtil.getInstance().get(Constant.USER_TOKEN_EXPIRED, false)) {
            if (TextUtils.equals(SERVICE_UNAVAILABLE, code)) {
                ToastUtil.showErrorMsg(msg);
                pageTurnLoginOut();
            } else if (TextUtils.equals(USER_NOT_LOGIN_ERROR, code)) {
                getNewToken(call);
            }
        }
    }
}
