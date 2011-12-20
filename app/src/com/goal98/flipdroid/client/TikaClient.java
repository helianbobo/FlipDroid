package com.goal98.flipdroid.client;

import android.util.Log;
import com.goal98.tika.common.URLRawRepo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/20/11
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class TikaClient {

    private String TAG = this.getClass().getName();

    private String host;

    public TikaClient(String host) {
        this.host = host;
    }

    public LastModifiedStampedResult updateRecommendSource(String type, long lastModified) {
        String requestURL = null;
        requestURL = "http://" + host + "/v1/recommend?type=" + type;
        try {
            return readWithIMS(requestURL, lastModified);
        } catch (TikaClientException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TikaSourceResponse> searchSource(String keyword) throws TikaClientException {
        String url = null;
        String requestURL = null;
        try {
            requestURL = "http://" + host + "/v1/sources/search?kw=" + URLEncoder.encode(keyword.trim(), "utf-8");
        } catch (UnsupportedEncodingException e) {

        }
        String tikaResponse = read(requestURL);
        List<TikaSourceResponse> sourceResponses = new ArrayList<TikaSourceResponse>();
        if ("{}".equals(tikaResponse)) {
            return sourceResponses;
        }
        if(tikaResponse ==null)
            return sourceResponses;
        try {
            JSONArray sourceArray = new JSONArray(tikaResponse);
            for (int i = 0; i < sourceArray.length(); i++) {
                JSONObject object = (JSONObject) sourceArray.get(i);
                String id = object.getString("id");
                String accountType = object.getString("accountType");
                String name = object.getString("name");
                String cat = object.getString("cat");

                String imageURL = "";
                if (object.has("image_url"))
                    object.getString("image_url");
                String desc = object.getString("desc");
                String contentURL = "";
                if (object.has("content_url"))
                    contentURL = object.getString("content_url");

                TikaSourceResponse response = new TikaSourceResponse();
                response.setId(id);
                response.setAccountType(accountType);
                response.setImageURL(imageURL);
                response.setDesc(desc);
                response.setContentURL(contentURL);
                response.setName(name);
                response.setCat(cat);

                sourceResponses.add(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return sourceResponses;
    }

    public List<TikaExtractResponse> getFeedsFromFeedJSON(String feedJSON) throws TikaClientException {
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

    public String getFeedJSON(String sourceURL) throws TikaClientException {
        String requestURL = null;
        try {
            requestURL = "http://" + host + "/v1/feed?source=" + URLEncoder.encode(sourceURL, "utf-8");
        } catch (UnsupportedEncodingException e) {

        }
        return read(requestURL);
    }

    public TikaExtractResponse extract(String url) throws TikaClientException {
        JSONObject s = toJSONObject(url);
        TikaExtractResponse tikaExtractResponse = toTikaResponse(s);
        return tikaExtractResponse;
    }

    private TikaExtractResponse toTikaResponse(JSONObject s) {
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

    private JSONObject toJSONObject(String url) throws TikaClientException {
        String tikeResponse = null;
        try {
            tikeResponse = read("http://" + host + "/v1/url/abstract?url=" + URLEncoder.encode(url.trim(), "utf-8") + "&nocache=false");
        } catch (UnsupportedEncodingException e) {

        }
        JSONObject s = null;
        try {
            s = new JSONObject(tikeResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }

    public String read(String url) throws TikaClientException {
        return (String) readWithIMS(url, -1).getResult();
    }

    public LastModifiedStampedResult readWithIMS(String url, long lastModified) throws TikaClientException {
        HttpURLConnection u = null;
        try {
            u = (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException e) {
            return new LastModifiedStampedResult(-1,null);
        }
        if (lastModified != -1)
            u.setIfModifiedSince(lastModified);
        try {
            final int responseCode = u.getResponseCode();
            if (responseCode >= 200 && responseCode <= 299) {
                byte[] response = URLRawRepo.getInstance().fetch(u);
                final String s = new String(response, "utf-8");
                if (s != null &&(s.startsWith("{") || s.startsWith("[")) )// json
                    return new LastModifiedStampedResult(u.getLastModified(), s);
                return null;
            } else if (responseCode == 304) {
                return null;
            } else {
                throw new TikaClientException();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw new TikaClientException(url, e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }


}
