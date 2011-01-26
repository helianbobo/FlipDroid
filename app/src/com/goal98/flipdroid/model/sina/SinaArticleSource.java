package com.goal98.flipdroid.model.sina;

import android.util.Log;
import com.goal98.flipdroid.client.WeiboExt;
import com.goal98.flipdroid.exception.NoNetworkException;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.model.AbstractArticleSource;
import com.goal98.flipdroid.model.Article;
import weibo4j.*;

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

    public SinaArticleSource(boolean useOauth, String param1, String param2, String sourceUserId) {

        System.setProperty("weibo4j.oauth.consumerKey", Constants.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Constants.CONSUMER_SECRET);

        Weibo.CONSUMER_KEY = Constants.CONSUMER_KEY;
        Weibo.CONSUMER_SECRET = Constants.CONSUMER_SECRET;

        if (useOauth) {
            oauthToken = param1;
            oauthTokenSecret = param2;
        } else {
            basicUser = param1;
            basicPassword = param2;
        }
        this.sourceUserId = sourceUserId;

        if(weibo == null)
            initWeibo();
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    private void loadArticle() {

        if(weibo == null)
            initWeibo();

        try {
            List<Status> statuses;
            Paging paging = new Paging(pageLoaded + 1);
            if (Constants.SOURCE_HOME.equals(sourceUserId)) {
                statuses = weibo.getHomeTimeline(paging);
            } else {
                statuses = weibo.getUserTimeline(sourceUserId, paging);
            }
            for (int i = 0; i < statuses.size(); i++) {
                Status status = statuses.get(i);
                Article article = new Article();
                article.setContent(status.getText());
                article.setTitle(status.getUser().getName());
                article.setPortraitImageUrl(status.getUser().getProfileImageURL());
                articleList.add(article);
            }

            pageLoaded++;

            this.lastModified = new Date();
        } catch (WeiboException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
            throw new NoNetworkException(e);
        }
    }

    private void initWeibo() {
        weibo = new WeiboExt ();
        weibo.setHttpConnectionTimeout(5000);
        if (oauthToken != null) {
            weibo.setToken(oauthToken, oauthTokenSecret);
        } else {
            weibo.setUserId(basicUser);
            weibo.setPassword(basicPassword);
        }
    }

    public void loadMore() {
        loadArticle();
    }

    public List<Source> searchSource(String queryStr){

        List<Source> result = new LinkedList<Source>();

        try {
            Query query = new Query(queryStr);
            List<User> userList = weibo.searchUser(query);
            if(userList != null){
                for (int i = 0; i < userList.size(); i++) {
                    User user = userList.get(i);
                    Source source = new Source();
                    source.setName(user.getName());
                    source.setId(String.valueOf(user.getId()));
                    source.setDesc(user.getDescription());
                    source.setAccountType(Constants.TYPE_SINA_WEIBO);
                    source.setImageUrl(user.getProfileImageURL().toString());
                    result.add(source);
                }
            }

        } catch (WeiboException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }

        return result;
    }
}
