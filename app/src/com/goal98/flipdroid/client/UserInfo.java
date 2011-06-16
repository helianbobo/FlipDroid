package com.goal98.flipdroid.client;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6/15/11
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserInfo {
    private String tokenSecret;
    private String userId;
    private String token;

    public String getToken() {
        return token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }
}
