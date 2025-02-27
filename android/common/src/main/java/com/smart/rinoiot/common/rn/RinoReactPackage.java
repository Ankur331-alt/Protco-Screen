package com.smart.rinoiot.common.rn;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.smart.rinoiot.common.rn.view.RinoRCTWheelViewManager;
import com.smart.rinoiot.common.utils.AppExecutors;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: Rino
 * @Package: com.smart.rino.hybrid.modules
 * @ClassName: RinoReactPakcge
 * @Description: Rino自己做的native 模块和插件包
 * @Author: ZhangStar
 * @Emali: ZhangStar666@gmali.com
 * @CreateDate: 2022/9/14 16:03
 * @UpdateUser: 更新者：
 * @UpdateDate: 2022/9/14 16:03
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class RinoReactPackage implements ReactPackage {

//    @Override
//    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
//        return Collections.emptyList();
//    }

    @Override
    public @NotNull List<ViewManager> createViewManagers(@NotNull ReactApplicationContext reactContext) {
        return Collections.singletonList(
                new RinoRCTWheelViewManager()
        );
    }

    @Override
    public @NotNull List<NativeModule> createNativeModules(
            @NotNull ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new RinoDeviceModule(reactContext));
        modules.add(new RinoNetworkModule(reactContext));
        modules.add(new RinoEmitterModule(reactContext));
        modules.add(new RinoNativeModule(reactContext));
        AppExecutors.getInstance().delayedThread().schedule(() -> RNDialogUtils.getInstance().stopDialogLoading(), 1000, TimeUnit.MILLISECONDS);
        return modules;
    }
}
