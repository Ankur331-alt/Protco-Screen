package com.smart.rinoiot.upush.helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.smart.rinoiot.upush.bean.PushConfig;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * PushSDK集成帮助类
 */
@SuppressLint("WrongConstant")
public class PushHelper {
    private static final String TAG = "PushHelper";
    private static PushHelper instance;
    private PushConfig config;

    public static PushHelper getInstance() {
        if (instance == null) {
            instance = new PushHelper();
        }

        return instance;
    }

    /**
     * 预初始化
     */
    public void preInit(Context context) {
//        setMetaData(context);
        UMConfigure.preInit(context, PushConstants.APP_KEY, PushConstants.CHANNEL);
    }

    /**
     * 初始化
     */
    public void init(final Context context) {
        // 基础组件包提供的初始化函数，应用配置信息：http://message.umeng.com/list/apps
        // 参数一：上下文context；
        // 参数二：应用申请的Appkey；
        // 参数三：发布渠道名称；
        // 参数四：设备类型，UMConfigure.DEVICE_TYPE_PHONE：手机；UMConfigure.DEVICE_TYPE_BOX：盒子；默认为手机
        // 参数五：Push推送业务的secret，填写Umeng Message Secret对应信息
        UMConfigure.init(context, PushConstants.APP_KEY, PushConstants.CHANNEL,
                UMConfigure.DEVICE_TYPE_PHONE, "");

//        UMConfigure.init(context, PushConstants.APP_KEY, PushConstants.CHANNEL,
//                UMConfigure.DEVICE_TYPE_PHONE, PushConstants.MESSAGE_SECRET);
//        //获取推送实例
//        PushAgent pushAgent = PushAgent.getInstance(context);
//
//        //TODO:需修改为您app/src/main/AndroidManifest.xml中package值
//        pushAgent.setResourcePackageName(config == null ? "com.smart.rinoiot" : config.getPackageName());
//
//        //推送设置
//        pushSetting(context);
//
//        //注册推送服务，每次调用register方法都会回调该接口
//        pushAgent.register(new UPushRegisterCallback() {
//
//            @Override
//            public void onSuccess(String deviceToken) {
//                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
//                Log.i(TAG, "deviceToken --> " + deviceToken);
////                //获取deviceToken可通过接口：
////                PushAgent.getInstance(context).getRegistrationId();
////                //可设置别名，推送时使用别名推送
////                String alias = "123456";
////                String type = "aa";
////                PushAgent.getInstance(context).setAlias(alias, type, new UPushAliasCallback() {
////                    @Override
////                    public void onMessage(boolean success, String message) {
////                        Log.i(TAG, "setAlias " + success + " msg:" + message);
////                    }
////                });
//            }
//
//            @Override
//            public void onFailure(String errCode, String errDesc) {
//                Log.e(TAG, "register failure：--> " + "code:" + errCode + ",desc:" + errDesc);
//            }
//        });
//
//        if (UMUtils.isMainProgress(context)) {
//            registerDeviceChannel(context);
//        }
    }

    /**
     * 注册设备推送通道（小米、华为等设备的推送）
     */
    private static void registerDeviceChannel(Context context) {
        //小米通道，填写您在小米后台APP对应的xiaomi id和key
        MiPushRegistar.register(context, PushConstants.MI_ID, PushConstants.MI_KEY);
        //华为，注意华为通道的初始化参数在minifest中配置
        HuaWeiRegister.register(context.getApplicationContext());
        //魅族，填写您在魅族后台APP对应的app id和key
        MeizuRegister.register(context, PushConstants.MEI_ZU_ID, PushConstants.MEI_ZU_KEY);
        //OPPO，填写您在OPPO后台APP对应的app key和secret
        OppoRegister.register(context, PushConstants.OPPO_KEY, PushConstants.OPPO_SECRET);
        //vivo，注意vivo通道的初始化参数在minifest中配置
        VivoRegister.register(context);

        //fcm
//        FCMRegister.register(context);
    }

    //推送设置
    private static void pushSetting(Context context) {
        PushAgent pushAgent = PushAgent.getInstance(context);

        //设置通知栏显示通知的最大个数（0～10），0：不限制个数
        pushAgent.setDisplayNotificationNumber(0);

        //推送消息处理
        UmengMessageHandler msgHandler = new UmengMessageHandler() {
            //处理通知栏消息
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                super.dealWithNotificationMessage(context, msg);
                Class clazz= null;
                try {
                    clazz = Class.forName("com.smart.rinoiot.user.FlashActivity");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                LinNotify.showMuch(context, msg.title, msg.text, clazz);
                Log.i(TAG, "notification receiver:" + msg.getRaw().toString());
            }

            //自定义通知样式，此方法可以修改通知样式等
            @Override
            public @Nullable
            Notification getNotification(Context context, UMessage msg) {
                return super.getNotification(context, msg);
            }

            //处理透传消息
            @Override
            public void dealWithCustomMessage(Context context, UMessage msg) {
                super.dealWithCustomMessage(context, msg);
                Log.i(TAG, "custom receiver:" + msg.getRaw().toString());
            }
        };
        pushAgent.setMessageHandler(msgHandler);

        //推送消息点击处理
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
                Log.i(TAG, "click openActivity: " + msg.getRaw().toString());
            }

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                Log.i(TAG, "click launchApp: " + msg.getRaw().toString());
            }

            @Override
            public void dismissNotification(Context context, UMessage msg) {
                super.dismissNotification(context, msg);
                Log.i(TAG, "click dismissNotification: " + msg.getRaw().toString());
            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);

        //自定义接收并处理消息
//        pushAgent.setPushIntentServiceClass(MyCustomMessageService.class);

    }


    private void setMetaData(Context context) {
        try {
            config = loadConfig(context);
            setUPushChannelData();
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            appInfo.metaData.putString("com.huawei.hms.client.appid", config.getHuaweiAppId());
            appInfo.metaData.putString("com.vivo.push.api_key", config.getVivoAppSecret());
            appInfo.metaData.putString("com.vivo.push.app_id", config.getVivoAppId());
        } catch (PackageManager.NameNotFoundException var4) {
            Log.e("setMetaData", var4.getMessage());
        }

    }

    /**
     * 解析assets资源文件
     */
    private PushConfig loadConfig(Context context) {
        String json = getJson("push_config.json", context);
        return TextUtils.isEmpty(json) ? new PushConfig() : new Gson().fromJson(json, PushConfig.class);
    }

    /**
     * 设置各厂家appid及secret
     */
    private void setUPushChannelData() {
        if (config != null) {
            PushConstants.APP_KEY = config.getUmKey();
            PushConstants.MESSAGE_SECRET = config.getMessageSecret();
            PushConstants.APP_MASTER_SECRET = config.getMasterSecret();
            PushConstants.MI_ID = config.getMiAppId();
            PushConstants.MI_KEY = config.getMiKey();
            PushConstants.MEI_ZU_ID = config.getMeiZuId();
            PushConstants.MEI_ZU_KEY = config.getMeiZuKey();
            PushConstants.OPPO_KEY = config.getOppoKey();
            PushConstants.OPPO_SECRET = config.getOppoSecret();
        }

    }

    public static String getJson(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));

            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
