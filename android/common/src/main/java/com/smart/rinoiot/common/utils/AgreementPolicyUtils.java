package com.smart.rinoiot.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.R;

/**
 * @author tw
 * @time 2022/11/3 20:07
 * @description 用户协议和隐私政策
 */
@SuppressLint("StringFormatInvalid")
public class AgreementPolicyUtils {
    /**
     * 获取隐私政策
     */
    public static String getPrivacyPolicy(Context context) {
        return String.format(context.getString(R.string.rino_user_privacy_policy_url), Constant.BASE_URL_H5);
    }

    /**
     * 获取用户协议
     */
    public static String getAgreement(Context context) {
        return String.format(context.getString(R.string.rino_user_user_agreement_url), Constant.BASE_URL_H5);
    }
}
