//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.smart.rinoiot.common.rn.view;

import android.graphics.Typeface;
import java.util.List;

public interface IWheelPicker {
    int getVisibleItemCount();

    void setVisibleItemCount(int var1);

    boolean isCyclic();

    void setCyclic(boolean var1);

    void setOnItemSelectedListener(WheelPicker.OnItemSelectedListener var1);

    int getSelectedItemPosition();

    void setSelectedItemPosition(int var1);

    int getCurrentItemPosition();

    List getData();

    void setData(List var1);

    void setSameWidth(boolean var1);

    boolean hasSameWidth();

    void setOnWheelChangeListener(WheelPicker.OnWheelChangeListener var1);

    String getMaximumWidthText();

    void setMaximumWidthText(String var1);

    int getMaximumWidthTextPosition();

    void setMaximumWidthTextPosition(int var1);

    int getSelectedItemTextColor();

    void setSelectedItemTextColor(int var1);

    int getItemTextColor();

    void setItemTextColor(int var1);

    int getItemTextSize();

    void setItemTextSize(int var1);

    int getItemSpace();

    void setItemSpace(int var1);

    void setIndicator(boolean var1);

    boolean hasIndicator();

    int getIndicatorSize();

    void setIndicatorSize(int var1);

    int getIndicatorColor();

    void setIndicatorColor(int var1);

    void setCurtain(boolean var1);

    boolean hasCurtain();

    int getCurtainColor();

    void setCurtainColor(int var1);

    void setAtmospheric(boolean var1);

    boolean hasAtmospheric();

    boolean isCurved();

    void setCurved(boolean var1);

    int getItemAlign();

    void setItemAlign(int var1);

    Typeface getTypeface();

    void setTypeface(Typeface var1);
}
