package com.goal98.flipdroid.model.rss;

import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.ArticleSource;
import com.goal98.flipdroid.util.Constants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/6/11
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class RSSArticleSource implements ArticleSource {
    private String contentUrl;
    private String sourceName;
    private boolean loaded;
    private String sourceImage;
    private LinkedList list = new LinkedList<Article>();

    public RSSArticleSource(String contentUrl, String sourceName, String sourceImage) {
        this.contentUrl = contentUrl;
        this.sourceName = sourceName;
        this.sourceImage = sourceImage;
    }

    public Date lastModified() {
        return new Date();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Article> getArticleList() {
        System.out.println("getting article list");


        return list;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean loadMore() {
        System.out.println("this.contentUrl" + this.contentUrl);
        RssParser rp = new RssParser(this.contentUrl);
        rp.parse();
        RssParser.RssFeed feed = rp.getFeed();
        System.out.println("this.contentUrl" + this.contentUrl);
        System.out.println("sourceImage:" + sourceImage);
        ArrayList<RssParser.Item> items = feed.getItems();
        for (int i = 0; i < items.size(); i++) {
            RssParser.Item item = items.get(i);
            System.out.println("item" + item.title);
            Article article = new Article();
            if (item.author == null || item.author.trim().length() == 0)
                article.setAuthor(sourceName);
            else
                article.setAuthor(item.author);

            if (sourceImage != null)
                try {
                    article.setPortraitImageUrl(new URL(sourceImage));
                } catch (MalformedURLException e) {
                    return false;
                }
            article.setTitle(item.title);
            article.setStatus(item.link);
            article.setSourceType(Constants.TYPE_RSS);
            list.add(article);
        }
        loaded = true;
        System.out.println("LOADED" + loaded);
        return true;
    }

    public boolean isNoMoreToLoad() {
        System.out.println("loaded--:" + loaded);
        if (loaded)
            return true;
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
