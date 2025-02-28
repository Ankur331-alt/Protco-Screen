package com.smart.rinoiot.user.bean.setting;

import java.io.Serializable;

/**
 * @Author : tw
 * @Time : On 2022/9/30 10:55
 * @Description : SendCodeParamsBean 修改密码传参
 */
public class InputPwdParamsBean implements Serializable {
    private String account;
    private String code;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
