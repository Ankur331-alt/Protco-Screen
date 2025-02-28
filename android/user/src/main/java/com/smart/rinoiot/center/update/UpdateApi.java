package com.smart.rinoiot.center.update;

import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.network.ApiService;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author author
 */
public interface UpdateApi extends ApiService {
    /**
     * 检查更新
     *
     * @param params params
     * @return the data
     */
    @POST(value = "business-app/v1/version/checkUpdate")
    Call<BaseResponse<UpdateBean>> checkUpdate(@Body Map<String, Object> params);

    /**
     * 下载文件用，添加这个注解用来下载大文件
     *
     * @param fileUrl file url
     * @return the response
     */
    @Streaming
    @GET()
    Call<ResponseBody> downloadFileUrl(@Url String fileUrl);
}
