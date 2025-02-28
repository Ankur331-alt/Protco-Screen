package com.smart.rinoiot.common.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.ProductGuideStepBean;

import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuideViewModel extends BaseViewModel {

    public GuideViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public CircleNavigator createIndicator(ViewPager vp, List<ProductGuideStepBean> data) {
        CircleNavigator circleNavigator = new CircleNavigator(vp.getContext());
        circleNavigator.setCircleCount(data.size());
        circleNavigator.setCircleColor(vp.getContext().getResources().getColor(R.color.main_theme_color));
        circleNavigator.setCircleClickListener(vp::setCurrentItem);
        return circleNavigator;
    }

    /**数据转化*/
}
