package com.smart.rinoiot.common.rn.comfun;

import android.text.TextUtils;
import android.util.Log;

import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.listener.DownloadFileListener;
import com.smart.rinoiot.common.manager.CommonNetworkManager;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.FileUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.MD5Utils;
import com.smart.rinoiot.common.utils.ToastUtil;

import java.io.File;

/**
 * @author tw
 * @time 2022/11/24 16:38
 * @description 常用功能更新及加载路径
 */
public class CommonPanelUtils {
    private static final String TAG = CommonPanelUtils.class.getSimpleName();
    private static CommonPanelUtils instance;
    private static final String COMMON_PANEL_NAME = "Rino_common_panel";
    public static final String COMMON_PANEL_PATH = BaseApplication.getApplication()
            .getApplicationContext().getFilesDir().getPath() + File.separator + COMMON_PANEL_NAME;

    public static CommonPanelUtils getInstance() {
        if (instance == null) {
            instance = new CommonPanelUtils();
        }
        return instance;
    }

    /**
     * 判断该路径下path: /data/data/package/files，是否存在fileName面板
     */
    public boolean getDataFilePanel() {
        File file = new File(COMMON_PANEL_PATH + File.separator + "index.android.bundle");
        return file.exists();
    }


    /**
     * 下载到本地的面板和当前线上版本面板MD5值对比
     */
    private boolean panelFileCheckMd5(String fileMd5) {
        //md5校验下载的zip包
        File file = new File(COMMON_PANEL_PATH + ".zip");
        if (file.exists()) {
            //文件存在
            String md5 = MD5Utils.fileMD5(file);
            return file.exists() && !TextUtils.isEmpty(md5) && !TextUtils.isEmpty(fileMd5) &&
                    TextUtils.equals(md5.toLowerCase(), fileMd5.toLowerCase());
        }
        return false;
    }

    /**
     * 下载常用功能面板
     * eg:{"md5":"5a09e901000c68d16c3eb6ce9fce1263",
     * "resUrl":"<a href="https://storage-app.rinoiot.com/commonly-shortcut/android/Rino-CommonlyShortcut_0.70_android_0.0.1.zip">resUrl</a>"}
     *
     * @return true:加载data目录下下载的文件 false:加载assets文件
     */
    public boolean downloadCommonPanel(String checkData) {
        Log.d(TAG, "downloadCommonPanel: " + checkData);
        /// try {
        ///     String androidUrl = "", fileMd5 = "", fileName = COMMON_PANEL_NAME + ".zip";
        ///     if (TextUtils.isEmpty(checkData) && !getDataFilePanel()) {
        ///         LgUtils.w(TAG + "   常用功能下载数据为空且本地没有下载，走asset目录");
        ///         return false;
        ///     }
        ///     if (!TextUtils.isEmpty(checkData)) {
        ///         JSONObject jsonObject = new JSONObject(checkData);
        ///         if (jsonObject.has("resUrl")) {
        ///             androidUrl = jsonObject.getString("resUrl");
        ///         }
        ///         if (jsonObject.has("md5")) {
        ///             fileMd5 = jsonObject.getString("md5");
        ///         }
        ///     }
        ///
        ///     if (getDataFilePanel()) {//本地是否下载了通用功能面板
        ///         LgUtils.w(TAG + "   data目录下以存在面板");
        ///         if (!panelFileCheckMd5(fileMd5) && !TextUtils.isEmpty(fileMd5) && !TextUtils.isEmpty(androidUrl)) {
        ///             LgUtils.w(TAG + "   data目录下以存在面板  但下载的md5和本地的不一致，重新下载  downLoadFileName=" + fileName);
        ///             commonPanelDownload(androidUrl, fileName);
        ///         }
        ///         //当前下载到data/data/files目录下为最新的
        ///         return true;
        ///
        ///     } else {//当前data/data/files目录下对应文件不存在，加载默认的assets文件
        ///         if (!TextUtils.isEmpty(fileMd5) && !TextUtils.isEmpty(androidUrl)) {
        ///             commonPanelDownload(androidUrl, fileName);
        ///         }
        ///         return false;
        ///     }
        /// } catch (JSONException e) {
        ///     e.printStackTrace();
        ///     return false;
        /// }
        return false;
    }

    /**
     * 常用功能面板文件下载
     */
    public void commonPanelDownload(String androidUrl, String downLoadFileName) {
        CommonNetworkManager.getInstance().downloadFileAsync(androidUrl, downLoadFileName, new DownloadFileListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onFinish(String path) {
                LgUtils.w(TAG + "  data目录下文件不存在或者已下载的md5和本地的不一致，重新下载  downLoadFileName=" + downLoadFileName);
                AppExecutors.getInstance().mainThread().execute(() -> FileUtils.uncompressZip4j(downLoadFileName, "",false));
            }

            @Override
            public void onError(String msg) {
                AppExecutors.getInstance().mainThread().execute(() -> ToastUtil.showMsg(msg));
            }
        });
    }
}
