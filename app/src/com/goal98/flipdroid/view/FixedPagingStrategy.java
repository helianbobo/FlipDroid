package com.goal98.flipdroid.view;

import android.util.Log;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.PagedPageView;
import com.goal98.flipdroid.model.UnPagedArticles;

import java.util.List;

public class FixedPagingStrategy implements PagingStrategy {

    private PageActivity activity;

    public void setNoMoreArticleListener(NoMoreArticleListener noMoreArticleListener) {
        this.noMoreArticleListener = noMoreArticleListener;
    }

    private NoMoreArticleListener noMoreArticleListener = new DoNothingListener();
    private int articlePerPage;

    public FixedPagingStrategy(PageActivity activity, int articlePerPage) {
        this.activity = activity;
        this.articlePerPage = articlePerPage;
    }

    public PagedPageView doPaging(UnPagedArticles unPagedArticles) {
        //Log.d("cache system", "paging " + unPagedArticles.getArticleList().size() + "articles");
        PagedPageView pagedPageView = new PagedPageView();


        List<Article> unpagesArticlesList = unPagedArticles.getArticleList();
        if (unpagesArticlesList.size() == 0) {//1第一次进来  2.正好分完，蛮巧的
            if (!onNoMoreArticle()) {
                return pagedPageView;
            }
            unpagesArticlesList = unPagedArticles.getArticleList();
        }
        Page smartPage = new Page(activity);
        int k = unPagedArticles.getPagedTo();
        for (int i = 0, j = 0; i < unpagesArticlesList.size(); i++) {
            Article article = unpagesArticlesList.get(i);
            if (j == articlePerPage) {
                j = 0;
                pagedPageView.add(smartPage);//这页加好了
                if (pagedPageView.size() >= 2) {//预拿2页
                    unPagedArticles.setPagedTo(k);//下次从i开始再拿
                    //Log.d("cache system", "paging done1,paged to" + unPagedArticles.getPagedTo());
                    return pagedPageView;
                }
                smartPage = new Page(activity);
            }
            smartPage.addArticle(article);
            j++;
            k++;
            unpagesArticlesList.remove(article);
            i--;

            if (unpagesArticlesList.size() == 0) {//分完了，还有吗?
                if (!onNoMoreArticle()) {
                    pagedPageView.add(smartPage);
                    unPagedArticles.setPagedTo(k);
                    return pagedPageView;
                }
            }
        }
        unPagedArticles.setPagedTo(k);
        //Log.d("cache system", "paging done2,paged to" + unPagedArticles.getPagedTo());
        return pagedPageView;
    }

    private boolean onNoMoreArticle() {
        if (noMoreArticleListener != null)
            try {
                noMoreArticleListener.onNoMoreArticle();
            } catch (NoMoreStatusException e) {//真的没了
                return false;
            }
        return true;
    }

    private class DoNothingListener implements NoMoreArticleListener {
        public void onNoMoreArticle() {
            //do nothing
        }
    }
}
