package com.goal98.girl.model;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/29/11
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class FilterChain implements ArticleFilter {
    protected ArticleFilter nextFilter;

    FilterChain(ArticleFilter filter) {
        this.nextFilter = filter;
    }
}
