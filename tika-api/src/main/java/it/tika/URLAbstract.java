package it.tika;

import com.mongodb.BasicDBList;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class URLAbstract {
    private String url;
    private String title;
    private String content;
    private Charset charset;
    private String base;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    private List<String> images = new ArrayList<String>();

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public byte[] getRawContent() {
        return rawContent;
    }

    private byte[] rawContent;

    public URLAbstract() {
    }

    public URLAbstract(byte[] rawContent, Charset charset) {
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
        //content = content.replaceAll("</[Hh][1-6]>", "</h1>\n").replaceAll("<[Hh][1-6]>.*?", "").replaceAll("</[pP]>", "</p>\n").replaceAll("<[pP].*?>", "").replaceAll("(?i)<[bB][Rr].*?/?>", "<br />\n").replaceAll("<.+?>", "");

        //return Jsoup.parse(content).text();
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

    public URLAbstract(String url, String title, String content, BasicDBList images) {
        this.url = url;
        this.title = title;
        this.content = content;
        if (images != null)
            for (int i = 0; i < images.size(); i++)
                this.images.add((String) images.get(i));
    }

    public Charset getCharset() {
        return charset;
    }
}
