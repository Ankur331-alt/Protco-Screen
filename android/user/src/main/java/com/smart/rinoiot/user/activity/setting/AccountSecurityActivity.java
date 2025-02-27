package com.smart.rinoiot.user.activity.setting;

import android.Manifest;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.utils.ActivityUtils;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.FileUtils;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.StatusBarUtil;
import com.smart.rinoiot.common.utils.SystemCameraManager;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityAccountSecurityBinding;
import com.smart.rinoiot.user.viewmodel.setting.AccountSecurityViewModel;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @Author : tw
 * @Time : On 2022/9/29 15:41
 * @Description : AboutAppActivity 账号与安全
 */
public class AccountSecurityActivity extends BaseActivity<ActivityAccountSecurityBinding, AccountSecurityViewModel> implements View.OnClickListener, OnResultCallbackListener<LocalMedia> {
    private final SystemCameraManager systemCameraManager = new SystemCameraManager();

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_mine_account_security);
    }

    @Override
    public void init() {
        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        //头像
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(this);
        if (userInfo != null) {
            ImageLoader.getInstance().bindCircleImageUrl(userInfo.avatarUrl, binding.ivProfilePhoto, R.drawable.icon_default_avatar);
            binding.includeUserName.tvSummary.setText(userInfo.nickname);
            String account = userInfo.phoneNumber;
            if (userInfo.registryType == 1) {//1=邮箱注册，2=手机注册，3=第三方注册
                account = userInfo.email;
            }
            binding.includeAccount.tvSummary.setText(account);
        }
        //昵称
        binding.includeUserName.tvTitle.setText(getString(R.string.rino_mine_profile_name));
        binding.includeUserName.ivRight.setVisibility(View.VISIBLE);
        binding.includeUserName.tvSummary.setVisibility(View.VISIBLE);
        //账号所在地
        binding.includeAccountLocal.tvTitle.setText(getString(R.string.rino_mine_account_local));
        binding.includeAccountLocal.tvSummary.setVisibility(View.VISIBLE);
        binding.includeAccountLocal.tvSummary.setText(SharedPreferenceUtil.getInstance().get(Constant.COUNTRY_NAME, ""));

        //手机号
        binding.includeAccount.tvTitle.setText(getString(R.string.rino_mine_account));
        binding.includeAccount.tvSummary.setVisibility(View.VISIBLE);
        //修改登录密码
        binding.includeChangePwd.tvTitle.setText(getString(R.string.rino_mine_change_pwd));
        binding.includeChangePwd.ivRight.setVisibility(View.VISIBLE);
        //注销账号
        binding.includeLogout.tvTitle.setText(getString(R.string.rino_mine_logout));
//        binding.includeLogout.getRoot().setVisibility(View.GONE);
        binding.includeLogout.ivRight.setVisibility(View.VISIBLE);
    }

    private void initListener() {
        binding.ivProfilePhoto.setOnClickListener(this);
        binding.includeUserName.getRoot().setOnClickListener(this);
        binding.includeChangePwd.getRoot().setOnClickListener(this);
        binding.includeLogout.getRoot().setOnClickListener(this);
    }

    private void initData() {
        mViewModel.getUpdateLiveData().observe(this, aBoolean -> {
            if (aBoolean) {
                ToastUtil.showMsg(getString(R.string.rino_common_operation_success));
                UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(this);
                if (userInfo != null) {
                    ImageLoader.getInstance().bindCircleImageUrl(userInfo.avatarUrl, binding.ivProfilePhoto, R.drawable.icon_default_avatar);
                    binding.includeUserName.tvSummary.setText(userInfo.nickname);
                }
//
            }
        });
    }

    @Override
    public ActivityAccountSecurityBinding getBinding(LayoutInflater inflater) {
        return ActivityAccountSecurityBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_profile_photo) {//修改头像
            EasyPermissions.requestPermissions(this, getString(R.string.rino_common_request_camera), SystemCameraManager.RC_CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        } else if (v.getId() == R.id.includeUserName) {//修改昵称
            mViewModel.updateRename(this, binding.includeUserName.tvSummary.getText().toString());
        } else if (v.getId() == R.id.includeChangePwd) {//修改登录密码
            ActivityUtils.startActivity(this, null, ChangePwdSendCodeActivity.class);
        } else if (v.getId() == R.id.includeLogout) {//注销账号
            ActivityUtils.startActivity(this, null, LogoutTipsActivity.class);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtil.showMsg(getString(R.string.rino_common_permission_denied));
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        DialogUtil.showBottomSheet(this, getString(R.string.rino_mine_feedback_select_picture), new String[]{getString(R.string.rino_mine_feedback_choose_pic_from_camera), getString(R.string.rino_mine_feedback_gallery_title)}, (position, text) -> {
            if (position == 0) {
                systemCameraManager.getPicFromCamera(this, this);
            } else {
                systemCameraManager.getPicFromAlbum(this, 1, this);
            }
        });
    }

    @Override
    public void onResult(List<LocalMedia> result) {
        if (result == null || result.size() == 0) {
            return;
        }
        if (FileUtils.fileLimit(result.get(0).getRealPath(), this)) {
            ToastUtil.showMsg(getString(R.string.rino_mine_feedback_file_limit_size));
            return;
        }
        mViewModel.uploadFile(result.get(0).getRealPath());
    }

    @Override
    public void onCancel() {

    }
}
