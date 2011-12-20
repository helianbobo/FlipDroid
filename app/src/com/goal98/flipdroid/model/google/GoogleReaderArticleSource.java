package com.goal98.flipdroid.model.google;

import android.util.Log;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.ArticleSource;
import com.goal98.flipdroid.util.Constants;
import com.goal98.tika.common.TikaConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/10/11
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleReaderArticleSource implements ArticleSource {
    private String sid;
    private String auth;
    private GReader gReader;
    private boolean noMoreToLoad;
    private List<Article> articles = new LinkedList<Article>();
    ;

    public GoogleReaderArticleSource(String sid, String auth) {
        this.sid = sid;
        this.auth = auth;
        gReader = new GReader(sid, auth);
        gReader.setEveryPage(20);
    }

    public Date lastModified() {
        return gReader.getLastModified();
    }

    public List<Article> getArticleList() {
        return articles;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean loadMore() {
        boolean result = false;
        //Log.d("GOOGLE_READER", "loading more");
        try {
            if (gReader.loadMore()) {
                //Log.d("GOOGLE_READER", "loaded");
                List<GreaderListItem> grFeeds = gReader.getNewRead();
                articles = new ArrayList<Article>();
                for (GreaderListItem item : grFeeds) {
                    ////System.out.println(item);
                    Article article = new Article();
                    article.setTitle(item.getTitle());
                    article.setStatus(item.getLink());
                    article.setAuthor(item.getAuthor());
                    article.setSourceType(TikaConstants.TYPE_GOOGLE_READER);
                    articles.add(article);
                }
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (!result)
            this.noMoreToLoad = true;

        return result;
    }

    public boolean isNoMoreToLoad() {
        return noMoreToLoad;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean getForceMagzine() {
        return true;
    }

    public boolean reset() {
        return false;
    }
}
