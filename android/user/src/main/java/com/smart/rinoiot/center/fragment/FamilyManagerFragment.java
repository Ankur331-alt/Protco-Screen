package com.smart.rinoiot.center.fragment;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.center.adapter.FamilyListAdapter;
import com.smart.rinoiot.center.adapter.JoinFamilyAdapter;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.InviteMemberBean;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.view.JoinFamilyPopupView;
import com.smart.rinoiot.family.activity.CreateFamilyActivity;
import com.smart.rinoiot.family.activity.FamilyInfoActivity;
import com.smart.rinoiot.family.manager.HomeDataManager;
import com.smart.rinoiot.family.viewmodel.FamilyManagerViewModel;
import com.smart.rinoiot.user.databinding.FragmentFamilyManagerBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 家庭管理
 */
public class FamilyManagerFragment extends BaseFragment<FragmentFamilyManagerBinding, FamilyManagerViewModel> {

    public final static int REQUEST_CODE_FROM_CREATE_FAMILY = 0x1001;
    public final static int REQUEST_CODE_FROM_EDIT_FAMILY = 0x1002;

    private List<AssetBean> list;
    private FamilyListAdapter familyListAdapter;
    private JoinFamilyAdapter joinFamilyAdapter;


    @Override
    public void init() {
        initView();
        loadListData(CacheDataManager.getInstance().getFamilyList());
        initLister();

    }

    private void initView() {
        // 自己的家庭
        binding.rvHomeList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        familyListAdapter = new FamilyListAdapter(getContext());
        familyListAdapter.setNewInstance(list);
        binding.rvHomeList.setAdapter(familyListAdapter);

        familyListAdapter.setOnItemClickListener((adapter, view, position) -> {
            AssetBean assetBean = (AssetBean) adapter.getData().get(position);
            if (assetBean == null) return;
            if (TextUtils.isEmpty(assetBean.getId())) {
                startActivityForResult(new Intent(getContext(), CreateFamilyActivity.class), REQUEST_CODE_FROM_CREATE_FAMILY);
                return;
            }
            HomeDataManager.getInstance().setAssetBean(assetBean);
            startActivityForResult(new Intent(getContext(), FamilyInfoActivity.class), REQUEST_CODE_FROM_EDIT_FAMILY);
        });

        // 待加入的家庭
        binding.rvJoinHomeList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        joinFamilyAdapter = new JoinFamilyAdapter(getContext());
        binding.rvJoinHomeList.setAdapter(joinFamilyAdapter);
        joinFamilyAdapter.setOnItemClickListener((adapter, view, position) -> showJoinFamilyDialog((InviteMemberBean) adapter.getData().get(position)));
        list = new ArrayList<>();
    }

    /**
     * 数据
     */
    private void loadListData(List<AssetBean> familyList) {
        list.clear();
        list.add(getAddAssetBean());
        if (familyList!=null)  list.addAll(familyList);
        familyListAdapter.setNewInstance(list);
        familyListAdapter.notifyDataSetChanged();
    }

    /**
     * 设置一个空的家庭
     */
    private AssetBean getAddAssetBean() {
        return new AssetBean();
    }

    /**
     * 监听
     */
    private void initLister() {

        mViewModel.getInviteMemberDataLive().observe(this, list -> {
            if (joinFamilyAdapter != null && list.size() > 0) {
                binding.llJoin.setVisibility(View.VISIBLE);
                joinFamilyAdapter.setNewInstance(list);
                joinFamilyAdapter.notifyDataSetChanged();
            } else {
                binding.llJoin.setVisibility(View.INVISIBLE);
            }
        });

        mViewModel.getFamilyListMutableLiveData().observe(this, this::loadListData);

        mViewModel.getIsAcceptMutableLiveData().observe(this, aBoolean -> {
            mViewModel.getFamilyList();
            mViewModel.getInviteMemberList();
        });
    }

    /**
     * 展示加入家庭弹框
     */
    public void showJoinFamilyDialog(InviteMemberBean inviteMemberBean) {
        new XPopup.Builder(getContext()).dismissOnTouchOutside(false).asCustom(new JoinFamilyPopupView(getContext(), new DialogOnListener() {
            @Override
            public void onCancel() {
                mViewModel.acceptInvited(false, inviteMemberBean.getId());
            }

            @Override
            public void onConfirm() {
                mViewModel.acceptInvited(true, inviteMemberBean.getId());
            }
        })).show();
    }

    @Override
    public FragmentFamilyManagerBinding getBinding(LayoutInflater inflater) {
        return FragmentFamilyManagerBinding.inflate(inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_CODE_FROM_CREATE_FAMILY || requestCode == REQUEST_CODE_FROM_EDIT_FAMILY) {
            mViewModel.getFamilyList();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mViewModel.getFamilyList();
            mViewModel.getInviteMemberList();
        }
    }
}
