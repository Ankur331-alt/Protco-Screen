package com.smart.rinoiot.common.manager;

import android.content.Context;
import android.text.TextUtils;

import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.MemberBean;
import com.smart.rinoiot.common.bean.UserInfoBean;

import java.util.List;

/**
 * @author tw
 * @time 2022/11/4 13:36
 * @description 家庭权限管理
 */
public class FamilyPermissionManager {
    private static FamilyPermissionManager instance;

    public static FamilyPermissionManager getInstance() {
        if (instance == null) {
            instance = new FamilyPermissionManager();
        }
        return instance;
    }

    /**
     * 1=拥有者，2=管理员，3=普通成员
     * 拥有者和管理员都不限制操作
     * 普通成员限制
     */

    public boolean getPermissionMemberRole(Context mContext, AssetBean assetBean) {
        boolean isOperateFlag = false;
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(mContext);
        if (assetBean == null) {
            assetBean = CacheDataManager.getInstance().getCurrentFamily();
        }
        if (userInfo != null && assetBean != null) {
            List<MemberBean> members = assetBean.getMembers();
            if (members==null||members.isEmpty()) return isOperateFlag;
            for (MemberBean memberBean : members) {
                if (TextUtils.equals(userInfo.id, memberBean.getUserId())) {//获取当前家庭角色成员劝劝你先
                    int memberRole = memberBean.getMemberRole();
                    isOperateFlag = memberBean.getMemberRole() != 3;
                    break;
                }
            }
        }
        return isOperateFlag;
    }

    /**
     * 1=拥有者，2=管理员，3=普通成员
     * 拥有者和管理员都不限制操作
     * 普通成员限制
     */

    public int getMemberRole(Context mContext, AssetBean assetBean) {
        int memberRole=3;
        UserInfoBean userInfo = UserInfoManager.getInstance().getUserInfo(mContext);
        if (assetBean == null) {
            assetBean = CacheDataManager.getInstance().getCurrentFamily();
        }
        if (userInfo != null && assetBean != null) {
            List<MemberBean> members = assetBean.getMembers();
            for (MemberBean memberBean : members) {
                if (TextUtils.equals(userInfo.id, memberBean.getUserId())) {//获取当前家庭角色成员劝劝你先
                     memberRole = memberBean.getMemberRole();
                    break;
                }
            }
        }
        return memberRole;
    }
}
