package com.smart.rinoiot.common.listener;

import com.smart.rinoiot.common.bean.AssetBean;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/6
 */
public interface OnHomeManagerClickLister {
    void onClick(boolean isHomeManager, AssetBean assetBean);
}
