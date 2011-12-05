package com.goal98.flipdroid.model.sina;

import android.util.Log;
import com.goal98.flipdroid.client.WeiboExt;
import com.goal98.flipdroid.model.GroupedSource;
import com.goal98.flipdroid.model.SearchSource;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.SourceRepo;
import com.goal98.flipdroid.util.Constants;
import weibo4j.Query;
import weibo4j.User;
import weibo4j.Weibo;
import weibo4j.WeiboException;


import java.util.*;

public class SinaSearchSource implements SearchSource {

    private WeiboExt weibo;

    private String oauthToken;
    private String oauthTokenSecret;

    private String basicUser;
    private String basicPassword;

    public SinaSearchSource(boolean useOauth, String param1, String param2, String sourceUserId) {
        System.setProperty("weibo4j.oauth.consumerKey", Constants.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Constants.CONSUMER_SECRET);

        Weibo.CONSUMER_KEY = Constants.CONSUMER_KEY;
        Weibo.CONSUMER_SECRET = Constants.CONSUMER_SECRET;

        if (useOauth) {
            oauthToken = param1;
            oauthTokenSecret = param2;
        } else {
            basicUser = param1;
            basicPassword = param2;
        }
        if (weibo == null)
            initWeibo();
    }

    private void initWeibo() {
        weibo = new WeiboExt();
        weibo.setHttpConnectionTimeout(5000);
        if (oauthToken != null) {
            weibo.setToken(oauthToken, oauthTokenSecret);
        } else {
            weibo.setUserId(basicUser);
            weibo.setPassword(basicPassword);
        }
    }

    public GroupedSource searchSource(String queryStr) {
        List<Map<String, String>> sourceList = new ArrayList<Map<String, String>>();

        try {
            Query query = new Query();
            query.setQ(queryStr);
            List<User> userList = weibo.searchUser(query);
            if (userList != null) {
                for (int i = 0; i < userList.size(); i++) {
                    User user = userList.get(i);
                    Map<String, String> result = new HashMap<String, String>();
                    result.put(Source.KEY_SOURCE_NAME, user.getName());
                    result.put(Source.KEY_SOURCE_ID, String.valueOf(user.getId()));
                    result.put(Source.KEY_SOURCE_DESC, user.getDescription());
                    result.put(Source.KEY_SOURCE_TYPE, Constants.TYPE_SINA_WEIBO);
                    result.put(Source.KEY_IMAGE_URL, user.getProfileImageURL().toString());
                    result.put(Source.KEY_CAT, "新浪微博");

                    sourceList.add(result);
                }
            }

        } catch (WeiboException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }

        return SourceRepo.group(sourceList);
    }
}
