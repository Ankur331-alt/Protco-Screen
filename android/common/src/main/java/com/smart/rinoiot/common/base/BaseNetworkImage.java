package com.smart.rinoiot.common.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.smart.rinoiot.common.listener.ImageLoadListener;

/**
 * @Package: com.wq.lib.base.api
 * @ClassName: BaseNetworkImage.java
 * @Description: ImageView图片加载显示基类
 * @Author: xf
 * @CreateDate: 2020/3/29 17:51
 * @UpdateUser: 更新者：xf
 * @UpdateDate: 2020/3/29 17:51
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public abstract class BaseNetworkImage<T> {
    /**
     * 加载显示网络图片
     *
     * @param url       图片地址
     * @param imageView 显示图片控件
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindImageUrl(String url, ImageView imageView);

    /**
     * 加载显示网络图片
     *
     * @param url          图片地址
     * @param imageView    显示图片控件
     * @param placeHoldRes 显示图片前默认图片
     * @param errorHoldRes 显示图片出错的图片
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindImageUrl(String url, ImageView imageView, int placeHoldRes, int errorHoldRes);

    /**
     * 显示本地图片
     *
     * @param res       图片资源id
     * @param imageView 显示图片控件
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindImageResource(int res, ImageView imageView);

    /**
     * 加载网络图片并以圆图显示
     *
     * @param url       图片地址
     * @param imageView 显示图片控件
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindCircleImageUrl(String url, ImageView imageView);
    /**
     * 加载网络图片并以圆图显示
     *
     * @param url       图片地址
     * @param imageView 显示图片控件
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindCircleImageUrl(String url, ImageView imageView,int placeHolder);

    /**
     * 加载网络图片并以圆角[round]显示
     *
     * @param url       图片地址
     * @param imageView 显示图片控件
     * @param round     显示图片圆角角度
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindRoundImageUrl(String url, ImageView imageView, int round, int placeHolder);

    /**
     * 加载网络图片并以圆角[round]显示
     *
     * @param url       图片地址
     * @param imageView 显示图片控件
     * @param round     显示图片圆角角度
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindRoundImageUrl(String url, ImageView imageView, int round);

    /**
     * 下载图片bitmap
     *
     * @param url      图片地址
     * @param context  context
     * @param listener 下载监听
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void loadImageToBitmap(String url, Context context, @NonNull ImageLoadListener<Bitmap> listener);

    /**
     * 下载图片Drawable
     *
     * @param url      图片地址
     * @param context  context
     * @param listener 下载监听
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void loadImageToDrawable(String url, Context context, @NonNull ImageLoadListener<Drawable> listener);

    /**
     * 加载适配第一帧
     *
     * @param url       图片地址
     * @param imageView 显示图片控件
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindVideo(String url, ImageView imageView);

    /**
     * 加载网络图片并以圆角[round]显示 左上右下
     *
     * @param url       图片地址
     * @param imageView 显示图片控件
     * @param round     显示图片圆角角度
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindRoundImageUrlLTRB(String url, ImageView imageView, int round, int placeHolder);

    /**
     * 加载网络图片并以圆角[round]显示 左上右下
     *
     * @param url       图片地址
     * @param imageView 显示图片控件
     * @param round     显示图片圆角角度
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindRoundImageUrlLTRB(String url, ImageView imageView,int type, int round, int placeHolder);

    /**
     * 显示bitmap
     *
     * @param bitmap    图片
     * @param imageView 显示图片控件
     * @return void
     * @method bindImageUrl
     * @date: 2020/3/29 18:01
     * @author: xf
     */
    public abstract void bindImageBitmap(Bitmap bitmap, ImageView imageView, int round);

}
