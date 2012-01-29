package com.goal98.flipdroid.model;

import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.exception.NoMorePageException;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.exception.NoNetworkException;
import com.goal98.flipdroid.view.Page;
import com.goal98.flipdroid.view.ThumbnailViewContainer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
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

    private Future<ThumbnailViewContainer> task;
    private ExecutorService executor;
    private PageActivity.WeiboPageViewFactory pageViewFactory;
    private ThumbnailViewContainer pageViewContainer;
    private ThumbnailViewContainer previousPeiboPageViewContainer;

    PageViewWindow(int index, int pageNumber, Lock preloadingLock, ContentRepo repo, PageActivity.WeiboPageViewFactory pageViewFactory, ExecutorService executor, ThumbnailViewContainer previousPeiboPageViewContainer) {
        super(index, pageNumber, preloadingLock);
        this.executor = executor;
        this.repo = repo;
        this.pageViewFactory = pageViewFactory;
        this.previousPeiboPageViewContainer = previousPeiboPageViewContainer;
        startTask();
    }

    @Override
    public synchronized ThumbnailViewContainer get() {
        try {
            task.get();
            return pageViewContainer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageViewFactory.createPageView();
    }

    public String toString() {
        return "Window, pageContainer number: " + pageNumber + ", skip:" + skip + ", last:" + this.isLastWindow;
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
                        //Log.d("SLIDING", "pageContainer loaded: " + this);
                    } catch (NoSuchPageException e) {
                        page = onNoMorePage(page);
                    } catch (NoMorePageException e) {
                        page = onNoMorePage(page);
                    } catch (Exception e) {
                        e.printStackTrace();
                        pageViewContainer = previousPeiboPageViewContainer;
                        return pageViewContainer;
                    }
//                    if (skip) {
//                        //Log.d("SLIDING", "skipped " + this);
//                        new Thread(new Runnable() {
//                            public void run() {
//                                for (OnLoadListener listener : onLoadListeners) {
//                                    //Log.d("SLIDING", "onload...");
//                                    listener.onWindowSkipped(PageViewWindow.this);
//                                }
//                            }
//                        }).start();
//
//                    }

                    pageViewContainer = pageViewFactory.createPageView();
                    if (page != null) {
                        pageViewContainer.setPage(page);
                    } else {
                        pageViewContainer = pageViewFactory.createLastPage();
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
                    previousPeiboPageViewContainer = null;
                    return pageViewContainer;
                } finally {
                    loading = false;
                    reloadingLock.unlock();
                }
            }

            private Page onNoMorePage(Page page) {
                //Log.d("SLIDING", "no more pageContainer, refreshing: " + this);
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
