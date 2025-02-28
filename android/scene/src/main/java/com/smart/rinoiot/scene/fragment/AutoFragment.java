package com.smart.rinoiot.scene.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.scwang.smart.refresh.header.MaterialHeader;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.FamilyPermissionManager;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.activity.CreateSceneActivity;
import com.smart.rinoiot.scene.activity.SceneConfigActivity;
import com.smart.rinoiot.scene.adapter.AutoSceneAdapter;
import com.smart.rinoiot.scene.databinding.FragmentSceneListBinding;
import com.smart.rinoiot.scene.manager.SceneNetworkManager;
import com.smart.rinoiot.scene.viewmodel.SceneViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author author
 */
public class AutoFragment extends BaseFragment<FragmentSceneListBinding, SceneViewModel> {

    private AutoSceneAdapter autoSceneAdapter;

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void init() {
        mViewModel.getSceneListLiveData().observe(this, sceneList -> {
            binding.refreshLayout.finishRefresh();
            if (sceneList != null && sceneList.size() > 0) {
                List<SceneBean> autoScenes = sceneList.stream()
                        .filter(scene -> scene.getSceneType() == Constant.SCENE_TYPE_FOR_AUTO)
                        .collect(Collectors.toList());

                if (autoScenes.size() > 0) {
                    autoSceneAdapter.setNewInstance(autoScenes);
                    autoSceneAdapter.notifyDataSetChanged();
                    pinedEmpty(true);
                } else {
                    pinedEmpty(false);
                }
            } else {
                pinedEmpty(false);
            }
        });

        autoSceneAdapter = new AutoSceneAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //配置布局，水平布局
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(autoSceneAdapter);

        autoSceneAdapter.setOnItemClickListener((adapter, view, position) -> {
            boolean hasPermission = FamilyPermissionManager.getInstance()
                    .getPermissionMemberRole(getContext(), null);
            if (!hasPermission) {
                DialogUtil.showNormalPermissionMsg((AppCompatActivity) getContext());
                return;
            }
            Intent intent = new Intent(requireContext(), SceneConfigActivity.class);
            intent.putExtra("scene_bean", autoSceneAdapter.getData().get(position));
            startActivity(intent);
        });

        autoSceneAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() != R.id.iv_status_toggle) {
                return;
            }
            toggleSceneStatus(position);
        });

        binding.refreshLayout.setRefreshHeader(new MaterialHeader(requireContext()));
        //下拉 刷新整个RecyclerView
        binding.refreshLayout.setOnRefreshListener(refreshLayout ->
                mViewModel.getAllScene(CacheDataManager.getInstance().getCurrentHomeId())
        );
        binding.llEmpty.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), CreateSceneActivity.class);
            intent.putExtra(Constant.SCENE_TYPE, 1);
            startActivity(intent);
        });
    }

    /**
     * Enables or disables the scene status
     * @param position the scene position
     */
    @SuppressLint("NotifyDataSetChanged")
    private void toggleSceneStatus(int position){
        mViewModel.showLoading();
        SceneBean sceneBean = autoSceneAdapter.getData().get(position);

        int status = sceneBean.getEnabled() == 1 ? 0 : 1;
        CallbackListener<Object> callbackListener = new CallbackListener<Object>() {
            @Override
            public void onSuccess(Object data) {
                mViewModel.hideLoading();
                sceneBean.setEnabled(sceneBean.getEnabled() == 1 ? 0 : 1);
                AppExecutors.getInstance().mainThread().execute(() -> {
                    String label = getString(sceneBean.getEnabled() == 1 ? R.string.rino_scene_auto_open : R.string.rino_scene_auto_close);
                    ToastUtil.customSystem(getContext(), label, Toast.LENGTH_SHORT);
                });
                autoSceneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String code, String error) {
                mViewModel.hideLoading();
                ToastUtil.showMsg(error);
            }
        };
        SceneNetworkManager.getInstance().changeSceneStatus(
                sceneBean.getId(),status , callbackListener
        );
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
                .getPermissionMemberRole(getContext(), null);
        binding.llEmpty.setVisibility(
                isHide ? View.GONE : !permissionMemberRole ? View.GONE : View.VISIBLE
        );
    }
}
