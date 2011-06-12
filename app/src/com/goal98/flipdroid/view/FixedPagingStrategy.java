package com.goal98.flipdroid.view;

import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.PagedArticles;
import com.goal98.flipdroid.model.UnPagedArticles;

import java.util.List;

public class FixedPagingStrategy implements PagingStrategy {

    private PageActivity activity;

    public void setNoMoreArticleListener(NoMoreArticleListener noMoreArticleListener) {
        this.noMoreArticleListener = noMoreArticleListener;
    }

    private NoMoreArticleListener noMoreArticleListener = new DoNothingListener();
    private int number;

    public FixedPagingStrategy(PageActivity activity, int number) {
        this.activity = activity;
        this.number = number;
    }

    public PagedArticles doPaging(UnPagedArticles unPagedArticles) {
        PagedArticles pagedArticles = new PagedArticles();


        List<Article> unpagesArticlesList = unPagedArticles.getArticleList();
        if (unpagesArticlesList.size() == 0 || unPagedArticles.getPagedTo() >= unpagesArticlesList.size()) {//1第一次进来  2.正好分完，蛮巧的
            if (!onNoMoreArticle())
                return pagedArticles;
            unpagesArticlesList = unPagedArticles.getArticleList();
        }
        Page smartPage = new Page(activity);
        for (int i = unPagedArticles.getPagedTo(), j = 0; i < unpagesArticlesList.size(); i++) {
            Article article = unpagesArticlesList.get(i);
            if (j == number) {
                j = 0;
                pagedArticles.add(smartPage);//这页加好了
                if (pagedArticles.size() >= 2) {//预拿2页
                    unPagedArticles.setPagedTo(i);//下次从i开始再拿
                    return pagedArticles;
                }
                smartPage = new Page(activity);
            }
            smartPage.addArticle(article);
            j++;
            unpagesArticlesList.remove(article);
            i--;

            if (unpagesArticlesList.size() == 0) {//分完了，还有吗?
                if (!onNoMoreArticle()) {
                    return pagedArticles;
                }
            }
        }

        return pagedArticles;
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
