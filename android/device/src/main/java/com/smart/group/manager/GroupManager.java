package com.smart.group.manager;

import android.app.Activity;

import com.smart.device.R;
import com.smart.group.api.GroupDeviceApiService;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.rn.PanelActivity;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

public class GroupManager {
    private static GroupManager instance;

    private GroupManager() {
    }

    public static GroupManager getInstance() {
        if (instance == null) {
            instance = new GroupManager();
        }
        return instance;
    }

    public void showNormalSheet(Activity activity, DeviceInfoBean infoBean) {
        String[] list = new String[2];
//        list[0] = activity.getString(R.string.rino_device_group_manager);
        list[0] = activity.getString(R.string.rino_device_remove_group);
        list[1] = activity.getString(R.string.rino_common_cancel);
        DialogUtil.showNormalSheet(activity, activity.getString(R.string.rino_device_group_empty_title), list, (position, text) -> {
//            if (position == 0) {//管理群里
//                Intent intent = new Intent(activity, CreateGroupActivity.class);
//                intent.putExtra(Constant.PRODUCT_ID, infoBean.getProductId());
//                if (infoBean.isCustomGroup()) {
//                    intent.putExtra(Constant.GROUP_ID, infoBean.getGroupId());
//                } else {
//                    intent.putExtra(Constant.DEV_ID, infoBean.getId());
//                }
//                activity.startActivity(intent);
//            } else
            if (position == 0) {//解散群组
                removeGroup(activity, infoBean.getGroupId(), false);
            }
        });
    }

    /**
     * 移除群组
     */
    public void removeGroup(Activity activity, String groupId, boolean isFinish) {
        RetrofitUtils.getService(GroupDeviceApiService.class).removeGroup(groupId).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                ToastUtil.info(activity.getString(R.string.rino_common_operation_success));
                AppManager.getInstance().finishActivity(PanelActivity.class);
                if (isFinish) activity.finish();
                if (!isFinish)
                    EventBus.getDefault().post(new DeviceEvent(DeviceEvent.Type.REMOVE_GROUP, "remove_group"));
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.info(msg);
            }
        });
    }
}
