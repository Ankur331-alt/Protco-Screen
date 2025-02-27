package com.smart.rinoiot.family.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.FamilyPermissionManager;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.adapter.RoomListAdapter;
import com.smart.rinoiot.family.databinding.ActivityRoomManagerBinding;
import com.smart.rinoiot.family.manager.HomeDataManager;
import com.smart.rinoiot.family.viewmodel.RoomManagerViewModel;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author jiangtao
 * <p>
 * create-time: 2022/9/7
 */
public class RoomManagerActivity extends BaseActivity<ActivityRoomManagerBinding, RoomManagerViewModel> implements View.OnClickListener {

    public static final int REQUEST_CODE_FROM_CREATE_ROOM = 0x1001;
    public static final int REQUEST_CODE_FROM_ROOM_INFO = 0x1002;

    private AssetBean familyDetail;
    private RoomListAdapter adapter;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_family_room_management_title);
    }

    @Override
    @SuppressWarnings("all")
    public void init() {
        familyDetail = (AssetBean) getIntent().getSerializableExtra("family_detail");
        Type type = new TypeToken<List<DeviceInfoBean>>() {}.getType();
        List<DeviceInfoBean> deviceList = new Gson().fromJson(
                getIntent().getStringExtra("device_list"),
                type
        );
        HomeDataManager.getInstance().setAssetBean(familyDetail);
        familyDetail = mViewModel.addDeviceListToAsset(familyDetail, deviceList);
        initView();
        initData();
        initLister();
    }

    /**
     * ui
     */
    @SuppressWarnings("all")
    private void initView() {
        adapter = new RoomListAdapter(this);
        adapter.setNewInstance(new ArrayList<>());
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        dragAndSlideListener(binding.rv, adapter);
        binding.rv.setAdapter(adapter);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.ivDelete) {
                showDeleteRoomDialog((AssetBean) adapter.getData().get(position), position);
            }
        });
        adapter.setOnItemClickListener((adapter, view, position) ->
                startActivityForResult(new Intent(RoomManagerActivity.this, RoomInfoActivity.class)
                                .putExtra("position", position)
                                .putExtra("room_detail", (Serializable) adapter.getItem(position))
                                .putExtra(Constant.FAMILY_DATA, familyDetail)
                        , REQUEST_CODE_FROM_ROOM_INFO));
    }

    /**
     * 数据
     */
    private void initData() {
        adapter.setNewInstance(familyDetail.getChildrens());
    }

    /**
     * 监听
     */
    private void initLister() {
        binding.ivAdd.setOnClickListener(this);
        binding.ivRoomManager.setOnClickListener(this);
        binding.tvRoomManager.setOnClickListener(this);
        mViewModel.getFamilyDetailLiveData().observe(this, netFamilyDetail -> {
            familyDetail = sortRoom(netFamilyDetail);
            setResult(RESULT_OK, new Intent().putExtra("family_detail", familyDetail));
            initData();
        });
        mViewModel.getIsSort().observe(this, isSuccess -> mViewModel.getFamilyDetail(familyDetail.getId()));
    }

    /**
     * 展示删除房间的类
     */
    private void showDeleteRoomDialog(AssetBean o, int position) {
        DialogUtil.showNormalMsg(RoomManagerActivity.this, "", getString(R.string.rino_family_delete_room_tip), new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                mViewModel.removeRoom(o.getId(), () -> {
                    mViewModel.getFamilyDetail(familyDetail.getId());
                    // ToDo() post room deleted event.
                });
            }
        });
    }

    /**
     * 监听侧滑和拽托
     */
    @SuppressWarnings("all")
    private void dragAndSlideListener(SwipeRecyclerView recyclerView, BaseQuickAdapter adapter) {
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // 拖拽排序，默认关闭。
            recyclerView.setLongPressDragEnabled(false);
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
            binding.rv.setOnItemMoveListener(new OnItemMoveListener() {
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
                    mViewModel.getFamilyDetail(familyDetail.getId());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    private AssetBean sortRoom(AssetBean familyDetail) {
        if (familyDetail != null && familyDetail.getChildrens() != null && familyDetail.getChildrens().size() > 0) {
            Comparator<AssetBean> comparator = (o1, o2) -> {
                return o1.getSortNumber() - o2.getSortNumber(); // 排序按升序
            };

            Collections.sort(familyDetail.getChildrens(), comparator);
        }

        return familyDetail;
    }

    @Override
    public ActivityRoomManagerBinding getBinding(LayoutInflater inflater) {
        return ActivityRoomManagerBinding.inflate(inflater);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_FROM_CREATE_ROOM) {
            mViewModel.getFamilyDetail(familyDetail.getId());
        } else if (requestCode == REQUEST_CODE_FROM_ROOM_INFO) {
            mViewModel.getFamilyDetail(familyDetail.getId());
///            if (familyDetail != null) {
///                initData();
///                setResult(RESULT_OK, new Intent().putExtra("family_detail", familyDetail));
///            }
        }
    }


    @Override
    @SuppressWarnings("all")
    public void onClick(View view) {
        if (view.getId() == R.id.ivAdd) {
            boolean hasPermission = FamilyPermissionManager.getInstance()
                    .getPermissionMemberRole(this, null);
            if (!hasPermission) {
                DialogUtil.showNormalPermissionMsg(this);
                return;
            }
            Intent intent = new Intent(RoomManagerActivity.this, CreateRoomActivity.class);
            startActivityForResult(intent, REQUEST_CODE_FROM_CREATE_ROOM);
        } else if (view.getId() == R.id.ivRoomManager) {
            binding.ivRoomManager.setVisibility(View.GONE);
            binding.tvRoomManager.setVisibility(View.VISIBLE);
            adapter.setEdit(true);
            // 拖拽排序，默认关闭。
            binding.rv.setLongPressDragEnabled(true);
        } else if (view.getId() == R.id.tvRoomManager) {
            adapter.setEdit(false);
            binding.tvRoomManager.setVisibility(View.GONE);
            binding.ivRoomManager.setVisibility(View.VISIBLE);
            // 拖拽排序，默认关闭。
            binding.rv.setLongPressDragEnabled(false);
            mViewModel.sortRoomList(adapter.getData());
        }
    }
}
