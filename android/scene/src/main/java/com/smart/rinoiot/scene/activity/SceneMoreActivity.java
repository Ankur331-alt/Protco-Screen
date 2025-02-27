package com.smart.rinoiot.scene.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.bean.SceneTriggerBean;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.ImageLoaderUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.scene.R;
import com.smart.rinoiot.scene.databinding.DialogSceneMoreBinding;
import com.smart.rinoiot.scene.view.PagerBottomPopView;
import com.smart.rinoiot.scene.viewmodel.SceneConfigViewModel;

public class SceneMoreActivity extends BaseActivity<DialogSceneMoreBinding, SceneConfigViewModel> implements View.OnClickListener {

    public final static int REQUEST_CODE_FROM_EFFECTIVE_PERIOD = 0x1001;

    private String bgColor;
    private String coverUrl;
    private SceneTriggerBean sceneTriggerBean;

    @Override
    public String getToolBarTitle() {
        return null;
    }

    @Override
    public void init() {
        // dialog样式，需要在此设置长宽，否则会有间隙导致无法全屏
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        // 设置左上和右上的圆角
        float db10 = DpUtils.dip2px(10.f);
        binding.viewBottom.setBackground(XPopupUtils.createDrawable(getResources().getColor(R.color.cen_common_item_bg_color),
                db10, db10, db10, db10));

        initListener();
        initContent();
        updateEffectivePeriod(sceneTriggerBean);

        mViewModel.getDeleteSceneLiveData().observe(this, isSuccess -> {
            mViewModel.hideLoading();
            if (isSuccess) {
                finishThis();
                AppManager.getInstance().finishActivity(SceneConfigActivity.class);
            }
        });
    }

    private void initContent() {
        SceneBean sceneBean = mViewModel.getSceneBean();
        binding.etSceneName.setText(sceneBean.getName());
        if (mViewModel.getSceneBean().getRuleMetaData() != null && mViewModel.getSceneBean().getRuleMetaData().getTriggers() != null
                && !mViewModel.getSceneBean().getRuleMetaData().getTriggers().isEmpty()) {
            sceneTriggerBean = mViewModel.getSceneBean().getRuleMetaData().getTriggers().get(0);
        }
        bgColor = sceneBean.getBgColor();
        coverUrl = sceneBean.getCoverUrl();

        if (TextUtils.isEmpty(bgColor)) bgColor = "#DCDCF4";
        if (TextUtils.isEmpty(coverUrl))
            coverUrl = "https://storage-app.rinoiot.com/scene/icon/default-icon.png";

        binding.ivBgStyle.setColorFilter(Color.parseColor(bgColor.trim()));
        ImageLoaderUtils.getInstance().bindImageUrl(coverUrl, binding.ivStyle);
        if (Constant.SCENE_TYPE_FOR_AUTO == sceneBean.getSceneType()) {
            binding.viewStyle.setVisibility(View.GONE);
//            binding.viewLine2.setVisibility(View.GONE);

            binding.viewIsShowHomePage.setVisibility(View.GONE);
//            binding.viewLine3.setVisibility(View.GONE);

            binding.viewEffectivePeriod.setVisibility(sceneTriggerBean==null?View.GONE:View.VISIBLE);
//            binding.viewLine4.setVisibility(sceneTriggerBean==null?View.GONE:View.VISIBLE);
        } else {
//            binding.viewStyle.setVisibility(View.VISIBLE);
            binding.viewStyle.setVisibility(View.GONE);
//            binding.viewLine2.setVisibility(View.VISIBLE);

            binding.viewIsShowHomePage.setVisibility(View.VISIBLE);
//            binding.viewLine3.setVisibility(View.VISIBLE);
            binding.viewEffectivePeriod.setVisibility(View.GONE);
//            binding.viewLine4.setVisibility(View.GONE);
        }

        binding.switchOp.setChecked(sceneBean.getIsHome() == 1);
        binding.tvDeleteScene.setVisibility(TextUtils.isEmpty(sceneBean.getId()) ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void updateEffectivePeriod(SceneTriggerBean sceneTriggerBean) {
        if (sceneTriggerBean == null) return;
        switch (sceneTriggerBean.getTimeInterval()) {
            case Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_ALL_DAY:
                binding.tvEffectivePeriod.setText(getString(R.string.rino_scene_effective_period_allday));
                break;
            case Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_DAY:
                binding.tvEffectivePeriod.setText(getString(R.string.rino_scene_effective_period_daytime));
                break;
            case Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_AT_NIGHT:
                binding.tvEffectivePeriod.setText(getString(R.string.rino_scene_effective_period_atnight));
                break;
            case Constant.SCENE_EFFECTIVE_PERIOD_TYPE_FOR_CUSTOM:
                binding.tvEffectivePeriod.setText(mViewModel.getEffectivePeriodStartTime(sceneTriggerBean) + "-" + mViewModel.getEffectivePeriodEndTime(sceneTriggerBean));
                break;
        }
    }

    private void initListener() {
        binding.tvConfirm.setOnClickListener(this);
        binding.ivDelete.setOnClickListener(this);
        binding.viewEffectivePeriod.setOnClickListener(this);
        binding.viewBlank.setOnClickListener(this);
        binding.viewBottom.setOnClickListener(this);
        binding.viewStyle.setOnClickListener(this);
        binding.tvDeleteScene.setOnClickListener(this);

        binding.etSceneName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(binding.etSceneName.getText().toString())) {
                    binding.ivDelete.setVisibility(View.GONE);
                } else {
                    binding.ivDelete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public DialogSceneMoreBinding getBinding(LayoutInflater inflater) {
        return DialogSceneMoreBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.viewBlank) {
            finishThis();
        } else if (v.getId() == R.id.tvConfirm) {
            String title = binding.etSceneName.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                ToastUtil.showMsg(R.string.rino_scene_edit_name_not_empty);
                return;
            }

            mViewModel.getSceneBean().setName(binding.etSceneName.getText().toString());
            if (mViewModel.getSceneBean().getRuleMetaData() != null && mViewModel.getSceneBean().getRuleMetaData().getTriggers() != null
                    && !mViewModel.getSceneBean().getRuleMetaData().getTriggers().isEmpty()) {
                mViewModel.getSceneBean().getRuleMetaData().getTriggers().set(0, sceneTriggerBean);
            }

            if (Constant.SCENE_TYPE_FOR_AUTO != mViewModel.getSceneBean().getSceneType()) {
                mViewModel.getSceneBean().setIsHome(binding.switchOp.isChecked() ? 1 : 0);
                mViewModel.getSceneBean().setBgColor(bgColor);
                mViewModel.getSceneBean().setCoverUrl(coverUrl);
            }

            setResult(RESULT_OK);
            finishThis();
        } else if (v.getId() == R.id.ivDelete) {
            binding.etSceneName.setText("");
        } else if (v.getId() == R.id.viewEffectivePeriod) {
            startActivityForResult(new Intent(v.getContext(), SceneEffectivePeriodActivity.class).putExtra("sceneTriggerBean", sceneTriggerBean), REQUEST_CODE_FROM_EFFECTIVE_PERIOD);
        } else if (v.getId() == R.id.viewStyle) {
            new XPopup.Builder(v.getContext())
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .isViewMode(true)
                    .asCustom(new PagerBottomPopView(v.getContext()).setOnDismissListener((colorBg, iconUrl) -> {
                        bgColor = colorBg;
                        coverUrl = iconUrl;

                        binding.ivBgStyle.setColorFilter(Color.parseColor(bgColor.trim()));
                        ImageLoaderUtils.getInstance().bindImageUrl(coverUrl, binding.ivStyle);
                    }))
                    .show();
        } else if (v.getId() == R.id.tvDeleteScene) {
            DialogUtil.showNormalMsg(this, "",
                    getString(mViewModel.getSceneBean().getSceneType() == 2
                            ? R.string.rino_scene_delete_auto : R.string.rino_scene_delete_manual), new DialogOnListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onConfirm() {
                            mViewModel.showLoading();
                            mViewModel.deleteScene(mViewModel.getSceneBean().getId());
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE_FROM_EFFECTIVE_PERIOD) {
            sceneTriggerBean = data != null ? (SceneTriggerBean) data.getSerializableExtra("sceneTriggerBean") : sceneTriggerBean;
            updateEffectivePeriod(sceneTriggerBean);
        }
    }
}
