package com.smart.rinoiot.center.update;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.AcpUtils;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.R;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 首页添加设备弹出框
 *
 * @Package: com.znkit.smart.home.view
 * @ClassName: CustomShowAddPop
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2021/3/24 11:19 AM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/3/24 11:19 AM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
@SuppressLint("ViewConstructor")
public class CustomUpdatePop extends CenterPopupView implements DownloadFileListener, PermissionListener {
    private static final String TAG = "CustomUpdatePop";

    private UpdateBean updateBean;
    private ProgressBar progressBar;

    public CustomUpdatePop(@NonNull Context context, UpdateBean updateBean) {
        super(context);
        this.updateBean = updateBean;
        PermissionManager.getInstance().setPermissionListener(this);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_update;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate() {
        super.onCreate();
        View btnUpdate = findViewById(R.id.btnUpdate);
        View btnCancel = findViewById(R.id.btnCancel);
        View viewHorizon = findViewById(R.id.viewHorizon);
        View ll_check = findViewById(R.id.ll_check);
        progressBar = findViewById(R.id.progressBar);
        if (updateBean.getUpgradeType() != null && (updateBean.getUpgradeType() == 1 || updateBean.getUpgradeType() == 2)) {
            btnCancel.setVisibility(VISIBLE);
        } else {
            btnCancel.setVisibility(GONE);
        }
        TextView tvDes = findViewById(R.id.tvDes);
        if (!TextUtils.isEmpty(updateBean.getUpgradeContent())) {
            tvDes.setText(Html.fromHtml(updateBean.getUpgradeContent()));
        }
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(String.format(getContext().getString(R.string.rino_common_update), updateBean.getVersion()));
        btnCancel.setOnClickListener(v -> {
            dismiss();
            SharedPreferenceUtil.getInstance().put(Constant.IGNORE_UPDATE, updateBean.getVersion());
        });
        btnUpdate.setOnClickListener(v -> {
            viewHorizon.setVisibility(GONE);
            ll_check.setVisibility(GONE);
            progressBar.setVisibility(VISIBLE);
            dialog.setCanceledOnTouchOutside(false);
            if (AcpUtils.hasPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                download();
            } else {
                AcpUtils.reqPermissions((Activity) getContext(), String.format(getContext().getString(R.string.rino_common_permission_tip_read_set), getContext().getString(R.string.rino_common_update)), 100, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    public static void checkStorageManagerPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PermissionManager.getInstance().remove();
    }

    private void download() {
        RetrofitUtils.getService(UpdateApi.class).downloadFileUrl(updateBean.getResUrl()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                new FileDownloadManager().downApkFile(response, CustomUpdatePop.this, getContext().getFilesDir().getPath());
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                ToastUtil.showMsg(getContext().getString(R.string.rino_common_download_failed));
            }
        });
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onProgress(int progress) {
        AppExecutors.getInstance().mainThread().execute(() -> progressBar.setProgress(progress));
    }

    @Override
    public void onFinish(String path) {
        Log.d(TAG, "onFinish: finished downloading=" + path);
        AppExecutors.getInstance().mainThread().execute(this::dismiss);
        ApkInstallUtils.openInstallFile(path, getContext());
    }

    @Override
    public void onError(String msg) {
        AppExecutors.getInstance().mainThread().execute(() -> {
            ToastUtil.showMsg(getContext().getString(R.string.rino_common_download_failed));
            dismiss();
        });
    }

    @Override
    public void onSuccess() {
        download();
    }

    @Override
    public void onFailed() {
        ToastUtil.showMsg(getContext().getString(R.string.rino_common_permission_denied));
        dismiss();
    }
}
