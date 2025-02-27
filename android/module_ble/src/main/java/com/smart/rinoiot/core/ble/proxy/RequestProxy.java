package com.smart.rinoiot.core.ble.proxy;

import android.content.Context;

import com.smart.rinoiot.core.ble.BleLog;
import com.smart.rinoiot.core.ble.request.ConnectRequest;
import com.smart.rinoiot.core.ble.request.DescriptorRequest;
import com.smart.rinoiot.core.ble.request.MtuRequest;
import com.smart.rinoiot.core.ble.request.NotifyRequest;
import com.smart.rinoiot.core.ble.request.ReadRequest;
import com.smart.rinoiot.core.ble.request.ReadRssiRequest;
import com.smart.rinoiot.core.ble.request.Rproxy;
import com.smart.rinoiot.core.ble.request.ScanRequest;
import com.smart.rinoiot.core.ble.request.WriteRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 *
 * Created by LiuLei on 2017/9/1.
 */

public class RequestProxy implements InvocationHandler{
    private static final String TAG = "RequestProxy";

    private RequestProxy(){}

    private Object receiver;

    public static RequestProxy newProxy(){
        return new RequestProxy();
    }

    //Bind the delegate object and return the proxy class
    public Object bindProxy(Context context, Object tar){
        this.receiver = tar;
        //绑定委托对象，并返回代理类
        BleLog.d(TAG, "bindProxy: "+"Binding agent successfully");
        Rproxy.init(ConnectRequest.class, MtuRequest.class,
                NotifyRequest.class, ReadRequest.class,
                ReadRssiRequest.class, ScanRequest.class,
                WriteRequest.class, DescriptorRequest.class);
        return Proxy.newProxyInstance(
                tar.getClass().getClassLoader(),
                tar.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(receiver, args);
    }
}
