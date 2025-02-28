package com.smart.rinoiot.scene.fragment;

import android.view.LayoutInflater;

import com.google.gson.Gson;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.scene.adapter.SceneDeviceAdapter;
import com.smart.rinoiot.scene.databinding.FragmentDeviceListBinding;
import com.smart.rinoiot.scene.viewmodel.SceneViewModel;

import java.util.ArrayList;

public class DeviceFragment extends BaseFragment<FragmentDeviceListBinding, SceneViewModel> {

    private SceneDeviceAdapter deviceAdapter;

    private final AssetBean assetData;

    public DeviceFragment(AssetBean assetData) {
        this.assetData = assetData;
    }

    @Override
    public void init() {
        deviceAdapter = new SceneDeviceAdapter(new ArrayList<>());
        binding.recyclerView.setAdapter(deviceAdapter);

        deviceAdapter.setOnItemClickListener((adapter, view, position) ->
            mViewModel.gotoDeviceFunctionSelect(requireContext(),
                    deviceAdapter.getItem(position).getId(),
                    requireActivity().getIntent().getSerializableExtra("action_array_list"),
                    requireActivity().getIntent().getIntExtra("condition_or_task", -1),
                    new Gson().toJson(deviceAdapter.getItem(position)))
        );

        if (assetData != null && assetData.getDeviceInfoBeans() != null && assetData.getDeviceInfoBeans().size() > 0) {
            hideEmptyView();

            deviceAdapter.setNewInstance(assetData.getDeviceInfoBeans());
            deviceAdapter.notifyDataSetChanged();
        } else {
            showEmptyView();
        }
    }

    @Override
    public FragmentDeviceListBinding getBinding(LayoutInflater inflater) {
        return FragmentDeviceListBinding.inflate(inflater);
    }
}
