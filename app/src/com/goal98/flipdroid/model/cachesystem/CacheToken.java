package com.goal98.flipdroid.model.cachesystem;

import com.goal98.flipdroid.activity.SourceItem;
import com.goal98.flipdroid.util.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-29
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
public class CacheToken {
    String type;
    String token;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean match(SourceItem item) {
        if (Constants.TYPE_RSS.equals(item.getSourceType())) {
            return item.getSourceURL().equals(token);
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }
}
