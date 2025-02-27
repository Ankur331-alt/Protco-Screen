package com.smart.rinoiot.common.magicIndicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;


/**
 * 自定义指示器颜色渐变
 */
@SuppressLint("DrawAllocation")
public class GradientLinePagerIndicator extends LinePagerIndicatorEx {
    public GradientLinePagerIndicator(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
         LinearGradient lg = new LinearGradient(getLineRect().left, getLineRect().top, getLineRect().right, getLineRect().bottom, new int[]{0xffbef068, 0xff0bd29d}, null, LinearGradient.TileMode.CLAMP);
        getPaint().setShader(lg);
        canvas.drawRoundRect(getLineRect(), getRoundRadius(), getRoundRadius(), getPaint());
    }
}