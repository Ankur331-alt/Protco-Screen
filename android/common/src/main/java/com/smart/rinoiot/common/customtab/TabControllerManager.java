package com.smart.rinoiot.common.customtab;

import android.content.Context;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.utils.LgUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;


/**
 * * @author tw
 * * @time 2022/12/5 15:28
 * * @description  左侧选择控制器
 */
public class TabControllerManager {
    private static TabControllerManager instance;
    private final Map<String, String> tabConfig = new HashMap<>();

    /**
     * 新增的模块必须在这里配置
     */
    private TabControllerManager() {}

    public static TabControllerManager getInstance() {
        if (instance == null) {
            instance = new TabControllerManager();
        }
        return instance;
    }

    public List<Fragment> getTabFragment(List<MenuBean> menuBeans) {
        if (menuBeans == null || menuBeans.size() == 0) {
            return Collections.emptyList();
        }
        List<Fragment> list = new ArrayList<>();
        for (MenuBean menuBean : menuBeans) {
            String s = tabConfig.get(menuBean.getMenuCode());
            if (TextUtils.isEmpty(s)) {
                continue;
            }
            try {
                Class<?> aClass = BaseApplication.getApplication().getClassLoader().loadClass(s);
                Object o = aClass.newInstance();
                list.add((Fragment) o);
            } catch (Exception e) {
                LgUtils.e(e.getMessage());
            }

        }
        return list;
    }

    public NavigationController createBottomBarItem(Context mContext, PageNavigationView mTab) {
        //自定义配置
        List<MenuBean> menuBeans = getMenuBeanData(mContext);
        //enableVerticalLayout设置垂直布局
        PageNavigationView.CustomBuilder custom = mTab.custom().enableVerticalLayout();
        for (MenuBean menuBean : menuBeans) {
            BaseTabItem item = newItem(
                    mContext,
                    menuBean.getNormalIcon(),
                    menuBean.getSelectIcon(),
                    menuBean.getMenuName(),
                    menuBeans.size()
            );
            custom.addItem(item);
        }

        return custom.build();
    }

    /**
     * 创建一个Item
     * @param mContext context
     * @param drawable normal icon
     * @param checkedDrawable checked icon
     * @param text item label
     * @param size size
     * @return the base tab item
     */
    private BaseTabItem newItem(Context mContext, int drawable, int checkedDrawable, String text, int size) {
        SpecialTab specialTab = new SpecialTab(mContext);
        specialTab.initialize(
                drawable, checkedDrawable, text, TextUtils.equals(text, mContext.getString(R.string.rino_common_information)), size
        );
        specialTab.setTextDefaultColor(
                mContext.getResources().getColor(R.color.cen_connect_not_connect_color)
        );
        specialTab.setTextCheckedColor(
                mContext.getResources().getColor(R.color.cen_connect_step_selected_color)
        );
        return specialTab;
    }

    /**
     * 获取tab页图片和标题
     *
     * @param context context
     */
    private List<MenuBean> getMenuBeanData(Context context) {
        List<String> tabNames = new ArrayList<>();
        tabNames.add(context.getString(R.string.rino_common_home));
        tabNames.add(context.getString(R.string.rino_common_household));
        /// tabNames.add(context.getString(R.string.rino_common_energy));
        tabNames.add(context.getString(R.string.rino_common_monitor));
        tabNames.add(context.getString(R.string.rino_common_settings));
        tabNames.add(context.getString(R.string.rino_common_information));
        //默认图标
        List<Integer> normalIcons = new ArrayList<>();
        normalIcons.add(R.drawable.icon_home_normal);
        normalIcons.add(R.drawable.icon_household_normal);
        /// normalIcons.add(R.drawable.icon_energy_normal);
        normalIcons.add(R.drawable.icon_monitor_normal);
        normalIcons.add(R.drawable.icon_set_normal);
        normalIcons.add(R.drawable.icon_infomation_normal);

        //选中图标
        List<Integer> selectIcons = new ArrayList<>();
        selectIcons.add(R.drawable.icon_home_selected);
        selectIcons.add(R.drawable.icon_household_selected);
        /// selectIcons.add(R.drawable.icon_energy_selected);
        selectIcons.add(R.drawable.icon_monitor_selected);
        selectIcons.add(R.drawable.icon_set_selected);
        selectIcons.add(R.drawable.icon_infomation_selected);

        List<MenuBean> menuBeans = new ArrayList<>();
        for (int i = 0; i < tabNames.size(); i++) {
            MenuBean menuBean = new MenuBean();
            menuBean.setMenuName(tabNames.get(i));
            menuBean.setNormalIcon(normalIcons.get(i));
            menuBean.setSelectIcon(selectIcons.get(i));
            menuBeans.add(menuBean);
        }
        return menuBeans;
    }
}
