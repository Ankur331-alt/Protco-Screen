package com.smart.rinoiot.common.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.billy.android.loading.Gloading;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.language.ChangeLanguageManager;
import com.smart.rinoiot.common.manager.ViewModelManager;
import com.smart.rinoiot.common.utils.LgUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseFragment<T extends ViewBinding, M extends BaseViewModel> extends Fragment implements EasyPermissions.PermissionCallbacks {
    protected T binding;
    protected Gloading.Holder mHolder;
    protected M mViewModel;
    private final List<BaseViewModel> models = new ArrayList<>();
    protected ViewGroup container;
    private AlertDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("className",this.getClass().getSimpleName()) ;
        this.container = container;
        binding = getBinding(LayoutInflater.from(getActivity()));
        initViewModel();
        mViewModel.onCreate(getActivity());
        for (BaseViewModel model : models) {
            model.onCreate(getActivity());
        }
        init();
        changeAppLanguage();
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        for (BaseViewModel model : models) {
            model.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (BaseViewModel model : models) {
            model.onDestroy();
        }
        models.clear();
    }


    @Override
    public void onPause() {
        super.onPause();
        for (BaseViewModel model : models) {
            model.onPause();
        }
    }

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
        mViewModel = new ViewModelManager<M>().createModel(createViewModelClass(), (AppCompatActivity) getActivity());
        mViewModel.getActonLiveData().observe(requireActivity(), (action) -> {
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
     * 初始化数据
     *
     * @return void
     * @method init
     * @date: 2020/3/23 19:57
     * @author: xf
     */
    public abstract void init();

    /**
     * 获取控件使用的binding
     *
     * @return T
     * @method getBinding
     * @date: 2020/3/23 19:57
     * @author: xf
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
        if (isResumed() || getContext() == null) return;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }
        loadingDialog = new AlertDialog.Builder(requireContext()).create();
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
    public void onPermissionsGranted(int requestCode, @NonNull @NotNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull @NotNull List<String> perms) {

    }

    public void changeAppLanguage() {
        ChangeLanguageManager.getInstance().changeLanguage(getActivity());
    }
}
