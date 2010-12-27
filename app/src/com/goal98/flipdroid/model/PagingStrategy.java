package com.goal98.flipdroid.model;

import java.util.List;

public interface PagingStrategy {

    public List<Page> doPaging(List<Article> articleList);

}
