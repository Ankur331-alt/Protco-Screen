package com.smart.rinoiot.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * * @ProjectName: XunfeiAndroid
 *
 * @Package: com.znkit.smart.utils
 * @ClassName: SystemCameraUtil
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2021/3/11 9:17 AM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/3/11 9:17 AM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class SystemCameraManager {
    public static final int RC_CAMERA = 1000;
    /**
     * 相册请求码
     */
    public static final int ALBUM_REQUEST_CODE = 1;
    /**
     * 相机请求码
     */
    public static final int CAMERA_REQUEST_CODE = 2;
    /**
     * 剪裁请求码
     */

    public static final int CROP_REQUEST_CODE = 3;

    /**
     * 视频
     */
    public static final int VIDEO_REQUEST_CODE = 4;
    private File tempFile;
    private String prefix = "error_image";

    /**
     * 去相册获取图片
     *
     * @param object 当前activity
     * @return void
     * @method getPicFromAlbum
     * @date: 2020/12/17 17:34
     * @author: xf
     */
    public void getPicFromAlbum(Object object) {
        prefix = prefix + System.currentTimeMillis();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        photoPickerIntent.setType("image/*");
        photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (object instanceof Activity) {
            ((Activity)object).startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
        }else  if (object instanceof Fragment) {
            ((Fragment)object).startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
        }
    }

    /**
     * 去相册获取图片,多选
     *
     * @param object 当前activity或者fragment
     * @return void
     * @method getPicFromAlbum
     * @date: 2020/12/17 17:34
     * @author: xf
     */
    public void getPicFromAlbum(Object object, int maxSize, int resultCode) {
        if (object instanceof Activity) {
            PictureSelector.create((Activity)object).openGallery(PictureMimeType.ofImage())
                    .imageEngine(GlideEngine.createGlideEngine())
                    .maxSelectNum(maxSize)// 最大图片选择数量 int
                    .minSelectNum(1)
                    .forResult(resultCode);
        }else  if (object instanceof Fragment) {
            PictureSelector.create((Fragment)object).openGallery(PictureMimeType.ofImage())
//                    .imageEngine(GlideEngine.createGlideEngine())
                    .maxSelectNum(maxSize)// 最大图片选择数量 int
                    .minSelectNum(1)
                    .forResult(resultCode);
        }
    }


    /**
     * 去相册获取视频
     *
     * @param object 当前activity
     * @return void
     * @method getPicFromAlbum
     * @date: 2020/12/17 17:34
     * @author: xf
     */
    public void getPicFromVideo(Object object) {
        prefix = prefix + System.currentTimeMillis();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        photoPickerIntent.setType("image/*");
        photoPickerIntent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
        if (object instanceof Activity) {
            ((Activity)object).startActivityForResult(photoPickerIntent, VIDEO_REQUEST_CODE);
        }else  if (object instanceof Fragment) {
            ((Fragment)object).startActivityForResult(photoPickerIntent, VIDEO_REQUEST_CODE);
        }
    }

    public File getTempFile() {
        return tempFile;
    }

    /**
     * 调系统拍照
     *
     * @param object 当前activity
     * @return void
     * @method getPicFromCamera
     * @date: 2020/12/17 17:42
     * @author: xf
     */
    public void getPicFromCamera(Object object) {
        prefix = prefix + System.currentTimeMillis();
        //用于保存调用相机拍照后所生成的文件
        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //判断版本
        if (object instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //如果在Android7.0以上,使用FileProvider获取Uri
                Uri contentUri = FileProvider.getUriForFile((Activity)object, ((Activity)object).getPackageName(), tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            } else {    //否则使用Uri.fromFile(file)方法获取Uri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            }
            ((Activity)object).startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }else if (object instanceof Fragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //如果在Android7.0以上,使用FileProvider获取Uri
                Uri contentUri = FileProvider.getUriForFile(((Fragment) object).getContext(), ((Fragment)object).getContext().getPackageName(), tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            } else {    //否则使用Uri.fromFile(file)方法获取Uri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            }
            ((Fragment)object).startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    /**
     * 调系统拍照
     *
     * @param object 当前activity
     * @return void
     * @method getPicFromCamera.
     * @date: 2020/12/17 17:42
     * @author: xf
     */
    public void getPicFromCamera(Object object, OnResultCallbackListener<LocalMedia> resultCallbackListener) {
        if (object instanceof Activity) {
            PictureSelector.create((Activity) object).openCamera(PictureMimeType.ofImage())
//                .isEnableCrop(isCrop).cropImageWideHigh(200, 200).circleDimmedLayer(isCrop)
                    .forResult(resultCallbackListener);
        } else {
            PictureSelector.create((Fragment) object)
                    .openCamera(PictureMimeType.ofImage())
//                .isEnableCrop(isCrop).cropImageWideHigh(200, 200).circleDimmedLayer(isCrop)
                    .forResult(resultCallbackListener);
        }
    }

    /**
     * 启动系统裁剪工具
     *
     * @param fromCamera 是否来自拍照
     * @param object   当前页面activity
     * @return void
     * @method crop
     * @date: 2020/12/17 17:41
     * @author: xf
     */
    public void crop(Object object, Uri uri, boolean fromCamera) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        if (fromCamera) {
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 16);
        cropIntent.putExtra("aspectY", 9);
        cropIntent.putExtra("outputX", 200);
        cropIntent.putExtra("outputY", 200);
        cropIntent.putExtra("noFaceDetection", true);
        cropIntent.putExtra("return-data", false);
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        File dir = new File(Environment.getExternalStorageDirectory().getPath(), prefix);
        if (!dir.exists() && dir.mkdirs()) ;
        File fileCrop = new File(dir, prefix + ".jpeg");
        if (!fileCrop.exists()) {
            try {
                fileCrop.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri cropImageUri = Uri.fromFile(fileCrop);
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        if (object instanceof Activity) {
            ((Activity)object).startActivityForResult(cropIntent, CROP_REQUEST_CODE);
        }else  if (object instanceof Fragment) {
            ((Fragment)object).startActivityForResult(cropIntent, CROP_REQUEST_CODE);
        }
    }

    /**
     * 调用摄像头回调
     *
     * @param object
     * @param resultCode 返回码
     * @return void
     * @method onCamera
     * @date: 2020/12/17 17:51
     * @author: xf
     */
    public void onCamera(Object object, int resultCode) {
        if (resultCode == RESULT_OK) {
                //用相机返回的照片去调用剪裁也需要对Uri进行处理
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri=null;
                    if (object instanceof Activity) {
                        contentUri = FileProvider.getUriForFile((Activity)object, ((Activity)object).getPackageName(), tempFile);
                    } else if (object instanceof Fragment) {
                        contentUri = FileProvider.getUriForFile(((Fragment) object).getContext(), ((Fragment)object).getContext().getPackageName(), tempFile);
                    }
                    crop(object, contentUri, true);
                } else {
                    crop(object, Uri.fromFile(tempFile), true);
                }
        }
    }

    /**
     * 调用摄像头回调
     *
     * @param activity
     * @param resultCode 返回码
     * @return void
     * @method onCamera
     * @date: 2020/12/17 17:51
     * @author: xf
     */
    public String onAddCamera(Activity activity, int resultCode) {
        if (resultCode == RESULT_OK) {
            //用相机返回的照片去调用剪裁也需要对Uri进行处理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(activity, activity.getPackageName(), tempFile);
            } else {
                Uri.fromFile(tempFile);
            }
            return tempFile == null ? "" : tempFile.getPath();
        }
        return "";
    }

    /**
     * 系统截图回调
     *
     * @param resultCode 返回码
     * @return void
     * @method onCrop
     * @date: 2020/12/17 17:52
     * @author: xf
     */
    public File onCrop(int resultCode) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath(), prefix);
        if (resultCode == RESULT_OK) {
            File fileCrop = new File(dir, prefix + ".jpeg");
            return fileCrop;
        }
        return dir;
    }
    /**
     * 去相册获取图片,多选
     *
     * @param object 当前activity或者fragment
     * @return void
     * @method getPicFromAlbum
     * @date: 2020/12/17 17:34
     * @author: xf
     */
    public void getPicFromAlbum(Object object, int maxSize, OnResultCallbackListener<LocalMedia> listener) {
        if (object instanceof Activity) {
            PictureSelector.create((Activity) object).openGallery(PictureMimeType.ofImage())
                    .imageEngine(GlideEngine.createGlideEngine())
                    .maxSelectNum(maxSize)// 最大图片选择数量 int
                    .minSelectNum(1)
                    .forResult(listener);
        } else if (object instanceof Fragment) {
            PictureSelector.create((Fragment) object).openGallery(PictureMimeType.ofImage())
//                    .imageEngine(GlideEngine.createGlideEngine())
                    .maxSelectNum(maxSize)// 最大图片选择数量 int
                    .minSelectNum(1)
                    .forResult(listener);
        }
    }
}
