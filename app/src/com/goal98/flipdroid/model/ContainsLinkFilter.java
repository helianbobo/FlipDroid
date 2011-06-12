package com.goal98.flipdroid.model;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/29/11
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContainsLinkFilter extends FilterChain{

    public ContainsLinkFilter(ArticleFilter filter) {
        super(filter);
    }

    public boolean doFilter(Article article) {
        return article.hasLink() && nextFilter.doFilter(article);
    }
}
