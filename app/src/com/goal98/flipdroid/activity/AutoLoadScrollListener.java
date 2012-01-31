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
        this.listView= listView;
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
                && (firstVisibleItem + visibleItemCount == totalItemCount)) {
            fireLoad = true;
        } else {
            if(isLastItemVisible())
                fireLoad = true;
            else
                fireLoad = false;
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (fireLoad && !adapter.isLoadingData())
                load();
            else {
                fireLoad = false;
            }
        }
    }

    public void load() {
        new Thread(new Runnable() {
            public void run() {
                adapter.setLoading(true);
                handler.post(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
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
        }).start();
    }
}

