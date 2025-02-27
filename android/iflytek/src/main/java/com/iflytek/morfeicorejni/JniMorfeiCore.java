package com.iflytek.morfeicorejni;

/**
 * created by kunzhang
 * on 2020/8/25
 */
public class JniMorfeiCore {
    static {
        System.loadLibrary("morfeicore");
        System.loadLibrary("morfeicoresdk");
    }

    /**
     *启动MorfeiCore
     * @param params
     * @param statusCb
     * @param obj
     * @return 0成功
     */
    public static native int start(String params, String statusCb, Object obj);


    /**
     *  增加lua脚本
     * @param name lua脚本名
     * @param lmodbin 二进制数据数组
     * @param binsz 数组长度
     * @return 0 代表成功
     */
    public static native int addlua(String name, byte[] lmodbin, int binsz);

    /**
     * 加载lua脚本
     * @param luaName lua脚本名
     * @param params 为null
     * @return 返回对应的loadid
     */
    public static native String loadlua(String luaName, String params);

    /**
     * 异步调用
     * @param loadId
     * @param msg  消息类型
     * @param types 传递的数据的类型数组
     * @param values 传递的具体数据数组
     * @return 返回异步调用0成功
     */
    public static native int asyncCalllua(String loadId, int msg, int[] types, Object[] values);

    /**
     * 注册lua回调
     * @param loadId
     * @param cbName 注册函数名称
     * @param luaCb 脚本回调函数
     * @return 返回注册lua回调是否成功，0成功
     */
    public static native int registerLuacb(String loadId, String cbName, String luaCb);

    /**
     * 停止morfeicore
     * @return 0成功
     */
    public static native int stop();


}
