package com.smart.rinoiot.family.manager;

import com.smart.rinoiot.common.bean.AssetBean;

import java.util.ArrayList;
import java.util.List;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/7
 */
public class HomeDataManager {
    public static HomeDataManager instance;
    public AssetBean assetBean;
    public List<AssetBean> assetBeans;
    //在当前家庭的成员等级
    public int role = -1;

    public static HomeDataManager getInstance() {
        if (instance == null) {
            instance = new HomeDataManager();
        }
        return instance;
    }


    public List<AssetBean> getAssetBeans() {
        return assetBeans == null ? new ArrayList<>() : assetBeans;
    }

    public void setAssetBeans(List<AssetBean> assetBeans) {
        this.assetBeans = assetBeans;
    }

    public AssetBean getAssetBean() {
        return assetBean == null ? new AssetBean() : assetBean;
    }

    public void setAssetBean(AssetBean assetBean) {
        this.assetBean = assetBean;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
