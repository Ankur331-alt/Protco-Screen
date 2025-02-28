package com.smart.rinoiot.center.api;


import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.network.ApiService;
import com.smart.rinoiot.center.bean.msg.MessageQueryBean;
import com.smart.rinoiot.center.bean.msg.MsgListBean;
import com.smart.rinoiot.center.bean.msg.UnreadBean;
import com.smart.rinoiot.user.bean.setting.UpdateUserBean;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 我的模块  接口api请求
 */
public interface MineApiService extends ApiService {
    /**
     * 获取消息列表
     */
    @POST("business-app/v1/message/list")
    Call<BaseResponse<MsgListBean>> getMsgList(@Body MessageQueryBean messageQueryBean);

    /**
     * 修改用户信息
     */
    @POST("business-app/v1/user/updateInfo")
    Call<BaseResponse<Object>> updateInfo(@Body UpdateUserBean updateUserBean);

    /**
     * 获取未读消息数
     */
    @POST("business-app/v1/message/getUnreadCount")
    Call<BaseResponse<UnreadBean>> getUnreadCount();

    /**
     * @deprecated 获取未读消息数
     */
    @POST("business-app/v1/message/setUserAllMessageReaded")
    Call<BaseResponse<Object>> setUserAllMessageReaded();

    /**
     * 获取用户资料
     */
    @Multipart
    @POST("business-app/v1/file/uploadFile")
//请求方法为POST，里面为你要上传的url
    Call<BaseResponse<String>> uploadFile(@Part MultipartBody.Part file);

    /**
     * 获取未读消息数
     */
    @POST("business-app/v2/message/getUnReadMsgCount")
    Call<BaseResponse<Integer>> getUnReadMsgCount(@Body Map<String,Object> params);

}
