package com.smart.rinoiot.common.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.listener.DialogOnListener;

import pub.devrel.easypermissions.EasyPermissions;

public class AcpUtils {
    public static final int REQUEST_CODE_SETTING = 0x39;

    public static void reqPermissions(Activity context, String msg, int requestCode, String... mPermissions) {
        if (hasPermission(context, mPermissions)) {
            return;
        }
        EasyPermissions.requestPermissions(context, msg, requestCode, mPermissions);
    }

    public static void reqPermissions(Fragment context, String msg, int requestCode, String... mPermissions) {
        EasyPermissions.requestPermissions(context, msg, requestCode, mPermissions);
    }

    public static boolean hasPermission(Context context, String... permissions) {
        return EasyPermissions.hasPermissions(context, permissions);
    }

    /**
     * 当权限被用户选择不再弹出时候再次提示
     *
     * @param
     * @return
     * @method showPermissionNeverTipDialog
     * @date: 2020/12/7 3:28 PM
     * @author: xf
     */
    public static void showPermissionNeverTipDialog(AppCompatActivity activity, String msg, String cancelStr, String confirmStr) {
        DialogUtil.showNormalMsg(activity, activity.getString(R.string.rino_common_alert_title), msg, cancelStr, confirmStr, new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {

            }
        });
    }

    /**
     * 跳转到设置界面
     */
    public static void startSetting(Activity mActivity) {
        if (MiuiOs.isMIUI()) {
            Intent intent = MiuiOs.getSettingIntent(mActivity);
            if (MiuiOs.isIntentAvailable(mActivity, intent)) {
                mActivity.startActivityForResult(intent, REQUEST_CODE_SETTING);
                return;
            }
        }
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + mActivity.getPackageName()));
            mActivity.startActivityForResult(intent, REQUEST_CODE_SETTING);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                mActivity.startActivityForResult(intent, REQUEST_CODE_SETTING);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }
}
