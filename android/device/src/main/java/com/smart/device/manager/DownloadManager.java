package com.smart.device.manager;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.device.listener.OtaUpgradeListener;

/**
 * @author tw
 * @time 2022/10/18 10:46
 * @description OTA文件下载管理类
 */
public class DownloadManager {
    private static final String TAG = DownloadManager.class.getSimpleName();
    private static DownloadManager instance;
    private OtaUpgradeListener otaUpgradeListener;
    private int downloadTaskId;

    public void setOtaUpgradeListener(OtaUpgradeListener otaUpgradeListener) {
        this.otaUpgradeListener = otaUpgradeListener;
    }

    public static DownloadManager getInstance() {
        if (instance == null) {
            instance = new DownloadManager();
        }
        return instance;
    }

    public void initDownLoad(Context context) {
        FileDownloader.setup(context);
    }

    public void downloadTask(Context context, String downloadUrl) {
        UpgradeDialogUtils.getInstance().showLoadingDialog();
        final String SDPath = context.getFilesDir().getPath() + "/OTA/" + downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        downloadTaskId = FileDownloader.getImpl().create(downloadUrl)
                .setPath(SDPath)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(downloadListener).start();
        LgUtils.w("downlod11111  downloadTask downloadTaskId[=" + downloadTaskId);
    }

    /**
     * 停止下载
     */
    public void stopDownLoad() {
        FileDownloader.getImpl().pause(downloadTaskId);
        LgUtils.w("downlod11111  stopDownLoad downloadTaskId[=" + downloadTaskId);
    }

    /**清除下载文件*/
    public void clearDownloadFile(String path) {
        FileDownloader.getImpl().clear(downloadTaskId, path);
    }

    FileDownloadListener downloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            LgUtils.w(TAG + "   pending   soFarBytes=" + soFarBytes + "   totalBytes=" + totalBytes);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            LgUtils.w(TAG + "   connected   etag=" + etag + "   isContinue=" + isContinue + "soFarBytes=" + soFarBytes + "   totalBytes=" + totalBytes);
//                        if (otaUpgradeListener != null) {
//                            otaUpgradeListener.upgradeStart();
//                        }
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if (otaUpgradeListener != null) {
                otaUpgradeListener.upgradeProgress(soFarBytes * 100 / totalBytes);
            }
            LgUtils.w(TAG + "   progress   soFarBytes=" + soFarBytes + "   totalBytes=" + totalBytes);
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            LgUtils.w(TAG + "   blockComplete   task=" + task.getPath() + "  name=" + task.getFilename());
        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            LgUtils.w(TAG + "   retry   soFarBytes=" + soFarBytes + "   retryingTimes=" + retryingTimes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            if (otaUpgradeListener != null) {
                otaUpgradeListener.upgradeComplete();
            }
            LgUtils.w(TAG + "   completed   task=" + task.getPath() + "  name=" + task.getFilename());
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            LgUtils.w(TAG + "   paused   soFarBytes=" + soFarBytes + "   totalBytes=" + totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            LgUtils.w(TAG + "   error   e=" + e.getMessage());
            if (otaUpgradeListener != null) {
                otaUpgradeListener.upgradeError(e.getMessage());
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            LgUtils.w(TAG + "   warn    task=" + task.getPath() + "  name=" + task.getFilename());
        }
    };
}
