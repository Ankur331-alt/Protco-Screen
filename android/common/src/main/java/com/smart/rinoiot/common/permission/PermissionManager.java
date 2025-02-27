package com.smart.rinoiot.common.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.ble.BleScanConnectManager;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.permission.bean.PermissionListBean;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.NetworkUtils;
import com.smart.rinoiot.common.view.WithButtonBottomPopView;
import com.smart.rinoiot.core.ble.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;
import pub.devrel.easypermissions.helper.BaseSupportPermissionsHelper;

/**
 * @author tw
 * @time 2023/3/29 14:16
 * @description
 */
@RequiresApi(api = Build.VERSION_CODES.S)
public class PermissionManager {
    public final static int FROM_AGREE_PERMISSION = 0x1001;
    public final static int FROM_AGREE_PERMISSION_CAMERA = 0x1005;
    public final static int FROM_ENABLE_BLUETOOTH = 0x1002;

    public final static int FROM_ANDROID_HEIGHT_FILE_PERMISSION = 0x1008;

    public final static int FROM_SETTING_PAGE_PERMISSION = 0x1009;
    /**
     * 蓝牙权限
     */
    public static final String[] permissionListLow = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};

    public static final String[] permissionListHigh = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            //兼容android 12手机必须添加的权限，否则关闭蓝牙崩溃+搜不到设备,旧蓝牙协议api最大支持sdk30，超过30必须增加新的蓝牙协议
            Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN};
    /**
     * 获取定位权限
     */
    public static String[] permissionListLocation = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 获取存储、相机权限
     */
    public static String[] permissionListUpdateAvatar = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    /**
     * 蓝牙权限提示框展示数据标识
     */
    public static final String BLE_SYSTEM_PERMISSION = "ble_system_permission";

    /**
     * 申请定位权限
     */
    public static final String[] permissionListDeniedLocation = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    /**
     * 获取媒体文件权限
     */
    public static String[] permissionListDeniedMediaFiles = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    /**
     * 相机权限
     */
    public static final String[] permissionListDeniedCamera = {Manifest.permission.CAMERA};

    /**
     * 蓝牙高版本权限设置
     */
    public static final String[] permissionListDeniedBleHeight = {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN};

    /**
     * 蓝牙高版本权限设置
     */
    public static final String[] permissionListDeniedBleLow = new String[]{"ble_system"};


    static {
        initPermissionList();
    }

    /**
     * 根据高低版本设置对应的权限列表
     */
    private static void initPermissionList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionListUpdateAvatar = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
            permissionListLocation = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_MEDIA_IMAGES};
            permissionListDeniedMediaFiles = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        }
    }

    /**
     * 自定义获取权限提示框，通过反射实现
     */
    @SuppressLint("RestrictedApi")
    public static void customRequestPermissions(Activity activity, int permissionCode, String[] permissionList) {
        String[] tempList = CompatibleHigherVersions(activity, permissionList);
        if (tempList == null || tempList.length == 0) return;
        boolean isEmptyPermission = setPermissionListData(activity, permissionCode, tempList);
        if (isEmptyPermission) {
            dismissCustomPermissionDialog();
            return;
        }

        String msgContent = activity.getString(R.string.rino_common_permission_rationale);
        String positiveStr = activity.getString(R.string.rino_common_confirm);
        String negativeStr = activity.getString(R.string.rino_common_cancel);
        PermissionRequest request = new PermissionRequest.Builder(activity, permissionCode, tempList).setRationale(msgContent).setPositiveButtonText(positiveStr).setNegativeButtonText(negativeStr).build();
        try {
            Class clazz = request.getClass();
            Field field = clazz.getDeclaredField("mHelper");
            field.setAccessible(true);
            BaseSupportPermissionsHelper<AppCompatActivity> permissionsHelper = (BaseSupportPermissionsHelper<AppCompatActivity>) field.get(request);
            field.set(request, new PermissionsHelperProxy(permissionsHelper));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //必须用第二种方法来实现权限请求，用第一种请求会出现在第一次安装应用授权时，会直接走directRequestPermissions()
//            EasyPermissions.requestPermissions(request);
            request.getHelper().showRequestPermissionRationale(request.getRationale(), request.getPositiveButtonText(), request.getNegativeButtonText(), request.getTheme(), request.getRequestCode(), request.getPerms());
        }
    }

    /**
     * 权限列表数据转化
     */
    public static List<PermissionListBean> permissionListBeans = new ArrayList<>();

    public static List<PermissionListBean> getPermissionListBeans() {
        return permissionListBeans;
    }

    public static boolean setPermissionListData(Context context, int permissionCode, String[] permissionList) {
        if (permissionListBeans != null && !permissionListBeans.isEmpty())
            permissionListBeans.clear();
        List<String> tempPermissionList = getPermissionListByDeniedList(context, permissionList);

        if (tempPermissionList != null && !tempPermissionList.isEmpty() && (tempPermissionList.contains(BLE_SYSTEM_PERMISSION)
//                        || tempPermissionList.contains(Manifest.permission.BLUETOOTH)
//                        && tempPermissionList.contains(Manifest.permission.BLUETOOTH_ADMIN)
        )) {//蓝牙系统权限
            systemBlePermissionData(context);
        } else if (tempPermissionList != null && !tempPermissionList.isEmpty() && filePermissionDeal(tempPermissionList) && tempPermissionList.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {//位置权限\媒体文件
            getPermissionTypeByPermissionBean(context, R.drawable.icon_location_permission, R.string.rino_common_access_location, R.string.rino_common_access_location_tips, false, permissionListDeniedLocation);

            getPermissionTypeByPermissionBean(context, R.drawable.icon_media_permission, R.string.rino_common_media_files, R.string.rino_common_media_files_tips, false, permissionListDeniedMediaFiles);

        } else if (tempPermissionList != null && !tempPermissionList.isEmpty() && tempPermissionList.contains(Manifest.permission.ACCESS_COARSE_LOCATION) && tempPermissionList.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {//位置权限
            getPermissionTypeByPermissionBean(context, R.drawable.icon_location_permission, R.string.rino_common_access_location, R.string.rino_common_access_location_tips, false, tempPermissionList.toArray(new String[tempPermissionList.size()]));

        } else if (tempPermissionList != null && !tempPermissionList.isEmpty() && tempPermissionList.contains(Manifest.permission.CAMERA) && filePermissionDeal(tempPermissionList)) {//相机\媒体文件
            getPermissionTypeByPermissionBean(context, R.drawable.icon_camera_permission, R.string.rino_common_camera, R.string.rino_common_camera_tips, false, permissionListDeniedCamera);

            getPermissionTypeByPermissionBean(context, R.drawable.icon_media_permission, R.string.rino_common_media_files, R.string.rino_common_media_files_tips, false, permissionListDeniedMediaFiles);

        } else if (tempPermissionList != null && !tempPermissionList.isEmpty() && filePermissionDeal(tempPermissionList)) {//媒体文件

            getPermissionTypeByPermissionBean(context, R.drawable.icon_media_permission, R.string.rino_common_media_files, R.string.rino_common_media_files_tips, false, tempPermissionList.toArray(new String[tempPermissionList.size()]));

        } else if (tempPermissionList != null && !tempPermissionList.isEmpty() && tempPermissionList.contains(Manifest.permission.CAMERA)) {//相机
            getPermissionTypeByPermissionBean(context, R.drawable.icon_camera_permission, R.string.rino_common_camera, R.string.rino_common_camera_tips, false, permissionListDeniedCamera);

        }
        return tempPermissionList == null | tempPermissionList.isEmpty();
    }

    /**
     * 用户拒绝权限是，提示用户是否去设置页面打开
     */
    public static int denyPermissionDialog(AppCompatActivity activity, List<String> perms) {
        int status = PermissionManager.heightAndroidSystemFiles(activity, perms);
        if (status == 2 || status == 4) return status;
        String permissionTips = getPermissionTipsByPermissionList(activity, perms);
        if (TextUtils.isEmpty(permissionTips)) {
            permissionTips = activity.getString(R.string.rino_common_open_setting_permission);
        }
        DialogUtil.showNormalMsg(activity, activity.getString(R.string.rino_common_alert_title), String.format(activity.getString(R.string.rino_common_permission_denied_desc), permissionTips), activity.getString(R.string.rino_common_cancel), activity.getString(R.string.rino_nfc_go_to_settings), new DialogOnListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onConfirm() {
                SystemPermissionPageManager.goToSetting(activity);
            }
        });
        return status;
    }

    /**
     * 自定义直接拉起系统权限
     */
    @SuppressLint("RestrictedApi")
    public static void customDirectRequestPermissions(Object object, int requestCode, String... permissionList) {
        PermissionRequest request = null;
        if (object == null) {
            return;
        }
        if (object instanceof Activity) {
            request = new PermissionRequest.Builder((Activity) object, requestCode, permissionList).build();
        } else {
            request = new PermissionRequest.Builder((Fragment) object, requestCode, permissionList).build();
        }
        request.getHelper().directRequestPermissions(requestCode, permissionList);

    }

    /**
     * 根据不同权限协议，返回对应权限文案提示
     */

    public static String getPermissionTipsByPermissionList(Context context, List<String> permissionList) {
        String permissionTips = "";
        if (permissionList.contains(Manifest.permission.ACCESS_COARSE_LOCATION) && permissionList.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {//定位权限
            permissionTips = context.getString(R.string.rino_common_permission_location);
        } else if (permissionList.contains(Manifest.permission.CAMERA)) {//相机权限
            permissionTips = context.getString(R.string.rino_common_permission_camera);
        } else if (filePermissionDeal(permissionList)) {//读写
            permissionTips = context.getString(R.string.rino_common_permission_storage);
        }
        return permissionTips;
    }

    /**
     * 0：默认；1、nfc页面用户不同意权限时，需要关闭nfc页面；2、nfc页面内用户同意权限时不关闭
     */
    public static int nFCPermissionType;

    public static void setNFCPermissionType(int nFCPermissionType) {
        PermissionManager.nFCPermissionType = nFCPermissionType;
    }

    /**
     * 自定义权限提示框
     */
    public static void customPermissionDialog(Context context, int offsetY, PermissionBottomPopView.OnSelectListener onSelectListener, String[] permissionList) {
        String[] tempList = CompatibleHigherVersions(context, permissionList);
        if (tempList == null || tempList.length == 0) {
            return;
        }
        String byPermissionList = getPermissionDialogTitleByPermissionList(context, tempList);
        boolean isSystemBle = TextUtils.equals(byPermissionList, context.getString(R.string.rino_nfc_turn_on_bluetooth));
        dismissCustomPermissionDialog();
        if (isSystemBle && setPermissionListData(context, 0, tempList)) {
            return;
        }
        PermissionBottomPopView permissionBottomPopView = new PermissionBottomPopView(context);
        permissionBottomPopView.setPermissionData(PermissionManager.getPermissionListBeans());
        permissionBottomPopView.setPermissionTitle(byPermissionList);
        permissionBottomPopView.setOnSelectListener(onSelectListener);
        permissionBottomPopView.setType(isSystemBle ? 0 : 1);
        permissionBottomPopView.setNFCPermission(PermissionManager.nFCPermissionType);
        new XPopup.Builder(context).hasStatusBarShadow(false)
                //.dismissOnBackPressed(false)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .autoOpenSoftInput(false).offsetY(-DpUtils.dip2px(offsetY)).maxHeight((int) (DpUtils.getScreenHeight() * 0.52)).asCustom(permissionBottomPopView).show();
        PermissionManager.setPopView(permissionBottomPopView);
    }

    /**
     * 根据不同权限协议，返回对应权限提示框标题
     */

    public static String getPermissionDialogTitleByPermissionList(Context context, String[] permissionList) {
//        List<String> tempPermissionList = getPermissionListByDeniedList(context, permissionList);
        List<String> tempPermissionList = new ArrayList<>();
        if (permissionList != null) {
            Collections.addAll(tempPermissionList, permissionList);
        }
        String permissionTips = context.getString(R.string.rino_nfc_turn_on_bluetooth);
        if (tempPermissionList != null && !tempPermissionList.isEmpty()) {
            if (!tempPermissionList.contains(BLE_SYSTEM_PERMISSION)) {
                permissionTips = context.getString(R.string.rino_common_permission_title);
            }
        }
        return permissionTips;
    }

    /**
     * 根据当前权限列表，判断app哪些没有权限，将没有权限添加到集合种返回
     */
    private static List<String> getPermissionListByDeniedList(Object object, String[] permissionList) {
        List<String> deniedList = new ArrayList<>();
        String[] tempList = CompatibleHigherVersions((Context) object, permissionList);
        if (tempList == null || tempList.length == 0) return deniedList;
        if (permissionList != null && object != null) {
            for (String strPermission : tempList) {
                if (TextUtils.equals(BLE_SYSTEM_PERMISSION, strPermission)) {
                    deniedList.add(strPermission);
                    continue;
                }
                if (Utils.isPermission((Context) object, strPermission)) {
                    //必须用这个来判断当前权限是否授权，
                    // 不能用EasyPermissions.somePermissionDenied(),因为第一次安装应用这个值都是false
                    continue;
                }
                deniedList.add(strPermission);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Collections.addAll(deniedList, permissionListDeniedMediaFiles);
        }

        if (deniedList == null || deniedList.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !isBluetoothOpen((Context) object)) {
                deniedList.add(BLE_SYSTEM_PERMISSION);
            }
        }
        return deniedList;
    }


    /**
     * 判断蓝牙系统权限  根据当前权限列表，判断app哪些没有权限，将没有权限添加到集合种返回
     */
    public static List<String> getPermissionListBleByDeniedList(Object object, String[] permissionList) {
        List<String> deniedList = new ArrayList<>();
        String[] tempList = CompatibleHigherVersions((Context) object, permissionList);
        if (tempList == null || tempList.length == 0) return deniedList;
        if (permissionList != null && object != null) {
            for (String strPermission : tempList) {
                if (TextUtils.equals(BLE_SYSTEM_PERMISSION, strPermission)) {
                    deniedList.add(strPermission);
                    continue;
                }
                if (Utils.isPermission((Context) object, strPermission)) {
                    //必须用这个来判断当前权限是否授权，
                    // 不能用EasyPermissions.somePermissionDenied(),因为第一次安装应用这个值都是false
                    continue;
                }
                deniedList.add(strPermission);
            }
        }
//        if (deniedList == null || deniedList.isEmpty()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !isBluetoothOpen()) {
//                deniedList.add(BLE_SYSTEM_PERMISSION);
//            }
//        }
        return deniedList;
    }

    /**
     * 根据权限分类，返回不同的权限实体类
     */
    public static void getPermissionTypeByPermissionBean(Context context, int ivResId, int titleResId, int descResId, boolean openStatus, String[] permissionList) {
        PermissionListBean permissionListBean = new PermissionListBean();
        permissionListBean.setPermissionResId(ivResId);
        permissionListBean.setPermissionTitle(context.getString(titleResId));
        if (descResId != 0) {
            permissionListBean.setPermissionDesc(context.getString(descResId));
        }
        boolean isNearby = false;
        PermissionListBean temp = null;
        for (PermissionListBean item : permissionListBeans) {
            if (TextUtils.equals(item.getPermissionTitle(), context.getString(R.string.rino_common_ble_nearby_permission))) {
                isNearby = true;
                temp = item;
                break;
            }
        }
        permissionListBean.setBleSystemStatus(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (isNearby && temp != null && !temp.isPermissionOpenStatus()));
        permissionListBean.setPermissionOpenStatus(openStatus);
        permissionListBean.setRequestCode(FROM_AGREE_PERMISSION);
        permissionListBean.setPermissionList(permissionList);
        permissionListBeans.add(permissionListBean);
    }


    /**
     * 系统蓝牙提示框及文案描述特殊处理
     */
    public static void systemBlePermissionData(Context context) {
        String[] permissionSystemList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionSystemList = PermissionManager.permissionListHigh;
        } else {
            permissionSystemList = PermissionManager.permissionListLow;
        }
        String[] tempList = CompatibleHigherVersions(context, permissionSystemList);
        if (tempList == null || tempList.length == 0) return;
        List<String> deniedList = getPermissionListBleByDeniedList(context, permissionSystemList);
        boolean isHeightFlag = deniedList == null || deniedList.isEmpty();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //附近蓝牙设备权限
            getPermissionTypeByPermissionBean(context, R.drawable.icon_permission_nearby_bluetooth, R.string.rino_common_ble_nearby_permission, 0, isHeightFlag, permissionListDeniedBleHeight);
        } else {
            getPermissionTypeByPermissionBean(context, R.drawable.icon_permission_nearby_bluetooth, R.string.rino_common_ble_permission, 0, true, permissionListDeniedBleLow);
        }
        getPermissionTypeByPermissionBean(context, R.drawable.icon_permission_system_bluetooth, R.string.rino_common_open_system_ble_permission, 0, !isHeightFlag || isHeightFlag && isBluetoothOpen(context), permissionListDeniedBleLow);
    }


    /**
     * 存入自定义提示框
     */
    private static PermissionBottomPopView popView;

    public static PermissionBottomPopView getPopView() {
        return popView;
    }

    public static void setPopView(PermissionBottomPopView permissionBottomPopView) {
        popView = permissionBottomPopView;
    }

    /**
     * 关闭自定义权限提示框
     */
    public static void dismissCustomPermissionDialog() {
        PermissionBottomPopView permissionBottomPopView = PermissionManager.getPopView();
        if (permissionBottomPopView != null && permissionBottomPopView.isShow()) {
            permissionBottomPopView.setNFCPermission(nFCPermissionType);
            permissionBottomPopView.dismiss();
            setPopView(null);
        }
    }

    /**
     * 系统蓝牙权限是否打开
     */
    public static boolean isBluetoothOpen(Context context) {
        return NetworkUtils.isBluetoothOpen(context);
    }

    /**
     * 开打系统蓝牙，根据版本走不同逻辑
     */
    @SuppressLint("MissingPermission")
    public static void blePermissionBuild(Activity activity, PermissionListBean item) {
        if (item != null) {
            List<String> perms = new ArrayList<>();
            Collections.addAll(perms, item.getPermissionList());
            if (perms != null && perms.contains(Manifest.permission.BLUETOOTH_CONNECT)
                    && perms.contains(Manifest.permission.BLUETOOTH_ADVERTISE)
                    && perms.contains(Manifest.permission.BLUETOOTH_SCAN)) {
                SystemPermissionPageManager.goToSetting(activity);
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, PermissionManager.FROM_ENABLE_BLUETOOTH);
        } else {
            WithButtonBottomPopView popupView = new WithButtonBottomPopView(activity);
            popupView.setTitleAndContent("", activity.getString(R.string.rino_device_enable_bluetooth));
            popupView.setOnConfirmListener(new WithButtonBottomPopView.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    BluetoothAdapter.getDefaultAdapter().enable();
                    BleScanConnectManager.getInstance().startScan();
                }

                @Override
                public void onCancel() {

                }
            });

            new XPopup.Builder(activity).isDarkTheme(false).hasShadowBg(true).isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .asCustom(popupView).show();
        }
    }

    /**
     * android 系统大于11时，文件权限特殊处理
     *
     * @return status  0:安卓系统小于11；1：安卓系统大于等于11且已授权；2、安卓系统大于等于11且未授权;
     * 3、安卓系统大于等于11且不包含读写权限,且未权限 4、安卓系统大于等于11且不包含读写权限，且该权限以授权
     */

    public static int heightAndroidSystemFiles(Object object, List<String> perms) {
        int status = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {// android 11  且 不是已经被拒绝
            if (perms != null && !perms.isEmpty() && filePermissionDeal(perms)) {
                // 先判断有没有权限
                status = 1;
                if (!Environment.isExternalStorageManager()) {
                    status = 2;
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    if (object == null) return status;
                    if (object instanceof Activity) {
                        intent.setData(Uri.parse("package:" + ((Activity) object).getPackageName()));
                        ((Activity) object).startActivityForResult(intent, FROM_ANDROID_HEIGHT_FILE_PERMISSION);

                    } else if (object instanceof Fragment) {
                        intent.setData(Uri.parse("package:" + ((Fragment) object).getActivity().getPackageName()));
                        ((Fragment) object).startActivityForResult(intent, FROM_ANDROID_HEIGHT_FILE_PERMISSION);
                    }
                }

            } else if (perms != null && !perms.isEmpty()) {
                boolean isOpen = true;
                for (String per : perms) {
                    if (!Utils.isPermission((Context) object, per)) {
                        isOpen = false;
                        break;
                    }
                }
                if (isOpen) {//高版本时，权限已过
                    status = 4;
                } else {
                    status = 3;
                }
            }
        }
        return status;
    }

    /**
     * 判断是否未高版本，如果时，且权限有读写权限，需要特殊处理申请权限
     */
    public static String[] android11FilePermissionList(Context context, String[] permissionList) {
        String[] formatPermissionList = null;
        List<String> tempPermissionList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        Collections.addAll(tempList, permissionList);
        //android 11  且 已授权，需要移除读写权限
        if (tempList != null && !tempList.isEmpty()) {
            for (String str : permissionList) {
                if (Environment.isExternalStorageManager() && Utils.isPermission(context, str)
                        && filePermissionDeal(tempList)) {//高版本只过滤调读写文件权限
                    continue;
                }
                tempPermissionList.add(str);
            }
        }
        if (tempPermissionList != null && !tempPermissionList.isEmpty()) {
            formatPermissionList = new String[tempPermissionList.size()];
            for (int i = 0; i < tempPermissionList.size(); i++) {
                formatPermissionList[i] = tempPermissionList.get(i);
            }
        }
        return formatPermissionList;
    }


    /**
     * 兼容高版本，检测那些权限没有
     */
    public static String[] CompatibleHigherVersions(Context context, String[] permissionList) {
        String[] tempList = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            tempList = android11FilePermissionList(context, permissionList);
            if (tempList == null || tempList.length == 0) {
                dismissCustomPermissionDialog();
                return tempList;
            }
        }
        if (tempList == null || tempList.length == 0) {
            tempList = permissionList;
        }
        return tempList;
    }

    /**
     * 针对文件权限问题，高低版本兼容是否授权成功
     */
    public static boolean getSpecialFilePermissionHighAndLow(Context context, String[] tempPermission) {
        boolean isPermissionSuccess = false;
        if (tempPermission == null || tempPermission.length == 0) {
            return true;
        }
        if (EasyPermissions.hasPermissions(context, tempPermission)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                isPermissionSuccess = true;
            }
        }
        return isPermissionSuccess;
    }

    /**
     * 设置文件提示框文案及权限时，根据高低版本，同意返回
     */
    private static boolean filePermissionDeal(List<String> tempPermissionList) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                tempPermissionList.contains(Manifest.permission.READ_MEDIA_IMAGES)
                || tempPermissionList.contains(Manifest.permission.READ_EXTERNAL_STORAGE)
                && tempPermissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}