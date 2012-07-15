package com.goal98.girl.client;

import android.app.Activity;
import android.content.Intent;
import com.goal98.girl.activity.BindSinaActivity;
import com.goal98.girl.util.Constants;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.SortedSet;

public class OAuth {
    private CommonsHttpOAuthConsumer httpOauthConsumer;
    private OAuthProvider httpOauthprovider;
    public String consumerKey;
    public String consumerSecret;

    public OAuth() {
// 第一组：（App Key和App Secret）
// 这组参数就是本系列文本第一篇提到的建一个新的应用获取App Key和App Secret。
        this(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
    }

    public OAuth(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public Boolean RequestAccessToken(Activity activity, String callBackUrl, OnRetrieved onRetrieved) {
        Boolean ret = false;
        try {
            httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
            httpOauthprovider = new DefaultOAuthProvider("http://api.t.sina.com.cn/oauth/request_token", "http://api.t.sina.com.cn/oauth/access_token", "http://api.t.sina.com.cn/oauth/authorize");
            String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, callBackUrl);
            if(onRetrieved!=null)
                onRetrieved.onRetrieved();

            Intent intent = new Intent(activity, BindSinaActivity.class);
            intent.putExtra(BindSinaActivity.URL, authUrl);

            activity.startActivityForResult(intent,1);

            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public UserInfo GetAccessToken(String url) {
        UserInfo user = null;

        String verifier = url.substring(url.indexOf("oauth_verifier") + "oauth_verifier=".length());
//        try {
        httpOauthprovider.setOAuth10a(true);
        try {
            httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            try {
                httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
            } catch (Exception e1) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                try {
                    httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
                } catch (Exception e2) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return null;
//        }
        SortedSet<String> user_id = httpOauthprovider.getResponseParameters().get("user_id");
        if(user_id==null){
            return null;
        }
        String userId = user_id.first();
        String userKey = httpOauthConsumer.getToken();
        String userSecret = httpOauthConsumer.getTokenSecret();
        user = new UserInfo();
        user.setUserId(userId);
        user.setToken(userKey);
        user.setTokenSecret(userSecret);
        return user;
    }

    public HttpResponse SignRequest(String token, String tokenSecret, String url, List params) {
        HttpPost post = new HttpPost(url);
//HttpClient httpClient = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//关闭Expect:100-Continue握手
//100-Continue握手需谨慎使用，因为遇到不支持HTTP/1.1协议的服务器或者代理时会引起问题
        post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        return SignRequest(token, tokenSecret, post);
    }

    public HttpResponse SignRequest(String token, String tokenSecret, HttpPost post) {
        httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        httpOauthConsumer.setTokenWithSecret(token, tokenSecret);
        HttpResponse response = null;
        try {
            httpOauthConsumer.sign(post);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }
//取得HTTP response
        try {
            response = new DefaultHttpClient().execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public interface OnRetrieved {
        public void onRetrieved();
    }
}