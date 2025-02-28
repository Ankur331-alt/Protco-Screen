package com.smart.rinoiot.common.permission.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2023/3/30 10:53
 * @description 权限列表bean
 */
public class PermissionListBean implements Serializable {
    private String permissionTitle;//权限描述/访问权限名称
    private boolean permissionOpenStatus;//权限是否打开
    private int permissionResId;//权限对应的图标展示

    private int requestCode;//权限回调code

    private String[] permissionList;//权限列表

    private String permissionDesc;//权限描述(针对非蓝牙权限)

    private boolean bleSystemStatus;//附近蓝牙权限未授权时，系统蓝牙权限打开状态隐藏

    public boolean isBleSystemStatus() {
        return bleSystemStatus;
    }

    public void setBleSystemStatus(boolean bleSystemStatus) {
        this.bleSystemStatus = bleSystemStatus;
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    public String[] getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(String[] permissionList) {
        this.permissionList = permissionList;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getPermissionTitle() {
        return permissionTitle;
    }

    public void setPermissionTitle(String permissionTitle) {
        this.permissionTitle = permissionTitle;
    }

    public boolean isPermissionOpenStatus() {
        return permissionOpenStatus;
    }

    public void setPermissionOpenStatus(boolean permissionOpenStatus) {
        this.permissionOpenStatus = permissionOpenStatus;
    }

    public int getPermissionResId() {
        return permissionResId;
    }

    public void setPermissionResId(int permissionResId) {
        this.permissionResId = permissionResId;
    }
}
