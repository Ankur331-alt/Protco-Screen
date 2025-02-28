package com.smart.rinoiot.common.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.listener.DownloadFileListener;

import net.lingala.zip4j.ZipFile;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.zip.ZipException;

import okhttp3.ResponseBody;

/**
 * 文件操作工具类
 *
 * @author xuexiang
 * @since 2020/6/6 11:52 AM
 */
public final class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * 只读模式
     */
    public static final String MODE_READ_ONLY = "r";

    private static final String EXT_STORAGE_PATH = getExtStoragePath();

    private static final String EXT_STORAGE_DIR = EXT_STORAGE_PATH + File.separator;

    private static final String APP_EXT_STORAGE_PATH = EXT_STORAGE_DIR + "Android";

    private static final String EXT_DOWNLOADS_PATH = getExtDownloadsPath();

    private static final String EXT_PICTURES_PATH = getExtPicturesPath();

    private static final String EXT_DCIM_PATH = getExtDCIMPath();

    private FileUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    @Nullable
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExists(file.getAbsolutePath());
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(final String filePath) {
        File file = getFileByPath(filePath);
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExistsApi29(filePath);
    }

    /**
     * Android 10判断文件是否存在的方法
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    private static boolean isFileExistsApi29(String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AssetFileDescriptor afd = null;
            try {
                Uri uri = Uri.parse(filePath);
                afd = openAssetFileDescriptor(uri);
                if (afd == null) {
                    return false;
                } else {
                    closeIOQuietly(afd);
                }
            } catch (FileNotFoundException e) {
                return false;
            } finally {
                closeIOQuietly(afd);
            }
            return true;
        }
        return false;
    }

    /**
     * 获取文件输入流
     *
     * @param file 文件
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream getFileInputStream(File file) throws FileNotFoundException {
        if (isScopedStorageMode()) {
            return getContentResolver().openInputStream(getUriByFile(file));
        } else {
            return new FileInputStream(file);
        }
    }


    /**
     * 根据文件获取uri
     *
     * @param file 文件
     * @return
     */
    public static Uri getUriByFile(final File file) {
        if (file == null) {
            return null;
        }
        if (isScopedStorageMode() && isPublicPath(file)) {
            String filePath = file.getAbsolutePath();
            if (filePath.startsWith(EXT_DOWNLOADS_PATH)) {
                return getDownloadContentUri(BaseApplication.getApplication(), file);
            } else if (filePath.startsWith(EXT_PICTURES_PATH) || filePath.startsWith(EXT_DCIM_PATH)) {
                return getMediaContentUri(BaseApplication.getApplication(), file);
            } else {
                return getUriForFile(file);
            }
        } else {
            return getUriForFile(file);
        }
    }


    /**
     * Return a content URI for a given file.
     *
     * @param file The file.
     * @return a content URI for a given file
     */
    @Nullable
    public static Uri getUriForFile(final File file) {
        if (file == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = BaseApplication.getApplication().getPackageName() + ".updateFileProvider";
            return FileProvider.getUriForFile(BaseApplication.getApplication(), authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 是否是分区存储模式：在公共目录下file的api无效了
     *
     * @return 是否是分区存储模式
     */
    public static boolean isScopedStorageMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy();
    }

    /**
     * 将媒体文件转化为资源定位符
     *
     * @param context
     * @param mediaFile 媒体文件
     * @return
     */
    public static Uri getMediaContentUri(Context context, File mediaFile) {
        String filePath = mediaFile.getAbsolutePath();
        Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(baseUri,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (mediaFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(baseUri, values);
            }
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri getDownloadContentUri(Context context, File file) {
        String filePath = file.getAbsolutePath();
        Uri baseUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(baseUri,
                new String[]{MediaStore.Downloads._ID}, MediaStore.Downloads.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.DownloadColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DATA, filePath);
                return context.getContentResolver().insert(baseUri, values);
            }
            return null;
        }
    }

    /**
     * 是否是私有目录
     *
     * <pre>path: /data/data/package/</pre>
     * <pre>path: /storage/emulated/0/Android/data/package/</pre>
     *
     * @param path 需要判断的目录
     * @return 是否是私有目录
     */
    public static boolean isPrivatePath(@NonNull Context context, @NonNull String path) {
        if (isSpace(path)) {
            return false;
        }
        String appIntPath = getAppIntPath(context);
        String appExtPath = getAppExtPath(context);
        return (!TextUtils.isEmpty(appIntPath) && path.startsWith(appIntPath))
                || (!TextUtils.isEmpty(appExtPath) && path.startsWith(appExtPath));
    }

    /**
     * 是否是公有目录
     *
     * @return 是否是公有目录
     */
    public static boolean isPublicPath(File file) {
        if (file == null) {
            return false;
        }
        try {
            return isPublicPath(file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否是公有目录
     *
     * @return 是否是公有目录
     */
    public static boolean isPublicPath(String filePath) {
        if (isSpace(filePath)) {
            return false;
        }
        return filePath.startsWith(EXT_STORAGE_PATH) && !filePath.startsWith(APP_EXT_STORAGE_PATH);
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 安静关闭 IO
     *
     * @param closeables closeables
     */
    public static void closeIOQuietly(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 从uri资源符中读取文件描述
     *
     * @param uri 文本资源符
     * @return AssetFileDescriptor
     */
    public static AssetFileDescriptor openAssetFileDescriptor(Uri uri) throws FileNotFoundException {
        return getContentResolver().openAssetFileDescriptor(uri, MODE_READ_ONLY);
    }

    private static ContentResolver getContentResolver() {
        return BaseApplication.getApplication().getContentResolver();
    }

    /**
     * 获取 Android 外置储存的根目录
     * <pre>path: /storage/emulated/0</pre>
     *
     * @return 外置储存根目录
     */
    public static String getExtStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取下载目录
     * <pre>path: /storage/emulated/0/Download</pre>
     *
     * @return 下载目录
     */
    public static String getExtDownloadsPath() {
        return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
    }

    /**
     * 获取图片目录
     * <pre>path: /storage/emulated/0/Pictures</pre>
     *
     * @return 图片目录
     */
    public static String getExtPicturesPath() {
        return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath();
    }

    /**
     * 获取相机拍摄的照片和视频的目录
     * <pre>path: /storage/emulated/0/DCIM</pre>
     *
     * @return 照片和视频目录
     */
    public static String getExtDCIMPath() {
        return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .getAbsolutePath();
    }

    /**
     * 获取此应用的私有存储目录
     * <pre>path: /data/data/package/</pre>
     *
     * @return 此应用的缓存目录
     */
    public static String getAppIntPath(@NonNull Context context) {
        File appIntCacheFile = context.getCacheDir();
        if (appIntCacheFile != null) {
            String appIntCachePath = appIntCacheFile.getAbsolutePath();
            return getDirName(appIntCachePath);
        }
        return null;
    }

    /**
     * 获取此应用在外置储存中的私有存储目录
     * <pre>path: /storage/emulated/0/Android/data/package/</pre>
     *
     * @return 此应用在外置储存中的缓存目录
     */
    public static String getAppExtPath(@NonNull Context context) {
        File appExtCacheFile = context.getExternalCacheDir();
        if (appExtCacheFile != null) {
            String appExtCachePath = appExtCacheFile.getAbsolutePath();
            return getDirName(appExtCachePath);
        }
        return null;
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param filePath 文件路径
     * @return filePath 最长目录
     */
    public static String getDirName(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? "" : filePath.substring(0, lastSep + 1);
    }

    /**
     * 获取文件大小
     *
     * @param path 文件路径
     */
    public static long getFileSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    /**
     * 获取文件夹总大小
     *
     * @param file 文件夹路径
     * @return 文件夹大小
     */
    public static long getFileSize(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getFileSize(child);
        return total;
    }

    /**
     * 格式化大小
     *
     * @param size 文件或文件夹总大小
     * @return 如：333.33MB
     */
    public static String formatFileSize(long size) {
        String unit = "B";
        float len = size;
        if (len > 900) {
            len /= 1024f;
            unit = "KB";
        }
        if (len > 900) {
            len /= 1024f;
            unit = "MB";
        }
        if (len > 900) {
            len /= 1024f;
            unit = "GB";
        }
        if (len > 900) {
            len /= 1024f;
            unit = "TB";
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(len) + unit;
    }

    /**
     * 文件上上传限制50M
     *
     * @param path    文件路径
     * @param context 上下文
     * @return true:文件超过50M；false：未超过
     */
    public static boolean fileLimit(String path, Context context) {
        long fileSize = getFileSize(path);
        return fileSize > 50 * 1024 * 1024;
    }

    /**
     * 获取指定文件大小
     *
     * @param file 文件
     * @return
     * @throws Exception
     */
    public static long getFileTotalSize(File file) {
        long size = 0;
        try {
            size = 0;
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 将bytes写到path这个目录中的fileName文件上
     * path: /data/data/package/files
     */
    public static void writeFileFromResponseBody(String fileName, ResponseBody body, DownloadFileListener callback) {
        String path = BaseApplication.getApplication().getApplicationContext().getFilesDir().getPath();
        File file;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        if (callback != null) callback.onStart();
        try {
            file = new File(path + File.separator + fileName);
            if (isFileExists(file)) {
                file.delete();
            }
            file.createNewFile();

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
                if (callback != null)
                    callback.onProgress((int) (100 * fileSizeDownloaded / fileSize));
                LgUtils.d("file download: " + fileSizeDownloaded + " of " + fileSize);
            }

            if (callback != null)
                callback.onFinish(file.getPath());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeIOQuietly(outputStream, inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断path这个目录中的fileName文件是否存在
     * path: /data/data/package/files
     */
    public static boolean isFileExistsForFilesDir(final String fileName) {
        String path = BaseApplication.getApplication().getApplicationContext().getFilesDir().getPath();
        File file = getFileByPath(path + File.separator + fileName);
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExists(file.getAbsolutePath());
    }

    /**
     * zip4j解压
     *
     * @param zipFileName 待解压的zip文件（目录）名称
     * @param password    密码
     * @param zipDelete   zip包是否删除
     * @return 状态返回值
     */
    public static boolean uncompressZip4j(String zipFileName, String password, boolean zipDelete) {
        String path = BaseApplication.getApplication().getApplicationContext().getFilesDir().getPath();
        File zipFile_ = new File(path + File.separator + zipFileName);
        File sourceFile = new File(path + File.separator + zipFileName.replace(".zip", ""));

        try {
            ZipFile zipFile = new ZipFile(zipFile_);
            if (!zipFile.isValidZipFile()) {   //检查输入的zip文件是否是有效的zip文件
                throw new ZipException("压缩文件不合法,可能被损坏.");
            }
            if (sourceFile.isDirectory() && !sourceFile.exists()) {
                sourceFile.mkdir();
            }
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(sourceFile.getPath()); //解压
            if (zipDelete) {
                zipFile_.delete();
            }
            return true;
        } catch (ZipException e) {
            ToastUtil.showMsg("解压失败" + e.getMessage());
            return false;
        } catch (IOException e) {
            ToastUtil.showMsg("解压失败" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除OTA升级包
     */
    public static void deleteOTAFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 将内容写入/data/data/com.xxx.xxx/cache
     *
     * @param content 待写入的内容
     * @throws IOException
     */
    public static void writeDataCenter(Context context, String content, String fileName) {
        //路径： data/data/com.xxx.xxx/cache/dataCenter
        try {
            String filename = context.getExternalCacheDir().getAbsolutePath() + File.separator + fileName;
            File fileDataCenter = new File(filename);
            if (!fileDataCenter.exists()) {
                fileDataCenter.createNewFile();
            }
            FileWriter fileWriter;
            fileWriter = new FileWriter(fileDataCenter, false);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            LgUtils.w("FileUtils   文件写入失败  e=" + e.getMessage());
        }
    }

    /**
     * 从私有文件夹中数读取据：data/data/包名/caches/
     *
     * @param fileName data/data/包名/caches/下的文件
     * @return String内容
     * 代码与readStringFromFilesDir完全一样
     */
    public static String readDataCenter(Context context, String fileName) {
        String filename = context.getExternalCacheDir().getAbsolutePath() + File.separator + fileName;
        File fileDataCenter = new File(filename);
        if (!fileDataCenter.exists()) return "";
        try {
            FileInputStream fis = new FileInputStream(fileDataCenter);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder content = new StringBuilder();
            String tmp;
            while ((tmp = br.readLine()) != null) {
                content.append(tmp);
            }
            br.close();
            fis.close();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LgUtils.w("FileUtils   文件读取失败  e=" + e.getMessage());
            return "";
        }
    }

    /**
     * 读取asset目录下文件。
     *
     * @param mContext the context
     * @param file the file
     * @param code the code
     * @return the contents of the read file.
     */
    public static String readFile(Context mContext, String file, String code) {
        int len;
        byte[] buf;
        String result = "";
        try {
            AssetManager manager = mContext.getAssets();
            InputStream is = manager.open(file);
            len = is.available();
            buf = new byte[len];
            int status = is.read(buf, 0, len);
            Log.d(TAG, "readFile: read file status=" + status);
            result = new String(buf, code);
            manager.close();
        } catch (Exception e) {
            Log.e(TAG, "readFile: Failed to read file. Cause=["+e.getLocalizedMessage()+"]");
        }
        return result;
    }
}
