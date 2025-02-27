package com.smart.rinoiot.center.adapter;

import android.net.wifi.ScanResult;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.user.R;

import java.util.List;

/**
 * @author tw
 * @time 2022/10/11 11:35
 * @description 附近wifi列表
 */
public class WifiListAdapter extends BaseQuickAdapter<ScanResult, BaseViewHolder> {
    public WifiListAdapter(@Nullable List<ScanResult> data) {
        super(R.layout.cen_connect_network_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ScanResult item) {
        ((ImageView) helper.getView(R.id.iv_network)).setImageResource(getWifiRssi(item.level));
        helper.setText(R.id.tv_connect_name, item.SSID);
    }

    public int getWifiRssi(int level) {
        int resourceId;
        if (level < -78) {
            resourceId = R.drawable.icon_network_normal;
        } else if (level >= -78 && level < -67) {
            resourceId = R.drawable.icon_network_normal;
        } else if (level >= -67 && level < -55) {
            resourceId = R.drawable.icon_network_normal;
        } else {
            resourceId = R.drawable.icon_network_normal;
        }
        return resourceId;
    }
}
