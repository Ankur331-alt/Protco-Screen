package com.smart.rinoiot.family.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.activity.MapActivity;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.MemberBean;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.FamilyPermissionManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.ActivityUtils;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.TextUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.view.InviteMemberPopupView;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.adapter.MemberAdapter;
import com.smart.rinoiot.family.adapter.RoomListAdapter;
import com.smart.rinoiot.family.databinding.ActivityFamilyInfoBinding;
import com.smart.rinoiot.family.listener.OnApiDataChangeObserver;
import com.smart.rinoiot.family.manager.FamilyDataChangeManager;
import com.smart.rinoiot.family.manager.HomeDataManager;
import com.smart.rinoiot.family.viewmodel.FamilyInfoViewModel;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("StringFormatMatches")
public class FamilyInfoActivity extends BaseActivity<ActivityFamilyInfoBinding, FamilyInfoViewModel> implements View.OnClickListener, OnApiDataChangeObserver {

    public static final int REQUEST_CODE_FROM_SELECT_ADDRESS = 0x1001;
    public static final int REQUEST_CODE_FROM_REMOVE_MEMBER = 0x1002;
    public static final int REQUEST_CODE_FROM_ROOM_MANAGER = 0x1003;
    public static final int REQUEST_CODE_FROM_ROOM_INFO = 0x1004;
    public static final int REQUEST_CODE_FROM_CREATE_ROOM = 0x1005;

    public AssetBean assetBean;
    public List<DeviceInfoBean> deviceList;
    private MemberAdapter memberAdapter;
    private RoomListAdapter roomListAdapter;
    private int memberRole;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_family_management);
    }

    @Override
    public void init() {
        binding.tvRemoveFamily.setVisibility(CacheDataManager.getInstance().getFamilyList().size() == 1 ? View.GONE : View.VISIBLE);

        initData();
        initView();
        roomListData();
        initLister();
        if (assetBean != null) {
            deviceList = CacheDataManager.getInstance().getAllDeviceList(assetBean.getId());
        }
        if (deviceList == null || deviceList.size() == 0) {
            mViewModel.getAllDeviceList(assetBean);
        } else {
            binding.familyDeviceView.tvSubTitle.setText(String.format(getString(R.string.rino_family_device_count), deviceList.size()));
        }

        mViewModel.getDeviceListLiveData().observe(this, netDeviceList -> {
            deviceList = netDeviceList;
            binding.familyDeviceView.tvSubTitle.setText(String.format(getString(R.string.rino_family_device_count), netDeviceList.size()));
        });
        mViewModel.getIsSort().observe(this, aBoolean -> {
            if (assetBean == null) return;
            mViewModel.getFamilyDetail(assetBean.getId());
        });
    }

    private void initLister() {
        FamilyDataChangeManager.getInstance().setOnApiDataChangeObserver(this);
        binding.tvRemoveFamily.setOnClickListener(this);
        binding.tvMember.setOnClickListener(this);
        binding.ivRoomManager.setOnClickListener(this);
        binding.ivAdd.setOnClickListener(this);
        binding.tvRoomManager.setOnClickListener(this);

        binding.familyNameView.getRoot().setOnClickListener(view -> showModifyFamilyNameDialog());


        memberAdapter.setOnItemClickListener((adapter, view, position) -> {
            MemberBean memberBean = (MemberBean) adapter.getData().get(position);
            startActivityForResult(new Intent(FamilyInfoActivity.this, MemberInfoActivity.class)
                            .putExtra(Constant.Member_Data, memberBean)
                            .putExtra(Constant.MEMBER_ROLE, memberRole),
                    REQUEST_CODE_FROM_REMOVE_MEMBER);
        });

        mViewModel.getDetailLiveData().observe(this, assetBean -> {
            if (assetBean == null) return;
            this.assetBean = assetBean;
            initRoomData();
            binding.familyNameView.tvSubTitle.setText(assetBean.getName());
            binding.familyLocationView.tvSubTitle.setText(assetBean.getAddress());
            if (assetBean.getMembers() == null) return;
            HomeDataManager.getInstance().setAssetBean(assetBean);
            peniedEmpty(assetBean);
            memberAdapter.setNewInstance(assetBean.getMembers());
            for (MemberBean member : assetBean.getMembers()) {
                UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(FamilyInfoActivity.this);
                if (userInfo != null && TextUtils.equals(member.getUserId(), userInfo.id)) {
                    HomeDataManager.getInstance().setRole(member.getMemberRole());
                    binding.tvRemoveFamily.setText(getString(member.getMemberRole() == 1 ? R.string.rino_family_remove_family : R.string.rino_family_leave_family));
                }
            }
        });

        binding.familyLocationView.getRoot().setOnClickListener(this);


        mViewModel.getIsSendInvite().observe(this, aBoolean -> {
            //暂时不用做什么
        });
    }

    /**
     * 传过来的数据
     */
    private void initData() {
        try {
            assetBean = HomeDataManager.getInstance().getAssetBean();
            mViewModel.getFamilyDetail(assetBean.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ui
     */
    @SuppressLint("StringFormatInvalid")
    private void initView() {
        try {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DpUtils.dip2px(320), DpUtils.dip2px(190));
            layoutParams.rightMargin = DpUtils.dip2px(20);
            int padding = DpUtils.dip2px(16);
            binding.familyNameView.getRoot().setPadding(padding, padding, padding, padding);
            binding.familyNameView.getRoot().setLayoutParams(layoutParams);
            binding.familyLocationView.getRoot().setLayoutParams(layoutParams);
            binding.familyNameView.tvTitle.setText(getString(R.string.rino_family_name));
            binding.familyLocationView.tvTitle.setText(getString(R.string.rino_family_location));


            binding.familyDeviceView.tvTitle.setText(getString(R.string.rino_family_device_counnt));
            binding.familyDeviceView.tvTitle.setTextColor(getResources().getColor(R.color.cen_connect_not_connect_color));
            binding.familyDeviceView.ivArrow.setVisibility(View.GONE);

            /**
             * 成员
             */
            binding.rvMember.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            memberAdapter = new MemberAdapter(FamilyInfoActivity.this);
            binding.rvMember.setAdapter(memberAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ActivityFamilyInfoBinding getBinding(LayoutInflater inflater) {
        return ActivityFamilyInfoBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvRemoveFamily) {
            showRemoveFamilyDialog();
        } else if (v.getId() == R.id.tvMember) {
            showAddMemberPopupView();
        } else if (v.getId() == R.id.familyLocationView) {
            ActivityUtils.startActivityForResult(FamilyInfoActivity.this, null, MapActivity.class, REQUEST_CODE_FROM_SELECT_ADDRESS);
        } else if (v.getId() == R.id.ivRoomManager) {
            binding.ivRoomManager.setVisibility(View.GONE);
            binding.tvRoomManager.setVisibility(View.VISIBLE);
            roomListAdapter.setEdit(true);
            binding.roomManagerRecyclerView.setLongPressDragEnabled(true); // 拖拽排序，默认关闭。
        } else if (v.getId() == R.id.tvRoomManager) {
            roomListAdapter.setEdit(false);
            binding.tvRoomManager.setVisibility(View.GONE);
            binding.ivRoomManager.setVisibility(View.VISIBLE);
            binding.roomManagerRecyclerView.setLongPressDragEnabled(false); // 拖拽排序，默认关闭。
            mViewModel.sortRoomList(roomListAdapter.getData());
        } else if (v.getId() == R.id.ivAdd) {
            startActivityForResult(new Intent(this, CreateRoomActivity.class), REQUEST_CODE_FROM_CREATE_ROOM);
        }
    }

    /**
     * 展示添加成员弹框
     */
    public void showAddMemberPopupView() {
        new XPopup.Builder(this).dismissOnTouchOutside(false).asCustom(new InviteMemberPopupView(this, "", "", account -> {
            String assetId = HomeDataManager.getInstance().getAssetBean().getId();
            mViewModel.inviteMember(account, assetId);
        })).show();
    }


    /**
     * 修改家庭名称
     */
    private void showModifyFamilyNameDialog() {
        new XPopup.Builder(this).dismissOnTouchOutside(false)
                .asCustom(new InviteMemberPopupView(this, getString(R.string.rino_family_name), getString(R.string.rino_family_input_name), account -> {
            binding.familyNameView.tvSubTitle.setText(account);
            mViewModel.updateFamilyName(account);
        })).show();
    }

    /**
     * 删除家庭
     */
    private void showRemoveFamilyDialog() {
        DialogUtil.showNormalMsg(this, getString(R.string.rino_family_confirm_delete_family), getString(R.string.rino_family_delete_family_desc), new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                if (assetBean == null) return;
                mViewModel.deleteFamily(assetBean.getId(), () -> {
                    ToastUtil.showMsg(getString(R.string.rino_family_delete_success));
                    setResult(RESULT_OK);
                    finishThis();
                });
            }
        });
    }

    @Override
    public void onApiDataChange() {
        setResult(RESULT_OK);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FamilyDataChangeManager.getInstance().removeOnApiDataChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE_FROM_SELECT_ADDRESS) {
            String address = data.getStringExtra("address");
            double lat = data.getDoubleExtra("latitude", 0);
            double lng = data.getDoubleExtra("longitude", 0);
            binding.familyLocationView.tvSubTitle.setText(address);
            mViewModel.updateFamilyAddress(address, lat, lng);
        } else if (requestCode == REQUEST_CODE_FROM_REMOVE_MEMBER) {//移除家庭成员
            initData();
        } else if (requestCode == REQUEST_CODE_FROM_ROOM_MANAGER) {
            initData();
        } else if (requestCode == REQUEST_CODE_FROM_CREATE_ROOM && assetBean != null) {
            mViewModel.getFamilyDetail(assetBean.getId());
        } else if (requestCode == REQUEST_CODE_FROM_ROOM_INFO && assetBean != null) {
            mViewModel.getFamilyDetail(assetBean.getId());
        }
    }

    /**
     * 根据权限及是否有数据展示空布局
     */
    private void peniedEmpty(AssetBean assetBean) {
        boolean permissionMemberRole = !FamilyPermissionManager.getInstance().getPermissionMemberRole(this, assetBean);
        TextUtil.setTvDrawableRight(this, permissionMemberRole ? R.drawable.icon_room_right : 0, binding.familyNameView.tvTitle);
        TextUtil.setTvDrawableRight(this, permissionMemberRole ? R.drawable.icon_room_right : 0, binding.familyLocationView.tvTitle);
        TextUtil.setTvDrawableRight(this, permissionMemberRole ? R.drawable.icon_add_device : 0, binding.tvMember);
        binding.tvMember.setEnabled(permissionMemberRole);
        binding.familyNameView.getRoot().setEnabled(permissionMemberRole);
        binding.familyLocationView.getRoot().setEnabled(permissionMemberRole);
        memberRole = FamilyPermissionManager.getInstance().getMemberRole(this, assetBean);
    }

    /**
     * 房间列表
     */
    private void roomListData() {
        roomListAdapter = new RoomListAdapter(this);
        roomListAdapter.setNewInstance(new ArrayList<>());
        binding.roomManagerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dragAndSlideListener(binding.roomManagerRecyclerView, roomListAdapter);
        binding.roomManagerRecyclerView.setAdapter(roomListAdapter);
        roomListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.ivDelete) {
                showDeleteRoomDialog((AssetBean) adapter.getData().get(position), position);
            }
        });
        roomListAdapter.setOnItemClickListener((adapter, view, position) ->
                startActivityForResult(new Intent(this, RoomInfoActivity.class)
                                .putExtra("position", position)
                                .putExtra("room_detail", (Serializable) adapter.getItem(position))
                                .putExtra(Constant.FAMILY_DATA, assetBean)
                        , REQUEST_CODE_FROM_ROOM_INFO));
        initRoomData();
    }
    /**
     * 数据
     */
    private void initRoomData() {
        if (assetBean==null) return;
        roomListAdapter.setNewInstance(assetBean.getChildrens());
    }

    /**
     * 监听侧滑和拽托
     */
    private void dragAndSlideListener(SwipeRecyclerView recyclerView, BaseQuickAdapter adapter) {
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setLongPressDragEnabled(false); // 拖拽排序，默认关闭。
            recyclerView.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
                SwipeMenuItem deleteItem = new SwipeMenuItem(this); // 各种文字和图标属性设置。
                deleteItem.setText(getString(R.string.rino_common_delete));
                deleteItem.setTextSize(16);
                deleteItem.setWidth(DpUtils.dip2px(60));
                deleteItem.setHeight(DpUtils.dip2px(30));
                deleteItem.setBackgroundColor(getResources().getColor(R.color.cen_information_unread_tip_color));
                deleteItem.setTextColor(getResources().getColor(R.color.cen_connect_step_selected_color));
                rightMenu.addMenuItem(deleteItem); // 在Item右侧添加一个菜单。
            });
            // 菜单点击监听。
            recyclerView.setOnItemMenuClickListener((menuBridge, position) -> {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                showDeleteRoomDialog((AssetBean) adapter.getData().get(position), position);
                menuBridge.closeMenu();
            });
            recyclerView.setOnItemMoveListener(new OnItemMoveListener() {
                @Override
                public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                    // 此方法在Item拖拽交换位置时被调用。
                    // 第一个参数是要交换为之的Item，第二个是目标位置的Item。

                    // 交换数据，并更新adapter。
                    int fromPosition = srcHolder.getAdapterPosition();
                    int toPosition = targetHolder.getAdapterPosition();
                    Collections.swap(adapter.getData(), fromPosition, toPosition);
                    adapter.notifyItemMoved(fromPosition, toPosition);
                    // 返回true，表示数据交换成功，ItemView可以交换位置。

                    mViewModel.sortRoomList(adapter.getData());
                    return true;
                }

                @Override
                public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {// 此方法在Item在侧滑删除时被调用。
                    // 从数据源移除该Item对应的数据，并刷新Adapter。
                    if (assetBean == null) return;
                    mViewModel.getFamilyDetail(assetBean.getId());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示删除房间的类
     */
    private void showDeleteRoomDialog(AssetBean o, int position) {
        DialogUtil.showNormalMsg(this, "", getString(R.string.rino_family_delete_room_tip), new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                mViewModel.removeRoom(o.getId(), () -> {
                    if (assetBean == null) return;
                    mViewModel.getFamilyDetail(assetBean.getId());
                });
            }
        });
    }
}
