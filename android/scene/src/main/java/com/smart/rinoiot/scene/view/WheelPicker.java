package com.smart.rinoiot.scene.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart.rinoiot.common.utils.ScreenUtils;
import com.smart.rinoiot.scene.R;
import com.ycuwq.datepicker.util.LinearGradient;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

/**
 * 滚动选择器
 *
 * @author ycuwq
 * @date 2017/12/12
 */
@SuppressWarnings({"FieldCanBeLocal", "unused", "SameParameterValue"})
public class WheelPicker<T> extends View {

    private final String TAG = "TheWheelPicker";

    /**
     * Item的Text的颜色
     */
    @ColorInt
    private int mTextColor;

    private int mTextSize;

    /**
     * 选中的Item的Text颜色
     */
    @ColorInt
    private int mSelectedItemTextColor;

    /**
     * 选中的Item的Text大小
     */
    private int mSelectedItemTextSize;

    /**
     * 指示器文字颜色
     */
    @ColorInt
    private int mIndicatorTextColor;

    /**
     * 指示器文字大小
     */
    private int mIndicatorTextSize;

    /**
     * 指示器文字距离左侧距离（DP）
     */
    private int mIndicatorPaddingLeft = 20;

    /**
     * 最大的一个Item的文本的宽高
     */
    private int mTextMaxHeight;
    private int mTextMaxWidth =  335;

    /**
     * 显示的Item一半的数量（中心Item上下两边分别的数量）
     * 总显示的数量为 mHalfVisibleItemCount * 2 + 1
     */
    private int mHalfVisibleItemCount;

    /**
     * 两个Item之间的高度间隔
     */
    private int mItemHeightSpace, mItemWidthSpace;

    private int mItemHeight;

    /**
     * 当前的Item的位置
     */
    private int mCurrentPosition;

    /**
     * 幕布颜色
     */
    @ColorInt
    private int mCurtainColor;

    /**
     * 幕布边框的颜色
     */
    @ColorInt
    private int mCurtainBorderColor;

    /**
     * 第一个Item的绘制Text的坐标
     */
    private int mFirstItemDrawX, mFirstItemDrawY;

    /**
     * 中心的Item绘制text的Y轴坐标
     */
    private int mCenterItemDrawnY;

    private final int mTouchSlop;

    private int mTouchDownY;

    /**
     * Y轴Scroll滚动的位移
     */
    private int mScrollOffsetY;

    /**
     * 最后手指Down事件的Y轴坐标，用于计算拖动距离
     */
    private int mLastDownY;

    /**
     * 最大可以Fling的距离
     */
    private int mMaxFlingY, mMinFlingY;

    /**
     * 滚轮滑动时的最小/最大速度
     */
    private int mMinimumVelocity = 50, mMaximumVelocity = 12000;

    /**
     * 字体渐变,开启后越靠近边缘,字体越模糊
     */
    private boolean mIsTextGradual;

    /**
     * 是否将中间的Item放大
     */
    private boolean mIsZoomInCenterItem;

    /**
     * 是否显示幕布，中央Item会遮盖一个颜色颜色
     */
    private boolean mIsShowCurtain;

    /**
     * 是否显示幕布的边框
     */
    private boolean mIsShowCurtainBorder;

    /**
     * 该标记的作用是,令mTouchSlop仅在一个滑动过程中生效一次。
     */
    private boolean mTouchSlopFlag;

    /**
     * 是否循环读取
     */
    private boolean mIsCyclic = true;

    /**
     * 是否是手动停止滚动
     */
    private boolean mIsAbortScroller;

    /**
     * 指示器文字
     * 会在中心文字后边多绘制一个文字。
     */
    private String mIndicatorText;

    /**
     * 输入的一段文字,可以用来测量 mTextMaxWidth
     */
    private String mItemMaximumWidthText;

    /**
     * 整个控件的可绘制面积
     */
    private final Rect mDrawnRect;

    /**
     * 中心被选中的Item的坐标矩形
     */
    private final RectF mSelectedItemRect;

    private Paint mPaint;

    private Paint mSelectPaint;

    private Format mDataFormat;

    private final Scroller mScroller;

    private VelocityTracker mTracker;

    private final LinearGradient mLinearGradient;

    private android.graphics.LinearGradient selectedItemGradientBg;

    private com.ycuwq.datepicker.WheelPicker.OnWheelChangeListener<T> mOnWheelChangeListener;

    private final Handler mHandler = new Handler();

    /**
     * 数据集合
     */
    private List<T> mDataList = new ArrayList<>();

    private final Runnable mScrollerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                int scrollerCurrY = mScroller.getCurrY();
                if (mIsCyclic) {
                    int visibleItemCount = 2 * mHalfVisibleItemCount + 1;
                    // 判断超过上下限直接令其恢复到初始坐标的值
                    while (scrollerCurrY > visibleItemCount * mItemHeight) {
                        scrollerCurrY -= mDataList.size() * mItemHeight;
                    }
                    while (scrollerCurrY < -(visibleItemCount + mDataList.size()) * mItemHeight) {
                        scrollerCurrY += mDataList.size() * mItemHeight;
                    }
                }
                mScrollOffsetY = scrollerCurrY;
                postInvalidate();
                mHandler.postDelayed(this, 16);
            }
            if (mScroller.isFinished()) {
                if (mOnWheelChangeListener == null) {
                    return;
                }
                if (mItemHeight == 0) {
                    return;
                }
                int position = -mScrollOffsetY / mItemHeight;
                if (mIsCyclic) {
                    // 当是循环状态时,根据循环效果的实现机制,mScrollOffsetY会超过list的大小,这里将position修正。
                    while (position >= mDataList.size()) {
                        position -= mDataList.size();
                    }
                    while (position < 0) {
                        position += mDataList.size();
                    }
                }
                if (mCurrentPosition != position && position != -1 && (position < mDataList.size())) {
                    mOnWheelChangeListener.onWheelSelected(mDataList.get(position), position);
                }
                mCurrentPosition = position;
            }
        }
    };

    public boolean scrollerIsFinished() {
        return mScroller.isFinished();
    }

    public WheelPicker(Context context) {
        this(context, null);
    }

    public WheelPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
        mLinearGradient = new LinearGradient(mTextColor, mSelectedItemTextColor);
        mDrawnRect = new Rect();
        mSelectedItemRect = new RectF();
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, com.ycuwq.datepicker.R.styleable.WheelPicker);
        mIndicatorPaddingLeft = ScreenUtils.dip2px(getContext(), mIndicatorPaddingLeft);
        mTextSize = a.getDimensionPixelSize(com.ycuwq.datepicker.R.styleable.WheelPicker_itemTextSize,
                getResources().getDimensionPixelSize(com.ycuwq.datepicker.R.dimen.WheelItemTextSize));
        mTextColor = a.getColor(com.ycuwq.datepicker.R.styleable.WheelPicker_itemTextColor,
                Color.BLACK);
        mIsTextGradual = a.getBoolean(com.ycuwq.datepicker.R.styleable.WheelPicker_textGradual, true);
        mIsCyclic = a.getBoolean(com.ycuwq.datepicker.R.styleable.WheelPicker_wheelCyclic, false);
        mHalfVisibleItemCount = a.getInteger(com.ycuwq.datepicker.R.styleable.WheelPicker_halfVisibleItemCount, 2);
        mItemMaximumWidthText = a.getString(com.ycuwq.datepicker.R.styleable.WheelPicker_itemMaximumWidthText);
        mSelectedItemTextColor = a.getColor(com.ycuwq.datepicker.R.styleable.WheelPicker_selectedTextColor, Color.parseColor("#33aaff"));
        mSelectedItemTextSize = a.getDimensionPixelSize(com.ycuwq.datepicker.R.styleable.WheelPicker_selectedTextSize,
                getResources().getDimensionPixelSize(com.ycuwq.datepicker.R.dimen.WheelSelectedItemTextSize));
        mCurrentPosition = a.getInteger(com.ycuwq.datepicker.R.styleable.WheelPicker_currentItemPosition, 0);
        mItemWidthSpace = a.getDimensionPixelSize(com.ycuwq.datepicker.R.styleable.WheelPicker_itemWidthSpace,
                getResources().getDimensionPixelOffset(com.ycuwq.datepicker.R.dimen.WheelItemWidthSpace));
        mItemHeightSpace = a.getDimensionPixelSize(com.ycuwq.datepicker.R.styleable.WheelPicker_itemHeightSpace,
                getResources().getDimensionPixelOffset(com.ycuwq.datepicker.R.dimen.WheelItemHeightSpace));
        mIsZoomInCenterItem = a.getBoolean(com.ycuwq.datepicker.R.styleable.WheelPicker_zoomInCenterItem, true);
        mIsShowCurtain = a.getBoolean(com.ycuwq.datepicker.R.styleable.WheelPicker_wheelCurtain, true);
        mCurtainColor = a.getColor(com.ycuwq.datepicker.R.styleable.WheelPicker_wheelCurtainColor,
                Color.parseColor("#303d3d3d"));
        mIsShowCurtainBorder = a.getBoolean(com.ycuwq.datepicker.R.styleable.WheelPicker_wheelCurtainBorder, true);
        mCurtainBorderColor = a.getColor(com.ycuwq.datepicker.R.styleable.WheelPicker_wheelCurtainBorderColor, Color.BLACK);
        mIndicatorText = a.getString(com.ycuwq.datepicker.R.styleable.WheelPicker_indicatorText);
        mIndicatorTextColor = a.getColor(com.ycuwq.datepicker.R.styleable.WheelPicker_indicatorTextColor, mSelectedItemTextColor);
        mIndicatorTextSize = a.getDimensionPixelSize(com.ycuwq.datepicker.R.styleable.WheelPicker_indicatorTextSize, mTextSize);
        Log.d(TAG, "initAttrs: mItemWidthSpace: " +  mItemWidthSpace  +  " mItemHeightSpace:" + mItemHeightSpace);
        a.recycle();
    }

    public void computeTextSize() {
        mTextMaxHeight = 0;
        if (mDataList.size() == 0) {
            return;
        }
        mPaint.setTextSize(mSelectedItemTextSize);

        if (!TextUtils.isEmpty(mItemMaximumWidthText)) {
            mTextMaxHeight = 0;
            mTextMaxWidth = (int) mPaint.measureText(mItemMaximumWidthText);
        }

        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        mTextMaxHeight = (int) (metrics.bottom - metrics.top);

        // 中间的Item边框
        int space = (-mTextMaxWidth - ScreenUtils.dip2px(getContext(), 40)) / 2;
        int left, top, right, bottom;
        if (!TextUtils.isEmpty(mIndicatorText)) {
            left = space - (int)(mIndicatorPaddingLeft / 2.0) + getPaddingLeft();
            right = getWidth() - space - (int)(mIndicatorPaddingLeft / 2.0) + getPaddingRight();
        } else {
            left = space;
            right = getWidth() - space;
        }
        top = mItemHeight * mHalfVisibleItemCount;
        bottom = mItemHeight + mItemHeight * mHalfVisibleItemCount;
        mSelectedItemRect.set(left, top, right, bottom);

        if (selectedItemGradientBg == null) {
            selectedItemGradientBg = new android.graphics.LinearGradient(
                    mSelectedItemRect.left,
                    0,
                    mSelectedItemRect.right,
                    0,
                    getResources().getColor(R.color.white, null),
                    getResources().getColor(R.color.wheel_curtain_color_one, null),
                    Shader.TileMode.CLAMP);
        }
    }

    public static String ellipsizeEnd(String str, int length) {
        if (str.length() <= length) {
            // Return the string as is if the length is less than or equal to 10
            return str;
        } else {
            // Return the substring of first 10 characters with ellipsis
            return str.substring(0, length) + "...";
        }
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mSelectedItemTextSize);
        mSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mSelectPaint.setStyle(Paint.Style.FILL);
        mSelectPaint.setTextAlign(Paint.Align.LEFT);
    }

    /**
     * 计算实际的大小
     *
     * @param specMode 测量模式
     * @param specSize 测量的大小
     * @param size     需要的大小
     * @return 返回的数值
     */
    private int measureSize(int specMode, int specSize, int size) {
        if (specMode == MeasureSpec.EXACTLY) {
            return specSize;
        } else {
            return Math.min(specSize, size);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = mTextMaxWidth + mItemWidthSpace;
        int height = (mTextMaxHeight + mItemHeightSpace) * getVisibleItemCount();

        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(measureSize(specWidthMode, specWidthSize, width),
                measureSize(specHeightMode, specHeightSize, height));
    }

    /**
     * 计算Fling极限
     * 如果为Cyclic模式则为Integer的极限值，如果正常模式，则为一整个数据集的上下限。
     */
    private void computeFlingLimitY() {
        mMinFlingY = mIsCyclic ? Integer.MIN_VALUE : - mItemHeight * (mDataList.size() - 1);
        mMaxFlingY = mIsCyclic ? Integer.MAX_VALUE : 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mDrawnRect.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        mItemHeight = mDrawnRect.height() / getVisibleItemCount();
        // 中间的Item边框
        int space = (- mTextMaxWidth - ScreenUtils.dip2px(getContext(), 40)) / 2;
        mFirstItemDrawX = mDrawnRect.left + ((space <= 0) ? 40 : space);
        int left, top, right, bottom = 0;

        if (!TextUtils.isEmpty(mIndicatorText)) {
            mFirstItemDrawX -= (mIndicatorPaddingLeft / 2);
            left = space - (mIndicatorPaddingLeft / 2) + getPaddingLeft();
            right = w - space - (mIndicatorPaddingLeft / 2) + getPaddingRight();
        } else {
            left = mDrawnRect.left;
            right = w;
        }

        top = mItemHeight * mHalfVisibleItemCount;
        bottom = mItemHeight + mItemHeight * mHalfVisibleItemCount;
        mSelectedItemRect.set(left, top, right, bottom);
        mFirstItemDrawY = (int) ((mItemHeight - (mPaint.ascent() + mPaint.descent())) / 2);

        computeFlingLimitY();
        mCenterItemDrawnY = mFirstItemDrawY + mItemHeight * mHalfVisibleItemCount;
        if (selectedItemGradientBg == null) {
            selectedItemGradientBg = new android.graphics.LinearGradient(
                    mSelectedItemRect.left,
                    0,
                    mSelectedItemRect.right,
                    0,
                    getResources().getColor(R.color.white, null),
                    getResources().getColor(R.color.wheel_curtain_color_one, null),
                    Shader.TileMode.CLAMP);
        }
        mScrollOffsetY = -mItemHeight * mCurrentPosition;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDataList == null || mDataList.isEmpty()) {
            return;
        }

        mPaint.setTextAlign(Paint.Align.LEFT);
        if (mIsShowCurtain) {
            // 作用于圆环结尾
            mSelectPaint.setStyle(Paint.Style.FILL);
            mSelectPaint.setShader(selectedItemGradientBg);
            canvas.drawRect(mSelectedItemRect, mSelectPaint);
        }
        if (mIsShowCurtainBorder) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mCurtainBorderColor);
            canvas.drawRect(mSelectedItemRect, mPaint);
            canvas.drawRect(mDrawnRect, mPaint);
        }
        int drawnSelectedPos = -mScrollOffsetY / mItemHeight;
        mPaint.setStyle(Paint.Style.FILL);
        // 首尾各多绘制一个用于缓冲
        for (int drawDataPos = drawnSelectedPos - mHalfVisibleItemCount - 1;
             drawDataPos <= drawnSelectedPos + mHalfVisibleItemCount + 1; drawDataPos++) {
            int pos = drawDataPos;
            if (mIsCyclic) {
                if (pos < 0) {
                    // 将数据集限定在0 ~ mDataList.size()-1之间
                    while (pos < 0) {
                        pos += mDataList.size();
                    }
                } else if (pos > mDataList.size() - 1) {
                    // 将数据集限定在0 ~ mDataList.size()-1之间
                    while (pos >= mDataList.size()) {
                        pos -= mDataList.size();
                    }
                }
            } else {
                if (pos < 0 || pos > mDataList.size() - 1) {
                    continue;
                }
            }
            // 在中间位置的Item作为被选中的。
            if (drawnSelectedPos == drawDataPos) {
                mPaint.setColor(mSelectedItemTextColor);
            } else {
                mPaint.setColor(mTextColor);
            }

            T item = mDataList.get(pos);
            int itemDrawY = mFirstItemDrawY + (drawDataPos + mHalfVisibleItemCount) * mItemHeight + mScrollOffsetY;
            // 距离中心的Y轴距离
            int distanceY = Math.abs(mCenterItemDrawnY - itemDrawY);
            if (mIsTextGradual) {
                float alphaRadio;
                if (itemDrawY > mCenterItemDrawnY) {
                    alphaRadio = (mDrawnRect.height() - itemDrawY) /
                            (float) (mDrawnRect.height() - (mCenterItemDrawnY));
                } else {
                    alphaRadio = itemDrawY / (float) mCenterItemDrawnY;
                }
                if (distanceY < mItemHeight) {
                    float colorRadio = 1 - (distanceY / (float) mItemHeight);
                    mPaint.setColor(mLinearGradient.getColor(colorRadio));
                } else {
                    mPaint.setColor(mTextColor);
                }
                alphaRadio = alphaRadio < 0 ? 0 : alphaRadio;
                mPaint.setAlpha((int) (alphaRadio * 255));
            } else {
                mPaint.setAlpha(255);
                mPaint.setColor(mSelectedItemTextColor);
            }
            // 开启此选项,会将越靠近中心的Item字体放大
            if (mIsZoomInCenterItem) {
                if (distanceY < mItemHeight) {
                    float addedSize = (mItemHeight - distanceY) / (float) mItemHeight * (mSelectedItemTextSize - mTextSize);
                    mPaint.setTextSize(mTextSize + addedSize);
                } else {
                    mPaint.setTextSize(mTextSize);
                }
            } else {
                mPaint.setTextSize(mTextSize);
            }

            String element = (mDataFormat != null) ? mDataFormat.format(item) : item.toString();
            String value = ellipsizeEnd(element, 11);
            canvas.drawText(value, mFirstItemDrawX, itemDrawY, mPaint);
        }

        if (!TextUtils.isEmpty(mIndicatorText)) {
            mPaint.setColor(mIndicatorTextColor);
            mPaint.setTextSize(mIndicatorTextSize);
            mPaint.setTextAlign(Paint.Align.LEFT);
            int x = mFirstItemDrawX + (int)(mTextMaxWidth / 2.0) + mIndicatorPaddingLeft;
            int y =  mCenterItemDrawnY;
            canvas.drawText(mIndicatorText, x, y, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        if (mTracker == null) {
            mTracker = VelocityTracker.obtain();
        }
        mTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    mIsAbortScroller = true;
                } else {
                    mIsAbortScroller = false;
                }
                mTracker.clear();
                mTouchDownY = mLastDownY = (int) event.getY();
                mTouchSlopFlag = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchSlopFlag && Math.abs(mTouchDownY - event.getY()) < mTouchSlop) {
                    break;
                }
                mTouchSlopFlag = false;
                float move = event.getY() - mLastDownY;
                mScrollOffsetY += move;
                mLastDownY = (int) event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsAbortScroller && mTouchDownY == mLastDownY) {
                    performClick();
                    if (event.getY() > mSelectedItemRect.bottom) {
                        int scrollItem = (int) (event.getY() - mSelectedItemRect.bottom) / mItemHeight + 1;
                        mScroller.startScroll(0, mScrollOffsetY, 0, -scrollItem * mItemHeight);
                    } else if (event.getY() < mSelectedItemRect.top) {
                        int scrollItem = (int) (mSelectedItemRect.top - event.getY()) / mItemHeight + 1;
                        mScroller.startScroll(0, mScrollOffsetY, 0, scrollItem * mItemHeight);
                    }
                } else {
                    mTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocity = (int) mTracker.getYVelocity();
                    if (Math.abs(velocity) > mMinimumVelocity) {
                        mScroller.fling(0, mScrollOffsetY, 0, velocity, 0, 0, mMinFlingY, mMaxFlingY);
                        mScroller.setFinalY(mScroller.getFinalY() + computeDistanceToEndPoint(mScroller.getFinalY() % mItemHeight));
                    } else {
                        mScroller.startScroll(0, mScrollOffsetY, 0, computeDistanceToEndPoint(mScrollOffsetY % mItemHeight));
                    }
                }
                if (!mIsCyclic) {
                    if (mScroller.getFinalY() > mMaxFlingY) {
                        mScroller.setFinalY(mMaxFlingY);
                    } else if (mScroller.getFinalY() < mMinFlingY) {
                        mScroller.setFinalY(mMinFlingY);
                    }
                }
                mHandler.post(mScrollerRunnable);
                mTracker.recycle();
                mTracker = null;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int computeDistanceToEndPoint(int remainder) {
        if (Math.abs(remainder) > mItemHeight / 2) {
            if (mScrollOffsetY < 0) {
                return -mItemHeight - remainder;
            } else {
                return mItemHeight - remainder;
            }
        } else {
            return -remainder;
        }
    }

    public void setOnWheelChangeListener(com.ycuwq.datepicker.WheelPicker.OnWheelChangeListener<T> onWheelChangeListener) {
        mOnWheelChangeListener = onWheelChangeListener;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void setDataList(@NonNull List<T> dataList) {
        mDataList = dataList;
        if (dataList.size() == 0) {
            return;
        }
        computeTextSize();
        computeFlingLimitY();
        requestLayout();
        postInvalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    /**
     * 一般列表的文本颜色
     *
     * @param textColor 文本颜色
     */
    public void setTextColor(@ColorInt int textColor) {
        if (mTextColor == textColor) {
            return;
        }
        mTextColor = textColor;
        postInvalidate();
    }

    public int getTextSize() {
        return mTextSize;
    }

    /**
     * 一般列表的文本大小
     *
     * @param textSize 文字大小
     */
    public void setTextSize(int textSize) {
        if (mTextSize == textSize) {
            return;
        }
        mTextSize = textSize;
        postInvalidate();
    }

    public int getSelectedItemTextColor() {
        return mSelectedItemTextColor;
    }

    /**
     * 设置被选中时候的文本颜色
     *
     * @param selectedItemTextColor 文本颜色
     */
    public void setSelectedItemTextColor(@ColorInt int selectedItemTextColor) {
        if (mSelectedItemTextColor == selectedItemTextColor) {
            return;
        }
        mSelectedItemTextColor = selectedItemTextColor;
        postInvalidate();
    }

    public int getSelectedItemTextSize() {
        return mSelectedItemTextSize;
    }

    /**
     * 设置被选中时候的文本大小
     *
     * @param selectedItemTextSize 文字大小
     */
    public void setSelectedItemTextSize(int selectedItemTextSize) {
        if (mSelectedItemTextSize == selectedItemTextSize) {
            return;
        }
        mSelectedItemTextSize = selectedItemTextSize;
        postInvalidate();
    }

    public String getItemMaximumWidthText() {
        return mItemMaximumWidthText;
    }

    /**
     * 设置输入的一段文字,用来测量 mTextMaxWidth
     *
     * @param itemMaximumWidthText 文本内容
     */
    public void setItemMaximumWidthText(String itemMaximumWidthText) {
        mItemMaximumWidthText = itemMaximumWidthText;
        requestLayout();
        postInvalidate();
    }

    public int getHalfVisibleItemCount() {
        return mHalfVisibleItemCount;
    }

    /**
     * 显示的个数等于上下两边Item的个数+ 中间的Item
     */
    public int getVisibleItemCount() {
        return mHalfVisibleItemCount * 2 + 1;
    }

    /**
     * 设置显示数据量的个数的一半。
     * 为保证总显示个数为奇数,这里将总数拆分,总数为 mHalfVisibleItemCount * 2 + 1
     *
     * @param halfVisibleItemCount 总数量的一半
     */
    public void setHalfVisibleItemCount(int halfVisibleItemCount) {
        if (mHalfVisibleItemCount == halfVisibleItemCount) {
            return;
        }
        mHalfVisibleItemCount = halfVisibleItemCount;
        requestLayout();
    }

    public int getItemWidthSpace() {
        return mItemWidthSpace;
    }

    public void setItemWidthSpace(int itemWidthSpace) {
        if (mItemWidthSpace == itemWidthSpace) {
            return;
        }
        mItemWidthSpace = itemWidthSpace;
        requestLayout();
    }

    public int getItemHeightSpace() {
        return mItemHeightSpace;
    }

    /**
     * 设置两个Item之间的间隔
     *
     * @param itemHeightSpace 间隔值
     */
    public void setItemHeightSpace(int itemHeightSpace) {
        if (mItemHeightSpace == itemHeightSpace) {
            return;
        }
        mItemHeightSpace = itemHeightSpace;
        requestLayout();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    /**
     * 设置当前选中的列表项,将滚动到所选位置
     *
     * @param currentPosition 设置的当前位置
     */
    public void setCurrentPosition(int currentPosition) {
        setCurrentPosition(currentPosition, true);
    }

    /**
     * 设置当前选中的列表位置
     *
     * @param currentPosition 设置的当前位置
     * @param smoothScroll    是否平滑滚动
     */
    public void setCurrentPosition(final int currentPosition, boolean smoothScroll) {
        if (mCurrentPosition == currentPosition) {
            return;
        }
        if (smoothScroll) {
            post(new Runnable() {
                @Override
                public void run() {
                    mScroller.startScroll(0, mScrollOffsetY, 0, (mCurrentPosition - currentPosition) * mItemHeight);
                    mHandler.post(mScrollerRunnable);
                }
            });
        } else {
            mCurrentPosition = currentPosition;
            mScrollOffsetY = -mItemHeight * mCurrentPosition;
            postInvalidate();
        }
    }

    public boolean isZoomInCenterItem() {
        return mIsZoomInCenterItem;
    }

    public void setZoomInCenterItem(boolean zoomInCenterItem) {
        if (mIsZoomInCenterItem == zoomInCenterItem) {
            return;
        }
        mIsZoomInCenterItem = zoomInCenterItem;
        postInvalidate();
    }

    public boolean isCyclic() {
        return mIsCyclic;
    }

    /**
     * 设置是否循环滚动。
     *
     * @param cyclic 上下边界是否相邻
     */
    public void setCyclic(boolean cyclic) {
        if (mIsCyclic == cyclic) {
            return;
        }
        mIsCyclic = cyclic;
        computeFlingLimitY();
        requestLayout();
    }

    public int getMinimumVelocity() {
        return mMinimumVelocity;
    }

    /**
     * 设置最小滚动速度,如果实际速度小于此速度,将不会触发滚动。
     *
     * @param minimumVelocity 最小速度
     */
    public void setMinimumVelocity(int minimumVelocity) {
        mMinimumVelocity = minimumVelocity;
    }

    public int getMaximumVelocity() {
        return mMaximumVelocity;
    }

    /**
     * 设置最大滚动的速度,实际滚动速度的上限
     *
     * @param maximumVelocity 最大滚动速度
     */
    public void setMaximumVelocity(int maximumVelocity) {
        mMaximumVelocity = maximumVelocity;
    }

    public boolean isTextGradual() {
        return mIsTextGradual;
    }

    /**
     * 设置文字渐变,离中心越远越淡。
     *
     * @param textGradual 是否渐变
     */
    public void setTextGradual(boolean textGradual) {
        if (mIsTextGradual == textGradual) {
            return;
        }
        mIsTextGradual = textGradual;
        postInvalidate();
    }

    public boolean isShowCurtain() {
        return mIsShowCurtain;
    }

    /**
     * 设置中心Item是否有幕布遮盖
     *
     * @param showCurtain 是否有幕布
     */
    public void setShowCurtain(boolean showCurtain) {
        if (mIsShowCurtain == showCurtain) {
            return;
        }
        mIsShowCurtain = showCurtain;
        postInvalidate();
    }

    public int getCurtainColor() {
        return mCurtainColor;
    }

    /**
     * 设置幕布颜色
     *
     * @param curtainColor 幕布颜色
     */
    public void setCurtainColor(@ColorInt int curtainColor) {
        if (mCurtainColor == curtainColor) {
            return;
        }
        mCurtainColor = curtainColor;
        postInvalidate();
    }

    public boolean isShowCurtainBorder() {
        return mIsShowCurtainBorder;
    }

    /**
     * 设置幕布是否显示边框
     *
     * @param showCurtainBorder 是否有幕布边框
     */
    public void setShowCurtainBorder(boolean showCurtainBorder) {
        if (mIsShowCurtainBorder == showCurtainBorder) {
            return;
        }
        mIsShowCurtainBorder = showCurtainBorder;
        postInvalidate();
    }

    public int getCurtainBorderColor() {
        return mCurtainBorderColor;
    }

    /**
     * 幕布边框的颜色
     *
     * @param curtainBorderColor 幕布边框颜色
     */
    public void setCurtainBorderColor(@ColorInt int curtainBorderColor) {
        if (mCurtainBorderColor == curtainBorderColor) {
            return;
        }
        mCurtainBorderColor = curtainBorderColor;
        postInvalidate();
    }

    public void setIndicatorText(String indicatorText) {
        mIndicatorText = indicatorText;
        postInvalidate();
    }

    public void setIndicatorTextColor(int indicatorTextColor) {
        mIndicatorTextColor = indicatorTextColor;
        postInvalidate();
    }

    public void setIndicatorTextSize(int indicatorTextSize) {
        mIndicatorTextSize = indicatorTextSize;
        postInvalidate();
    }

    /**
     * 设置数据集格式
     *
     * @param dataFormat 格式
     */
    public void setDataFormat(Format dataFormat) {
        mDataFormat = dataFormat;
        postInvalidate();
    }

    public Format getDataFormat() {
        return mDataFormat;
    }

    public interface OnWheelChangeListener<T> {
        void onWheelSelected(T item, int position);
    }
}