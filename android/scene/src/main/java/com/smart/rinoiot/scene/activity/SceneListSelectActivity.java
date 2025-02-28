package com.smart.rinoiot.scene.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceDpJsonBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.SceneActionBean;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.adapter.ItemSelectAdapter;
import com.smart.rinoiot.scene.adapter.SingleSelectAdapter;
import com.smart.rinoiot.scene.bean.ItemSelectBean;
import com.smart.rinoiot.scene.bean.SingleSelectBean;
import com.smart.rinoiot.scene.databinding.ActivitySceneListSelectBinding;
import com.smart.rinoiot.scene.view.SeekbarBottomPopView;
import com.smart.rinoiot.scene.view.SingleSelectBottomPopView;
import com.smart.rinoiot.scene.viewmodel.SceneConfigViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SceneListSelectActivity extends BaseActivity<ActivitySceneListSelectBinding, SceneConfigViewModel> {

    private SingleSelectAdapter singleSelectAdapter;
    private ItemSelectAdapter itemSelectAdapter;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
//        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
//        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);

        String title = getIntent().getStringExtra(Constant.ACTIVITY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setToolBarTitle(title);
        }
        String subTitle = getIntent().getStringExtra(Constant.ACTIVITY_SUB_TITLE);
        if (!TextUtils.isEmpty(subTitle)) {
            binding.tvSubTitle.setText(subTitle);
        } else {
            binding.tvSubTitle.setVisibility(View.GONE);
        }

        hideEmptyView();
        switch (getIntent().getStringExtra(Constant.SCENE_LIST_SELECT_TYPE)) {
            case Constant.SCENE_LIST_SELECT_FOR_WEATHER: {
                List<ItemSelectBean> itemList = new ArrayList<>();
                ItemSelectBean item = new ItemSelectBean();
                item.setName(getString(R.string.rino_scene_weather_change_temperature));
                itemList.add(item);

                item = new ItemSelectBean();
                item.setName(getString(R.string.rino_scene_weather_change_humidity));
                itemList.add(item);

                item = new ItemSelectBean();
                item.setName(getString(R.string.rino_scene_weather_change_status));
                itemList.add(item);

                item = new ItemSelectBean();
                item.setName(getString(R.string.rino_scene_weather_change_pm25));
                itemList.add(item);

                item = new ItemSelectBean();
                item.setName(getString(R.string.rino_scene_weather_change_air_quality));
                itemList.add(item);

                item = new ItemSelectBean();
                item.setName(getString(R.string.rino_scene_weather_change_sunrise_sunset));
                itemList.add(item);

                item = new ItemSelectBean();
                item.setName(getString(R.string.rino_scene_weather_change_wind_speed));
                itemList.add(item);

                itemSelectAdapter = new ItemSelectAdapter(itemList);
                binding.recyclerView.setAdapter(itemSelectAdapter);
                binding.recyclerView.setVisibility(View.VISIBLE);

                itemSelectAdapter.setOnItemClickListener((adapter, view, position) -> {
                    if (getString(R.string.rino_scene_weather_change_temperature).equals(itemList.get(position).getName())) {
                        startActivity(new Intent(getBaseContext(), SceneSeekConfigActivity.class)
                                .putExtra(Constant.ACTIVITY_TITLE, itemList.get(position).getName())
                                .putExtra(Constant.SCENE_SEEK_CONFIG_TYPE, Constant.SCENE_SEEK_CONFIG_FOR_TEMPERATURE));
                    } else if (getString(R.string.rino_scene_weather_change_wind_speed).equals(itemList.get(position).getName())) {
                        startActivity(new Intent(getBaseContext(), SceneSeekConfigActivity.class)
                                .putExtra(Constant.ACTIVITY_TITLE, itemList.get(position).getName())
                                .putExtra(Constant.SCENE_SEEK_CONFIG_TYPE, Constant.SCENE_SEEK_CONFIG_FOR_WIND_SPEED));
                    } else if (getString(R.string.rino_scene_weather_change_humidity).equals(itemList.get(position).getName())) {
                        startActivity(new Intent(getBaseContext(), SceneSingleConfigActivity.class)
                                .putExtra(Constant.ACTIVITY_TITLE, itemList.get(position).getName())
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_HUMIDITY));
                    } else if (getString(R.string.rino_scene_weather_change_status).equals(itemList.get(position).getName())) {
                        startActivity(new Intent(getBaseContext(), SceneSingleConfigActivity.class)
                                .putExtra(Constant.ACTIVITY_TITLE, itemList.get(position).getName())
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_WEATHER));
                    } else if (getString(R.string.rino_scene_weather_change_pm25).equals(itemList.get(position).getName())) {
                        startActivity(new Intent(getBaseContext(), SceneSingleConfigActivity.class)
                                .putExtra(Constant.ACTIVITY_TITLE, itemList.get(position).getName())
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_PM25));
                    } else if (getString(R.string.rino_scene_weather_change_air_quality).equals(itemList.get(position).getName())) {
                        startActivity(new Intent(getBaseContext(), SceneSingleConfigActivity.class)
                                .putExtra(Constant.ACTIVITY_TITLE, itemList.get(position).getName())
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_AIR_QUALITY));
                    } else if (getString(R.string.rino_scene_weather_change_sunrise_sunset).equals(itemList.get(position).getName())) {
                        startActivity(new Intent(getBaseContext(), SceneSingleConfigActivity.class)
                                .putExtra(Constant.ACTIVITY_TITLE, itemList.get(position).getName())
                                .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_SUNSET_SUNRISE));
                    }
                });
            }
                break;
            case Constant.SCENE_LIST_SELECT_FOR_WEEK: {
                String weexListData = getIntent().getStringExtra("week_select_data");
                boolean mustSelectOne = getIntent().getBooleanExtra("must_select_one", false);
                if (!TextUtils.isEmpty(weexListData)) {
                    List<SingleSelectBean> weekList = new ArrayList<>();
                    SingleSelectBean item = new SingleSelectBean();
                    item.setName(getString(R.string.rino_scene_time_sunday));
                    item.setSelect("1".equals(String.valueOf(weexListData.charAt(0))));
                    weekList.add(item);

                    item = new SingleSelectBean();
                    item.setName(getString(R.string.rino_scene_time_monday));
                    item.setSelect("1".equals(String.valueOf(weexListData.charAt(1))));
                    weekList.add(item);

                    item = new SingleSelectBean();
                    item.setName(getString(R.string.rino_scene_time_tuesday));
                    item.setSelect("1".equals(String.valueOf(weexListData.charAt(2))));
                    weekList.add(item);

                    item = new SingleSelectBean();
                    item.setName(getString(R.string.rino_scene_time_wednesday));
                    item.setSelect("1".equals(String.valueOf(weexListData.charAt(3))));
                    weekList.add(item);

                    item = new SingleSelectBean();
                    item.setName(getString(R.string.rino_scene_time_thursday));
                    item.setSelect("1".equals(String.valueOf(weexListData.charAt(4))));
                    weekList.add(item);

                    item = new SingleSelectBean();
                    item.setName(getString(R.string.rino_scene_time_friday));
                    item.setSelect("1".equals(String.valueOf(weexListData.charAt(5))));
                    weekList.add(item);

                    item = new SingleSelectBean();
                    item.setName(getString(R.string.rino_scene_time_saturday));
                    item.setSelect("1".equals(String.valueOf(weexListData.charAt(6))));
                    weekList.add(item);

                    singleSelectAdapter = new SingleSelectAdapter(weekList);
                    binding.recyclerView.setAdapter(singleSelectAdapter);
                    binding.recyclerView.setVisibility(View.VISIBLE);

                    singleSelectAdapter.setOnItemClickListener((adapter, view, position) -> {
                        SingleSelectBean item1 = singleSelectAdapter.getData().get(position);
                        item1.setSelect(!item1.isSelect());

                        if (mustSelectOne && !item1.isSelect()) {
                            boolean isAllCancel = true;
                            for (SingleSelectBean item2: singleSelectAdapter.getData()) {
                                if (item2.isSelect()) {
                                    isAllCancel = false;
                                    break;
                                }
                            }

                            if (isAllCancel) {
                                item1.setSelect(!item1.isSelect());
                                ToastUtil.showMsg(R.string.rino_scene_at_least_one_day);
                            }
                        }
                        singleSelectAdapter.notifyDataSetChanged();
                    });
                }
            }
                break;
            case Constant.SCENE_LIST_SELECT_FOR_ALREADY_SMART: {
                List<ItemSelectBean> itemList = new ArrayList<>();
                ItemSelectBean item = new ItemSelectBean();
                item.setName(getString(R.string.rino_scene_task_select_manual));
                item.setClick(mViewModel.getSceneBean().getSceneType() != Constant.SCENE_TYPE_FOR_MANUAL);
                item.setTip(getString(R.string.rino_scene_task_clicked_tip));
                itemList.add(item);

                item = new ItemSelectBean();
                item.setName(getString(R.string.rino_scene_task_select_auto));
                itemList.add(item);

                itemSelectAdapter = new ItemSelectAdapter(itemList);
                binding.recyclerView.setAdapter(itemSelectAdapter);
                binding.recyclerView.setVisibility(View.VISIBLE);

                itemSelectAdapter.setOnItemClickListener((adapter, view, position) -> {
                    if (getString(R.string.rino_scene_task_select_manual).equals(itemList.get(position).getName())) {
                        if (itemList.get(position).isClick()) {
                            startActivity(new Intent(getBaseContext(), SceneMultipleConfigActivity.class)
                                    .putExtra("action_array_list", getIntent().getSerializableExtra("action_array_list"))
                                    .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_task_manual_list_title))
                                    .putExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE, Constant.SCENE_MULTIPLE_CONFIG_FOR_MANUAL_SCENE));
                        } else {
                            ToastUtil.showMsg(itemList.get(position).getTip());
                        }
                    } else if (getString(R.string.rino_scene_task_select_auto).equals(itemList.get(position).getName())) {
                        startActivity(new Intent(getBaseContext(), SceneMultipleConfigActivity.class)
                                .putExtra("action_array_list", getIntent().getSerializableExtra("action_array_list"))
                                .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_task_auto_list_title))
                                .putExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE, Constant.SCENE_MULTIPLE_CONFIG_FOR_AUTO_SCENE));
                    }
                });
            }
                break;
            case Constant.SCENE_LIST_SELECT_FOR_DEVICE: {
                if (getIntent().getIntExtra("condition_or_task", -1) == Constant.SCENE_CHILDREN_TYPE_FOR_TASK) {
                    setToolBarRightText(getString(R.string.rino_common_next));
                }

                DeviceInfoBean deviceInfo = new Gson().fromJson(getIntent().getStringExtra("device_info"), DeviceInfoBean.class);
                List<DeviceDpBean> deviceDpList = (List<DeviceDpBean>) getIntent().getSerializableExtra("device_dp_list");
                if (deviceDpList == null || deviceDpList.size() == 0) {
                    showEmptyView();
                } else {
                    List<ItemSelectBean> itemList = new ArrayList<>();
                    List<DeviceDpJsonBean> selectArray = new ArrayList<>();
                    ItemSelectBean item;
                    for (DeviceDpBean deviceDpBean: deviceDpList) {
                        DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);

                        if (deviceDpJson.getAccessMode().equals("rw")
                                && (deviceDpJson.getDataType().getType().equals("bool")
                                || deviceDpJson.getDataType().getType().equals("enum")
                                || deviceDpJson.getDataType().getType().equals("int"))) {
                            item = new ItemSelectBean();
                            item.setName(deviceDpJson.getName());
                            item.setData(deviceDpJson.getIdentifier());
                            itemList.add(item);
                            selectArray.add(deviceDpJson);
                        }
                    }

                    itemSelectAdapter = new ItemSelectAdapter(itemList);
                    binding.recyclerView.setAdapter(itemSelectAdapter);
                    binding.recyclerView.setVisibility(View.VISIBLE);

                    itemSelectAdapter.setOnItemClickListener((adapter, view, position) -> {
                        int type = getIntent().getIntExtra("condition_or_task", -1);
                        if (type == Constant.SCENE_CHILDREN_TYPE_FOR_TASK) {
                            try {
                                List<SingleSelectBean> data = new ArrayList<>();
                                DeviceDpJsonBean deviceDpJson = selectArray.get(position);
                                ItemSelectBean itemSelectBean = itemList.get(position);
                                SingleSelectBean dataItem;
                                if (deviceDpJson.getDataType().getType().equals("enum")) {
                                    List<String> enumList = (List<String>) deviceDpJson.getDataType().getSpecs().get("enums");
                                    for (int i = 0; i < enumList.size(); i++) {
                                        dataItem = new SingleSelectBean();
                                        dataItem.setName(enumList.get(i));
                                        dataItem.setShowLine(false);
                                        dataItem.setSelect(!TextUtils.isEmpty(itemSelectBean.getValue()) && itemSelectBean.getValue().equals(dataItem.getName()));
                                        data.add(dataItem);
                                    }
                                } else if (deviceDpJson.getDataType().getType().equals("bool")) {
                                    for (int i = 0; i < deviceDpJson.getDataType().getSpecs().size(); i++) {
                                        dataItem = new SingleSelectBean();
                                        dataItem.setName(String.valueOf(deviceDpJson.getDataType().getSpecs().get(String.valueOf(i == 1))));
                                        dataItem.setShowLine(false);
                                        dataItem.setSelect(!TextUtils.isEmpty(itemSelectBean.getValue()) && itemSelectBean.getValue().equals(dataItem.getName()));
                                        data.add(dataItem);
                                    }
                                } else if (deviceDpJson.getDataType().getType().equals("int")) {
                                    String unit = deviceDpJson.getDataType().getSpecs().get("unitName").toString();
                                    String value = itemSelectAdapter.getItem(position).getValue();
                                    if (!TextUtils.isEmpty(value)) {
                                        value = value.replace(unit, "");
                                    }

                                    SeekbarBottomPopView popupView = new SeekbarBottomPopView(SceneListSelectActivity.this);
                                    popupView.setData(itemSelectAdapter.getItem(position).getName(), deviceInfo.getId(), selectArray.get(position).getIdentifier(), TextUtils.isEmpty(value) ? 0 : Double.parseDouble(value));
                                    popupView.setOnConfirmListener(item12 -> {
                                        itemSelectAdapter.getItem(position).setValue(item12);
                                        itemSelectAdapter.notifyDataSetChanged();
                                    });

                                    new XPopup.Builder(SceneListSelectActivity.this)
                                            .isDarkTheme(false)
                                            .hasShadowBg(true)
                                            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                                            .asCustom(popupView)
                                            .show();
                                    return;
                                } else {
                                    for (int i = 0; i < deviceDpJson.getDataType().getSpecs().size(); i++) {
                                        dataItem = new SingleSelectBean();
                                        dataItem.setName(String.valueOf(deviceDpJson.getDataType().getSpecs().get(String.valueOf(i))));
                                        dataItem.setShowLine(false);
                                        dataItem.setSelect(!TextUtils.isEmpty(itemSelectBean.getValue()) && itemSelectBean.getValue().equals(dataItem.getName()));
                                        data.add(dataItem);
                                    }
                                }

                                SingleSelectBottomPopView popupView = new SingleSelectBottomPopView(SceneListSelectActivity.this);
                                popupView.setData(itemSelectAdapter.getItem(position).getName(), data);
                                popupView.setOnSelectListener((selectPosition, item12) -> {
                                    itemSelectAdapter.getItem(position).setValue(item12.getName());
                                    itemSelectAdapter.notifyDataSetChanged();
                                });

                                new XPopup.Builder(SceneListSelectActivity.this)
                                        .isDarkTheme(false)
                                        .hasShadowBg(true)
                                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                                        .asCustom(popupView)
                                        .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (type == Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION) {
                            DeviceDpJsonBean deviceDpJson = selectArray.get(position);
                            if (deviceDpJson.getDataType().getType().equals("int")) {
                                startActivity(new Intent(getBaseContext(), SceneSeekConfigActivity.class)
                                        .putExtra(Constant.SCENE_SEEK_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE)
                                        .putExtra("condition_or_task", Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION)
                                        .putExtra("device_id", deviceInfo.getId())
                                        .putExtra("dp_key", selectArray.get(position).getIdentifier()));
                            } else {
                                startActivity(new Intent(getBaseContext(), SceneSingleConfigActivity.class)
                                        .putExtra(Constant.SCENE_SINGLE_CONFIG_TYPE, Constant.SCENE_SINGLE_CONFIG_FOR_DEVICE)
                                        .putExtra("condition_or_task", Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION)
                                        .putExtra("device_id", deviceInfo.getId())
                                        .putExtra("dp_key", selectArray.get(position).getIdentifier()));
                            }
                        }
                    });
                }
            }
            break;
        }
    }

    @Override
    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (Constant.SCENE_LIST_SELECT_FOR_WEEK.equals(getIntent().getStringExtra(Constant.SCENE_LIST_SELECT_TYPE))) {
            if (singleSelectAdapter != null) {
                Intent data = new Intent();
                StringBuilder weekData = new StringBuilder();
                List<SingleSelectBean> resultList = singleSelectAdapter.getData();
                for (SingleSelectBean content : resultList) {
                    weekData.append(content.isSelect() ? "1" : "0");
                }
                data.putExtra("week_select_data", weekData.toString());
                setResult(RESULT_OK, data);
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        if (Constant.SCENE_LIST_SELECT_FOR_DEVICE.equals(getIntent().getStringExtra(Constant.SCENE_LIST_SELECT_TYPE))) {
            List<ItemSelectBean> selectArray = itemSelectAdapter.getData();
            if (selectArray.size() == 0) {
                ToastUtil.showMsg(R.string.rino_scene_select_function_tip);
                return;
            }

            List<SceneActionBean> actionArrayList = (List<SceneActionBean>) getIntent().getSerializableExtra("action_array_list");
            SceneActionBean actionBean;

            for (ItemSelectBean itemSelectBean: selectArray) {
                if (TextUtils.isEmpty(itemSelectBean.getValue())) continue;

                DeviceInfoBean deviceInfo = new Gson().fromJson(getIntent().getStringExtra("device_info"), DeviceInfoBean.class);
                for (DeviceDpBean deviceDpBean: CacheDataManager.getInstance().getDeviceDpList(deviceInfo.getId())) {
                    DeviceDpJsonBean deviceDpJson = new Gson().fromJson(deviceDpBean.getDpJson(), DeviceDpJsonBean.class);

                    if (TextUtils.equals(itemSelectBean.getData(), deviceDpJson.getIdentifier())) {
                        actionBean = new SceneActionBean();
                        actionBean.setActionType(Constant.SCENE_TASK_FOR_OPERATE_DEVICE);
                        actionBean.setTargetId(deviceInfo.getId());
                        Map<String, Object> props = new HashMap<>();
                        if ("bool".equals(deviceDpJson.getDataType().getType())) {
                            String key = "";
                            for (String mapKey: deviceDpJson.getDataType().getSpecs().keySet()) {
                                if (Objects.equals(deviceDpJson.getDataType().getSpecs().get(mapKey), itemSelectBean.getValue())) {
                                    key = mapKey;
                                    break;
                                }
                            }

                            props.put(deviceDpJson.getIdentifier(), "true".equals(key));
                        } else if ("int".equals(deviceDpJson.getDataType().getType())) {
                            String unit = deviceDpJson.getDataType().getSpecs().get("unitName").toString();
                            String value = itemSelectBean.getValue();
                            if (!TextUtils.isEmpty(value)) {
                                value = value.replace(unit, "");
                            }
                            props.put(deviceDpJson.getIdentifier(), value);
                        } else {
                            props.put(deviceDpJson.getIdentifier(), itemSelectBean.getValue());
                        }
                        actionBean.setActionData("{\"props\":" + new Gson().toJson(props) + "}");
                        actionArrayList.add(actionBean);
                        break;
                    }
                }
            }

            if (getIntent().getBooleanExtra("isNeedResult", false)) {
                setResult(RESULT_OK, new Intent().putExtra("action_array_list", (Serializable) actionArrayList));
            } else {
                startActivity(new Intent(this, SceneConfigActivity.class)
                        .putExtra("action_array_list", (Serializable) actionArrayList));
                AppManager.getInstance().finishActivity(SceneSelectDeviceActivity.class);
            }

            finishThis();
        }
    }

    @Override
    public ActivitySceneListSelectBinding getBinding(LayoutInflater inflater) {
        return ActivitySceneListSelectBinding.inflate(inflater);
    }
}
