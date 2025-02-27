package com.smart.rinoiot.user.bean.setting;

import java.io.Serializable;

/**
 * @Author : tw
 * @Time : On 2022/9/30 10:55
 * @Description : SendCodeParamsBean 发送验证--》输入验证码
 */
public class SendCodeParamsBean implements Serializable {
    private String account;
    private int registryType;


    public int getRegistryType() {
        return registryType;
    }

    public void setRegistryType(int registryType) {
        this.registryType = registryType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
