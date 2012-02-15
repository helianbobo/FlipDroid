package com.goal98.flipdroid.model;

import com.goal98.flipdroid.client.*;
import com.goal98.flipdroid.model.cachesystem.BaseCacheableArticleSource;
import com.goal98.flipdroid.model.cachesystem.CacheToken;
import com.goal98.flipdroid.util.Constants;
import com.goal98.tika.common.TikaConstants;
import org.apache.commons.io.IOUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/30/11
 * Time: 9:46 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RemoteArticleSource extends BaseCacheableArticleSource {
    protected String remoteSourceToken;
    private String sourceName;
    private String sourceImage;
    TikaClient tikaClient;

    public String getImageUrl() {
        return sourceImage;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public String getAuthor() {
        return sourceName;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public RemoteArticleSource(String remoteSourceToken, String sourceName, String sourceImage) {
        this.remoteSourceToken = remoteSourceToken;
        this.sourceName = sourceName;
        this.sourceImage = sourceImage;
        tikaClient = new TikaClient(Constants.TIKA_HOST);
    }

    public abstract CacheToken getCacheToken();

    protected LastModifiedStampedResult getLatestSource() {
        try {
            LastModifiedStampedResult feedJSON = tikaClient.getFeedJSON(remoteSourceToken, lastModified);
            if (feedJSON == null)
                return null;
            return feedJSON;
        } catch (TikaClientException e) {
            return null;
        }
    }

    public Date lastModified() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean load() {
        try {
            byte[] contentsBytes = IOUtils.toByteArray(content);
            list.addAll(contentToArticles(new String(contentsBytes)));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Article> contentToArticles(String content)  {
        List<Article> articles = new ArrayList<Article>();
        List<TikaExtractResponse> tikaResponses = tikaClient.getFeedsFromFeedJSON(content);
        for (int i = 0; i < tikaResponses.size(); i++) {
            TikaExtractResponse tikaExtractResponse = tikaResponses.get(i);
            Article article = new Article();
            if (tikaExtractResponse.getAuthor() == null || tikaExtractResponse.getAuthor().trim().length() == 0)
                article.setAuthor(sourceName);
            else {
                String[] authors = tikaExtractResponse.getAuthor().split(" ");
                article.setAuthor(authors[0]);
            }

            if (sourceImage != null) {
                try {
                    article.setPortraitImageUrl(new URL(sourceImage));
                } catch (MalformedURLException e) {

                }
            }
            setSourceType(article);
            feedJSONParser.toArticle(tikaExtractResponse, article);
            articles.add(article);
        }
        return articles;
    }


    private final FeedJSONParser feedJSONParser = new FeedJSONParser();
    protected abstract void setSourceType(Article article);
}
