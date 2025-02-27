package com.smart.rinoiot.common.ble;

import android.text.TextUtils;

import com.smart.rinoiot.common.bean.ProductInfoBean;
import com.smart.rinoiot.common.listener.MultiDevDisNetworkListener;
import com.smart.rinoiot.common.listener.SingleDevMultiDisNetworkListener;
import com.smart.rinoiot.common.listener.StringCallback;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.RlinkPackDataUtil;
import com.smart.rinoiot.ftms.BleRssiDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author tw
 * @time 2022/10/25 21:35
 * @description 蓝牙数据处理类
 */
public class BleScanConnectDataManager {
    private static final String TAG = BleScanConnectDataManager.class.getSimpleName();
    private static BleScanConnectDataManager instance;

    //扫描回调数据
    private final Map<String, ProductInfoBean> productInfoBeanMap = new HashMap<>();
    /**
     * 接收分段蓝牙数据时使用的拼装集合
     */
    private final Map<String, Map<Integer, String>> deviceResponseMapData = new HashMap<>();
    /**
     * 回调出收到的蓝牙通知数据的监听集合
     */
    private final Map<String, StringCallback> stringCallbackMap = new HashMap<>();

    /**
     * 发送消息时，没有匹配到uuid，需要重新开启扫码+ 发送空消息时，必须要处理扫码接口
     */
    private final Map<String, Boolean> reScanMap = new HashMap<>();

    /**
     * 通知注册成功，数据保存
     */
    private final Map<String, BleRssiDevice> bleRssiDeviceMap = new HashMap<>();


    /**
     * 相同uuid，发送多条蓝牙消息时，根据不同类型保存
     */
    private final Map<String, String> messageTypeMap = new HashMap<>();

    /**
     * 多设备同时配网时，判断最大连接数
     */
    private Map<String, Integer> connectCountMap = new HashMap<>();


    public static BleScanConnectDataManager getInstance() {
        if (instance == null) {
            instance = new BleScanConnectDataManager();
        }
        return instance;
    }

    /**
     * 保存当前uuid的扫描数据
     */
    public void addProductInfoBean(String uuid, ProductInfoBean infoBean) {
        productInfoBeanMap.put(uuid, infoBean);
    }

    /**
     * 获取所有扫描数据
     */
    public Map<String, ProductInfoBean> getProductInfoBeanMap() {
        return productInfoBeanMap;
    }

    /**
     * 获取所有扫描数据
     */
    public ProductInfoBean getProductInfoBeanByUuid(String uuid) {
        ProductInfoBean productInfoBean = new ProductInfoBean();
        Map<String, ProductInfoBean> productInfoBeanMap = getProductInfoBeanMap();
        if (productInfoBeanMap != null && !productInfoBeanMap.isEmpty() && productInfoBeanMap.containsKey(uuid)) {
            productInfoBean = productInfoBeanMap.get(uuid);
        }
        return productInfoBean;
    }


    /**
     * 移除当前uuid的扫描数据
     */
    public void removeUuidProductInfoBeanData(String uuid) {
        productInfoBeanMap.remove(uuid);
    }

    /**
     * 获取当前uuid的响应数据
     */
    public Map<Integer, String> getUuidResponseData(String uuid) {
        uuid = uuid + getMessageType(uuid);
        //单个设备数据响应
        Map<Integer, String> deviceResponseData = new HashMap<>();
        if (deviceResponseMapData.containsKey(uuid)) {
            deviceResponseData = deviceResponseMapData.get(uuid);
        } else {
            deviceResponseMapData.put(uuid, deviceResponseData);
        }
        return deviceResponseData;
    }

    /**
     * 移除当前uuid的响应数据
     */
    public void removeUuidResponseData(String uuid) {
        deviceResponseMapData.remove(uuid + getMessageType(uuid));
    }

    /**
     * 获取当前uuid的回调监听
     */
    public StringCallback getUuidStringCallBackData(String uuid) {
        uuid = uuid + getMessageType(uuid);
        StringCallback stringCallback = null;
        if (stringCallbackMap.containsKey(uuid)) {
            stringCallback = stringCallbackMap.get(uuid);
        } else {
            stringCallbackMap.put(uuid, stringCallback);
        }
        return stringCallback;
    }

    /**
     * 保存当前uuid的回调监听，一个uuid只会保存一个回调对象
     */
    public void saveStringCallBackByUuid(String uuid, StringCallback stringCallback) {
        stringCallbackMap.put(uuid + getMessageType(uuid), stringCallback);
    }


    /**
     * 移除当前uuid的回调监听
     */
    public void removeUuidStringCallBackData(String uuid) {
        stringCallbackMap.remove(uuid + getMessageType(uuid));
    }

    /**
     * 发送消息时，没有匹配到uuid，需要重新开启扫码+ 发送空消息时，必须要处理扫码接口 保存
     */
    public void saveReScanByUuid(String uuid, boolean reScanFlag) {
        reScanMap.put(uuid, reScanFlag);
    }

    /**
     * 移除当前uuid的发送消息时，没有匹配到uuid，需要重新开启扫码+ 发送空消息时，必须要处理扫码接口
     */
    public void removeReScanByUuid(String uuid) {
        reScanMap.remove(uuid);
    }

    /**
     * 获取当前uuid的发送消息时，没有匹配到uuid，需要重新开启扫码+ 发送空消息时，必须要处理扫码接口
     */
    public Map<String, Boolean> getReScanMap() {
        return reScanMap;
    }


    /**
     * 添加当前uuid的通知注册成功
     */
    public void saveBleRssiDeviceUuid(String uuid, BleRssiDevice bleRssiDevice) {
        bleRssiDeviceMap.put(uuid, bleRssiDevice);
    }

    /**
     * 移除当前uuid的通知注册成功，
     */
    public void removeBleRssiDeviceUuid(String uuid) {
        bleRssiDeviceMap.remove(uuid);
    }

    /**
     * 获取当前uuid的通知注册成功，
     */
    public Map<String, BleRssiDevice> getBleRssiDeviceMap() {
        return bleRssiDeviceMap;
    }


    /**
     * 添加当前uuid的发送多条蓝牙消息时，根据不同类型
     */
    public void saveMessageTypeUuid(String uuid, String type) {
        messageTypeMap.put(uuid, type);
    }

    /**
     * 移除当前uuid的发送多条蓝牙消息时，根据不同类型，
     */
    public void removeMessageTypeUuid(String uuid) {
        messageTypeMap.remove(uuid);
    }

    /**
     * 获取当前uuid的发送多条蓝牙消息时，根据不同类型
     */
    public Map<String, String> getMessageTypeMap() {
        return messageTypeMap;
    }

    /**
     * 获取当前uuid的发送多条蓝牙消息时，根据不同类型
     */
    public String getMessageType(String uuid) {
        String messageType = "";
        if (getMessageTypeMap().containsKey(uuid)) {
            messageType = getMessageTypeMap().get(uuid);
        }
        return messageType;
    }

    public void releaseData() {
        productInfoBeanMap.clear();
        deviceResponseMapData.clear();
        stringCallbackMap.clear();
        reScanMap.clear();
        bleRssiDeviceMap.clear();
        messageByteMap.clear();
    }


    /**
     * 发送消息内容  byte[]数组数据
     */
    private final Map<String, List<byte[]>> messageByteMap = new HashMap<>();

    /**
     * 发送数据格式
     */
    private String sendDataFormat(String message) {
        String formatMsg = message;
        JSONObject objectSend = new JSONObject();
        JSONObject data = new JSONObject();
        if (TextUtils.isEmpty(message)) {
            return "";
        }
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.has("data")) {
                JSONObject objectData = jsonObject.getJSONObject("data");
                if (objectData.has("devices")) {
                    JSONArray devices = objectData.getJSONArray("devices");
                    for (int i = 0; i < devices.length(); i++) {
                        if (devices.getJSONObject(i).has("properties")) {
                            Iterator<String> keys = devices.getJSONObject(i).getJSONObject("properties").keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                Object value = devices.getJSONObject(i).getJSONObject("properties").get(key);
                                data.put(key, value);
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(String.valueOf(data))) {
                        objectSend.put("data", data);
                    }
                    if (jsonObject.has("ts")) {
                        objectSend.put("ts", jsonObject.getString("ts"));
                    }
                    if (jsonObject.has("msgId")) {
                        objectSend.put("msgId", jsonObject.getString("msgId"));
                    }
                    if (jsonObject.has("type")) {
                        objectSend.put("type", jsonObject.getString("type"));
                    }
                    formatMsg = objectSend.toString();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return formatMsg;
        }
    }

    /**
     * 添加当前uuid的发送消息内容 byte[]数据
     */
    public void saveMessageByteUuid(String uuid, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        String sendMessage = sendDataFormat(message);
        List<String> packList = RlinkPackDataUtil.rnLinkDataPacking(sendMessage);
        LgUtils.w("BleScanConnectManager    saveMessageByteUuid     sendMessage=" + sendMessage + "   packList.size=" + packList.size() + "  uuid=" + uuid);
        int len;
        List<byte[]> bytesList = new ArrayList<>();
        for (int i = 0; i < packList.size(); i++) {
            len = packList.get(i).length();
            if (len > 0 && len % 2 == 0) {
                //HEX TO byte
                byte[] bytes = RlinkPackDataUtil.toByteArray(packList.get(i));
                if (bytes != null) {
                    bytesList.add(bytes);
                    messageByteMap.put(uuid + getMessageType(uuid), bytesList);
                } else {
                    LgUtils.e(TAG + " 分多段数据处理，分别写入  write value fail" + "  uuid=" + uuid);
                }
            }
        }
        LgUtils.w("BleScanConnectManager   saveMessageByteUuid bytesList.size=" + bytesList.size() + "  uuid=" + uuid);
    }

    /**
     * 移除当前uuid的发送消息内容 byte[]数据
     */
    public void removeMessageByteUuid(String uuid) {
        uuid = uuid + getMessageType(uuid);
        messageByteMap.remove(uuid);
    }

    /**
     * 获取当前uuid的发送消息内容 byte[]数据
     */
    public List<byte[]> getMessageByteDataByUuid(String uuid) {
        uuid = uuid + getMessageType(uuid);
        List<byte[]> bytes = new ArrayList<>();
        if (messageByteMap.containsKey(uuid)) {
            bytes = messageByteMap.get(uuid);
        }
        return bytes;
    }

    /**
     * 多设备同时配网时，判断最大连接数
     */
    public Map<String, Integer> getConnectCountMap() {
        LgUtils.w(TAG + "   getConnectCountMap  connectCountMap=" + connectCountMap.size());
        return connectCountMap;
    }

    /**
     * 多设备同时配网时，增加连接
     */
    MultiDevDisNetworkListener multiDevDisNetworkListener;
    SingleDevMultiDisNetworkListener singleDevMultiDisNetworkListener;

    //是否是单设备配网；是否是wifi列表获取的信息;超过最大数据时，打个标签
    private boolean isSingle, isWifiList, overMaxCount;

    // 同时点击单个设备多次时，也需要回到
    private List<String> singleDevUuidManyList = new ArrayList<>();

    public void setOverMaxCount(boolean overMaxCount) {
        this.overMaxCount = overMaxCount;
    }

    public boolean isOverMaxCount() {
        return overMaxCount;
    }

    public void setWifiList(boolean wifiList) {
        isWifiList = wifiList;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    public void setSingleDevMultiDisNetworkListener(SingleDevMultiDisNetworkListener singleDevMultiDisNetworkListener) {
        this.singleDevMultiDisNetworkListener = singleDevMultiDisNetworkListener;
    }

    public void setMultiDevDisNetworkListener(MultiDevDisNetworkListener multiDevDisNetworkListener) {
        this.multiDevDisNetworkListener = multiDevDisNetworkListener;
    }

    public void addConnectCount(String uuid) {
        LgUtils.w(TAG + "   bleConnectCount  addConnectCount  uuid=" + uuid
                + "   connectCountMap=" + !connectCountMap.containsKey(uuid) + "   !isSingle=" + !isSingle + "  !isWifiList= " + !isWifiList);
        if (!connectCountMap.containsKey(uuid)) {
            connectCountMap.put(uuid, connectCountMap.size() + 1);
            if (multiDevDisNetworkListener != null && !isSingle && !isWifiList) {
                multiDevDisNetworkListener.bleConnectCount(connectCountMap.size());
            }
        }
    }

    /**
     * 多设备同时配网时，移除连接
     */
    public void removeConnectCount(String uuid) {
        LgUtils.w(TAG + "   bleConnectCount  removeConnectCount  uuid=" + uuid
                + "   connectCountMap=" + connectCountMap.containsKey(uuid) + "   !isSingle=" + !isSingle + "  !isWifiList= " + !isWifiList);
        if (connectCountMap.containsKey(uuid)) {
            connectCountMap.remove(uuid);
            if (multiDevDisNetworkListener != null && !isSingle && !isWifiList && overMaxCount) {
                multiDevDisNetworkListener.bleConnectCount(connectCountMap.size());
            }
        }
    }

    public void addSingleManyData(String uuid) {
        LgUtils.w(TAG + "   bleConnectCount  addSingleManyData  uuid=" + uuid
                + "   singleDevUuidManyList.contains(uuid)=" + singleDevUuidManyList.contains(uuid) + "   singleDevUuidManyList.size=" + singleDevUuidManyList.size());
        if (!singleDevUuidManyList.contains(uuid)) {
            singleDevUuidManyList.add(uuid);
        }
    }

    /**
     * 单设备多点同时配网时，
     */
    public void removeSingleManyData(String uuid) {
        LgUtils.w(TAG + "   bleConnectCount  addSingleManyData  uuid=" + uuid
                + "   singleDevUuidManyList.contains(uuid)=" + singleDevUuidManyList.contains(uuid) + "   singleDevUuidManyList.size=" + singleDevUuidManyList.size());
        if (singleDevUuidManyList.contains(uuid)&&singleDevMultiDisNetworkListener!=null) {
            singleDevUuidManyList.remove(uuid);
            if (!singleDevUuidManyList.isEmpty()) {
                singleDevMultiDisNetworkListener.singleDeviceBleConnectCount(singleDevUuidManyList.get(0));
            }
        }
    }

    /**单独取消*/
    public void removeCancelSingle(String uuid) {
        LgUtils.w(TAG + "   bleConnectCount  removeCancelSingle  uuid=" + uuid
                + "   singleDevUuidManyList.contains(uuid)=" + singleDevUuidManyList.contains(uuid) + "   singleDevUuidManyList.size=" + singleDevUuidManyList.size());
        if (singleDevUuidManyList.contains(uuid)) {
            singleDevUuidManyList.remove(uuid);
        }
    }

    /**
     * 多设备同时配网时，移除连接
     */
    public void resetConnectCount() {
        LgUtils.w(TAG + "   resetConnectCount  connectCountMap=" + connectCountMap.size()
                +"   singleDevUuidManyList.size="+singleDevUuidManyList.size());
        connectCountMap.clear();
        singleDevUuidManyList.clear();
    }

    /**
     * 根据mac地址对比获取uuid
     */
    public String getDeviceUuidByMac(String address, String uuid) {
        String tempUuid = uuid;
        Map<String, ProductInfoBean> productInfoBeanMap = getProductInfoBeanMap();
        if (productInfoBeanMap != null && !productInfoBeanMap.isEmpty()) {
            for (String key : productInfoBeanMap.keySet()) {
                ProductInfoBean productInfoBean = productInfoBeanMap.get(key);
                if (productInfoBean != null && TextUtils.equals(productInfoBean.getAddress(), address)) {
                    tempUuid = productInfoBean.getDeviceUuid();
                    break;
                }
            }
        }
        return tempUuid;
    }

    /**
     * 缓存已出现在自动搜索页面数据，调查询设备接口成功的，需要拿到里面的模型数据
     */
    private final Map<String, ProductInfoBean> searchProductInfoBeanMap = new HashMap<>();

    public Map<String, ProductInfoBean> getSearchProductInfoBeanMap() {
        return searchProductInfoBeanMap;
    }

    public ProductInfoBean getSearchShowProductInfoBean(String uuid) {
        ProductInfoBean productInfoBean = new ProductInfoBean();
        if (searchProductInfoBeanMap.isEmpty()) {
            return productInfoBean;
        }
        for (String key : searchProductInfoBeanMap.keySet()) {
            if (TextUtils.equals(key, uuid)) {
                productInfoBean = searchProductInfoBeanMap.get(key);
                break;
            }
        }
        if (productInfoBean == null) {
            productInfoBean = new ProductInfoBean();
        }
        return productInfoBean;
    }

    public void removeSearchShowProductInfoBean(String uuid) {
        if (searchProductInfoBeanMap.isEmpty()) {
            return;
        }
        if (searchProductInfoBeanMap.containsKey(uuid)) {
            searchProductInfoBeanMap.remove(uuid);
        }
    }

    public void addSearchShowProductInfoBean(String uuid, ProductInfoBean productInfoBean) {
        searchProductInfoBeanMap.put(uuid, productInfoBean);
    }

    public void resetSearchProductInfoBeanMap() {
        searchProductInfoBeanMap.clear();
    }
}
