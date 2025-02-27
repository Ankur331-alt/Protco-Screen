package com.rinoiot.iflytek.engine;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.iflytek.morfeicorejni.JniMorfeiCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * created by kunzhang
 * on 2020/8/25
 * 语音交互接口VUI（Voice User Interface）
 */
public class MorfeiCoreVui {
    private static  final  String TAG = "MorfeiCoreVui";
    private static MorfeiCoreVui instance = null;
    private ArrayList<MofeiCoreVuiListener> mVUIListeners = new ArrayList<>();
    private static Context mContext;
    private static final String scriptAddName = "mfcore_vui";
    private static String loadid;
    /*************global msg**********************/
    private static final int MFCORE_VUI_MSG_INIT         = 1001;
    private static final int MFCORE_VUI_MSG_UNINIT       = 1002;
    private static final int MFCORE_VUI_MSG_RESET        = 1003;
    private static final int MFCORE_VUI_MSG_UPLOAD_ENT_RES = 1004;
    private static final int MFCORE_VUI_MSG_CHECK_ENT_RES  = 1005;
    /*************asr************************/
    private static final int MFCORE_VUI_MSG_INTACT_MODE  = 2001;
    private static final int MFCORE_VUI_MSG_ASR_MODE     = 2002;

    private static final int MFCORE_VUI_MSG_START_ASR    = 2003;
    private static final int MFCORE_VUI_MSG_STOP_ASR     = 2004;

    private static final int MFCORE_VUI_MSG_WRITE_AUDIO  = 2005;
    private static final int MFCORE_VUI_MSG_WAKEUP       = 2006;
    private static final int MFCORE_VUI_MSG_NLP_TEXT     = 2007;
    /*************tts*************************/
    private static final int MFCORE_VUI_MSG_START_TTS    = 3001;
    private static final int MFCORE_VUI_MSG_STOP_TTS     = 3002;
    private static final int MFCORE_VUI_MSG_SYNTH_TEXT   = 3003;
    private static final int MFCORE_VUI_MSG_PLAY_BEGIN   = 3004;
    private static final int MFCORE_VUI_MSG_PLAY_END     = 3005;
    private static final int MFCORE_VUI_MSG_BREAK_TTS    = 3006;
    /*************cae*************************/
    private static final int MFCORE_VUI_MSG_START_CAEIVW    = 4001;
    private static final int MFCORE_VUI_MSG_STOP_CAEIVW     = 4002;

    private static final int MFCORE_VUI_MSG_UPDATE_RES      = 4004;
    private static final int MFCORE_VUI_MSG_CREATE_RES      = 4005;



    private static byte[]  readScriptFile(){
        byte[] scriptByte = null;
        InputStream is = null;
        int len = 0;
        File scriptFile = new File("/sdcard/"+ MorfeiCoreVuiCfg.scriptName);
        try {
            if (scriptFile.exists()){
                is = new FileInputStream(scriptFile);
            }
            else {
                AssetManager am = mContext.getAssets();
                is = am.open(MorfeiCoreVuiCfg.scriptPreDir+ MorfeiCoreVuiCfg.scriptName);
            }
            len = is.available();
            scriptByte = new byte[len];
            is.read(scriptByte);
            is.close();
            return  scriptByte;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    protected void stateCb(int type, int status, int param1, String param2) {
        Log.d(TAG,"stateCb | type :" + type + "; status :" + status + "param1 :" + param1 + "; param2 :" + param2);
    }

    protected void ttsAuiodCb(int[] types, Object[] values){
        Log.d(TAG, "***********ttsAuiodCb****************");
        for (MofeiCoreVuiListener listener : mVUIListeners){
            listener.onTtsAudio((byte[]) values[0], (int)Double.parseDouble((String)values[1]));
        }
    }

    protected void asrRstCb(int[] types, Object[] values){
        Log.d(TAG, "***********asrRstCb****************");
        if (types.length != 2){
            return;
        }
        for (MofeiCoreVuiListener listener : mVUIListeners){
            listener.onAsrResult((int)Double.parseDouble((String)values[0]), (String) values[1]);
        }

    }
    protected void wakeupCb(int[] types, Object[] values){
        Log.d(TAG, "************wakeupCb****************");
        for (MofeiCoreVuiListener listener : mVUIListeners){
            listener.onWakeup((String) values[0]);
        }
    }

    protected void statusCb(int[] types, Object[] values){
        Log.d(TAG, "************statusCb****************");
        for (int i = 0; i < types.length; i++) {
            Log.d(TAG, "luaCb type:" + types[i] + " value:" + values[i]);
        }
        int statusType = (int)Double.parseDouble((String)values[0]);
        int statusCode = 0;
        String detail = null;
        int paramNum = types.length;
        switch (paramNum){
            case 2:{
                statusCode =  (int)Double.parseDouble((String)values[1]);
            }
            break;
            case 3:{
                statusCode =  (int)Double.parseDouble((String)values[1]);
                detail = (String)values[2];
            }
            break;
        }
        for (MofeiCoreVuiListener listener : mVUIListeners){
            listener.onStatus(statusType, statusCode, detail);
        }
    }
    protected void entityRsltCb(int[] types, Object[] values){
        Log.d(TAG, "************entityRsltCb****************");
        for (int i = 0; i < types.length; i++) {
            Log.d(TAG, "luaCb type:" + types[i] + " value:" + values[i]);
        }
        int entType = (int)Double.parseDouble((String)values[0]);
        int code = (int)Double.parseDouble((String)values[1]);
        String detail = null;
        String sid = null;
        int paramNum = types.length;
        switch (paramNum){
            case 3:{
                if (types[2] != MorfeiCoreType.MORFEICORE_TUNKNOWN){
                    sid = (String)values[2];
                }
            }
            break;
            case 4:{
                if (types[2] != MorfeiCoreType.MORFEICORE_TUNKNOWN){
                    sid = (String)values[2];
                }
                if (types[3] != MorfeiCoreType.MORFEICORE_TUNKNOWN){
                    detail = (String)values[3];
                }
            }
            break;
        }
        for (MofeiCoreVuiListener listener : mVUIListeners){
            listener.onEntityResult(entType, code, sid, detail);
        }
    }

    private MorfeiCoreVui(){
    }

    /**
     * 获得VUI（Voice User Interface）实例，初始化语音交互
     * @param ctx
     * @return
     */
    public static MorfeiCoreVui getInstance(final Context ctx, final String initParams) {
        if(null == ctx){
            Log.e(TAG,"getInstance | ctx = null");
            return null;
        }
        mContext = ctx.getApplicationContext();
        if (null == mContext) {
            Log.e(TAG,"getInstance | appCtx = null");
            return null;
        }
        if (instance == null) {
            synchronized(MorfeiCoreVui.class){
                if (null == instance){
                    byte scriptByte[] = readScriptFile();
                    if (null == scriptByte) {
                        Log.e(TAG,"readScriptFile failed");
                        return null;
                    }
                    instance = new MorfeiCoreVui();
                    int retVal;
                    retVal = JniMorfeiCore.start(initParams, "stateCb", instance);
                    if (retVal != 0){
                        Log.e(TAG,"JniMorfeiCore.start failed ret:" + retVal);
                        instance = null;
                        return null;
                    }
                    JniMorfeiCore.addlua(scriptAddName, scriptByte, scriptByte.length);
                    loadid = JniMorfeiCore.loadlua(scriptAddName, null);
                    if (null == loadid){
                        Log.e(TAG,"JniMorfeiCore.loadlua failed");
                        instance = null;
                        return null;
                    }
                    Log.d(TAG,"JniMorfeiCore.loadlua loadid" + loadid);
                    JniMorfeiCore.registerLuacb(loadid, "lua_cb_asr_result", "asrRstCb");
                    JniMorfeiCore.registerLuacb(loadid, "lua_cb_wakeup", "wakeupCb");
                    JniMorfeiCore.registerLuacb(loadid, "lua_cb_tts_audio", "ttsAuiodCb");
                    JniMorfeiCore.registerLuacb(loadid, "lua_cb_status", "statusCb");
                    JniMorfeiCore.registerLuacb(loadid, "lua_cb_ent_res_rslt", "entityRsltCb");
                    int paramType[] = new int[1];
                    Object[] paramValue = new Object[1];
                    paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
                    paramValue[0] = initParams;
                    JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_INIT, paramType, paramValue);
                }
            }
        }
        return instance;
    }

    /**
     * 添加监听
     * @param listener
     * 监听器
     */
    public void addVUIListener(MofeiCoreVuiListener listener){
        if(null != listener){
            mVUIListeners.add(listener);
        }
    }

    /**
     * 对VUI进行重置，打断正在进行的合成和识别，恢复各VUI功能到start之后状态
     * @return
     */
    public int reset(){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(0);
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_RESET, paramType, paramValue);
    }

    /**
     * 上传动态实体资源
     * @param params 动态实体资源的参数部分
     * @param data  动态实体资源的数据部分
     * @param res_id 连续多次上传时，onEntityResult中通过detail携带res_id区分是哪次上传的资源回调（不连续上传可以不关注，填值“uploadRes1”）
     * @return
     * 0-接口调用成功 other-失败
     */
    public int uploadEntityRes(final String params, final String data, final String res_id){
        int paramType[] = new int[3];
        Object[] paramValue = new Object[3];
        paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[0] = params;
        paramType[1] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[1] = data;
        paramType[2] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[2] = res_id;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_UPLOAD_ENT_RES, paramType, paramValue);
    }

    /**
     * 检查动态实体资源数据是否正确
     * @param sid  onEntityResult中type值为ENT_RES_RESULT_UPLOAD时返回的sid
     * @return
     * 0-接口调用成功 other-失败
     */
    public int checkEntityRes(final String sid){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[0] = sid;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_CHECK_ENT_RES, paramType, paramValue);
    }

    /**
     * 设置语音识别交互模式
     * @param mode 模式选择
     * INTACT_MODE_ONESHOT单轮交互，唤醒一次进行一次识别
     * INTACT_MODE_HALF_CONTINUE 半双工交互， 在播音时不进行识别， 播音结束继续识别（播音开始和结束需要调用playBegin， playEnd接口）
     * INTACT_MODE_CONTINUE 全双工交互，唤醒一次可以连续识别
     * @param params
     *  如"intact_time=6000" 交互持续时间，单位ms，默认为6s
     * @return
     * 0-设置成功 other-失败
     */
    public int setIntactMode(int mode, final String params){
        int paramType[] = new int[2];
        Object[] paramValue = new Object[2];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(mode);;
        paramType[1] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[1] = params;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_INTACT_MODE, paramType, paramValue);
    }

    /**
     * 设置识别模式
     * @param mode
     * ASR_MODE_CLOUND 云端识别
     * ASR_MODE_LOCAL 本地识别 （暂不支持）
     * @param params
     * 保留
     * @return
     * 0-设置成功 other-失败
     */
    public int setASRMode(int mode, final String params){
        int paramType[] = new int[2];
        Object[] paramValue = new Object[2];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(mode);
        paramType[1] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[1] = params;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_ASR_MODE, paramType, paramValue);
    }

    /**
     * 开启识别功能
     * @param params
     * 识别需要参数（详见demo）
     * @return
     * 0-设置成功 other-失败
     */
    public int startASR(final String params){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[0] = params;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_START_ASR, paramType, paramValue);
    }

    /**
     * 停止识别功能
     * @return
     * 0-设置成功 other-失败
     */
    public int stopASR(){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(0);
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_STOP_ASR, paramType, paramValue);
    }

    /**
     * 对文本进行语义识别，结果通过onAsrResult返回
     * @param text
     * 文本内容 如：“合肥今天天气”
     * @return
     * 0-设置成功 other-失败
     */
    public int nlpText(final String text){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[0] = text;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_NLP_TEXT, paramType, paramValue);
    }

    /**
     * 向VUI写入音频
     * @param audio
     * 音频数据
     * @param status
     * AUDIO_STATUS_CONTINUE 后续还有音频，AUDIO_STATUS_LAST最后一块音频
     * @return
     * 0-设置成功 other-失败
     */
    public int writeAudio(byte[] audio, int status){
        int paramType[] = new int[2];
        Object[] paramValue = new Object[2];
        paramType[0] = MorfeiCoreType.MORFEICORE_TBUFFER;
        paramValue[0] = audio;
        paramType[1] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[1] = Integer.toString(status);;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_WRITE_AUDIO, paramType, paramValue);
    }

    /**
     * 主动唤醒，外部主动调用此接口触发开始识别
     * @param params
     * 保留
     * @return
     * 0-设置成功 other-失败
     */
    public int wakeup(String params){
        int[] paramType = null;
        Object[] paramValue = null;
        if (params != null){
            paramType = new int[1];
            paramValue = new Object[1];
            paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
            paramValue[0] = params;
        }
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_WAKEUP, paramType, paramValue);
    }

    /**
     * 开启合成功能
     * @param params
     * 合成需要参数
     * @return
     * 0-设置成功 other-失败
     */
    public int startTTS(String params){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[0] = params;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_START_TTS, paramType, paramValue);
    }

    /**
     * 停止合成功能
     * @return
     * 0-设置成功 other-失败
     */
    public int stopTTS(){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(0);;;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_STOP_TTS, paramType, paramValue);
    }

    /**
     * 传入文本进行语音合成
     * @param text
     * 需要合成的文本(utf-8)
     * @return
     * 0-设置成功 other-失败
     */
    public int synthText(String text){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[0] = text;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_SYNTH_TEXT, paramType, paramValue);
    }

    /**
     * 打断正在进行的语音合成
     * @return
     * 0-设置成功 other-失败
     */
    public int breakTTS(){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(0);
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_BREAK_TTS, paramType, paramValue);
    }

    /**
     * 播音开始（INTACT_MODE_HALF_CONTINUE交互模式下使用）
     * @return
     * 0-设置成功 other-失败
     */
    public int playBegin(){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(0);
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_PLAY_BEGIN, paramType, paramValue);
    }
    /**
     * 更新唤醒词资源，外部主动调用此接口
     * @param params
     * 保留
     * @return
     * 0-设置成功 other-失败
     */
    public int upadateRes(String params){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[0] = params;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_UPDATE_RES, paramType, paramValue);
    }

    /**
     * 自定义唤醒词资源，外部主动调用此接口
     * 保留
     * @return
     * 0-设置成功 other-失败
     */
    public int createRes(String keyword,String savepath,int addflag){
        int paramType[] = new int[3];
        Object[] paramValue = new Object[3];
        paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[0] = keyword;
        paramType[1] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[1] = savepath;
        paramType[2] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[2] = Integer.toString(addflag);
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_CREATE_RES, paramType, paramValue);
    }
    /**
     * 播音结束（INTACT_MODE_HALF_CONTINUE交互模式下使用）
     * @return
     * 0-设置成功 other-失败
     */
    public int playEnd(){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(0);
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_PLAY_END, paramType, paramValue);
    }

    /**
     * 开启降噪和唤醒功能
     * @param params
     * 降噪唤醒所需参数
     * @return
     * 0-设置成功 other-失败
     */
    public int startCaeIvw(String params){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TSTRING;
        paramValue[0] = params;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_START_CAEIVW, paramType, paramValue);
    }

    /**
     * 停止降噪唤醒功能
     * @return
     * 0-设置成功 other-失败
     */
    public int stopCaeIvw(){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(0);;;
        return JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_STOP_CAEIVW, paramType, paramValue);
    }

    /**
     * 销毁VUI
     */
    public void destroy(){
        int paramType[] = new int[1];
        Object[] paramValue = new Object[1];
        paramType[0] = MorfeiCoreType.MORFEICORE_TNUMBER;
        paramValue[0] = Integer.toString(0);
        JniMorfeiCore.asyncCalllua(loadid, MFCORE_VUI_MSG_UNINIT, paramType, paramValue);
        JniMorfeiCore.stop();
        instance = null;
    }
}
