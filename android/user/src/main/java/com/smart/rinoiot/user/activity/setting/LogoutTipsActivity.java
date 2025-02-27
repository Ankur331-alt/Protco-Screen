package com.smart.rinoiot.user.activity.setting;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;

import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.utils.ActivityUtils;
import com.smart.rinoiot.common.utils.DateUtils;
import com.smart.rinoiot.common.utils.PartClickUtil;
import com.smart.rinoiot.user.R;
import com.smart.rinoiot.user.databinding.ActivityLogoutTipsBinding;

/**
 * @Author : tw
 * @Time : On 2022/9/29 15:41
 * @Description : 注销账号提示页面
 */
@SuppressLint({"StringFormatInvalid", "SetTextI18n"})
public class LogoutTipsActivity extends BaseActivity<ActivityLogoutTipsBinding, BaseViewModel> implements View.OnClickListener {

    @Override
    public String getToolBarTitle() {
        return getString(R.string.rino_mine_logout);
    }

    @Override
    public void init() {
        initView();
        initListener();
        countDown();
    }


    @SuppressLint("SetTextI18n")
    private void initView() {
        String logoutCuurent = DateUtils.logoutCurrentTime() + "  00:00:00";
        String logoutWeek = DateUtils.logoutWeekTime() + "  00:00:00";
        binding.tvAccountDescribe.setText(PartClickUtil.stringColor(this,
                String.format(getString(R.string.rino_user_cancel_account_describe2), logoutCuurent)
                , R.color.c_FF8D49, getString(R.string.rino_user_cancel_account_describe2).indexOf("%1s"),
                getString(R.string.rino_user_cancel_account_describe2).indexOf("%1s") + logoutCuurent.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE));
        binding.tvAccountDescribe2.setText(String.format(getString(R.string.rino_user_cancel_account_describe3), logoutWeek));
    }

    private void initListener() {
        binding.tvLogout.setOnClickListener(this);

    }

    @Override
    public ActivityLogoutTipsBinding getBinding(LayoutInflater inflater) {
        return ActivityLogoutTipsBinding.inflate(inflater);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvLogout) {//确定注销
            ActivityUtils.startActivity(this, null, LogoutSendCodeActivity.class);
        }
    }

    /**
     * wifi列表刷新
     */
    private CountDownTimer timer;

    private void countDown() {
        if (timer != null) {
            timer.cancel();
        }
        // 倒计时60秒，一次1秒
        timer = new CountDownTimer(6 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                if (millisUntilFinished > 1000) {//最后疫苗不更新
                    binding.tvLogout.setEnabled(false);
                    binding.tvLogout.setSelected(false);
                    binding.tvLogout.setText(getString(R.string.rino_user_cancel_account_confirm) + "(" + millisUntilFinished / 1000 + "s)");
                } else {
                    binding.tvLogout.setSelected(true);
                    binding.tvLogout.setEnabled(true);
                    binding.tvLogout.setText(getString(R.string.rino_user_cancel_account_confirm));
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
}
