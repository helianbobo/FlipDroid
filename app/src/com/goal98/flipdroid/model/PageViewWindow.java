package com.goal98.flipdroid.model;

import android.util.Log;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.exception.NoMorePageException;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.exception.NoNetworkException;
import com.goal98.flipdroid.view.Page;
import com.goal98.flipdroid.view.WeiboPageView;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/30/11
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageViewWindow extends Window {
    private ContentRepo repo;

    private Future<WeiboPageView> task;
    private ExecutorService executor;
    private PageActivity.WeiboPageViewFactory pageViewFactory;
    private WeiboPageView pageView;

    PageViewWindow(int index, int pageNumber, Lock preloadingLock, ContentRepo repo, PageActivity.WeiboPageViewFactory pageViewFactory) {
        super(index, pageNumber, preloadingLock);
        executor = Executors.newFixedThreadPool(1);
        this.repo = repo;
        this.pageViewFactory = pageViewFactory;
        startTask();
    }

    @Override
    public synchronized WeiboPageView get() {
        try {
            task.get();
            return pageView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageViewFactory.createPageView();
    }

    public String toString() {
        return "Window, page number: " + pageNumber + ", skip:" + skip + ", last:" + this.isLastWindow;
    }

    public synchronized void startTask() {
        if (loading)
            return;

        //Log.d("SLIDING", "starting Task:" + this);
        loading = true;
        task = executor.submit(new Callable() {
            public Object call() {
                reloadingLock.lock();
                try {
                    Page page = null;
                    try {
                        page = repo.getPage(pageNumber);
                        //Log.d("SLIDING", "page loaded: " + this);
                    } catch (NoMorePageException e) {
                        page = onNoMorePage(page);
                    } catch (Exception e) {
                        e.printStackTrace();
                        skip = true;
                    }
                    if (skip) {
                        //Log.d("SLIDING", "skipped " + this);
                        new Thread(new Runnable() {
                            public void run() {
                                for (OnLoadListener listener : onLoadListeners) {
                                    //Log.d("SLIDING", "onload...");
                                    listener.onWindowSkipped(PageViewWindow.this);
                                }
                            }
                        }).start();
                        return null;
                    }

                    pageView = pageViewFactory.createPageView();
                    //Log.d("SLIDING", "creating page view on " + PageViewWindow.this + ", page:" + page);
                    if (page != null) {
                        pageView.setPage(page);
                    } else {
                        pageView = pageViewFactory.createLastPage();
                    }

                    new Thread(new Runnable() {
                        public void run() {
                            //Log.d("SLIDING", "calling on load from  " + PageViewWindow.this);
                            for (OnLoadListener listener : onLoadListeners) {
                                //Log.d("SLIDING", "onload...");
                                listener.onWindowLoaded(PageViewWindow.this);
                            }
                        }
                    }).start();

                    loaded = true;
                    //Log.d("SLIDING", "done. returning" + pageView.getClass().getSimpleName());
                    return pageView;
                } finally {
                    loading = false;
                    reloadingLock.unlock();
                }
            }

            private Page onNoMorePage(Page page) {
                //Log.d("SLIDING", "no more page, refreshing: " + this);
                int currentToken = repo.getRefreshingToken();
                try {
                    repo.refreshAndPage(currentToken);
                    try {
                        page = repo.getPage(pageNumber);
                    } catch (NoSuchPageException e1) {
                        e1.printStackTrace();
                        isLastWindow = true;
                    } catch (NoMorePageException e) {
                        page = onNoMorePage(page);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        skip = true;
                    }
                } catch (NoNetworkException e1) {
                    e1.printStackTrace();
                    skip = true;
                } catch (NoMoreStatusException e2) {
                    e2.printStackTrace();
                    isLastWindow = true;
                }
                return page;
            }
        });
    }

    public void registerOnLoadListener(OnLoadListener listener) {
        //Log.d("SLIDING", "adding listener on  " + PageViewWindow.this);
        this.addListener(listener);
        if (!this.loading) {
            //Log.d("SLIDING", "missed, calling listener on  " + PageViewWindow.this);
            if (skip)
                listener.onWindowSkipped(this);
            else
                listener.onWindowLoaded(this);
        }
    }
}
