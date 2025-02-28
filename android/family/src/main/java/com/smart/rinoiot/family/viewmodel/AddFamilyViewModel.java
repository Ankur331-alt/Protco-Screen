package com.smart.rinoiot.family.viewmodel;

import android.app.Application;

import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.family.manager.FamilyNetworkManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

/**
 * @author jiangtao
 * <p>
 * create-time: 2022/9/8
 */
public class AddFamilyViewModel extends BaseViewModel {
    private MutableLiveData<Boolean> isCreate = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsCreate() {
        return isCreate;
    }

    public AddFamilyViewModel(@NonNull Application application) {
        super(application);
    }

    public void createFamily(String name, String address, double lat, double lng){
        FamilyNetworkManager.getInstance().createFamily(name,address,lat,lng,new CallbackListener<String>() {
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
