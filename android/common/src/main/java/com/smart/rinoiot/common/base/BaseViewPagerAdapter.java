package com.smart.rinoiot.common.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * viewpager适配器基类
 *
 * @Package: com.znkit.smart.base
 * @ClassName: BaseViewPagerAdapter
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2020/12/7 11:04 AM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/7 11:04 AM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class BaseViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> titles;

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
        notifyDataSetChanged();
    }

    public BaseViewPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
        super(fm, BEHAVIOR_SET_USER_VISIBLE_HINT);
        this.fragmentList = fragmentList;
    }

    public BaseViewPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentList = fragmentList;
        this.titles = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return PagerAdapter.POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles == null ? super.getPageTitle(position) : titles.get(position);
    }
}
