package com.smart.rinoiot.scene.view;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.common.base.BaseViewPagerAdapter;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.fragment.StyleFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 双时间滚轮选择器
 * Create by dance, at 2018/12/25
 */
@SuppressLint("SetTextI18n")
public class PagerBottomPopView extends CenterPopupView {

    private StyleFragment colorStyleFragment;
    private StyleFragment iconStyleFragment;

    private OnDismissListener dismissListener;

    public PagerBottomPopView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom_pager;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        getPopupImplView().setBackground(XPopupUtils.createDrawable(getResources().getColor(R.color.cen_common_item_bg_color),
                popupInfo.borderRadius, popupInfo.borderRadius, popupInfo.borderRadius,popupInfo.borderRadius));
        FragmentActivity activity = (FragmentActivity) getContext();

        String[] mTitleDataList = {getContext().getString(R.string.rino_scene_more_style_color),
                getContext().getString(R.string.rino_scene_more_style_icon)};
        List<Fragment> fragments = new ArrayList<>();
        colorStyleFragment = new StyleFragment(true);
        iconStyleFragment = new StyleFragment(false);
        fragments.add(colorStyleFragment);
        fragments.add(iconStyleFragment);

        ViewPager mViewPager = findViewById(R.id.pager);
        BaseViewPagerAdapter baseViewPagerAdapter = new BaseViewPagerAdapter(activity.getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(baseViewPagerAdapter);

        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleDataList.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(context.getResources().getColor(R.color.summaryColor));
                colorTransitionPagerTitleView.setSelectedColor(context.getResources().getColor(R.color.f_c3c3c3));
                colorTransitionPagerTitleView.setText(mTitleDataList[index]);
                colorTransitionPagerTitleView.setOnClickListener(view -> mViewPager.setCurrentItem(index));
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        magicIndicator.setNavigator(commonNavigator);

        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    public PagerBottomPopView setOnDismissListener(OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if (dismissListener != null) dismissListener.onDismiss(colorStyleFragment.getValue(), iconStyleFragment.getValue());
    }

    public interface OnDismissListener {
        void onDismiss(String colorBg, String iconUrl);
    }
}