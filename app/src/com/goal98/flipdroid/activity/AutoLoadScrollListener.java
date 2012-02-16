package com.goal98.flipdroid.activity;

import android.os.Handler;
import android.widget.AbsListView;
import android.widget.ListView;
import com.goal98.flipdroid.model.NoSuchPageException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/10/11
 * Time: 11:35 AM
 * To change this template use FileType | Settings | FileType Templates.
 */
public class AutoLoadScrollListener implements AbsListView.OnScrollListener {
    int lastItem;
    boolean fireLoad = false;
    private OnLoadListener onLoadListener;
    private AutoLoadArrayAdapter adapter;
    Handler handler = new Handler();
    private ListView listView;

    public AutoLoadScrollListener(OnLoadListener listener, AutoLoadArrayAdapter adapter, ListView listView) {
        this.onLoadListener = listener;
        this.adapter = adapter;
        this.listView = listView;
        listView.setFastScrollEnabled(true);
    }

    private boolean isLastItemVisible() {
        final int count = this.listView.getCount();
        if (count == 0) {
            return true;
        } else if (listView.getLastVisiblePosition() == count - 1) {
            return true;
        } else {
            return false;
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (visibleItemCount > 0 && visibleItemCount < totalItemCount
                && (firstVisibleItem + visibleItemCount == totalItemCount - 1)) {
            fireLoad = true;
        } else {
            if (isLastItemVisible())
                fireLoad = true;
            else
                fireLoad = false;
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        System.out.println("cena2" + (view.getLastVisiblePosition() == adapter.getCount() - 1));
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if ((fireLoad || view.getLastVisiblePosition() == adapter.getCount() - 1) && !adapter.isLoadingData())
                load(true);
            else {
                fireLoad = false;
            }
        }
    }

    public void load(final boolean showLoading) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                if (showLoading) {
                    adapter.setLoading(true);
                    handler.post(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                final List loadedItems;
                try {
                    loadedItems = onLoadListener.load();
                    adapter.addItems(loadedItems);
                } catch (NoSuchPageException e) {
                    adapter.setNoMoreToLoad(true);
                }

                adapter.setLoading(false);
                handler.post(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        thread.start();
        if(!showLoading){
            try {
                thread.join();
            } catch (InterruptedException e) {

            }
        }
    }
}

