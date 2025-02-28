package com.smart.rinoiot.center.fragment;

import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.smart.rinoiot.center.activity.CenFlashActivity;
import com.smart.rinoiot.center.socket.HeartBeat;
import com.smart.rinoiot.center.viewmodel.CenLoginViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.AppQrLogInMsgBean;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.mqtt2.Manager.TopicManager;
import com.smart.rinoiot.common.utils.AgreementPolicyUtils;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.DpUtils;
import com.smart.rinoiot.common.utils.PartClickUtil;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.view.QRCodeUtils;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.FragmentScanLoginBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 扫码登录
 */
public class ScanLoginFragment extends BaseFragment<FragmentScanLoginBinding, CenLoginViewModel>
        implements View.OnClickListener {

    private static final String TAG = "ScanLoginFragment";

    /**
     * The current login token
     */
    private String mqttToken = "";

    /**
     * App qr login topic prefix
     */
    private static final String QR_CODE_LOGIN_TEMP_CREDENTIALS_PREFIX = "app_qr_login:";

    @Override
    public void init() {
        initData();
    }

    @Override
    public FragmentScanLoginBinding getBinding(LayoutInflater inflater) {
        return FragmentScanLoginBinding.inflate(inflater);
    }

    private void initData() {
        String[] title = {getString(R.string.rino_user_user_agreement), getString(R.string.rino_user_privacy_policy)};
        String[] url = {AgreementPolicyUtils.getAgreement(getContext()), AgreementPolicyUtils.getPrivacyPolicy(getContext())};
        binding.tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvPrivacy.setText(PartClickUtil.getPrivacyCenText(getContext(), getResources().getString(R.string.rino_user_agree_tip), title, url, getResources().getColor(R.color._xpopup_cancel)));
        binding.ivAgree.setOnClickListener(this);

        binding.ivScanLoginCode.setOnClickListener(this);
        mViewModel.getTempCredentialsLiveData().observe(this, tempCredentials -> {
            if (!binding.ivAgree.isSelected()) {
                return;
            }
            AppExecutors.getInstance().networkIO().execute(() -> AppExecutors.getInstance().mainThread().execute(() -> {
                Bitmap bitmap = QRCodeUtils.getInstance().createQRCodeBitmap(tempCredentials, DpUtils.dip2px(140), DpUtils.dip2px(140));
                AppExecutors.getInstance().mainThread().execute(() -> {
                    binding.ivScanLoginCode.setImageBitmap(bitmap);
                    countDown();
                    if (tempCredentials.startsWith(QR_CODE_LOGIN_TEMP_CREDENTIALS_PREFIX)) {
                        MqttManager.getInstance().tempMqttClientConnect(tempCredentials);
                        subscribeToAppQrLogin(tempCredentials);
                    }
                });
            }));
        });

        mViewModel.getVerityLiveData().observe(this, userInfoBean -> {
            stopTimer();
            UserInfoManager.getInstance().saveUserInfo(userInfoBean);
            ((CenFlashActivity) getActivity()).gotoNextFragment(new BindSuccessFragment());
        });

        // register the event bus
        EventBus.getDefault().register(this);
    }

    /**
     * Subscribe to app qr login topic
     *
     * @param tempCredentials temporary credentials
     */
    private void subscribeToAppQrLogin(String tempCredentials) {
        String token = extractToken(tempCredentials);
        if (StringUtil.strIsNull(token)) {
            Log.e(TAG, "subscribeToAppQrLogin: failed to extract token from credentials");
            return;
        }

        // cache token in this fragment's lifecycle
        this.mqttToken = token;
        // start subscriptions
        MqttManager.getInstance().addSubscriptionTopic(TopicManager.getAppQrLoginTopic(token));
    }

    /**
     * Extracts the token from temporary login credentials
     *
     * @param tempCredentials temporary credentials
     * @return the token
     */
    private String extractToken(String tempCredentials) {
        // tokenize the temp credentials
        List<String> chunks = Arrays.asList(
                tempCredentials.split(QR_CODE_LOGIN_TEMP_CREDENTIALS_PREFIX)
        );

        // check the chunks
        int expectedChunks = 2;
        if (chunks.size() != expectedChunks) {
            // ToDo() consider throwing something
            return null;
        }

        return chunks.get(1);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivAgree) {
            binding.ivAgree.setSelected(!binding.ivAgree.isSelected());
            if (binding.ivAgree.isSelected()) {
                mViewModel.getTempCredentials();
            } else {
                stopTimer();
                binding.ivScanLoginCode.setImageResource(R.drawable.icon_scan_login_code);
            }
        }
    }

    /**
     * 生成的二维码3分钟超时
     */
    private CountDownTimer timer;

    private void countDown() {
        stopTimer();
        timer = new CountDownTimer(3 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                if (binding.ivAgree.isSelected()) {
                    mViewModel.getTempCredentials();
                    countDown();
                } else {
                    stopTimer();
                }
            }
        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
        HeartBeat.getInstance().closeConnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusNotification(AppQrLogInMsgBean loginMessage) {
        // unsubscribe from app qr login token
        MqttManager.getInstance().unSubscribe(TopicManager.getAppQrLoginTopic(this.mqttToken));
        // process message
        Log.d(TAG, "onEventBusNotification: " + new Gson().toJson(loginMessage));
        if (null == loginMessage) {
            return;
        }

        // cache user info
        cacheUserInfo(loginMessage);

        // fetch info from server
        mViewModel.getUserInfo();
    }

    /**
     * Cache the received user info
     *
     * @param loginMessage the login message
     */
    private void cacheUserInfo(AppQrLogInMsgBean loginMessage) {
        // convert bytes to Parcel
        String userInfo = SharedPreferenceUtil.getInstance().get("user_info_bean", "");
        byte[] bytes = Base64.decode(userInfo.getBytes(), Base64.DEFAULT);
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);

        // update user bean with what we have at the moment
        if (loginMessage != null && !TextUtils.isEmpty(loginMessage.getClientId())) {
            Constant.CLIENT_ID = loginMessage.getClientId();
            SharedPreferenceUtil.getInstance().put(Constant.MQTT_CLIENT_ID, Constant.CLIENT_ID);
        }
        UserInfoBean userInfoBean = new UserInfoBean(parcel);
        userInfoBean.id = loginMessage.getUserId();
        userInfoBean.accessToken = loginMessage.getAccessToken();
        userInfoBean.refreshToken = loginMessage.getRefreshToken();
        userInfoBean.nickname = loginMessage.getNickname();
        userInfoBean.tz = loginMessage.getTz();
        userInfoBean.avatarUrl = loginMessage.getAvatarUrl();
        UserInfoManager.getInstance().saveUserInfo(userInfoBean);

        // recycle the parcel
        parcel.recycle();
    }
}
