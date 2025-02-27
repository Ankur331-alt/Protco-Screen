package com.smart.rinoiot.common.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.listener.DialogListener;
import com.smart.rinoiot.common.view.CenterListSelectedPopupView;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.view.RinoAttachListPopupView;

/**
 * @author author
 */
public class DialogUtil {
    public DialogUtil() {
    }

    public static void showNormalMsg(AppCompatActivity activity, String title, String msg, DialogOnListener dialogOnListener) {
        XPopup.setPrimaryColor(R.color.cen_main_theme_bg);
        (new XPopup.Builder(activity)).borderRadius(20.0F).hasStatusBarShadow(false).isDestroyOnDismiss(true).asConfirm(title, msg, activity.getString(R.string.rino_common_cancel), activity.getString(R.string.rino_common_confirm), () -> {
            if (dialogOnListener != null) {
                dialogOnListener.onConfirm();
            }

        }, () -> {
            if (dialogOnListener != null) {
                dialogOnListener.onCancel();
            }

        }, false).show();
    }

    public static void showNormalMsg(Activity activity, String title, String msg, DialogOnListener dialogOnListener) {
        (new XPopup.Builder(activity)).borderRadius(20.0F).hasStatusBarShadow(false).isDestroyOnDismiss(true).asConfirm(title, msg, activity.getString(R.string.rino_common_cancel), activity.getString(R.string.rino_common_confirm), () -> {
            if (dialogOnListener != null) {
                dialogOnListener.onConfirm();
            }

        }, () -> {
            if (dialogOnListener != null) {
                dialogOnListener.onCancel();
            }

        }, false).show();
    }

    public static ConfirmPopupView showNormalMsg(Context activity, String title, String msg, String cancelText, String confirmText, DialogOnListener dialogOnListener) {
        XPopup.setPrimaryColor(R.color.cen_main_theme_bg);
        ConfirmPopupView confirmPopupView = (new XPopup.Builder(activity)).hasStatusBarShadow(false).borderRadius(20.0F).isDestroyOnDismiss(false)
                .asConfirm(title, msg, cancelText, confirmText, () -> {
                    if (dialogOnListener != null) {
                        dialogOnListener.onConfirm();
                    }

                }, () -> {
                    if (dialogOnListener != null) {
                        dialogOnListener.onCancel();
                    }

                }, false);
        confirmPopupView.show();
        return confirmPopupView;
    }


    /**
     * 权限管理提示框
     */
    public static void showNormalPermissionMsg(AppCompatActivity activity) {
        XPopup.setPrimaryColor(R.color.cen_main_theme_bg);
        (new XPopup.Builder(activity)).hasStatusBarShadow(false).borderRadius(20.0F).isDestroyOnDismiss(true)
                .asConfirm(activity.getString(R.string.rino_family_permission_denied_title), activity.getString(R.string.rino_family_permission_denied_tips),
                        "", activity.getString(R.string.rino_device_not_support_network_config_cancel), () -> {

                        }, () -> {

                        }, true).show();
    }

    /**
     * 权限管理提示框
     */
    public static void showNormalLogoutMsg(AppCompatActivity activity, String msg) {
        XPopup.setPrimaryColor(R.color.cen_main_theme_bg);
        (new XPopup.Builder(activity)).hasStatusBarShadow(false).borderRadius(20.0F).isDestroyOnDismiss(true)
                .asConfirm("", msg,
                        "", activity.getString(R.string.rino_common_confirm), () -> {

                        }, () -> {

                        }, true).show();
    }

    public static void showNormalRecipeMsg(AppCompatActivity activity, String msg, String confirmText, DialogOnListener dialogOnListener) {
        (new XPopup.Builder(activity)).hasStatusBarShadow(false).isDestroyOnDismiss(true).asConfirm("", msg, "", confirmText, () -> {
            if (dialogOnListener != null) {
                dialogOnListener.onConfirm();
            }

        }, () -> {
            if (dialogOnListener != null) {
                dialogOnListener.onCancel();
            }

        }, true).show();
    }


    public static void showInputDialog(Activity activity, String title, String hint, String input, OnInputConfirmListener onConfirmListener, OnCancelListener onCancelListener) {
        new XPopup.Builder(activity)
                .hasStatusBarShadow(false)
                ///.dismissOnBackPressed(false)
                //对于只使用一次的弹窗，推荐设置这个
                .isDestroyOnDismiss(true)
                .autoOpenSoftInput(true)
                //是否让弹窗内的EditText自动获取焦点，默认是true
                /// .autoFocusEditText(false)
                //是否移动到软键盘上面，默认为true
                ///.moveUpToKeyboard(false)
                .asInputConfirm(title, null, input,
                        hint, onConfirmListener, onCancelListener, R.layout.dialog_msg).show();
    }

    public static void showBottomSheet(Activity activity, String title, String[] list, OnSelectListener onSelectListener) {
        new XPopup.Builder(activity)
                .hasStatusBarShadow(false)
                ///.dismissOnBackPressed(false)
                //对于只使用一次的弹窗，推荐设置这个
                .isDestroyOnDismiss(true)
                .autoOpenSoftInput(false)
                .asBottomList(title, list, onSelectListener).show();
    }

    /**
     * 首页和家庭管理提示框
     */
    public static void showHomeAttachListPopupView(View attachView, String[] arrList, int[] iconRes, OnSelectListener onSelectListener) {
        RinoAttachListPopupView popupView = new RinoAttachListPopupView(attachView.getContext(), 0, 0)
                .setStringData(arrList, iconRes)
                .setContentGravity(Gravity.CENTER)
                .setOnSelectListener(onSelectListener);
        new XPopup.Builder(attachView.getContext())
                .atView(attachView)
                .hasShadowBg(true)
                .borderRadius(DpUtils.dip2px(8))
                .asCustom(popupView)
                .show();
    }

    /**
     * 修改群组名称
     */
    public static void showInputGroupNameDialog(Activity activity, String title, String hint, String input, OnInputConfirmListener onConfirmListener, OnCancelListener onCancelListener) {
        XPopup.setPrimaryColor(R.color.cen_main_theme_bg);
        new XPopup.Builder(activity)
                .hasStatusBarShadow(false)
                ///.dismissOnBackPressed(false)
                //对于只使用一次的弹窗，推荐设置这个
                .isDestroyOnDismiss(true)
                .autoOpenSoftInput(true)
                //是否让弹窗内的EditText自动获取焦点，默认是true
                /// .autoFocusEditText(false)
                //是否移动到软键盘上面，默认为true
                ///.moveUpToKeyboard(false)
                .asInputConfirm(title, null, input,
                        hint, onConfirmListener, onCancelListener, R.layout.dialog_input_group).show();
    }

    /**
     * 群组下设备没有设备列表
     */
    public static void showNormalSheet(Activity activity, String title, String[] list, OnSelectListener onSelectListener) {
        new XPopup.Builder(activity)
                .hasStatusBarShadow(false)
                ///.dismissOnBackPressed(false)
                //对于只使用一次的弹窗，推荐设置这个
                .isDestroyOnDismiss(true)
                .autoOpenSoftInput(false)
                .asCenterList(title, list, onSelectListener).show();
    }

    /**
     * 列表形式的提示框+图标
     */
    public static void showListSelectedDialog(Activity activity, String title, String[] list, int position, DialogListener dialogListener) {
        int[] icons = new int[2];
        icons[0] = R.drawable.ic_check;
        icons[1] = R.drawable.ic_uncheck;
        CenterListSelectedPopupView centerListSelectedPopupView = new CenterListSelectedPopupView(activity, 0, 0)
                .setStringData(title, list, icons)
                .setCheckedPosition(position)
                .setOnSelectListener(dialogListener);
        new XPopup.Builder(activity)
                .hasStatusBarShadow(false)
                ///.dismissOnBackPressed(false)
                //对于只使用一次的弹窗，推荐设置这个
                .isDestroyOnDismiss(true)
                .popupWidth(DpUtils.dip2px(600))
                .autoOpenSoftInput(false)
                .asCustom(centerListSelectedPopupView)
                .show();
    }
}

