package com.goal98.flipdroid.view;

import com.goal98.flipdroid.model.PagedPageView;
import com.goal98.flipdroid.model.UnPagedArticles;

public interface PagingStrategy {
    public PagedPageView doPaging(UnPagedArticles articleList);

    void setNoMoreArticleListener(NoMoreArticleListener noMoreArticleListener);
}
