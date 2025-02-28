package com.smart.rinoiot.common.bean;

import java.io.Serializable;
import java.util.List;

public class SceneDeviceDpBean implements Serializable {
    private List<DeviceDpBean> actionSupportDps;
    private List<DeviceDpBean> conditionSupportDps;

    public List<DeviceDpBean> getActionSupportDps() {
        return actionSupportDps;
    }

    public void setActionSupportDps(List<DeviceDpBean> actionSupportDps) {
        this.actionSupportDps = actionSupportDps;
    }

    public List<DeviceDpBean> getConditionSupportDps() {
        return conditionSupportDps;
    }

    public void setConditionSupportDps(List<DeviceDpBean> conditionSupportDps) {
        this.conditionSupportDps = conditionSupportDps;
    }
}
