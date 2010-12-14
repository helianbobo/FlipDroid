package com.goal98.flipdroid.model.sina;

import android.util.Log;
import com.goal98.flipdroid.Constants;
import com.goal98.flipdroid.model.AbstractArticleSource;
import com.goal98.flipdroid.model.Article;
import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.util.List;

public class SinaArticleSource extends AbstractArticleSource {

    public SinaArticleSource() {

        System.setProperty("weibo4j.oauth.consumerKey", Constants.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Constants.CONSUMER_SECRET);

        Weibo.CONSUMER_KEY = Constants.CONSUMER_KEY;
        Weibo.CONSUMER_SECRET = Constants.CONSUMER_SECRET;

        testWeibo();
    }

    public List<Article> getArticleList() {
        return null;
    }

    private void testWeibo() {

        try {
            String id = "1702755335";
            Weibo weibo = getWeibo(false, new String[]{"13774256612","541116"});


            testGetUserTimeline(id, weibo);
        } catch (WeiboException e) {
            Log.e("Weibo", "Weibo error", e);
        }

    }

    private static void testGetUserTimeline(String id, Weibo weibo) throws WeiboException {
        List<Status> statuses = weibo.getUserTimeline(id);
        for (Status status : statuses) {
            Log.v("Weibo",status.getUser().getName() + ":" +
                    status.getText());
        }
    }

    private static Weibo getWeibo(boolean isOauth, String[] args) {
        Weibo weibo = new Weibo();
        if (isOauth) {
            weibo.setToken(args[0], args[1]);
        } else {
            weibo.setUserId(args[0]);
            weibo.setPassword(args[1]);
        }
        return weibo;
    }
}
