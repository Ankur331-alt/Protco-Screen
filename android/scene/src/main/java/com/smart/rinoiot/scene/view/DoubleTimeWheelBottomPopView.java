package com.smart.rinoiot.scene.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.contrarywind.view.WheelView;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.smart.rinoiot.scene.R;

/**
 * Description: 双时间滚轮选择器
 * Create by dance, at 2018/12/25
 */
@SuppressLint("SetTextI18n")
public class DoubleTimeWheelBottomPopView extends CenterPopupView implements View.OnClickListener {

    private String startTime = "00:00";
    private String endTime = "23:59";

    private TextView tvStartTime;
    private TextView tvEndTime;

    private WheelView hour1;
    private WheelView wvBlank1;
    private WheelView min1;

    private WheelView hour2;
    private WheelView wvBlank2;
    private WheelView min2;

    public DoubleTimeWheelBottomPopView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom_double_time_wheel;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        getPopupImplView().setBackground(XPopupUtils.createDrawable(getResources().getColor(R.color.cen_common_item_bg_color),
                popupInfo.borderRadius, popupInfo.borderRadius, popupInfo.borderRadius,popupInfo.borderRadius));

        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);

        hour1 = findViewById(R.id.hour1);
        wvBlank1 = findViewById(R.id.wvBlank1);
        min1 = findViewById(R.id.min1);

        hour2 = findViewById(R.id.hour2);
        wvBlank2 = findViewById(R.id.wvBlank2);
        min2 = findViewById(R.id.min2);

        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvConfirm).setOnClickListener(this);

        tvStartTime.setText(startTime);
        tvEndTime.setText(endTime);

        initWheelView();
    }

    private void initWheelView() {
        String[] time1 = startTime.split(":");
        String[] time2 = endTime.split(":");

        hour1.setAdapter(new NumericWheelAdapter(0, 23));
        hour1.setCyclic(true);
        hour1.setItemsVisibleCount(3);
        hour1.setCurrentItem(stringToInt(time1[0]));

        // 空白间隔，将小时与分钟隔开
        wvBlank1.setAdapter(new NumericWheelAdapter(0, 1));
        wvBlank1.setItemsVisibleCount(3);

        min1.setAdapter(new NumericWheelAdapter(0, 59));
        min1.setCyclic(true);
        min1.setItemsVisibleCount(3);
        min1.setCurrentItem(stringToInt(time1[1]));

        hour2.setAdapter(new NumericWheelAdapter(0, 23));
        hour2.setCyclic(true);
        hour2.setItemsVisibleCount(3);
        hour2.setCurrentItem(stringToInt(time2[0]));

        // 空白间隔，将小时与分钟隔开
        wvBlank2.setAdapter(new NumericWheelAdapter(0, 1));
        wvBlank2.setItemsVisibleCount(3);

        min2.setAdapter(new NumericWheelAdapter(0, 59));
        min2.setCyclic(true);
        min2.setItemsVisibleCount(3);
        min2.setCurrentItem(stringToInt(time2[1]));

        hour1.setOnItemSelectedListener(index -> tvStartTime.setText(intToString(index) + ":" + intToString(min1.getCurrentItem())));
        min1.setOnItemSelectedListener(index -> tvStartTime.setText(intToString(hour1.getCurrentItem()) + ":" + intToString(index)));
        hour2.setOnItemSelectedListener(index -> tvEndTime.setText(intToString(index) + ":" + intToString(min2.getCurrentItem())));
        min2.setOnItemSelectedListener(index -> tvEndTime.setText(intToString(hour2.getCurrentItem()) + ":" + intToString(index)));
    }

    private int stringToInt(String target) {
        return Integer.parseInt(target);
    }

    private String intToString(int target) {
        StringBuilder result = new StringBuilder();
        if (target < 10) result.append(0);
        result.append(target);
        return result.toString();
    }

    private String getStartTime() {
        return tvStartTime.getText().toString();
    }

    private String getEndTime() {
        return tvEndTime.getText().toString();
    }

    private OnConfirmListener confirmListener;

    public DoubleTimeWheelBottomPopView setOnConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
        return this;
    }

    public DoubleTimeWheelBottomPopView setEffectivePeriod(String startTime, String endTime) {
        if (!TextUtils.isEmpty(startTime)) {
            this.startTime = startTime;
        }
        if (!TextUtils.isEmpty(endTime)) {
            this.endTime = endTime;
        }
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCancel) {
            dismiss();
        } else if (v.getId() == R.id.tvConfirm) {
            if (confirmListener != null) confirmListener.onConfirm(getStartTime(), getEndTime());
            dismiss();
        }
    }

    public interface OnConfirmListener {
        void onConfirm(String startTime, String endTime);
    }
}