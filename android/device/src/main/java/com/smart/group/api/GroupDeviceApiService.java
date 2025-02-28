package com.smart.group.api;

import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.bean.GroupBean;
import com.smart.rinoiot.common.network.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GroupDeviceApiService extends ApiService {

    /** 创建或更新群组 */
    @POST(value = "business-app/v1/deviceGroup/saveOrUpdate")
    Call<BaseResponse<GroupBean>> saveOrUpdate(@Body Map<String, Object> params);

    /** 删除群组 */
    @POST(value = "business-app/v1/deviceGroup/del/{groupId}")
    Call<BaseResponse<Object>> removeGroup(@Path("groupId") String groupId);

    /** 获取群组详细 */
    @POST(value = "business-app/v1/deviceGroup/detail/{groupId}")
    Call<BaseResponse<GroupBean>> getGroupDetail(@Path("groupId") String groupId);
}
