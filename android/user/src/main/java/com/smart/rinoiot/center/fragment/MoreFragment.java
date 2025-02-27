package com.smart.rinoiot.center.fragment;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import com.smart.rinoiot.center.viewmodel.SetUpViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.bean.UserInfoBean;
import com.smart.rinoiot.common.language.ChangeLanguageManager;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.manager.AppManager;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.DataCleanManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.mqtt2.Manager.TopicManager;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.utils.SystemUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.view.TextInputDialog;
import com.smart.rinoiot.common.view.VoiceAssistantSettingsDialog;
import com.smart.rinoiot.common.voice.VoiceAssistantSettings;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.FragmentMoreBinding;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 更多信息
 */
@SuppressLint({"StringFormatInvalid", "SetTextI18n", "StringFormatMatches"})
public class MoreFragment extends BaseFragment<FragmentMoreBinding, SetUpViewModel>{
    private String[] styleList;
    private String[] languageList;
    private String[] screenList;

    private static final String EN_LANG = "en";

    private static final String ZH_LANG = "zh";

    private static final String TAG = "MoreFragment";

    private TextInputDialog serverAddressInputDialog;

    private VoiceAssistantSettingsDialog voiceAssistantSettingsDialog;

    @Override
    public void init() {
        initData();
    }

    @Override
    public FragmentMoreBinding getBinding(LayoutInflater inflater) {
        return FragmentMoreBinding.inflate(inflater);
    }

    @SuppressWarnings("all")
    private void initData() {
        String currentLanguage = SharedPreferenceUtil.getInstance().get(
                ChangeLanguageManager.LANGUAGE, "en"
        );
        styleList = new String[]{getString(R.string.rino_user_style_dark), getString(R.string.rino_user_style_white)};
        languageList = new String[]{getString(R.string.rino_user_language_chinese), getString(R.string.rino_user_language_english)};
        screenList = new String[]{getString(R.string.rino_user_screen_time_tips_seconds), getString(R.string.rino_user_screen_time_tips_minute),
                String.format(getString(R.string.rino_user_screen_time_tips), 2), String.format(getString(R.string.rino_user_screen_time_tips), 3),
                String.format(getString(R.string.rino_user_screen_time_tips), 5), String.format(getString(R.string.rino_user_screen_time_tips), 10)};


        // set style value
        binding.tvStyleValue.setText(R.string.rino_user_style_dark);
        binding.llStyle.setOnClickListener(v -> showStyleSettingDialog());

        // set the language value
        binding.tvLanguageValue.setText(currentLanguage.equals(ZH_LANG) ?
                R.string.rino_user_language_chinese:
                R.string.rino_user_language_english
        );
        binding.llLanguage.setOnClickListener(v -> showLanguageSettingDialog());

        // set the screen off time.
        int screenOffTime = SharedPreferenceUtil.getInstance().get(Constant.SCREEN_OFF, 3);
        binding.tvScreenOffValue.setText(screenList[screenOffTime]);
        SystemUtil.setSystemScreenOff(getContext(), getScreenOffTime(screenOffTime));
        binding.llScreenOffTime.setOnClickListener(v -> showScreenOffTimeDialog());

        // set the server address
        String address = CacheDataManager.getInstance().getInferenceServerAddress();
        binding.tvServerValue.setText(StringUtil.isBlank(address) ?
                getText(R.string.rino_com_default_server_address) : address
        );

        binding.llServerAddress.setOnClickListener(v -> serverAddressInputDialog.show());

        // voice assistant settings
        binding.llVoiceAssistantSettings.setOnClickListener(view -> {
            VoiceAssistantSettings settings = CacheDataManager.getInstance().getVoiceAssistantSettings();
            voiceAssistantSettingsDialog.setSettings(settings);
            voiceAssistantSettingsDialog.show();
        });

        // set the cache size
        long cacheSize = DataCleanManager.getAllSize(requireContext());
        binding.tvCacheValue.setText(cacheSize == 0 ? "" : DataCleanManager.getFormatSize(
                cacheSize
        ));

        binding.llCache.setOnClickListener(v -> showClearCacheDialog());

        // initialize server address input dialog
        serverAddressInputDialog = new TextInputDialog.Builder(requireContext())
                .setTitle(R.string.rino_com_ai_server_address_modify)
                .setPositiveButton(R.string.rino_common_save, text -> updateInferenceServerAddress(text))
                .create();

        voiceAssistantSettingsDialog = new VoiceAssistantSettingsDialog.Builder(requireContext())
                .setTitle(R.string.rino_com_voice_assistant_settings_modify)
                .setPositiveButton(R.string.rino_common_save, (duration , checked) -> updateVoiceAssistantSettings(duration, checked))
                .create();
    }

    private void updateVoiceAssistantSettings(Duration duration, boolean checked) {
        // set debug http host
        if(duration.getSeconds() >= 10 && duration.getSeconds() <= 30) {
            // update the settings
            VoiceAssistantSettings settings = CacheDataManager.getInstance().getVoiceAssistantSettings();
            settings.setContinuousListeningDuration((int)duration.getSeconds());
            settings.setContinuousMode(checked);
            // cache the settings
            CacheDataManager.getInstance().cacheVoiceAssistantSettings(settings);
            // restart the app
            restart();
        }else{
            ToastUtil.showErrorMsg(R.string.rino_com_invalid_server_address);
        }
    }

    private void updateInferenceServerAddress(String serverAddress){
        // set debug http host
        if(validateAddress(serverAddress)) {
            // cache the server address
            CacheDataManager.getInstance().cacheInferenceServerAddress(serverAddress.trim());
            // restart the app.
            restart();
        }else{
            ToastUtil.showErrorMsg(R.string.rino_com_invalid_server_address);
        }
    }

    private boolean validateAddress(String address) {
        if (StringUtil.isBlank(address)){
            return false;
        }

        try {
            URL url = new URL(address);
            // Check that the protocol is HTTPS
            if (!url.getProtocol().equalsIgnoreCase("https")) {
                return false;
            }

            // Check that the domain name is not empty
            if (url.getHost().isEmpty()) {
                return false;
            }

            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Shows the style settings dialog
     */
    private void showStyleSettingDialog() {
        DialogUtil.showListSelectedDialog(
                getActivity(), getString(R.string.rino_scene_more_style_title), styleList,
                getPosition(styleList, binding.tvStyleValue.getText().toString(), false),
                (text, position) -> binding.tvStyleValue.setText(text)
        );
    }

    /**
     * Shows the language settings dialog
     */
    private void showLanguageSettingDialog(){
        DialogUtil.showListSelectedDialog(getActivity(), getString(R.string.rino_user_language),
                languageList, getPosition(
                        languageList,
                        binding.tvLanguageValue.getText().toString(),
                        false
                ),
                (text, position) -> {
                    if (!TextUtils.equals(text, binding.tvLanguageValue.getText())) {
                        if (TextUtils.equals(text, languageList[0])) {
                            SharedPreferenceUtil.getInstance().put(
                                    ChangeLanguageManager.LANGUAGE, ZH_LANG
                            );
                        } else {
                            SharedPreferenceUtil.getInstance().put(
                                    ChangeLanguageManager.LANGUAGE, EN_LANG
                            );
                        }
                        // restart the app.
                        restart();
                    }
                });
    }

    /**
     * Restarts the app.
     */
    private void restart(){
        mViewModel.showLoading();
        Completable.complete()
                .delay(1, TimeUnit.SECONDS)
                .doOnComplete(() -> {
                    mViewModel.hideLoading();
                    AppManager.getInstance().finishAllActivity();
                    AppManager.getInstance().triggerRebirth(requireContext());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * Shows the screen off settings time dialog
     */
    private void showScreenOffTimeDialog() {
        DialogUtil.showListSelectedDialog(getActivity(), getString(R.string.rino_user_screen_time), screenList,
                getPosition(screenList, binding.tvScreenOffValue.getText().toString(), true),
                (text, position) -> {
                    binding.tvScreenOffValue.setText(text);
                    if (position == -1 || position >= screenList.length) {
                        binding.tvScreenOffValue.setText(screenList[3]);
                        SystemUtil.setSystemScreenOff(getContext(), 0);
                    } else {
                        SharedPreferenceUtil.getInstance().put(Constant.SCREEN_OFF,position);
                        SystemUtil.setSystemScreenOff(getContext(), getScreenOffTime(position));
                    }
                });
    }

    /**
     * Shows the clear cache dialog
     */
    private void showClearCacheDialog(){
        DialogOnListener listener = new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                DataCleanManager.cleanCacheData(requireActivity());
                long cacheSize = DataCleanManager.getAllSize(requireContext());
                binding.tvCacheValue.setText(cacheSize == 0 ? "" : DataCleanManager.getFormatSize(
                        cacheSize
                ));
            }
        };

        DialogUtil.showNormalMsg(
                requireActivity(), "", getString(R.string.rino_mine_clear_cache_tips), listener
        );
    }

    /**
     * 根据当前显示的，获取对应的位置
     */
    private int getPosition(String[] titles, String text, boolean screenOffFlag) {
        int index = screenOffFlag ? 3 : 0;
        for (int i = 0; i < titles.length; i++) {
            if (TextUtils.equals(text, titles[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 获取息屏时间
     */
    private int getScreenOffTime(int position) {
        int time = 3 * 60;
        if (position == 0) {
            time = 30;
        } else if (position == 1) {
            time = 60;
        } else if (position == 2) {
            time = 2 * 60;
        } else if (position == 3) {
            time = 3 * 60;
        } else if (position == 4) {
            time = 5 * 60;
        } else if (position == 5) {
            time = 10 * 60;
        }
        return time;
    }
}
