package com.smart.rinoiot.user.viewmodel.setting;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.center.update.CheckUpdateUtil;
import com.smart.rinoiot.center.update.UpdateApi;
import com.smart.rinoiot.center.update.UpdateBean;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.user.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 关于model
 * @author author
 */
public class AboutAppViewModel extends BaseViewModel {

    private static final String TAG = "AboutAppViewModel";
    private final MutableLiveData<UpdateBean> checkUpdateLiveData = new MutableLiveData<>();

    public AboutAppViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public MutableLiveData<UpdateBean> getCheckUpdateLiveData() {
        return checkUpdateLiveData;
    }

    /**
     * Checks if there is an update available
     * @param context context
     */
    public void checkUpdate(Context context) {
        showLoading();
        Map<String, Object> params = new HashMap<>(2);
        params.put("platform", 7);
        params.put("version", AppUtil.getLocalVersion());
        RetrofitUtils.getService(UpdateApi.class).checkUpdate(params).enqueue(
                new BaseRequestListener<UpdateBean>() {
                    @Override
                    public void onResult(UpdateBean result) {
                        hideLoading();
                        if (context == null || result == null) {
                            showToast(context.getString(R.string.rino_common_latest_version));
                            return;
                        }
                        boolean isNewVersion = CheckUpdateUtil.compareIgnoreVersion(result);
                        if (isNewVersion) {
                            CheckUpdateUtil.showUpdatePop(context, result);
                        } else {
                            showToast(context.getString(R.string.rino_common_latest_version));
                        }
                    }

                    @Override
                    public void onError(String error, String msg) {
                        hideLoading();
                        showToast(context.getString(R.string.rino_common_latest_version));
                        Log.e(TAG, "onError: code=" + error + " | msg=" +msg);
                    }
                }
        );
    }
}
