package com.rinoiot.iflytek.engine;

/**
 * created by kunzhang
 * on 2020/8/27
 */
public interface MofeiCoreVuiListener {
    /**
     * 识别结果监听
     * @param type
     * 云端识别结果：MorfeiCoreType.ASR_RESULT_CLOUND 本地识别结果：MorfeiCoreType.ASR_RESULT_LOCAL
     * @param result
     * 识别内容
     */
    void onAsrResult(int type, String result);

    /**
     * 合成音频监听
     * @param audio
     * 合成音频数据（pcm，16k，16bit）
     * @param is_last
     * 非最后一块音频为0，最后一块音频为1
     */
    void onTtsAudio(byte[] audio, int is_last);

    /**
     * 唤醒成功监听
     * @param wakeupInfo
     * 唤醒消息内容（保留使用，不需要解析）
     */
    void onWakeup(String wakeupInfo);

    /**
     * 状态监听
     * @param type
     * MorfeiCoreType.STATUS_TYPR_ERROR 发生错误，code为错误码，detail为错误详细描述
     * MorfeiCoreType.STATUS_TYPR_SLEEP VUI休眠，需要再次唤醒进行识别
     * @param code
     * 发生错误时的错误码
     * @param detail
     * 发生错误时的详细信息
     */
    void onStatus(int type, int code, String detail);

    /**
     * 状态监听
     * @param type
     * MorfeiCoreType.ENT_RES_RESULT_UPLOAD 上传动态实体个性化资源结果信息通知回调
     * MorfeiCoreType.ENT_RES_RESULT_CHECK 检查动态实体个性化资源结果信息通知回调
     * @param code
     * 发生错误时的错误码，成功为0（ENT_RES_RESULT_UPLOAD时，仅表示资源数据上传成功，对资源的正确性需要进行检查）
     * @param sid
     * type值为ENT_RES_RESULT_UPLOAD时，为上传动态实体个性化资源返回的sid，用于调用checkEntityRes检查上传资源的正确性
     * type值为ENT_RES_RESULT_CHECK时，checkEntityRes检查的sid
     * @param detail
     * 详细信息描述
     */
    void onEntityResult(int type, int code, String sid, String detail);
}
