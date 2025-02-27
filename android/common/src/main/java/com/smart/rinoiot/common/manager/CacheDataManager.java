package com.smart.rinoiot.common.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.CityBean;
import com.smart.rinoiot.common.bean.CountryBean;
import com.smart.rinoiot.common.bean.CreateGroupBean;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Geolocation;
import com.smart.rinoiot.common.bean.GroupBean;
import com.smart.rinoiot.common.bean.GroupDeviceItemBean;
import com.smart.rinoiot.common.bean.MatterLocalBean;
import com.smart.rinoiot.common.bean.PanelMultiLangBean;
import com.smart.rinoiot.common.bean.ProductInfoBean;
import com.smart.rinoiot.common.bean.SceneBean;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.voice.VoiceAssistantSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * @author author
 */
public class CacheDataManager {

    private final static String CURRENT_COUNTRY = "current_country";
    private final static String FAMILY_LIST = "family_list";
    private final static String HOME_ID = "home_id";
    private final static String CURRENT_HOME = "current_home";
    private final static String ALL_DEVICE_LIST = "all_device_list";
    private final static String ALL_SCENE_LIST = "all_scene_list";
    private final static String CITY_LIST = "city_list";
    private final static String COUNTRY_LIST = "country_list";
    private final static String DEVICE_DP_LIST = "device_dp_list";
    private final static String ALL_GROUP_DEVICE_LIST = "all_group_device_list";
    private final static String COUNTRY_DOMAIN_JSON = "country_domain_json";
    private final static String ANDROID_DEV_CITY_INFO = "android_dev_city_info";
    private final static String MQTT_CLIENT_CREDENTIALS = "mqtt_client_credentials";
    private static final String AI_SERVER_ADDRESS = "ai_server_address";

    private static final String VOICE_ASSISTANT_SETTINGS = "voice_assistant_settings";
    private final static String ANDROID_DEV_LAST_KNOWN_LOCATION = "android_dev_last_known_location";

    /**
     * 面板多语言数据缓存
     */
    private final static String PANEL_MULTI_LANG = "panel_multi_lang";

    /**
     * matter设备保存本地物模型影子数据
     */
    private final static String LOCAL_MATTER_DP_DATA = "local_matter_dp_data";
    private final List<String> keyArray = new ArrayList<>();

    private static CacheDataManager instance;

    private static final String TAG = "CacheDataManager";

    private CacheDataManager() {
    }

    public static CacheDataManager getInstance() {
        if (instance == null) {
            instance = new CacheDataManager();
        }
        return instance;
    }

    public void saveCurrentCountry(CountryBean currentCountry) {
        if (currentCountry != null) {
            SharedPreferenceUtil.getInstance().put(Constant.COUNTRY_CODE, currentCountry.getCountryCode());
            SharedPreferenceUtil.getInstance().put(Constant.COUNTRY_NAME, currentCountry.getCountryName());
        }
        if (!keyArray.contains(CURRENT_COUNTRY)) {
            keyArray.add(CURRENT_COUNTRY);
        }

        String json = new Gson().toJson(currentCountry);
        SharedPreferenceUtil.getInstance().put(CURRENT_COUNTRY, json);
    }

    public CountryBean getCurrentCountry() {
        String json = SharedPreferenceUtil.getInstance().get(CURRENT_COUNTRY, "");
        CountryBean countryBean = new Gson().fromJson(json, new TypeToken<CountryBean>() {
        }.getType());
        return countryBean==null?new CountryBean():countryBean;
    }

    public void saveCountryList(List<CountryBean> countryList) {
        if (!keyArray.contains(COUNTRY_LIST)) {
            keyArray.add(COUNTRY_LIST);
        }

        String json = new Gson().toJson(countryList);
        SharedPreferenceUtil.getInstance().put(COUNTRY_LIST, json);
    }

    public List<CountryBean> getCountryList() {
        String json = SharedPreferenceUtil.getInstance().get(COUNTRY_LIST, "");
        return new Gson().fromJson(json, new TypeToken<List<CountryBean>>() {
        }.getType());
    }

    public void saveCityList(List<CityBean> countryList) {
        if (!keyArray.contains(CITY_LIST)) {
            keyArray.add(CITY_LIST);
        }

        String json = new Gson().toJson(countryList);
        SharedPreferenceUtil.getInstance().put(CITY_LIST, json);
    }

    public List<CityBean> getCityList() {
        String json = SharedPreferenceUtil.getInstance().get(CITY_LIST, "");
        return new Gson().fromJson(json, new TypeToken<List<CityBean>>() {
        }.getType());
    }

    public void setCurrentHomeId(String homeId) {
        if (!keyArray.contains(HOME_ID)) {
            keyArray.add(HOME_ID);
        }
        SharedPreferenceUtil.getInstance().put(HOME_ID, homeId);
    }

    public String getCurrentHomeId() {
        return SharedPreferenceUtil.getInstance().get(HOME_ID, "");
    }

    public void saveFamilyList(List<AssetBean> familyList) {
        if (!keyArray.contains(FAMILY_LIST)) {
            keyArray.add(FAMILY_LIST);
        }

        String json = new Gson().toJson(familyList);
        SharedPreferenceUtil.getInstance().put(FAMILY_LIST, json);
    }

    public List<AssetBean> getFamilyList() {
        String json = SharedPreferenceUtil.getInstance().get(FAMILY_LIST, "");
        return new Gson().fromJson(json, new TypeToken<List<AssetBean>>() {
        }.getType());
    }

    public void saveCurrentFamily(AssetBean currentFamily) {
        if (!keyArray.contains(CURRENT_HOME)) {
            keyArray.add(CURRENT_HOME);
        }

        String json = new Gson().toJson(currentFamily);
        SharedPreferenceUtil.getInstance().put(CURRENT_HOME, json);
    }

    public AssetBean getCurrentFamily() {
        String json = SharedPreferenceUtil.getInstance().get(CURRENT_HOME, "");
        return new Gson().fromJson(json, new TypeToken<AssetBean>() {
        }.getType());
    }

    public void saveAllDeviceList(String assetId, List<DeviceInfoBean> allDeviceList) {
        if (TextUtils.isEmpty(assetId) || allDeviceList == null) {
            return;
        }
        if (!keyArray.contains(getKey(assetId, ALL_DEVICE_LIST))) {
            keyArray.add(getKey(assetId, ALL_DEVICE_LIST));
        }

        String json = new Gson().toJson(allDeviceList);
        SharedPreferenceUtil.getInstance().put(getKey(assetId, ALL_DEVICE_LIST), json);
    }

    public List<DeviceInfoBean> getAllDeviceList(String assetId) {
        String json = SharedPreferenceUtil.getInstance().get(getKey(assetId, ALL_DEVICE_LIST), "");
        try {
            return new Gson().fromJson(json, new TypeToken<List<DeviceInfoBean>>() {
            }.getType());
        }catch (Exception exception){
            Log.e(TAG, "Failed to get device list. Cause=" + exception.getLocalizedMessage());
            saveAllDeviceList(assetId, new ArrayList<>());
            // ToDo() Fetch new data
            throw exception;
        }
    }

    public void saveDeviceDpList(String deviceId, List<DeviceDpBean> deviceDpList) {
        if (TextUtils.isEmpty(deviceId) || deviceDpList == null) {
            return;
        }

        if (!keyArray.contains(getKey(deviceId, DEVICE_DP_LIST))) {
            keyArray.add(getKey(deviceId, DEVICE_DP_LIST));
        }

        String json = new Gson().toJson(deviceDpList);
        SharedPreferenceUtil.getInstance().put(getKey(deviceId, DEVICE_DP_LIST), json);
    }

    public List<DeviceDpBean> getDeviceDpList(String deviceId) {
        String json = SharedPreferenceUtil.getInstance().get(getKey(deviceId, DEVICE_DP_LIST), "");
        return new Gson().fromJson(json, new TypeToken<List<DeviceDpBean>>() {
        }.getType());
    }

    /**
     * 获取设备详情  群组设备或者单个设备
     * @param  devId the device identifier
     */
    public DeviceInfoBean getDeviceInfo(String devId) {
        if (TextUtils.isEmpty(devId)){
            return null;
        }

        List<DeviceInfoBean> allDeviceList = getAllDeviceList(getCurrentHomeId());
        if (allDeviceList == null || allDeviceList.size() == 0) {
            return null;
        }

        DeviceInfoBean deviceInfoBean = null;
        for (DeviceInfoBean target : allDeviceList) {
            if (TextUtils.equals(devId, target.getId()) && !TextUtils.isEmpty(target.getGroupId())) {
                //群组设备
                GroupBean groupDeviceInfo = getGroupDeviceInfo(target.getGroupId());
                if (groupDeviceInfo != null && groupDeviceInfo.getDeviceList() != null && !groupDeviceInfo.getDeviceList().isEmpty()) {
                    deviceInfoBean = getGroupDeviceDpData(getAllDeviceList(getCurrentHomeId()), groupDeviceInfo.getDeviceList().get(0).getId());
                    break;
                }
            } else {//单设备
                if (TextUtils.equals(devId, target.getId())) {
                    deviceInfoBean = target;
                    break;
                }
            }
        }
        return deviceInfoBean;
    }

    /**
     * Update cached device info
     * @param homeId home identifier
     * @param deviceId device info.
     * @param deviceName device info.
     */
    public void updateDeviceInfo(String homeId, String deviceId, String deviceName) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            if(StringUtil.hasBlank(deviceId, deviceName, homeId)){
                return;
            }

            List<DeviceInfoBean> deviceList = getAllDeviceList(homeId);
            if (deviceList == null || deviceList.size() == 0) {
                return ;
            }

            Predicate<DeviceInfoBean> deviceInfoPredicate = deviceInfoBean ->
                    StringUtil.isNotBlank(deviceInfoBean.getId()) &&
                            deviceId.contentEquals(deviceInfoBean.getId());

            // find the device
            int index = IntStream.range(0, deviceList.size())
                    .filter(i -> deviceInfoPredicate.test(deviceList.get(i)))
                    .findFirst()
                    .orElse(-1);

            // replace it with the new device info
            if (index == -1) {
                return;
            }

            // replace the old device info with the new one
            deviceList.get(index).setName(deviceName);
            // update device cache
            saveAllDeviceList(homeId, deviceList);
        });
    }

    public DeviceInfoBean getDeviceInfoByUuid(String uuid) {
        if (TextUtils.isEmpty(uuid)) {
            return null;
        }

        DeviceInfoBean deviceInfoBean = null;
        List<DeviceInfoBean> allDeviceList = getAllDeviceList(getCurrentHomeId());
        if (allDeviceList != null && allDeviceList.size() > 0) {
            for (DeviceInfoBean target : allDeviceList) {
                if (uuid.equals(target.getUuid())) {
                    deviceInfoBean = target;
                    break;
                }
            }
        }
        return deviceInfoBean;
    }

    public void saveAllSceneList(String assetId, List<SceneBean> allSceneList) {
        if (TextUtils.isEmpty(assetId) || allSceneList == null) {
            return;
        }
        if (!keyArray.contains(getKey(assetId, ALL_SCENE_LIST))) {
            keyArray.add(getKey(assetId, ALL_SCENE_LIST));
        }

        String json = new Gson().toJson(allSceneList);
        SharedPreferenceUtil.getInstance().put(getKey(assetId, ALL_SCENE_LIST), json);
    }

    public List<SceneBean> getAllSceneList(String assetId) {
        String json = SharedPreferenceUtil.getInstance().get(getKey(assetId, ALL_SCENE_LIST), "");
        return new Gson().fromJson(json, new TypeToken<List<SceneBean>>() {
        }.getType());
    }

    public void clear() {
        for (String key : keyArray) {
            SharedPreferenceUtil.getInstance().remove(key);
        }
        keyArray.clear();
    }

    private String getKey(String key1, String key2) {
        return key1 + key2;
    }

    /**
     * 面板中双模设备wifi配网数据转化
     */
    public List<ProductInfoBean> getPanelWifiConfigData(String devId) {
        List<ProductInfoBean> list = new ArrayList<>();
        if (TextUtils.isEmpty(devId)) {
            return list;
        }
        DeviceInfoBean deviceInfo = getDeviceInfo(devId);
        if (deviceInfo == null) {
            return null;
        }
        ProductInfoBean panelWifiConfigBean = new ProductInfoBean();
        panelWifiConfigBean.setProtocolName(deviceInfo.getName());
        panelWifiConfigBean.setProtocolImageUrl(deviceInfo.getImageUrl());
        panelWifiConfigBean.setImageUrl(deviceInfo.getImageUrl());
        panelWifiConfigBean.setCanNetStatus(true);
        panelWifiConfigBean.setUuid(deviceInfo.getUuid());
        panelWifiConfigBean.setDeviceUuid(deviceInfo.getUuid());
        /// panelWifiConfigBean.setAddress(deviceInfo.);
        list.add(panelWifiConfigBean);
        return list;
    }

    /**
     * 根据资产id 获取资产数据
     */
    public AssetBean getAssetDataById(String assetId) {
        AssetBean assetBean = null;
        List<AssetBean> familyList = getFamilyList();
        for (AssetBean item : familyList) {
            if (TextUtils.equals(assetId, item.getId())) {
                assetBean = item;
                break;
            }
        }
        return assetBean;
    }

    /**
     * 移除设备时需要移除缓存，防止在FNC判断时，接口没请求成功又再次走NFC逻辑
     */
    public void removeDeviceUpdateData(DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean == null || TextUtils.isEmpty(deviceInfoBean.getId())) {
            return;
        }
        List<DeviceInfoBean> allDeviceTempList = new ArrayList<>();
        List<DeviceInfoBean> allDeviceList = getAllDeviceList(getCurrentHomeId());
        if (allDeviceList != null && allDeviceList.size() > 0) {
            for (DeviceInfoBean target : allDeviceList) {
                if (TextUtils.equals(deviceInfoBean.getId(), target.getId()) && TextUtils.equals(deviceInfoBean.getGroupId(), target.getGroupId())) {
                    continue;
                }
                allDeviceTempList.add(target);
            }
            saveAllDeviceList(getCurrentHomeId(), allDeviceTempList);
        }
    }

    /**
     * 场景中选择和设备相关的功能时，过滤所有群组设备
     */
    public List<DeviceInfoBean> sceneRemoveGroupDeviceData(String assetId) {
        List<DeviceInfoBean> infoBeanList = getAllDeviceList(assetId);
        List<DeviceInfoBean> sceneDeviceList = new ArrayList<>();
        for (DeviceInfoBean infoBean : infoBeanList) {
            if (!infoBean.isCustomGroup()) {
                sceneDeviceList.add(infoBean);
            }
        }
        return sceneDeviceList;
    }

    /**
     * 保存群组数据
     */
    public void saveAllGroupDeviceList(String assetId, List<GroupBean> groupBeanList) {
        if (TextUtils.isEmpty(assetId) || groupBeanList == null) {
            return;
        }
        if (!keyArray.contains(getKey(assetId, ALL_GROUP_DEVICE_LIST))) {
            keyArray.add(getKey(assetId, ALL_GROUP_DEVICE_LIST));
        }

        String json = new Gson().toJson(groupBeanList);
        SharedPreferenceUtil.getInstance().put(getKey(assetId, ALL_GROUP_DEVICE_LIST), json);
    }

    public List<GroupBean> getAllGroupDeviceList(String assetId) {
        String json = SharedPreferenceUtil.getInstance().get(getKey(assetId, ALL_GROUP_DEVICE_LIST), "");
        return new Gson().fromJson(json, new TypeToken<List<GroupBean>>() {
        }.getType());
    }

    /**
     * 根据群组id，获取群组bean
     */
    public GroupBean getGroupDeviceInfo(String groupId) {
        if (TextUtils.isEmpty(groupId)) {
            return null;
        }

        GroupBean groupBean = null;
        List<GroupBean> allGroupDeviceList = getAllGroupDeviceList(getCurrentHomeId());
        if (allGroupDeviceList != null && allGroupDeviceList.size() > 0) {
            for (GroupBean target : allGroupDeviceList) {
                if (TextUtils.equals(groupId, target.getId())) {
                    groupBean = target;
                    break;
                }
            }
        }
        return groupBean;
    }

    /**
     * 创建或者编辑群组时，过滤（所有群组设备+非相同pid的设备+相同pid下已存在的设备+暂时只展示wifi列表）
     */
    public List<CreateGroupBean> getAllGroupDeviceData(String productId, GroupBean groupDeviceInfo, String devId) {
        List<CreateGroupBean> createGroupBeans = new ArrayList<>();
        boolean groupDataEmpty = groupDeviceInfo == null || groupDeviceInfo.getDeviceList() == null
                || groupDeviceInfo.getDeviceList().isEmpty();
        AssetBean assetBean = getCurrentFamily();
        List<DeviceInfoBean> infoBeanList = getAllDeviceList(getCurrentHomeId());
        for (DeviceInfoBean infoBean : infoBeanList) {
            if (!infoBean.isCustomGroup() && infoBean.getIsGroup() == 1 && infoBean.getBindMode() == 0 && TextUtils.equals(infoBean.getProductId(), productId)) {//群组、不同pid设备过滤、配网模式必须wifi配网
                boolean isExit = false;
                if (!groupDataEmpty) {
                    //群组为空，展示所有的
                    for (GroupDeviceItemBean itemBean : groupDeviceInfo.getDeviceList()) {
                        if (TextUtils.equals(itemBean.getId(), infoBean.getId())) {
                            //已存在群组设备中的设备过滤
                            isExit = true;
                            break;
                        }
                    }
                }
                if (!TextUtils.equals(devId, infoBean.getId()) && !isExit) {
                    CreateGroupBean createGroupBean = new CreateGroupBean();
                    createGroupBean.setDeviceName(infoBean.getName());
                    createGroupBean.setDevId(infoBean.getId());
                    createGroupBean.setImageUrl(infoBean.getImageUrl());
                    createGroupBean.setAssetId(infoBean.getAssetId());
                    createGroupBeans.add(createGroupBean);
                }
            }
        }
        if (assetBean == null) {
            return createGroupBeans;
        }
        for (AssetBean room : assetBean.getChildrens()) {
            //根据房间信息，设置房间名称
            for (CreateGroupBean item : createGroupBeans) {
                if (TextUtils.equals(room.getId(), item.getAssetId())) {
                    //设置房间名称
                    item.setRoomName(room.getName());
                    break;
                }
            }
        }

        return createGroupBeans;
    }

    /**
     * 通过群组id，获取对应群组下的设备集合，如果当前群组为创建，显示当前设备的信息
     */
    public List<CreateGroupBean> getGroupDeviceData(GroupBean groupDeviceInfo, String devId) {
        AssetBean assetBean = getCurrentFamily();
        List<CreateGroupBean> list = new ArrayList<>();
        if (groupDeviceInfo == null) {
            //创建群组
            if (TextUtils.isEmpty(devId)) {
                return list;
            }
            DeviceInfoBean deviceInfo = getDeviceInfo(devId);
            if (deviceInfo == null) {
                return list;
            }
            CreateGroupBean createGroupBean = new CreateGroupBean();
            createGroupBean.setDeviceName(deviceInfo.getName());
            createGroupBean.setDevId(deviceInfo.getId());
            createGroupBean.setImageUrl(deviceInfo.getImageUrl());
            createGroupBean.setCurrentRoomFlag(true);
            createGroupBean.setAssetId(deviceInfo.getAssetId());
            list.add(createGroupBean);
        } else {
            //编辑群组
            if (groupDeviceInfo == null || groupDeviceInfo.getDeviceList() == null
                    || groupDeviceInfo.getDeviceList().isEmpty()) {
                return list;
            }
            for (GroupDeviceItemBean item : groupDeviceInfo.getDeviceList()) {
                CreateGroupBean createGroupBean = new CreateGroupBean();
                createGroupBean.setDeviceName(item.getName());
                createGroupBean.setDevId(item.getId());
                createGroupBean.setImageUrl(item.getImageUrl());
                createGroupBean.setAssetId(groupDeviceInfo.getAssetId());
                createGroupBean.setCurrentRoomFlag(true);
                list.add(createGroupBean);
            }
        }

        if (assetBean == null) {
            return list;
        }
        for (AssetBean room : assetBean.getChildrens()) {
            //根据房间信息，设置房间名称
            for (CreateGroupBean item : list) {
                if (TextUtils.equals(room.getId(), item.getAssetId())) {
                    //设置房间名称
                    item.setRoomName(room.getName());
                    break;
                }
            }
        }
        return list;
    }

    /**
     * 更新群组缓存数据
     */
    public void updateModeData(GroupBean groupBean) {
        if (groupBean == null) {
            return;
        }
        List<GroupBean> allGroupTempList = new ArrayList<>();
        List<GroupBean> allGroupList = getAllGroupDeviceList(getCurrentHomeId());
        if (allGroupList != null && allGroupList.size() > 0) {
            for (GroupBean target : allGroupList) {
                if (TextUtils.equals(groupBean.getId(), target.getId())) {
                    target = groupBean;
                }
                allGroupTempList.add(target);
            }
            saveAllGroupDeviceList(getCurrentHomeId(), allGroupTempList);
        }
    }


    /**
     * 获取群组设备且转化为单设备数据格式
     */
    public DeviceInfoBean getGroupDeviceSingleData(GroupBean groupBean, List<DeviceInfoBean> deviceInfoBeans) {
        DeviceInfoBean deviceInfoBean = new DeviceInfoBean();
        if (groupBean.getDeviceList() != null && !groupBean.getDeviceList().isEmpty()) {
            deviceInfoBean = getGroupDeviceDpData(deviceInfoBeans, groupBean.getDeviceList().get(0).getId());
            deviceInfoBean.setGroupOffLineFlag(false);
        } else {
            deviceInfoBean.setGroupOffLineFlag(true);
        }
        deviceInfoBean.setAssetId(groupBean.getAssetId());
        deviceInfoBean.setGroupId(groupBean.getId());
        deviceInfoBean.setCreateTime(groupBean.getCreateTime());
        deviceInfoBean.setName(groupBean.getName());
        String productId = groupBean.getProductIds();
        if (!TextUtils.isEmpty(groupBean.getProductIds())&&groupBean.getProductIds().contains(",")) {
            String[] split = groupBean.getProductIds().split(",");
            if (split != null) {
                productId = split[0];
            }
        }
        if (!TextUtils.isEmpty(productId)) {
            deviceInfoBean.setProductId(productId);
        }
        deviceInfoBean.setImageUrl(groupBean.getImageUrl());
        deviceInfoBean.setUserId(groupBean.getUserId());
        deviceInfoBean.setCustomGroup(true);
        return deviceInfoBean;
    }

    /**
     * 接口请求 数据缓存，群组设备的设备转为为单设备的设备id及为群组id；根据群组中第一个设备的id获取快捷开关物模型
     */
    private DeviceInfoBean getGroupDeviceDpData(List<DeviceInfoBean> infoBeanList, String devId) {
        DeviceInfoBean infoBean = new DeviceInfoBean();
        if (infoBeanList == null || infoBeanList.isEmpty()) {
            return infoBean;
        }
        for (DeviceInfoBean item : infoBeanList) {
            if (TextUtils.equals(devId, item.getId())) {
                //群组中第一个设备的id和家庭下所有设备中id取匹配
                infoBean.setSwitchDpInfoVOList(item.getSwitchDpInfoVOList());
                infoBean.setUuid(item.getUuid());
                infoBean.setProductId(item.getProductId());
                infoBean.setImageUrl(item.getImageUrl());
                infoBean.setId(item.getId());
                infoBean.setDpInfoVOList(item.getDpInfoVOList());
                infoBean.setBindMode(item.getBindMode());
                infoBean.setFirmwareId(item.getFirmwareId());
                infoBean.setIp(item.getIp());
                infoBean.setName(infoBean.getName());
                infoBean.setFirmwareVersion(item.getFirmwareVersion());
                infoBean.setOnlineStatus(item.getOnlineStatus());
                infoBean.setProtocolType(item.getProtocolType());
                infoBean.setStockDpInfoVOList(item.getStockDpInfoVOList());
                infoBean.setCustomGroup(true);
                break;
            }
        }
        return infoBean;
    }

    /**
     * 获取群组设备 根据设备id,针对mqtt下发群组数据
     */
    public DeviceInfoBean getDevIdByDeviceInfo(String devId) {
        if (TextUtils.isEmpty(devId)) {
            return null;
        }

        DeviceInfoBean deviceInfoBean = null;
        List<DeviceInfoBean> allDeviceList = getAllDeviceList(getCurrentHomeId());
        if (allDeviceList == null || allDeviceList.size() == 0) {
            return null;
        }

        for (DeviceInfoBean target : allDeviceList) {
            //1、群组；2、为本地群组
            if(TextUtils.isEmpty(target.getGroupId())) {
                continue;
            }

            if (TextUtils.equals(devId, target.getId())) {
                deviceInfoBean = target;
                break;
            }
        }
        return deviceInfoBean;
    }

    /**
     * 根据matter sdk获取当前matter设备物模型影子存到本地
     */
    public void saveMatterDeviceData(String matterDevId, MatterLocalBean matterLocalBean) {
        if (TextUtils.isEmpty(matterDevId) || matterLocalBean == null) {
            return;
        }

        if (!keyArray.contains(getKey(matterDevId, LOCAL_MATTER_DP_DATA))) {
            keyArray.add(getKey(matterDevId, LOCAL_MATTER_DP_DATA));
        }

        String json = new Gson().toJson(matterLocalBean);
        SharedPreferenceUtil.getInstance().put(getKey(matterDevId, LOCAL_MATTER_DP_DATA), json);
    }

    public MatterLocalBean getMatterDeviceData(String matterDevId) {
        String json = SharedPreferenceUtil
                .getInstance()
                .get(getKey(matterDevId, LOCAL_MATTER_DP_DATA), "");
        return new Gson().fromJson(json, new TypeToken<MatterLocalBean>() {}.getType());
    }


    /**
     * 根据群组id，获取群组下的所有设备数据
     */
    public List<DeviceInfoBean> getGroupDeviceListData(String groupId) {
        List<DeviceInfoBean> groupDeviceList = new ArrayList<>();
        GroupBean groupBean = getGroupDeviceInfo(groupId);
        List<DeviceInfoBean> allDeviceList = getAllDeviceList(getCurrentHomeId());
        if (groupBean == null || groupBean.getDeviceList() == null || groupBean.getDeviceList().isEmpty() || allDeviceList == null || allDeviceList.isEmpty()) {
            return groupDeviceList;
        }
        List<GroupDeviceItemBean> groupDeviceItemBeans = groupBean.getDeviceList();
        for (GroupDeviceItemBean groupItem : groupDeviceItemBeans) {
            //群组
            for (DeviceInfoBean info : allDeviceList) {
                //所有设备
                if (TextUtils.equals(groupItem.getId(), info.getId()) && TextUtils.isEmpty(info.getGroupId())) {
                    groupDeviceList.add(info);
                    break;
                }
            }
        }
        return groupDeviceList;
    }

    /**
     * 保存 根据国家areaId 获取对应的domain+mqtt端口及ip地址
     */
    public void saveCountryDomainJson(String countryDomainJson) {
        if (!keyArray.contains(COUNTRY_DOMAIN_JSON)) {
            keyArray.add(COUNTRY_DOMAIN_JSON);
        }
        SharedPreferenceUtil.getInstance().put(COUNTRY_DOMAIN_JSON, countryDomainJson);
    }

    public String getCountryDomainJson() {
        return SharedPreferenceUtil.getInstance().get(COUNTRY_DOMAIN_JSON, "");
    }

    /**
     * 根据设备id，判断改设备是否在群组下，返回对应的群组列表（用于控制单设备的同时控制其对应的群组）
     */
    public List<String> getDevIdByGroupIds(String devId) {
        List<String> tempGroupIds = new ArrayList<>();
        List<GroupBean> groupDeviceList = getAllGroupDeviceList(getCurrentHomeId());
        if (groupDeviceList == null || groupDeviceList.isEmpty()) {
            return  tempGroupIds;
        }

        for (GroupBean item : groupDeviceList) {
            if(null == item) {
                continue;
            }

            if (item.getDeviceList() == null || item.getDeviceList().isEmpty()) {
                continue;
            }

            for (GroupDeviceItemBean itemBean : item.getDeviceList()) {
                if (TextUtils.equals(itemBean.getId(), devId)) {
                    //群组id
                    tempGroupIds.add(item.getId());
                    break;
                }
            }
        }
        return tempGroupIds;
    }

    /**
     * 获取面板多语言数据资源
     */
    public PanelMultiLangBean getPanelMultiLang(String productId) {
        String json = SharedPreferenceUtil.getInstance().get(getKey(productId, PANEL_MULTI_LANG), "");
        return new Gson().fromJson(json, new TypeToken<PanelMultiLangBean>() {
        }.getType());
    }


    /**
     * 保存面板多语言数据资源
     */
    public void savePanelMultiLang(String productId, PanelMultiLangBean panelMultiLangBean) {
        if (TextUtils.isEmpty(productId) || panelMultiLangBean == null) {
            return;
        }
        if (!keyArray.contains(getKey(productId, PANEL_MULTI_LANG))) {
            keyArray.add(getKey(productId, PANEL_MULTI_LANG));
        }

        String json = new Gson().toJson(panelMultiLangBean);
        SharedPreferenceUtil.getInstance().put(getKey(productId, PANEL_MULTI_LANG), json);
    }

    public void cacheDeviceLastKnownLocation(Geolocation geolocation) {
        if (!keyArray.contains(ANDROID_DEV_LAST_KNOWN_LOCATION)) {
            keyArray.add(ANDROID_DEV_LAST_KNOWN_LOCATION);
        }

        String json = new Gson().toJson(geolocation);
        SharedPreferenceUtil.getInstance().put(ANDROID_DEV_LAST_KNOWN_LOCATION, json);
    }

    public Geolocation getDeviceLastKnownLocation() {
        String json = SharedPreferenceUtil.getInstance().get(ANDROID_DEV_LAST_KNOWN_LOCATION, "");
        if(json.isEmpty()){
            return null;
        }
        return new Gson().fromJson(json, new TypeToken<Geolocation>() {
        }.getType());
    }

    public String getInferenceServerAddress(){
        String address = SharedPreferenceUtil.getInstance().get(AI_SERVER_ADDRESS, "");
        return address.trim();
    }

    public void cacheInferenceServerAddress(String address){
        if(StringUtil.isBlank(address)){
            return;
        }

        if (!keyArray.contains(AI_SERVER_ADDRESS)) {
            keyArray.add(AI_SERVER_ADDRESS);
        }
        SharedPreferenceUtil.getInstance().put(AI_SERVER_ADDRESS, address);
    }

    public void cacheCityInfo(CityBean city) {
        if (!keyArray.contains(ANDROID_DEV_CITY_INFO)) {
            keyArray.add(ANDROID_DEV_CITY_INFO);
        }

        String json = new Gson().toJson(city);
        SharedPreferenceUtil.getInstance().put(ANDROID_DEV_CITY_INFO, json);
    }

    public CityBean getCityInfo(){
        String json = SharedPreferenceUtil.getInstance().get(ANDROID_DEV_CITY_INFO, "");
        if(json.isEmpty()){
            return null;
        }
        return new Gson().fromJson(json, new TypeToken<CityBean>() {
        }.getType());
    }

    public VoiceAssistantSettings getVoiceAssistantSettings() {
        String json = SharedPreferenceUtil.getInstance().get(VOICE_ASSISTANT_SETTINGS, "");
        if(StringUtil.isBlank(json)){
            return new VoiceAssistantSettings();
        }
        return new Gson().fromJson(json, VoiceAssistantSettings.class);
    }

    public void cacheVoiceAssistantSettings(VoiceAssistantSettings settings) {
        if (!keyArray.contains(VOICE_ASSISTANT_SETTINGS)) {
            keyArray.add(VOICE_ASSISTANT_SETTINGS);
        }

        String json = new Gson().toJson(settings);
        SharedPreferenceUtil.getInstance().put(VOICE_ASSISTANT_SETTINGS, json);
    }

    /**
     * Clears the cache
     * @param context the android context.
     */
    public void clearCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                // Delete all the files in the cache directory
                deleteFiles(cacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Deletes the cache files
     * @param file the file  to be deleted
     */
    @SuppressWarnings("all")
    private void deleteFiles(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                for (File child : children) {
                    deleteFiles(child);
                }
            }
            file.delete();
        }
    }


    /**
     * Returns the size of the cache in megabytes
     * @param context the android context.
     * @return the size of the cache in megabytes
     */
    public double getCacheSize(Context context) {
        double size = 0;
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                File[] files = cacheDir.listFiles();
                for (File file : files) {
                    size += file.length();
                }
            }
            // Convert size to megabytes
            size = size / (1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
