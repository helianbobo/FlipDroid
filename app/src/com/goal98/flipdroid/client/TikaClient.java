package com.goal98.flipdroid.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/20/11
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class TikaClient {
    public List<TikaSourceResponse> searchSource(String keyword) throws TikaClientException {
        String url = null;
        String requestURL = null;
        try {
            requestURL = "http://www.tika.it/v1/sources/search?kw=" + URLEncoder.encode(keyword.trim(), "utf-8");
        } catch (UnsupportedEncodingException e) {

        }
        String tikaResponse = read(requestURL);
        List<TikaSourceResponse> sourceResponses = new ArrayList<TikaSourceResponse>();
        if("{}".equals(tikaResponse)){
            return sourceResponses;
        }
        try {
            JSONArray sourceArray = new JSONArray(tikaResponse);
            for (int i = 0; i < sourceArray.length(); i++) {
                JSONObject object = (JSONObject) sourceArray.get(i);
                String id = object.getString("id");
                String accountType = object.getString("accountType");
                String name = object.getString("name");
                String cat = object.getString("cat");

                String imageURL = "";
                if(object.has("image_url"))
                    object.getString("image_url");
                String desc = object.getString("desc");
                String contentURL = "";
                if(object.has("content_url"))
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

    public TikaExtractResponse extract(String url) throws TikaClientException {
        JSONObject s = toJSONObject(url);
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
        return tikaExtractResponse;
    }

    private JSONObject toJSONObject(String url) throws TikaClientException {
        String tikeResponse = null;
        try {
            tikeResponse = read("http://www.tika.it/v1/url/abstract?url=" + URLEncoder.encode(url.trim(), "utf-8") + "&nocache=false");
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
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299) {
                InputStream is = response.getEntity().getContent();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int i;
                byte[] bytes = new byte[1024];
                while ((i = is.read(bytes)) != -1) {
                    baos.write(bytes, 0, i);
                    bytes = new byte[1024];
                }

                String s = new String(baos.toByteArray(), "utf-8");
                ////System.out.println(s);
                return s;
            } else {
                throw new TikaClientException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new TikaClientException(url, e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) throws TikaClientException {
        TikaClient client = new TikaClient();
        TikaExtractResponse extractResponse = client.extract("http://www.88799.com/show.php?tid=1797");
        ////System.out.println(extractResponse.getTitle());
    }
}
