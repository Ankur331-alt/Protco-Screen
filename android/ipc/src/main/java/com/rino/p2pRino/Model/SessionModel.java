package com.rino.p2pRino.Model;

import com.p2p.pppp_api.st_PPCS_Session;
import com.rino.p2pRino.Util.LogUtil;
import com.rino.p2pRino.Util.RINO_APIs;

public class SessionModel {

    public final int session;
    public final int skt;
    public final int connect_time;
    public final String did;
    public String mode;

    public final String remote_ip;
    public final String my_lan_ip;
    public final String my_wan_ip;

    public final int remote_port;
    public final int my_lan_port;
    public final int my_wan_port;

    public final boolean is_device;

    public boolean supportPkt = true;

    public SessionModel(int session, st_PPCS_Session sInfo) {

        this.did = sInfo.getDID();
        this.session = session;
        this.skt = sInfo.getSkt();
        this.connect_time = sInfo.getConnectTime();
        this.remote_ip = sInfo.getRemoteIP();
        this.my_lan_ip = sInfo.getMyLocalIP();
        this.my_wan_ip = sInfo.getMyWanIP();

        this.remote_port = sInfo.getRemotePort();
        this.my_lan_port = sInfo.getMyLocalPort();
        this.my_wan_port = sInfo.getMyWanPort();

        this.is_device = sInfo.getCorD() == 1;

        if (sInfo.getMode() == 0) {
            if (checkIsLanIP(sInfo.getMyLocalIP(), sInfo.getRemoteIP())) {
                this.mode = "LAN";
            } else {
                this.mode = "P2P";
            }
            if (RINO_APIs.getSocketType(sInfo.getSkt()) == 1) {
                this.mode = this.mode + ".";
                this.supportPkt = false;
            }
        } else if (sInfo.getMode() == 1) {
            this.mode = "RLY";
        } else if (sInfo.getMode() == 2) {
            this.mode = "TCP";
            this.supportPkt = false;
        } else if (sInfo.getMode() == 3) {
            this.mode = "RP2P";
        }
    }

    boolean checkIsLanIP(String ip1, String ip2) {

        LogUtil.d("checkIsLanIP " + ip1 + " " + ip2);
        String[] strings1 = ip1.split("\\.");
        String[] strings2 = ip2.split("\\.");

        if (strings1.length != 4 || strings2.length != 4) return false;

        for (int item = 0; item < 3; item++) {
            if (!strings1[item].equals(strings2[item])) return false;
        }
        return true;
    }

    public String toString() {

        StringBuilder builder;
        builder = new StringBuilder();
        builder.append("----------- Session info: ").append(this.session).append(" -----------");
        builder.append("\nSocket: ").append(this.skt);
        builder.append("\nRemote Addr: ").append(this.remote_ip).append(":").append(this.remote_port);
        builder.append("\nMy Wan Addr: ").append(this.my_wan_ip).append(":").append(this.my_wan_port);
        builder.append("\nMy Lan Addr: ").append(this.my_lan_ip).append(":").append(this.my_lan_port);
        builder.append("\nConnection time: ").append(this.connect_time).append(" second before");
        builder.append("\nDID: ").append(this.did);
        builder.append("\nI am ").append(this.is_device ? "Device" : "Client");
        builder.append("\nConnection mode: ").append(this.mode);
        builder.append("\n---------- End of Session info ----------\n");

        return builder.toString();
    }
}
