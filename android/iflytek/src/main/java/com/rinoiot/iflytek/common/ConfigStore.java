package com.rinoiot.iflytek.common;

import com.rinoiot.iflytek.audio.AudioConfig;

/**
 * @author edwin
 */
public class ConfigStore {

    /**
     * Config store instance
     */
    private static ConfigStore instance;

    /**
     * aiui状态，idle(空闲状态)，ready(待唤醒状态,不能对话)，working(已唤醒状态，可以对话)
     */
    private int aiuiState;

    /**
     * aiui唤醒类型，Voice(唤醒引擎送数据给云端识别),TEXT(唤醒引擎不送音频给云端识别，只响应文本请求)
     */
    private int wakeupType;

    /**
     * 无意义词过滤功能：当前识别结果是否有意义
     */
    private boolean meaningful;

    /**
     * 记录初始化次数，ota刷机后设备信息改变，第一次初始化会报错11217，需要再次初始化aiui绑定当前设备
     */
    private boolean firstInit;

    /**
     * Audio config
     */
    private AudioConfig audioConfig;

    /**
     * Getter for the config store instance
     *
     * @return the config store instance
     */
    public static ConfigStore getInstance() {
        if(null != instance) {
            return instance;
        }

        synchronized (ConfigStore.class) {
            // set new instance.
            instance = new ConfigStore();
        }
        return instance;
    }

    public int getAiuiState() {
        return aiuiState;
    }

    public void setAiuiState(int aiuiState) {
        this.aiuiState = aiuiState;
    }

    public int getWakeupType() {
        return wakeupType;
    }

    public void setWakeupType(int wakeupType) {
        this.wakeupType = wakeupType;
    }

    public boolean isMeaningful() {
        return meaningful;
    }

    public void setMeaningful(boolean meaningful) {
        this.meaningful = meaningful;
    }

    public boolean isFirstInit() {
        return firstInit;
    }

    public void setFirstInit(boolean firstInit) {
        this.firstInit = firstInit;
    }

    public AudioConfig getAudioConfig() {
        return audioConfig;
    }

    public void setAudioConfig(AudioConfig audioConfig) {
        this.audioConfig = audioConfig;
    }
}
