package com.smart.rinoiot.family.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.DialogListener;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.api.FamilyApiService;
import com.smart.rinoiot.family.listener.OnRemoveFamilyListener;
import com.smart.rinoiot.family.manager.HomeDataManager;

import java.util.HashMap;
import java.util.Map;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/6
 */
public class MemberInfoViewModel extends BaseViewModel {


    public MemberInfoViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 获取管理
     *
     * @param role
     * @return
     */
    public String getRole(int role) {
        String roleStr;
        if (role == 1) {
            roleStr = mContext.getString(R.string.rino_family_role_owner);
        } else if (role == 2) {
            roleStr = mContext.getString(R.string.rino_family_role_admin);
        } else {
            roleStr = mContext.getString(R.string.rino_family_role_member);
        }
        return roleStr;
    }


    /**
     * 离开家庭 相当于删除成员
     */
    public void removeMember(String assetId, String memberId, OnRemoveFamilyListener onRemoveFamilyListener) {
        RetrofitUtils.getService(FamilyApiService.class).removeMember(assetId, memberId).enqueue(new BaseRequestListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                onRemoveFamilyListener.OnRemove();
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showErrorMsg(msg);
            }
        });

    }

    /**
     * 删除家庭
     */
    public void showRemoveFamilyDialog(AppCompatActivity appCompatActivity, String memberId) {
        DialogUtil.showNormalMsg(appCompatActivity, getString(R.string.rino_family_delete_member_tips), "", new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                removeMember(HomeDataManager.getInstance().assetBean.getId(), memberId, () -> {
                    ToastUtil.showMsg(getString(R.string.rino_family_delete_success));
                    appCompatActivity.setResult(Activity.RESULT_OK);
                    appCompatActivity.finish();
                });

            }
        });
    }

    /**
     * 设置成员角色
     */
    public void showChangeRole(Activity activity, String assetId, String memberId, int memberRole) {
        DialogUtil.showListSelectedDialog(activity, "", new String[]{getString(R.string.rino_family_role_admin), getString(R.string.rino_family_role_member)}, 0, new DialogListener() {
            @Override
            public void onConfirm(String text, int position) {
                if ((position == 0 ? 2 : 3) == memberRole) return;
                removeMember(assetId, memberId, position == 0 ? 2 : 3, text);
            }
        });
    }

    /**
     * 设置成员角色
     * 成员角色（1=拥有者，2=管理员，3=普通成员
     */
    private final MutableLiveData<String> roleLiveData = new MutableLiveData<>();

    public MutableLiveData<String> getRoleLiveData() {
        return roleLiveData;
    }

    public void removeMember(String assetId, String memberId, int memberRole, String roleText) {
        Map<String, Object> map = new HashMap<>();
        map.put("assetId", assetId);
        map.put("memberId", memberId);
        map.put("memberRole", memberRole);
        RetrofitUtils.getService(FamilyApiService.class).setMemberRole(map).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                roleLiveData.postValue(roleText);
            }

            @Override
            public void onError(String error, String msg) {
                ToastUtil.showErrorMsg(msg);
            }
        });

    }
}
