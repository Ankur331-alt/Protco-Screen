package com.smart.rinoiot.user.manager;

import android.content.Context;

import com.smart.rinoiot.center.api.UserApiService;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.api.CommonApiService;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.CountryBean;
import com.smart.rinoiot.common.bean.TimeZoneBean;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.upush.helper.PushConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public class UserNetworkManager {
    private static UserNetworkManager instance;

    private UserNetworkManager() {
    }

    public static UserNetworkManager getInstance() {
        if (instance == null) {
            instance = new UserNetworkManager();
        }
        return instance;
    }

    /**
     * 获取时区列表
     */
    public void getTimezoneList(CallbackListener<List<TimeZoneBean>> callback) {
        RetrofitUtils.getService(UserApiService.class).getTimezoneList().enqueue(new BaseRequestListener<List<TimeZoneBean>>() {
            @Override
            public void onResult(List<TimeZoneBean> result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 获取国家列表
     */
    public void getCountryList(CallbackListener<List<CountryBean>> callback) {
        RetrofitUtils.getService(UserApiService.class).getCountryList().enqueue(new BaseRequestListener<List<CountryBean>>() {
            @Override
            public void onResult(List<CountryBean> result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 根据经纬度获取国家明细
     * areaCode	国家码
     * countryName	国家名称
     * lat	纬度
     * lng	经度
     * qt	查询类型(1=经纬度,2=国家名称,3=区域码)
     */
    public void getCountryByCondition(double lat, double lng, CallbackListener<CountryBean> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("lat", lat);
        map.put("lng", lng);
        map.put("qt", 1);
        RetrofitUtils.getService(UserApiService.class).getCountryByCondition(map).enqueue(new BaseRequestListener<CountryBean>() {
            @Override
            public void onResult(CountryBean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }


    /**
     * Fetches the list of cities.
     * @return the list of cities.
     */
    public Observable<List<CityBean>> getCityList(){
        return Observable.create(emitter -> {
            BaseRequestListener<List<CityBean>> listener = new BaseRequestListener<List<CityBean>>() {
                @Override
                public void onResult(List<CityBean> result) {
                    if(null != result) {
                        emitter.onNext(result);
                    }
                    emitter.onComplete();
                }

                @Override
                public void onError(String error, String msg) {
                    emitter.onError(new Exception(msg));
                }
            };
            RetrofitUtils.getService(UserApiService.class).getCityList().enqueue(listener);
        });
    }

    /**
     * 获取城市列表
     */
    public void getCityList(CallbackListener<List<CityBean>> callback) {
        RetrofitUtils.getService(UserApiService.class).getCityList().enqueue(new BaseRequestListener<List<CityBean>>() {
            @Override
            public void onResult(List<CityBean> result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 发送验证码
     */
    public void sendVerifyCode(String account, int userPushType, CallbackListener<String> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("account", account);
        map.put("userPushType", userPushType);
        RetrofitUtils.getService(UserApiService.class).sendVerifyCode(map).enqueue(new BaseRequestListener<String>() {
            @Override
            public void onResult(String result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 校验验证码
     */
    public void checkVerifyCode(String account, String verifyCode, CallbackListener<String> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("account", account);
        map.put("verifyCode", verifyCode);
        RetrofitUtils.getService(UserApiService.class).checkVerifyCode(map).enqueue(new BaseRequestListener<String>() {
            @Override
            public void onResult(String result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 注册
     *
     * @param account    邮箱或手机号
     * @param areaCode   区域码
     * @param password   密码
     * @param verifyCode 验证码
     * @param callback   回调
     */
    public void registry(String account, String areaCode, String password, String verifyCode, CallbackListener<UserInfoBean> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("account", account);
        map.put("areaCode", areaCode);
        map.put("password", password);
        // 注册类型(1=邮箱登录,2=手机动态码登录)
        map.put("registryType", AppUtil.isEmail(account) ? 1 : 2);
        map.put("verifyCode", verifyCode);
        RetrofitUtils.getService(UserApiService.class).registry(map).enqueue(new BaseRequestListener<UserInfoBean>() {
            @Override
            public void onResult(UserInfoBean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 匿名登录
     *
     * @param areaCode 区域码
     * @param callback 回调
     */
    public void anonymousLogin(String areaCode, CallbackListener<UserInfoBean> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", Constant.AES_APPID);
        map.put("area_code", areaCode);
        map.put("grant_type", Constant.GRANT_TYPE_FOR_ANONYMOUS);
        RetrofitUtils.getService(UserApiService.class).login(map).enqueue(new BaseRequestListener<UserInfoBean>() {
            @Override
            public void onResult(UserInfoBean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 账号登录
     *
     * @param account  账号
     * @param password 密码
     * @param areaCode 区域码
     * @param callback 回调
     */
    public void loginWithAccount(String account, String password, String areaCode, CallbackListener<UserInfoBean> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", Constant.AES_APPID);
        map.put("username", account);
        map.put("password", password);
        map.put("area_code", areaCode);
        map.put("grant_type", Constant.GRANT_TYPE_FOR_PASSWORD);
        RetrofitUtils.getService(UserApiService.class).login(map).enqueue(new BaseRequestListener<UserInfoBean>() {
            @Override
            public void onResult(UserInfoBean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 验证码登录
     *
     * @param phone    手机号
     * @param areaCode 区域码
     * @param code     验证码
     * @param callback 回调
     */
    public void loginWithPhone(String phone, String areaCode, String code, CallbackListener<UserInfoBean> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", Constant.AES_APPID);
        map.put("username", phone);
        map.put("code", code);
        map.put("area_code", areaCode);
        map.put("grant_type", Constant.GRANT_TYPE_FOR_SMS_CODE);
        RetrofitUtils.getService(UserApiService.class).login(map).enqueue(new BaseRequestListener<UserInfoBean>() {
            @Override
            public void onResult(UserInfoBean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 忘记密码
     *
     * @param account    邮箱
     * @param password   密码
     * @param areaCode   区域码
     * @param verifyCode 验证码
     * @param callback   回调
     */
    public void resetPassword(String account, String password, String areaCode, String verifyCode, CallbackListener<String> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("account", account);
        map.put("password", password);
        map.put("areaCode", areaCode);
        map.put("verifyCode", verifyCode);
        RetrofitUtils.getService(UserApiService.class).resetPassword(map).enqueue(
                new BaseRequestListener<String>() {
                    @Override
                    public void onResult(String result) {
                        if (callback != null) callback.onSuccess(result);
                    }

                    @Override
                    public void onError(String error, String msg) {
                        if (callback != null) callback.onError(error, msg);
                    }
                });
    }

    public void logout(CallbackListener<String> callback) {
        RetrofitUtils.getService(UserApiService.class).logout().enqueue(new BaseRequestListener<String>() {
            @Override
            public void onResult(String result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    public void getUserInfo(CallbackListener<UserInfoBean> callback) {
        RetrofitUtils.getService(UserApiService.class).getUserInfo().enqueue(new BaseRequestListener<UserInfoBean>() {
            @Override
            public void onResult(UserInfoBean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 刷新token
     *
     * @param callback 回调
     */
    public void refreshToken(Context mContext, CallbackListener<UserInfoBean> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("grant_type", "refresh_token");
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(mContext);
        if (userInfo != null) {
            map.put("refresh_token", userInfo.accessToken);
        }
        RetrofitUtils.getService(CommonApiService.class).refreshToken(map).enqueue(new BaseRequestListener<UserInfoBean>() {
            @Override
            public void onResult(UserInfoBean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 注销账号
     */
    public void cancelAccount(String verifyCode, CallbackListener<Object> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("verifyCode", verifyCode);
        RetrofitUtils.getService(UserApiService.class).cancelAccount(map).enqueue(new BaseRequestListener<Object>() {
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

    /**
     * 友盟一键登录
     *
     * @param token    友盟一键登录token
     * @param verifyId 友盟一键登录requestId
     * @param callback 回调
     */
    public void loginWithOneClick(String token, String verifyId, CallbackListener<UserInfoBean> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", Constant.AES_APPID);
        map.put("verifyId", verifyId);
        map.put("umengToken", token);
        map.put("umengAppKey", PushConstants.APP_KEY);
        map.put("area_code", "86");
        map.put("grant_type", Constant.GRANT_TYPE_FOR_ONE_KEY);
        RetrofitUtils.getService(UserApiService.class).login(map).enqueue(new BaseRequestListener<UserInfoBean>() {
            @Override
            public void onResult(UserInfoBean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }


    /**
     * 第三方语音启用技能
     */
    public void voiceEnableSkill(String alexaCode, CallbackListener<Boolean> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("alexaCode", alexaCode);
        RetrofitUtils.getService(UserApiService.class).voiceEnableSkill(map).enqueue(new BaseRequestListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 第三方语音是否绑定技能
     */
    public void voiceIsBind(CallbackListener<Boolean> callback) {
        RetrofitUtils.getService(UserApiService.class).voiceIsBind().enqueue(new BaseRequestListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     *第三方语音禁用技能
     */
    public void voiceDisableSkill(CallbackListener<Boolean> callback) {
        RetrofitUtils.getService(UserApiService.class).voiceDisableSkill().enqueue(new BaseRequestListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (callback != null) callback.onSuccess(result);
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) callback.onError(error, msg);
            }
        });
    }

    /**
     * 第三方语音启用技能
     */
    public void getTempCredentials(CallbackListener<Object> callback) {
        RetrofitUtils.getService(UserApiService.class).getTempCredentials().enqueue(new BaseRequestListener<Object>() {
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
