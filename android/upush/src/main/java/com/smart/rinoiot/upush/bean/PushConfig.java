package com.smart.rinoiot.upush.bean;

public class PushConfig {
    //华为
    private String huaweiAppId = "appid=106659207";
    //oppo
    private String oppoKey="891cd5f9be5f4df4b77f8465ecb54971";
    private String oppoSecret="57d3a30487aa4ca0a448c1a01d48dc9f";
    //小米
    private String miAppId = "2882303761520170874";
    private String miKey = "5332017093874";
    //vivo
    private String vivoAppId = "105574370";
    private String vivoAppSecret = "d75842f5-9514-4263-ae0e-0ec554eded11";
    //友盟
    private String umKey = "62c7f9f788ccdf4b7ec37c93";
    private String messageSecret = "0b24903ded947bf828550256204f3e46";
    private String masterSecret = "lumatziznszyndbgvy6afegqyl8alqcs";
    //魅族
    private String meiZuId = "116090";
    private String meiZuKey = "9413f9968d654df092afecf4d3782391";

    private String packageName = "com.smart.rinoiot";

    public PushConfig() {
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getHuaweiAppId() {
        return this.huaweiAppId;
    }

    public void setHuaweiAppId(String huaweiAppId) {
        this.huaweiAppId = huaweiAppId;
    }

    public String getOppoKey() {
        return this.oppoKey;
    }

    public void setOppoKey(String oppoKey) {
        this.oppoKey = oppoKey;
    }

    public String getOppoSecret() {
        return this.oppoSecret;
    }

    public void setOppoSecret(String oppoSecret) {
        this.oppoSecret = oppoSecret;
    }

    public String getMiAppId() {
        return this.miAppId;
    }

    public void setMiAppId(String miAppId) {
        this.miAppId = miAppId;
    }

    public String getMiKey() {
        return miKey;
    }

    public void setMiKey(String miKey) {
        this.miKey = miKey;
    }

    public String getVivoAppId() {
        return this.vivoAppId;
    }

    public void setVivoAppId(String vivoAppId) {
        this.vivoAppId = vivoAppId;
    }

    public String getVivoAppSecret() {
        return this.vivoAppSecret;
    }

    public void setVivoAppSecret(String vivoAppSecret) {
        this.vivoAppSecret = vivoAppSecret;
    }

    public String getUmKey() {
        return this.umKey;
    }

    public void setUmKey(String umKey) {
        this.umKey = umKey;
    }

    public String getMessageSecret() {
        return this.messageSecret;
    }

    public void setMessageSecret(String messageSecret) {
        this.messageSecret = messageSecret;
    }

    public String getMasterSecret() {
        return masterSecret;
    }

    public void setMasterSecret(String masterSecret) {
        this.masterSecret = masterSecret;
    }

    public String getMeiZuId() {
        return meiZuId;
    }

    public void setMeiZuId(String meiZuId) {
        this.meiZuId = meiZuId;
    }

    public String getMeiZuKey() {
        return meiZuKey;
    }

    public void setMeiZuKey(String meiZuKey) {
        this.meiZuKey = meiZuKey;
    }
}
