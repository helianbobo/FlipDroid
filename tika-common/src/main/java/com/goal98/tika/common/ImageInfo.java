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
        String content = "<img src=http://www.blogcdn.com/www.engadget.com/media/2011/12/samsungsmarttv00.jpgc72ce73b-d7af-431d-91e9-8075bf4a9280large.jpg width=438 height=423 >hack</img>显然 <a href=http://cn.engadget.com/tag/samsung/>Samsung</a> 还是想偷偷卖个关子，这个在 Youtube 上的预告片段，其实只有在结尾时才仅有几行文字出现，所以我们跟各位一样知道的也并不多。三星刚刚释出「Through the years」（中译：那些年，我们看过的电视！？）的广告，把超过 50 年的电视产品史浓缩为一个片段（例如：黑白转换为彩色的时代、常常要调整方位的兔耳型天线、DVR、3D 电视... 等），并且用「<a href=http://www.engadget.com/2011/09/01/samsungs-smart-tv-update-will-feature-youtube-3d-videos/>Smart TV</a> 的未来体验」做为结尾，还预告了其将在次月 CES 来到的消息。不过如同以往，我们也<a href=http://www.engadget.com/2011/11/29/engadget-the-official-online-news-source-of-ces-2012-and-the-ce/>将在现场</a>，所以大家就先看看来源的影片，预想一下猜猜究竟出现的会是什么样的产品，再等候我们的后续报导吧！<br/><p><br/></p>";

        Paragraphs paragraphs = new Paragraphs();
        paragraphs.toParagraph(content);
        System.out.println(paragraphs);
    }
}