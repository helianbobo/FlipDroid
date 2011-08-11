package com.goal98.flipdroid.model.rss;

import com.goal98.flipdroid.exception.NoNetworkException;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.OnSourceLoadedListener;
import com.goal98.flipdroid.model.cachesystem.CacheToken;
import com.goal98.flipdroid.model.cachesystem.CacheableArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCacheObject;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.EncodingDetector;
import com.goal98.flipdroid.util.StopWatch;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RSSArticleSource implements CacheableArticleSource {
    private String contentUrl;
    private String sourceName;
    private boolean loaded;
    private String sourceImage;
    private LinkedList list = new LinkedList<Article>();
    private InputStream content;
    private OnSourceLoadedListener listener;
    private byte[] cachedBytes;
    private byte[] loadedBytes;

    public RSSArticleSource(String contentUrl, String sourceName, String sourceImage) {
        this.contentUrl = contentUrl;
        this.sourceName = sourceName;
        this.sourceImage = sourceImage;
    }

    public RSSArticleSource(InputStream content, String sourceName, String sourceImage) {
        this.content = content;
        this.sourceName = sourceName;
        this.sourceImage = sourceImage;
    }

    public Date lastModified() {
        return new Date();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Article> getArticleList() {
        return list;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static long cachedTime = -1;

    public boolean loadMore() {
        RssParser rp;
        if (content == null)
            if (!loadFromSource())
                return false;
            else
                content = new ByteArrayInputStream(loadedBytes);

        rp = new RssParser(this.content);

        try {
            rp.parse();
        } catch (Exception e) {
            return false;
        }
        StopWatch sw = new StopWatch();
        sw.start("parse rss...");
        RssParser.RssFeed feed = rp.getFeed();
        ArrayList<RssParser.Item> items = feed.getItems();
        for (int i = 0; i < items.size(); i++) {
            RssParser.Item item = items.get(i);
            //System.out.println("item" + item.title);
            Article article = new Article();
            if (item.author == null || item.author.trim().length() == 0)
                article.setAuthor(sourceName);
            else {
                String[] authors = item.author.split(" ");
                article.setAuthor(authors[0]);
            }

            if (sourceImage != null) {
                try {
                    article.setPortraitImageUrl(new URL(sourceImage));
                } catch (MalformedURLException e) {

                }
            }
            article.setTitle(item.title);
            article.setStatus(item.link);
            //System.out.println("item.pubDate " + item.pubDate);
            Date date = new Date();
            if (item.pubDate != null) {
                item.pubDate = preFormat(item.pubDate);
                try {
                    date = new SimpleDateFormat("dd MM yyyy HH:mm:ss").parse(item.pubDate);
                } catch (ParseException e) {
                    try {
                        date = new SimpleDateFormat("dd MM yyyy HH:mm").parse(item.pubDate);
                    } catch (ParseException e1) {
                        try {
                            date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(item.pubDate);
                        } catch (ParseException e2) {
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.pubDate);
                            } catch (ParseException e3) {
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd").parse(item.pubDate);
                                } catch (ParseException e4) {

                                }
                            }
                        }
                    }
                }
            }
            article.setCreatedDate(date);
            article.setSourceType(Constants.TYPE_RSS);
            list.add(article);
        }
        sw.stopPrintReset();
        loaded = true;
        ////System.out.println("LOADED" + loaded);
        return true;
    }

    private boolean loadFromSource() {
        URL url = null;
        InputStream is = null;
        try {
            url = new URL(this.contentUrl);
            is = url.openConnection().getInputStream();


            loadedBytes = IOUtils.toByteArray(is);

            String encoding = EncodingDetector.detect(new ByteArrayInputStream(loadedBytes));
            if (listener != null)
                listener.onLoaded(new String(loadedBytes, encoding));


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {

                }
        }
        return true;
    }

    private String preFormat(String pubDate) {
        return pubDate.replace("Jan", "01")
                .replace("Feb", "02")
                .replace("Mar", "03")
                .replace("Apr", "04")
                .replace("May", "05")
                .replace("Jun", "06")
                .replace("Jul", "07")
                .replace("Aug", "08")
                .replace("Sep", "09")
                .replace("Oct", "10")
                .replace("Nov", "11")
                .replace("Dec", "12")
                .replace("Sun, ", "")
                .replace("Mon, ", "")
                .replace("Tue, ", "")
                .replace("Wen, ", "")
                .replace("Thu, ", "")
                .replace("Fri, ", "")
                .replace("Sat, ", "");
    }

    public boolean isNoMoreToLoad() {
        ////System.out.println("loaded--:" + loaded);
        if (loaded)
            return true;
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean getForceMagzine() {
        return true;
    }

    public boolean reset() {
        list.clear();
        loaded = false;
        return true;
    }

    public CacheToken getCacheToken() {
        CacheToken token = new CacheToken();
        token.setType(Constants.TYPE_RSS);
        token.setToken(this.contentUrl);
        return token;
    }

    public void fromCache(SourceCacheObject cachedObject) {
        cachedBytes = cachedObject.getContent().getBytes();
        this.content = new ByteArrayInputStream(cachedBytes);
    }

    public void registerOnLoadListener(OnSourceLoadedListener listener) {
        this.listener = listener;
    }

    public boolean loadLatestSource() throws NoNetworkException {
        if (!loadFromSource())
            return false;
        byte[] latestSource = this.loadedBytes;
        boolean needUpdate = false;
        if (cachedBytes != null) {
            for (int i = 0; i < latestSource.length; i++) {
                if (i >= cachedBytes.length) {
                    needUpdate = true;
                    break;
                }
                if (latestSource[i] != cachedBytes[i]) {
                    needUpdate = true;
                    break;
                }
            }
        }else{
            needUpdate = true;
        }

        if (needUpdate) {
            this.cachedBytes = loadedBytes;
            return true;
        }
        return false;
    }
}
