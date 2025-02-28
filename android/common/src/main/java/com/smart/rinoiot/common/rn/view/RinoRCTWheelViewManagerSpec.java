//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.smart.rinoiot.common.rn.view;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;

public interface RinoRCTWheelViewManagerSpec<T extends View> {
    void setItems(T var1, ReadableArray var2);

    void setLoop(T var1, boolean var2);

    void setTextSize(T var1, float var2);

    void setSelectedIndex(T var1, int var2);

    void setItemTextColor(T var1, int var2);

    void setSelectedItemTextColor(T var1, int var2);

    void setDividerColor(T var1, int var2);

    void setVisibleItemCount(T var1, int var2);

    void setItemAlign(T var1, String var2);

    void onChange(View var1, WritableMap var2);
}
