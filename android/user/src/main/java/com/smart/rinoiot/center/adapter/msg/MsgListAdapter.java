package com.smart.rinoiot.center.adapter.msg;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.smart.rinoiot.center.bean.msg.MsgRecordItemBean;
import com.smart.rinoiot.common.impl.ImageLoader;
import com.smart.rinoiot.common.page.BaseSectionAdapter;
import com.smart.rinoiot.common.utils.DateUtils;
import com.smart.rinoiot.user.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @Author : tw
 * @Time : On 2022/9/28 17:24
 * @Description : MsgListAdapter  消息列表选择器
 */
public class MsgListAdapter extends BaseSectionAdapter<MsgRecordItemBean> {

    public MsgListAdapter(List<MsgRecordItemBean> data) {
        super(R.layout.item_msg_list_head_item, R.layout.item_msg_list_item, data);
        addItemType(SectionEntity.HEADER_TYPE, R.layout.item_msg_list_head_item);
        addItemType(SectionEntity.NORMAL_TYPE, R.layout.item_msg_list_item);
    }

    @Override
    protected void convert(@NonNull @NotNull BaseViewHolder helper, MsgRecordItemBean item) {
        ImageView iv_msg = helper.getView(R.id.iv_msg);
        if (item.getActionSource() == 3 || item.getActionSource() == 4 || item.getActionSource() == 5) {
            int resourceId;
            if (item.getActionSource() == 3) {//动作源(1=问题反馈，2=资产变化，3=自动化，4=一键执行，5=设备告警，6=设备操作，7=云定时)
                resourceId = R.drawable.icon_msg_auto;
            } else if (item.getActionSource() == 4) {
                resourceId = R.drawable.icon_msg_manual;
            } else {
                resourceId = R.drawable.icon_msg_alarm;
            }
            iv_msg.setImageResource(resourceId);
        } else {
            ImageLoader.getInstance().bindImageUrl(TextUtils.isEmpty(item.getIcon()) ? "" : item.getIcon(), iv_msg, R.drawable.icon_placeholder, R.drawable.icon_placeholder);
        }
        helper.setText(R.id.tv_msg_name, item.getTitle());
        helper.setText(R.id.tv_msg_content, item.getContent());
        helper.setText(R.id.tv_msg_family_name, item.getAssetName());
        helper.setText(R.id.tv_msg_time, DateUtils.getDateHour(item.getCreateTime()));
    }

    @Override
    protected void convertHeader(@NotNull BaseViewHolder baseViewHolder, @NotNull MsgRecordItemBean msgRecordItemBean) {
        baseViewHolder.setText(R.id.tv_msg_time, msgRecordItemBean.getHeader());
    }
}
