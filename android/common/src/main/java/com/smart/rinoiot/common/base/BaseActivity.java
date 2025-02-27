package com.smart.rinoiot.common.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.billy.android.loading.Gloading;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.language.ChangeLanguageManager;
import com.smart.rinoiot.common.listener.ToolBarListener;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.ViewModelManager;
import com.smart.rinoiot.common.utils.BarConfig;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.FontDisplayUtils;
import com.smart.rinoiot.common.utils.PageActivityPathUtils;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.common.view.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseActivity<T extends ViewBinding, M extends BaseViewModel> extends AppCompatActivity implements ToolBarListener, EasyPermissions.PermissionCallbacks {
    protected T binding;
    protected M mViewModel;
    protected BaseTitleBarWidget toolBar;
    protected Gloading.Holder mHolder;
    private AlertDialog loadingDialog;
    protected View toolbarView;
    private final List<BaseViewModel> models = new ArrayList<>();
    protected final String TAG = this.getClass().getSimpleName();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            AppManager.getInstance().finishAllActivity();
            Intent intent = new Intent();
            intent.setAction(PageActivityPathUtils.FLASH_ACTIVITY);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        changeAppLanguage();
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding = getBinding(LayoutInflater.from(this));
        setFullStatusBar(R.color.transparent);
        //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
        StatusBarUtil.setNormalStatusBar(this);
        setContentView(binding.getRoot());
        initViewModel();
        mViewModel.onCreate(this);
        initToolBar();
        init();
        AppManager.getInstance().addActivity(this);
        setBottomNavigationBarColor();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 普通model的创建方法
     */
    protected Class<M> createViewModelClass() {
        //这里获得到的是类的泛型的类型
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        if (type != null) {
            Type[] actualTypeArguments = type.getActualTypeArguments();
            if (actualTypeArguments.length < 1) {
                return null;
            }
            return (Class<M>) actualTypeArguments[1];
        }
        return null;
    }

    /**
     * 多个model不共享方法创建
     *
     * @param mClass
     * @param <M>
     * @return
     */
    protected <M extends BaseViewModel> M createNoShareModel(Class<M> mClass) {
        BaseViewModel m = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(mClass);
        if (!models.contains(m)) {
            models.add(m);
        }
        return (M) m;
    }

    /**
     * 初始viewmodel
     *
     * @return void
     * @method initViewModel
     * @date: 2020/5/7 3:34 PM
     * @author: xf
     */
    private void initViewModel() {
        Class<M> viewModel = createViewModelClass();
        if (viewModel == null) {
            return;
        }
        mViewModel = new ViewModelManager<M>().createModel(viewModel, this);
        mViewModel.getActonLiveData().observe(this, (action) -> {
            if (action == null) {
                return;
            }
            switch (action) {
                case Constant.LOADING_FAILED:
                    // 显示加载数据失败view
                    showLoadFailed();
                    break;
                case Constant.SHOW_LOADING:
                    // 显示数据加载中loading
                    showLoadingDialog();
                    break;
                case Constant.EMPTY_DATA:
                    // 显示无数据view
                    showEmptyView();
                    break;
                case Constant.LOADING_SUCCESS:
                    // 关闭数据加载中loading
                    stopDialogLoading();
                    break;
                case Constant.HAS_DATA:
                default:
                    // 显示加载成功页面
                    hideEmptyView();
                    break;

            }
        });
    }

    /**
     * 初始化toolbar
     */
    private void initToolBar() {
        toolbarView = binding.getRoot().findViewById(R.id.toolbar);
        if (toolbarView != null) {
            toolBar = new TitleBar(toolbarView);
            toolBar.setTitle(getToolBarTitle());
            toolBar.setToolBarListener(this);
        }
    }

    public void setFullStatusBar(int resourceId) {
        ViewGroup viewGroup = (ViewGroup) binding.getRoot();
        View childAt = viewGroup.findViewById(R.id.statusBarView);
        if (childAt == null) {
            return;
        }
        BarConfig barConfig = new BarConfig(this);
        ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
        layoutParams.height = barConfig.getStatusBarHeight();
        childAt.setLayoutParams(layoutParams);
        childAt.setBackgroundResource(resourceId);
    }

    /**
     * 获取状态高度 默认20
     */
    public int getStatusBarHeight() {
        BarConfig barConfig = new BarConfig(this);
        return barConfig.getStatusBarHeight();
    }

    public void setNormalStatusBar(int resourceId) {
        ViewGroup viewGroup = (ViewGroup) binding.getRoot();
        View childAt = viewGroup.findViewById(R.id.statusBarView);
        if (childAt == null) {
            return;
        }
        BarConfig barConfig = new BarConfig(this);
        ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
        layoutParams.height = 0;
        childAt.setLayoutParams(layoutParams);
        childAt.setBackgroundResource(resourceId);
    }


    public void setFullStatusHeightBar() {
        ViewGroup viewGroup = (ViewGroup) binding.getRoot();
        View childAt = viewGroup.findViewById(R.id.statusBarView);
        if (childAt == null) {
            return;
        }
        BarConfig barConfig = new BarConfig(this);
        ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
        layoutParams.height = barConfig.getStatusBarHeight() + DpUtils.dip2px(30);
        childAt.setLayoutParams(layoutParams);
        childAt.setBackgroundResource(R.color.white);
    }

    /**
     * 设置toolbar背景色
     */
    public void setToolBarBackground(int color) {
        if (toolBar != null) {
            toolBar.setToolBarBackground(color);
        }
    }

    /**
     * 设置toolbar标题
     */
    public void setToolBarTitle(String title) {
        if (toolBar != null) {
            toolBar.setTitle(title);
        }
    }

    /**
     * 设置toolbar右侧文字按钮
     */
    public void setToolBarRightText(String rightText) {
        if (toolBar != null) {
            toolBar.setRightTextButton(rightText);
            toolBar.setRightVisible(true);
        }
    }

    /**
     * 设置toolbar右侧文字按钮
     */
    public void setToolBarRightTextButton(String rightText) {
        if (toolBar != null) {
            toolBar.setRightText(rightText);
            toolBar.setRightVisible(true);
        }
    }

    /**
     * 设置toolbar右侧图片
     */
    public void setToolBarRightIv(int res) {
        if (toolBar != null) {
            toolBar.setRightIcon(res);
            toolBar.setRightVisible(true);
        }
    }

    /**
     * 设置toolbar右侧颜色
     */
    public void setToolBarRightTextColor(int rightTextColor) {
        if (toolBar != null) {
            toolBar.setRightTextColor(rightTextColor);
        }
    }

    /**
     * 设置toolbar右侧大小
     */
    public void setToolBarRightTextSize(int rightTextSize) {
        if (toolBar != null) {
            toolBar.setRightTextSize(rightTextSize);
        }
    }

    /**
     * 隐藏toolbar右侧文字按钮
     */
    public void hideToolBarRightText() {
        if (toolBar != null) {
            toolBar.setRightVisible(false);
        }
    }

    /**
     * 隐藏toolbar左侧返回按钮
     */
    public void hideToolBarBack() {
        if (toolBar != null) {
            toolBar.hideToolBarBack();
        }
    }

    /**
     * 设置右边的图片
     *
     * @param imgSrc
     */
    public void setToolBarRightImage(int imgSrc) {
        if (toolBar != null) {
            toolBar.setRightIcon(imgSrc);
            toolBar.setRightImageVisible(true);
        }
    }

    /**
     * 隐藏右边的图片
     */
    public void hideToolBarRightImage() {
        if (toolBar != null) {
            toolBar.setRightImageVisible(false);
        }
    }

    protected void setBottomNavigationBarColor() {
        // 虚拟导航栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.cen_main_theme_bg));
        }
    }

    /**
     * 设置toolbar左侧文字按钮
     */
    public void setToolBarLeftText(String leftText) {
        if (toolBar != null) {
            toolBar.setLeftText(leftText);
        }
    }

    /**
     * 设置toolbar左侧文字大小
     */
    public void setToolBarLeftTextSize(int leftTextSize) {
        if (toolBar != null) {
            toolBar.setLeftTextSize(leftTextSize);
        }
    }

    /**
     * 设置toolbar左侧文字颜色
     */
    public void setToolBarLeftTextColor(int leftTextColor) {
        if (toolBar != null) {
            toolBar.setLeftTextColor(leftTextColor);
        }
    }

    /**
     * 获取toolbar标题
     */
    public abstract String getToolBarTitle();

    /**
     * 初始化数据
     */
    public abstract void init();

    /**
     * 获取控件使用的binding
     */
    public abstract T getBinding(LayoutInflater inflater);

    /**
     * make a Gloading.Holder wrap with current activity by default
     * override this method in subclass to do special initialization
     */
    protected void initLoadingStatusViewIfNeed() {
        if (mHolder == null) {
            //bind status view to activity root view by default
            ViewGroup root = (ViewGroup) binding.getRoot();
            mHolder = Gloading.getDefault().wrap(root.getChildAt(1)).withRetry(this::onLoadRetry);
        }
    }

    protected void onLoadRetry() {
        // override this method in subclass to do retry task
    }

    public void showLoadFailed() {
        initLoadingStatusViewIfNeed();
        mHolder.showLoadFailed();
    }

    protected void showEmptyView() {
        if (getEmptyView() != null) {
            getEmptyView().setVisibility(View.VISIBLE);
        }
    }

    protected void hideEmptyView() {
        if (getEmptyView() != null) {
            getEmptyView().setVisibility(View.GONE);
        }
    }

    protected View getEmptyView() {
        return binding.getRoot().findViewById(R.id.viewEmpty);
    }

    public void showLoadingDialog() {
        if (isFinishing()) return;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }
        loadingDialog = new AlertDialog.Builder(this).create();
        WindowManager.LayoutParams params = loadingDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        loadingDialog.getWindow().setAttributes(params);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        loadingDialog.setCancelable(false);
        loadingDialog.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK);
        loadingDialog.show();
        loadingDialog.setContentView(R.layout.popup_center_circle_progress);
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    protected void stopDialogLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        finishThis();
    }

    @Override
    public void onBack(View view) {
        finishThis();
    }

    @Override
    public void onRightClick(View view) {

    }

    public void finishThis() {
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String @NotNull [] permissions, int @NotNull [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull @NotNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull @NotNull List<String> perms) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusNotify(DeviceEvent deviceEvent) {
    }

    //设置NestedScrollView与recyclerView滑动冲突
    public void setScroolData(RecyclerView recyclerView) {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setFocusable(false);
    }

    static float fontScale = 1.1f;

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        return FontDisplayUtils.getResources(this, resources, fontScale);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(FontDisplayUtils.attachBaseContext(base, fontScale));
    }

    /**
     * 设置字体大小，同时通知界面重绘
     */
    public void setFontScale(float fontScale) {
        this.fontScale = fontScale;
        FontDisplayUtils.recreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeAppLanguage();
        mViewModel.onResume();
        for (BaseViewModel model : models) {
            model.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewModel.onPause();
        for (BaseViewModel model : models) {
            model.onPause();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mViewModel.onNewIntent(intent);
        for (BaseViewModel model : models) {
            model.onNewIntent(intent);
        }
    }

    public void changeAppLanguage() {
        ChangeLanguageManager.getInstance().changeLanguage(this);
    }
}
