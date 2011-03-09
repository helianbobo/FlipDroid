package it.tika;

public class URLAbstract {
    private String url;
    private String title;
    private String content;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URLAbstract(String url, String title, String content) {
        this.url = url;
        this.title = title;
        this.content = content;

    }
}
