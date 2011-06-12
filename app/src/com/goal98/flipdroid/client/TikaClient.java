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
    public TikaResponse extract(String url) throws TikaClientException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = null;
        try {
            request = new HttpGet("http://www.tika.it/v1/url/abstract?url=" + URLEncoder.encode(url.trim(), "utf-8") + "&nocache=false");
        } catch (UnsupportedEncodingException e) {

        }
        try {
            HttpResponse response = client.execute(request);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299) {
                InputStream is = response.getEntity().getContent();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int i = -1;
                byte[] bytes = new byte[1024];
                while ((i = is.read(bytes)) != -1) {
                    baos.write(bytes, 0, i);
                    bytes = new byte[1024];
                }
                JSONObject s = null;
                String tikeResponse = new String(baos.toByteArray(), "UTF-8");
                System.out.println(tikeResponse);
                s = new JSONObject(tikeResponse);
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
                    for (int j = 0; j < imagesArr.length(); j++){
                        String imageURL = (String) imagesArr.get(j);
                        System.out.println(imageURL);
                        images.add(imageURL);
                    }
                } catch (JSONException e) {

                }
                TikaResponse tikaResponse = new TikaResponse();
                tikaResponse.setContent(content);
                tikaResponse.setTitle(title);
                tikaResponse.setImages(images);
                return tikaResponse;
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
        TikaResponse response = client.extract("http://www.88799.com/show.php?tid=1797");
        System.out.println(response.getTitle());
    }
}
