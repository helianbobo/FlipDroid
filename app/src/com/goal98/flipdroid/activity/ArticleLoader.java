package com.goal98.flipdroid.activity;

import android.app.Activity;
import com.goal98.flipdroid.db.SourceContentDB;
import com.goal98.flipdroid.exception.NoMorePageException;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.exception.NoNetworkException;
import com.goal98.flipdroid.model.*;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.view.FixedPagingStrategy;
import com.goal98.flipdroid.view.NoMoreArticleListener;
import com.goal98.flipdroid.view.Page;
import com.goal98.flipdroid.view.PagingStrategy;

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
    private int articlePerPage;
    private final ArticleSource articleSource;
    private SourceContentDB contentDB;

    public ArticleLoader(Activity activity, int articlePerPage) {
        this.activity = activity;
        this.articlePerPage = articlePerPage;
        PagingStrategy pagingStrategy = new FixedPagingStrategy(activity, articlePerPage);
        pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
            public void onNoMoreArticle() throws NoMoreStatusException {
                throw new NoMoreStatusException();
            }
        });
        Semaphore refreshingSemaphore = new Semaphore(1, true);
        repo = new ContentRepo(pagingStrategy, refreshingSemaphore);
        contentDB = new SourceContentDB(activity);
        articleSource = new AllLocalArticleSource(contentDB);
        repo.setArticleSource(articleSource);
        repo.setPagingStrategy(pagingStrategy);
    }


    public List<ArticleDetailInfo> load(int pageNumber, int count) throws NoSuchPageException {
        pageNumber--;
        List<Article> articles = null;
        Page p;
        try {
            repo.refreshAndPage(0);
            articles = repo.getPage(pageNumber).getArticleList();
        } catch (NoMorePageException e) {
            articles = onNoMorePage(pageNumber).getArticleList();
        } catch (NoSuchPageException e1) {
            throw e1;
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
            } catch (NoSuchPageException e1) {
                e1.printStackTrace();
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
}
