package com.goal98.girl.model.cachesystem;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-11
 * Time: 下午1:17
 * To change this template use File | Settings | File Templates.
 */
public interface SourceUpdateable {
    void notifyUpdating(CachedArticleSource cachedArticleSource);

    void notifyHasNew(CachedArticleSource cachedArticleSource);

    void notifyNoNew(CachedArticleSource cachedArticleSource);

    void notifyUpdateDone(CachedArticleSource cachedArticleSource);
}
