package com.smart.rinoiot.family.activity;

import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.bean.MemberBean;
import com.smart.rinoiot.common.utils.ImageLoaderUtils;
import com.smart.rinoiot.family.R;
import com.smart.rinoiot.family.databinding.ActivityMemberInfoBinding;
import com.smart.rinoiot.family.viewmodel.MemberInfoViewModel;

/**
 * author：jiangtao
 * <p>
 * create-time: 2022/9/6
 */
public class MemberInfoActivity extends BaseActivity<ActivityMemberInfoBinding, MemberInfoViewModel> {
    private MemberBean memberBean;
    private int memberRole;

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_family_member_info);
    }

    @Override
    public void init() {
        memberBean = (MemberBean) getIntent().getSerializableExtra(Constant.Member_Data);
        memberRole = getIntent().getIntExtra(Constant.MEMBER_ROLE, -1);
        if (memberBean == null) return;
        peniedEmpty();
        ImageLoaderUtils.getInstance().bindRoundImageUrl(memberBean.getAvatarUrl(), binding.ivUser, 15, R.drawable.icon_default_avatar);
        binding.tvName.setText(memberBean.getName());
        binding.tvAccount.setText(memberBean.getUserName());

        binding.tvRole.setText(mViewModel.getRole(memberBean.getMemberRole()));

        /**
         * 成员等级 比自己等级低的可以将它移除
         */
        binding.tvRemoveFamily.setOnClickListener(view -> //关闭Activity
                mViewModel.showRemoveFamilyDialog(this, memberBean.getUserId()));
        binding.tvRole.setOnClickListener(v -> mViewModel.showChangeRole(this, memberBean.getAssetId(), memberBean.getUserId(), memberBean.getMemberRole()));
        mViewModel.getRoleLiveData().observe(this, s -> {
            binding.tvRole.setText(s);
            setResult(RESULT_OK);
        });
    }

    @Override
    public ActivityMemberInfoBinding getBinding(LayoutInflater inflater) {
        return ActivityMemberInfoBinding.inflate(inflater);
    }

    /**
     * 根据权限及是否有数据展示空布局
     * 成员角色（1=拥有者，2=管理员，3=普通成员）
     */
    private void peniedEmpty() {
        if (memberRole == -1) return;
        if (memberRole == 1) {//拥有者
            if (memberBean.getMemberRole() == 1) {
                binding.tvRemoveFamily.setVisibility(View.GONE);
                binding.ivArrow.setVisibility(View.GONE);
                binding.tvRole.setEnabled(false);
            } else {
                binding.tvRemoveFamily.setVisibility(View.VISIBLE);
                binding.ivArrow.setVisibility(View.VISIBLE);
                binding.tvRole.setEnabled(true);
            }
        } else if (memberRole == 2) {//管理员
            if (memberBean.getMemberRole() == 1) {
                binding.tvRemoveFamily.setVisibility(View.GONE);
            } else {
                binding.tvRemoveFamily.setVisibility(memberRole < memberBean.getMemberRole() ? View.VISIBLE : View.GONE);
            }
            binding.ivArrow.setVisibility(View.GONE);
            binding.tvRole.setEnabled(false);
        } else {//普通成员
            binding.tvRemoveFamily.setVisibility(View.GONE);
            binding.ivArrow.setVisibility(View.GONE);
            binding.tvRole.setEnabled(false);
        }
    }
}
