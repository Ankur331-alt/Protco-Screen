package com.smart.rinoiot.common.page;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.base.BaseFragment;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.view.SpacesItemDecoration;

import java.util.Collection;

public abstract class BaseFragmentMultiRecyclerView<T extends ViewBinding, M extends BaseViewModel, V extends MultiItemEntity> extends BaseFragment<T, M> {

    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected PageInfo pageInfo = new PageInfo();
    protected BaseMultiAdapter<V> adapter;

    protected abstract BaseMultiAdapter<V> createRecycleViewAdapter();

    protected abstract SwipeRefreshLayout createSwipeRefresh();

    protected abstract RecyclerView createRecyclerView();

    protected abstract void onRefresh();

    protected abstract void onLoadMore();

    protected int createEmptyView() {
        return R.layout.layout_empty;
    }

    /**
     * 初始化Recycler和Adapter
     */
    protected void initRecyclerAndAdapter(int type) {
        initRecyclerAndAdapter(LinearLayoutManager.VERTICAL, type);
    }

    /**
     * 初始化Recycler和Adapter
     */
    protected void initRecyclerAndAdapter(@RecyclerView.Orientation int orientation, int type) {
        recyclerView = createRecyclerView();
        swipeRefreshLayout = createSwipeRefresh();
        if (recyclerView == null) return;
        setScroolData();
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        if (type == 1) {//列表
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), orientation, false));
        } else if (type == 2) {//网状
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, orientation, false));
            recyclerView.addItemDecoration(new SpacesItemDecoration(14, 15));
        } else {
            //瀑布流
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            //防止Item切换
            //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            recyclerView.setLayoutManager(layoutManager);
            //解决底部滚动到顶部时，顶部item上方偶尔会出现一大片间隔的问题6
            final int spanCount = 2;
            recyclerView.addItemDecoration(new StaggeredDividerItemDecoration(getContext(), 10, spanCount));
        }

        adapter = createRecycleViewAdapter();
        if (adapter == null) return;
        // 开启动画
        adapter.setAnimationEnable(false);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化下拉刷新
     */
    protected void initRefreshLayout() {
        if (swipeRefreshLayout == null) return;
        swipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        swipeRefreshLayout.setOnRefreshListener(this::refresh);
    }

    /**
     * 初始化加载更多
     */
    protected void initLoadMore() {
        if (adapter != null) adapter.getLoadMoreModule().setEnableLoadMore(true);
        if (adapter != null) adapter.getLoadMoreModule().setOnLoadMoreListener(this::onLoadMore);
    }

    /**
     * 刷新
     */
    protected void refresh() {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
        // 这里的作用是防止下拉刷新的时候还可以上拉加载
        if (adapter != null) adapter.getLoadMoreModule().setEnableLoadMore(false);
        // 下拉刷新，需要重置页数
        pageInfo.reset();
        onRefresh();
    }

    /**
     * 显示数据
     */
    protected void showDatas(@NonNull Collection<? extends V> datas) {
        showDatas(datas, false);
    }

    /**
     * 显示数据
     */
    protected void showDatas(@NonNull Collection<? extends V> datas, boolean finishEndView) {
        if (adapter == null) return;
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
        adapter.getLoadMoreModule().setEnableLoadMore(true);

        if (pageInfo.isFirstPage()) {
            // 如果是加载的第一页数据，replaceData()
            if (datas != null) {
                adapter.setList(datas);
            }
        } else {
            // 不是第一页，则用addData()
            adapter.addData(datas);
        }

        if (datas != null && datas.size() < Constant.PAGE_SIZE) {
            // 如果不够一页,显示没有更多数据布局
            adapter.getLoadMoreModule().loadMoreEnd(true);
            if (finishEndView)
                adapter.getLoadMoreModule().setLoadMoreView(new SimpleLoadMoreView());
        } else {
            adapter.getLoadMoreModule().loadMoreComplete();
        }

        // page加一
        pageInfo.nextPage();
        adapter.notifyDataSetChanged();
        // 没有数据的时候默认显示该布局
        adapter.setEmptyView(createEmptyView());
    }

    //设置NestedScrollView与recyclerView滑动冲突
    private void setScroolData() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setFocusable(false);
    }
}
