package com.rinoiot.iflytek.engine;

/**
 * 引擎配置参数
 * @author edwin
 */
public class EngineConstants {
    /**
     * 规则1：填写要保留的原始音频声道号，有几个数字输出音频就有几个声道，第1声道是声道0，第2声道是声道1，以此类推
     * 示例：“1,0”表示输出音频保留原始音频的声道1和0,原始音频的声道1放在输出音频的第1声道，声道0放在输出音频的第2声道
     * 示例：“0,1"表示输出音频保留原始音频的声道0和1,原始音频声道0放在输出音频的第1声道，声道1放在输出音频的第2声道
     * 示例：“2,1,0"表示输出音频保留原始音频的声道2和1,原始音频声道2放在输出音频的第1声道，声道1放在输出音频的第2声道，声道0放在输出音频的第3声道
     * 规则2：-1表示保留声道，但是数据清空
     * 示例：”0,-1"：输出数据保留2个声道，原始音频声道0放在输出音频的第1声道，输出音频的第2声道是空数据
     * 示例："7,-1"：输出数据保留2个声道，原始音频声道7放在输出音频的第1声道，输出音频的第2声道是空数据
     * 示例："5,3,2"：输出数据保留2个声道，原始音频声道5放在输出音频的第1声道，声道3放在输出音频的第2声道，声道2放在输出音频的第3声道
     **/
    public final static String CHANNEL_PARAMS = "2,3,4,5,6,7";

    /**
     * 原始音频音量倍数,默认1.0f
     */
    public static final float RAW_AUDIO_GAIN = 12.0f;
    /**
     * 识别音频音量倍数,默认1.0f
     */
    public static final float ASR_AUDIO_GAIN = 1.0f;

    /**
     * Alsa-Jni录音模块日志控制 1(debug) 2(info) 3(warn) 4(error) 5(fatal)
     */
    public static final int WAKE_ENGINE_LOG_LEVEL = 4;

    /**
     * ini配置文件名,真实路径是wakeupEngineDir/iniPath，例如 /sdcard/vtn/vtn.ini
     * 通用配置
     */
    public static final String INI_PATH = "vtn.ini";

    /**
     * 版本号，用日期标注
     */
    public static final String SDK_VERSION = "2023-05-05";

    @SuppressWarnings("SpellCheckingInspection")
    public static class AiuiAssistant {

        /**
         * 免费深度训练唤醒词：小犀小犀
         */
        public static final  String XIOA_XI_RES = "xiaoxixiaoxi";

        /**
         * 免费深度训练唤醒词：小飞小飞
         */
        public static final String XIAO_FEI_RES = "xiaofeixiaofei";
    }
}
