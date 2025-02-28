package com.smart.rinoiot.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**自定义刷新控件，解决滑动冲突*/
public class MySwipeRefreshLayout extends SwipeRefreshLayout {

    private float mInitialDownYValue = 0;
    private int miniTouchSlop;

    public MySwipeRefreshLayout(Context context) {
        super(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs){
        super(context,attrs);
        miniTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop()*8;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()){
            return false;
        }
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownYValue = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float  yDiff = ev.getY() - mInitialDownYValue;
                if (yDiff < miniTouchSlop ){
                    return false;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

}