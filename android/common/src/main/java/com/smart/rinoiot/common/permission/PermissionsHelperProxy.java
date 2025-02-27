package com.smart.rinoiot.common.permission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.smart.rinoiot.common.utils.LgUtils;

import pub.devrel.easypermissions.helper.BaseSupportPermissionsHelper;

/**
 * @author tw
 * @time 2023/3/29 18:27
 * @description hook替换PermissionRequest的mHelper
 */
@RequiresApi(api = Build.VERSION_CODES.S)
@SuppressLint("RestrictedApi")
public class PermissionsHelperProxy<T> extends BaseSupportPermissionsHelper<T> {
    private BaseSupportPermissionsHelper<T> permissionsHelper;

    public PermissionsHelperProxy(T permissionsHelper) {
        super(permissionsHelper);
        this.permissionsHelper = (BaseSupportPermissionsHelper<T>) permissionsHelper;
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        return permissionsHelper.getSupportFragmentManager();
    }

    @Override
    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        permissionsHelper.directRequestPermissions(requestCode, perms);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return permissionsHelper.shouldShowRequestPermissionRationale(perm);
    }

    @Override
    public Context getContext() {
        return permissionsHelper.getContext();
    }

    @Override
    public void showRequestPermissionRationale(@NonNull String rationale, @NonNull String positiveButton,
                                               @NonNull String negativeButton, int theme, int requestCode,
                                               @NonNull String... perms) {
        PermissionBottomPopView permissionBottomPopView = PermissionManager.getPopView();
        if (permissionBottomPopView != null && permissionBottomPopView.isShow()) {
            permissionBottomPopView.updatePermissionStatus(PermissionManager.getPermissionListBeans());
            return;
        }
        LgUtils.w("customRequestPermissions333   permissionList=" + new Gson().toJson(perms)
                + "permissionBottomPopView =" + permissionBottomPopView
                + "  PermissionManager.getPermissionListBeans()=" + new Gson().toJson(PermissionManager.getPermissionListBeans()));
        PermissionManager.customPermissionDialog(getContext(), 0,null, perms);
    }


}
