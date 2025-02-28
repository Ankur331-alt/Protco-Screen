//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.smart.rinoiot.common.rn.view;

import android.graphics.Paint;
import android.util.SparseIntArray;
import android.view.View;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import java.util.ArrayList;

public class RinoRCTWheelViewManager extends SimpleViewManager<WheelPicker> implements RinoRCTWheelViewManagerSpec<WheelPicker>, WheelPicker.OnItemSelectedListener {
    public static final int COLOR_GRAY = -1118482;
    private ReactContext mReactContext;
    private SparseIntArray selectedIndexMap = new SparseIntArray();


    @ReactProp(
        name = "items"
    )
    public void setItems(WheelPicker view, ReadableArray items) {
        ArrayList<String> labelData = new ArrayList();

        for(int i = 0; i < items.size(); ++i) {
            ReadableMap itemMap = items.getMap(i);
            labelData.add(itemMap.getString("label"));
        }

        view.setData(labelData);
        view.setSelectedItemPosition(this.selectedIndexMap.get(view.getId()), true);
    }

    @ReactProp(
        name = "loop"
    )
    public void setLoop(WheelPicker view, boolean loop) {
        view.setCyclic(loop);
    }

    @ReactProp(
        name = "textSize"
    )
    public void setTextSize(WheelPicker view, float textSize) {
        view.setItemTextSize((int)PixelUtil.toPixelFromDIP(textSize));
    }

    @ReactProp(
        name = "selectedIndex"
    )
    public void setSelectedIndex(WheelPicker view, int selectedIndex) {
        this.selectedIndexMap.put(view.getId(), selectedIndex);
        view.setSelectedItemPosition(selectedIndex, true);
    }

    @ReactProp(
        name = "itemTextColor",
        customType = "Color",
        defaultInt = -1118482
    )
    public void setItemTextColor(WheelPicker view, int itemTextColor) {
        if (view != null) {
            view.setItemTextColor(itemTextColor);
        }

    }

    @ReactProp(
        name = "selectedItemTextColor",
        customType = "Color",
        defaultInt = -1118482
    )
    public void setSelectedItemTextColor(WheelPicker view, int selectedItemTextColor) {
        if (view != null) {
            view.setSelectedItemTextColor(selectedItemTextColor);
        }

    }

    @ReactProp(
        name = "dividerColor",
        customType = "Color",
        defaultInt = -1118482
    )
    public void setDividerColor(WheelPicker view, int dividerColor) {
        if (view != null) {
            view.setIndicatorColor(dividerColor);
        }

    }

    @ReactProp(
        name = "visibleItemCount"
    )
    public void setVisibleItemCount(WheelPicker view, int visibleItemCount) {
        if (view != null && visibleItemCount != 0) {
            view.setVisibleItemCount(visibleItemCount);
        }

    }

    @ReactProp(
        name = "itemAlign"
    )
    public void setItemAlign(WheelPicker view, String itemAlign) {
        if (view != null) {
            byte var4 = -1;
            switch(itemAlign.hashCode()) {
            case -1364013995:
                if (itemAlign.equals("center")) {
                    var4 = 4;
                }
                break;
            case -46581362:
                if (itemAlign.equals("flex-start")) {
                    var4 = 1;
                }
                break;
            case 3317767:
                if (itemAlign.equals("left")) {
                    var4 = 0;
                }
                break;
            case 108511772:
                if (itemAlign.equals("right")) {
                    var4 = 2;
                }
                break;
            case 1742952711:
                if (itemAlign.equals("flex-end")) {
                    var4 = 3;
                }
            }

            switch(var4) {
            case 0:
            case 1:
                view.setItemAlign(1);
                break;
            case 2:
            case 3:
                view.setItemAlign(2);
                break;
            case 4:
            default:
                view.setItemAlign(0);
            }
        }

    }

    public void onChange(View view, WritableMap param) {
        if (this.getReactApplicationContext() != null && view != null) {
            ((RCTEventEmitter)this.getReactApplicationContext().getJSModule(RCTEventEmitter.class)).receiveEvent(view.getId(), "topChange", param);
        }

    }

    public String getName() {
        return "RinoRCTWheelViewManager";
    }

    public ReactContext getReactApplicationContext() {
        return this.mReactContext;
    }

    protected WheelPicker createViewInstance(ThemedReactContext reactContext) {
        this.mReactContext = reactContext;
        WheelPicker wheelPicker = new WheelPicker(reactContext);
        wheelPicker.setOnItemSelectedListener(this);
        wheelPicker.setIndicator(true);
        wheelPicker.setIndicatorSize((int)PixelUtil.toPixelFromDIP(1.0F));
        wheelPicker.setSelectedItemTextColor(-16777216);
        wheelPicker.setAtmospheric(true);
        wheelPicker.setCurved(true);
        wheelPicker.setCyclic(true);
        wheelPicker.setLayerType(View.LAYER_TYPE_HARDWARE, (Paint)null);
        return wheelPicker;
    }

    public void onItemSelected(final WheelPicker picker, Object data, int position) {
        if (picker != null) {
            final WritableMap event = Arguments.createMap();
            event.putInt("newIndex", position);
            if (this.mReactContext != null && this.mReactContext.hasActiveCatalystInstance()) {
                this.mReactContext.runOnNativeModulesQueueThread(new Runnable() {
                    public void run() {
                        ((RCTEventEmitter) RinoRCTWheelViewManager.this.mReactContext.getJSModule(RCTEventEmitter.class)).receiveEvent(picker.getId(), "topChange", event);
                    }
                });
            }
        }

    }
}
