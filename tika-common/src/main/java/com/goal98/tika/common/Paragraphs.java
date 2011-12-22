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
            int linkAt = articleContent.indexOf("<a ");
            while (brAt == startAt) {
                startAt = articleContent.indexOf("<", brAt + 1);
                brAt = articleContent.indexOf("<br/>", brAt + 1);
            }
            if (startAt > 0 && startAt!=linkAt) {
                String text = articleContent.substring(0, startAt);
                if (!text.replaceAll("<p>","").replaceAll("</p>","").equals("<br/>"))
                    paragraphs.add(new Text(text));
            }

            endAt = cutAt;
            String paragraph = articleContent.substring(startAt, endAt);
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
            } else{

                if(!paragraph.matches(PAT_TAG_NO_TEXT2.pattern()) && !paragraph.matches(PAT_TAG_NO_TEXT.pattern()))
                    paragraphs.add(new Text(paragraph));
            }

            articleContent = articleContent.substring(endAt);
        }

    }
    static Pattern startTag = Pattern.compile("(<[^>]+>)|</[^>]+>");

    private int findNextClosingTag(String articleContent) {
        if (articleContent.trim().length() == 0)
            return -1;
        if (articleContent.replaceAll("<br/>", "").replaceAll("<p>", "").replaceAll("</p>", "").trim().length() == 0)
            return -1;

        Matcher startMatcher = startTag.matcher(articleContent);
        int level = 0;
        while (startMatcher.find()) {
//            System.out.println(startMatcher.group());

            final String group = startMatcher.group();
//            System.out.println(group);
            if (group.indexOf("<br/>") != -1) {
                continue;
            }
            if (group.startsWith("<a ") || group.startsWith("</a"))
                continue;
            if (group.startsWith("</")) {
                level--;
                if (level < 0) {
                    level = 0;
                }
            } else {
                level++;
            }
            if (level == 0) {
//                System.out.println(startMatcher.start());
                return startMatcher.start() + group.length();
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
        String a = "<img src=http://www.blogcdn.com/www.engadget.com/media/2011/11/blackberry-london-verge-1321303731.jpg width=445 height=450 >hack</img><br/>如果以上的图片是真的话，这便是 RIM 第一部使用 QNX 系统的 BlackBerry 智能型手机，也就传说中的 BBX。这部看似来有点像 <a href=http://cn.engadget.com/2011/10/27/slug:%20porsche-design-p9981-blackberry-provides-a-long-awaited-design/>P'9981</a> 的手机，代号是 London，拥有相似的棱角设计，号称较 iPhone 4 更薄、更长一点。从图片看来，其拥有全触控屏幕，配合有点特色的界面，实在较现时的 BlackBerry OS 好看多了！<br/>回归现实，这也许只是一张概念图片、原型机图片或 PS 出来的图片，一切仍未被确认。根据 The Verge 的说法，这只是一台样版机，但真实的手机将配备 1.5GHz 双核心 TI OMAP 处理器、1GB RAM、16GB 内存，并最少拥有 800万像素镜头，200万像素前置镜头，预计将于 2012年推出。随着 BlackBerry 装置近月的失利，我们希望 London 快点出来，将 RIM 从困境中拯救出来呢！<br/><p><br/></p><p><br/></p><p><br/></p><p><br/><h4><a href=http://cn.engadget.com/2011/11/26/china-nokia-n9-hands-on/><br/></a></h4></p><p><br/><h4><a href=http://cn.engadget.com/2011/11/06/bbk-vivo-v1-hands-on/><br/></a></h4></p><p><br/></p><p><br/><h4><a href=http://cn.engadget.com/2011/10/16/smartq-ten2-ips-10-inch-android-tablet/><br/></a></h4></p>";
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
    public static final Pattern PAT_TAG_NO_TEXT = Pattern.compile("<[^/][^>]*>(<br/>)?</[^>]*>");
    public static final Pattern PAT_TAG_NO_TEXT2 = Pattern.compile("</[^>]*>(<br/>)?<[^/][^>]*>");
}
