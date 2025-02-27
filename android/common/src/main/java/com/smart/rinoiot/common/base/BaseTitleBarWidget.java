package com.smart.rinoiot.common.base;

import android.view.View;

import com.smart.rinoiot.common.listener.ToolBarListener;

/**
 * @Package: com.wq.lib.base.view
 * @ClassName: BaseTitleBarWidget.java
 * @Description: 实现自定义toolbar基类
 * @Author: xf
 * @CreateDate: 2020/3/29 17:22
 * @UpdateUser: 更新者：xf
 * @UpdateDate: 2020/3/29 17:22
 * @Version: 1.0
 */
public abstract class BaseTitleBarWidget {

    protected ToolBarListener toolBarListener;
    protected View view;

    public BaseTitleBarWidget(View view) {
        this.view = view;
        init(view);
    }

    public void setToolBarListener(ToolBarListener toolBarListener) {
        this.toolBarListener = toolBarListener;
    }

    public abstract void init(View view);

    /**
     * @param title
     * @return void
     * @method setTitle
     * @description 设置标题
     * @date: 2020/3/29 17:23
     * @author: xf
     */
    public abstract void setTitle(String title);

    //
//    public abstract void setTitleColor(int color);
//
//    public abstract void setTitleTextSize(float fontSize);
//
    //

    /**
     * @param rightText 文字
     * @return void
     * @method setRightText
     * @description 设置右边文字
     * @date: 2020/3/29 17:24
     * @author: xf
     */
    public abstract void setRightText(String rightText);

    /**
     * @param rightTextColor 文字字体颜色
     * @return void
     * @method setRightText
     * @description 设置右边文字顔色
     * @date: 2020/3/29 17:24
     * @author: xf
     */
    public abstract void setRightTextColor(int rightTextColor);

    /**
     * @param rightTextSize 文字字体大小
     * @return void
     * @method setRightText
     * @description 设置右边文字大小
     * @date: 2020/3/29 17:24
     * @author: xf
     */
    public abstract void setRightTextSize(int rightTextSize);

    /**
     * @param res 图片资源
     * @return void
     * @method setRightIcon
     * @description 设置右边图片
     * @date: 2020/3/29 17:24
     * @author: xf
     */
    public abstract void setRightIcon(int res);

    /**
     * @param res 背景图片资源id
     * @return void
     * @method setBackIcon
     * @description 设置背景图片
     * @date: 2020/3/29 17:28
     * @author: xf
     */
    public abstract void setBackIcon(int res);

    /**
     * 设置标题居中显示
     *
     * @param centerTitle 标题居中
     * @return void
     * @method setCenterTitle
     * @description 是否标题居中
     * @date: 2020/3/29 17:29
     * @author: xf
     */
    public abstract void setCenterTitle(boolean centerTitle);

    /**
     * 设置标题大小
     *
     * @param size 文字大小
     * @return void
     * @method
     * @date: 2020/6/1 12:02 PM
     * @author: xf
     */
    public abstract void setTitleSize(float size);

    /**
     * 设置标题颜色
     *
     * @param color 文字颜色
     * @return void
     * @method
     * @date: 2020/6/1 12:02 PM
     * @author: xf
     */
    public abstract void setTitleColor(int color);

    /**
     * 设置右边文字是否显示
     *
     * @param visible 是否显示
     * @return void
     * @method
     * @date: 2020/6/1 12:02 PM
     * @author: xf
     */
    public abstract void setRightVisible(boolean visible);
    /**
     * 设置右边图片是否显示
     *
     * @param visible 是否显示
     * @return void
     * @method
     * @date: 2020/6/1 12:02 PM
     * @author: xf
     */
    public abstract void setRightImageVisible(boolean visible);

    /**
     * toolbar背景颜色
     *
     * @param color 颜色
     * @return void
     * @method
     * @date: 2020/6/1 12:02 PM
     * @author: xf
     */
    public abstract void setToolBarBackground(int color);

    /**
     * @param rightText 文字
     * @return void
     * @method setRightText
     * @description 设置右边文字
     * @date: 2020/3/29 17:24
     * @author: xf
     */
    public abstract void setRightTextButton(String rightText);

    /**
     * @return void
     * @method hideToolBarBack
     * @description 隐藏左侧返回按钮
     * @date: 2020/3/29 17:24
     * @author: xf
     */
    public abstract void hideToolBarBack();

    /**
     * @return void
     * @method hideToolBarBack
     * @description 设置左侧文案按钮
     * @date: 2020/3/29 17:24
     * @author: xf
     */
    public abstract void setLeftText(String leftText);

    /**
     * @param leftTextColor 文字字体颜色
     * @return void
     * @method setLeftTextColor
     * @description 设置左边文字顔色
     * @date: 2020/3/29 17:24
     * @author: xf
     */
    public abstract void setLeftTextColor(int leftTextColor);

    /**
     * @param leftTextSize 文字字体大小
     * @return void
     * @method setLeftTextSize
     * @description 设置左边文字大小
     * @date: 2020/3/29 17:24
     * @author: xf
     */
    public abstract void setLeftTextSize(int leftTextSize);
}
