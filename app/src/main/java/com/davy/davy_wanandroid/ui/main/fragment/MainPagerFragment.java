package com.davy.davy_wanandroid.ui.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.davy.davy_wanandroid.R;
import com.davy.davy_wanandroid.app.Constants;
import com.davy.davy_wanandroid.base.fragment.BaseRootFragment;
import com.davy.davy_wanandroid.bean.main.BannerData;
import com.davy.davy_wanandroid.bean.main.WanAndroidArticleData;
import com.davy.davy_wanandroid.bean.main.WanAndroidArticleListData;
import com.davy.davy_wanandroid.contract.mainpager.MainPagerContract;
import com.davy.davy_wanandroid.core.event.AutoLoginEvent;
import com.davy.davy_wanandroid.core.event.LoginEvent;
import com.davy.davy_wanandroid.di.component.ApplicationComponent;
import com.davy.davy_wanandroid.di.component.DaggerHttpComponent;
import com.davy.davy_wanandroid.presenter.MainPagerPresenter;
import com.davy.davy_wanandroid.ui.mainpager.adapter.ArticleListAdapter;
import com.davy.davy_wanandroid.utils.CommonUtils;
import com.davy.davy_wanandroid.utils.GlideImageLoader;
import com.davy.davy_wanandroid.utils.RxBus;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author: Davy
 * date: 18/9/29
 */
public class MainPagerFragment extends BaseRootFragment<MainPagerPresenter> implements MainPagerContract.View {

    @BindView(R.id.main_pager_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.normal_view)
    SmartRefreshLayout mRefreshLayout;

    private boolean isRecreate;
    private List<WanAndroidArticleData> mWanAndroidArticleData;
    private ArticleListAdapter mArticleListAdapter;
    private Banner mBanner;
    private List<String> mBannerTitleList;
    private List<String> mBannerUrlList;

    public static MainPagerFragment getInstance(boolean param1, String param2) {
        MainPagerFragment fragment = new MainPagerFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.ARG_PARAM1, param1);
        args.putString(Constants.ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initInjector(ApplicationComponent applicationComponent) {
        DaggerHttpComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isRecreate = getArguments().getBoolean(Constants.ARG_PARAM1);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mBanner != null){
            mBanner.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBanner != null){
            mBanner.stopAutoPlay();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_pager;
    }

    @Override
    protected void initView() {
        super.initView();
        initRecyclerView();
    }

    private void initRecyclerView() {
       mWanAndroidArticleData = new ArrayList<>();
       mArticleListAdapter = new ArticleListAdapter(R.layout.item_pager, mWanAndroidArticleData);

       mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
       mRecyclerView.setHasFixedSize(true);
        LinearLayout mHeaderGroup = (LinearLayout) LayoutInflater.from(_mActivity).inflate(R.layout.head_banner, null);
        mBanner = mHeaderGroup.findViewById(R.id.head_banner);
        mHeaderGroup.removeView(mBanner);
        mArticleListAdapter.addHeaderView(mBanner);
        mRecyclerView.setAdapter(mArticleListAdapter);
    }

    @Override
    protected void initEventAndData() {
        super.initEventAndData();
        setRefresh();
        if(loginAndNotRecreate()){
            mPresenter.loadMainPagerData();
        }else{
            mPresenter.autoRefresh(true);
        }
        if(CommonUtils.isNetWorkConnected()){
            showLoading();
        }

    }

    private boolean loginAndNotRecreate() {
        return !TextUtils.isEmpty(mPresenter.getLoginAccount())
                && !TextUtils.isEmpty(mPresenter.getLoginPassword())
                && !isRecreate;
    }

    private void setRefresh() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mPresenter.autoRefresh(false);
                refreshLayout.finishRefresh(1000);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mPresenter.loadMore();
                refreshLayout.finishLoadMore(1000);
            }
        });
    }

    public void jumpToTop(){
        if(mRecyclerView != null){
            mRecyclerView.smoothScrollToPosition(0);
        }
    }
    @Override
    public void showError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        super.showError();
    }

    @Override
    public void reload() {
        if(mRefreshLayout != null && mPresenter != null
                && mRecyclerView.getVisibility() == View.INVISIBLE
                && CommonUtils.isNetWorkConnected()){
            mRefreshLayout.autoRefresh();
        }
    }

    @Override
    public void showLoginView() {
        mPresenter.getWanAndroidArticleList(false);
    }

    @Override
    public void showLoginOutView() {
        mPresenter.getWanAndroidArticleList(false);
    }

    @Override
    public void showCollectSucces() {

    }

    @Override
    public void showCancleCollectSucces() {

    }

    @Override
    public void showAutoLoginSuccess() {
        if(isAdded()){
            CommonUtils.showSnackMessage(_mActivity, getString(R.string.auto_login_success));
            RxBus.getDefault().post(new AutoLoginEvent());
        }
    }

    @Override
    public void showAutoLoginFail() {
        mPresenter.setLoginStatus(false);
        //Cookies处理

        RxBus.getDefault().post(new LoginEvent(false));
    }

    @Override
    public void showArticleList(WanAndroidArticleListData wanAndroidArticleListData, boolean isRefresh) {
        if(mPresenter.getCurrentPage() == Constants.TYPY_MAIN_PAGER){
            mRecyclerView.setVisibility(View.VISIBLE);
        }else {
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
        if(mArticleListAdapter == null){
            return;
        }
        if(isRefresh){
            mWanAndroidArticleData = wanAndroidArticleListData.getDatas();
            mArticleListAdapter.replaceData(wanAndroidArticleListData.getDatas());
        }else {
            mWanAndroidArticleData.addAll(wanAndroidArticleListData.getDatas());
            mArticleListAdapter.addData(wanAndroidArticleListData.getDatas());
        }

        showNormal();
    }

    @Override
    public void showCollectArticleData(int positin, WanAndroidArticleData wanAndroidArticleData, WanAndroidArticleListData wanAndroidArticleListData) {
        mArticleListAdapter.addData(positin,wanAndroidArticleData);
        CommonUtils.showSnackMessage(_mActivity, getString(R.string.collet_success));
    }

    @Override
    public void showCancleCollectArticleData(int position, WanAndroidArticleData wanAndroidArticleData, WanAndroidArticleListData wanAndroidArticleListData) {
        mArticleListAdapter.addData(position,wanAndroidArticleData);
        CommonUtils.showSnackMessage(_mActivity, getString(R.string.cancel_collet_success));
    }

    @Override
    public void showBannerData(List<BannerData> bannerDataList) {
        mBannerTitleList = new ArrayList<>();
        List<String> bannerImageList = new ArrayList<>();
        mBannerUrlList = new ArrayList<>();
        for (BannerData bannerData : bannerDataList){
            mBannerTitleList.add(bannerData.getTitle());
            mBannerUrlList.add(bannerData.getUrl());
            bannerImageList.add(bannerData.getImagePath());
        }

        mBanner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setImages(bannerImageList);
        mBanner.setBannerAnimation(Transformer.DepthPage);
        mBanner.setBannerTitles(mBannerTitleList);
        mBanner.isAutoPlay(true);
        mBanner.setDelayTime(bannerDataList.size() * 400);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);

        mBanner.start();

    }
}
