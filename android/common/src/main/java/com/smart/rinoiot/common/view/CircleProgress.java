package com.smart.rinoiot.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.utils.DpUtils;

public class CircleProgress extends View {

    private Paint _paint;
    private RectF _rectF;
    private Rect _rect;
    private int _current = 1, _max = 100;
    // 圆弧（也可以说是圆环）的宽度
    private float _arcWidth = DpUtils.dip2px(1.5f);
    // 控件的宽度
    private float _width;
    // 显示进度文案
    private boolean showText;
    // 文字大小
    private float textSize = 14;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _paint = new Paint();
        _paint.setAntiAlias(true);
        _rectF = new RectF();
        _rect = new Rect();
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public void setCurrent(int _current) {
//        LgUtils.i(TAG + " ---> 当前值：" + _current + "，最大值：" + _max);
        this._current = _current;
        invalidate();
    }

    public int getCurrent() {
        return _current;
    }

    public void setMax(int _max) {
        this._max = _max;
    }

    public int getMax() {
        return _max;
    }

    public void setStrokeWidth(float _arcWidth) {
        this._arcWidth = _arcWidth;
    }

    public void setTextSize(float size) {
        textSize = size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //getMeasuredWidth获取的是view的原始大小，也就是xml中配置或者代码中设置的大小
        //getWidth获取的是view最终显示的大小，这个大小不一定等于原始大小
        _width = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制圆形
        //设置为空心圆，如果不理解绘制弧线是什么意思就把这里的属性改为“填充”，跑一下瞬间就明白了
        _paint.setStyle(Paint.Style.STROKE);
        //设置圆弧的宽度（圆环的宽度）
        _paint.setStrokeWidth(_arcWidth);
        _paint.setColor(getContext().getResources().getColor(R.color.f_c7c7c7));
        //大圆的半径
        float bigCircleRadius = _width / 2;
        //小圆的半径
        float smallCircleRadius = bigCircleRadius - _arcWidth;
        //绘制小圆
        canvas.drawCircle(bigCircleRadius, bigCircleRadius, smallCircleRadius, _paint);
        _paint.setColor(getContext().getResources().getColor(R.color.f_3761F3));
        _rectF.set(_arcWidth, _arcWidth, _width - _arcWidth, _width - _arcWidth);
        //绘制圆弧
        canvas.drawArc(_rectF, -90, _current * 360 / _max, false, _paint);
        if (showText) {
            // 计算百分比 计算中心方块
            String txt = _current * 100 / _max + "%";
            _paint.setStrokeWidth(0);
            _paint.setTextSize(textSize);
            _paint.getTextBounds(txt, 0, txt.length(), _rect);
            _paint.setColor(getContext().getResources().getColor(R.color.black));
            // 绘制百分比
            canvas.drawText(txt, bigCircleRadius - _rect.width() / 2 - 2, bigCircleRadius + _rect.height() / 2 - 1, _paint);
        } else {
            // 绘制中心小方块
            _paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(bigCircleRadius - DpUtils.dip2px(3), bigCircleRadius + DpUtils.dip2px(3), bigCircleRadius  + DpUtils.dip2px(3), bigCircleRadius  - DpUtils.dip2px(3), _paint);
        }
    }
}
