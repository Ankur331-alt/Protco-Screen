package com.smart.group.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.lxj.xpopup.XPopup;
import com.smart.device.R;
import com.smart.group.api.GroupDeviceApiService;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.GroupBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tw
 * @time 2022/10/13 11:54
 * @description 创建群组 module
 */
public class CreateGroupViewModel extends BaseViewModel {
    private static final String TAG = CreateGroupViewModel.class.getSimpleName();

    public CreateGroupViewModel(@NonNull @NotNull Application application) {
        super(application);
    }


    /**
     * 创建或更新群组
     */
    private final MutableLiveData<GroupBean> groupBeanLiveData = new MutableLiveData<>();

    public MutableLiveData<GroupBean> getGroupBeanLiveData() {
        return groupBeanLiveData;
    }

    /**
     * 创建或更新群组
     */
    public void saveOrUpdate(List<String> deviceIds, String groupName, String groupId) {
        if (TextUtils.isEmpty(groupName)) {
            ToastUtil.showMsg(getString(R.string.rinp_device_group_input_name));
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("assetId", CacheDataManager.getInstance().getCurrentHomeId());
        map.put("deviceIds", deviceIds);
        map.put("name", groupName);
        map.put("id", groupId);
        RetrofitUtils.getService(GroupDeviceApiService.class).saveOrUpdate(map).enqueue(new BaseRequestListener<GroupBean>() {
            @Override
            public void onResult(GroupBean result) {
                ToastUtil.info(getString(R.string.rino_common_operation_success));
                groupBeanLiveData.postValue(result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.info(msg);
            }
        });
    }

    /**
     * 修改群组名称
     */
    private void updateGroupName(AppCompatActivity activity, String oldName, List<String> devIds, String groupId) {
        DialogUtil.showInputGroupNameDialog(activity, getString(R.string.rino_device_create_group_name), "", oldName, text -> saveOrUpdate(devIds, text, groupId), () -> {

        });
    }


    /**创建群组或者编辑群组时，必须有2个设备*/
    /**
     * 创建群组不满足条件提示框
     */
    public void showNormalGroupMsg(AppCompatActivity activity, String oldGroupName, List<String> devIds, String groupId) {
        if (devIds.size() >= 2) {
            if (TextUtils.isEmpty(groupId)) {
                updateGroupName(activity, oldGroupName, devIds, groupId);
            } else {
                saveOrUpdate(devIds, oldGroupName, groupId);
            }
            return;
        }
        XPopup.setPrimaryColor(R.color.main_theme_color);
        (new XPopup.Builder(activity)).hasStatusBarShadow(false).borderRadius(20.0F).isDestroyOnDismiss(true)
                .asConfirm(activity.getString(R.string.rino_common_alert_title),
                        activity.getString(R.string.rino_device_save_group_condition_tips),
                        "", activity.getString(R.string.rino_device_not_support_network_config_cancel), () -> {

                        }, () -> {

                        }, true).show();
    }

    /**
     * 获取群组详细
     */
    private final MutableLiveData<GroupBean> groupDetailLiveData = new MutableLiveData<>();

    public MutableLiveData<GroupBean> getGroupDetailLiveData() {
        return groupDetailLiveData;
    }

    public void getGroupDetail(String groupId) {
        RetrofitUtils.getService(GroupDeviceApiService.class).getGroupDetail(groupId).enqueue(new BaseRequestListener<GroupBean>() {
            @Override
            public void onResult(GroupBean result) {
                groupDetailLiveData.postValue(result);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.info(msg);
            }
        });
    }
}
