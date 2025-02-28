package com.smart.rinoiot.center.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.smart.rinoiot.center.activity.HomeActivity;
import com.smart.rinoiot.center.adapter.msg.MsgListAdapter;
import com.smart.rinoiot.center.bean.msg.MsgRecordItemBean;
import com.smart.rinoiot.center.viewmodel.msg.MsgViewModel;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.page.BaseSectionAdapter;
import com.smart.rinoiot.common.page.BaseSectionFragmentRecyclerView;
import com.smart.rinoiot.common.utils.AppExecutors;
import com.smart.rinoiot.user.databinding.FragmentMsgListBinding;

import java.util.concurrent.TimeUnit;

/**
 * @author tw
 * @time 2022/12/5 15:25
 * @description 消息中心
 */
@SuppressLint("NewApi")
public class InformationFragment extends BaseSectionFragmentRecyclerView<FragmentMsgListBinding, MsgViewModel, MsgRecordItemBean> {
    @Override
    public void init() {
        initRecyclerView();
        initData();
        requestMsg(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        AppExecutors.getInstance().delayedThread().schedule(() -> {
            //必须加延迟，否则mviewmodel没有初始化
            if (isVisibleToUser) {
                if (mViewModel != null) {
                    mViewModel.setUserAllReadMessages();
                }
            } else {
                if (mViewModel != null) {
//                    mViewModel.getUnreadCount();
                    mViewModel.getUnReadMsgCount();
                }
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        initRecyclerAndAdapter(1);
        //设置 Header 为 贝塞尔雷达 样式
        binding.refresh.setRefreshHeader(new ClassicsHeader(getContext()));
        //设置 Footer 为 球脉冲 样式
        binding.refresh.setRefreshFooter(new ClassicsFooter(getContext()));
    }

    private void initData() {
        mViewModel.getMsgListLiveData().observe(this, msgRecordItemBeans -> {
            binding.refresh.finishRefresh();
            binding.refresh.finishLoadMore();
            if (msgRecordItemBeans != null && !msgRecordItemBeans.isEmpty()) {
                hideEmptyView();
                if (pageInfo.isFirstPage()) {//第一页，且数据不为空时设置
                    adapter.setNewInstance(mViewModel.formatMsgData(msgRecordItemBeans, null));
                } else {//
                    adapter.setNewInstance(mViewModel.formatMsgData(msgRecordItemBeans, adapter.getData()));
                }

                if (msgRecordItemBeans.size() < Constant.PAGE_SIZE) {//当前数据小于分页数据，说明以加载完
                    adapter.getLoadMoreModule().loadMoreEnd(true);
                } else {
                    adapter.getLoadMoreModule().loadMoreComplete();
                }
                adapter.notifyDataSetChanged();
            } else {
                adapter.getLoadMoreModule().loadMoreComplete();
            }
            if (adapter.getData() == null || adapter.getData().isEmpty()) {
                showEmptyView();
            }
        });

        binding.refresh.setOnRefreshListener(refreshLayout -> {
            mViewModel.setUserAllReadMessages();
            requestMsg(true);
        });

        binding.refresh.setOnLoadMoreListener(refreshLayout -> requestMsg(false));
//        mViewModel.getUnreadLiveData().observe(this, unreadBean -> {
//            boolean hasMessage = !(unreadBean == null || (unreadBean.getEventCount() == 0 && unreadBean.getNoticeCount() == 0));
//            ((HomeActivity) getActivity()).unreadUpdate(hasMessage);
//        });
        mViewModel.getUnreadV2LiveData().observe(this, unReadCount -> {
            boolean hasMessage = (null != unReadCount) && (unReadCount > 0);
            ((HomeActivity) getActivity()).unreadUpdate(hasMessage);
        });
    }

    private void listener() {
        adapter.setOnItemClickListener((adapter, view, position) -> {

        });
    }

    @Override
    public FragmentMsgListBinding getBinding(LayoutInflater inflater) {
        return FragmentMsgListBinding.inflate(inflater);
    }

    @Override
    protected BaseSectionAdapter<MsgRecordItemBean> createRecycleViewAdapter() {
        return new MsgListAdapter(null);
    }

    @Override
    protected SwipeRefreshLayout createSwipeRefresh() {
        return null;
    }

    @Override
    protected RecyclerView createRecyclerView() {
        return binding.recyclerView;
    }

    @Override
    protected void onRefresh() {
        requestMsg(true);
    }

    @Override
    protected void onLoadMore() {
        requestMsg(false);
    }

    /**
     * 消息列表接口请求
     *
     * @param isFresh true：刷新，page=1
     */
    private void requestMsg(boolean isFresh) {
        if (isFresh) {
            pageInfo.reset();
        } else {
            pageInfo.nextPage();
        }
        mViewModel.getMsgList(pageInfo.getPage());
    }

}
