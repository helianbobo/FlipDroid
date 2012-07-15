package com.goal98.girl.view;

import com.goal98.girl.model.PagedPageView;
import com.goal98.girl.model.UnPagedArticles;

public interface PagingStrategy {
    public PagedPageView doPaging(UnPagedArticles articleList);

    void setNoMoreArticleListener(NoMoreArticleListener noMoreArticleListener);
}
