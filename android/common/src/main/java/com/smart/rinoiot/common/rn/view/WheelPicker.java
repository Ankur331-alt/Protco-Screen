//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.smart.rinoiot.common.rn.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.utils.LgUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WheelPicker extends View implements IDebug, IWheelPicker, Runnable {
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SCROLLING = 2;
    public static final int ALIGN_CENTER = 0;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    private static final String TAG = WheelPicker.class.getSimpleName();
    private final Handler mHandler;
    private Paint mPaint;
    private Scroller mScroller;
    private VelocityTracker mTracker;
    private boolean isTouchTriggered;
    private OnItemSelectedListener mOnItemSelectedListener;
    private OnWheelChangeListener mOnWheelChangeListener;
    private Rect mRectDrawn;
    private Rect mRectIndicatorHead;
    private Rect mRectIndicatorFoot;
    private Rect mRectCurrentItem;
    private Camera mCamera;
    private Matrix mMatrixRotate;
    private Matrix mMatrixDepth;
    private List mLabelData;
    private String mMaxWidthText;
    private int mVisibleItemCount;
    private int mDrawnItemCount;
    private int mHalfDrawnItemCount;
    private int mTextMaxWidth;
    private int mTextMaxHeight;
    private int mItemTextColor;
    private int mSelectedItemTextColor;
    private int mItemTextSize;
    private int mIndicatorSize;
    private int mIndicatorColor;
    private int mCurtainColor;
    private int mItemSpace;
    private int mItemAlign;
    private int mItemHeight;
    private int mHalfItemHeight;
    private int mHalfWheelHeight;
    private int mSelectedItemPosition;
    private int mCurrentItemPosition;
    private int mMinFlingY;
    private int mMaxFlingY;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mWheelCenterX;
    private int mWheelCenterY;
    private int mDrawnCenterX;
    private int mDrawnCenterY;
    private int mScrollOffsetY;
    private int mTextMaxWidthPosition;
    private int mLastPointY;
    private int mDownPointY;
    private int mTouchSlop;
    private boolean hasSameWidth;
    private boolean hasIndicator;
    private boolean hasCurtain;
    private boolean hasAtmospheric;
    private boolean isCyclic;
    private boolean isCurved;
    private boolean isClick;
    private boolean isForceFinishScroll;
    private String fontPath;
    private boolean isDebug;

    public WheelPicker(Context context) {
        this(context, (AttributeSet) null);
    }

    public WheelPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHandler = new Handler();
        this.mMinimumVelocity = 50;
        this.mMaximumVelocity = 3000;
        this.mTouchSlop = 10;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PanelWheelPicker);
        int idData = a.getResourceId(R.styleable.PanelWheelPicker_wheel_data, 0);
        this.mLabelData = Arrays.asList(this.getResources().getStringArray(idData == 0 ? R.array.PanelWheelArrayDefault : idData));
        this.mItemTextSize = a.getDimensionPixelSize(R.styleable.PanelWheelPicker_wheel_item_text_size, this.getResources().getDimensionPixelSize(R.dimen.spp_24));
        this.mVisibleItemCount = a.getInt(R.styleable.PanelWheelPicker_wheel_visible_item_count, 7);
        this.mSelectedItemPosition = a.getInt(R.styleable.PanelWheelPicker_wheel_selected_item_position, 0);
        this.hasSameWidth = a.getBoolean(R.styleable.PanelWheelPicker_wheel_same_width, false);
        this.mTextMaxWidthPosition = a.getInt(R.styleable.PanelWheelPicker_wheel_maximum_width_text_position, -1);
        this.mMaxWidthText = a.getString(R.styleable.PanelWheelPicker_wheel_maximum_width_text);
        this.mSelectedItemTextColor = a.getColor(R.styleable.PanelWheelPicker_wheel_selected_item_text_color, -1);
        this.mItemTextColor = a.getColor(R.styleable.PanelWheelPicker_wheel_item_text_color, getResources().getColor(R.color.titleColor));
        this.mItemSpace = a.getDimensionPixelSize(R.styleable.PanelWheelPicker_wheel_item_space, this.getResources().getDimensionPixelSize(R.dimen.dpp_24));
        this.isCyclic = a.getBoolean(R.styleable.PanelWheelPicker_wheel_cyclic, false);
        this.hasIndicator = a.getBoolean(R.styleable.PanelWheelPicker_wheel_indicator, false);
        this.mIndicatorColor = a.getColor(R.styleable.PanelWheelPicker_wheel_indicator_color, getResources().getColor(R.color.main_theme_color));
        this.mIndicatorSize = a.getDimensionPixelSize(R.styleable.PanelWheelPicker_wheel_indicator_size, this.getResources().getDimensionPixelSize(R.dimen.dpp_4));
        this.hasCurtain = a.getBoolean(R.styleable.PanelWheelPicker_wheel_curtain, false);
        this.mCurtainColor = a.getColor(R.styleable.PanelWheelPicker_wheel_curtain_color, getResources().getColor(R.color.white));
        this.hasAtmospheric = a.getBoolean(R.styleable.PanelWheelPicker_wheel_atmospheric, false);
        this.isCurved = a.getBoolean(R.styleable.PanelWheelPicker_wheel_curved, false);
        this.mItemAlign = a.getInt(R.styleable.PanelWheelPicker_wheel_item_align, 0);
        this.fontPath = a.getString(R.styleable.PanelWheelPicker_wheel_font_path);
        a.recycle();
        this.updateVisibleItemCount();
        this.mPaint = new Paint(69);
        this.mPaint.setTextSize((float) this.mItemTextSize);
        if (this.fontPath != null) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), this.fontPath);
            this.setTypeface(typeface);
        }

        this.updateItemTextAlign();
        this.computeTextSize();
        this.mScroller = new Scroller(this.getContext());
        if (VERSION.SDK_INT >= 4) {
            ViewConfiguration conf = ViewConfiguration.get(this.getContext());
            this.mMinimumVelocity = conf.getScaledMinimumFlingVelocity();
            this.mMaximumVelocity = conf.getScaledMaximumFlingVelocity();
            this.mTouchSlop = conf.getScaledTouchSlop();
        }

        this.mRectDrawn = new Rect();
        this.mRectIndicatorHead = new Rect();
        this.mRectIndicatorFoot = new Rect();
        this.mRectCurrentItem = new Rect();
        this.mCamera = new Camera();
        this.mMatrixRotate = new Matrix();
        this.mMatrixDepth = new Matrix();
    }

    private void updateVisibleItemCount() {
        if (this.mVisibleItemCount < 2) {
            throw new ArithmeticException("Wheel's visible item count can not be less than 2!");
        } else {
            if (this.mVisibleItemCount % 2 == 0) {
                ++this.mVisibleItemCount;
            }

            this.mDrawnItemCount = this.mVisibleItemCount + 2;
            this.mHalfDrawnItemCount = this.mDrawnItemCount / 2;
        }
    }

    private void computeTextSize() {
        this.mTextMaxWidth = this.mTextMaxHeight = 0;
        if (this.hasSameWidth) {
            this.mTextMaxWidth = (int) this.mPaint.measureText(String.valueOf(this.mLabelData.get(0)));
        } else if (this.isPosInRang(this.mTextMaxWidthPosition)) {
            this.mTextMaxWidth = (int) this.mPaint.measureText(String.valueOf(this.mLabelData.get(this.mTextMaxWidthPosition)));
        } else {
            int width;
            if (!TextUtils.isEmpty(this.mMaxWidthText)) {
                this.mTextMaxWidth = (int) this.mPaint.measureText(this.mMaxWidthText);
            } else {
                for (Iterator var1 = this.mLabelData.iterator(); var1.hasNext(); this.mTextMaxWidth = Math.max(this.mTextMaxWidth, width)) {
                    Object obj = var1.next();
                    String text = String.valueOf(obj);
                    width = (int) this.mPaint.measureText(text);
                }
            }
        }

        FontMetrics metrics = this.mPaint.getFontMetrics();
        this.mTextMaxHeight = (int) (metrics.bottom - metrics.top);
    }

    private void updateItemTextAlign() {
        switch (this.mItemAlign) {
            case 1:
                this.mPaint.setTextAlign(Align.LEFT);
                break;
            case 2:
                this.mPaint.setTextAlign(Align.RIGHT);
                break;
            default:
                this.mPaint.setTextAlign(Align.CENTER);
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int resultWidth = this.mTextMaxWidth;
        int resultHeight = this.mTextMaxHeight * this.mVisibleItemCount + this.mItemSpace * (this.mVisibleItemCount - 1);
        if (this.isCurved) {
            resultHeight = (int) ((double) (2 * resultHeight) / 3.141592653589793D);
        }

        if (this.isDebug) {
            LgUtils.i(TAG + " ---> Wheel's content size is (" + resultWidth + ":" + resultHeight + ")");
        }

        resultWidth += this.getPaddingLeft() + this.getPaddingRight();
        resultHeight += this.getPaddingTop() + this.getPaddingBottom();
        if (this.isDebug) {
            LgUtils.i(TAG + " ---> Wheel's size is (" + resultWidth + ":" + resultHeight + ")");
        }

        resultWidth = this.measureSize(modeWidth, sizeWidth, resultWidth);
        resultHeight = this.measureSize(modeHeight, sizeHeight, resultHeight);
        this.setMeasuredDimension(resultWidth, resultHeight);
    }

    private int measureSize(int mode, int sizeExpect, int sizeActual) {
        int realSize;
        if (mode == 1073741824) {
            realSize = sizeExpect;
        } else {
            realSize = sizeActual;
            if (mode == -2147483648) {
                realSize = Math.min(sizeActual, sizeExpect);
            }
        }

        return realSize;
    }

    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        this.mRectDrawn.set(this.getPaddingLeft(), this.getPaddingTop(), this.getWidth() - this.getPaddingRight(), this.getHeight() - this.getPaddingBottom());
        if (this.isDebug) {
            LgUtils.i(TAG + " ---> Wheel's drawn rect size is (" + this.mRectDrawn.width() + ":" + this.mRectDrawn.height() + ") and location is (" + this.mRectDrawn.left + ":" + this.mRectDrawn.top + ")");
        }

        this.mWheelCenterX = this.mRectDrawn.centerX();
        this.mWheelCenterY = this.mRectDrawn.centerY();
        this.computeDrawnCenter();
        this.mHalfWheelHeight = this.mRectDrawn.height() / 2;
        this.mItemHeight = this.mRectDrawn.height() / this.mVisibleItemCount;
        this.mHalfItemHeight = this.mItemHeight / 2;
        this.computeFlingLimitY();
        this.computeIndicatorRect();
        this.computeCurrentItemRect();
    }

    private void computeDrawnCenter() {
        switch (this.mItemAlign) {
            case 1:
                this.mDrawnCenterX = this.mRectDrawn.left;
                break;
            case 2:
                this.mDrawnCenterX = this.mRectDrawn.right;
                break;
            default:
                this.mDrawnCenterX = this.mWheelCenterX;
        }

        this.mDrawnCenterY = (int) ((float) this.mWheelCenterY - (this.mPaint.ascent() + this.mPaint.descent()) / 2.0F);
    }

    private void computeFlingLimitY() {
        int currentItemOffset = this.mSelectedItemPosition * this.mItemHeight;
        this.mMinFlingY = this.isCyclic ? -2147483648 : -this.mItemHeight * (this.mLabelData.size() - 1) + currentItemOffset;
        this.mMaxFlingY = this.isCyclic ? 2147483647 : currentItemOffset;
    }

    private void computeIndicatorRect() {
        if (this.hasIndicator) {
            int halfIndicatorSize = this.mIndicatorSize / 2;
            int indicatorHeadCenterY = this.mWheelCenterY + this.mHalfItemHeight;
            int indicatorFootCenterY = this.mWheelCenterY - this.mHalfItemHeight;
            this.mRectIndicatorHead.set(this.mRectDrawn.left, indicatorHeadCenterY - halfIndicatorSize, this.mRectDrawn.right, indicatorHeadCenterY + halfIndicatorSize);
            this.mRectIndicatorFoot.set(this.mRectDrawn.left, indicatorFootCenterY - halfIndicatorSize, this.mRectDrawn.right, indicatorFootCenterY + halfIndicatorSize);
        }
    }

    private void computeCurrentItemRect() {
        if (this.hasCurtain || this.mSelectedItemTextColor != -1) {
            this.mRectCurrentItem.set(this.mRectDrawn.left, this.mWheelCenterY - this.mHalfItemHeight, this.mRectDrawn.right, this.mWheelCenterY + this.mHalfItemHeight);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (null != this.mOnWheelChangeListener) {
            this.mOnWheelChangeListener.onWheelScrolled(this.mScrollOffsetY);
        }

        int drawnDataStartPos = -this.mScrollOffsetY / this.mItemHeight - this.mHalfDrawnItemCount;
        int drawnDataPos = drawnDataStartPos + this.mSelectedItemPosition;

        for (int drawnOffsetPos = -this.mHalfDrawnItemCount; drawnDataPos < drawnDataStartPos + this.mSelectedItemPosition + this.mDrawnItemCount; ++drawnOffsetPos) {
            String data = "";
            int mDrawnItemCenterY;
            if (this.isCyclic) {
                mDrawnItemCenterY = drawnDataPos % this.mLabelData.size();
                mDrawnItemCenterY = mDrawnItemCenterY < 0 ? mDrawnItemCenterY + this.mLabelData.size() : mDrawnItemCenterY;
                data = String.valueOf(this.mLabelData.get(mDrawnItemCenterY));
            } else if (this.isPosInRang(drawnDataPos)) {
                data = String.valueOf(this.mLabelData.get(drawnDataPos));
            }

            this.mPaint.setColor(this.mItemTextColor);
            this.mPaint.setStyle(Style.FILL);
            mDrawnItemCenterY = this.mDrawnCenterY + drawnOffsetPos * this.mItemHeight + this.mScrollOffsetY % this.mItemHeight;
            int distanceToCenter = 0;
            if (this.isCurved) {
                float ratio = (float) (this.mDrawnCenterY - Math.abs(this.mDrawnCenterY - mDrawnItemCenterY) - this.mRectDrawn.top) * 1.0F / (float) (this.mDrawnCenterY - this.mRectDrawn.top);
                int unit = 0;
                if (mDrawnItemCenterY > this.mDrawnCenterY) {
                    unit = 1;
                } else if (mDrawnItemCenterY < this.mDrawnCenterY) {
                    unit = -1;
                }

                float degree = -(1.0F - ratio) * 90.0F * (float) unit;
                if (degree < -90.0F) {
                    degree = -90.0F;
                }

                if (degree > 90.0F) {
                    degree = 90.0F;
                }

                distanceToCenter = this.computeSpace((int) degree);
                int transX = this.mWheelCenterX;
                switch (this.mItemAlign) {
                    case 1:
                        transX = this.mRectDrawn.left;
                        break;
                    case 2:
                        transX = this.mRectDrawn.right;
                }

                int transY = this.mWheelCenterY - distanceToCenter;
                this.mCamera.save();
                this.mCamera.rotateX(degree);
                this.mCamera.getMatrix(this.mMatrixRotate);
                this.mCamera.restore();
                this.mMatrixRotate.preTranslate((float) (-transX), (float) (-transY));
                this.mMatrixRotate.postTranslate((float) transX, (float) transY);
                this.mCamera.save();
                this.mCamera.translate(0.0F, 0.0F, (float) this.computeDepth((int) degree));
                this.mCamera.getMatrix(this.mMatrixDepth);
                this.mCamera.restore();
                this.mMatrixDepth.preTranslate((float) (-transX), (float) (-transY));
                this.mMatrixDepth.postTranslate((float) transX, (float) transY);
                this.mMatrixRotate.postConcat(this.mMatrixDepth);
            }

            int drawnCenterY;
            if (this.hasAtmospheric) {
                drawnCenterY = (int) ((float) (this.mDrawnCenterY - Math.abs(this.mDrawnCenterY - mDrawnItemCenterY)) * 1.0F / (float) this.mDrawnCenterY * 255.0F);
                drawnCenterY = drawnCenterY < 0 ? 0 : drawnCenterY;
                this.mPaint.setAlpha(drawnCenterY);
            }

            drawnCenterY = this.isCurved ? this.mDrawnCenterY - distanceToCenter : mDrawnItemCenterY;
            if (this.mSelectedItemTextColor != -1) {
                canvas.save();
                if (this.isCurved) {
                    canvas.concat(this.mMatrixRotate);
                }

                canvas.clipRect(this.mRectCurrentItem, Op.DIFFERENCE);
                canvas.drawText(data, (float) this.mDrawnCenterX, (float) drawnCenterY, this.mPaint);
                canvas.restore();
                this.mPaint.setColor(this.mSelectedItemTextColor);
                canvas.save();
                if (this.isCurved) {
                    canvas.concat(this.mMatrixRotate);
                }

                canvas.clipRect(this.mRectCurrentItem);
                canvas.drawText(data, (float) this.mDrawnCenterX, (float) drawnCenterY, this.mPaint);
                canvas.restore();
            } else {
                canvas.save();
                canvas.clipRect(this.mRectDrawn);
                if (this.isCurved) {
                    canvas.concat(this.mMatrixRotate);
                }

                canvas.drawText(data, (float) this.mDrawnCenterX, (float) drawnCenterY, this.mPaint);
                canvas.restore();
            }

            if (this.isDebug) {
                canvas.save();
                canvas.clipRect(this.mRectDrawn);
                this.mPaint.setColor(-1166541);
                int lineCenterY = this.mWheelCenterY + drawnOffsetPos * this.mItemHeight;
                canvas.drawLine((float) this.mRectDrawn.left, (float) lineCenterY, (float) this.mRectDrawn.right, (float) lineCenterY, this.mPaint);
                this.mPaint.setColor(-13421586);
                this.mPaint.setStyle(Style.STROKE);
                int top = lineCenterY - this.mHalfItemHeight;
                canvas.drawRect((float) this.mRectDrawn.left, (float) top, (float) this.mRectDrawn.right, (float) (top + this.mItemHeight), this.mPaint);
                canvas.restore();
            }

            ++drawnDataPos;
        }

        if (this.hasCurtain) {
            this.mPaint.setColor(this.mCurtainColor);
            this.mPaint.setStyle(Style.FILL);
            canvas.drawRect(this.mRectCurrentItem, this.mPaint);
        }

        if (this.hasIndicator) {
            this.mPaint.setColor(this.mIndicatorColor);
            this.mPaint.setStyle(Style.FILL);
            canvas.drawRect(this.mRectIndicatorHead, this.mPaint);
            canvas.drawRect(this.mRectIndicatorFoot, this.mPaint);
        }

        if (this.isDebug) {
            this.mPaint.setColor(1144254003);
            this.mPaint.setStyle(Style.FILL);
            canvas.drawRect(0.0F, 0.0F, (float) this.getPaddingLeft(), (float) this.getHeight(), this.mPaint);
            canvas.drawRect(0.0F, 0.0F, (float) this.getWidth(), (float) this.getPaddingTop(), this.mPaint);
            canvas.drawRect((float) (this.getWidth() - this.getPaddingRight()), 0.0F, (float) this.getWidth(), (float) this.getHeight(), this.mPaint);
            canvas.drawRect(0.0F, (float) (this.getHeight() - this.getPaddingBottom()), (float) this.getWidth(), (float) this.getHeight(), this.mPaint);
        }

    }

    private boolean isPosInRang(int position) {
        return position >= 0 && position < this.mLabelData.size();
    }

    private int computeSpace(int degree) {
        return (int) (Math.sin(Math.toRadians((double) degree)) * (double) this.mHalfWheelHeight);
    }

    private int computeDepth(int degree) {
        return (int) ((double) this.mHalfWheelHeight - Math.cos(Math.toRadians((double) degree)) * (double) this.mHalfWheelHeight);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.isClick = true;
                this.isTouchTriggered = true;
                if (null != this.getParent()) {
                    this.getParent().requestDisallowInterceptTouchEvent(true);
                }

                if (null == this.mTracker) {
                    this.mTracker = VelocityTracker.obtain();
                } else {
                    this.mTracker.clear();
                }

                this.mTracker.addMovement(event);
                if (!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                    this.isForceFinishScroll = true;
                }

                this.mDownPointY = this.mLastPointY = (int) event.getY();
                break;
            case 1:
                if (null != this.getParent()) {
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                }

                int velocity;
                if (this.isClick && !this.isForceFinishScroll) {
                    velocity = this.mWheelCenterY - this.mHalfItemHeight;
                    float offsetPx = event.getY() - (float) velocity;
                    int offset = (int) Math.floor((double) (offsetPx / (float) this.mItemHeight));
                    if (this.isDebug) {
                        LgUtils.i(TAG + " ---> Got click with dY (" + offsetPx + ") offset " + offset + ", adding to " + this.mCurrentItemPosition);
                    }

                    if (offset != 0) {
                        if (this.mCurrentItemPosition + offset < 0) {
                            this.setSelectedItemPosition(0);
                        } else if (this.mCurrentItemPosition + offset > this.mLabelData.size() - 1) {
                            this.setSelectedItemPosition(this.mLabelData.size() - 1);
                        } else {
                            this.setSelectedItemPosition(this.mCurrentItemPosition + offset);
                        }
                    }
                } else {
                    this.mTracker.addMovement(event);
                    if (VERSION.SDK_INT >= 4) {
                        this.mTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                    } else {
                        this.mTracker.computeCurrentVelocity(1000);
                    }

                    this.isForceFinishScroll = false;
                    velocity = (int) this.mTracker.getYVelocity();
                    if (Math.abs(velocity) > this.mMinimumVelocity) {
                        this.mScroller.fling(0, this.mScrollOffsetY, 0, velocity, 0, 0, this.mMinFlingY, this.mMaxFlingY);
                        this.mScroller.setFinalY(this.mScroller.getFinalY() + this.computeDistanceToEndPoint(this.mScroller.getFinalY() % this.mItemHeight));
                    } else {
                        this.mScroller.startScroll(0, this.mScrollOffsetY, 0, this.computeDistanceToEndPoint(this.mScrollOffsetY % this.mItemHeight));
                    }

                    if (!this.isCyclic) {
                        if (this.mScroller.getFinalY() > this.mMaxFlingY) {
                            this.mScroller.setFinalY(this.mMaxFlingY);
                        } else if (this.mScroller.getFinalY() < this.mMinFlingY) {
                            this.mScroller.setFinalY(this.mMinFlingY);
                        }
                    }

                    this.mHandler.post(this);
                    if (null != this.mTracker) {
                        this.mTracker.recycle();
                        this.mTracker = null;
                    }
                }
                break;
            case 2:
                if (Math.abs((float) this.mDownPointY - event.getY()) < (float) this.mTouchSlop) {
                    this.isClick = true;
                } else {
                    this.isClick = false;
                    this.mTracker.addMovement(event);
                    if (null != this.mOnWheelChangeListener) {
                        this.mOnWheelChangeListener.onWheelScrollStateChanged(1);
                    }

                    float move = event.getY() - (float) this.mLastPointY;
                    if (Math.abs(move) >= 1.0F) {
                        this.mScrollOffsetY = (int) ((float) this.mScrollOffsetY + move);
                        this.mLastPointY = (int) event.getY();
                        this.invalidate();
                    }
                }
                break;
            case 3:
                if (null != this.getParent()) {
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                }

                if (null != this.mTracker) {
                    this.mTracker.recycle();
                    this.mTracker = null;
                }
        }

        return true;
    }

    private int computeDistanceToEndPoint(int remainder) {
        if (Math.abs(remainder) > this.mHalfItemHeight) {
            return this.mScrollOffsetY < 0 ? -this.mItemHeight - remainder : this.mItemHeight - remainder;
        } else {
            return -remainder;
        }
    }

    public void run() {
        if (null != this.mLabelData && this.mLabelData.size() != 0) {
            if (this.mScroller.isFinished() && !this.isForceFinishScroll) {
                if (this.mItemHeight == 0) {
                    return;
                }

                int position = (-this.mScrollOffsetY / this.mItemHeight + this.mSelectedItemPosition) % this.mLabelData.size();
                position = position < 0 ? position + this.mLabelData.size() : position;
                if (this.isDebug) {
                    LgUtils.i(TAG + " ---> " + position + ":" + this.mLabelData.get(position) + ":" + this.mScrollOffsetY);
                }

                this.mCurrentItemPosition = position;
                if (null != this.mOnItemSelectedListener && this.isTouchTriggered) {
                    this.mOnItemSelectedListener.onItemSelected(this, this.mLabelData.get(position), position);
                }

                if (null != this.mOnWheelChangeListener && this.isTouchTriggered) {
                    this.mOnWheelChangeListener.onWheelSelected(position);
                    this.mOnWheelChangeListener.onWheelScrollStateChanged(0);
                }
            }

            if (this.mScroller.computeScrollOffset()) {
                if (null != this.mOnWheelChangeListener) {
                    this.mOnWheelChangeListener.onWheelScrollStateChanged(2);
                }

                this.mScrollOffsetY = this.mScroller.getCurrY();
                this.postInvalidate();
                this.mHandler.postDelayed(this, 16L);
            }

        }
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public int getVisibleItemCount() {
        return this.mVisibleItemCount;
    }

    public void setVisibleItemCount(int count) {
        this.mVisibleItemCount = count;
        this.updateVisibleItemCount();
        this.requestLayout();
    }

    public boolean isCyclic() {
        return this.isCyclic;
    }

    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
        this.computeFlingLimitY();
        this.invalidate();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    public int getSelectedItemPosition() {
        return this.mSelectedItemPosition;
    }

    public void setSelectedItemPosition(int position) {
        this.setSelectedItemPosition(position, true);
    }

    public void setSelectedItemPosition(int position, boolean animated) {
        this.isTouchTriggered = false;
        boolean isComponentDrawn = this.mItemHeight > 0;
        if (animated && isComponentDrawn && this.mScroller.isFinished()) {
            if (this.isClick) {
                this.isTouchTriggered = true;
            }

            int length = this.getData().size();
            int itemDifference = position - this.mCurrentItemPosition;
            if (itemDifference == 0) {
                return;
            }

            if (this.isCyclic && Math.abs(itemDifference) > length / 2) {
                itemDifference += itemDifference > 0 ? -length : length;
            }

            this.mScroller.startScroll(0, this.mScrollOffsetY, 0, -itemDifference * this.mItemHeight);
            this.mHandler.post(this);
        } else {
            if (!this.mScroller.isFinished()) {
                this.mScroller.abortAnimation();
            }

            position = Math.min(position, this.mLabelData.size() - 1);
            position = Math.max(position, 0);
            this.mSelectedItemPosition = position;
            this.mCurrentItemPosition = position;
            this.mScrollOffsetY = 0;
            this.computeFlingLimitY();
            this.requestLayout();
            this.invalidate();
        }

    }

    public int getCurrentItemPosition() {
        return this.mCurrentItemPosition;
    }

    public List getData() {
        return this.mLabelData;
    }

    public void setData(List data) {
        if (null == data) {
            throw new NullPointerException("WheelPicker's data can not be null!");
        } else {
            this.mLabelData = data;
            if (this.mSelectedItemPosition <= data.size() - 1 && this.mCurrentItemPosition <= data.size() - 1) {
                this.mSelectedItemPosition = this.mCurrentItemPosition;
            } else {
                this.mSelectedItemPosition = this.mCurrentItemPosition = data.size() - 1;
            }

            this.mScrollOffsetY = 0;
            this.computeTextSize();
            this.computeFlingLimitY();
            this.requestLayout();
            this.invalidate();
        }
    }

    public void setSameWidth(boolean hasSameWidth) {
        this.hasSameWidth = hasSameWidth;
        this.computeTextSize();
        this.requestLayout();
        this.invalidate();
    }

    public boolean hasSameWidth() {
        return this.hasSameWidth;
    }

    public void setOnWheelChangeListener(OnWheelChangeListener listener) {
        this.mOnWheelChangeListener = listener;
    }

    public String getMaximumWidthText() {
        return this.mMaxWidthText;
    }

    public void setMaximumWidthText(String text) {
        if (null == text) {
            throw new NullPointerException("Maximum width text can not be null!");
        } else {
            this.mMaxWidthText = text;
            this.computeTextSize();
            this.requestLayout();
            this.invalidate();
        }
    }

    public int getMaximumWidthTextPosition() {
        return this.mTextMaxWidthPosition;
    }

    public void setMaximumWidthTextPosition(int position) {
        if (!this.isPosInRang(position)) {
            throw new ArrayIndexOutOfBoundsException("Maximum width text Position must in [0, " + this.mLabelData.size() + "), but current is " + position);
        } else {
            this.mTextMaxWidthPosition = position;
            this.computeTextSize();
            this.requestLayout();
            this.invalidate();
        }
    }

    public int getSelectedItemTextColor() {
        return this.mSelectedItemTextColor;
    }

    public void setSelectedItemTextColor(int color) {
        this.mSelectedItemTextColor = color;
        this.computeCurrentItemRect();
        this.invalidate();
    }

    public int getItemTextColor() {
        return this.mItemTextColor;
    }

    public void setItemTextColor(int color) {
        this.mItemTextColor = color;
        this.invalidate();
    }

    public int getItemTextSize() {
        return this.mItemTextSize;
    }

    public void setItemTextSize(int size) {
        this.mItemTextSize = size;
        this.mPaint.setTextSize((float) this.mItemTextSize);
        this.computeTextSize();
        this.requestLayout();
        this.invalidate();
    }

    public int getItemSpace() {
        return this.mItemSpace;
    }

    public void setItemSpace(int space) {
        this.mItemSpace = space;
        this.requestLayout();
        this.invalidate();
    }

    public void setIndicator(boolean hasIndicator) {
        this.hasIndicator = hasIndicator;
        this.computeIndicatorRect();
        this.invalidate();
    }

    public boolean hasIndicator() {
        return this.hasIndicator;
    }

    public int getIndicatorSize() {
        return this.mIndicatorSize;
    }

    public void setIndicatorSize(int size) {
        this.mIndicatorSize = size;
        this.computeIndicatorRect();
        this.invalidate();
    }

    public int getIndicatorColor() {
        return this.mIndicatorColor;
    }

    public void setIndicatorColor(int color) {
        this.mIndicatorColor = color;
        this.invalidate();
    }

    public void setCurtain(boolean hasCurtain) {
        this.hasCurtain = hasCurtain;
        this.computeCurrentItemRect();
        this.invalidate();
    }

    public boolean hasCurtain() {
        return this.hasCurtain;
    }

    public int getCurtainColor() {
        return this.mCurtainColor;
    }

    public void setCurtainColor(int color) {
        this.mCurtainColor = color;
        this.invalidate();
    }

    public void setAtmospheric(boolean hasAtmospheric) {
        this.hasAtmospheric = hasAtmospheric;
        this.invalidate();
    }

    public boolean hasAtmospheric() {
        return this.hasAtmospheric;
    }

    public boolean isCurved() {
        return this.isCurved;
    }

    public void setCurved(boolean isCurved) {
        this.isCurved = isCurved;
        this.requestLayout();
        this.invalidate();
    }

    public int getItemAlign() {
        return this.mItemAlign;
    }

    public void setItemAlign(int align) {
        this.mItemAlign = align;
        this.updateItemTextAlign();
        this.computeDrawnCenter();
        this.invalidate();
    }

    public Typeface getTypeface() {
        return null != this.mPaint ? this.mPaint.getTypeface() : null;
    }

    public void setTypeface(Typeface tf) {
        if (null != this.mPaint) {
            this.mPaint.setTypeface(tf);
        }

        this.computeTextSize();
        this.requestLayout();
        this.invalidate();
    }

    public interface OnWheelChangeListener {
        void onWheelScrolled(int var1);

        void onWheelSelected(int var1);

        void onWheelScrollStateChanged(int var1);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(WheelPicker var1, Object var2, int var3);
    }
}
