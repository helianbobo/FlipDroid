package com.goal98.tika.common;

public class ImageInfo implements TikaUIObject {

    public static final String HACK_IMG = ">hack</img>";

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
        this.url = paragraph.substring(srcStarts + 4, paragraph.indexOf(" ",srcStarts));
        if(paragraph.indexOf("width")!=-1){
            final int widthStarts = paragraph.indexOf("width=");
            this.width = Integer.valueOf(paragraph.substring(widthStarts + 6, paragraph.indexOf(" ",widthStarts)));
        }
        if(paragraph.indexOf("height")!=-1){
            final int heightStarts = paragraph.indexOf("height=");
            this.height = Integer.valueOf(paragraph.substring(heightStarts + 7, paragraph.indexOf(" ",heightStarts)));
        }
    }
}