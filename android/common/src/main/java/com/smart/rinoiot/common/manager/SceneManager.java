package com.smart.rinoiot.common.manager;

import com.smart.rinoiot.common.bean.SceneBean;

public class SceneManager {
    private static SceneManager instance;
    private SceneBean currentEditSceneBean;

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setCurrentEditSceneBean(SceneBean currentEditSceneBean) {
        this.currentEditSceneBean = currentEditSceneBean;
    }

    public SceneBean getCurrentEditSceneBean() {
        return currentEditSceneBean;
    }
}
