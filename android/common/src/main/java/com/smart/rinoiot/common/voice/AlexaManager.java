package com.smart.rinoiot.common.voice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.ProfileScope;
import com.amazon.identity.auth.device.api.authorization.Scope;
import com.amazon.identity.auth.device.api.authorization.User;
import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.google.gson.Gson;
import com.smart.rinoiot.common.utils.LgUtils;

import java.util.List;

/**
 * @author tw
 * @time 2022/11/28 17:06
 * @description Alexa 语音登录授权管理
 */
@SuppressLint("QueryPermissionsNeeded")
public class AlexaManager {
    public static final String ALEXA_PACKAGE_NAME = "com.amazon.dee.app";
    public static final String APP_TO_ALEXA_APP_URL = "https://alexa.amazon.com/spa/skill-account-linking-consent?fragment=skill-account-linking-consent&client_id=amzn1.application-oa2-client.10c1e82a4a8f4d52ba76a1994e0f0209&scope=alexa::skills:account_linking&skill_stage=live&response_type=code&redirect_uri=https://iot.rinoiot.com/auth&state=state";
    public static final String APP_TO_ALEXA_WEB_URL = "https://www.amazon.com/ap/oa?client_id=amzn1.application-oa2-client.10c1e82a4a8f4d52ba76a1994e0f0209&scope=alexa::skills:account_linking alexa::ask:skills:readwrite&response_type=code&redirect_uri=https://iot.rinoiot.com/auth&state=state";
    private static final String TAG = AlexaManager.class.getSimpleName();
    private static AlexaManager instance;
    private RequestContext requestContext;

    public static AlexaManager getInstance() {
        if (instance == null) {
            instance = new AlexaManager();
        }
        return instance;
    }

    /**
     * 初始化Alexa监听
     */
    public void alexaInit(Context context) {
        requestContext = RequestContext.create(context);
        requestContext.registerListener(new AuthorizeListener() {
            @Override
            public void onSuccess(AuthorizeResult authorizeResult) {
                LgUtils.w(TAG + "  onSuccess   authorizeResult=" + new Gson().toJson(authorizeResult));
                if (alexaListener != null)
                    alexaListener.alexaBindSuccess(2, authorizeResult.getAccessToken());
                User.fetch(context, new Listener<User, AuthError>() {

                    /* fetch completed successfully. */
                    @Override
                    public void onSuccess(User user) {
                        final String name = user.getUserName();
                        final String email = user.getUserEmail();
                        final String account = user.getUserId();
                        final String zipCode = user.getUserPostalCode();
                        LgUtils.w(TAG + "   user=" + new Gson().toJson(user));
                    }

                    /* There was an error during the attempt to get the profile. */
                    @Override
                    public void onError(AuthError ae) {
                        LgUtils.w(TAG + "   user=" + new Gson().toJson(ae));
                    }
                });


            }

            @Override
            public void onError(AuthError authError) {
                LgUtils.w(TAG + "  onError   authError=" + new Gson().toJson(authError));
            }

            @Override
            public void onCancel(AuthCancellation authCancellation) {
                LgUtils.w(TAG + "  onCancel   authCancellation=" + new Gson().toJson(authCancellation));
            }
        });
    }

    /**
     * 重新加载 果您的应用在用户完成授权流程前被操作系统关闭
     */
    public void onResume() {
        if (requestContext != null) {
            requestContext.onResume();
        }
    }

    /**
     * Login with Amazon当前支持的范围如下：profile（访问用户的名称、电子邮件地址和亚马逊账户ID）
     * 、profile:user_id（仅访问用户的亚马逊账户ID）和postal_code（访问用户保存在亚马逊账户文件中的邮政编码）
     */
    public void alexaAuthorize() {
        if (requestContext != null) {
            AuthorizationManager.authorize(new AuthorizeRequest
                    .Builder(requestContext)
                    .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                    .build());
        }
    }

    /**
     * 检查首次登录的用户 getToken以查看应用是否处于授权状态
     */
    public void onStart(Context context) {
        Scope[] scopes = {
                ProfileScope.profile(),
                ProfileScope.postalCode()
        };
        AuthorizationManager.getToken(context, scopes, new Listener<AuthorizeResult, AuthError>() {

            @Override
            public void onSuccess(AuthorizeResult result) {
                if (result.getAccessToken() != null) {
                    /*用户已登录*/
                    LgUtils.w(TAG + "   onStart 用户已登录 onSuccess result=" + new Gson().toJson(result));
                    if (alexaListener != null)
                        alexaListener.alexaBindSuccess(1, result.getAccessToken());
                } else {
                    /*用户未登录*/
                    LgUtils.w(TAG + "   onStart 用户未登录 onSuccess result=" + new Gson().toJson(result));
                }
            }

            @Override
            public void onError(AuthError ae) {
                /*用户未登录*/
                LgUtils.w(TAG + "   onStart 用户未登录 onError ae=" + new Gson().toJson(ae));
            }
        });
    }

    /**
     * 清除授权数据并注销用户
     */
    public void signOut(Context context) {
        AuthorizationManager.signOut(context, new Listener<Void, AuthError>() {
            @Override
            public void onSuccess(Void response) {
                // 设置退出状态UI
                LgUtils.w(TAG + "   signOut  onSuccess");
                if (alexaListener != null) alexaListener.alexaUnBindSuccess();
            }

            @Override
            public void onError(AuthError authError) {
                // 记录错误
                LgUtils.w(TAG + "   signOut  onError  authError=" + new Gson().toJson(authError));
            }
        });
    }

    private AlexaListener alexaListener;

    public AlexaListener getAlexaListener() {
        return alexaListener;
    }

    public void setAlexaListener(AlexaListener alexaListener) {
        this.alexaListener = alexaListener;
    }

    /**
     * alexa数据回调
     */
    public interface AlexaListener {
        //type  1:已授权且同步给后台，不需要在同步；2、已授权但未同步接口
        void alexaBindSuccess(int type, String accessToken);//绑定成功

        void alexaUnBindSuccess();//解绑成功
    }

    /**
     * 检测程序是否安装alexa app
     */
    public boolean isInstalled(Context mContext, String packageName) {
        PackageManager manager = mContext.getApplicationContext().getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo info : installedPackages) {
                if (info.packageName.equals(packageName))
                    return true;
            }
        }
        return false;
    }

}
