package com.smart.rinoiot.center.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.smart.rinoiot.center.adapter.IpCameraDashboardAdapter.IpCameraDashboardViewHolder;
import com.smart.rinoiot.center.adapter.listener.IpCameraDashboardAdapterInterface.ItemClickListener;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ScreenUtils;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ItemIpCameraBinding;

import java.io.File;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * @author edwin
 */
public class IpCameraDashboardAdapter extends RecyclerView.Adapter<IpCameraDashboardViewHolder> {

    private final Rect camerasRecycleViewRect;
    private final List<DeviceInfoBean> mCameras;
    private ItemClickListener mItemClickListener;
    private static final int VERTICAL_MARGIN_DP = 20;
    private static final int HORIZONTAL_MARGIN_DP = 40;
    private static final int NAVIGATION_TAB_WIDTH_DP = 118;
    /**
     * Note: the card view also have a margin bottom of 20dp set in the layout.xml
     */
    private static final int ITEM_VERTICAL_MARGIN_END_DP = 4;
    private static final int ITEM_HORIZONTAL_MARGIN_END_DP = 20;

    /**
     * The minimum number of cams that we should display.
     */
    private static final int MIN_ITEM_COUNT = 4;

    public IpCameraDashboardAdapter(Context context, List<DeviceInfoBean> cameras) {
        this.mCameras = cameras;
        this.camerasRecycleViewRect = calculateCanvasDimensions(context);
    }

    @NonNull
    @Override
    public IpCameraDashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIpCameraBinding binding = ItemIpCameraBinding.inflate(
                LayoutInflater.from(parent.getContext())
        );
        ViewGroup.LayoutParams layoutParams = getLayoutParams(parent.getContext());
        binding.getRoot().setLayoutParams(layoutParams);
        return new IpCameraDashboardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IpCameraDashboardViewHolder holder, int position) {
        if (position < mCameras.size()) {
            holder.bindData(mCameras.get(position));
        } else {
            holder.bindPlaceholder();
        }
    }

    @Override
    public int getItemCount() {
        return Math.max(mCameras.size(), MIN_ITEM_COUNT);
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    /**
     * Calculate the width and height for the cameras recycler view
     *
     * @param context the context
     * @return the width and height for the cameras recycler view
     */
    private Rect calculateCanvasDimensions(Context context) {
        int navTabWidth = ScreenUtils.dip2px(context, NAVIGATION_TAB_WIDTH_DP);
        int marginVertical = 2 * ScreenUtils.dip2px(context, VERTICAL_MARGIN_DP);
        int marginHorizontal = 2 * ScreenUtils.dip2px(context, HORIZONTAL_MARGIN_DP);
        int camerasRvHeight = ScreenUtils.getScreenHeight(context) - marginVertical;
        int camerasRvWidth = (ScreenUtils.getScreenWidth(context) - marginHorizontal - navTabWidth);
        return new Rect(camerasRvWidth, camerasRvHeight);
    }

    /**
     * Generate layout params
     *
     * @return layout params
     */
    private ViewGroup.LayoutParams getLayoutParams(Context context) {
        int width = (camerasRecycleViewRect.getWidth() - ScreenUtils.dip2px(
                context, ITEM_HORIZONTAL_MARGIN_END_DP
        )) / 2;
        int height = (camerasRecycleViewRect.getHeight() - ScreenUtils.dip2px(
                context, ITEM_VERTICAL_MARGIN_END_DP
        )) / 2;
        return new ViewGroup.LayoutParams(width, height);
    }

    static class Rect {

        /**
         * The width
         */
        private final int width;

        /**
         * The height
         */
        private final int height;

        public Rect(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    class IpCameraDashboardViewHolder extends RecyclerView.ViewHolder {
        ItemIpCameraBinding mBinding;

        public IpCameraDashboardViewHolder(@NonNull ItemIpCameraBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        /**
         * Binds the data
         *
         * @param deviceInfo device info
         */
        @SuppressLint("CheckResult")
        public void bindData(DeviceInfoBean deviceInfo) {
//            mBinding.ipcCvVideoStream.fetchToken(deviceInfo.getId(), deviceInfo.getName(), null, false, null);
            mBinding.tvCameraName.setText(deviceInfo.getName());

            // show things
            mBinding.ivIpcPlaceholder.setVisibility(View.VISIBLE);
            mBinding.ivIpcPlay.setVisibility(View.VISIBLE);
            mBinding.ipcCvVideoStream.setVisibility(View.GONE);
            mBinding.llCameraName.setVisibility(View.VISIBLE);
            updatePlace(mBinding.ivIpcPlaceholder, deviceInfo.getId());

            // register item click listener.
            mBinding.getRoot().setOnClickListener(v -> {
                if (mItemClickListener != null)
                    mItemClickListener.onClick(mBinding.ipcCvVideoStream, deviceInfo);
                mBinding.ivIpcPlay.setVisibility(View.GONE);
                mBinding.ivIpcPlaceholder.setVisibility(View.GONE);
                mBinding.ipcCvVideoStream.setVisibility(View.VISIBLE);

            });
            mBinding.tvEnterPanel.setOnClickListener(v -> {
                int adapterPosition = getBindingAdapterPosition();
                if (null == mItemClickListener || adapterPosition == RecyclerView.NO_POSITION) {
                    return;
                }
                if (adapterPosition >= mCameras.size()) {
                    return;
                }
                if (mItemClickListener != null)
                    mItemClickListener.onChildClick(adapterPosition, mCameras.get(adapterPosition));

            });
        }

        /**
         * Binds the placeholder view
         */
        public void bindPlaceholder() {
            mBinding.ivIpcPlaceholder.setVisibility(View.VISIBLE);
            mBinding.ivIpcPlay.setVisibility(View.VISIBLE);
            mBinding.ipcCvVideoStream.setVisibility(View.GONE);
            mBinding.llCameraName.setVisibility(View.GONE);
        }
    }

    private void updatePlace(ImageView ivIpcPlaceholder, String devId) {
        ivIpcPlaceholder.setBackgroundResource(R.drawable.icon_ipc_place_holder);
        File dirFile = new File(ivIpcPlaceholder.getContext().getExternalCacheDir().getAbsolutePath() + "/ipc/center/" + devId);
        LgUtils.w("3333333333333  updatePlace    dirFile=" + dirFile.exists());
        if (dirFile.exists()) {
            File[] tempList = dirFile.listFiles();
            LgUtils.w("3333333333333  updatePlace    tempList=" + tempList.length);
            if (tempList != null && tempList.length > 0) {
                BlurTransformation transformation = new BlurTransformation(1, 10);
                Glide.with(ivIpcPlaceholder.getContext())
                        .load(tempList[0])
                        .skipMemoryCache(false)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.icon_ipc_place_holder)
                        .error(R.drawable.icon_ipc_place_holder)
                        .apply(RequestOptions.bitmapTransform(transformation))
                        .into(ivIpcPlaceholder);
            }
        }
    }
}
