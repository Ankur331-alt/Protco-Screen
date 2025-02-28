package com.smart.rinoiot.common.bean;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/11/22 17:02
 * @description
 */
public class PanelCommonFunBean implements Serializable {
    private ReactInstanceManager reactInstanceManager;
    private ReactRootView reactRootView;

    public ReactInstanceManager getReactInstanceManager() {
        return reactInstanceManager;
    }

    public void setReactInstanceManager(ReactInstanceManager reactInstanceManager) {
        this.reactInstanceManager = reactInstanceManager;
    }

    public ReactRootView getReactRootView() {
        return reactRootView;
    }

    public void setReactRootView(ReactRootView reactRootView) {
        this.reactRootView = reactRootView;
    }
}
