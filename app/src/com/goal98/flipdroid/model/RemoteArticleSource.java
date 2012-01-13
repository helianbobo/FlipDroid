package com.goal98.flipdroid.model;

import com.goal98.flipdroid.client.LastModifiedStampedResult;
import com.goal98.flipdroid.client.TikaClient;
import com.goal98.flipdroid.client.TikaClientException;
import com.goal98.flipdroid.client.TikaExtractResponse;
import com.goal98.flipdroid.model.cachesystem.BaseCacheableArticleSource;
import com.goal98.flipdroid.model.cachesystem.CacheToken;
import com.goal98.flipdroid.util.Constants;
import com.goal98.tika.common.TikaConstants;
import org.apache.commons.io.IOUtils;

import java.net.MalformedURLException;
import java.net.URL;
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
            if(feedJSON == null)
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
                setSourceType(article);
                article.setAlreadyLoaded(true);
                article.setExpandable(true);
                article.setContent(tikaExtractResponse.getContent());
                article.setTitle(tikaExtractResponse.getTitle());
                article.setSourceURL(tikaExtractResponse.getSourceURL());
                article.setCreatedDate(tikaExtractResponse.getCreateDate());
                if (tikaExtractResponse.getImages().size() != 0) {
                    article.setImageUrl(new URL(tikaExtractResponse.getImages().get(0)));
                }
                List<String> responsedImages = tikaExtractResponse.getImages();
                    for (int j = 0; j < responsedImages.size(); j++) {
                        String imageURL = responsedImages.get(j);
                        if (imageURL != null && imageURL.length() != 0) {
                            int sizeInfoBeginAt = imageURL.lastIndexOf("#");
                            String sizeInfoStr = imageURL.substring(sizeInfoBeginAt + 1);
                            imageURL = imageURL.substring(0, sizeInfoBeginAt);

                            if (j == 0) {//primary image
                                String[] sizeInfo = sizeInfoStr.split(",");
                                int width = Integer.valueOf(sizeInfo[0]);
                                int height = Integer.valueOf(sizeInfo[1]);
                                article.setImageWidth(width);
                                article.setImageHeight(height);
                                try {
                                    URL url = new URL(imageURL);
                                    article.setImageUrl(url);
//                                    article.loadPrimaryImage(imageURL, DeviceInfo.getInstance(activity));
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                            article.getImagesMap().put(imageURL, null);
                            article.getImages().add(imageURL);
                        }
                    }
                list.add(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected abstract void setSourceType(Article article);
}
