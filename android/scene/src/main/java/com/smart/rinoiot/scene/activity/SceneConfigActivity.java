package com.smart.rinoiot.scene.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.SceneActionBean;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.bean.SceneConditionBean;
import com.smart.rinoiot.common.bean.SceneTriggerBean;
import com.smart.rinoiot.common.bean.SelectItemBean;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.SceneManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.adapter.SceneActionAdapter;
import com.smart.rinoiot.scene.adapter.SceneConditionAdapter;
import com.smart.rinoiot.scene.databinding.ActivitySceneConfigBinding;
import com.smart.rinoiot.scene.viewmodel.SceneConfigViewModel;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SceneConfigActivity extends BaseActivity<ActivitySceneConfigBinding, SceneConfigViewModel> implements View.OnClickListener {

    public final static int REQUEST_CODE_FROM_ITEM_CONFIG = 0x1001;
    public final static int REQUEST_CODE_FROM_MORE = 0x1002;

    private SceneConditionAdapter conditionItemAdapter;
    private SceneActionAdapter taskItemAdapter;

    private String cityName;
    private String cityCode;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
        /// setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
        /// StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        initRecyclerView();
        SceneBean sceneBean = (SceneBean) getIntent().getSerializableExtra("scene_bean");
        sceneBean = updateTrigger(sceneBean);

        SceneManager.getInstance().setCurrentEditSceneBean(sceneBean);
        if (sceneBean.getSceneType() == Constant.SCENE_TYPE_FOR_MANUAL) {
            SceneConditionBean conditionBean = new SceneConditionBean();
            conditionBean.setCondType(Constant.SCENE_CONDITION_FOR_ONE_KEY);
            List<SceneConditionBean> data = new ArrayList<>();
            data.add(conditionBean);
            conditionItemAdapter.setNewInstance(data);
        } else {
            conditionItemAdapter.setNewInstance(sceneBean.getRuleMetaData().getConditions());
        }

        List<SceneActionBean> actionList = initializeActions(sceneBean);
        taskItemAdapter.setNewInstance(actionList);

        SceneConditionBean conditionBean = (SceneConditionBean) getIntent().getSerializableExtra(
                "condition_bean"
        );
        if (conditionBean != null) {
            sceneBean.setName(mViewModel.getSceneTitle(sceneBean, conditionBean));
            List<SceneConditionBean> data = new ArrayList<>();
            data.add(conditionBean);
            conditionItemAdapter.setNewInstance(data);
        }

        binding.tvSceneTitle.setText(sceneBean.getName());
        binding.tvMeetTheConditions.setText(
            getString(
                sceneBean.getRuleMetaData().getMatchType() == 2 ?
                        R.string.rino_scene_condition_section_sub_title1 : R.string.rino_scene_condition_section_sub_title2
            )
        );

        updateEffectivePeriod();
        updateConditionData();
        updateTaskData();

        binding.ivAddCondition.setOnClickListener(this);
        binding.viewAddCondition.setOnClickListener(this);
        binding.ivAddTask.setOnClickListener(this);
        binding.viewAddTask.setOnClickListener(this);
        binding.tvMeetTheConditions.setOnClickListener(this);
        binding.ivSceneMore.setOnClickListener(this);
        binding.tvSave.setOnClickListener(this);

        // setup observers
        setupObservers();
    }

    /**
     * Initialize actions
     *
     * @param sceneBean scene bean
     * @return the list of scene action beans
     */
    private List<SceneActionBean> initializeActions(SceneBean sceneBean){
        List<SceneActionBean> actionList = sceneBean.getRuleMetaData().getActions();
        if (actionList != null && actionList.size() > 0) {
            List<SceneBean> allSceneList = CacheDataManager.getInstance().getAllSceneList(
                    CacheDataManager.getInstance().getCurrentHomeId()
            );
            for (SceneActionBean sceneActionBean : actionList) {
                if (sceneActionBean.getActionType() == Constant.SCENE_TASK_FOR_ALREADY_SMART) {
                    for (SceneBean sceneBean1 : allSceneList) {
                        if (sceneBean1.getId().equals(sceneActionBean.getTargetId())) {
                            sceneActionBean.setActionName(sceneBean1.getName());
                            break;
                        }
                    }
                }
            }
        }
        return actionList;
    }

    /**
     * Initializes the scene trigger
     * @param sceneBean the scene bean
     */
    private SceneBean updateTrigger(SceneBean sceneBean){
        if (sceneBean == null) {
            sceneBean = new SceneBean();
            SceneTriggerBean sceneTriggerBean = new SceneTriggerBean();
            String tz = UserInfoManager.getInstance().getUserInfo(SceneConfigActivity.this).tz;
            if (TextUtils.isEmpty(tz)) {
                tz = AppUtil.getSystemTimeZone();
            }
            sceneTriggerBean.setTz(tz);
            sceneBean.getRuleMetaData().getTriggers().add(sceneTriggerBean);
            mViewModel.toLocationCity(this);
        } else {
            if (sceneBean.getRuleMetaData().getTriggers() == null || sceneBean.getRuleMetaData().getTriggers().size() == 0) {
                SceneTriggerBean sceneTriggerBean = new SceneTriggerBean();
                String tz = UserInfoManager.getInstance().getUserInfo(
                        SceneConfigActivity.this
                ).tz;
                if (TextUtils.isEmpty(tz)) {
                    tz = AppUtil.getSystemTimeZone();
                }
                sceneTriggerBean.setTz(tz);
                sceneBean.getRuleMetaData().getTriggers().add(sceneTriggerBean);
                mViewModel.toLocationCity(this);
            }
        }
        return sceneBean;
    }

    /**
     * Set event observers
     */
    private void setupObservers(){
        mViewModel.getEditSceneLiveData().observe(this, data -> {
            mViewModel.hideLoading();
            if (data != null) {
                if (data.getSceneType() == Constant.SCENE_TYPE_FOR_AUTO && TextUtils.isEmpty(mViewModel.getSceneBean().getId())) {
                    DialogUtil.showNormalMsg(this, "", getString(R.string.rino_scene_is_enable_now), getString(R.string.rino_scene_is_enable_now_no), getString(R.string.rino_scene_is_enable_now_yes), new DialogOnListener() {
                        @Override
                        public void onCancel() {
                            finishThis();
                        }

                        @Override
                        public void onConfirm() {
                            mViewModel.showLoading();
                            mViewModel.changeSceneStatus(data.getId(), 1);
                        }
                    });
                } else {
                    ToastUtil.showMsg(R.string.rino_common_save_success);
                    finishThis();
                }
            }
        });

        mViewModel.getEnableSceneLiveData().observe(this, isSuccess -> {
            mViewModel.hideLoading();
            if (isSuccess) {
                ToastUtil.showMsg(R.string.rino_common_save_success);
            }
            finishThis();
        });

        mViewModel.getCityLiveData().observe(this, cityBean -> {
            cityName = cityBean.getName();
            cityCode = cityBean.getCityCode();
        });
    }

    private void initRecyclerView() {
        // 设置监听器，并创建菜单：
        binding.recyclerViewCondition.setSwipeMenuCreator(mSwipeMenuCreator);
        binding.recyclerViewTask.setSwipeMenuCreator(mSwipeMenuCreator);
        // 拖拽排序，默认关闭。
        binding.recyclerViewCondition.setLongPressDragEnabled(true);
        binding.recyclerViewTask.setLongPressDragEnabled(true);

        // 菜单点击监听。
        binding.recyclerViewCondition.setOnItemMenuClickListener((menuBridge, position) -> {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();
            if (conditionItemAdapter.getData().size() == 1) {
                mViewModel.getSceneBean().setSceneType(-1);
            }
            conditionItemAdapter.getData().remove(position);
            conditionItemAdapter.notifyDataSetChanged();
            updateConditionData();
        });
        binding.recyclerViewTask.setOnItemMenuClickListener((menuBridge, position) -> {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();
            taskItemAdapter.getData().remove(position);
            taskItemAdapter.notifyDataSetChanged();
            updateTaskData();
        });

        conditionItemAdapter = new SceneConditionAdapter(new ArrayList<>());
        binding.recyclerViewCondition.setAdapter(conditionItemAdapter);

        taskItemAdapter = new SceneActionAdapter(new ArrayList<>());
        binding.recyclerViewTask.setAdapter(taskItemAdapter);

        conditionItemAdapter.setOnItemClickListener((adapter, view, position) -> mViewModel.editCondition(SceneConfigActivity.this, conditionItemAdapter.getData().get(position), position, REQUEST_CODE_FROM_ITEM_CONFIG));
        taskItemAdapter.setOnItemClickListener((adapter, view, position) -> mViewModel.editTask(SceneConfigActivity.this, taskItemAdapter.getData().get(position), taskItemAdapter.getData(), position, REQUEST_CODE_FROM_ITEM_CONFIG));

        binding.recyclerViewCondition.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                // 此方法在Item拖拽交换位置时被调用。
                // 第一个参数是要交换为之的Item，第二个是目标位置的Item。

                // 交换数据，并更新adapter。
                int fromPosition = srcHolder.getBindingAdapterPosition();
                int toPosition = targetHolder.getBindingAdapterPosition();
                Collections.swap(conditionItemAdapter.getData(), fromPosition, toPosition);
                conditionItemAdapter.notifyItemMoved(fromPosition, toPosition);

                // 返回true，表示数据交换成功，ItemView可以交换位置。
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                // 此方法在Item在侧滑删除时被调用。
            }
        });

        binding.recyclerViewTask.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                // 此方法在Item拖拽交换位置时被调用。
                // 第一个参数是要交换为之的Item，第二个是目标位置的Item。

                // 交换数据，并更新adapter。
                int fromPosition = srcHolder.getBindingAdapterPosition();
                int toPosition = targetHolder.getBindingAdapterPosition();
                Collections.swap(taskItemAdapter.getData(), fromPosition, toPosition);
                taskItemAdapter.notifyItemMoved(fromPosition, toPosition);

                // 返回true，表示数据交换成功，ItemView可以交换位置。
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                // 此方法在Item在侧滑删除时被调用。
            }
        });
    }

    private final SwipeMenuCreator mSwipeMenuCreator = (leftMenu, rightMenu, position) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dpp_400);
        int height = -1;
        // 各种文字和图标属性设置。
        SwipeMenuItem deleteItem = (new SwipeMenuItem(getBaseContext()))
                .setBackgroundColorResource(R.color.red)
                .setText(R.string.rino_common_delete)
                .setTextSize(28)
                .setTextColor(-1)
                .setWidth(width)
                .setHeight(height);
        rightMenu.addMenuItem(deleteItem);
        // 注意：哪边不想要菜单，那么不要添加即可。
    };

    private void updateConditionData() {
        if (conditionItemAdapter != null && conditionItemAdapter.getData().size() > 0) {
            binding.tvMeetTheConditions.setVisibility(mViewModel.getSceneBean().getSceneType() == Constant.SCENE_TYPE_FOR_MANUAL ? View.GONE : View.VISIBLE);
            binding.ivAddCondition.setVisibility(mViewModel.getSceneBean().getSceneType() == Constant.SCENE_TYPE_FOR_MANUAL ? View.GONE : View.VISIBLE);
            binding.viewAddCondition.setVisibility(View.GONE);
            binding.tvSceneEffectiveTime.setVisibility(mViewModel.getSceneBean().getSceneType() == Constant.SCENE_TYPE_FOR_MANUAL ? View.GONE : View.VISIBLE);
        } else {
            binding.tvMeetTheConditions.setVisibility(View.GONE);
            binding.ivAddCondition.setVisibility(View.GONE);
            binding.viewAddCondition.setVisibility(View.VISIBLE);
            binding.tvSceneEffectiveTime.setVisibility(View.GONE);
        }
    }

    private void updateTaskData() {
        if (taskItemAdapter != null && taskItemAdapter.getData().size() > 0) {
            binding.ivAddTask.setVisibility(View.VISIBLE);
            binding.viewAddTask.setVisibility(View.GONE);
        } else {
            binding.ivAddTask.setVisibility(View.GONE);
            binding.viewAddTask.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateEffectivePeriod() {
        SceneTriggerBean sceneTriggerBean = mViewModel.getSceneBean().getRuleMetaData().getTriggers().get(0);
        switch (sceneTriggerBean.getTimeInterval()) {
            case Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_ALL_DAY:
                binding.tvSceneEffectiveTime.setText(getString(R.string.rino_scene_effective_period_allday));
                break;
            case Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_DAY:
                binding.tvSceneEffectiveTime.setText(getString(R.string.rino_scene_effective_period_daytime));
                break;
            case Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_NIGHT:
                binding.tvSceneEffectiveTime.setText(getString(R.string.rino_scene_effective_period_atnight));
                break;
            case Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_CUSTOM:
                binding.tvSceneEffectiveTime.setText(mViewModel.getEffectivePeriodStartTime(sceneTriggerBean) + "-" + mViewModel.getEffectivePeriodEndTime(sceneTriggerBean));
                break;
            default:
                break;
        }
    }

    @Override
    public ActivitySceneConfigBinding getBinding(LayoutInflater inflater) {
        return ActivitySceneConfigBinding.inflate(inflater);
    }

    @Override
    public void onBack(View view) {
        if (!TextUtils.isEmpty(mViewModel.getSceneBean().getId())) {
            DialogUtil.showNormalMsg(this, "", getString(R.string.rino_scene_is_cancel_edit_scene), new DialogOnListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onConfirm() {
                    finishThis();
                }
            });
            return;
        }
        super.onBack(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivAddCondition || v.getId() == R.id.viewAddCondition) {
            List<SelectItemBean> dataArray = new ArrayList<>();
            SelectItemBean item = new SelectItemBean();
            boolean isClick = true;
            String tip = "";
            if (conditionItemAdapter.getData().size() > 0) {
                isClick = false;
                tip = getString(R.string.rino_scene_condition_error_for_manual);
            }
            if (isClick && taskItemAdapter.getData().size() > 0) {
                boolean hasNotifyTask = false;
                for (SceneActionBean sceneActionBean : taskItemAdapter.getData()) {
                    if (sceneActionBean.getActionType() == Constant.SCENE_TASK_FOR_NOTIFY ||
                            (sceneActionBean.getActionType() == Constant.SCENE_TASK_FOR_ALREADY_SMART && TextUtils.isEmpty(sceneActionBean.getActionData()))) {
                        hasNotifyTask = true;
                        break;
                    }
                }

                if (hasNotifyTask) {
                    isClick = false;
                    tip = getString(R.string.rino_scene_action_not_support_this_condition);
                }
            }

            item.setIconRes(isClick ? R.drawable.ic_scene_one_click_execution : R.drawable.ic_scene_one_click_execution_disable);
            item.setTitle(getString(R.string.rino_scene_manual_title));
            item.setTips(tip);
            item.setClickable(isClick);
            dataArray.add(item);

            item = new SelectItemBean();
            item.setIconRes(R.drawable.ic_scene_weather_change);
            item.setTitle(getString(R.string.rino_scene_weather_changes_title));
            item.setClickable(true);
            dataArray.add(item);

            item = new SelectItemBean();
            item.setIconRes(R.drawable.ic_scene_timing);
            item.setTitle(getString(R.string.rino_scene_timing_title));
            item.setClickable(true);
            dataArray.add(item);

            item = new SelectItemBean();
            item.setIconRes(R.drawable.ic_scene_device_change);
            item.setTitle(getString(R.string.rino_scene_device_change_title));
            item.setClickable(true);
            dataArray.add(item);

            mViewModel.createBottomDialog(v.getContext(), getString(R.string.rino_scene_add_condition_title), dataArray, (position, item1) -> {
                if (getString(R.string.rino_scene_manual_title).equals(item1.getTitle())) {
                    mViewModel.getSceneBean().setSceneType(Constant.SCENE_TYPE_FOR_MANUAL);
                    SceneConditionBean conditionBean = new SceneConditionBean();
                    conditionBean.setCondType(Constant.SCENE_CONDITION_FOR_ONE_KEY);

                    List<SceneConditionBean> data = new ArrayList<>();
                    data.add(conditionBean);
                    conditionItemAdapter.setNewInstance(data);
                    updateConditionData();
                } else if (getString(R.string.rino_scene_weather_changes_title).equals(item1.getTitle())) {
                    startActivityForResult(new Intent(getBaseContext(), SceneListSelectActivity.class)
                            .putExtra("isNeedResult", true)
                            .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_weather_changes_title))
                            .putExtra(Constant.SCENE_LIST_SELECT_TYPE, Constant.SCENE_LIST_SELECT_FOR_WEATHER), REQUEST_CODE_FROM_ITEM_CONFIG);
                } else if (getString(R.string.rino_scene_timing_title).equals(item1.getTitle())) {
                    startActivityForResult(new Intent(getBaseContext(), SceneTimingActivity.class)
                            .putExtra("isNeedResult", true), REQUEST_CODE_FROM_ITEM_CONFIG);
                } else if (getString(R.string.rino_scene_device_change_title).equals(item1.getTitle())) {
                    startActivity(new Intent(this, SceneSelectDeviceActivity.class).putExtra("condition_or_task", Constant.SCENE_CHILDREN_TYPE_FOR_CONDITION));
                }
            });
        } else if (v.getId() == R.id.ivAddTask || v.getId() == R.id.viewAddTask) {
            boolean isContainsOneKey = mViewModel.getSceneBean().getSceneType() == Constant.SCENE_TYPE_FOR_MANUAL;

            List<SelectItemBean> dataArray = new ArrayList<>();
            SelectItemBean item = new SelectItemBean();
            item.setIconRes(R.drawable.ic_scene_task_operate_device);
            item.setTitle(getString(R.string.rino_scene_task_device));
            item.setClickable(true);
            dataArray.add(item);

            item = new SelectItemBean();
            item.setIconRes(R.drawable.ic_scene_task_already_smart);
            item.setTitle(getString(R.string.rino_scene_task_select_scene));
            item.setClickable(true);
            dataArray.add(item);

            item = new SelectItemBean();
            item.setIconRes(isContainsOneKey ? R.drawable.ic_scene_task_notify_disable : R.drawable.ic_scene_notify_center);
            item.setTitle(getString(R.string.rino_scene_task_message_notice));
            item.setTips(getString(R.string.rino_scene_task_error_for_manual));
            item.setClickable(!isContainsOneKey);
            dataArray.add(item);

            item = new SelectItemBean();
            item.setIconRes(R.drawable.ic_scene_task_delayed);
            item.setTitle(getString(R.string.rino_scene_task_delay));
            item.setClickable(true);
            dataArray.add(item);

            mViewModel.createBottomDialog(v.getContext(), getString(R.string.rino_scene_add_task_title), dataArray, (position, item1) -> {
                if (getString(R.string.rino_scene_task_device).equals(item1.getTitle())) {
                    startActivity(new Intent(this, SceneSelectDeviceActivity.class)
                            .putExtra("action_array_list", (Serializable) taskItemAdapter.getData())
                            .putExtra("condition_or_task", Constant.SCENE_CHILDREN_TYPE_FOR_TASK));
                } else if (getString(R.string.rino_scene_task_select_scene).equals(item1.getTitle())) {
                    startActivity(new Intent(getBaseContext(), SceneListSelectActivity.class)
                            .putExtra("action_array_list", (Serializable) taskItemAdapter.getData())
                            .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_task_select_scene))
                            .putExtra(Constant.SCENE_LIST_SELECT_TYPE, Constant.SCENE_LIST_SELECT_FOR_ALREADY_SMART));
                } else if (getString(R.string.rino_scene_task_message_notice).equals(item1.getTitle())) {
                    int index = -1;
                    for (int i = 0; i < taskItemAdapter.getData().size(); i++) {
                        SceneActionBean actionBean = taskItemAdapter.getData().get(i);
                        if (Constant.SCENE_TASK_FOR_NOTIFY == actionBean.getActionType()) {
                            index = i;
                            break;
                        }
                    }
                    startActivityForResult(new Intent(getBaseContext(), SceneMultipleConfigActivity.class)
                            .putExtra("position", index)
                            .putExtra("action_array_list", (Serializable) taskItemAdapter.getData())
                            .putExtra("isNeedResult", true)
                            .putExtra(Constant.ACTIVITY_TITLE, getString(R.string.rino_scene_task_message_way))
                            .putExtra(Constant.SCENE_MULTIPLE_CONFIG_TYPE, Constant.SCENE_MULTIPLE_CONFIG_FOR_NOTIFY), REQUEST_CODE_FROM_ITEM_CONFIG);
                } else if (getString(R.string.rino_scene_task_delay).equals(item1.getTitle())) {
                    startActivityForResult(new Intent(getBaseContext(), SceneDelayActivity.class)
                            .putExtra("isNeedResult", true), REQUEST_CODE_FROM_ITEM_CONFIG);
                }
            });
        } else if (v.getId() == R.id.tvMeetTheConditions) {
            List<SelectItemBean> dataArray = new ArrayList<>();
            SelectItemBean item = new SelectItemBean();
            item.setIconRes(-1);
            item.setTitle(getString(R.string.rino_scene_condition_section_sub_title1));
            item.setTextGravity(Gravity.CENTER);
            item.setHideArrow(true);
            item.setClickable(true);
            item.setShowLine(false);
            dataArray.add(item);

            item = new SelectItemBean();
            item.setIconRes(-1);
            item.setTitle(getString(R.string.rino_scene_condition_section_sub_title2));
            item.setTextGravity(Gravity.CENTER);
            item.setHideArrow(true);
            item.setClickable(true);
            item.setShowLine(false);
            dataArray.add(item);

            mViewModel.createBottomDialog(v.getContext(), "", dataArray, (position, item12) -> {
                binding.tvMeetTheConditions.setText(item12.getTitle());
                mViewModel.getSceneBean().getRuleMetaData().setMatchType(position == 0 ? 2 : 1);
            });
        } else if (v.getId() == R.id.ivSceneMore) {
            startActivityForResult(new Intent(this, SceneMoreActivity.class), REQUEST_CODE_FROM_MORE);
        } else if (v.getId() == R.id.tvSave) {
            if (conditionItemAdapter == null || conditionItemAdapter.getData().size() <= 0) {
                ToastUtil.showMsg(R.string.rino_scene_please_set_condition);
                return;
            }

            if (taskItemAdapter == null || taskItemAdapter.getData().size() <= 0) {
                ToastUtil.showMsg(R.string.rino_scene_please_set_action);
                return;
            }

            if (taskItemAdapter.getData().size() == 1 && taskItemAdapter.getData().get(0).getActionType() == Constant.SCENE_TASK_FOR_DELAYED) {
                ToastUtil.showMsg(R.string.rino_scene_can_not_only_one_delay_action);
                return;
            }

            if (taskItemAdapter.getData().get(taskItemAdapter.getData().size() - 1).getActionType() == Constant.SCENE_TASK_FOR_DELAYED) {
                ToastUtil.showMsg(R.string.rino_scene_can_not_last_one_delay_action);
                return;
            }

            SceneBean sceneBean = mViewModel.getSceneBean();

            if (sceneBean.getSceneType() == Constant.SCENE_TYPE_FOR_AUTO) {
                List<SceneConditionBean> conditionList = conditionItemAdapter.getData();
                for (int i = 0; i < conditionList.size(); i++) {
                    conditionList.get(i).setOrderNum(i + 1);
                }
                sceneBean.getRuleMetaData().setConditions(conditionList);

                if (sceneBean.getRuleMetaData().getTriggers() != null && sceneBean.getRuleMetaData().getTriggers().size() > 0) {
                    SceneTriggerBean triggerBean = sceneBean.getRuleMetaData().getTriggers().get(0);
                    if (TextUtils.isEmpty(triggerBean.getCityCode())) {
                        if (TextUtils.isEmpty(cityCode)) {
                            cityCode = "430101";
                            cityName = "ChangSha";
                        }
                        sceneBean.getRuleMetaData().getTriggers().get(0).setCityCode(cityCode);
                        sceneBean.getRuleMetaData().getTriggers().get(0).setCityName(cityName);
                    }
                }
            }
            List<SceneActionBean> actionList = taskItemAdapter.getData();
            for (int i = 0; i < actionList.size(); i++) {
                actionList.get(i).setOrderNum(i + 1);
            }
            sceneBean.getRuleMetaData().setActions(actionList);
            sceneBean.setAssetId(CacheDataManager.getInstance().getCurrentHomeId());
            // TODO 强制设置默认图标
            sceneBean.setCoverUrl("https://storage-app.rinoiot.com/scene/icon/default-icon.png");

            mViewModel.showLoading();
            if (TextUtils.isEmpty(sceneBean.getId())) {
                mViewModel.createScene(sceneBean);
            } else {
                mViewModel.updateScene(sceneBean);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_FROM_ITEM_CONFIG) {
            if (data != null) {
                int position = data.getIntExtra("position", -1);
                SceneConditionBean conditionBean = (SceneConditionBean) data.getSerializableExtra("condition_bean");
                if (conditionBean != null) {
                    if (position == -1) {
                        if (conditionItemAdapter.getData().size() == 0) {
                            mViewModel.getSceneBean().setSceneType(Constant.SCENE_CONDITION_FOR_ONE_KEY == conditionBean.getCondType() ? Constant.SCENE_TYPE_FOR_MANUAL : Constant.SCENE_TYPE_FOR_AUTO);
                        }
                        conditionItemAdapter.getData().add(conditionBean);
                    } else {
                        conditionItemAdapter.getData().set(position, conditionBean);
                    }
                    conditionItemAdapter.notifyDataSetChanged();
                    updateConditionData();
                }

                SceneActionBean actionBean = (SceneActionBean) data.getSerializableExtra("task_bean");
                if (actionBean != null) {
                    if (position == -1) {
                        taskItemAdapter.getData().add(actionBean);
                    } else {
                        taskItemAdapter.getData().set(position, actionBean);
                    }
                    taskItemAdapter.notifyDataSetChanged();
                    updateTaskData();
                }

                if (data.getSerializableExtra("action_array_list") != null) {
                    List<SceneActionBean> actionBeanList =
                            (List<SceneActionBean>) data.getSerializableExtra("action_array_list");
                    taskItemAdapter.setNewInstance(actionBeanList);
                    taskItemAdapter.notifyDataSetChanged();
                    updateTaskData();
                }
            }
        } else if (requestCode == REQUEST_CODE_FROM_MORE) {
            binding.tvSceneTitle.setText(mViewModel.getSceneBean().getName());
            updateEffectivePeriod();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        SceneConditionBean conditionBean =
                (SceneConditionBean) intent.getSerializableExtra("condition_bean");
        if (conditionBean != null) {
            if (conditionItemAdapter.getData().size() == 0) {
                mViewModel.getSceneBean().setSceneType(Constant.SCENE_CONDITION_FOR_ONE_KEY == conditionBean.getCondType() ? Constant.SCENE_TYPE_FOR_MANUAL : Constant.SCENE_TYPE_FOR_AUTO);
            }
            conditionItemAdapter.getData().add(conditionBean);
            conditionItemAdapter.notifyDataSetChanged();
            updateConditionData();
        }

        SceneActionBean actionBean = (SceneActionBean) intent.getSerializableExtra("task_bean");
        if (actionBean != null) {
            taskItemAdapter.getData().add(actionBean);
            taskItemAdapter.notifyDataSetChanged();
            updateTaskData();
        }

        if (intent.getSerializableExtra("action_array_list") != null) {
            List<SceneActionBean> actionBeanList =
                    (List<SceneActionBean>) intent.getSerializableExtra("action_array_list");
            taskItemAdapter.setNewInstance(actionBeanList);
            taskItemAdapter.notifyDataSetChanged();
            updateTaskData();
        }
    }
}
