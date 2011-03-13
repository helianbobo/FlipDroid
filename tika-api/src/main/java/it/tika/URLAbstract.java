package it.tika;

import java.nio.charset.Charset;

public class URLAbstract {
    private String url;
    private String title;
    private String content;
    private Charset charset;

    public byte[] getRawContent() {
        return rawContent;
    }

    private byte[] rawContent;

    public URLAbstract() {
    }
    
    public URLAbstract(byte[] rawContent, Charset charset){
        this.rawContent = rawContent;
        this.charset = charset;
    }

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

    public Charset getCharset() {
        return charset;
    }
}
