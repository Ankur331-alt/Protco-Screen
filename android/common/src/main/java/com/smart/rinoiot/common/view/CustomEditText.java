package com.smart.rinoiot.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smart.rinoiot.common.R;

/**
 * 自定义输入控件，封装一些逻辑处理
 *
 * @Package: com.znkit.smart.view
 * @ClassName: CustomEditText
 * @Author: xf
 * @CreateDate: 2020/6/2 5:26 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/6/2 5:26 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CustomEditText extends LinearLayout {
    private EditText editText;
    private ImageView ivClear, ivPassword;
    private OnTextChange onTextChange;

    public CustomEditText(Context context) {
        super(context);
        init(null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.setGravity(Gravity.CENTER);
        this.setOrientation(LinearLayout.HORIZONTAL);
        boolean isPassword = false;
        String hintText = "";
        if (attrs != null && getContext() != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomEditText);
            isPassword = typedArray.getBoolean(R.styleable.CustomEditText_isPassword, false);
            hintText = typedArray.getString(R.styleable.CustomEditText_hintText);
            typedArray.recycle();
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.input_layout, this);
        editText = view.findViewById(R.id.edit);

        ivClear = view.findViewById(R.id.ivClear);
        ivPassword = view.findViewById(R.id.ivPassword);
        ivPassword.setVisibility(isPassword ? VISIBLE : GONE);
        if (!TextUtils.isEmpty(hintText)) {
            editText.setHint(hintText);
        }
        if (isPassword) {
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            ivClear.setLayoutParams(params);
        }
        ivClear.setOnClickListener(v -> {
            editText.setText("");
            ivClear.setVisibility(GONE);
        });
        ivClear.setVisibility(GONE);
        if (isPassword) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        ivPassword.setOnClickListener(v -> {
            if (editText.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editText.setSelection(editText.getText().length());
                ivPassword.setImageResource(R.drawable.ic_password_off);
            } else {
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editText.setSelection(editText.getText().length());
                ivPassword.setImageResource(R.drawable.ic_password_on);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (onTextChange != null) {
                    onTextChange.onChange(s.toString());
                }
                if (TextUtils.isEmpty(s)) {
                    ivClear.setVisibility(GONE);
                } else {
                    ivClear.setVisibility(VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setInputMethod(TransformationMethod method) {
        editText.setTransformationMethod(method);
    }

    public void setInputType(int type) {
        editText.setInputType(type);
    }

    public void setOnTextChange(OnTextChange onTextChange) {
        this.onTextChange = onTextChange;
    }

    /**
     * 获取editText数据
     *
     * @return String
     * @method getText
     * @date: 2020/6/2 5:56 PM
     * @author: xf
     */
    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public void setHintText(String hintText) {
        editText.setHint(hintText);
    }

    public interface OnTextChange {
        /**
         * 文本输入监听方法回调
         *
         * @param text 输入文本
         * @return void
         * @method onChange
         * @date: 2020/6/2 5:59 PM
         * @author: xf
         */
        void onChange(String text);
    }

    public void setRequestFocues(int length) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setSelection(length);
        editText.requestFocus();
    }
    public   void  setMaxLength(int  maxLength){
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
        editText.setFilters(filters);
    }
}
