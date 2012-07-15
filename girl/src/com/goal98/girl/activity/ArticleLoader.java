package com.goal98.girl.activity;

import android.app.Activity;
import com.goal98.girl.db.RSSURLDB;
import com.goal98.girl.exception.NoMorePageException;
import com.goal98.girl.exception.NoMoreStatusException;
import com.goal98.girl.exception.NoNetworkException;
import com.goal98.girl.model.*;
import com.goal98.girl.model.cachesystem.CachedArticleSource;
import com.goal98.girl.model.cachesystem.SourceUpdateable;
import com.goal98.girl.view.FixedPagingStrategy;
import com.goal98.girl.view.NoMoreArticleListener;
import com.goal98.girl.view.Page;
import com.goal98.girl.view.PagingStrategy;
import com.srz.androidtools.autoloadlistview.NoSuchPageException;
import com.srz.androidtools.autoloadlistview.PaginationLoaderService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/18/11
 * Time: 11:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleLoader implements PaginationLoaderService, SourceUpdateable {
    Activity activity;
    private ContentRepo repo;
    private final ArticleSource articleSource;
    private RSSURLDB rssurlDB;
    private String name;
    private boolean isFavorite;

    public ArticleLoader(Activity activity, int articlePerPage){
        this(activity,articlePerPage,null,null,-1,-1);
    }

    public ArticleLoader(Activity activity, int articlePerPage, String from, String name, int inDaysFrom, int inDaysTo) {
        this.activity = activity;
        this.name = name;
        PagingStrategy pagingStrategy = new FixedPagingStrategy(activity, articlePerPage);
        pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
            public void onNoMoreArticle() throws NoMoreStatusException {
                throw new NoMoreStatusException();
            }
        });
        Semaphore refreshingSemaphore = new Semaphore(1, true);
        repo = new ContentRepo(pagingStrategy, refreshingSemaphore);
        rssurlDB = new RSSURLDB(activity);
        articleSource = new AllLocalArticleSource(rssurlDB, from, inDaysFrom, inDaysTo);
        repo.setArticleSource(articleSource);
        repo.setPagingStrategy(pagingStrategy);
    }

    public ArticleLoader(Activity activity, int articlePerPage,String from, boolean isFavorite) {
        this.activity = activity;
        this.isFavorite = isFavorite;
        PagingStrategy pagingStrategy = new FixedPagingStrategy(activity, articlePerPage);
        pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
            public void onNoMoreArticle() throws NoMoreStatusException {
                throw new NoMoreStatusException();
            }
        });
        Semaphore refreshingSemaphore = new Semaphore(1, true);
        repo = new ContentRepo(pagingStrategy, refreshingSemaphore);
        rssurlDB = new RSSURLDB(activity);
        articleSource = new AllLocalFavoriteArticleSource(rssurlDB, from);
        repo.setArticleSource(articleSource);
        repo.setPagingStrategy(pagingStrategy);
    }


    public List<ArticleDetailInfo> load(int pageNumber) throws NoSuchPageException {
        pageNumber--;
        List<Article> articles = null;
        Page p;
        try {
            repo.refreshAndPage(0);
            articles = repo.getPage(pageNumber).getArticleList();
        } catch (NoMorePageException e) {
            articles = onNoMorePage(pageNumber).getArticleList();
        } catch (NoMoreStatusException e) {
            throw new NoSuchPageException("");
        } catch (Exception e) {
            return new ArrayList<ArticleDetailInfo>();
        }
        List<ArticleDetailInfo> detailInfos = new ArrayList<ArticleDetailInfo>();
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            ArticleDetailInfo articleDetailInfo = new ArticleDetailInfo(article);
            detailInfos.add(articleDetailInfo);
        }
        return detailInfos;
    }

    private Page onNoMorePage(int pageNumber) throws NoSuchPageException {
        Page page = null;
        //Log.d("SLIDING", "no more pageContainer, refreshing: " + this);
        int currentToken = repo.getRefreshingToken();
        try {
            repo.refreshAndPage(currentToken);
            try {
                page = repo.getPage(pageNumber);
            } catch (NoMorePageException e) {
                page = onNoMorePage(pageNumber);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (NoNetworkException e1) {
            e1.printStackTrace();
        } catch (NoMoreStatusException e2) {
           throw new NoSuchPageException("");
        }
        return page;
    }

    public void notifyUpdating(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyHasNew(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyNoNew(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyUpdateDone(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reset() {
        articleSource.reset();
        repo.getContentCache().reset();
    }

    public void closeDB() {
        rssurlDB.close();
    }

    public void reopen() {
        rssurlDB.open();
    }

    public String getName() {
        return name;
    }
}
