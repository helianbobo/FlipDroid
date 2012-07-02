package com.goal98.flipdroid2.client;

import com.goal98.flipdroid2.model.Article;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedJSONParser {
    public List<TikaExtractResponse> getFeedsFromFeedJSON(String feedJSON){
        List<TikaExtractResponse> sourceResponses = new ArrayList<TikaExtractResponse>();
        if ("{}".equals(feedJSON)) {
            return sourceResponses;
        }
        try {
            JSONObject feedsJSON = new JSONObject(feedJSON);
            JSONArray feeds = feedsJSON.getJSONArray("abstracts");
            for (int i = 0; i < feeds.length(); i++) {
                JSONObject object = (JSONObject) feeds.get(i);
                TikaExtractResponse response = toTikaResponse(object);
                sourceResponses.add(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sourceResponses;
    }

    public TikaExtractResponse toTikaResponse(JSONObject s) {
        String title = "";
        try {
            title = (String) s.get("title");
        } catch (Exception e) {

        }
        String content = null;
        try {
            content = (String) s.get("content");
        } catch (JSONException e) {

        }
        Date date = new Date();
        try {
            long time = s.getLong("createDate");
            if (time != 0)
                date = new Date(time);
        } catch (JSONException e) {

        }
        String sourceURL = null;
        try {
            sourceURL = (String) s.get("sourceURL");
        } catch (JSONException e) {

        }
        List<String> images = new ArrayList<String>();
        try {
            JSONArray imagesArr = (JSONArray) s.get("images");
            for (int j = 0; j < imagesArr.length(); j++) {
                String imageURL = (String) imagesArr.get(j);
                images.add(imageURL);
            }
        } catch (JSONException e) {

        }
        TikaExtractResponse tikaExtractResponse = new TikaExtractResponse();
        tikaExtractResponse.setContent(content);
        tikaExtractResponse.setTitle(title);
        tikaExtractResponse.setImages(images);
        tikaExtractResponse.setSourceURL(sourceURL);
        tikaExtractResponse.setCreateDate(date);
        return tikaExtractResponse;
    }

    public void toArticle(TikaExtractResponse tikaExtractResponse, Article article)  {

        article.setAlreadyLoaded(true);
        article.setExpandable(true);
        article.setContent(tikaExtractResponse.getContent());
        article.setTitle(tikaExtractResponse.getTitle());
        article.setSourceURL(tikaExtractResponse.getSourceURL());
        article.setCreatedDate(tikaExtractResponse.getCreateDate());
        if (tikaExtractResponse.getImages().size() != 0) {
            try {
                article.setImageUrl(new URL(tikaExtractResponse.getImages().get(0)));
            } catch (MalformedURLException e) {

            }
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
    }
}