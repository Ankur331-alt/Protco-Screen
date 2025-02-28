package com.smart.rinoiot.common.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.smart.rinoiot.common.Constant;

import java.io.File;
import java.math.BigDecimal;

@SuppressLint("SdCardPath")
public class DataCleanManager {

    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     */
    public static void cleanInternalCache(Context context) {
        deleteFolderFile(context.getCacheDir().getAbsolutePath(), false);
//        return deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * * 按名字清除本应用数据库 * *
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * * 清除/data/data/com.xxx.xxx/files下的内容 * *
     */
    public static void cleanFiles(Context context) {
        deleteFolderFile(context.getFilesDir().getAbsolutePath(), false);
//        return deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFolderFileCache(context.getExternalCacheDir().getAbsolutePath(), false);
//            return deleteFilesByDirectoryCache(context.getExternalCacheDir());
        }
//        return false;
    }

    /**
     * * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * *
     *
     * @param filePath 文件路径
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * * 清除本应用所有的数据 * *
     *
     * @param filepath 文件路径
     */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
//        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        if (filepath == null) {
            return;
        }
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }

    /**
     * * 清除本应用缓存的数据 * *
     */
    public static void cleanCacheData(Context context) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanFiles(context);
//        return cleanInternalCache(context) && cleanExternalCache(context) && cleanFiles(context);
    }

    /**
     * 计算internalCache、externalCache和filesCache
     */
    public static long getAllSize(Context context) {
        long internalCache = getFolderSize(context.getCacheDir()) + getFolderSize(context.getFilesDir());
        long externalCache = 0;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            externalCache = getFolderSize(context.getExternalCacheDir());
        }
//        long filesCache = getFolderSize(context.getFilesDir());
        return internalCache + externalCache;
    }

    /**
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
     */
    private static boolean deleteFilesByDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteFilesByDirectory(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
//        return dir.getAbsolutePath().contains(Constant.DATA_CENTER_FILE_NAME) || dir.delete();
        return dir.delete();
    }

    /**
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
     */
    private static boolean deleteFilesByDirectoryCache(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteFilesByDirectoryCache(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.getAbsolutePath().contains(Constant.DATA_CENTER_FILE_NAME) || dir.delete();
    }


    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File value : fileList) {
                // 如果下面还有文件
                if (value.isDirectory()) {
                    size = size + getFolderSize(value);
                } else {
                    size = size + value.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录  过滤某个文件
     *
     * @param deleteThisPath 文件路径
     */
    public static void deleteFolderFileCache(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (File value : files) {
                        if (value.getAbsolutePath().contains(Constant.DATA_CENTER_FILE_NAME)) {
                            continue;
                        }
                        deleteFolderFile(value.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath 文件路径
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (File value : files) {
                        deleteFolderFile(value.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size 大小
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        /*if (kiloByte < 1) {
            return size + "Byte";
        }*/

        double megaByte = kiloByte / 1024;
        /*if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }*/

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "G";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "T";
    }

    public static String getCacheSize(File file) {
        return getFormatSize(getFolderSize(file));
    }
}
