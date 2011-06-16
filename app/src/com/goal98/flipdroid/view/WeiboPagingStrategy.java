package com.goal98.flipdroid.view;

import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.PagedPageView;
import com.goal98.flipdroid.model.UnPagedArticles;

import java.util.List;

public class WeiboPagingStrategy implements PagingStrategy {

    private PageActivity activity;

    public void setNoMoreArticleListener(NoMoreArticleListener noMoreArticleListener) {
        this.noMoreArticleListener = noMoreArticleListener;
    }

    private NoMoreArticleListener noMoreArticleListener = new DoNothingListener();

    public WeiboPagingStrategy(PageActivity activity) {
        this.activity = activity;
    }

    public PagedPageView doPaging(UnPagedArticles unPagedArticles) {
        PagedPageView pagedPageView = new PagedPageView();

        SmartPage smartPage = new SmartPage(activity);

        List<Article> unpagedArticlesList = unPagedArticles.getArticleList();
        if (unpagedArticlesList.size() == 0 || unPagedArticles.getPagedTo() >= unpagedArticlesList.size()) {//1第一次进来  2.正好分完，蛮巧的
            if (!onNoMoreArticle())
                return pagedPageView;
            unpagedArticlesList = unPagedArticles.getArticleList();
        }

        for (int i = unPagedArticles.getPagedTo(); i < unpagedArticlesList.size(); i++) {
            Article article = unpagedArticlesList.get(i);

            if (!smartPage.addArticle(article)) {//试试看能不能加进去
                pagedPageView.add(smartPage);
                if (pagedPageView.size() >= 2) {//预拿2页
                    unPagedArticles.setPagedTo(i);//下次从i开始再拿
                    return pagedPageView;
                }
                i--;//这页没加进去，下次继续加
                smartPage = new SmartPage(activity);
            } else {
                unpagedArticlesList.remove(article);
                i--;

                if (unpagedArticlesList.size() == 0) {//分完了，还有吗?
                    if (!onNoMoreArticle()) {
                        return pagedPageView;
                    }

                }
            }
        }
        return pagedPageView;
    }

    private boolean onNoMoreArticle() {
        if (noMoreArticleListener != null)
            try {
                noMoreArticleListener.onNoMoreArticle();
            } catch (NoMoreStatusException e) {//真的没了
                return false;
            } finally {
            }
        return true;
    }

    private class DoNothingListener implements NoMoreArticleListener {
        public void onNoMoreArticle() {
            //do nothing
        }
    }
}
