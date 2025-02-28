package com.smart.rinoiot.family.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.smart.rinoiot.common.activity.MapActivity;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.listener.DialogOnListener;
import com.smart.rinoiot.common.utils.ActivityUtils;
import com.smart.rinoiot.common.utils.DialogUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.databinding.ActivityAddFamilyBinding;
import com.smart.rinoiot.family.manager.FamilyDataChangeManager;
import com.smart.rinoiot.family.viewmodel.AddFamilyViewModel;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/8
 */
public class CreateFamilyActivity extends BaseActivity<ActivityAddFamilyBinding, AddFamilyViewModel> implements View.OnClickListener {

    public static final int REQUEST_CODE_FROM_SELECT_ADDRESS = 0x1001;

    private double lat;
    private double lng;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_family_create_family);
    }

    @Override
    public void init() {
//        setToolBarBackground(getResources().getColor(R.color.main_theme_bg));
//        StatusBarUtil.setTransparentNormalStatusBar(this, R.color.main_theme_bg);

        initView();
        initLister();
    }

    @Override
    public void onBack(View view) {
        showBackPopupView();
    }


    private void initLister() {
        binding.tvConfirm.setOnClickListener(this);
        binding.familyLocationInputView.getRoot().setOnClickListener(this);

        mViewModel.getIsCreate().observe(this, aBoolean -> {
            ToastUtil.showMsg(getString(R.string.rino_family_add_success));
            FamilyDataChangeManager.getInstance().changeViewDataSuccess();
            setResult(RESULT_OK);
            finishThis();
        });

        binding.familyNameInputView.tvSubTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvConfirm.setEnabled(!TextUtils.isEmpty(charSequence));
                binding.tvConfirm.setSelected(!TextUtils.isEmpty(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.familyNameInputView.ivClear.setOnClickListener(v -> binding.familyNameInputView.tvSubTitle.setText(""));
    }

    /**
     * ui
     */
    private void initView() {
        binding.tvConfirm.setSelected(false);
        binding.tvConfirm.setEnabled(false);

        binding.familyNameInputView.tvTitle.setText(R.string.rino_family_name);
        binding.familyLocationInputView.tvTitle.setText(R.string.rino_family_location);

        binding.familyNameInputView.tvSubTitle.setHint(R.string.rino_family_input_name_hint);

        binding.familyLocationInputView.tvSubTitle.setHint(R.string.rino_family_input_location_hint);

    }

    @Override
    public ActivityAddFamilyBinding getBinding(LayoutInflater inflater) {
        return ActivityAddFamilyBinding.inflate(inflater);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvConfirm) {
            String familyName = binding.familyNameInputView.tvSubTitle.getText().toString();
            if (TextUtils.isEmpty(familyName) || familyName.equals(getString(R.string.rino_family_input_name))) {
                ToastUtil.showErrorMsg(R.string.rino_family_input_name);
                return;
            }
            String address = binding.familyLocationInputView.tvSubTitle.getText().toString();
            mViewModel.createFamily(familyName, address, lat, lng);
        } else if (view.getId() == R.id.familyLocationInputView) {
            ActivityUtils.startActivityForResult(CreateFamilyActivity.this, null, MapActivity.class, REQUEST_CODE_FROM_SELECT_ADDRESS);
        }
    }

    /**
     * 返回
     */
    private void showBackPopupView() {
        DialogUtil.showNormalMsg(this, "", getString(R.string.rino_family_exit_title), new DialogOnListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE_FROM_SELECT_ADDRESS) {
            String address = data.getStringExtra("address");
            lat = data.getDoubleExtra("latitude", 0);
            lng = data.getDoubleExtra("longitude", 0);
            binding.familyLocationInputView.tvSubTitle.setText(address);
        }
    }
}
