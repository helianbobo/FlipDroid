package com.goal98.girl.view;

import com.goal98.girl.exception.NoMoreStatusException;

/**
 * Created by IntelliJ IDEA.
 * User: lsha6086
 * Date: 3/28/11
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public interface NoMoreArticleListener {
    public void onNoMoreArticle() throws NoMoreStatusException;
}
