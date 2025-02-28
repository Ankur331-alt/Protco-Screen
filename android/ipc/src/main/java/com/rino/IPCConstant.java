package com.rino;

public class IPCConstant {

    public static final String IPC_CONNECT_STATUS_INITIALIZATION = "Initialization";
    public static final String IPC_CONNECT_STATUS_PREPARING = "Preparing";
    public static final String IPC_CONNECT_STATUS_INITIALIZATION_FAILED = "Initialization Failed";
    public static final String IPC_CONNECT_STATUS_CONNECTING = "Connecting";
    public static final String IPC_CONNECT_STATUS_CONNECTING_FAILED = "Connection failed";
    public static final String IPC_CONNECT_STATUS_DISCONNECT = "Disconnect";
    public static final String IPC_CONNECT_STATUS_CONNECTED = "Connected";
    public static final String IPC_CONNECT_STATUS_PULL_STREAM = "Pull stream";
    public static final String IPC_CONNECT_STATUS_PULL_STREAM_FAILED = "Pull stream failed";
    public static final String IPC_CONNECT_STATUS_PULL_PLAYING = "Playing";

    //IPC三方SDK业务内部出错状态码，此状态码把对三方业务往上抛
    public static final int IPC_CONNECT_ERROR_BUSINESS= 30000;
    public static final int IPC_CONNECT_ERROR_PREPARING = 30001;
    public static final int IPC_CONNECT_ERROR_INITIALIZATION = 30002;
    public static final int IPC_CONNECT_ERROR_CONNECT = 30003;
    public static final int IPC_CONNECT_ERROR_PULL_STREAM = 30004;

}
