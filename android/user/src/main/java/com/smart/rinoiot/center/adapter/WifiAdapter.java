package com.smart.rinoiot.center.adapter;

import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.WifiUtil;
import com.smart.rinoiot.user.R;

import java.util.List;

/**
 * @author tw
 * @time 2022/10/11 11:35
 * @description 切换wifi
 */
public class WifiAdapter extends BaseQuickAdapter<ScanResult, BaseViewHolder> {
    public WifiAdapter(@Nullable List<ScanResult> data) {
        super(R.layout.cen_connect_network_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ScanResult item) {
        if (item==null) return;
        ((ImageView) helper.getView(R.id.iv_network)).setImageResource(getWifiRssi(item.level,TextUtils.equals(item.SSID, WifiUtil.getInstance().getSSID())));
        helper.setText(R.id.tv_connect_name, item.SSID);
        String connectStatus = "";
        if (TextUtils.equals(item.SSID, WifiUtil.getInstance().getSSID())) {
            connectStatus = getContext().getString(R.string.rino_user_current_network_available);
            helper.setGone(R.id.tv_connect_status,false);

        } else {
            if (!TextUtils.isEmpty(SharedPreferenceUtil.getInstance().get(item.SSID, ""))) {//密码不为空
                connectStatus = getContext().getString(R.string.rino_user_wifi_save_password);
                helper.setGone(R.id.tv_connect_status,false);
            }
        }
        helper.setText(R.id.tv_connect_status,connectStatus);
    }

    public int getWifiRssi(int level,boolean isSave) {
        int resourceId;
        if (level < -78) {
            resourceId = isSave?R.drawable.icon_network_selected:R.drawable.icon_network_normal;
        } else if (level >= -78 && level < -67) {
            resourceId = isSave?R.drawable.icon_network_selected:R.drawable.icon_network_normal;
        } else if (level >= -67 && level < -55) {
            resourceId = isSave?R.drawable.icon_network_selected:R.drawable.icon_network_normal;
        } else {
            resourceId = isSave?R.drawable.icon_network_selected:R.drawable.icon_network_normal;
        }
        return resourceId;
    }


}
