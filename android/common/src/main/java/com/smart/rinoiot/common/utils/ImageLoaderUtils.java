package com.smart.rinoiot.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.base.BaseNetworkImage;
import com.smart.rinoiot.common.listener.ImageLoadListener;

import org.jetbrains.annotations.NotNull;

import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

/**
 * 默认使用glide加载图片
 *
 * @Package: com.wq.lib.base.api.impl
 * @ClassName: DefaultImageLoadImpl.java
 * @Description: 默认图片加载器
 * @Author: xf
 * @CreateDate: 2020/3/29 17:43
 * @UpdateUser: 更新者：xf
 * @UpdateDate: 2020/3/29 17:43
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ImageLoaderUtils extends BaseNetworkImage {
    private RequestOptions options = new RequestOptions();
    private static ImageLoaderUtils instance;

    public static ImageLoaderUtils getInstance() {
        if (instance == null) {
            return new ImageLoaderUtils();
        }
        return instance;
    }

    private ImageLoaderUtils() {
        options = options.encodeFormat(Bitmap.CompressFormat.WEBP);
    }

    @Override
    public void bindImageUrl(String url, ImageView imageView) {
        glideWith(imageView, url).apply(options).into(imageView);
    }

    @Override
    public void bindImageUrl(String url, ImageView imageView, int placeHoldRes, int errorHoldRes) {
        glideWith(imageView, url).apply(options).placeholder(placeHoldRes).error(errorHoldRes).into(imageView);
    }

    @Override
    public void bindImageResource(int res, ImageView imageView) {
        glideWith(imageView, res).apply(options).into(imageView);

    }

    @SuppressLint("CheckResult")
    public void bindGifImageUrl(String url, ImageView imageView) {
        Glide.with(imageView.getContext()).asGif().load(url)
                .skipMemoryCache(false)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(imageView.getDrawable());
    }

    @Override
    public void bindCircleImageUrl(String url, ImageView imageView) {
        //原来的逻辑，刷新时图片会闪烁 以及错位
//        glideWith(imageView, url).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);

        RequestOptions requestOptions = RequestOptions.circleCropTransform();
        Glide.with(imageView.getContext()).load(url).apply(requestOptions).into(imageView);
    }

    @Override
    public void bindCircleImageUrl(String url, ImageView imageView, int placeHolder) {
        glideWith(imageView, url).apply(RequestOptions.bitmapTransform(new CircleCrop()).error(placeHolder).placeholder(placeHolder)).into(imageView);
    }

    @Override
    public void bindRoundImageUrl(String url, ImageView imageView, int round, int placeHolder) {
//        glideWith(imageView, url).apply(RequestOptions.bitmapTransform(options).error(placeHolder).placeholder(placeHolder)).into();
        Glide.with(imageView.getContext()).asBitmap().load(url).apply(new RequestOptions().transform(new CenterCrop()))
                .placeholder(placeHolder).error(placeHolder).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                super.setResource(resource);
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(imageView.getContext().getResources(), resource);
                roundedBitmapDrawable.setCornerRadius(round);
                imageView.setImageDrawable(roundedBitmapDrawable);
            }
        });
    }

    @Override
    public void bindRoundImageUrl(String url, ImageView imageView, int round) {
        glideWith(imageView, url).apply(RequestOptions.bitmapTransform(new RoundedCorners(round))).into(imageView);
    }

    public void bindRoundImageUrl(String url, int width, int height, ImageView imageView, int round) {
        MultiTransformation multiTransformation = new MultiTransformation(new CropTransformation(width, height, CropTransformation.CropType.CENTER), new GranularRoundedCorners(round, round, round, round));
        glideWith(imageView, url).apply(RequestOptions.bitmapTransform(multiTransformation)).into(imageView);
    }


    @SuppressLint("CheckResult")
    @Override
    public void loadImageToBitmap(String url, Context context, @NonNull final ImageLoadListener listener) {
        Glide.with(context).asBitmap().load(url).addListener(new RequestListener<Bitmap>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                listener.onError(e);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                listener.onSuccess(resource);
                return false;
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadImageToDrawable(String url, Context context, @NonNull final ImageLoadListener listener) {
        Glide.with(context).load(url).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                listener.onError(e);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                listener.onSuccess(resource);
                return false;
            }
        });
    }

    public void loadImageToDrawable(String url, Context context, int round, int placeHolder, @NonNull final ImageLoadListener listener) {
        MultiTransformation multiTransformation = new MultiTransformation(new ColorFilterTransformation(0x4c000000), new GranularRoundedCorners(round, round, round, round));
        Glide.with(context).load(url).apply(RequestOptions.bitmapTransform(multiTransformation))

                .error(placeHolder).placeholder(placeHolder).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                listener.onSuccess(resource);
            }

            @Override
            public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                listener.onError(null);
            }
        });
    }

    @Override
    public void bindVideo(String url, ImageView imageView) {
//        glideWith(imageView, url).apply(options.frame(1)
//                .centerCrop().error(R.drawable.icon_placeholder)//可以忽略
//                .placeholder(R.drawable.icon_placeholder)).into(imageView);


        RequestOptions requestOptions = RequestOptions.noTransformation();
        Glide.with(imageView.getContext()).load(url).apply(requestOptions).placeholder(R.drawable.icon_placeholder).into(imageView);
    }

    public void bindVideo(String url, ImageView imageView, int round) {
        MultiTransformation multiTransformation = new MultiTransformation(new RoundedCorners(round));
        RequestOptions requestOptions = RequestOptions.bitmapTransform(multiTransformation);
        Glide.with(imageView.getContext()).load(url).apply(requestOptions).placeholder(R.drawable.icon_placeholder).into(imageView);
    }

    public void bindVideo(String url, int width, int height, ImageView imageView, int round) {
        MultiTransformation multiTransformation = new MultiTransformation(new CropTransformation(width, height, CropTransformation.CropType.CENTER), new GranularRoundedCorners(round, round, round, round));
        RequestOptions requestOptions = RequestOptions.bitmapTransform(multiTransformation);
        Glide.with(imageView.getContext()).load(url).apply(requestOptions).placeholder(R.drawable.icon_placeholder).into(imageView);
    }

    @Override
    public void bindRoundImageUrlLTRB(String url, ImageView imageView, int round, int placeHolder) {

        MultiTransformation multiTransformation = new MultiTransformation(new GranularRoundedCorners(0, 0, round, round));
//        CornerTransform cornerTransform = new CornerTransform(imageView.getContext(), round);
//        cornerTransform.setExceptCorner(false, false, true, true);
        glideWith(imageView, url).apply(RequestOptions.bitmapTransform(multiTransformation)
                .error(placeHolder).placeholder(placeHolder)
                .skipMemoryCache(true)).into(imageView);
    }

    @Override
    public void bindImageBitmap(Bitmap bitmap, ImageView imageView, int round) {
        glideWith(imageView, bitmap).apply(RequestOptions.bitmapTransform(new RoundedCorners(round))).into(imageView);
    }

    @Override
    public void bindRoundImageUrlLTRB(String url, ImageView imageView, int type, int round, int placeHolder) {
        MultiTransformation multiTransformation = null;
        if (type == 3) {
            multiTransformation = new MultiTransformation(new GranularRoundedCorners(0, 0, round, 0));
        } else {
            multiTransformation = new MultiTransformation(new GranularRoundedCorners(round, round, round, round));
        }
        glideWith(imageView, url).apply(RequestOptions.bitmapTransform(multiTransformation)
                .error(placeHolder)).into(imageView);
    }

    private RequestBuilder<Drawable> glideWith(ImageView imageView, Bitmap bitmap) {
        return Glide.with(imageView.getContext()).load(bitmap)
                .skipMemoryCache(false)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(imageView.getDrawable());
    }

    private RequestBuilder<Drawable> glideWith(ImageView imageView, String url) {
        return Glide.with(imageView.getContext()).load(url)
                .skipMemoryCache(false)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(imageView.getDrawable());
    }

    private RequestBuilder<Drawable> glideWith(ImageView imageView, int res) {
        return Glide.with(imageView.getContext()).load(res)
                .skipMemoryCache(false)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(imageView.getDrawable());
    }
}
