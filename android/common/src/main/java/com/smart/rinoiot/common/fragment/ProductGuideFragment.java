package com.smart.rinoiot.common.fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.ProductGuideBean;
import com.smart.rinoiot.common.bean.ProductGuideStepBean;
import com.smart.rinoiot.common.databinding.FragmentProductGuideBinding;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.viewmodel.GuideViewModel;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class ProductGuideFragment extends BaseFragment<FragmentProductGuideBinding, GuideViewModel> {

    private final ProductGuideStepBean currentModeContentStep;

    public ProductGuideFragment(ProductGuideStepBean currentModeContentStep) {
        this.currentModeContentStep = currentModeContentStep;
    }

    @Override
    public void init() {
        int screenWidth = (int) DpUtils.getScreenWidth();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, screenWidth * 318 / 375);
        layoutParams.gravity = Gravity.CENTER;
        binding.videoPlay.setLayoutParams(layoutParams);
        binding.ivTip.setLayoutParams(layoutParams);
        if (currentModeContentStep != null) {
            binding.tvTitle.setVisibility(TextUtils.isEmpty(currentModeContentStep.getTitle()) ? View.GONE : View.VISIBLE);
            binding.tvTitle.setText(currentModeContentStep.getTitle());

            binding.tvSubTitle.setVisibility(TextUtils.isEmpty(currentModeContentStep.getSubtitle()) ? View.GONE : View.VISIBLE);
            binding.tvSubTitle.setText(currentModeContentStep.getSubtitle());

            binding.tvTip.setVisibility(TextUtils.isEmpty(currentModeContentStep.getDescribe()) ? View.GONE : View.VISIBLE);
            binding.tvTip.setText(currentModeContentStep.getDescribe());
            
            binding.ivTip.setVisibility(TextUtils.isEmpty(currentModeContentStep.getUrl()) ? View.GONE : View.VISIBLE);
            ImageLoader.getInstance().bindImageUrl(currentModeContentStep.getUrl(), binding.ivTip, R.drawable.icon_placeholder, R.drawable.icon_placeholder);

        }
    }

    @Override
    public FragmentProductGuideBinding getBinding(LayoutInflater inflater) {
        return FragmentProductGuideBinding.inflate(inflater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onPause() {
        super.onPause();
        Jzvd.goOnPlayOnPause();
    }

    /**
     * 视频和语音播放指引
     */
    private void videoAnda(ProductGuideBean.StepBean currentModeContentStep) {
        if (currentModeContentStep != null) {
            if (currentModeContentStep.getType() == 1) {//1图片，2视频
                binding.ivTip.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().bindImageUrl(currentModeContentStep.getUrl(), binding.ivTip, R.drawable.icon_placeholder, R.drawable.icon_placeholder);
            } else {
                if (!TextUtils.isEmpty(currentModeContentStep.getUrl())) {//视频地址不为空
                    binding.videoPlay.setVisibility(View.VISIBLE);
                    JZDataSource source = new JZDataSource(currentModeContentStep.getUrl());
                    source.looping = true;
                    binding.videoPlay.setUp(source, JzvdStd.SCREEN_NORMAL);
                    binding.videoPlay.startVideo();  //获取到开始播放按钮，模拟点击
                }
            }
            if (!TextUtils.isEmpty(currentModeContentStep.getRadioUrl())) {
                JZDataSource source = new JZDataSource(currentModeContentStep.getRadioUrl());
                source.looping = true;
                binding.audioPlay.setUp(source, JzvdStd.SCREEN_NORMAL);
                binding.audioPlay.startVideo();  //获取到开始播放按钮，模拟点击
            }
//            String video = "https://pub.xiuzhan365.com/resourceFiles/web/home/videos/xiuzhan_new.mp4";
//            String audio = "http://qcloudcos.xunjiepdf.com/xunjievideo/temp/202210151601/99c8beedb53945589f0ddbda762df206/xiuzhan_new.mp3";
            binding.tvTip.setText(currentModeContentStep.getDescribe());
            binding.tvTitle.setText(currentModeContentStep.getTitle());
        }
    }
}
