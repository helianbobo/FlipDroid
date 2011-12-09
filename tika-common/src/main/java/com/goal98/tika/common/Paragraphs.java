package com.goal98.tika.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/4/11
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class Paragraphs {


    List<TikaUIObject> paragraphs = new ArrayList<TikaUIObject>();

    public List<TikaUIObject> getParagraphs() {
        return paragraphs;
    }

    public String getImageSrc(String paragraph) {
        if (paragraph.startsWith("<img")) {
            return paragraph.substring(paragraph.indexOf("src=") + 4, paragraph.indexOf(ImageInfo.HACK_IMG));
        }
        return null;
    }

    public void toParagraph(String articleContent) {
        if (articleContent == null)
            return;
        int cutAt = 0;
        int endAt = -1;
        while ((cutAt = findNextClosingTag(articleContent)) != -1) {
            if (cutAt == -2) {
                paragraphs.add(new Text(articleContent));
                return;
            }

            int startAt = articleContent.indexOf("<");
            int brAt = articleContent.indexOf("<br/>");
            while (brAt == startAt) {
                startAt = articleContent.indexOf("<", brAt + 1);
                brAt = articleContent.indexOf("<br/>", brAt + 1);
            }
            if (startAt > 0) {
                paragraphs.add(new Text(articleContent.substring(0, startAt)));
            }

            endAt = articleContent.indexOf(">", cutAt);
            String paragraph = articleContent.substring(startAt, endAt + 1);
            if (paragraph.indexOf(ImageInfo.IMG_START) != -1) {
                paragraph = parseImg(paragraph);


                if (paragraph.trim().length() != 0) {
                    Matcher m;
                    boolean repeat = true;
                    while (repeat) {
                        repeat = false;
                        m = PAT_TAG_A_NO_TEXT.matcher(paragraph);
                        if (m.find()) {
                            repeat = true;
                            paragraph = m.replaceAll("");
                        }

                        m = PAT_TAG_STRONG_NO_TEXT.matcher(paragraph);
                        if (m.find()) {
                            repeat = true;
                            paragraph = m.replaceAll("");
                        }

                        m = PAT_TAG_HEADER_NO_TEXT.matcher(paragraph);
                        if (m.find()) {
                            repeat = true;
                            paragraph = m.replaceAll("");
                        }
                    }
                    toParagraph(paragraph);
                }
            } else
                paragraphs.add(new Text(paragraph));

            articleContent = articleContent.substring(endAt + 1);
        }

    }

    static Pattern startTag = Pattern.compile("(<[^>]+>)|</[^>]+>");

    private int findNextClosingTag(String articleContent) {
//        articleContent = "<img src=http://www.ifanr.com/wp-content/uploads/avatar/1347.JPG >hack</img>";
        if (articleContent.trim().startsWith("</"))
            return 0;
        if (articleContent.trim().length() == 0)
            return -1;
        if (articleContent.replaceAll("<br/>", "").replaceAll("<p>", "").replaceAll("</p>", "").trim().length() == 0)
            return -1;

        Matcher startMatcher = startTag.matcher(articleContent);
        int level = 0;
        while (startMatcher.find()) {
//            System.out.println(startMatcher.group());

            final String group = startMatcher.group();
            if (group.indexOf("<br/>") != -1) {
                continue;
            }
            if (group.indexOf("</") == -1) {
                level++;
            } else {
                level--;
            }
            if (level == 0) {
//                System.out.println(startMatcher.start());
                return startMatcher.start();
            }

        }
        System.out.println(articleContent);
        return -2;
    }

    private String parseImg(String paragraph) {
        while (paragraph.indexOf(ImageInfo.IMG_START) != -1) {
            int startOfImg = paragraph.indexOf(ImageInfo.IMG_START);
            int endOfImg = paragraph.indexOf(ImageInfo.HACK_IMG);

            int endIndex = endOfImg + ImageInfo.HACK_IMG.length();
            final ImageInfo imageInfo = new ImageInfo();
            imageInfo.setUnparsedInfo(paragraph.substring(startOfImg, endIndex));
            paragraphs.add(imageInfo);
            paragraph = paragraph.substring(0, startOfImg) + paragraph.substring(endIndex);
        }
        return paragraph;
    }

    public void retain(List<String> filteredImages, Map<String, ImageInfo> imageInfoMap) {
        Iterator<TikaUIObject> iter = paragraphs.iterator();
        while (iter.hasNext()) {
            TikaUIObject tikaUIObject = iter.next();
            if (tikaUIObject.getType().equals(TikaUIObject.TYPE_IMAGE)) {
                final ImageInfo tikaImage = (ImageInfo) tikaUIObject;
                String src = tikaImage.getUrl();
                boolean found = false;
                int i = 0;
                for (; i < filteredImages.size(); i++) {
                    String s = filteredImages.get(i);
                    if (s.indexOf(src) != -1) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    iter.remove();
                } else {
                    final String imageUrlWithInfo = filteredImages.get(i);
                    if (imageUrlWithInfo.indexOf("#") == -1) {
                        iter.remove();
                    }
                    String url = imageUrlWithInfo.substring(0, imageUrlWithInfo.indexOf("#"));
                    ImageInfo imageInfo = imageInfoMap.get(url);
                    if (imageInfo != null) {
                        tikaImage.setUrl(url);
                        tikaImage.setWidth(imageInfo.getWidth());
                        tikaImage.setHeight(imageInfo.getHeight());
                    } else {
                        iter.remove();
                    }
                }
            }
        }

    }

    public String toContent() {
        StringBuilder sb = new StringBuilder();
        Iterator<TikaUIObject> iter = paragraphs.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next().getOutput());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String a = "<img src=http://www.blogcdn.com/www.engadget.com/media/2011/12/lg2.jpg width=480 height=319 >hack</img><br/>LG <a href=http://cn.engadget.com/tag/OptimusPad/>Optimus Pad</a><p> ，或者也可以说是</p><a href=http://cn.engadget.com/tag/GSlate/> G Slate</a><p> 的后续平板电脑机型，已经出现在韩国的网站了，根据报道，这款新的平板设备将支持 </p><a href=http://cn.engadget.com/tag/LTE/>LTE</a><p> 的 4G 网络。用的是 8.9 英寸 WXGA 分辨率屏幕，高通的双核 1.5GHz 处理器，摒弃了前代双 500 万摄像头的 3D 拍摄设计，直接用一个后置 800 万像素的摄像头取代，当然还有一个视频摄像头。根据上图中左边的设备来看，这款机子采用的是 </p><a href=http://cn.engadget.com/tag/Honeycomb/>Honeycomb</a><p> 3.1 固件，做了一些定制改动，应该会有 </p><a href=http://cn.engadget.com/tag/IceCreamSandwich/>Ice Cream Sandwich</a><p> 冰激凌三明治升级。至于价格和发布时间目前还是处于保密状态，点击跳转还有一张图片可以看。<br/></p><img src=http://www.blogcdn.com/www.engadget.com/media/2011/12/ltelg.jpg width=480 height=319 >hack</img><p><br/></p><br/><p><br/><h4><a href=http://cn.engadget.com/2011/11/26/china-nokia-n9-hands-on/><br/></a></h4></p><p><br/><h4><a href=http://cn.engadget.com/2011/11/06/bbk-vivo-v1-hands-on/><br/></a></h4></p><p><br/></p><p><br/><h4><a href=http://cn.engadget.com/2011/10/16/smartq-ten2-ips-10-inch-android-tablet/><br/></a></h4></p>";
        Paragraphs p = new Paragraphs();
        p.toParagraph(a);
        System.out.println(p.getParagraphs());

    }

    class Text implements TikaUIObject {
        String body;

        Text(String body) {
            this.body = body;
        }

        public String getType() {
            return TikaUIObject.TYPE_TEXT;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String getObjectBody() {
            return body;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public String getOutput() {
            if (body.trim().length() == 0)
                return "";
            if (!body.startsWith("<"))
                return "<p>" + body + "</p>";

            Matcher m;
            String remains = body;
            boolean repeat = true;
            while (repeat) {
                repeat = false;
                m = PAT_TAG_NO_TEXT.matcher(remains);
                if (m.find()) {
                    repeat = true;
                    remains = m.replaceAll("");
                }
            }
            if (remains.trim().length() == 0)
                return "";

            return body;  //To change body of implemented methods use File | Settings | File Templates.
        }


    }

    private static final Pattern PAT_TAG_A_NO_TEXT = Pattern.compile("<a [^>]*?></a>");
    private static final Pattern PAT_TAG_STRONG_NO_TEXT = Pattern.compile("<strong></strong>");
    private static final Pattern PAT_TAG_HEADER_NO_TEXT = Pattern.compile("<h[1-6]+></h[1-6]+>");
    public static final Pattern PAT_TAG_NO_TEXT = Pattern.compile("<[^/][^>]*></[^>]*>");
}
