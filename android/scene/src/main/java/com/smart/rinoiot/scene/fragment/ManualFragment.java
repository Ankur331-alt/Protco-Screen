package com.smart.rinoiot.scene.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.scwang.smart.refresh.header.MaterialHeader;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.bean.SceneConditionBean;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.FamilyPermissionManager;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.activity.SceneConfigActivity;
import com.smart.rinoiot.scene.adapter.ManualSceneAdapter;
import com.smart.rinoiot.scene.databinding.FragmentSceneListBinding;
import com.smart.rinoiot.scene.manager.SceneNetworkManager;
import com.smart.rinoiot.scene.viewmodel.SceneViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author author
 */
public class ManualFragment extends BaseFragment<FragmentSceneListBinding, SceneViewModel> {

    private ManualSceneAdapter manualSceneAdapter;

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void init() {
        mViewModel.getSceneListLiveData().observe(this, sceneList -> {
            binding.refreshLayout.finishRefresh();
            if (sceneList != null && sceneList.size() > 0) {
                List<SceneBean> manualScenes = sceneList.stream()
                        .filter(scene -> scene.getSceneType() == Constant.SCENE_TYPE_FOR_MANUAL)
                        .collect(Collectors.toList());

                if (manualScenes.size() > 0) {
                    manualSceneAdapter.setNewInstance(manualScenes);
                    manualSceneAdapter.notifyDataSetChanged();
                    pinedEmpty(true);
                } else {
                    pinedEmpty(false);
                }
            } else {
                pinedEmpty(false);
            }
        });

        manualSceneAdapter = new ManualSceneAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //配置布局，水平布局
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(manualSceneAdapter);

        manualSceneAdapter.setOnItemClickListener((adapter, view, position) -> {
            triggerScene(position);
        });

        manualSceneAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() != R.id.ll_manual_scene) {
                return;
            }
            triggerScene(position);
        });

        manualSceneAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            boolean hasPermission = FamilyPermissionManager.getInstance()
                    .getPermissionMemberRole(getContext(),null);
            if (!hasPermission) {
                DialogUtil.showNormalPermissionMsg((AppCompatActivity) getContext());
                return false;
            }

            if (view.getId() == R.id.ll_manual_scene) {
                Intent intent = new Intent(requireContext(), SceneConfigActivity.class);
                intent.putExtra("scene_bean", manualSceneAdapter.getData().get(position));
                startActivity(intent);
            }
            return false;
        });

        binding.refreshLayout.setRefreshHeader(new MaterialHeader(requireContext()));
        //下拉 刷新整个RecyclerView
        binding.refreshLayout.setOnRefreshListener(refreshLayout ->
                mViewModel.getAllScene(CacheDataManager.getInstance().getCurrentHomeId())
        );

        binding.llEmpty.setOnClickListener(v -> {
            SceneConditionBean conditionBean = new SceneConditionBean();
            conditionBean.setCondType(Constant.SCENE_CONDITION_FOR_ONE_KEY);

            startActivity(new Intent(getContext(), SceneConfigActivity.class)
                    .putExtra("condition_bean", conditionBean));
        });
    }

    /**
     * Triggers the automation/scene
     * @param position the scene position
     */
    private void triggerScene(int position) {
        mViewModel.showLoading();
        SceneBean sceneBean = manualSceneAdapter.getData().get(position);
        CallbackListener<Object> callbackListener = new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                mViewModel.hideLoading();
                ToastUtil.showMsg(getString(R.string.rino_scene_manual_execution_succeed));
                mViewModel.getAllScene(CacheDataManager.getInstance().getCurrentHomeId());
            }

            @Override
            public void onError(String code, String error) {
                mViewModel.hideLoading();
                ToastUtil.showMsg(error);
            }
        };
        SceneNetworkManager.getInstance().oneKeyExecute(sceneBean.getId(), callbackListener);
    }

    @Override
    public FragmentSceneListBinding getBinding(LayoutInflater inflater) {
        return FragmentSceneListBinding.inflate(inflater);
    }

    /**
     * 根据权限及是否有数据展示空布局
     */
    private void pinedEmpty(boolean isHide) {
        binding.recyclerView.setVisibility(!isHide ? View.GONE : View.VISIBLE);
        boolean permissionMemberRole = FamilyPermissionManager.getInstance()
                .getPermissionMemberRole(getContext(),null);
        binding.llEmpty.setVisibility(
                isHide ? View.GONE : !permissionMemberRole ? View.GONE : View.VISIBLE
        );
    }
}
