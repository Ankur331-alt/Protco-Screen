package com.smart.rinoiot.center.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ScreenUtils;
import com.smart.rinoiot.user.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * ipc 适配器
 */
public class IpcCameraAdapter extends BaseQuickAdapter<DeviceInfoBean, BaseViewHolder> {

    private static final int VERTICAL_MARGIN_DP = 20;
    private static final int HORIZONTAL_MARGIN_DP = 40;
    private static final int NAVIGATION_TAB_WIDTH_DP = 118;
    /**
     * Note: the card view also have a margin bottom of 20dp set in the layout.xml
     */
    private static final int ITEM_VERTICAL_MARGIN_END_DP = 4;
    private static final int ITEM_HORIZONTAL_MARGIN_END_DP = 20;


    public IpcCameraAdapter() {
        super(R.layout.item_ip_camera);
        addChildClickViewIds(R.id.tvEnterPanel,R.id.ivIpcPause);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, DeviceInfoBean deviceInfo) {
        if (deviceInfo == null) return;
        ConstraintLayout.LayoutParams layoutParams = getLayoutParams(getContext());
        baseViewHolder.getView(R.id.llItem).setLayoutParams(layoutParams);
        baseViewHolder.setText(R.id.tv_camera_name, deviceInfo.getName());
        bindPlaceholder(baseViewHolder, deviceInfo);
        baseViewHolder.setText(R.id.tv_camera_name, deviceInfo.getName());
        updatePlace(baseViewHolder.getView(R.id.iv_ipc_placeholder), deviceInfo.getId());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, DeviceInfoBean item, @NonNull List<?> payloads) {
        if (payloads.isEmpty()) {
            convert(holder, item);
        } else {
            updateSingle(holder,item);
        }
    }

    /**
     * Binds the placeholder view
     */
    public void bindPlaceholder(BaseViewHolder baseViewHolder, DeviceInfoBean deviceInfo) {
        if (TextUtils.isEmpty(deviceInfo.getId())) {
            baseViewHolder.setGone(R.id.ll_ipc_placeholder, false);
        } else {
            baseViewHolder.setGone(R.id.ll_ipc_placeholder, true);
            baseViewHolder.setGone(R.id.iv_ipc_placeholder, false);
            baseViewHolder.setGone(R.id.ivIpcPlay, false);
            baseViewHolder.setGone(R.id.ipc_cv_video_stream, true);
            baseViewHolder.setGone(R.id.ll_camera_name, false);
            baseViewHolder.setGone(R.id.tvEnterPanel, false);
        }
        baseViewHolder.setGone(R.id.ivIpcPause, true);
    }

    /**
     * 刷新单个ipc设备数据
     */
    public void updateSingle(BaseViewHolder baseViewHolder,DeviceInfoBean item) {
        baseViewHolder.setGone(R.id.ivIpcPlay, item.isClick());
        baseViewHolder.setGone(R.id.iv_ipc_placeholder, true);
        baseViewHolder.setGone(R.id.ipc_cv_video_stream, false);
        baseViewHolder.setGone(R.id.ivIpcPause, !item.isClick());

    }

    /**
     * 根据是否有第一帧，显示第一帧图片
     */
    private void updatePlace(ImageView ivIpcPlaceholder, String devId) {
        ivIpcPlaceholder.setImageResource(R.drawable.icon_ipc_place_holder);
        File dirFile = new File(getContext().getExternalCacheDir().getAbsolutePath() + "/ipc/center/" + devId);
        LgUtils.w("3333333333333  updatePlace    dirFile=" + dirFile.exists());
        if (dirFile.exists()) {
            File[] tempList = dirFile.listFiles();
            if (tempList != null && tempList.length > 0) {
                LgUtils.w("3333333333333  updatePlace    tempList=" + tempList.length+"    devId="+devId+"  tempList[0]="+tempList[0].getAbsolutePath());
                BlurTransformation transformation = new BlurTransformation(1, 10);
                Glide.with(getContext())
                        .load(tempList[0])
                        .skipMemoryCache(true)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.icon_ipc_place_holder)
                        .error(R.drawable.icon_ipc_place_holder)
                        .apply(RequestOptions.bitmapTransform(transformation))
                        .into(ivIpcPlaceholder);
            }
        }
    }

    /**
     * Generate layout params
     *
     * @return layout params
     */
    private ConstraintLayout.LayoutParams getLayoutParams(Context context) {
        int navTabWidth = ScreenUtils.dip2px(context, NAVIGATION_TAB_WIDTH_DP);
        int marginVertical = 2 * ScreenUtils.dip2px(context, VERTICAL_MARGIN_DP);
        int marginHorizontal = 2 * ScreenUtils.dip2px(context, HORIZONTAL_MARGIN_DP);
        int camerasRvHeight = ScreenUtils.getScreenHeight(context) - marginVertical;
        int camerasRvWidth = (ScreenUtils.getScreenWidth(context) - marginHorizontal - navTabWidth);

        int width = (camerasRvWidth - ScreenUtils.dip2px(
                context, ITEM_HORIZONTAL_MARGIN_END_DP
        )) / 2;
        int height = (camerasRvHeight - ScreenUtils.dip2px(
                context, ITEM_VERTICAL_MARGIN_END_DP
        )) / 2;
        return new ConstraintLayout.LayoutParams(width, height);
    }
}
