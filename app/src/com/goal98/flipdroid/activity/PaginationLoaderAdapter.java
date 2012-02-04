package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import com.goal98.flipdroid.exception.NoMorePageException;
import com.goal98.flipdroid.model.NoSuchPageException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/16/11
 * Time: 11:40 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PaginationLoaderAdapter extends AutoLoadArrayAdapter {
    int pageCount = 10;
    int page = 0;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    PaginationLoaderService loader;

    public PaginationLoaderAdapter(Activity activity, ListView listView, int layoutId, int progressDrawableResourceId, List items, int nodataview, PaginationLoaderService loader, View.OnClickListener noitemListener) {
        super(activity, listView, layoutId, progressDrawableResourceId, items, nodataview, noitemListener);
        this.loader = loader;
    }

    public List load() throws NoSuchPageException {
            return loader.load(++page, pageCount);
    }

    public void reset() {
        items.clear();
//        refreshingToken
        this.page = 0;
    }
}
