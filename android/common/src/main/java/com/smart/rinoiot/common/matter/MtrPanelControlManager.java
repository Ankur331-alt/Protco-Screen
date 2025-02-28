package com.smart.rinoiot.common.matter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.MatterLocalBean;
import com.smart.rinoiot.common.bean.PanelInfo;
import com.smart.rinoiot.common.bean.PanelMultiLangBean;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.listener.DownloadFileListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.CommonNetworkManager;
import com.smart.rinoiot.common.manager.PanelParamsManager;
import com.smart.rinoiot.common.rn.BundleJSONConverter;
import com.smart.rinoiot.common.rn.MtrDevicePanelActivity;
import com.smart.rinoiot.common.rn.RNDialogUtils;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.common.utils.FileUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.common.view.CircleProgressPopView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 单设备面板数据处理
 * @author author
 */
public class MtrPanelControlManager {
    private static final String TAG = MtrPanelControlManager.class.getSimpleName();
    private static MtrPanelControlManager instance;

    /**
     * 物模型接口、面板多语言接口
     */
    private boolean dpInfoFlag, panelLangFlag;

    /**
     * matter设备物模型影子数据更新
     */
    public static String MATTER_SWITCH = "switch";
    public static String MATTER_COLOR = "color";
    public static String MATTER_COLOR_TEMP = "color_temp";
    public static String MATTER_BRIGHTNESS = "brightness";

    private MtrPanelControlManager() {
    }

    public static MtrPanelControlManager getInstance() {
        if (instance == null) {
            instance = new MtrPanelControlManager();
        }
        return instance;
    }

    /**
     * 打开面板容器
     */
    public void goToPanel(Context context, DeviceInfoBean deviceInfoBean) {
        dpInfoFlag = false;
        panelLangFlag = false;
        if (deviceInfoBean != null) {
            getDpInfos(context, deviceInfoBean);
            getPanelLang(context, deviceInfoBean);
        } else {
            Constant.ADD_PANEL_ENTRANCE = false;
            RNDialogUtils.getInstance().stopDialogLoading();
            ToastUtil.showMsg("设备基础信息为空，不能打开面板");
        }
    }

    private void getPanel(Context context, DeviceInfoBean deviceInfoBean) {
        RNDialogUtils.getInstance().stopDialogLoading();
        CircleProgressPopView dialog = new CircleProgressPopView(context, true);
        new XPopup.Builder(context).dismissOnBackPressed(false).dismissOnTouchOutside(false).asCustom(dialog).show();
        CommonNetworkManager.getInstance().getPanelAsync(deviceInfoBean.getProductId(), new CallbackListener<PanelInfo>() {
            @Override
            public void onSuccess(PanelInfo data) {
                String fileName = deviceInfoBean.getProductId() + data.getId();
                if (!FileUtils.isFileExistsForFilesDir(fileName)) {
                    String downLoadFileName = fileName + ".zip";
                    CommonNetworkManager.getInstance().downloadFileAsync(data.getAndroidUrl(), downLoadFileName, new DownloadFileListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onProgress(int progress) {
                            AppExecutors.getInstance().mainThread().execute(() -> dialog.setProgress(progress));
                        }

                        @Override
                        public void onFinish(String path) {
                            AppExecutors.getInstance().mainThread().execute(() -> {
                                if (FileUtils.uncompressZip4j(downLoadFileName, "", true)) {
                                    gotoPanel(context, fileName, deviceInfoBean);
                                    dialog.dismiss();
                                    return;
                                }
                                dialog.dismiss();
                                Constant.ADD_PANEL_ENTRANCE = false;
                            });
                        }

                        @Override
                        public void onError(String msg) {
                            AppExecutors.getInstance().mainThread().execute(() -> {
                                ToastUtil.showMsg(msg);
                                dialog.dismiss();
                                Constant.ADD_PANEL_ENTRANCE = false;
                            });
                        }
                    });
                } else {
                    gotoPanel(context, fileName, deviceInfoBean);
                    dialog.dismiss();
                    Constant.ADD_PANEL_ENTRANCE = false;
                }
            }

            @Override
            public void onError(String code, String error) {
                dialog.dismiss();
                ToastUtil.showMsg(error);
                Constant.ADD_PANEL_ENTRANCE = false;
            }
        });
    }

    private void gotoPanel(Context context, String rnDirName, DeviceInfoBean deviceInfoBean) {
        Bundle bundle = new Bundle();
        bundle.putString("id", deviceInfoBean.getId());
        bundle.putString("name", deviceInfoBean.getName());
        bundle.putString("deviceType", "Matter");
        bundle.putString("productId", deviceInfoBean.getProductId());

        ArrayList<Bundle> dataPointArray = new ArrayList<>();
        Bundle dataPointArrayItem = null;
        Bundle dataPointValues = new Bundle();
        JSONArray jsonArray = new JSONArray();
        for (DeviceDpBean item : deviceInfoBean.getDpInfoVOList()) {
            if (!TextUtils.isEmpty(item.getDpJson())) {
                Log.d(TAG, "Should be JSON: "  +  item.getDpJson());
                dataPointArrayItem = BundleJSONConverter.jsonStringToBundle(item.getDpJson());
                try {
                    JSONObject jsonObject = new JSONObject(item.getDpJson());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (item.getValue() instanceof Integer) {
                dataPointValues.putInt(item.getKey(), (int) item.getValue());
            } else if (item.getValue() instanceof Double) {
                dataPointValues.putDouble(item.getKey(), (Double) item.getValue());
            } else if (item.getValue() instanceof Float) {
                dataPointValues.putFloat(item.getKey(), (Float) item.getValue());
            } else if (item.getValue() instanceof Boolean) {
                dataPointValues.putBoolean(item.getKey(), (Boolean) item.getValue());
            } else if (item.getValue() instanceof String) {
                dataPointValues.putString(item.getKey(), String.valueOf(item.getValue()));
            } else {
                dataPointValues.putString(item.getKey(), new Gson().toJson(item.getValue()));
            }
            dataPointArray.add(dataPointArrayItem);
        }

        // String 物模型
        bundle.putString("dataPointJson", jsonArray.toString());
        // Array 物模型
        bundle.putParcelableArrayList("dataPointArray", dataPointArray);

        // Dictionary或Map 云端物模型数据
        bundle.putBundle("dataPointValues", dataPointValues);
        bundle.putBoolean("isGroup", false);
        bundle.putBoolean("isOnline", true);

        //获取产品多语言词条
        bundle.putString("productLang", new Gson().toJson(CacheDataManager.getInstance().getPanelMultiLang(deviceInfoBean.getProductId())));

        PanelParamsManager.getInstance().panelParamsRawData(bundle, deviceInfoBean, rnDirName);
        context.startActivity(new Intent(context, MtrDevicePanelActivity.class)
                .putExtra(Constant.PANEL_DEVICE_BUNDLE_DATA, bundle)
                .putExtra(Constant.PANEL_DIRNAME_PATH, rnDirName)
                .putExtra(Constant.PANEL_DEVICE_INFO, deviceInfoBean));
    }

    /**
     * 根据设备id获取设备物模型
     */
    private void getDpInfos(Context context, DeviceInfoBean deviceInfoBean) {
        CommonNetworkManager.getInstance().getDeviceDataPointsAsync(deviceInfoBean.getId(), new CallbackListener<List<DeviceDpBean>>() {
            @Override
            public void onSuccess(List<DeviceDpBean> data) {
                List<DeviceDpBean> dpBeanList = updateMatterDeviceDpData(deviceInfoBean.getId(), data);
                deviceInfoBean.setDpInfoVOList(dpBeanList == null ? new ArrayList<>() : dpBeanList);
                dpInfoFlag = true;
                LgUtils.w(TAG + "   getDpInfos  panelLangFlag=" + panelLangFlag + "   dpInfoFlag=" + dpInfoFlag +
                        "   ==>" + CacheDataManager.getInstance().getPanelMultiLang(deviceInfoBean.getProductId()));
                if (panelLangFlag && dpInfoFlag) {
                    getPanel(context, deviceInfoBean);
                }
            }

            @Override
            public void onError(String code, String error) {
                LgUtils.w(TAG + "getDpInfos  onError   error=" + error);
                ToastUtil.showMsg(error);
                dpInfoFlag = false;
                Constant.ADD_PANEL_ENTRANCE = false;
                RNDialogUtils.getInstance().stopDialogLoading();
            }
        });
    }

    /**
     * 获取产品多语言词条
     */
    private void getPanelLang(Context context, DeviceInfoBean deviceInfoBean) {
        CommonNetworkManager.getInstance().getPanelLangAsync(deviceInfoBean.getProductId(), new CallbackListener<PanelMultiLangBean>() {
            @Override
            public void onSuccess(PanelMultiLangBean data) {
                panelLangFlag = true;
                if (data == null) {
                    PanelMultiLangBean panelMultiLang = CacheDataManager.getInstance().getPanelMultiLang(deviceInfoBean.getProductId());
                    if (panelMultiLang == null) {
                        panelLangFlag = false;
                        Constant.ADD_PANEL_ENTRANCE = false;
                        RNDialogUtils.getInstance().stopDialogLoading();
                        return;
                    }
                } else {
                    CacheDataManager.getInstance().savePanelMultiLang(deviceInfoBean.getProductId(), data);
                }
                LgUtils.w(TAG + "   getPanelLang  panelLangFlag=" + panelLangFlag + "   dpInfoFlag=" + dpInfoFlag +
                        "   ==>" + new Gson().toJson(CacheDataManager.getInstance().getPanelMultiLang(deviceInfoBean.getProductId())));
                if (panelLangFlag && dpInfoFlag) {
                    getPanel(context, deviceInfoBean);
                }
            }

            @Override
            public void onError(String code, String error) {
                RNDialogUtils.getInstance().stopDialogLoading();
                ToastUtil.showMsg(error);
                PanelMultiLangBean panelMultiLang = CacheDataManager.getInstance().getPanelMultiLang(deviceInfoBean.getProductId());
                LgUtils.w(TAG + "getPanelLang  onError   error=" + error + "   ==>" + new Gson().toJson(panelMultiLang));
                if (panelMultiLang != null) {
                    panelLangFlag = true;
                    return;
                }
                Constant.ADD_PANEL_ENTRANCE = false;
            }
        });
    }

    public List<DeviceDpBean> updateMatterDeviceDpData(String devId, List<DeviceDpBean> data) {
        List<DeviceDpBean> dpBeanList = new ArrayList<>();
        MatterLocalBean matterDeviceData = CacheDataManager.getInstance().getMatterDeviceData(devId);
        if (matterDeviceData == null) {
            matterDeviceData = new MatterLocalBean();
        }
        if (data == null || data.isEmpty()) {
            return data;
        }
        for (DeviceDpBean item : data) {
            //获取产品多语言词条
            if (item.getKey().contains(MATTER_SWITCH)) {
                item.setValue(matterDeviceData.isSwitch());
            } else if (item.getKey().contains(MATTER_COLOR_TEMP)) {
                //色温
                MatterLocalBean.MatterColor color = matterDeviceData.getColor();
                if (color != null) {
                    item.setValue(color.getValue());
                }
            } else if (item.getKey().contains(MATTER_COLOR)) {
                //颜色
                MatterLocalBean.MatterColor color = matterDeviceData.getColor();
                if (color != null) {
                    String stringBuffer = temToHexString(color.getHue(), 4) +
                            temToHexString(color.getSaturation(), 0) +
                            temToHexString(100, 0);
                    item.setValue(stringBuffer);
                } else {
                    item.setValue("00006464");
                }
            } else if (item.getKey().contains(MATTER_BRIGHTNESS)) {
                //亮度
                item.setValue(matterDeviceData.getBrightness());
            }
            dpBeanList.add(item);
        }
        return dpBeanList;
    }

    /**
     * 十进制转16进制，且设置保留位数
     */
    public String temToHexString(double value, int length) {
        StringBuilder stringBuffer = new StringBuilder();
        String hexString = Long.toHexString((long) value);
        //保留默认位数
        if (length != 0) {
            int size = length - hexString.length();
            for (int i = 0; i < size; i++) {
                stringBuffer.append("0");
            }
        }
        stringBuffer.append(hexString);
        return stringBuffer.toString();
    }
}
