package com.wuliwuwai.listviewpager;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ListViewPager {

    /**
     * ���ط�ҳ
     */
    private LinearLayout     loadingLayout;
    /**
     * ��ǰҳ
     */
    private Integer          page            = 1;
    /**
     * �и���
     */
    private Boolean          hasMore         = true;
    /**
     * ��ǰ��ʵ���һ��item
     */
    private Integer          lastItem        = 0;
    /**
     * �����¼�
     */
    private OnScrollListener onScrollListener;

    /**
     * �ɷ�������ر�ǣ�trueΪ���Լ��أ�false�����ܼ���
     */
    private AtomicBoolean    keepOnAppending = new AtomicBoolean(true);
    /**
     * ���ص�ListView�ؼ�
     */
    private ListView         listView;

    private PagerService     pagerService;

    public interface PagerService {
        /**
         * 
         * @param page
         *            ҳ�룬ͨ�������page++
         * @param finished
         *            ���ص��ӿ�ִ����ɺ󣬵���finished��Ļص����onFinished
         */
        public void getNext(int page, OnServiceFinished finished);
    }

    public interface OnServiceFinished {
        /**
         * serviceִ�����֮����Ҫ����
         */
        public void onFinished();
    }

    public void removeFootView() {
        this.listView.removeFooterView(loadingLayout);
    }

    public void reset() {
        // removeFootView();
        this.page = 1;
        this.lastItem = 0;
        this.hasMore = true;
        keepOnAppending.set(true);
    }

    public void requestData() {

    }

    /**
     * pull to refresh
     * 
     * @param context
     * @param mListView
     * @param isPullToRefresh
     * @param service
     */
    public ListViewPager(Context context, ListView mListView,
            boolean isPullToRefresh, final PagerService service) {
        super();
        if (isPullToRefresh) {
            reset();
            this.listView = mListView;
            loadingLayout = (LinearLayout) LayoutInflater.from(context)
                    .inflate(R.layout.view_loading_layout, null);

            this.listView.addHeaderView(loadingLayout);
            onScrollListener = new OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view,
                        int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                        int visibleItemCount, int totalItemCount) {

                    if (hasMore && keepOnAppending.get()) {

                        // lastItem = firstVisibleItem + visibleItemCount - 1;
                        if (firstVisibleItem == 0) {
                            getPullData(service);
                        }
                    }
                }
            };
            this.listView.setOnScrollListener(onScrollListener);
        }
    }

    public ListViewPager(Context context, ListView mListView,
            final PagerService service) {
        reset();

        this.listView = mListView;
        loadingLayout = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.view_loading_layout, null);
        this.listView.addFooterView(loadingLayout);
        onScrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

                if (hasMore && keepOnAppending.get()) {
                    lastItem = firstVisibleItem + visibleItemCount - 1;
                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        getData(service);
                    }
                }
            }
        };
        this.listView.setOnScrollListener(onScrollListener);

    }

    private void getPullData(PagerService service) {
        final int pCount = listView.getCount();
        keepOnAppending.set(false);
        service.getNext(page++, new OnServiceFinished() {
            @Override
            public void onFinished() {

                Log.i("ListViewPager", pCount + "   " + listView.getCount()
                        + " footViewCount:" + listView.getFooterViewsCount()
                        + " childCount:" + listView.getCount());
                if (listView.getCount() == pCount) {
                    hasMore = false;

                    listView.removeHeaderView(loadingLayout);

                }
                keepOnAppending.set(true);
            }
        });
    }

    private void getData(PagerService service) {
        final int pCount = listView.getCount();
        keepOnAppending.set(false);
        service.getNext(page++, new OnServiceFinished() {
            @Override
            public void onFinished() {

                Log.i("ListViewPager", pCount + "   " + listView.getCount()
                        + " footViewCount:" + listView.getFooterViewsCount()
                        + " childCount:" + listView.getCount());
                if (listView.getCount() == pCount) {
                    hasMore = false;
                    // if (listView.getFooterViewsCount() > 0)
                    // listView.getChildCount();
                    // loadingLayout.setVisibility(View.GONE);
                    listView.removeFooterView(loadingLayout);
                }
                keepOnAppending.set(true);
            }
        });
    }

}
