package com.smart.rinoiot.family.api;

import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.BaseResponse;
import com.smart.rinoiot.common.bean.DeviceGroupBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.InviteMemberBean;
import com.smart.rinoiot.common.bean.SortBean;
import com.smart.rinoiot.common.network.ApiService;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FamilyApiService extends ApiService {

    /**
     * 获取用户资产树
     */
    @POST(value = "business-app/v1/asset/assetTree")
    Call<BaseResponse<List<AssetBean>>> getAssetTree();

    /**
     * 创建资产（智能家居行业，前端限制2级）
     */
    @POST(value = "business-app/v1/asset/create")
    Call<BaseResponse<String>> createAsset(@Body Map<String, Object> params);

    /**
     * 获取资产详细
     */
    @POST(value = "business-app/v1/asset/detail/{assetId}")
    Call<BaseResponse<AssetBean>> getAssetDetail(@Path("assetId") String assetId);

    /**
     * 选中资产
     */
    @POST(value = "business-app/v1/asset/{assetId}/selected")
    Call<BaseResponse<String>> selectedAsset(@Path("assetId") String assetId);

    /**
     * 删除资产
     *
     * @param assetId
     * @return
     */
    @POST(value = "business-app/v1/asset/delete/{assetId}")
    Call<BaseResponse<Boolean>> deleteFamily(@Path("assetId") String assetId);


    /**
     * 移除指定成员
     */
    @POST(value = "business-app/v1/asset/delete/{assetId}/member/{memberId}")
    Call<BaseResponse<Boolean>> removeMember(@Path("assetId") String assetId, @Path("memberId") String memberId);

    /**
     * 创建家庭
     */
    @POST(value = "business-app/v1/asset/create")
    Call<BaseResponse<String>> createFamily(@Body Map<String, Object> params);


    /**
     * 获取设备列表
     */
    @POST(value = "business-app/v1/device/getUserDeviceList")
    Call<BaseResponse<List<DeviceInfoBean>>> getDeviceList(@Body Map<String, Object> params);

    /**
     * 邀请家庭新成员
     */
    @POST(value = "business-app/v1/asset/memberInvite")
    Call<BaseResponse<String>> inviteMember(@Body Map<String, Object> params);


    /**
     * 成员退出家庭
     */
    @POST(value = "business-app/v1/asset/{assetId}/memberExit")
    Call<BaseResponse<Boolean>> exitFamily(@Path("assetId") String assetId);

    /**
     * 获取资产成员邀请的记录
     */
    @POST(value = "business-app/v1/asset/{assetId}/inviteList")
    Call<BaseResponse<List<InviteMemberBean>>> getMemberInvitedList(@Path("assetId") String assetId);

    /**
     * 获取被邀请的记录
     */
    @POST(value = "business-app/v1/asset/myInviteList")
    Call<BaseResponse<List<InviteMemberBean>>> getInvitedList();

    /**
     * 获取被邀请的记录
     */
    @POST(value = "business-app/v1/asset/memberInviteConfirm")
    Call<BaseResponse<Boolean>> acceptInvite(@Body Map<String, Object> params);

    /**
     * 排序
     */
    @POST(value = "business-app/v1/asset/setSorts")
    Call<BaseResponse<Boolean>> sortRoom(@Body List<SortBean> params);

    /**
     * 修改家庭信息
     */
    @POST(value = "business-app/v1/asset/update")
    Call<BaseResponse<Object>> updateFamilyInfo(@Body Map<String, Object> params);

    /**
     * 设置成员角色
     */
    @POST(value = "business-app/v1/asset/setMemberRole")
    Call<BaseResponse<Object>> setMemberRole(@Body Map<String, Object> params);

    /**
     * 批量修改设备资产
     */
    @POST(value = "business-app/v1/device/batchChangeDeviceAsset")
    Call<BaseResponse<Object>> batchChangeDeviceAsset(@Body Map<String, Object> params);

    /**
     * 获取首页设列表(群组、设备)
     */
    @POST(value = "business-app/v1/device/getHomeDeviceAndGroupList")
    Call<BaseResponse<DeviceGroupBean>> getHomeDeviceList(@Body Map<String, Object> params);

    /**
     * 批量修改设备资产 设备+群组
     */
    @POST(value = "business-app/v1/device/batchChangeAssetAffiliation")
    Call<BaseResponse<Object>> batchChangeAssetAffiliation(@Body Map<String, Object> params);

    /**
     * 批量修改设备资产 设备+群组
     */
    @POST(value = "business-app/v1/xunfei/discovery/discoverDevice")
    Call<BaseResponse<Object>> discoverDevice();
}
