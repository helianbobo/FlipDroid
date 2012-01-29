package com.goal98.flipdroid.model;

import android.util.Log;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.view.ThumbnailViewContainer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/30/11
 * Time: 7:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageViewSlidingWindows extends SlidingWindows {
    private PageActivity.WeiboPageViewFactory pageViewFactory;
    private ContentRepo repo;
    private Lock preloadingLock = new ReentrantLock();
    private ExecutorService executor;
    public PageViewSlidingWindows(int worker, ContentRepo repo, PageActivity.WeiboPageViewFactory pageViewFactory, int step) {
        super(worker, step);
        //Log.d("SLIDING", "creating PageViewSlidingWindows with " + worker + " workers");
//        for (int i = 0; i < worker; i++) {
        executor = Executors.newCachedThreadPool();
        windows[0] = new PageViewWindow(0, 0, preloadingLock, repo, pageViewFactory,executor,null);
        windows[0].startTask();
        this.repo = repo;
        this.pageViewFactory = pageViewFactory;

    }

    protected Window getLastWindow() {
        return new CoverWindow(pageViewFactory.createLastPage());
    }

    public void createWindowIfNullOrOld(int index, int pageNumber) {
        if (windows[index] == null || windows[index].pageNumber != pageNumber) {
            Log.d("SLIDING", "creating new Window: arr pos:" + (index) + "pageNumber:" + pageNumber + "cycle:" + cycle);
            ThumbnailViewContainer thumbnailViewContainer = null;
            if (windows[index] != null) {
                thumbnailViewContainer = (windows[index]).get();
                if (thumbnailViewContainer != null)
                    thumbnailViewContainer.releaseResource();
            }
            windows[index] = new PageViewWindow(index, pageNumber, preloadingLock, repo, pageViewFactory, executor, thumbnailViewContainer);
        }
    }

    protected Window getFirstWindow() {
        return new CoverWindow(pageViewFactory.createFirstPage());
    }

    private class CoverWindow extends Window {
        ThumbnailViewContainer pageContainer;

        public CoverWindow(ThumbnailViewContainer pageContainer) {
            this.pageContainer = pageContainer;
            this.loaded = true;
            this.skip = false;
            this.loading = false;
        }

        public void startTask() {
        }

        public ThumbnailViewContainer get() {
            return pageContainer;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
