package com.smart.rinoiot.scene.api;

import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.bean.SceneDeviceDpBean;
import com.smart.rinoiot.common.network.ApiService;
import com.smart.rinoiot.scene.bean.IconItemBean;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SceneApiService extends ApiService {
    /** 查询资产下的所有场景 */
    @POST(value = "business-app/v1/scene/list")
    Call<BaseResponse<List<SceneBean>>> getAllSceneList(@Body Map<String, Object> params);

    /** 根据id查询数据 */
    @POST(value = "business-app/v1/scene/getInfo/{sceneId}")
    Call<BaseResponse<SceneBean>> getSceneById(@Path("sceneId") String sceneId);

    /** 新增场景 */
    @POST(value = "business-app/v1/scene/create")
    Call<BaseResponse<SceneBean>> createScene(@Body SceneBean sceneBean);

    /** 更新场景 */
    @POST(value = "business-app/v1/scene/update")
    Call<BaseResponse<SceneBean>> updateScene(@Body SceneBean sceneBean);

    /** 删除场景 */
    @POST(value = "business-app/v1/scene/delete/{sceneId}")
    Call<BaseResponse<Object>> deleteSceneById(@Path("sceneId") String sceneId);

    /** 查询场景图标列表 */
    @POST(value = "business-app/v1/scene/icons")
    Call<BaseResponse<List<IconItemBean>>> getSceneIconList();

    /** 启用-禁用场景 */
    @POST(value = "business-app/v1/scene/changeEnabeldStatus")
    Call<BaseResponse<Object>> changeSceneStatus(@Body Map<String, Object> params);

    /** 一键执行 */
    @POST(value = "business-app/v1/scene/onkeyExcute/{sceneId}")
    Call<BaseResponse<Object>> oneKeyExecute(@Path("sceneId") String sceneId);

    /** 获取场景支持的DP点列表 */
    @POST(value = "business-app/v1/device/getSceneSupportDpList/{deviceId}")
    Call<BaseResponse<SceneDeviceDpBean>> getSceneSupportDpList(@Path("deviceId") String deviceId);
}
