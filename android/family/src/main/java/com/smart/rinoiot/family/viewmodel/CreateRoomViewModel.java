package com.smart.rinoiot.family.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.manager.FamilyNetworkManager;

import java.util.ArrayList;
import java.util.List;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/8
 */
public class CreateRoomViewModel extends BaseViewModel {
    private final MutableLiveData<Boolean> isCreate = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsCreate() {
        return isCreate;
    }

    public CreateRoomViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 创建房间
     */
    public void createRoom(String name, String parentId) {
        FamilyNetworkManager.getInstance().createRoom(name, parentId, new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                isCreate.setValue(true);
            }

            @Override
            public void onError(String code, String error) {

            }
        });
    }
}
