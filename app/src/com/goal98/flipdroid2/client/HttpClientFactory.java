package com.goal98.flipdroid2.client;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 11-8-7
 * Time: 下午12:25
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientFactory {
    static HttpClient client;
    public static final int MAX_TOTAL_CONNECTIONS = 15;
    public static final int MAX_PER_ROUTE = 1;

    public static synchronized HttpClient getHttpClient() {
        if (client == null) {
            HttpParams params = new BasicHttpParams();

            ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);
            ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_PER_ROUTE);
            ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            ClientConnectionManager cm = new ThreadSafeClientConnManager(params, registry);
            client = new DefaultHttpClient(cm, params);
        }
        System.out.println("return cached client");
        return client;

    }
}
