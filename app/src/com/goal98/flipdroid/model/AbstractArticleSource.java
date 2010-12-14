package com.goal98.flipdroid.model;

import java.util.Date;

public abstract class AbstractArticleSource implements ArticleSource{

    protected Date lastModified;

    public Date lastModified() {
        return lastModified;
    }
}
