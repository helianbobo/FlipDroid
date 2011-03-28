package com.goal98.flipdroid.view;

import android.app.Activity;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.PagedArticles;
import com.goal98.flipdroid.model.UnPagedArticles;

import java.util.List;

public class SimplePagingStrategy implements PagingStrategy {

    private Activity activity;

    public void setNoMoreArticleListener(NoMoreArticleListener noMoreArticleListener) {
        this.noMoreArticleListener = noMoreArticleListener;
    }

    private NoMoreArticleListener noMoreArticleListener = new DoNothingListener();

    public SimplePagingStrategy(Activity activity) {
        this.activity = activity;
    }

    public PagedArticles doPaging(UnPagedArticles unPagedArticles) {
        PagedArticles pagedArticles = new PagedArticles();

        Page page = new Page(activity);

        List<Article> articles = unPagedArticles.getArticleList();
        if (articles.size() == 0 || unPagedArticles.getPagedTo() >= articles.size()) {//1第一次进来  2.正好分完，蛮巧的
            if (!onNoMoreArticle())
                return pagedArticles;
            articles = unPagedArticles.getArticleList();
        }

        for (int i = unPagedArticles.getPagedTo(); i < articles.size(); i++) {
            Article article = articles.get(i);

            if (!page.addArticle(article)) {//试试看能不能加进去
                page.settle();//搞定一页，重新做layout
                pagedArticles.add(page);
                if (pagedArticles.size() >= 2) {//预拿2页
                    unPagedArticles.setPagedTo(i);//下次从i开始再拿
                    return pagedArticles;
                }
                i--;//这页没加进去，下次继续加
                page = new Page(activity);
            } else {
                articles.remove(article);
                i--;

                if (articles.size() <= 0) {
                    if (articles.size() == 0) {//分完了，还有吗?
                        if (!onNoMoreArticle()) {
                            return pagedArticles;
                        }

                    }
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
