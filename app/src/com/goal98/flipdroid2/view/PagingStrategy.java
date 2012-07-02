package com.goal98.flipdroid2.view;

import com.goal98.flipdroid2.model.PagedPageView;
import com.goal98.flipdroid2.model.UnPagedArticles;

public interface PagingStrategy {
    public PagedPageView doPaging(UnPagedArticles articleList);

    void setNoMoreArticleListener(NoMoreArticleListener noMoreArticleListener);
}
