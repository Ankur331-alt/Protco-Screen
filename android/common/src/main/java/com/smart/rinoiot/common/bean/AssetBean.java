package com.smart.rinoiot.common.bean;

import androidx.annotation.NonNull;

import com.smart.rinoiot.common.tag.TagBaseBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产模型（包含家庭或者房间） 继承下tagBean类，为配网选择房间使用
 */
public class AssetBean extends TagBaseBean implements Serializable {
    /**
     * 地址
     */
    private String address;
    /**
     * 子资产
     */
    private List<AssetBean> childrens;
    /**
     * 是否当前选中
     */
    private boolean currentSelected;
    /**
     * 资产ID
     */
    private String id;
    /**
     * 纬度
     */
    private double lat;
    /**
     * 经度
     */
    private double lng;
    /**
     * 资产名称
     */
    private String name;
    /**
     * 资产父ID
     */
    private String parentId;
    /**
     * 资产成员
     */
    private List<MemberBean> members;
    /**
     * 排序
     */
    private int sortNumber;
    /**
     * 设备列表 外部设置
     */
    private List<DeviceInfoBean> deviceInfoBeans;

    /**
     * 在当前家庭的成员等级 外部设置
     *
     * @return
     */
    private int role;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public List<DeviceInfoBean> getDeviceInfoBeans() {
        return deviceInfoBeans == null ? new ArrayList<>() : deviceInfoBeans;
    }

    public void setDeviceInfoBeans(List<DeviceInfoBean> deviceInfoBeans) {
        this.deviceInfoBeans = deviceInfoBeans;
    }

    public List<MemberBean> getMembers() {
        return members;
    }

    public void setMembers(List<MemberBean> members) {
        this.members = members;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<AssetBean> getChildrens() {
        return childrens == null ? new ArrayList<>() : childrens;
    }

    public void setChildrens(List<AssetBean> childrens) {
        this.childrens = childrens;
    }

    public boolean isCurrentSelected() {
        return currentSelected;
    }

    public void setCurrentSelected(boolean currentSelected) {
        this.currentSelected = currentSelected;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
