package com.goal98.flipdroid2.model;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/29/11
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContainsLinkOrImageFilter extends FilterChain{

    public ContainsLinkOrImageFilter(ArticleFilter filter) {
        super(filter);
    }

    public boolean doFilter(Article article) {
        return (article.hasLink() || article.getImageUrl() != null) && nextFilter.doFilter(article);
    }
}
