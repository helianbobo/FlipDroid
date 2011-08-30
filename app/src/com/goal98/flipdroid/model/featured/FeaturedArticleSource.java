package com.goal98.flipdroid.model.featured;

import com.goal98.flipdroid.client.TikaClient;
import com.goal98.flipdroid.client.TikaClientException;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.cachesystem.BaseCacheableArticleSource;
import com.goal98.flipdroid.model.cachesystem.CacheToken;
import com.goal98.flipdroid.model.cachesystem.CacheableArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCacheObject;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.TikaResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
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
public class FeaturedArticleSource extends BaseCacheableArticleSource {
    private String feedURL;
    private String sourceName;
    private String sourceImage;
    TikaClient tikaClient;
    private List<TikaExtractResponse> responses = new ArrayList<TikaExtractResponse>();

    public FeaturedArticleSource(String feedURL, String sourceName, String sourceImage) {
        this.feedURL = feedURL;
        this.sourceName = sourceName;
        this.sourceImage = sourceImage;
        tikaClient = new TikaClient(Constants.TIKA_HOST);
    }

    public CacheToken getCacheToken() {
        CacheToken token = new CacheToken();
        token.setType(Constants.TYPE_FEATURED);
        token.setToken(this.feedURL);
        return token;
    }

    protected byte[] getLatestSource() {
        try {
            return tikaClient.getFeedJSON(feedURL).getBytes();
        } catch (TikaClientException e) {
            return new byte[0];
        }
    }

    public Date lastModified() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean load() {
        try {
            byte[] contentsBytes = IOUtils.toByteArray(content);
            List<TikaExtractResponse> tikaResponses = tikaClient.getFeedsFromFeedJSON(new String(contentsBytes));
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
                article.setSourceType(Constants.TYPE_FEATURED);
                article.setAlreadyLoaded(true);
                article.setExpandable(true);
                article.setContent(tikaExtractResponse.getContent());
                article.setTitle(tikaExtractResponse.getTitle());
                if (tikaExtractResponse.getImages().size() != 0) {
                    article.setImageUrl(new URL(tikaExtractResponse.getImages().get(0)));
                }
                list.add(article);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TikaClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
