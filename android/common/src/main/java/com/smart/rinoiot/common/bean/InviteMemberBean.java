package com.smart.rinoiot.common.bean;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/9
 */
public class InviteMemberBean {

    private String acceptAvatarUrl;
    private String acceptUserId;
    private String acceptUserName;
    private String assetId;
    private String assetName;
    //private int createTime;
    private String id;
    private int inviteStatus;
    private String inviteUserId;

    public String getAcceptAvatarUrl() {
        return acceptAvatarUrl;
    }

    public void setAcceptAvatarUrl(String acceptAvatarUrl) {
        this.acceptAvatarUrl = acceptAvatarUrl;
    }

    public String getAcceptUserId() {
        return acceptUserId;
    }

    public void setAcceptUserId(String acceptUserId) {
        this.acceptUserId = acceptUserId;
    }

    public String getAcceptUserName() {
        return acceptUserName;
    }

    public void setAcceptUserName(String acceptUserName) {
        this.acceptUserName = acceptUserName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

//    public int getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(int createTime) {
//        this.createTime = createTime;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(int inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public String getInviteUserId() {
        return inviteUserId;
    }

    public void setInviteUserId(String inviteUserId) {
        this.inviteUserId = inviteUserId;
    }
}
