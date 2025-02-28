package com.smart.rinoiot.center.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;

/**
 * @author author
 */
@SuppressLint("StringFormatInvalid")
public class CheckUpdateUtil {

    public static void showUpdatePop(Context context, UpdateBean updateBean) {
        boolean isCancel = updateBean.getUpgradeType() != null && updateBean.getUpgradeType() != 3;
        new XPopup.Builder(context).dismissOnBackPressed(isCancel).dismissOnTouchOutside(isCancel)
                .asCustom(new CustomUpdatePop(context, updateBean)).show();
    }

    public static boolean compareIgnoreVersion(UpdateBean updateBean) {
        //获取本地的版本号
        String localVersion;
        try {
            localVersion = BaseApplication.getApplication().getPackageManager().
                    getPackageInfo(BaseApplication.getApplication().getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            localVersion = SharedPreferenceUtil.getInstance().get(Constant.IGNORE_UPDATE, "");
        }
        String onlineVersion = updateBean.getVersion();
        //相等返回0，前者大返回值大于0，前者小返回值小于0
        return !TextUtils.isEmpty(onlineVersion) && localVersion.compareTo(onlineVersion) < 0;
    }

    public static String getLocalVersion() {
        String localVersion;
        try {
            localVersion = BaseApplication.getApplication().getPackageManager().
                    getPackageInfo(BaseApplication.getApplication().getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            localVersion = SharedPreferenceUtil.getInstance().get(Constant.IGNORE_UPDATE, "");
        }
        return localVersion;
    }
}
