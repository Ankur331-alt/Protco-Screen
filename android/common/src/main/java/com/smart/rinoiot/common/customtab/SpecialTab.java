package com.smart.rinoiot.common.customtab;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.utils.DpUtils;

import me.majiajie.pagerbottomtabstrip.internal.RoundMessageView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;

public class SpecialTab extends BaseTabItem {
    private RelativeLayout rlItem;
    private View selectedView;
    private TextView tvTabName;
    private ImageView ivTabIcon, ivUnread;
    private RoundMessageView mMessages;

    private Drawable mDefaultDrawable;
    private Drawable mCheckedDrawable;

    private int mDefaultTextColor = 0x56000000;
    private int mCheckedTextColor = 0x56000000;

    private boolean mChecked;

    public SpecialTab(Context context) {
        this(context, null);
    }

    public SpecialTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.special_tab, this, true);
        rlItem = findViewById(R.id.rlItem);
        selectedView = findViewById(R.id.selectedView);
        tvTabName = findViewById(R.id.tvTabName);
        ivTabIcon = findViewById(R.id.ivTabIcon);
        mMessages = findViewById(R.id.messages);
        ivUnread = findViewById(R.id.ivUnread);
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        View view = getChildAt(0);
        if (view != null) {
            view.setOnClickListener(l);
        }
    }

    /**
     * 方便初始化的方法
     *
     * @param drawableRes        默认状态的图标
     * @param checkedDrawableRes 选中状态的图标
     * @param title              标题
     * @param isSpecial          true:特殊样式或者边距
     */
    public void initialize(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, String title, boolean isSpecial, int size) {
        mDefaultDrawable = ContextCompat.getDrawable(getContext(), drawableRes);
        mCheckedDrawable = ContextCompat.getDrawable(getContext(), checkedDrawableRes);
        tvTabName.setText(title);
        ivTabIcon.setImageDrawable(mDefaultDrawable);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, DpUtils.dip2px(90));
        if (isSpecial) {
            layoutParams.topMargin = (int) (DpUtils.getScreenHeight() - DpUtils.dip2px(90) * size - DpUtils.dip2px(192));
        }
        rlItem.setLayoutParams(layoutParams);
//        rlItem.setGravity(isSpecial ? Gravity.BOTTOM : Gravity.CENTER);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked) {
            tvTabName.setTextColor(mCheckedTextColor);
            selectedView.setVisibility(VISIBLE);
            ivTabIcon.setImageDrawable(mCheckedDrawable);
        } else {
            tvTabName.setTextColor(mDefaultTextColor);
            selectedView.setVisibility(GONE);
            ivTabIcon.setImageDrawable(mDefaultDrawable);
        }
        mChecked = checked;
    }

    @Override
    public void setMessageNumber(int number) {
        mMessages.setMessageNumber(number);
    }

    @Override
    public void setHasMessage(boolean hasMessage) {
        mMessages.setHasMessage(hasMessage);
        setInformationUnreadShow(hasMessage);
    }

    @Override
    public void setTitle(String title) {
        tvTabName.setText(title);
    }

    @Override
    public void setDefaultDrawable(Drawable drawable) {
        mDefaultDrawable = drawable;
        if (!mChecked) {
            ivTabIcon.setImageDrawable(drawable);
        }
    }

    @Override
    public void setSelectedDrawable(Drawable drawable) {
        mCheckedDrawable = drawable;
        if (mChecked) {
            ivTabIcon.setImageDrawable(drawable);
        }
    }

    @Override
    public String getTitle() {
        return tvTabName.getText().toString();
    }

    public void setTextDefaultColor(@ColorInt int color) {
        mDefaultTextColor = color;
    }

    public void setTextCheckedColor(@ColorInt int color) {
        mCheckedTextColor = color;
    }

    /**
     * 设置未读消息展示和隐藏
     */
    public void setInformationUnreadShow(boolean unread) {
        if (ivUnread != null) {
            ivUnread.setVisibility(!mChecked && unread ? VISIBLE : GONE);
        }
    }
}
