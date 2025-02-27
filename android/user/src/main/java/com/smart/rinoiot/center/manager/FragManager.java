package com.smart.rinoiot.center.manager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.smart.rinoiot.user.R;

import java.util.List;

/**
 * @author tw
 * @time 2022/12/5 15:13
 * @description fragment 管理页面
 */
public class FragManager {
    private static FragManager instance;

    public static FragManager getInstance() {
        if (instance == null) {
            instance = new FragManager();
        }
        return instance;
    }


    /**
     * 管理fragment 初始化
     */
    public FragmentManager loadFragment(FragmentActivity activity, FragmentManager fragmentManager, Fragment fragment, String tag) {
        // 获取到FragmentManager对象
        if (fragmentManager == null)
            fragmentManager = activity.getSupportFragmentManager();
        // 开启一个事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, fragment, tag);
        fragmentTransaction.commit();
        return fragmentManager;
    }

    /**
     * 管理fragment 移除
     */
    public void removeFragment(FragmentActivity activity, FragmentManager fragmentManager, List<Fragment> fragment) {
        // 获取到FragmentManager对象
        if (fragmentManager == null)
            fragmentManager = activity.getSupportFragmentManager();

        // 开启一个事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment.get(fragment.size() - 1));
        fragmentTransaction.commit();
    }

    /**
     * 管理fragment 下一个fragment
     */
    public void gotoNextFragment(FragmentActivity activity, FragmentManager fragmentManager, Fragment fragment) {
        if (fragmentManager != null) {
            loadFragment(activity, fragmentManager, fragment, null);
        }
    }
}
