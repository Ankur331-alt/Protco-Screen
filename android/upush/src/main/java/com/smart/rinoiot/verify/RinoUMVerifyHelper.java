package com.smart.rinoiot.verify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.smart.rinoiot.common.BuildConfig;
import com.smart.rinoiot.common.utils.AgreementPolicyUtils;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.upush.R;
import com.umeng.umverify.UMVerifyHelper;
import com.umeng.umverify.listener.UMPreLoginResultListener;
import com.umeng.umverify.listener.UMTokenResultListener;
import com.umeng.umverify.model.UMTokenRet;
import com.umeng.umverify.view.UMAuthUIConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author tw
 * @time 2022/11/29 13:54
 * @description 一键登录+本机号码认证
 */
@SuppressLint({"StaticFieldLeak", "RtlHardcoded"})
public class RinoUMVerifyHelper {
    private static final String SECRET_KEY = "+Ey+G3aRWDD34kWoODbFmQ6LnRdFurogVsr07eymaxtnAYMyAHGfxCthg44yw7pfilbJl0lahPJ7vejs/QsgsEiVXCH53Vf9AR09s4HSL2anueshtoOfK2c4Dzdcl3FlC5sBvsRusnlaacMeKdEJMOnsOwH9Cy5tQ2kg7/cIE3GjPYCL8Ik8CUqf/BO9QgZ6cWB71OTKEv8SMaY7F9UMi8MYJsmAlae+IXnghyInK5Ci0vMarM4HdjrI+cY5LR9CKg+nPirrBZ2BJJN9gfcqcufyd8PSrIMgRzDMLWcTU1OESYlJqgO3qQ==";
    private static RinoUMVerifyHelper instance;
    private static final String TAG = RinoUMVerifyHelper.class.getSimpleName();
    private Activity context;

    private VerifyListener verifyListener;


    public void setVerifyListener(VerifyListener verifyListener) {
        this.verifyListener = verifyListener;
    }

    public interface VerifyListener {
        void verifySuccess();//认证成功

        void getTokenSuccess(String token, String verifyId);//获取token成功

        void getTokenFail();//获取token失败
    }

    public static RinoUMVerifyHelper getInstance() {
        if (instance == null) {
            instance = new RinoUMVerifyHelper();
        }
        return instance;
    }

    /**
     * 友盟一键登录及本机号码认证
     * 获取UMVerifyHelper实例，设置秘钥信息
     */
    UMVerifyHelper umVerifyHelper;

    public void uMVerifyInit(Activity context) {
        this.context = context;
        umVerifyHelper = UMVerifyHelper.getInstance(context, mTokenListener);
        umVerifyHelper.setAuthSDKInfo(SECRET_KEY);
        umVerifyHelper.setAuthListener(mTokenListener);
        umVerifyHelper.setLoggerEnable(BuildConfig.DEBUG);
    }

    /**
     * 600000 获取token成功 无
     * 600001 唤起授权页成功 无
     * 600002 唤起授权页失败 建议切换到其他登录方式
     * 600004 获取运营商配置信息失败 建议切换到其它登录方式；创建工单
     * 600005 手机终端不安全 建议切换到其他登录方式
     * 600007 未检测到sim卡 提示用户检查sim卡后重试；或建议切换到其他登录方式
     * 600008 蜂窝网络未开启 提示用户开启移动网络后重试；或建议切换到其他登录方式；
     * 600009 无法判断运营商 建议切换到其它登录方式；创建工单
     * 600010 未知异常 建议切换到其它登录方式；创建工单
     * 600011 获取token失败 切换到其他登录方式
     * 600012 获取预取号失败 建议切换到其它登录方式；
     * 600013 运营商维护升级 该功能不可用 建议切换到其它登录方式；创建工单
     * 600014 运营商维护升级，该功能已达最大调用次数 建议切换到其它登录方式；创建工单
     * 600015 接口超时 建议切换到其他登录方式
     * 600017 AppID、Appkey解析失败 建议切换到其它登录方式；创建工单
     * 600021 点击登录时检测到运营商已切换 建议切换到其它登录方式
     * 600023 加载⾃定义控件异常 检查⾃定义控件添加是否正确
     * 600024 终端环境检查⽀持认证 无
     * 600025 终端检测参数错误 建议切换到其它登录方式；检查传⼊参数类型与范围是否正确
     * 600026 授权页已加载时不允许调用加速或预取号接口 检查是否有授权页拉起后，去调用加速或预取号接口，该行为不允许
     **/
    UMTokenResultListener mTokenListener = new UMTokenResultListener() {
        @Override
        public void onTokenSuccess(String s) {
            LgUtils.w(TAG + "  onTokenSuccess   s=" + s);
            try {
                UMTokenRet tokenRet = new Gson().fromJson(s, UMTokenRet.class);
                if (context != null && tokenRet != null && TextUtils.equals(tokenRet.getCode(), "600024")) {//终端支持认证
                    getLoginToken(context);
                } else if (tokenRet != null && TextUtils.equals(tokenRet.getCode(), "600000")) {//获取toklen成功
                    if (verifyListener != null)
                        verifyListener.getTokenSuccess(tokenRet.getToken(), tokenRet.getRequestId());
                } else {
                    if (tokenRet != null && TextUtils.equals(tokenRet.getCode(), "600001")) {// 唤起授权页成功
                        if (verifyListener != null) verifyListener.verifySuccess();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTokenFailed(String s) {
            LgUtils.w(TAG + "  onTokenFailed   s=" + s);
            umVerifyHelper.hideLoginLoading();
            umVerifyHelper.quitLoginPage();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject != null && jsonObject.has("code") && !TextUtils.equals(jsonObject.getString("code"), "700000")) {
                    if (verifyListener != null && !clickFlag) verifyListener.getTokenFail();
                    if (clickFlag && jsonObject.has("msg")) {
                        new AppExecutors().mainThread().execute(() -> {
                            try {
                                ToastUtil.customSystem(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            ToastUtil.showMsg("获取认证token失败！");
        }
    };

    /***
     * SDK环境检查函数，检查终端是否支持号码认证，通过UMTokenResultListener返回code
     * @param type 1:本机号码校验 UMVerifyHelper.SERVICE_TYPE_AUTH 2：一键登录 UMVerifyHelper.SERVICE_TYPE_LOGIN
     * 600024 终端支持认证
     * 600013 系统维护，功能不可用*/
    public void checkEnvAvailable(int type) {
        if (umVerifyHelper != null) {
            umVerifyHelper.checkEnvAvailable(type);
//            umVerifyHelper.setAuthListener(mTokenListener);
        }
    }

    /**
     * 拉取一键登录授权页页面
     * 参数一：当前上下文context；如果需要添加效果，则需要传入Activity句柄
     * 参数二：超时时间ms；
     */
    public void getLoginToken(Context context) {
        if (umVerifyHelper != null) {
            umVerifyHelper.getLoginToken(context, 5000);
        }
    }

    /**
     * 预取号码信息，建议在 APP 登录页初始化时调用，提高后续授权页的打开速度。
     * 不要在 App 启动初始化时调用，预取号接口有有效期，避免接口调用资源浪费
     * <p>
     * //     * @param overdueTime 超时时间 单位ms
     * //     * @param listener    预取结果回调函数
     */
    public void accelerateLoginPage() {
        umVerifyHelper.accelerateLoginPage(3000000, new UMPreLoginResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                LgUtils.w(TAG + "  accelerateLoginPage  onTokenSuccess  s=" + s);
//                ToastUtil.showMsg("预取号成功！");
            }

            @Override
            public void onTokenFailed(String s, String s1) {
                LgUtils.w(TAG + "  accelerateLoginPage  onTokenFailed  s=" + s + "   s1=" + s1);
//                ToastUtil.showMsg("预取号失败！");
            }
        });
    }

    public Activity getContext() {
        return context;
    }

    /**
     * 自定义一键登录展示页面 窗口模式
     */
    private boolean clickFlag;//点击一键登录 true 否则false

    public void oneClickLogInitDialog(boolean clickFlag) {
        release();
        this.clickFlag = clickFlag;
        checkEnvAvailable(UMVerifyHelper.SERVICE_TYPE_LOGIN);//本机认证
        configAuthPage();
//        verifyAuthUiControlClick();
    }

    /**
     * 设置一键登录页面样式
     */
    private void configAuthPage() {
        if (umVerifyHelper == null || context == null) return;
        int radius = DpUtils.dip2px(10);
        int[] intRadius = {radius, radius, 0, 0};
//        umVerifyHelper.closeAuthPageReturnBack(true);//授权页物理返回键禁用
        umVerifyHelper.setAuthUIConfig(new UMAuthUIConfig.Builder()
                .setAppPrivacyOne(String.format(context.getString(R.string.rino_user_book_name_fill),
                        context.getString(R.string.rino_user_user_agreement)),
                        AgreementPolicyUtils.getAgreement(context))//设置开发者隐私条款 1 名称和URL(名称，url)
                .setAppPrivacyTwo(String.format(context.getString(R.string.rino_user_book_name_fill),
                        context.getString(R.string.rino_user_privacy_policy)),
                        AgreementPolicyUtils.getPrivacyPolicy(context))//设置开发者隐私条款 2 名称和URL(名称，url)
                .setAppPrivacyColor(context.getResources().getColor(R.color.c_999999),
                        context.getResources().getColor(R.color.c_7B7B7B))//设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
                .setProtocolGravity(Gravity.LEFT)//设置隐私条款文字对齐方式，单位Gravity.xxx
                .setPrivacyTextSizeDp(12)//设置隐私条款文字大小（单位：dp，字体大小不随系统变化）
                .setLogBtnToastHidden(false)//设置checkbox未勾选时，点击登录按钮toast是否显示
                .setVendorPrivacyPrefix("《")//设置运营商协议前缀符号，只能设置一个字符，且只能设置<>()《》【】『』[]（）中的一个
                .setVendorPrivacySuffix("》")//设置运营商协议后缀符号，只能设置一个字符，且只能设置<>()《》【】『』[]（）中的一个
                .setNavHidden(false)//设置默认导航栏是否隐藏
                .setLogoHidden(true)//隐藏logo
                .setSloganHidden(false)
                .setSwitchAccHidden(true)//设置切换按钮点是否可见
                .setPrivacyState(false)//设置隐私条款是否默认勾选
                .setCheckboxHidden(false)//设置复选框是否隐藏
                .setLightColor(true)//设置状态栏字体颜色（系统版本 6.0 以上可设置黑色、白色）。true 为黑色
                .setStatusBarColor(Color.WHITE)//设置状态栏颜色（系统版本 5.0 以上可设置）
                .setStatusBarUIFlag(View.SYSTEM_UI_FLAG_VISIBLE)//设置状态栏UI属性 View.SYSTEM_UI_FLAG_LOW_PROFILE View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                .setWebViewStatusBarColor(Color.TRANSPARENT)//设置协议页状态栏颜色（系统版本 5.0 以上可设置）
                .setWebNavColor(Color.TRANSPARENT)//设置协议页顶部导航栏颜色
                .setWebNavTextColor(context.getResources().getColor(R.color.f_454545))//设置协议页顶部导航栏标题颜色
                .setWebNavTextSize(18)//设置协议页顶部导航栏文字大小
                .setWebNavReturnImgDrawable(context.getDrawable(R.drawable.icon_back))//设置协议页顶部导航栏返回键图片
                .setUncheckedImgDrawable(context.getDrawable(R.drawable.ic_checkbox_uncheck))//设置复选框未选中时的图片
                .setCheckedImgDrawable(context.getDrawable(R.drawable.ic_checkbox_check))//设置复选框选中时的图片
                .setNumberSizeDp(26)//设置手机号码字体大小（单位：db，字体大小不随系统变化）
                .setNumberColor(context.getResources().getColor(R.color.c_333333))//设置手机号码字体颜色
                .setNumFieldOffsetY_B(220)//设置号码栏控件相对底部的位移，单位 dp
                .setLogBtnText(context.getString(R.string.rino_user_login_one_click))//设置登录按钮文字
                .setLogBtnTextSizeDp(18)//设置登录按钮文字大小（单位：db，字体大小不随系统变化）
                .setLogBtnMarginLeftAndRight(20)//设置登录按钮相对于屏幕左右边缘边距
                .setLogBtnBackgroundDrawable(context.getDrawable(R.drawable.shape_btn_bg_22252b_8))//设置登录按钮背景图片的路径
                .setLogBtnOffsetY_B(120)//设置登录按钮相对底部的位移，单位 dp
//                .setLoadingImgDrawable(context.getDrawable(R.drawable.jz_dialog_progress))
                .setPrivacyBefore(context.getString(R.string.rino_user_agree_tip))//设置开发者隐私条款前置自定义文案
                .setSloganOffsetY_B(190)//设置slogan 相对底部的位移，单位dp
                .setSloganTextSizeDp(10)//设置Slogan文字大小（单位：db，字体大小不随系统变化）
                .setSloganTextColor(context.getResources().getColor(R.color.f_aaaaaa))//设置slogan 文字颜色
                .setDialogBottom(true)
                .setDialogAlpha(0.2f)
                .setDialogHeight(300)
                .setBottomNavColor(context.getResources().getColor(R.color.c_333333))
                .setNavReturnHidden(false)//设置导航栏返回按钮隐藏
                .setNavReturnImgDrawable(context.getDrawable(R.drawable.icon_guide_close))
                .setDialogWidth(DpUtils.px2dip(DpUtils.getScreenWidth()))
                .setPageBackgroundDrawable(context.getDrawable(R.drawable.shape_bg_white_only_top_10))
                .setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .setPackageName(context.getPackageName())
//                .setPrivacyAlertIsNeedShow(true)//设置二次隐私协议弹窗是否需要显示。取值：true：表示显示。false（默认值）：表示不显示。说明 针对弹窗形式的授权页暂时不支持二次授权弹窗页面。
//                .setPrivacyAlertIsNeedAutoLogin(true)//设置二次隐私协议弹窗点击按钮是否需要执行登录。取值：true（默认值）：表示需要执行。false：表示不需要执行。
//                .setPrivacyAlertMaskIsNeedShow(true)//设置二次隐私协议弹窗背景蒙层是否显示。true（默认值）：表示显示。false：表示不显示。
//                .setPrivacyAlertMaskAlpha(0.3f)//设置二次隐私协议弹窗蒙层透明度。默认值0.3。说明 取值范围0.3~1.0。
//                .setPrivacyAlertAlpha(1.0f)//设置二次隐私协议弹窗透明度。默认值1.0。说明 取值范围0.3~1.0。
////                .setPrivacyAlertBackgroundColor()//设置二次隐私协议弹窗背景色（同意并继续按钮区域）。
//                .setPrivacyAlertCornerRadiusArray(intRadius)//设置二次隐私协议弹窗的四个圆角值。说明 顺序为左上、右上、右下、左下，需要填充4个值，不足4个值则无效，如果值小于等于0则为直角。
////                .setPrivacyAlertAlignment()//设置屏幕居中、居上、居下、居左、居右，默认居中显示。
//                .setTapPrivacyAlertMaskCloseAlert(false)//设置二次隐私协议弹窗点击背景蒙层是否关闭弹窗。true（默认值）：表示关闭。 false：表示不关闭。
//                .setPrivacyAlertBtnTextColor(Color.WHITE)//设置按钮文字颜色。
//                .setPrivacyAlertBtnTextSize(15)
//                .setPrivacyAlertBtnBackgroundImgDrawable(context.getDrawable(R.drawable.shape_btn_bg_22252b_8))//设置按钮背景图片对象。
////                .setPrivacyAlertBtnTextSize()//设置按钮文字大小，默认值18 sp。
//                .setPrivacyAlertContentColor(context.getResources().getColor(R.color.main_theme_color))//设置服务协议文字颜色。
//                .setPrivacyAlertContentBaseColor(context.getResources().getColor(R.color.c_999999))//设置服务协议非协议文字颜色。
//                .setPrivacyAlertHeight(400)//设置弹窗高度。
//                .setPrivacyAlertWidth(320)//设置弹窗宽度。
//                .setPrivacyAlertTitleTextSize(15)//设置标题文字大小，默认值18 sp。
//                .setPrivacyAlertTitleColor(context.getResources().getColor(R.color.c_333333))//	设置标题文字颜色。
//                .setPrivacyAlertTitleOffsetY(20)//设置标题文字竖直偏移量。（单位：dp）
//                .setPrivacyAlertContentTextSize(13)//设置标题文字大小，默认值18 sp。
//                .setPrivacyAlertBtnWidth(300)//设置按钮高度。（单位：dp）
//                .setPrivacyAlertBtnHeight(50)//设置按钮宽度。（单位：dp）
                .create());

    }

    /**
     * 授权页面点击事件
     * 700000 点击返回，⽤户取消免密登录
     * 700001 点击切换按钮，⽤户取消免密登录
     * 700002 点击登录按钮事件
     * 700003 点击check box事件
     * 700004 点击协议富文本文字事件
     * 700006 点击一键登录拉起授权页二次弹窗
     * 700007 隐私协议二次弹窗关闭
     * 700008 点击隐私协议二次弹窗上同意并继续
     * 700009 点击隐私协议二次弹窗上的协议富文本文字
     */
    private void verifyAuthUiControlClick() {
        if (umVerifyHelper == null || context == null) return;
        umVerifyHelper.setUIClickListener((code, mContext, jsonObjectStr) -> {
            try {
                LgUtils.w(TAG + "   verifyAuthUiControlClick  code=" + code + "   jsonObjectStr=" + jsonObjectStr);
                if (TextUtils.equals("700002", code)) {//点击授权页面的登录按钮
                    JSONObject loginJson = new JSONObject(jsonObjectStr);
                    if (loginJson.has("isChecked") && loginJson.getBoolean("isChecked")) {
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LgUtils.w(TAG + "   verifyAuthUiControlClick 异常 e=" + e.getMessage());
            }
        });
    }

    /**
     * 主动设置选中用户协议和隐私政策
     */
    public void setProtocolChecked() {
        if (umVerifyHelper != null) {
            umVerifyHelper.setProtocolChecked(true);
//            umVerifyHelper.accelerateVerify();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (umVerifyHelper != null) {
            umVerifyHelper.quitLoginPage();
            umVerifyHelper.hideLoginLoading();
//            umVerifyHelper.setAuthListener(null);
//            umVerifyHelper.setUIClickListener(null);
//            umVerifyHelper.removeAuthRegisterViewConfig();
//            umVerifyHelper.removeAuthRegisterXmlConfig();
        }
    }
}
