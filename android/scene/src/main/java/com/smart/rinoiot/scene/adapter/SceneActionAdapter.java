package com.smart.rinoiot.scene.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceDpJsonBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.SceneActionBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.DateUtils;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.scene.R;

import java.util.List;
import java.util.Map;

public class SceneActionAdapter extends BaseQuickAdapter<SceneActionBean, BaseViewHolder> {
    public SceneActionAdapter(@Nullable List<SceneActionBean> data) {
        super(R.layout.adapter_condition_or_task_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SceneActionBean item) {
        switch (item.getActionType()) {
            case Constant.SCENE_TASK_FOR_OPERATE_DEVICE:
                DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(item.getTargetId());
                List<DeviceDpBean> deviceDpList = CacheDataManager.getInstance().getDeviceDpList(item.getTargetId());
                if (deviceInfo != null) {
                    if(StringUtil.isBlank(deviceInfo.getImageUrl())){
                        helper.setImageResource(R.id.ivIcon, R.drawable.ic_scene_device_change);
                    }else{
                        ImageLoader.getInstance().bindImageUrl(
                                deviceInfo.getImageUrl(), helper.getView(R.id.ivIcon)
                        );
                    }
                    helper.setText(R.id.tvName, deviceInfo.getName());
                    helper.setGone(R.id.tvSubName, false);

                    for (DeviceDpBean deviceDpBean: deviceDpList) {
                        DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);
                        Map<String, Map<String, Object>> actionData = new Gson().fromJson(item.getActionData(), new TypeToken<Map<String, Object>>(){}.getType());
                        Map<String, Object> props = actionData.get("props");
                        if (props != null && props.containsKey(deviceDpJson.getIdentifier())) {
                            String value = String.valueOf(props.get(deviceDpJson.getIdentifier()));
                            if (deviceDpJson.getDataType().getType().equals("bool")) {
                                value = String.valueOf(deviceDpJson.getDataType().getSpecs().get(value));
                            } else if (deviceDpJson.getDataType().getType().equals("int")) {
                                Object unitName = deviceDpJson.getDataType().getSpecs().get("unitName");
                                if(null != unitName){
                                    String unit = unitName.toString();
                                    value = value + unit;
                                }
                            }
                            helper.setText(R.id.tvSubName, deviceDpJson.getName() + ": " + value);
                            break;
                        }
                    }
                } else {
                    helper.setImageResource(R.id.ivIcon, R.drawable.ic_default_circle_image);
                    helper.setText(R.id.tvName, getContext().getResources().getString(R.string.rino_scene_device_not_exist));
                    helper.setGone(R.id.tvSubName, true);
                }
                break;
            case Constant.SCENE_TASK_FOR_NOTIFY:
                helper.setImageResource(R.id.ivIcon, R.drawable.ic_scene_notify_center);
                helper.setText(R.id.tvSubName, getContext().getString(R.string.rino_scene_task_message_turn_on));
                helper.setText(R.id.tvName, item.getActionData().contains("\"noticeType\":1") ? getContext().getString(R.string.rino_scene_task_message_center) : item.getActionName());
                helper.setGone(R.id.tvSubName, false);
                break;
            case Constant.SCENE_TASK_FOR_DELAYED:
                helper.setImageResource(R.id.ivIcon, R.drawable.ic_scene_task_delayed);
                helper.setText(R.id.tvName, getContext().getString(R.string.rino_scene_task_delay));
                helper.setText(R.id.tvSubName, DateUtils.changeToTime(getContext(), item.getDelay()));
                helper.setGone(R.id.tvSubName, false);
                break;
            case Constant.SCENE_TASK_FOR_ALREADY_SMART:
                helper.setImageResource(R.id.ivIcon, R.drawable.ic_scene_task_already_smart);
                helper.setText(R.id.tvName, item.getActionName());
                if (!TextUtils.isEmpty(item.getActionData())) {
                    helper.setText(R.id.tvSubName, getContext().getString(R.string.rino_scene_task_auto_text) + ":"
                            + getContext().getString(item.getActionData().contains("\"enabled\":true") ? R.string.rino_scene_status_start : R.string.rino_scene_status_stop));
                } else {
                    helper.setText(R.id.tvSubName, getContext().getString(R.string.rino_scene_manual_title) + ":"
                            + getContext().getString(R.string.rino_scene_task_manual_text));
                }

                helper.setGone(R.id.tvSubName, false);
                break;
            default:
                break;
        }
    }
}
