package com.smart.rinoiot.common.crash;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CommonNetworkManager;
import com.smart.rinoiot.common.utils.FileUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider;
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials;
import com.tencent.qcloud.core.auth.SessionQCloudCredentials;

import java.io.File;

/**
 * @author tw
 * @time 2022/12/19 16:03
 * @description 腾讯云文件上传
 */
public class OSSUpdateManager {
    private static final String TAG = OSSUpdateManager.class.getSimpleName();
    private static OSSUpdateManager instance;
    private CosXmlService cosXmlService;

    public static OSSUpdateManager getInstance() {
        if (instance == null) {
            instance = new OSSUpdateManager();
        }
        return instance;
    }

    private void initService(Context context, String region, String bucket, String srcPath) {
        if (TextUtils.isEmpty(region)) {
            region = "ap-changsha";
        }
        // 存储桶region可以在COS控制台指定存储桶的概览页查看 https://console.cloud.tencent.com/cos5/bucket/
        // 关于地域的详情见 https://cloud.tencent.com/document/product/436/6224
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(region)
                // 使用 HTTPS 请求，默认为 HTTP 请求
                .isHttps(true)
                .builder();

        cosXmlService = new CosXmlService(context, serviceConfig, new ServerCredentialProvider());
        cosUpdateFile(context, bucket, srcPath);
    }

    /**
     * 传参文件参数
     *
     * @param bucket  存储桶名称，由bucketname-appid 组成，appid必须填入，可以在COS控制台查看存储桶名称
     *                对象在存储桶中的位置标识符，即称对象键"exampleobject"
     * @param srcPath 本地文件的绝对路径new File(context.getCacheDir(), "exampleobject").toString();
     *                若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
     */
    private void cosUpdateFile(Context context, String bucket, String srcPath) {
        // 初始化 TransferConfig，这里使用默认配置，如果需要定制，请参考 SDK 接口文档
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        // 初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);
        String path = context.getExternalCacheDir().getAbsolutePath() + CrashHandler.CRASH_PATH;
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File temp : files) {
                    String tempPath = temp.getAbsolutePath();
                    String cosPath = "";
                    if (!TextUtils.isEmpty(tempPath) && tempPath.contains("/")) {
                        cosPath = tempPath.substring(tempPath.lastIndexOf("/") + 1);
                    }
                    COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, tempPath, null);
                    cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
                        @Override
                        public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                            COSXMLUploadTask.COSXMLUploadTaskResult uploadResult =
                                    (COSXMLUploadTask.COSXMLUploadTaskResult) cosXmlResult;
                            LgUtils.w(TAG + "  onSuccess OSS上传成功  uploadResult=" + new Gson().toJson(uploadResult));
                            FileUtils.deleteOTAFile(tempPath);
                            CommonNetworkManager.getInstance().uploadCrashSave(uploadResult.accessUrl);

                        }

                        @Override
                        public void onFail(CosXmlRequest cosXmlRequest, @Nullable @org.jetbrains.annotations.Nullable CosXmlClientException clientException, @Nullable @org.jetbrains.annotations.Nullable CosXmlServiceException serviceException) {
                            LgUtils.w(TAG + "  onFail OSS上传失败  CosXmlClientException=" + new Gson().toJson(clientException) + "  serviceException= " + new Gson().toJson(serviceException));
                            if (clientException != null) {
                                clientException.printStackTrace();
                            } else if(serviceException != null) {
                                serviceException.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 将获取腾讯云的tmpSecretId、tmpSecretKey、sessionToken、expiredTime、startTime数据处理
     */
    String tmpSecretId, tmpSecretKey, sessionToken;
    long expiredTime, startTime;

    public void getOssToken(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            LgUtils.w(TAG + "   getOssToken  异常文件路径为空");
            return;
        }
        CommonNetworkManager.getInstance().getOssToken(new CallbackListener<OSSTokenBean>() {
            @Override
            public void onSuccess(OSSTokenBean result) {
                if (result != null && result.getAuthBean() != null) {
                    OSSTokenBean.AuthBean authBean = result.getAuthBean();
                    expiredTime = Long.parseLong(authBean.getExpiredTime());
                    startTime = Long.parseLong(authBean.getStartTime());
                    if (authBean.getCredentials() != null) {
                        OSSTokenBean.AuthBean.Credentials credentials = authBean.getCredentials();
                        tmpSecretId = credentials.getTmpSecretId();
                        tmpSecretKey = credentials.getTmpSecretKey();
                        sessionToken = credentials.getSessionToken();
                        initService(context, result.getRegion(), result.getBucket(), filePath);
                    }
                }
                LgUtils.w(TAG + "    getOssToken  onResult  result=" + new Gson().toJson(result));
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                LgUtils.w(TAG + "    getOssToken  onError  error=" + error + "    msg=" + error);
            }
        });
    }

    public class ServerCredentialProvider extends BasicLifecycleCredentialProvider {

        @Override
        protected QCloudLifecycleCredentials fetchNewCredentials() {
            // 最后返回临时密钥信息对象
            return new SessionQCloudCredentials(tmpSecretId, tmpSecretKey, sessionToken, startTime, expiredTime);
        }
    }

    /**
     * 上报异常文件且删除
     */
    public void upLoadCrashFile(Context context) {
        String path = context.getExternalCacheDir().getAbsolutePath() + CrashHandler.CRASH_PATH;
        File file = new File(path);
        if (file.exists() && file.isDirectory()&&file.listFiles()!=null&&file.listFiles().length>0) {
            getOssToken(context, path);
        }
    }
}
