package com.smart.rinoiot.center.update;

/**
 * * @ProjectName: XunfeiAndroid
 *
 * @Package: com.znkit.smart.manager
 * @ClassName: PermissionManager
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2021/3/29 3:12 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/3/29 3:12 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PermissionManager {
    private static PermissionManager instance;
    private PermissionListener permissionListener;

    public void setPermissionListener(PermissionListener permissionListener) {
        this.permissionListener = permissionListener;
    }

    public void remove() {
        permissionListener = null;
    }

    public PermissionListener getPermissionListener() {
        return permissionListener;
    }

    public static PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }

}
