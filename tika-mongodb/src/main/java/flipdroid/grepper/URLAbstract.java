package flipdroid.grepper;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class URLAbstract {
    private String url;
    private String title;
    private String content;
    private Charset charset;
    private String base;
    private List<String> images = new ArrayList<String>();
    private String referencedFrom;
    private String sogouClass;

    public Date getCreateDate() {
        return createDate;
    }

    private Date createDate;

    public String getReferencedFrom() {
        return referencedFrom;
    }

    public void setReferencedFrom(String referencedFrom) {
        this.referencedFrom = referencedFrom;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }



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

    public List getIndexURL() {
        return indexURL;
    }

    public void setIndexURL(List indexURL) {
        this.indexURL = indexURL;
    }

    private List indexURL = new ArrayList();

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

    public URLAbstract(String url, String title, String content, List<String> images) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.images = images;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URLAbstract that = (URLAbstract) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    public void setClazz(String sogouClass) {
        this.sogouClass = sogouClass;
    }

    public String getSogouClass() {
        return sogouClass;
    }

    public void setSogouClass(String sogouClass) {
        this.sogouClass = sogouClass;
    }
}
