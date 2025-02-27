package com.smart.rinoiot.center.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;

import com.lxj.xpopup.impl.ConfirmPopupView;
import com.smart.rinoiot.center.update.CheckUpdateUtil;
import com.smart.rinoiot.center.update.UpdateApi;
import com.smart.rinoiot.center.update.UpdateBean;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.magicIndicator.GradientLinePagerIndicator;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.manager.UserNetworkManager;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tw
 * * @time 2022/12/5 15:25
 * * @description 设置module
 */
public class SetUpViewModel extends BaseViewModel {
    private static final String TAG = "SetUpViewModel";

    /**
     * 检测wifi是否打开，且wifi是否可用
     */
    @SuppressLint("StaticFieldLeak")
    public ConfirmPopupView confirmPopupView;
    private List<ColorTransitionPagerTitleView> titleViews;
    private final MutableLiveData<Boolean> logoutLiveData = new MutableLiveData<>();

    public SetUpViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> getLogoutLiveData() {
        return logoutLiveData;
    }

    public void logout() {
        UserNetworkManager.getInstance().logout(new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                DataSourceManager.getInstance().clear();
                MqttManager.getInstance().disconnect();
                logoutLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                logoutLiveData.postValue(false);
            }
        });
    }

    @SuppressWarnings("all")
    public CommonNavigator createIndicator(ViewPager vp, List<String> titles) {
        CommonNavigator commonNavigator = new CommonNavigator(vp.getContext());
        titleViews = new ArrayList<>();
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                titleViews.add(colorTransitionPagerTitleView);
                colorTransitionPagerTitleView.setTextSize(24);
                colorTransitionPagerTitleView.setNormalColor(context.getResources().getColor(com.smart.rinoiot.family.R.color.cen_connect_step_selected_color));
                colorTransitionPagerTitleView.setSelectedColor(context.getResources().getColor(com.smart.rinoiot.family.R.color.cen_connect_step_selected_color));
                colorTransitionPagerTitleView.setText(titles.get(index));
                if (index == 0) {
                    colorTransitionPagerTitleView.setTextSize(28);
                    colorTransitionPagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                colorTransitionPagerTitleView.setOnClickListener(v -> {
                    vp.setCurrentItem(index);
                    setSelectedType(index);
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                //这是指示器跟文案内容一样长是必须设置setMode(MODE_WRAP_CONTENT)这个模式，指示器颜色渐变时，必须实现GradientLinePagerIndicator
                GradientLinePagerIndicator linePagerIndicator = new GradientLinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setPadding(0, DpUtils.dip2px(8), 0, DpUtils.dip2px(8));
                linePagerIndicator.setStartInterpolator(new AccelerateInterpolator());
                linePagerIndicator.setRoundRadius(DpUtils.dip2px(16));
                linePagerIndicator.setLineHeight(DpUtils.dip2px(16));
                linePagerIndicator.setLineWidth(DpUtils.dip2px(16));
                linePagerIndicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                return linePagerIndicator;
            }
        });
        return commonNavigator;
    }

    public void setSelectedType(int selectedIndex) {
        if (titleViews == null || titleViews.isEmpty()) {
            return;
        }
        for (ColorTransitionPagerTitleView textview : titleViews) {
            textview.setTypeface(Typeface.SANS_SERIF);
            textview.setTextSize(24);
        }
        if (selectedIndex < titleViews.size()) {
            titleViews.get(selectedIndex).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            titleViews.get(selectedIndex).setTextSize(28);
        }
    }

    public void checkoutWifiStatus(Fragment fragment, int requestCode) {
        if (confirmPopupView != null && confirmPopupView.isDismiss()) {
            confirmPopupView.show();
            return;
        }
        confirmPopupView = DialogUtil.showNormalMsg(fragment.getActivity(), "",
                getString(R.string.rino_device_current_no_wifi),
                getString(com.smart.rinoiot.common.R.string.rino_common_cancel),
                getString(com.smart.rinoiot.common.R.string.rino_device_to_connect),
                new DialogOnListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirm() {
                        Intent intent = new Intent();
                        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                        fragment.startActivityForResult(intent, requestCode);
                    }
                });
    }

    /**
     * app升级
     */
    public void checkUpdate() {
        showLoading();
        Map<String, Object> params = new HashMap<>(2);
        String localVersion = AppUtil.getLocalVersion();
        params.put("platform", 7);
        params.put("version", localVersion);
        RetrofitUtils.getService(UpdateApi.class).checkUpdate(params).enqueue(
                new BaseRequestListener<UpdateBean>() {
                    @Override
                    public void onResult(UpdateBean result) {
                        hideLoading();
                        if (result == null) {
                            showToast(mContext.getString(R.string.rino_common_latest_version));
                            return;
                        }
                        boolean isNewVersion = CheckUpdateUtil.compareIgnoreVersion(result);
                        if (isNewVersion) {
                            CheckUpdateUtil.showUpdatePop(mContext, result);
                        } else {
                            showToast(mContext.getString(R.string.rino_common_latest_version));
                        }
                    }

                    @Override
                    public void onError(String error, String msg) {
                        hideLoading();
                        showToast(mContext.getString(R.string.rino_common_latest_version));
                        Log.e(TAG, "onError: code=" + error + " | msg=" +msg);
                    }
                }

        );
    }
}
