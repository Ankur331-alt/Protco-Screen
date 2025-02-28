package com.smart.rinoiot.center.update;


import com.smart.rinoiot.common.utils.LgUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 文件下载管理类
 *
 * @Package: com.znkit.smart.manager
 * @ClassName: FileDownloadManager
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2021/3/29 2:23 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/3/29 2:23 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class FileDownloadManager {
    private String path;

    /**
     * 下载文件
     *
     * @param responseBodyResponse 返回数据
     * @param mDownloadApkListener 下载回调
     */
    public void downApkFile(Response<ResponseBody> responseBodyResponse, DownloadFileListener mDownloadApkListener, String path) {
        this.path = path;
        new Thread(new FileDownloadRun(responseBodyResponse, mDownloadApkListener)).start();
    }

    public class FileDownloadRun implements Runnable {
        Response<ResponseBody> mResponseBody;
        DownloadFileListener mDownloadApkListener;

        public FileDownloadRun(Response<ResponseBody> responseBody,
                               final DownloadFileListener downloadApkListener) {
            mResponseBody = responseBody;
            mDownloadApkListener = downloadApkListener;
        }

        @Override
        public void run() {
            writeResponseBodyToDisk(mResponseBody.body(), mDownloadApkListener);
        }
    }


    /**
     * @param body
     * @param downloadListener
     */
    private void writeResponseBodyToDisk(ResponseBody body, final DownloadFileListener downloadListener) {
        if (downloadListener != null)
            downloadListener.onStart();
        try {
            // 改成自己需要的存储位置
            File file = new File(path + File.separator + "temp.apk");
            LgUtils.i("writeResponseBodyToDisk() file=" + file.getPath());
            if (file.exists()) {
                file.delete();
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    //计算当前下载百分比，并经由回调传出
                    if (downloadListener != null)
                        downloadListener.onProgress((int) (100 * fileSizeDownloaded / fileSize));
                    LgUtils.d("file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                if (downloadListener != null)
                    downloadListener.onFinish(file.getPath());
                outputStream.flush();

            } catch (Exception e) {
                LgUtils.e(e.getMessage());
                if (downloadListener != null)
                    downloadListener.onError("" + e.getMessage());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
