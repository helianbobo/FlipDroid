package com.goal98.flipdroid.util;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
        HttpGet request = new HttpGet("http://www.tika.it/v1/url/abstract?url="+url+"&nocache=true");

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
                s = new JSONObject(new String(baos.toByteArray(), "UTF-8"));
                String title = (String) s.get("title");
                String content = (String) s.get("content");
                TikaResponse tikaResponse = new TikaResponse();
                tikaResponse.setContent(content);
                tikaResponse.setTitle(title);
                return tikaResponse;
            }
        } catch (Exception e) {
            throw new TikaClientException(e);  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public static void main(String[] args) throws TikaClientException {
        TikaClient client = new TikaClient();
        TikaResponse response = client.extract("http://www.88799.com/show.php?tid=1797");
        System.out.println(response.getTitle());
    }
}
