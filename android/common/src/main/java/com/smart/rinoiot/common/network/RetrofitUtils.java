package com.smart.rinoiot.common.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.interceptor.NullOnEmptyConverterFactory;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.AESUtil;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.LgUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@SuppressLint("TrustAllX509TrustManager")
public class RetrofitUtils {
    public static Retrofit retrofit = null;
    private static String url = "";

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";

    @SuppressLint("StaticFieldLeak")
    private static Context context;
//    private static String access_token;

//    private static AppTokenListener appTokenListener;
//
//    public static void setAppTokenListener(AppTokenListener tokenListener) {
//        appTokenListener = tokenListener;
//    }

    public static void init(Context mContext) {
        context = mContext;
    }

    public static Retrofit createInstance() {
        url = Constant.BASE_URL;
        retrofit = create();
        return retrofit;
    }

    public static ApiService getService() {
        url = Constant.BASE_URL;
        if (retrofit == null) {
            return create().create(ApiService.class);
        } else {
            return retrofit.create(ApiService.class);
        }
    }

    public static <T> T getService(Class<T> t) {
        url = Constant.BASE_URL;
        if (retrofit == null) {
            return create().create(t);
        } else {
            return retrofit.create(t);
        }
    }

    private static Retrofit create() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // log用拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor requestInterceptor = chain -> {
            Request request = chain.request();
            Request.Builder requestBuilder;
            Map<String, String> params = new HashMap<>();
            if (POST.equals(request.method())) {
                RequestBody requestBody = requestBody(request);
                if (requestBody instanceof FormBody) {
                    FormBody.Builder newBuilder = new FormBody.Builder();
                    FormBody formBody = (FormBody) requestBody;
                    for (int i = 0; i < formBody.size(); i++) {
                        newBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                    }
                    for (String key : params.keySet()) {
                        newBuilder.addEncoded(key, params.get(key));
                    }
                    formBody = newBuilder.build();
                    requestBuilder = getBuilder(request);
                    request = requestBuilder.post(formBody).build();
                } else if (request.body() instanceof MultipartBody) {
                    requestBuilder = getBuilder(request);
                    request = requestBuilder.post(requestBody).build();
                } else if (request.body() instanceof RequestBody) {
                    requestBuilder = getBuilder(request);
                    request = requestBuilder.post(requestBody).build();
                } else {
                    requestBuilder = request.newBuilder();
                    request = requestBuilder.build();
                }
            } else if (PUT.equals(request.method())) {
                RequestBody requestBody = requestBodyAES(request);
                if (requestBody instanceof FormBody) {
                    FormBody.Builder newBuilder = new FormBody.Builder();
                    FormBody formBody = (FormBody) requestBody;
                    for (int i = 0; i < formBody.size(); i++) {
                        newBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                    }
                    for (String key : params.keySet()) {
                        newBuilder.addEncoded(key, params.get(key));
                    }
                    formBody = newBuilder.build();
                    requestBuilder = getBuilder(request);
                    request = requestBuilder.put(formBody).build();
                } else if (request.body() instanceof RequestBody) {
                    requestBuilder = getBuilder(request);
                    request = requestBuilder.put(requestBody).build();
                } else {
                    requestBuilder = request.newBuilder();
                    request = requestBuilder.build();
                }
            } else if (GET.equals(request.method()) || DELETE.equals(request.method())) {
                //拿到拥有以前的request里的url的那些信息的builder
                HttpUrl.Builder newBuilder = request
                        .url()
                        .newBuilder();

                for (String key : params.keySet()) {
                    newBuilder.addQueryParameter(key, params.get(key));
                }
                HttpUrl httpUrl = newBuilder.build();
                requestBuilder = getBuilder(request);
                if (GET.equals(request.method())) {
                    request = requestBuilder.url(httpUrl).get().build();
                } else {
                    request = requestBuilder.url(httpUrl).delete().build();
                }

            } else {
                requestBuilder = request.newBuilder();
                request = requestBuilder.build();
            }

            return chain.proceed(request);
        };
        NetworkErrorInterceptor networkErrorInterceptor = new NetworkErrorInterceptor();
        builder
                .addInterceptor(requestInterceptor)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(networkErrorInterceptor)
                //设置超时
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                //错误重连
                .retryOnConnectionFailure(true);
        OkHttpClient client = builder.build();
        disableCertification(client);
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                //设置 Json 转换器
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    /**
     * 添加头部信息
     */
    private static Request.Builder getBuilder(Request request) {
        Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.addHeader("client_id", Constant.CLIENT_ID);
        requestBuilder.addHeader("auth", Constant.CLIENT_ID);
        requestBuilder.addHeader("version", AppUtil.getLocalVersion());
        requestBuilder.addHeader("os_name", "android");
        requestBuilder.addHeader("language", AppUtil.getInstance().getLanguageData());
        requestBuilder.addHeader("app_id", Constant.AES_APPID);
        requestBuilder.addHeader("devId", AppUtil.getInstance().getAndroidDeviceId());
        if (request.url().toString().contains("auth/oauth/token") || request.url().toString().contains("auth/oauth/getTempCredentials")) {
            requestBuilder.addHeader("scope", Constant.SCOPE);
            requestBuilder.addHeader("Authorization", "Basic " + Constant.CLIENT_ID);
            UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(context);
            if (userInfo != null) {
                requestBuilder.addHeader("refresh_token", userInfo.refreshToken);
            }
        } else if (request.url().toString().contains("auth/oauth/logout")) {
            UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(context);
            if (userInfo != null && !TextUtils.isEmpty(userInfo.accessToken)) {
                requestBuilder.addHeader("Authorization", "Bearer " + userInfo.accessToken);
            }
        } else if (request.url().toString().contains("https://storage.rinoiot.com/")) {
            /// UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(context);
            ///if (userInfo != null && !TextUtils.isEmpty(userInfo.accessToken)) {
            ///    requestBuilder.addHeader("Authorization", "Bearer " + userInfo.accessToken);
            ///}
        } else {
            UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(context);
            if (userInfo != null && !TextUtils.isEmpty(userInfo.accessToken)) {
                requestBuilder.addHeader("Authorization", "Bearer " + userInfo.accessToken);
            }
        }
        return requestBuilder;
    }

    private static RequestBody requestBodyAES(Request request) {
        RequestBody requestBody = null;
        try {
            String url = request.url().toString();
            if (url.contains("buriedPoint/dash") || !url.contains("sync")) {
                requestBody = request.body();
            } else {
                String strBody = AESUtil.encrypt(getParamContent(request.body()), Constant.AES_SECRET);
                requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), strBody);
                LgUtils.e("strBody = " + strBody + "strBodydec = " + AESUtil.decrypt(strBody, Constant.AES_SECRET));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestBody;
    }

    private static RequestBody requestBody(Request request) {
        RequestBody requestBody = null;
        try {
            assert request.body() != null;
            String strBody = getParamContent(request.body());
            if (request.url().toString().contains("auth/oauth/token") || request.url().toString().contains("auth/oauth/logout")) {
                requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), strBody);
            } else if (request.body() != null) {
                requestBody = request.body();
            } else {
                requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), strBody);
            }
            LgUtils.e("okhttp Body = " + strBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestBody;
    }

//    //组装MD5加密数据
//    private static String setSignMD5Str(String timestamp, String strUid) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("appid=").append(Constants.AES_APPID).append("&timestamp=").append(timestamp)
//                .append("&uid=").append(strUid).append("&").append(Constants.AES_SECRET);
//        return stringBuilder.toString();
//    }
//
//    private static String getSign(Map<String, String> params) {
//        Map<String, String> tempMap = sortMapByKey(params);
//        StringBuilder tempSignResult = new StringBuilder();
//        for (String mS : tempMap.keySet()) {
//            tempSignResult.append(mS);
//            tempSignResult.append(tempMap.get(mS));
//        }
//        tempSignResult.append("G2CvxXtFuS53welU");
//        return Md5Util.md5(tempSignResult.toString());
//    }

//    /**
//     * 使用 Map按key进行排序
//     *
//     * @param map
//     * @return
//     */
//    private static Map<String, String> sortMapByKey(Map<String, String> map) {
//        if (map == null || map.isEmpty()) {
//            return null;
//        }
//        Map<String, String> sortMap = new TreeMap<String, String>(
//                new MapKeyComparator());
//        sortMap.putAll(map);
//        return sortMap;
//    }

//    private static class MapKeyComparator implements Comparator<String> {
//
//        @Override
//        public int compare(String str1, String str2) {
//
//            return str1.compareTo(str2);
//        }
//    }

    /**
     * 获取常规post请求参数
     */
    private static String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }

//    private static String getToken() {
//        //为了保证效率不能每次都去文件里面读取,优先从缓存里面读取
//        access_token = SharedPreferenceUtil.getInstance().get(Constant.ACCESS_TOKEN, "");
//        LgUtil.e("===============================>>>getToken="+access_token);
//        return access_token;
//
//    }

    private static void disableCertification(OkHttpClient okHttpClient) {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        HostnameVerifier hv1 = (hostname, session) -> true;

        String workerClassName = "okhttp3.OkHttpClient";
        try {
            Class workerClass = Class.forName(workerClassName);
            Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
            hostnameVerifier.setAccessible(true);
            hostnameVerifier.set(okHttpClient, hv1);

            Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
            sslSocketFactory.setAccessible(true);
            sslSocketFactory.set(okHttpClient, sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}