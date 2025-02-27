package com.smart.rinoiot.family.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.InviteMemberBean;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.manager.FamilyNetworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author jiangtao
 * <p>
 * create-time: 2022/9/8
 */
public class FamilyManagerViewModel extends BaseViewModel {

    /**
     * 设备列表
     */
    private final MutableLiveData<List<DeviceInfoBean>> deviceDataLive = new MutableLiveData<>();

    /**
     * 是否同意邀请
     */
    private final MutableLiveData<Boolean> isAcceptMutableLiveData = new MutableLiveData<>();

    /**
     * 获取家庭列表回调
     */
    private final MutableLiveData<List<AssetBean>> familyListMutableLiveData = new MutableLiveData<>();

    /**
     * 被邀请进入的家庭列表
     */
    private final MutableLiveData<List<InviteMemberBean>> inviteMemberDataLive = new MutableLiveData<>();

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public FamilyManagerViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<AssetBean>> getFamilyListMutableLiveData() {
        return familyListMutableLiveData;
    }

    public MutableLiveData<List<InviteMemberBean>> getInviteMemberDataLive() {
        return inviteMemberDataLive;
    }

    public MutableLiveData<Boolean> getIsAcceptMutableLiveData() {
        return isAcceptMutableLiveData;
    }

    /**
     * 获取家庭列表
     */
    public void getFamilyList() {
        Disposable disposable = FamilyNetworkManager.getInstance()
                .getFamilyList()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        familyListMutableLiveData::postValue,
                        throwable -> familyListMutableLiveData.postValue(new ArrayList<>())
                );
        mCompositeDisposable.add(disposable);
    }

    /**
     * 获取邀请记录
     */
    public void getInviteMemberList() {
        FamilyNetworkManager.getInstance().getInvitedList(new CallbackListener<List<InviteMemberBean>>() {
            @Override
            public void onSuccess(List<InviteMemberBean> data) {
                inviteMemberDataLive.postValue(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    /**
     * 同意邀请
     */
    public void acceptInvited(boolean isAccept, String inviteId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("accept", isAccept);
        map.put("inviteId", inviteId);
        FamilyNetworkManager.getInstance().acceptInvited(map, new CallbackListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                ToastUtil.showMsg(getString(isAccept ? R.string.rino_family_join_success : R.string.rino_common_operation_success));
                isAcceptMutableLiveData.setValue(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showErrorMsg(error);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear();
    }
}
