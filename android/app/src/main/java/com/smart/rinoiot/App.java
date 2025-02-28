package com.smart.rinoiot;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.maps.MapsInitializer;
import com.facebook.soloader.SoLoader;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.ble.BleManager;
import com.smart.rinoiot.common.crash.CrashHandler;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.language.ChangeLanguageManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ServiceProductUtils;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.SystemUtil;
import com.smart.rinoiot.common.utils.ToastUtil;

import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

@HiltAndroidApp
public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    @Override
    public void init() {
        SharedPreferenceUtil.getInstance().init(this);
        AppUtil.getInstance().init(this);
        DataSourceManager.init(this);
        LgUtils.init(this);
        ToastUtil.init(this);
        DpUtils.init(this);
        //获取服务地址、端口、mqtt地址
        ServiceProductUtils.getInstance().init();
        RetrofitUtils.init(getApplicationContext());
        // SoLoader 加载JSC引擎
        Observable.create(emitter -> SoLoader.init(this, false)).subscribeOn(Schedulers.io()).subscribe();
        //友盟初始化
        if (!BuildConfig.DEBUG) {
            PushManagerHelper.getInstance().initUmengSDK(this);
        }

        // 高德地图，隐私合规检查
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);
        SystemUtil.setSystemScreenOff(this, 0);
        /// CrashHandler.getInstance().init(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                if (activity instanceof BaseActivity) {
                    ((BaseActivity<?, ?>) activity).changeAppLanguage();
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    @Override
    public Resources getResources() {
        SharedPreferenceUtil.getInstance().init(this);
        return ChangeLanguageManager.getInstance().getResources(super.getResources());
    }
}
