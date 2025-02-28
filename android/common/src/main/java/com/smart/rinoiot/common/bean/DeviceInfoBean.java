package com.smart.rinoiot.common.bean;

import com.smart.rinoiot.common.device.UnifiedDataPoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangtao
 * <p>
 * create-time: 2022/9/8
 */

public class DeviceInfoBean implements Serializable {

    /**
     * 设备公用常用快捷面板（android）
     */
    private String androidCommonlyShortcutPackage;

    /**
     * 资产ID
     */
    private String assetId;

    /**
     * 绑定模式0:wifi; 1:蓝牙
     */
    private int bindMode;

    /**
     * 边缘网关IP
     */
    private String brokerHost;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 设备功能点别名
     */
    private String dpAlias;

    /**
     * DP点列表
     */
    private List<DeviceDpBean> dpInfoVOList;

    /**
     * 固件id
     */
    private String firmwareId;

    /**
     * 固件版本
     */
    private String firmwareVersion;

    /**
     * 是否是网关：1是 0否
     */
    private int gateway;

    /**
     * 网关类型:1普通网关 2边缘网关
     */
    private int gatewayType;

    /**
     * id
     */
    private String id;

    /**
     * 设备ICON
     */
    private String imageUrl;

    /**
     * 设备公用常用快捷面板（ios）
     */
    private String iosCommonlyShortcutPackage;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 是否支持群组
     */
    private int isGroup;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 在线状态（0离线，1在线）
     */
    private int onlineStatus;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 通讯协议（1：WIFI+BLE，2：蓝牙Mesh（SIG），3：Zigbee，4：PLC，5：传统BLE，6：NB-IOT,7:plc 无线；8：WIFI；9：RS-485;10:RJ-45）
     */
    private String protocolType;

    /**
     * 协议名称
     */
    private String protocolTypeName;

    /**
     * 常用功能DP点列表
     */
    private List<DeviceDpBean> stockDpInfoVOList;

    /**
     * 开关DP点列表
     */
    private List<DeviceDpBean> switchDpInfoVOList;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 时区
     */
    private String timeZone;

    /**
     * 设备UUID
     */
    private String uuid;

    /**
     * 自定义属性，是否选中
     */
    private boolean select;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 群组id
     */
    private String groupId;

    /**
     * 群组下设备是否为空
     */
    private boolean groupOffLineFlag;

    /**
     * 自定义群组数据，将群组转化为单设备
     */
    private boolean isCustomGroup;

    /**
     * 配网模式（1=wifi+ble,2=wifi，3=ble,4=二维码，5=条形码）
     */
    private int distributionNetMode;

    /**
     * NFC UUID
     */
    private List<String> nfcUuids;

    /**
     * 条形码
     */
    private String barcode;

    /**
     * 是否为本地群组
     */
    private boolean islocalGroup;

    /**
     * 虚拟设备(1=是)
     */
    private int isVirtual;

    /**
     * Whether a device is an ip cam or not.
     */
    private boolean isIpc = false;

    /**
     * IPC设备摄像头数量
     */
    private Integer ipcCameraNum = 0;

    /**
     * 产品能力 多个逗号隔开 e.g. IPC
     */
    private String capacity;

    /**
     * 自定义字段，ipc设备门铃呼叫，"video"|"voice"
     */
    private String isFromIntercom;

    /**
     * 自定义属性，当前面板为最新面板
     */
    private boolean isNewPanel;

    /**
     * 群组短ID
     */
    private int shortId;

    /**
     *  Device metadata
     */
    private String metaInfo;


    /**
     * Weather the device is to be shown on the home screen or not.
     */
    private boolean isHomeScreen = false;

    private boolean isClick;//是否以被点击

    /**
     * Unified data points
     */
    private Map<UnifiedDataPoint, Object> unifiedDataPoints = new HashMap<>();

    public String getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
    }

    public int getShortId() {
        return shortId;
    }

    public void setShortId(int shortId) {
        this.shortId = shortId;
    }

    public int getIsVirtual() {
        return isVirtual;
    }

    public void setIsVirtual(int isVirtual) {
        this.isVirtual = isVirtual;
    }

    public boolean isIslocalGroup() {
        return islocalGroup;
    }

    public void setIslocalGroup(boolean islocalGroup) {
        this.islocalGroup = islocalGroup;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public List<String> getNfcUuids() {
        return nfcUuids;
    }

    public void setNfcUuids(List<String> nfcUuids) {
        this.nfcUuids = nfcUuids;
    }

    public int getDistributionNetMode() {
        return distributionNetMode;
    }

    public void setDistributionNetMode(int distributionNetMode) {
        this.distributionNetMode = distributionNetMode;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getDpAlias() {
        return dpAlias;
    }

    public void setDpAlias(String dpAlias) {
        this.dpAlias = dpAlias;
    }

    public String getProtocolTypeName() {
        return protocolTypeName;
    }

    public void setProtocolTypeName(String protocolTypeName) {
        this.protocolTypeName = protocolTypeName;
    }

    public String getAndroidCommonlyShortcutPackage() {
        return androidCommonlyShortcutPackage;
    }

    public void setAndroidCommonlyShortcutPackage(String androidCommonlyShortcutPackage) {
        this.androidCommonlyShortcutPackage = androidCommonlyShortcutPackage;
    }

    public String getIosCommonlyShortcutPackage() {
        return iosCommonlyShortcutPackage;
    }

    public void setIosCommonlyShortcutPackage(String iosCommonlyShortcutPackage) {
        this.iosCommonlyShortcutPackage = iosCommonlyShortcutPackage;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

    public int getGateway() {
        return gateway;
    }

    public void setGateway(int gateway) {
        this.gateway = gateway;
    }

    public int getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(int gatewayType) {
        this.gatewayType = gatewayType;
    }

    public boolean isGroupOffLineFlag() {
        return groupOffLineFlag;
    }

    public void setGroupOffLineFlag(boolean groupOffLineFlag) {
        this.groupOffLineFlag = groupOffLineFlag;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    public boolean isCustomGroup() {
        return isCustomGroup;
    }

    public void setCustomGroup(boolean customGroup) {
        isCustomGroup = customGroup;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<DeviceDpBean> getStockDpInfoVOList() {
        return stockDpInfoVOList;
    }

    public void setStockDpInfoVOList(List<DeviceDpBean> stockDpInfoVOList) {
        this.stockDpInfoVOList = stockDpInfoVOList;
    }

    public List<DeviceDpBean> getSwitchDpInfoVOList() {
        return switchDpInfoVOList;
    }

    public void setSwitchDpInfoVOList(List<DeviceDpBean> switchDpInfoVOList) {
        this.switchDpInfoVOList = switchDpInfoVOList;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getFirmwareId() {
        return firmwareId;
    }

    public void setFirmwareId(String firmwareId) {
        this.firmwareId = firmwareId;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public List<DeviceDpBean> getDpInfoVOList() {
        return dpInfoVOList;
    }

    public void setDpInfoVOList(List<DeviceDpBean> dpInfoVOList) {
        this.dpInfoVOList = dpInfoVOList;
    }

    public int getBindMode() {
        return bindMode;
    }

    public void setBindMode(int bindMode) {
        this.bindMode = bindMode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Map<UnifiedDataPoint, Object> getUnifiedDataPoints() {
        return unifiedDataPoints;
    }

    public void setUnifiedDataPoints(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
        this.unifiedDataPoints = unifiedDataPoints;
    }

    public boolean isHomeScreen() {
        return isHomeScreen;
    }

    public void setHomeScreen(boolean homeScreen) {
        isHomeScreen = homeScreen;
    }

    public boolean isIpc() {
        return isIpc;
    }

    public void setIpc(boolean ipc) {
        isIpc = ipc;
    }

    public Integer getIpcCameraNum() {
        return ipcCameraNum;
    }

    public void setIpcCameraNum(Integer ipcCameraNum) {
        this.ipcCameraNum = ipcCameraNum;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getIsFromIntercom() {
        return isFromIntercom;
    }

    public void setIsFromIntercom(String isFromIntercom) {
        this.isFromIntercom = isFromIntercom;
    }

    public boolean isNewPanel() {
        return isNewPanel;
    }

    public void setNewPanel(boolean newPanel) {
        isNewPanel = newPanel;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
}
