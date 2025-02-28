package com.smart.rinoiot.family.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.tag.FlowTagLayout;
import com.smart.rinoiot.common.tag.TagAdapter;
import com.smart.rinoiot.common.tag.TagBaseBean;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.databinding.ActivityCreateRoomBinding;
import com.smart.rinoiot.family.manager.FamilyDataChangeManager;
import com.smart.rinoiot.family.manager.HomeDataManager;
import com.smart.rinoiot.family.viewmodel.CreateRoomViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/8
 */
public class CreateRoomActivity extends BaseActivity<ActivityCreateRoomBinding, CreateRoomViewModel> {
    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_family_add_room_title);
    }

    @Override
    public void init() {
        initView();
        initLister();
    }

    /**
     * listener
     */
    private void initLister() {
        binding.inputRoomNameView.tvSubTitle.addTextChangedListener(new TextWatcher() {
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

        binding.tvConfirm.setOnClickListener(view -> {
            String roomName = binding.inputRoomNameView.tvSubTitle.getText().toString();
            if (!TextUtils.isEmpty(roomName)) {
                mViewModel.createRoom(roomName, HomeDataManager.getInstance().getAssetBean().getId());
            }
        });

        binding.inputRoomNameView.ivClear.setOnClickListener(v -> binding.inputRoomNameView.tvSubTitle.setText(""));


        mViewModel.getIsCreate().observe(this, aBoolean -> {
            FamilyDataChangeManager.getInstance().changeViewDataSuccess();
            ToastUtil.showMsg(R.string.rino_family_add_room_success);
            setResult(RESULT_OK);
            finishThis();
        });
    }

    /**
     * ui
     */
    private void initView() {
        binding.inputRoomNameView.tvTitle.setText(getString(R.string.rino_family_room_name_title));
        binding.inputRoomNameView.tvSubTitle.setHint(getString(R.string.rino_family_room_input_hint));
        initTag();
    }

    @Override
    public ActivityCreateRoomBinding getBinding(LayoutInflater inflater) {
        return ActivityCreateRoomBinding.inflate(inflater);
    }

    /**
     * 房间随机布局
     */
    private void initTag() {
        TagAdapter<TagBaseBean> tagAdapter = new TagAdapter<>(this);
        binding.tagLayout.setAdapter(tagAdapter);

        List<TagBaseBean> tagBaseBeans = getRecommendedRoomListList().stream().map(name -> {
            TagBaseBean roomName = new TagBaseBean();
            roomName.setTagName(name);
            return roomName;
        }).collect(Collectors.toList());

        tagAdapter.onlyAddAll(tagBaseBeans);

        binding.tagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        binding.tagLayout.setOnTagSelectListener((parent, selectedList) -> {
            if (!tagBaseBeans.isEmpty() && tagBaseBeans.size() > selectedList.get(0)) {
                TagBaseBean tagBaseBean = tagBaseBeans.get(selectedList.get(0));
                binding.inputRoomNameView.tvSubTitle.setText(tagBaseBean.getTagName());
                binding.tvConfirm.setBackgroundResource(R.drawable.shape_cen_dialog_confirm);
            }
        });
    }

    private List<String> getRecommendedRoomListList() {
        return new ArrayList<String>() {{
            add(getString(R.string.rino_family_living_room));
            add(getString(R.string.rino_family_master_bedroom));
            add(getString(R.string.rino_family_second_bedroom));
            add(getString(R.string.rino_family_dining_room));
            add(getString(R.string.rino_family_kitchen));
            add(getString(R.string.rino_family_balcony));
        }};
    }
}
