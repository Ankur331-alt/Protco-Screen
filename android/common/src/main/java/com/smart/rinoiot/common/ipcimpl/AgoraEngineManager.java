package com.smart.rinoiot.common.ipcimpl;

import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

public class AgoraEngineManager {
    static RtcEngine sRtcEngine;

    public static RtcEngine create(RtcEngineConfig config) {
        if (sRtcEngine == null) {
            try {
                sRtcEngine = RtcEngine.create(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sRtcEngine;
    }

    public static void detroy() {
        if (sRtcEngine != null) {
            sRtcEngine.leaveChannel();
            sRtcEngine = null;
        }
        RtcEngine.destroy();
    }
}
