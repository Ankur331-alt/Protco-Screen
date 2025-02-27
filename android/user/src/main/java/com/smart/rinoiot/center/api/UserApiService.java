package com.smart.rinoiot.center.api;


import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.CountryBean;
import com.smart.rinoiot.common.bean.TimeZoneBean;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.network.ApiService;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserApiService extends ApiService {

    /** 获取国家列表 */
    @POST(value = "business-app/v1/country/list")
    Call<BaseResponse<List<CountryBean>>> getCountryList();

    /** 获取时区列表 */
    @POST(value = "business-app/v1/country/getTimezoneList")
    Call<BaseResponse<List<TimeZoneBean>>> getTimezoneList();

    /** 根据指定数据获取国家明细 */
    @POST(value = "business-app/v1/country/getInfo")
    Call<BaseResponse<CountryBean>> getCountryByCondition(@Body Map<String, Object> params);

    /** 获取城市列表 */
    @POST(value = "business-app/v1/city/list")
    Call<BaseResponse<List<CityBean>>> getCityList();

    /** 发送验证码 */
    @POST("business-app/v1/verifyCode/send")
    Call<BaseResponse<String>> sendVerifyCode(@Body Map<String, Object> params);

    /** 校验验证码 */
    @POST("business-app/v1/verifyCode/checkVerifyCode")
    Call<BaseResponse<String>> checkVerifyCode(@Body Map<String, Object> params);

    /** 注册 */
    @POST("business-app/v1/user/registry")
    Call<BaseResponse<UserInfoBean>> registry(@Body Map<String, Object> params);

    /** 登录 */
    @FormUrlEncoded
    @POST("auth/oauth/token")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    Call<BaseResponse<UserInfoBean>> login(@FieldMap Map<String, Object> params);

    /** 登出 */
    @POST("auth/oauth/logout")
    Call<BaseResponse<String>> logout();

    /** 忘记密码 */
    @POST("business-app/v1/user/resetPassword")
    Call<BaseResponse<String>> resetPassword(@Body Map<String, Object> params);

    /** 获取用户资料 */
    @POST("business-app/v1/user/profile")
    Call<BaseResponse<UserInfoBean>> getUserInfo();

    /** 注销账号 */
    @POST("business-app/v1/user/cancelAccount")
    Call<BaseResponse<Object>> cancelAccount(@Body Map<String, Object> params);


    /** 第三方语音启用技能 */
    @POST("business-app/v1/alexa/skill/enableSkill")
    Call<BaseResponse<Boolean>> voiceEnableSkill(@Body Map<String, Object> params);

    /** 第三方语音是否绑定技能 */
    @POST("business-app/v1/alexa/skill/isBind")
    Call<BaseResponse<Boolean>> voiceIsBind();

    /** 第三方语音禁用技能 */
    @POST("business-app/v1/alexa/skill/disableSkill")
    Call<BaseResponse<Boolean>> voiceDisableSkill();

    /** 获取生成二维码的数据 */
    @GET("auth/oauth/getTempCredentials")
    Call<BaseResponse<Object>> getTempCredentials();

}
