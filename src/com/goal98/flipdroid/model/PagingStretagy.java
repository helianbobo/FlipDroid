package com.goal98.flipdroid.model;

import java.util.List;

public interface PagingStretagy {

    public List<Page> doPaging(List<Article> articleList);

}
