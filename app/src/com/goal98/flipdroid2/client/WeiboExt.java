package com.goal98.flipdroid2.client;

import weibo4j.*;
import weibo4j.http.Response;

import java.util.LinkedList;
import java.util.List;

public class WeiboExt extends Weibo {

    private String searchUserBaseURL = Configuration.getScheme() + "api.t.sina.com.cn/users/";

    public List<User> searchUser(Query query) throws WeiboException {
        try {
            Response response = get(searchUserBaseURL + "search.json", query.getParameters(), false);
            return User.constructUsers(response);
        } catch (WeiboException te) {
            if (404 == te.getStatusCode()) {
                return new LinkedList<User>();
            } else {
                throw te;
            }
        }
    }

}
