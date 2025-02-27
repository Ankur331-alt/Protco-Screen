package com.smart.rinoiot.scene.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.SceneActionBean;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.bean.SelectItemBean;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.adapter.PartItemSelectAdapter;
import com.smart.rinoiot.scene.bean.ItemSelectBean;
import com.smart.rinoiot.scene.databinding.ActivitySceneSingleConfigBinding;
import com.smart.rinoiot.scene.viewmodel.SceneConfigViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SceneMultipleConfigActivity extends BaseActivity<ActivitySceneSingleConfigBinding, SceneConfigViewModel> {

    private PartItemSelectAdapter partItemSelectAdapter;
    private List<SceneActionBean> actionArrayList;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
//        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
//        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        setToolBarRightText(getString(R.string.rino_common_next));

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

        binding.viewItem.setVisibility(View.GONE);
        binding.recyclerView.setBackground(null);

        actionArrayList = (List<SceneActionBean>) getIntent().getSerializableExtra("action_array_list");
        List<SceneActionBean> actionBeanList = findActionList();

        List<ItemSelectBean> arrayList = new ArrayList<>();
        ItemSelectBean item;
        switch (getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE)) {
            case Constant.SCENE_MULTIPLE_CONFIG_FOR_NOTIFY:
                item = new ItemSelectBean();
                item.setIconRes(R.drawable.ic_scene_notify_center);
                item.setName(getString(R.string.rino_scene_task_message_center));
                item.setClick(true);
                item.setSelect(isSelect("1", actionBeanList) != null);
                arrayList.add(item);

                item = new ItemSelectBean();
                item.setIconRes(R.drawable.ic_scene_notify_phone);
                item.setName(getString(R.string.rino_scene_task_call_message));
                item.setClick(false);
                item.setTip(getString(R.string.rino_scene_task_no_open));
                item.setSelect(isSelect("2", actionBeanList) != null);
                arrayList.add(item);

                item = new ItemSelectBean();
                item.setIconRes(R.drawable.ic_scene_notify_sms);
                item.setName(getString(R.string.rino_scene_task_telephone_message));
                item.setClick(false);
                item.setTip(getString(R.string.rino_scene_task_no_open));
                item.setSelect(isSelect("3", actionBeanList) != null);
                arrayList.add(item);
                break;
            case Constant.SCENE_MULTIPLE_CONFIG_FOR_MANUAL_SCENE:
                binding.viewItem.setVisibility(View.GONE);

                List<SceneBean> allSceneList = CacheDataManager.getInstance().getAllSceneList(CacheDataManager.getInstance().getCurrentHomeId());
                if (allSceneList != null && allSceneList.size() > 0) {
                    for (SceneBean sceneBean: allSceneList) {
                        if (sceneBean.getSceneType() == Constant.SCENE_TYPE_FOR_MANUAL) {
                            item = new ItemSelectBean();
                            item.setName(sceneBean.getName());
                            item.setClick(true);
                            item.setSelect(isSelect(sceneBean.getId(), actionBeanList) != null);
                            item.setData(sceneBean.getId());
                            arrayList.add(item);
                        }
                    }
                }

                if (arrayList.size() == 0) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
                break;
            case Constant.SCENE_MULTIPLE_CONFIG_FOR_AUTO_SCENE:
                binding.viewItem.setVisibility(View.GONE);

                List<SceneBean> allSceneList2 = CacheDataManager.getInstance().getAllSceneList(CacheDataManager.getInstance().getCurrentHomeId());
                if (allSceneList2 != null && allSceneList2.size() > 0) {
                    for (SceneBean sceneBean: allSceneList2) {
                        if (sceneBean.getSceneType() == Constant.SCENE_TYPE_FOR_AUTO) {
                            item = new ItemSelectBean();
                            item.setName(sceneBean.getName());
                            item.setClick(true);
                            SceneActionBean sceneActionBean = isSelect(sceneBean.getId(), actionBeanList);
                            if (sceneActionBean != null) {
                                item.setSelect(true);
                                item.setValue(getString(sceneActionBean.getActionData().contains("\"enabled\":true") ? R.string.rino_scene_task_auto_start : R.string.rino_scene_task_auto_stop));
                            } else {
                                item.setSelect(false);
                            }
                            item.setData(sceneBean.getId());
                            arrayList.add(item);
                        }
                    }
                }

                if (arrayList.size() == 0) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
                break;
        }
        initMultipleChoice(arrayList);
    }

    private void initMultipleChoice(List<ItemSelectBean> array) {
        partItemSelectAdapter = new PartItemSelectAdapter(array);
        binding.recyclerView.setAdapter(partItemSelectAdapter);
        binding.recyclerView.setVisibility(View.VISIBLE);

        partItemSelectAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (Constant.SCENE_MULTIPLE_CONFIG_FOR_NOTIFY.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
                ItemSelectBean item = partItemSelectAdapter.getData().get(position);
                item.setSelect(!item.isSelect());
                partItemSelectAdapter.notifyDataSetChanged();
            } else if (Constant.SCENE_MULTIPLE_CONFIG_FOR_MANUAL_SCENE.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
                ItemSelectBean item = partItemSelectAdapter.getData().get(position);
                item.setSelect(!item.isSelect());
                partItemSelectAdapter.notifyDataSetChanged();
            } else if (Constant.SCENE_MULTIPLE_CONFIG_FOR_AUTO_SCENE.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
                ItemSelectBean item = partItemSelectAdapter.getData().get(position);
                if (!item.isSelect()) {
                    List<SelectItemBean> dataArray = new ArrayList<>();
                    SelectItemBean item1 = new SelectItemBean();
                    item1.setIconRes(-1);
                    item1.setTitle(getString(R.string.rino_scene_status_start));
                    item1.setTextGravity(Gravity.CENTER);
                    item1.setHideArrow(true);
                    item1.setClickable(true);
                    item1.setShowLine(false);
                    dataArray.add(item1);

                    item1 = new SelectItemBean();
                    item1.setIconRes(-1);
                    item1.setTitle(getString(R.string.rino_scene_status_stop));
                    item1.setTextGravity(Gravity.CENTER);
                    item1.setHideArrow(true);
                    item1.setClickable(true);
                    item1.setShowLine(false);
                    dataArray.add(item1);

                    mViewModel.createBottomDialog(SceneMultipleConfigActivity.this, "", dataArray, (position1, item12) -> {
                        item.setSelect(true);
                        item.setValue(getString(position1 == 0 ? R.string.rino_scene_task_auto_start : R.string.rino_scene_task_auto_stop));
                        partItemSelectAdapter.notifyDataSetChanged();
                    });
                } else {
                    item.setSelect(false);
                    item.setValue("");
                    partItemSelectAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private List<Integer> getSelectList() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < partItemSelectAdapter.getData().size(); i++) {
            if (partItemSelectAdapter.getData().get(i).isSelect()) {
                result.add(i);
            }
        }

        return result;
    }

    @Override
    public ActivitySceneSingleConfigBinding getBinding(LayoutInflater inflater) {
        return ActivitySceneSingleConfigBinding.inflate(inflater);
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        List<Integer> selectList = getSelectList();
        if (Constant.SCENE_MULTIPLE_CONFIG_FOR_AUTO_SCENE.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
            if (selectList.size() == 0) {
                ToastUtil.showMsg(R.string.rino_scene_task_no_select_auto_tip);
                return;
            }

            StringBuilder allSceneId = new StringBuilder();
            SceneActionBean actionBean;
            List<SceneActionBean> actionBeans = new ArrayList<>();
            for (int i:selectList) {
                ItemSelectBean item = partItemSelectAdapter.getData().get(i);
                actionBean = new SceneActionBean();
                actionBean.setActionType(Constant.SCENE_TASK_FOR_ALREADY_SMART);
                actionBean.setTargetId(item.getData());
                actionBean.setActionName(item.getName());
                actionBean.setActionData("{\"enabled\":" + (item.getValue().equals(getString(R.string.rino_scene_task_auto_start))) + ", \"sceneType\":2}");
                actionBeans.add(actionBean);
                if (allSceneId.length() > 0) allSceneId.append(",");
                allSceneId.append(item.getData());
            }

            for (SceneActionBean sceneActionBean: actionBeans) {
                int index = -1;
                for (int i = 0; i < actionArrayList.size(); i++) {
                    SceneActionBean item = actionArrayList.get(i);
                    if (item.getActionType() == Constant.SCENE_TASK_FOR_ALREADY_SMART && sceneActionBean.getTargetId().equals(item.getTargetId())) {
                        index = i;
                        break;
                    }
                }

                if (index >= 0) {
                    actionArrayList.set(index, sceneActionBean);
                } else {
                    actionArrayList.add(sceneActionBean);
                }
            }

            for (int i = 0; i < actionArrayList.size(); i++) {
                SceneActionBean item = actionArrayList.get(i);
                if (item.getActionType() == Constant.SCENE_TASK_FOR_ALREADY_SMART && !TextUtils.isEmpty(item.getActionData())) {
                    if (!allSceneId.toString().contains(item.getTargetId())) {
                        actionArrayList.remove(i);
                        --i;
                    }
                }
            }
        } else if (Constant.SCENE_MULTIPLE_CONFIG_FOR_MANUAL_SCENE.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
            if (selectList.size() == 0) {
                ToastUtil.showMsg(R.string.rino_scene_task_no_select_manual_tip);
                return;
            }

            StringBuilder allSceneId = new StringBuilder();
            SceneActionBean actionBean;
            List<SceneActionBean> actionBeans = new ArrayList<>();
            for (int i:selectList) {
                ItemSelectBean item = partItemSelectAdapter.getData().get(i);
                actionBean = new SceneActionBean();
                actionBean.setActionType(Constant.SCENE_TASK_FOR_ALREADY_SMART);
                actionBean.setTargetId(item.getData());
                actionBean.setActionName(item.getName());
                actionBean.setActionData("{\"enabled\":true, \"sceneType\":1}");
                actionBeans.add(actionBean);
                if (allSceneId.length() > 0) allSceneId.append(",");
                allSceneId.append(item.getData());
            }

            for (SceneActionBean sceneActionBean: actionBeans) {
                int index = -1;
                for (int i = 0; i < actionArrayList.size(); i++) {
                    SceneActionBean item = actionArrayList.get(i);
                    if (item.getActionType() == Constant.SCENE_TASK_FOR_ALREADY_SMART && sceneActionBean.getTargetId().equals(item.getTargetId())) {
                        index = i;
                        break;
                    }
                }

                if (index >= 0) {
                    actionArrayList.set(index, sceneActionBean);
                } else {
                    actionArrayList.add(sceneActionBean);
                }
            }

            for (int i = 0; i < actionArrayList.size(); i++) {
                SceneActionBean item = actionArrayList.get(i);
                if (item.getActionType() == Constant.SCENE_TASK_FOR_ALREADY_SMART && TextUtils.isEmpty(item.getActionData())) {
                    if (!allSceneId.toString().contains(item.getTargetId())) {
                        actionArrayList.remove(i);
                        --i;
                    }
                }
            }
        } else if (Constant.SCENE_MULTIPLE_CONFIG_FOR_NOTIFY.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
            if (selectList.size() == 0) {
                ToastUtil.showMsg(R.string.rino_scene_task_select_reminder_method);
                return;
            }

            SceneActionBean actionBean = new SceneActionBean();
            actionBean.setActionType(Constant.SCENE_TASK_FOR_NOTIFY);
            actionBean.setActionName(getString(R.string.rino_scene_task_message_center));
            actionBean.setActionData("{\"noticeType\":1}");

            int index = -1;
            for (int i = 0; i < actionArrayList.size(); i++) {
                SceneActionBean item = actionArrayList.get(i);
                if (item.getActionType() == Constant.SCENE_TASK_FOR_NOTIFY) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                actionArrayList.set(index, actionBean);
            } else {
                actionArrayList.add(actionBean);
            }
        }

        if (getIntent().getBooleanExtra("isNeedResult", false)) {
            setResult(RESULT_OK, new Intent().putExtra("action_array_list", (Serializable) actionArrayList));
        } else {
            startActivity(new Intent(this, SceneConfigActivity.class)
                    .putExtra("action_array_list", (Serializable) actionArrayList));
            AppManager.getInstance().finishActivity(SceneListSelectActivity.class);
            AppManager.getInstance().finishActivity(CreateSceneActivity.class);
        }

        finishThis();
    }

    private List<SceneActionBean> findActionList() {
        List<SceneActionBean> result = new ArrayList<>();
        for (int i = 0; i < actionArrayList.size(); i++) {
            SceneActionBean actionBean = actionArrayList.get(i);
            if (Constant.SCENE_MULTIPLE_CONFIG_FOR_NOTIFY.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
                if (Constant.SCENE_TASK_FOR_NOTIFY == actionBean.getActionType()) {
                    result.add(actionBean);
                }
            } else if (Constant.SCENE_MULTIPLE_CONFIG_FOR_MANUAL_SCENE.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
                if (Constant.SCENE_TASK_FOR_ALREADY_SMART == actionBean.getActionType() && TextUtils.isEmpty(actionBean.getActionData())) {
                    result.add(actionBean);
                }
            } else if (Constant.SCENE_MULTIPLE_CONFIG_FOR_AUTO_SCENE.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
                if (Constant.SCENE_TASK_FOR_ALREADY_SMART == actionBean.getActionType() && !TextUtils.isEmpty(actionBean.getActionData())) {
                    result.add(actionBean);
                }
            }
        }
        return result;
    }

    private SceneActionBean isSelect(String keyData, List<SceneActionBean> targetArray) {
        SceneActionBean result = null;
        for (SceneActionBean item: targetArray) {
            if (Constant.SCENE_MULTIPLE_CONFIG_FOR_NOTIFY.equals(getIntent().getStringExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE))) {
                if (item.getActionData().contains(keyData)) {
                    result = item;
                    break;
                }
            } else {
                if (keyData.equals(item.getTargetId())) {
                    result = item;
                    break;
                }
            }
        }
        return result;
    }
}
