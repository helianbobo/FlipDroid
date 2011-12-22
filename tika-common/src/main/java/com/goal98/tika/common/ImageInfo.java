package com.goal98.tika.common;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageInfo implements TikaUIObject {

    public static final String HACK_IMG = ">hack</img>";
    public static final String IMG_START = "<img";
    private int size;
    private int width;
    private int height;
    private String url;

    public ImageInfo(String url) {
        this.url = url;
    }

    public ImageInfo(int width, int height, String url) {
        this.width = width;
        this.height = height;
        this.url = url;
    }

    public ImageInfo() {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getType() {
        return TikaUIObject.TYPE_IMAGE;
    }

    public String getObjectBody() {
        return url;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getOutput() {
        return "<img src=" + url + " width=" + width + " height=" + height + " >hack</img>";
    }

    public void setUnparsedInfo(String paragraph) {
        final int srcStarts = paragraph.indexOf("src=");
        this.url = paragraph.substring(srcStarts + 4, paragraph.indexOf(" ", srcStarts));
        final int widthStarts = paragraph.indexOf(" width=");
        if (widthStarts != -1) {
            this.width = Integer.valueOf(paragraph.substring(widthStarts + 7, paragraph.indexOf(" ", widthStarts + 1)));
        }
        final int heightStarts = paragraph.indexOf(" height=");
        if (heightStarts != -1) {
            this.height = Integer.valueOf(paragraph.substring(heightStarts + 8, paragraph.indexOf(" ", heightStarts+1)));
        }
    }

    public static void main(String[] args) {
        String content = "<img src=http://www.blogcdn.com/www.engadget.com/media/2011/11/dsc02377-1321224796.jpg width=600 height=399 >hack</img><br/>拥有 <a href=http://www.engadget.com/2011/11/14/amazon-kindle-fire-review/>Kindle Fire</a> 的朋友应该有注意到，当你心血来潮想在其内建的 <a href=http://www.engadget.com/2011/11/18/behind-amazons-silk-browser-lurks-a-really-fast-supercomputer/>Slik 浏览器</a>中看看 Android Market 网页时，才发现该网页在这块平板上竟是被封锁的状态，虽然其实是会被导向到 Amazon App Store，但这样近似浏览器绑架的行为，仍让人质疑该公司的做法是否失当。当然我们可以合理推测，其原意是希望消费者不要被非原厂支持的商店所混淆。但很显然地，会去阻挡与重新导向用户观赏内容的公司，怎可能奢求得到消费者的信任？（哪天我们浏览的个人财物信息会出什么问题也很难说）不过幸好，在最近一次的<a href=http://www.engadget.com/2011/12/20/kindle-fires-6-2-1-update-breaks-root-disables-superoneclick-u/>更新中</a>，该公司已经解除这样的限制了，所以你已经可以顺利在 Fire 上浏览该网址了，不过... 还是别期待可以直接在该网页安装 Apps 啰（不<a href=http://www.engadget.com/2011/11/29/kindle-fire-gets-first-taste-of-cm7-needs-work-on-its-hand-eye/>破解</a>的话啦！）。<br/><p><br/></p>";

        Paragraphs paragraphs = new Paragraphs();
        paragraphs.toParagraph(content);
        System.out.println(paragraphs);
    }
}