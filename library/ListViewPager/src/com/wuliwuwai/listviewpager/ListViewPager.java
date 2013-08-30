package com.wuliwuwai.listviewpager;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class ListViewPager {
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
         * �ͻ��˻�����ݺ������õķ���
         */
        public void onFinished();
    }

    /**
     * ���ط�ҳ
     */
    private View             loadingView;
    /**
     * ��ǰҳ
     */
    private Integer          page            = 1;
    /**
     * �и���
     */
    private Boolean          hasMore         = true;

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
    private ListView         mListView;
    /**
     * �ص��ӿ�
     */
    private PagerService     pagerService;
    /**
     * �����Ķ���
     */
    private Context          mContext;

    public ListViewPager(Context context, ListView listView,
            PagerService pagerService) {
        this(context, listView);
        this.setPagerService(pagerService);
    }

    public ListViewPager(Context context, ListView listView) {
        super();
        reSet();
        this.mContext = context;
        this.mListView = listView;
        loadingView = LayoutInflater.from(this.mContext).inflate(
                R.layout.view_loading_layout, null);
        this.mListView.addFooterView(loadingView);
        onScrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

                if (hasMore && keepOnAppending.get()) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        if (pagerService != null) {
                            final int pCount = mListView.getCount();
                            keepOnAppending.set(false);
                            pagerService.getNext(page++,
                                    new OnServiceFinished() {
                                        @Override
                                        public void onFinished() {
                                            // ��ȡ��һҳ���ݺ����ݵĸ���û�б仯��ʾû���ĵ����ݣ����ؼ��ؿؼ�
                                            if (mListView.getCount() == pCount) {
                                                hasMore = false;
                                                // listView.removeFooterView(loadingView);
                                                loadingView
                                                        .setVisibility(View.GONE);
                                            }
                                            keepOnAppending.set(true);
                                        }
                                    });
                        }
                    }
                }
            }
        };
        this.mListView.setOnScrollListener(onScrollListener);
    }

    /**
     * �Ƴ��ײ��ؼ�
     */
    public void removeFootView() {
        this.mListView.removeFooterView(loadingView);
    }

    /**
     * ������������������ˢ�£��ͻ��˴�����Ҫͬʱ��յ�ListView�������е�����
     * 
     * 
     */
    public void reSet() {
        if (loadingView != null) {
            this.loadingView.setVisibility(View.VISIBLE);
        }
        this.page = 1;
        this.hasMore = true;
        keepOnAppending.set(true);
    }

    public Integer getPage() {
        return page;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public View getLoadingView() {
        return loadingView;
    }

    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
    }

    public void setPagerService(PagerService pagerService) {
        this.pagerService = pagerService;
    }

}
