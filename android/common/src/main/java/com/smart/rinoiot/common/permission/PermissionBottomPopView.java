package com.smart.rinoiot.common.permission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxj.xpopup.core.BottomPopupView;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.permission.bean.PermissionListBean;

import java.util.List;

/**
 * Description: 权限提示框
 */
@RequiresApi(api = Build.VERSION_CODES.S)
@SuppressLint({"SetTextI18n", "RestrictedApi"})
public class PermissionBottomPopView extends BottomPopupView implements View.OnClickListener {

    private AppCompatActivity activity;

    private List<PermissionListBean> permissionListBeans;

    private RecyclerView recyclerView;
    private PermissionAdapter permissionAdapter;
    private String strTitle;

    private int nFCPermissionType;//0：默认；1、nfc页面用户不同意权限时，需要关闭nfc页面；2、nfc页面内用户同意权限时不关闭

    private int type;//0:蓝牙权限；1、定位、媒体权限

    public PermissionBottomPopView(@NonNull Context context) {
        super(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom_permission;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.ivCancel).setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        if (TextUtils.equals(strTitle, getContext().getString(R.string.rino_nfc_turn_on_bluetooth))) {
            tvTitle.setGravity(Gravity.CENTER);
        } else {
            tvTitle.setGravity(Gravity.LEFT);
        }
        tvTitle.setText(strTitle);
        initRecyclerView();
    }

    public void setPermissionData(List<PermissionListBean> permissionListBeans) {
        this.permissionListBeans = permissionListBeans;
    }

    public void setPermissionTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public void setNFCPermission(int nFCPermissionType) {
        this.nFCPermissionType = nFCPermissionType;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 更新权限状态
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updatePermissionStatus(List<PermissionListBean> permissionListBeans) {
        if (recyclerView != null && permissionAdapter != null) {
            permissionAdapter.setNewInstance(permissionListBeans);
            permissionAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (DpUtils.getScreenHeight() * 0.42));
//        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        permissionAdapter = new PermissionAdapter(permissionListBeans);
        permissionAdapter.setPermissionType(type);
        recyclerView.setAdapter(permissionAdapter);
        setScrollData();
        permissionAdapter.setOnItemClickListener((adapter, view, position) -> openPermission(adapter, position));
        permissionAdapter.setOnItemChildClickListener((adapter, view, position) -> openPermission(adapter, position));

    }


    /**
     * 打开权限
     */
    private void openPermission(@NonNull BaseQuickAdapter<?, ?> adapter, int position) {
        PermissionListBean permissionListBean = (PermissionListBean) adapter.getData().get(position);
        if (permissionListBean != null && !permissionListBean.isPermissionOpenStatus()) {
            if (selectListener != null) {
                selectListener.onSelect(position, permissionListBean);
                return;
            }
            PermissionManager.customDirectRequestPermissions(activity, permissionListBean.getRequestCode(),
                    permissionListBean.getPermissionList());
//            dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivCancel) {//取消
            dismiss();
        }
    }

    private void setScrollData() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setFocusable(false);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        PermissionManager.setNFCPermissionType(0);
        if (nFCPermissionType == 1 && activity != null) activity.finish();
    }

    private OnSelectListener selectListener;

    public void setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public interface OnSelectListener {
        void onSelect(int position, PermissionListBean item);
    }
}