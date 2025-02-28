package com.smart.rinoiot.common.network;

import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.PanelInfo;
import com.smart.rinoiot.common.bean.ProductGuideBean;
import com.smart.rinoiot.common.bean.ProductGuideStepBean;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 接口类
 *
 * @Package: com.znkit.smart.api
 * @ClassName: ApiService
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2021/3/5 3:50 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/3/5 3:50 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface ApiService {
    /**
     * 获取注册验证码
     *
     * @param phone
     * @return
     */
    @GET(value = "login/sendRegCode")
    Call<BaseResponse<String>> getRegisterCode(@Query(value = "phone") String phone);

    /**
     * 校验验证码
     *
     * @param phone
     * @return
     */
    @FormUrlEncoded
    @POST(value = "login/checkRegCode")
    Call<BaseResponse<String>> checkRegisterCode(@Field(value = "phone") String phone, @Field(value = "code") String code);

    /**
     * 获取找回密码验证码
     *
     * @param phone
     * @return
     */
    @GET(value = "user/pwd/sendFindPwdCode")
    Call<BaseResponse<String>> getForgetPasswordCode(@Query(value = "phone") String phone);

    /**
     * 校验验证码
     *
     * @param phone 电话号码
     * @param code  验证码
     * @return
     */
    @FormUrlEncoded
    @POST(value = "user/pwd/checkFindPwdCode")
    Call<BaseResponse<String>> checkPasswordCode(@Field(value = "phone") String phone, @Field(value = "code") String code);

    /**
     * 更新密码
     *
     * @param phone    电话号码
     * @param password 密码
     * @return
     */
    @FormUrlEncoded
    @POST(value = "user/pwd/findPwdSubmit")
    Call<BaseResponse<String>> updateFindPassword(@Field(value = "phone") String phone, @Field(value = "password") String password);


    @POST(value = "{url}")
    Call<BaseResponse<Object>> rinoRnApiRequest(@Path("url") String url, @Body Map<String, Object> params);

    @GET
    Call<BaseResponse<Object>> test(@Url String url);

    /**
     * 获取产品当前应用的面板
     */
    @POST(value = "business-app/v1/product/{productId}/getPanel")
    Call<BaseResponse<PanelInfo>> getPanel(@Path("productId") String productId);

    /**
     * 文件下载
     */
    @Streaming
    @GET()
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    /**
     * 获取设备最新物模型信息
     */
    @POST(value = "business-app/v1/device/dp/{uuid}")
    Call<BaseResponse<List<DeviceDpBean>>> getDeviceDpList(@Path("uuid") String uuid);

    /**
     * 根据功能ID与配网类型查询配网引导数据
     */
    @POST(value = "business-app/v1/product/getByFunctionIdAndNetworkType")
    Call<BaseResponse<ProductGuideBean>> getByFunctionIdAndNetworkType(@Body Map<String, Object> params);

    /**
     * 根据品类ID以及协议类型获取配网引导数据
     */
    @POST(value = "business-app/v1/product/getNetworkGuide")
    Call<BaseResponse<List<ProductGuideStepBean>>> getNetworkGuide(@Body Map<String, Object> params);
}
