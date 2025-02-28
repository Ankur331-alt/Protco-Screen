package com.smart.rinoiot.scene.manager;

import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.common.bean.DisplayDateTime;
import com.smart.rinoiot.common.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimerTask;

/**
 * @author edwin
 */
public class DisplayTimeTask extends TimerTask {
    private final MutableLiveData<DisplayDateTime> currentTime;

    public DisplayTimeTask(MutableLiveData<DisplayDateTime> currentTime) {
        this.currentTime = currentTime;
    }

    private String getFormattedDate(){
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        return format.format(new Date());
    }

    @Override
    public void run() {
        // Update the LiveData object
        currentTime.postValue(DateUtils.getDisplayDateTime());
    }
}