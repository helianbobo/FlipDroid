package com.goal98.flipdroid.model.sina;

import android.util.Log;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.model.AbstractArticleSource;
import com.goal98.flipdroid.model.Article;
import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SinaArticleSource extends AbstractArticleSource {

    private Weibo weibo;

    private String oauthToken;
    private String oauthTokenSecret;

    private String basicUser;
    private String basicPassword;

    private String sourceUserId;

    public SinaArticleSource(boolean useOauth, String param1, String param2, String sourceUserId) {

        System.setProperty("weibo4j.oauth.consumerKey", Constants.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Constants.CONSUMER_SECRET);

        Weibo.CONSUMER_KEY = Constants.CONSUMER_KEY;
        Weibo.CONSUMER_SECRET = Constants.CONSUMER_SECRET;

        if(useOauth){
            oauthToken = param1;
            oauthTokenSecret = param2;
        }else{
            basicUser = param1;
            basicPassword = param2;
        }
        this.sourceUserId = sourceUserId;

        initWeibo();
    }

    public List<Article> getArticleList() {

        List<Article> result = new LinkedList<Article>();

        try {
            List<Status> statuses = weibo.getUserTimeline(sourceUserId);
            for (int i = 0; i < statuses.size(); i++) {
                Status status = statuses.get(i);
                Article article = new Article();
                article.setContent(status.getText());
                article.setTitle(status.getUser().getName());
                article.setImageUrl(status.getUser().getProfileImageURL());
                result.add(article);
            }

            this.lastModified = new Date();
        } catch (WeiboException e) {
            Log.e(this.getClass().getName(),e.getMessage(),e);
        }
        return result;
    }

    private void initWeibo() {
        weibo = new Weibo();
        if (oauthToken != null) {
            weibo.setToken(oauthToken, oauthTokenSecret);
        } else {
            weibo.setUserId(basicUser);
            weibo.setPassword(basicPassword);
        }
    }
}
