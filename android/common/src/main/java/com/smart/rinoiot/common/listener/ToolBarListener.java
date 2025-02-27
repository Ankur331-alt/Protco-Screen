package com.smart.rinoiot.common.listener;

import android.view.View;

/**
 * @Package: com.wq.lib.base.listener
 * @ClassName: ToolBarListener.java
 * @Description: toolbar点击事件监听
 * @Author: xf
 * @CreateDate: 2020/3/29 17:57
 * @UpdateUser: 更新者：xf
 * @UpdateDate: 2020/3/29 17:57
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface ToolBarListener {
    /**
     * @param
     * @return
     * @method onBack
     * @description 返回监听
     * @date: 2020/3/29 17:57
     * @author: xf
     */
    void onBack(View view);

    /**
     * @param
     * @return
     * @method onRightClick
     * @description 右边点击事件
     * @date: 2020/3/29 17:58
     * @author: xf
     */
    void onRightClick(View view);
}
