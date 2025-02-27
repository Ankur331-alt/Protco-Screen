package com.rinoiot.iflytek.engine;

/**
 * created by kunzhang
 * on 2020/8/25
 */
public class MorfeiCoreType {
    /* morfeicore data type */
    public static final int MORFEICORE_TBOOLEAN = 1;
    public static final int MORFEICORE_TNUMBER = 2;
    public static final int MORFEICORE_TSTRING = 3;
    public static final int MORFEICORE_TBUFFER = 4;
    public static final int MORFEICORE_TUNKNOWN = 5;

    /* status type  */
    public static final int STATUS_TYPR_ERROR = 1;  
    public static final int STATUS_TYPR_SLEEP = 2;
    public static final int eStatTYPE_ERROR_ASR     = 3;
    public static final int eStatTYPE_ERROR_TTS     = 4;
    public static final int eStatTYPE_ERROR_CAE_IVW = 5;
    public static final int eStatTYPE_ERROR_ENTITY  = 6;

    /* asr result type  */
    public static final int ASR_RESULT_CLOUD = 1;
    public static final int ASR_RESULT_LOCAL  = 2;

    /* asr mode type  */
    public static final int ASR_MODE_CLOUD = 1;
    public static final int ASR_MODE_LOCAL  = 2;

    /* interaction mode type  */
    public static final int INTACT_MODE_ONESHOT        = 1;
    public static final int INTACT_MODE_CONTINUE       = 2;
    public static final int INTACT_MODE_FULL_CONTINUE  = 3;

    /* audio status type  */
    public static final int AUDIO_STATUS_CONTINUE        = 1;
    public static final int AUDIO_STATUS_LAST            = 2;

    /* entity callback type  */
    public static final int ENT_RES_RESULT_UPLOAD        = 1;
    public static final int ENT_RES_RESULT_CHECK         = 2;

}
