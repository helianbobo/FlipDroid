package it.tika.mongodb.source;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-29
 * Time: 下午7:49
 * To change this template use File | Settings | File Templates.
 */
public class Source {
    String url;
    String id;
    String clazz;
    String type;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
