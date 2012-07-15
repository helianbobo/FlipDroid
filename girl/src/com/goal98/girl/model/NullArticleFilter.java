package com.goal98.girl.model;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/29/11
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class NullArticleFilter implements ArticleFilter {
    public boolean doFilter(Article article) {
        return true;
    }
}
