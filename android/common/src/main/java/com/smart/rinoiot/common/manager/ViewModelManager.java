package com.smart.rinoiot.common.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.smart.rinoiot.common.base.BaseViewModel;


/**
 * viewmodel管理类
 *
 * @Package: com.app.car.manager
 * @ClassName: ViewModelManager.java
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2020/7/19 13:48
 * @UpdateUser: 更新者：xf
 * @UpdateDate: 2020/7/19 13:48
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ViewModelManager<M extends BaseViewModel> {

    /**
     * 多个model是用次方法创建
     *
     * @param mClass
     * @return
     */
    public M createModel(Class<M> mClass, AppCompatActivity activity) {
        return new ViewModelProvider(activity, new ViewModelProvider.AndroidViewModelFactory(activity.getApplication())).get(mClass);
    }
}