package com.goal98.flipdroid.view;

import com.goal98.flipdroid.model.PagedArticles;
import com.goal98.flipdroid.model.UnPagedArticles;

public interface PagingStrategy {
    public PagedArticles doPaging(UnPagedArticles articleList);
}
