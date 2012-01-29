package com.goal98.flipdroid.model;

import android.util.Log;
import com.goal98.flipdroid.exception.NoMorePageException;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.view.Page;
import com.goal98.flipdroid.view.PagingStrategy;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by IntelliJ IDEA.
 * User: lsha6086
 * Date: 3/25/11
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentCache {
    private PagedPageView pagedList;
    private UnPagedArticles unPagedArticles;
    private PagingStrategy pagingStrategy;
    private ArticleSource articleSource;
    private Date articleSourceLastModified;

    private Semaphore refreshingSemaphore;

    private int refreshingToken = 0;

    public int getRefreshingToken() {
        return refreshingToken;
    }

    Lock lock = new ReentrantLock();

    public int getPageCacheTo() {
        return pageCacheTo;
    }

    private int pageCacheTo;

    public ContentCache(PagedPageView pagedList, UnPagedArticles unPagedArticles, PagingStrategy pagingStrategy, Semaphore refreshingSemaphore) {
        this.pagedList = pagedList;
        this.unPagedArticles = unPagedArticles;
        this.pagingStrategy = pagingStrategy;
        this.refreshingSemaphore = refreshingSemaphore;
    }

    public Page getPagePreload(int pageNo) throws NoMorePageException, NoSuchPageException {
        try {
            lock.lock();
            Page smartPage = pagedList.getPage(pageNo);
            //Log.d("cache system", "from cache, smartPage " + pageNo + " loaded");
            return smartPage;
        } catch (NoMorePageException e) {
            //Log.d("cache system", "no more pages, doing paging in a sec");
            try {
                refreshingSemaphore.acquire();
                List<Page> newSmartPages = pagingStrategy.doPaging(unPagedArticles).getPages();
                if (newSmartPages.size() == 0) {
                    //Log.d("cache system", "no more status.");
                    throw new NoSuchPageException("pageContainer no" + pageNo);
                }
                pagedList.addAll(newSmartPages);
                pageCacheTo += newSmartPages.size();
                //Log.d("cache system", "added " + newSmartPages.size() + " more pages");
                return pagedList.getPage(pageNo);
            } catch (InterruptedException e1) {
                return pagedList.getPage(pageNo);
            } finally {
                refreshingSemaphore.release();
            }
        } catch (Exception e) {
            //Log.d("cache system", e.getMessage());
            return pagedList.getPage(pageNo - 1);
        } finally {
            lock.unlock();
        }
    }


    public Page getPage(int pageNo) throws NoMorePageException, NoSuchPageException {
        Page smartPage = pagedList.getPage(pageNo);
        //Log.d("cache system", "from cache, smartPage " + pageNo + " loaded");
        return smartPage;
    }

    public void refresh(int refreshingToken) throws NoMoreStatusException {
        //Log.d("cache system", "someone issued a refresh request, token " + refreshingToken);
        //Log.d("cache system", "content repo token is " + this.refreshingToken);
        if (this.refreshingToken <= refreshingToken) {
            this.refreshingToken++;
            if (articleSource.isNoMoreToLoad()) {
                //Log.d("cache system", "source says it has no more to give,let's see if that is true");
                //Log.d("cache system", "paged to " + unPagedArticles.getPagedTo() + ", " + unPagedArticles.getArticleList().size());

                if (unPagedArticles.getArticleList().size() > 0) {
                    //Log.d("cache system", "oh, he lied");
                    pageAfterRefresh();
                    return;
                } else {
                    throw new NoMoreStatusException();
                }
            }

            //Log.d("cache system", "refreshing");
            boolean loadResult = articleSource.loadMore();
            if (!loadResult) {
                throw new NoMoreStatusException();
            }
            if (articleSourceLastModified == null || articleSource.lastModified().after(articleSourceLastModified)) {
                unPagedArticles.setArticles(articleSource.getArticleList());
                articleSourceLastModified = articleSource.lastModified();
            }
            //Log.d("cache system", "refresh done");

        } else {
            //Log.d("cache system", "ignore the refresh request since toke doesn't match");
        }
    }

    public void pageAfterRefresh() {
        List<Page> smartPageList = pagingStrategy.doPaging(unPagedArticles).getPages();
        pagedList.addAll(smartPageList);
        //Log.d("cache system", "added " + smartPageList.size() + " pages");
        pageCacheTo += smartPageList.size();
    }

    public void setArticleSource(ArticleSource articleSource) {
        this.articleSource = articleSource;
    }
}
