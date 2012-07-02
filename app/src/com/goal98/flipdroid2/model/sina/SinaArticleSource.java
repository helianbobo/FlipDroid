package com.goal98.flipdroid2.model.sina;

import com.goal98.flipdroid2.client.WeiboExt;
import com.goal98.flipdroid2.exception.NoNetworkException;
import com.goal98.flipdroid2.model.AbstractArticleSource;
import com.goal98.flipdroid2.model.Article;
import com.goal98.flipdroid2.model.ArticleFilter;
import com.goal98.flipdroid2.util.Constants;
import com.goal98.tika.common.TikaConstants;
import weibo4j.Paging;
import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SinaArticleSource extends AbstractArticleSource {

    private WeiboExt weibo;

    private String oauthToken;
    private String oauthTokenSecret;

    private String basicUser;
    private String basicPassword;

    private String sourceUserId;

    private int pageLoaded = 0;

    private List<Article> articleList = new LinkedList<Article>();
    private ArticleFilter filter;

    public SinaArticleSource(boolean useOauth, String userId, String token, String sourceUserId, ArticleFilter filter) {
        this.filter = filter;
        System.setProperty("weibo4j.oauth.consumerKey", Constants.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Constants.CONSUMER_SECRET);

        Weibo.CONSUMER_KEY = Constants.CONSUMER_KEY;
        Weibo.CONSUMER_SECRET = Constants.CONSUMER_SECRET;

        if (useOauth) {
            oauthToken = userId;
            oauthTokenSecret = token;
        } else {
            basicUser = userId;
            basicPassword = token;
        }
        this.sourceUserId = sourceUserId;

        if (weibo == null)
            initWeibo();
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    private synchronized boolean loadArticle() {
        if (weibo == null)
            initWeibo();

        try {
            List<Status> statuses;
            Paging paging = new Paging(pageLoaded + 1);
            paging.setCount(10);
            if (Constants.SOURCE_HOME.equals(sourceUserId)) {
                statuses = weibo.getHomeTimeline(paging);
            } else {
                statuses = weibo.getUserTimeline(sourceUserId, paging);
            }
            if (statuses == null || statuses.size() == 0)
                return false;

            for (int i = 0; i < statuses.size(); i++) {
                Status status = statuses.get(i);
                Article article = new Article();
                article.setSourceType(TikaConstants.TYPE_SINA_WEIBO);
                try {
                    String original_pic = status.getOriginal_pic();
                    if (original_pic != null && original_pic.length() != 0)
                        article.setImageUrl(new URL(original_pic));
                } catch (MalformedURLException e) {

                }
                article.setStatus(status.getText());
                article.setContent(status.getText());
                article.setAuthor(status.getUser().getName());
                article.setCreatedDate(status.getCreatedAt());
                article.setPortraitImageUrl(status.getUser().getProfileImageURL());
                article.setStatusId(status.getId());

                if (filter.doFilter(article)) {
                    articleList.add(article);
                    if (article.hasLink()){
                        article.setSourceURL(article.extractURL());
                    }
                }
            }

            pageLoaded++;

            this.lastModified = new Date();
            return true;
        } catch (WeiboException e) {
            throw new NoNetworkException(e);
        }
    }

    private void initWeibo() {
        weibo = new WeiboExt();
        weibo.setHttpConnectionTimeout(5000);
        if (oauthToken != null) {
            weibo.setToken(oauthToken, oauthTokenSecret);
        } else {
            weibo.setUserId(basicUser);
            weibo.setPassword(basicPassword);
        }
    }

    public boolean loadMore() {
        boolean result = false;
        try {
            result = loadArticle();
        } catch (Exception e) {
            return false;
        }
        //Log.d("cache system", "loading more " + (result ? "succeed" : "failed"));
        if (!result)
            this.noMoreToLoad = true;

        return result;
    }

    volatile boolean noMoreToLoad;

    public boolean isNoMoreToLoad() {
        return noMoreToLoad;
    }

    public boolean getForceMagzine() {
        return false;
    }

    public boolean reset() {
        return false;
    }
}
