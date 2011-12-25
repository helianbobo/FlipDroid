package it.tika;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/24/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class CachedContent {
    Object content;
    Date lastModified;

    public CachedContent(Object content, Date lastModified) {
        this.content = content;
        this.lastModified = lastModified;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
