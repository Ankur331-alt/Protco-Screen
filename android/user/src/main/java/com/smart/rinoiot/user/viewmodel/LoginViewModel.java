package com.smart.rinoiot.user.viewmodel;

import android.app.Application;
import android.os.CountDownTimer;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.CountryBean;
import com.smart.rinoiot.common.bean.TimeZoneBean;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.utils.PinyinComparator;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.user.bean.CountryHeadBean;
import com.smart.rinoiot.user.bean.TimeZoneHeadBean;
import com.smart.rinoiot.user.manager.UserNetworkManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 登录页逻辑处理
 *
 * @Package: com.znkit.smart.ui.user.viewmodel
 * @ClassName: LoginViewModel
 * @Author: xf
 * @CreateDate: 2020/5/7 4:24 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/7 4:24 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */

public class LoginViewModel extends BaseViewModel {
    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 登录成功后用户数据监听
     */
    private final MutableLiveData<UserInfoBean> userLiveData = new MutableLiveData<>();
    /**
     * 国家地区列表数据
     */
    private final MutableLiveData<List<CountryHeadBean>> countryLiveData = new MutableLiveData<>();
    /**
     * 搜索数据回调
     */
    private final MutableLiveData<List<CountryHeadBean>> searchData = new MutableLiveData<>();
    /**
     * 时区列表数据
     */
    private final MutableLiveData<List<TimeZoneHeadBean>> timeZoneLiveData = new MutableLiveData<>();
    /**
     * 搜索时区数据回调
     */
    private final MutableLiveData<List<TimeZoneHeadBean>> searchTimeZoneData = new MutableLiveData<>();
    /**
     * 验证码是否获取成功动作数据发送
     */
    private final MutableLiveData<String> codeActionLiveData = new MutableLiveData<>();
    /**
     * 校验成功回调
     */
    private final MutableLiveData<Boolean> checkCodeLiveData = new MutableLiveData<>();
    /**
     * 设置密码回调
     */
    private final MutableLiveData<Boolean> setPsdLiveData = new MutableLiveData<>();
    /**
     * 倒计时数据
     */
    private final MutableLiveData<Integer> countDownLiveData = new MutableLiveData<>();

    /**
     * 索引列表
     */
    private final Map<String, Integer> indexMap = new HashMap<>();
    /**
     * 城市列表bean封装
     */
    private final List<CountryHeadBean> countryList = new ArrayList<>();
    /**
     * 搜索数据列表
     */
    private final List<CountryHeadBean> searchList = new ArrayList<>();
    /**
     * 时区列表bean封装
     */
    private final List<TimeZoneHeadBean> timeZoneList = new ArrayList<>();
    /**
     * 搜索时区数据列表
     */
    private final List<TimeZoneHeadBean> searchTimeZoneList = new ArrayList<>();

    /**
     * 定时器
     */
    private CountDownTimer timer;

    /**
     *
     */
    private static final String ENGLISH_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    public MutableLiveData<UserInfoBean> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<List<CountryHeadBean>> getCountryLiveData() {
        return countryLiveData;
    }

    public MutableLiveData<List<TimeZoneHeadBean>> getTimeZoneLiveData() {
        return timeZoneLiveData;
    }

    public MutableLiveData<List<CountryHeadBean>> getSearchData() {
        return searchData;
    }

    public MutableLiveData<List<TimeZoneHeadBean>> getSearchTimeZoneData() {
        return searchTimeZoneData;
    }

    public MutableLiveData<String> getCodeActionLiveData() {
        return codeActionLiveData;
    }

    public MutableLiveData<Boolean> getCheckCodeLiveData() {
        return checkCodeLiveData;
    }

    public MutableLiveData<Boolean> getSetPsdLiveData() {
        return setPsdLiveData;
    }

    public MutableLiveData<Integer> getCountDownLiveData() {
        return countDownLiveData;
    }

    public List<CountryHeadBean> getCountryList() {
        return countryList;
    }

    public List<TimeZoneHeadBean> getTimeZoneList() {
        return timeZoneList;
    }

    /**
     * 选择国家地区列表右边索引数据
     */
    public Map<String, Integer> getIndexMap() {
        return indexMap;
    }

    public void getNetTimeZoneList() {
        UserNetworkManager.getInstance().getTimezoneList(new CallbackListener<List<TimeZoneBean>>() {
            @Override
            public void onSuccess(List<TimeZoneBean> data) {
                handleTimeZoneData(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                timeZoneLiveData.postValue(null);
            }
        });
    }

    /**
     * 获取国家地区列表
     */
    public void getNetCountryList() {
        handleCountryData(CacheDataManager.getInstance().getCountryList());
    }

    private void handleCountryData(List<CountryBean> countryData) {
        if (countryData == null) {
            return;
        }
        Map<String, List<CountryBean>> map = new HashMap<>();
        for (int i = 0; i < ENGLISH_ALPHABET.length(); i++) {
            List<CountryBean> countryBeans = new ArrayList<>();
            map.put(String.valueOf(ENGLISH_ALPHABET.charAt(i)), countryBeans);
        }
        for (int i = 0; i < countryData.size(); i++) {
            CountryBean bean = countryData.get(i);
            String matchingStr = (bean.getMatchingStr() == null ?
                    new PinyinComparator().getPingYin(bean.getCountryName()).substring(0, 1).toUpperCase():
                    bean.getMatchingStr().substring(0, 1).toUpperCase());
            try {
                Objects.requireNonNull(map.get(matchingStr)).add(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int index = 0;
        for (int i = 0; i < ENGLISH_ALPHABET.length(); i++) {
            List<CountryBean> countryBeanList = map.get(String.valueOf(ENGLISH_ALPHABET.charAt(i)));
            if (countryBeanList != null && !countryBeanList.isEmpty()) {
                this.countryList.add(new CountryHeadBean(true, String.valueOf(ENGLISH_ALPHABET.charAt(i))));
                indexMap.put(String.valueOf(ENGLISH_ALPHABET.charAt(i)), index);
                // 排序
                Collections.sort(countryBeanList, new PinyinComparator());
                for (int j = 0; j < countryBeanList.size(); j++) {
                    countryList.add(new CountryHeadBean(countryBeanList.get(j)));
                    index += 1;
                }
                index += 1;
            }
        }
        countryLiveData.postValue(countryList);
    }

    /**
     * 选择国家地区列表搜索方法
     */
    public void searchCountry(String s) {
        if (TextUtils.isEmpty(s)) {
            searchList.clear();
            countryLiveData.postValue(countryList);
        } else {
            searchList.clear();
            for (int i = 0; i < countryList.size(); i++) {
                CountryBean bean = countryList.get(i).getCountryBean();
                if (null == bean) {
                    continue;
                }

                boolean matchedCountryCode =
                        (!TextUtils.isEmpty(bean.getCountryCode())
                                &&  bean.getCountryCode().contains(s));

                boolean matchedCountryName =
                        (!TextUtils.isEmpty(bean.getCountryName())
                                && bean.getCountryName().toLowerCase().contains(s.toLowerCase()));

                if (matchedCountryName || matchedCountryCode) {
                    searchList.add(countryList.get(i));
                }
            }
            searchData.postValue(searchList);
        }
    }

    private void handleTimeZoneData(List<TimeZoneBean> timeZoneData) {
        Map<String, List<TimeZoneBean>> map = new HashMap<>();
        for (int i = 0; i < ENGLISH_ALPHABET.length(); i++) {
            List<TimeZoneBean> timeZoneBeans = new ArrayList<>();
            map.put(String.valueOf(ENGLISH_ALPHABET.charAt(i)), timeZoneBeans);
        }

        for (int i = 0; i < timeZoneData.size(); i++) {
            TimeZoneBean bean = timeZoneData.get(i);
            String matchingStr = (bean.getMatchingStr() == null ?
                    new PinyinComparator().getPingYin(bean.getCountryName()).substring(0, 1).toUpperCase():
                    bean.getMatchingStr().substring(0, 1).toUpperCase());
            try {
                map.get(matchingStr).add(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int index = 0;
        for (int i = 0; i < ENGLISH_ALPHABET.length(); i++) {
            List<TimeZoneBean> timeZoneBeans = map.get(String.valueOf(ENGLISH_ALPHABET.charAt(i)));
            if (timeZoneBeans != null && !timeZoneBeans.isEmpty()) {
                this.timeZoneList.add(
                        new TimeZoneHeadBean(
                                true, String.valueOf(ENGLISH_ALPHABET.charAt(i))
                        )
                );
                indexMap.put(String.valueOf(ENGLISH_ALPHABET.charAt(i)), index);
                // 排序
                Collections.sort(timeZoneBeans, new PinyinComparator());
                for (int j = 0; j < timeZoneBeans.size(); j++) {
                    timeZoneList.add(new TimeZoneHeadBean(timeZoneBeans.get(j)));
                    index += 1;
                }
                index += 1;
            }
        }
        timeZoneLiveData.postValue(timeZoneList);
    }

    /**
     * 选择时区列表搜索方法
     */
    public void searchTimeZone(String s) {
        if (TextUtils.isEmpty(s)) {
            searchTimeZoneList.clear();
            timeZoneLiveData.postValue(timeZoneList);
        } else {
            searchTimeZoneList.clear();
            for (int i = 0; i < timeZoneList.size(); i++) {
                TimeZoneBean bean = timeZoneList.get(i).getTimeZoneBean();
                if (bean == null) {
                    continue;
                }

                boolean matchedCountryName =
                        (!TextUtils.isEmpty(bean.getCountryName().toLowerCase()) &&
                                bean.getCountryName().toLowerCase().contains(s.toLowerCase()));
                boolean matchedTimezone =
                        (!TextUtils.isEmpty(bean.getTz()) && bean.getTz().contains(s));

                if ( matchedCountryName || matchedTimezone) {
                    searchTimeZoneList.add(timeZoneList.get(i));
                }
            }
            searchTimeZoneData.postValue(searchTimeZoneList);
        }
    }

    /**
     * 获取验证码
     *
     * @param account      账号，邮箱或者手机号
     * @param userPushType 验证码类型（1=登录，2=注册,3=重置密码,4=匿名账号升级）
     */
    public void getCode(String account, int userPushType) {
        UserNetworkManager.getInstance().sendVerifyCode(account, userPushType, new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                codeActionLiveData.postValue("");
            }

            @Override
            public void onError(String code, String error) {
                if ("11008".equals(code)) {
                    error = code;
                }
                codeActionLiveData.postValue(error);
            }
        });
    }

    /**
     * 校验验证码
     *
     * @param account    账号，邮箱或者手机号
     * @param verifyCode 验证码
     */
    public void checkCode(String account, String verifyCode) {
        UserNetworkManager.getInstance().checkVerifyCode(account, verifyCode, new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                checkCodeLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                checkCodeLiveData.postValue(false);
            }
        });
    }

    /**
     * 注册
     *
     * @param account    邮箱或手机号
     * @param areaCode   区域码
     * @param password   密码
     * @param verifyCode 验证码
     */
    public void register(String account, String areaCode, String password, String verifyCode) {
        UserNetworkManager.getInstance().registry(account, areaCode, password, verifyCode, new CallbackListener<UserInfoBean>() {
            @Override
            public void onSuccess(UserInfoBean data) {
                loginWithAccount(account, password, areaCode);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                userLiveData.postValue(null);
            }
        });
    }

    /**
     * 匿名登录
     *
     * @param areaCode 区域码
     */
    public void anonymousLogin(String areaCode) {
        UserNetworkManager.getInstance().anonymousLogin(areaCode, new CallbackListener<UserInfoBean>() {
            @Override
            public void onSuccess(UserInfoBean data) {
                getUserInfo(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                userLiveData.postValue(null);
            }
        });
    }

    /**
     * 账号密码登录
     *
     * @param account  账号，邮箱或手机号
     * @param password 密码
     * @param areaCode 区域码
     */
    public void loginWithAccount(String account, String password, String areaCode) {
        UserNetworkManager.getInstance().loginWithAccount(account, password, areaCode, new CallbackListener<UserInfoBean>() {
            @Override
            public void onSuccess(UserInfoBean data) {
                getUserInfo(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                userLiveData.postValue(null);
            }
        });
    }

    /**
     * 验证码登录
     *
     * @param account  账号，邮箱或手机号
     * @param areaCode 区域码
     * @param code     验证码
     */
    public void loginWithPhone(String account, String areaCode, String code) {
        UserNetworkManager.getInstance().loginWithPhone(account, areaCode, code, new CallbackListener<UserInfoBean>() {
            @Override
            public void onSuccess(UserInfoBean data) {
                getUserInfo(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                userLiveData.postValue(null);
            }
        });
    }

    /**
     * 重置密码
     *
     * @param account    邮箱
     * @param password   密码
     * @param areaCode   区域码
     * @param verifyCode 验证码
     */
    public void resetPassword(String account, String password, String areaCode, String verifyCode) {
        UserNetworkManager.getInstance().resetPassword(account, password, areaCode, verifyCode, new CallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                setPsdLiveData.postValue(true);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                setPsdLiveData.postValue(false);
            }
        });
    }

    private void getUserInfo(UserInfoBean userInfoBean) {
        UserInfoManager.getInstance().saveUserInfo(userInfoBean);
        UserNetworkManager.getInstance().getUserInfo(new CallbackListener<UserInfoBean>() {
            @Override
            public void onSuccess(UserInfoBean data) {
                UserInfoManager.getInstance().saveUserInfo(data);
                userLiveData.postValue(data);
                MqttManager.getInstance().mqttConnectInit(data, getApplication());
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                userLiveData.postValue(null);
            }
        });
    }

    /**
     * 验证码倒计时
     */
    public void countDown() {
        if (timer != null) {
            timer.cancel();
        }

        // 倒计时60秒，一次1秒
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                countDownLiveData.postValue((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                countDownLiveData.postValue(0);
            }
        }.start();
    }


    /**友盟一键登录*/
    /**
     * 账号密码登录
     *
     * @param token    友盟一键登录token
     * @param verifyId 友盟一键登录requestId
     */
    public void loginWithOneClick(String token, String verifyId) {
        UserNetworkManager.getInstance().loginWithOneClick(token, verifyId, new CallbackListener<UserInfoBean>() {
            @Override
            public void onSuccess(UserInfoBean data) {
                getUserInfo(data);
            }

            @Override
            public void onError(String code, String error) {
                ToastUtil.showMsg(error);
                userLiveData.postValue(null);
            }
        });
    }
}
