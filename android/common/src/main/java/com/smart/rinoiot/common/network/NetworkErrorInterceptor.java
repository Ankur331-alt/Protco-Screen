package com.smart.rinoiot.common.network;


import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;

import org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;

import okhttp3.Interceptor;
import okhttp3.Response;

import static com.smart.rinoiot.common.base.BaseApplication.getApplication;


/**
 * 网络异常时，统一处理提示
 */
public class NetworkErrorInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response mResponse = null;
        try {
            mResponse = chain.proceed(chain.request());
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof UnknownHostException) {
                exceptionTipDeal();
            } else {
                throw e;
            }
        }
        return mResponse;
    }

    /**
     * 页面重复请求多个接口时，延迟3s，反复弹出提示框
     */
    private void exceptionTipDeal() throws ApiException {
        long beforeTime = SharedPreferenceUtil.getInstance().get("exceptionTime", 0L);
        long currentTime = System.currentTimeMillis();
        if (currentTime - beforeTime > 3000) {
            SharedPreferenceUtil.getInstance().put("exceptionTime", currentTime);
            throw new ApiException(getApplication().getBaseContext().getString(R.string.rino_common_network_unavailable));
        } else {
            throw new ApiException("");
        }
    }
}
