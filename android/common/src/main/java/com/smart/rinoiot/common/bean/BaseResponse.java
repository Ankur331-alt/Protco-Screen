package com.smart.rinoiot.common.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 请求接口数据承接基类
 *
 * @Package: com.znkit.smart.bean
 * @ClassName: BaseResponse
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2021/1/13 3:58 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/1/13 3:58 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class BaseResponse<T> {
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String msg;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T list) {
        this.data = list;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
