package com.smart.rinoiot.common.view;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.base.BaseTitleBarWidget;


/**
 * 自定义toolbar
 *
 * @Package: com.znkit.smart.view
 * @ClassName: TitleBar
 * @Author: xf
 * @CreateDate: 2020/5/6 2:15 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/6 2:15 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TitleBar extends BaseTitleBarWidget {
    private TextView tvRight, tvLeft;
    private TextView tvTitle;
    private ImageView ivBack;
    private ImageView ivRight;

    public TitleBar(View view) {
        super(view);
    }

    @Override
    public void init(View view) {
        tvRight = view.findViewById(R.id.tvRight);
        tvTitle = view.findViewById(R.id.tvTitle);
        ivBack = view.findViewById(R.id.ivBack);
        ivRight = view.findViewById(R.id.ivRight);
        tvLeft = view.findViewById(R.id.tvLeft);
        if (ivRight != null) {
            ivRight.setOnClickListener(v -> {
                if (toolBarListener != null) {
                    toolBarListener.onRightClick(v);
                }
            });
        }
        if (tvRight != null) {
            tvRight.setOnClickListener(v -> {
                if (toolBarListener != null) {
                    toolBarListener.onRightClick(v);
                }
            });
        }
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> {
                if (toolBarListener != null) {
                    toolBarListener.onBack(v);
                }
            });
        }
        if (tvLeft != null) {
            tvLeft.setOnClickListener(v -> {
                if (toolBarListener != null) {
                    toolBarListener.onBack(v);
                }
            });
        }
    }

    @Override
    public void setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    @Override
    public void setRightText(String rightText) {
        if (tvRight != null) {
            tvRight.setText(rightText);
            tvRight.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setRightTextColor(int rightTextColor) {
        if (tvRight != null) {
            tvRight.setTextColor(rightTextColor);
        }
    }

    @Override
    public void setRightTextSize(int rightTextSize) {
        if (tvRight != null) {
            tvRight.setTextSize(rightTextSize);
        }
    }

    @Override
    public void setRightIcon(int res) {
        if (ivRight != null) {
            ivRight.setBackgroundResource(res);
        }
    }

    @Override
    public void setBackIcon(int res) {
        if (ivBack != null) {
            ivBack.setImageResource(res);
        }
    }

    @Override
    public void setCenterTitle(boolean centerTitle) {
        if (tvTitle != null) {
            tvTitle.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void setTitleSize(float size) {
        if (tvTitle != null) {
            tvTitle.setTextSize(size);
        }
    }

    @Override
    public void setTitleColor(int color) {
        if (tvTitle != null) {
            tvTitle.setTextColor(color);
        }
    }

    @Override
    public void setRightVisible(boolean visible) {
        int vi = visible ? View.VISIBLE : View.GONE;
        if (tvRight != null) {
            tvRight.setVisibility(vi);
        }
        if (ivRight != null) {
            ivRight.setVisibility(View.GONE);
        }
    }

    @Override
    public void setRightImageVisible(boolean visible) {
        int vi = visible ? View.VISIBLE : View.GONE;
        if (tvRight != null) {
            tvRight.setVisibility(View.GONE);
        }
        if (ivRight != null) {
            ivRight.setVisibility(vi);
        }
    }

    @Override
    public void setToolBarBackground(int color) {
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    @Override
    public void setRightTextButton(String rightText) {
        if (tvRight != null) {
            tvRight.setText(rightText);
            tvRight.setBackground(null);
        }
    }

    @Override
    public void hideToolBarBack() {
        if (ivBack != null) {
            ivBack.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLeftText(String leftText) {
        if (tvLeft != null) {
            tvLeft.setVisibility(View.VISIBLE);
            tvLeft.setText(leftText);
        }
    }

    @Override
    public void setLeftTextColor(int leftTextColor) {
        if (tvLeft != null) {
            tvLeft.setTextColor(leftTextColor);
        }
    }

    @Override
    public void setLeftTextSize(int leftTextSize) {
        if (tvLeft != null) {
            tvLeft.setTextSize(leftTextSize);
        }
    }
}
