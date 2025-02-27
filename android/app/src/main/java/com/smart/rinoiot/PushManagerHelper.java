package com.smart.rinoiot;

import android.content.Context;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;
import com.smart.rinoiot.upush.helper.PushHelper;

/**
 * 友盟推送
 */
public class PushManagerHelper {
    private static PushManagerHelper instance;

    public static PushManagerHelper getInstance() {
        if (instance == null) {
            instance = new PushManagerHelper();
        }

        return instance;
    }

    /**
     * 初始化友盟SDK
     */
    public void initUmengSDK(Context context) {
        //日志开关
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        boolean isMainProcess = UMUtils.isMainProgress(context);
        //预初始化
        PushHelper.getInstance().preInit(context);
        //是否同意隐私政策
//        boolean agreed = MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
//        if (!agreed) {
//            return;
//        }
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            new Thread(() -> {
                PushHelper.getInstance().init(context);
            }).start();
        }
    }


}
