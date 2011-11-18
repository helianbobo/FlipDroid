package com.goal98.tika.common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/18/11
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class URLConnectionUtil {
    public static HttpURLConnection decorateURLConnection(URL touchingImageURL) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) (touchingImageURL
                .openConnection());
        httpConnection.setConnectTimeout(2000);
        httpConnection.setReadTimeout(3000);

        httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
        return httpConnection;
    }
}
