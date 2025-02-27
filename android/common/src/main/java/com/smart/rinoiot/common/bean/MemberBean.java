package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/** 成员模型 */
public class MemberBean implements Serializable {
    /** 受邀用户头像 */
    private String avatarUrl;
    /** 成员ID */
    private String id;
    /** 成员角色（1=拥有者，2=管理员，3=普通成员） */
    private int memberRole;
    /** 成员用户ID */
    private String userId;
    /** 受邀用户名 */
    private String userName;
    /** 资产ID */
    private String assetId;
    /** 名称 */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(int memberRole) {
        this.memberRole = memberRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}
