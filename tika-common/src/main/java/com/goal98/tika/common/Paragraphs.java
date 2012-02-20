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

            int startAt = 0;
//            int startAt = articleContent.indexOf("<");
//            int brAt = articleContent.indexOf("<br/>");
//            int linkAt = articleContent.indexOf("<a ");
//            while (brAt == startAt) {
//                startAt = articleContent.indexOf("<", brAt + 1);
//                brAt = articleContent.indexOf("<br/>", brAt + 1);
//            }
//            if (startAt > 0 && startAt!=linkAt) {
//                String text = articleContent.substring(0, startAt);
//                if (!text.replaceAll("<p>","").replaceAll("</p>","").equals("<br/>"))
//                    paragraphs.add(new Text(text));
//            }

            endAt = cutAt;
            String paragraph = articleContent.substring(startAt, endAt);
            System.out.println(paragraph);
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
    static Pattern startTag = Pattern.compile("(<[a-zA-Z][^>]+>)|</[^>]+>");

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
            if (group.startsWith("<strong ") || group.startsWith("</strong"))
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
        String a = "<br/>Current thread (0x0000000040124800):  GCTaskThread [stack: 0x0000000000000000,0x0000000000000000] [id=23997]<br/>出错的时候运行的是GCTaskThread,说明是GC的时候出错的,执行的是libjvm.so 代码<br/>R9 =0x00002b1fea3ea080<br/>0x00002b1fea3ea080: <offset 0x9b9080> in /data/dynasty/jdk/jre/lib/amd64/server/libjvm.so at 0x00002b1fe9a31000<br/>出错的代码段在libjvm.so 的偏移量0x9b9080位置(特别要注意这个偏移量，log中的其他内存地址都是不可比较的，只有偏移量,在同一版本的so库中，偏移量相同必然是表示的是同一代码)。多次crash都是这个偏移值。<br/>Heap<br/> par new generation   total 943744K, used 438097K [0x00000006f4000000, 0x0000000734000000, 0x0000000734000000)<br/>  eden space 838912K,  50% used [0x00000006f4000000, 0x000000070ded3cf8, 0x0000000727340000)<br/>  from space 104832K,  12% used [0x000000072d9a0000, 0x000000072e6a09d0, 0x0000000734000000)<br/>  to   space 104832K,   0% used [0x0000000727340000, 0x0000000727340000, 0x000000072d9a0000)<br/> concurrent mark-sweep generation total 3145728K, used 1573716K [0x0000000734000000, 0x00000007f4000000, 0x00000007f4000000)<br/> concurrent-mark-sweep perm gen total 196608K, used 86517K [0x00000007f4000000, 0x0000000800000000, 0x0000000800000000)<br/>内存一切正常,不是因为内存不足造成的.<br/>代码也找不到任何问题，直到在网上搜到了一个jdk的bug：<a href=http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7002666>http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7002666</a>，理解为在一定情况下（GC时指针压缩），java本地类库中某段逻辑会引用错误的内存地址。这个bug在jdk6u25版本时修复了。<br/>怀疑是这个问题导致，于是升级jdk版本至6u25，试用了一个多月，应用没有再无故crash。问题解决！<br/>官网引用了一段代码，可验证此bug（要求环境为linux64位系统）：<br/><pre>    static byte[] bb;    public static void main(String[] args) {        bb = new byte[1024 * 1024];//这句的意义在于促进GC        for (int i = 0; i < 25000; i++) {            Object[] a = test(TestJdk024bug.class, new TestJdk024bug());            if (a[0] != null) {                // The element should be null but if it's not then                // we've hit the bug.  This will most likely crash but                // at least throw an exception.                System.out.println(\"i = \" + i);                System.err.println(a[0]);                throw new InternalError(a[0].toString());            }        }        System.out.println(\"end........\");    }    public static Object[] test(Class c, Object o) {        // allocate an array small enough to be trigger the bug        Object[] a = (Object[])java.lang.reflect.Array.newInstance(c, 1);        return a;    }</pre><br/>在多线程情况下跑，会出现a[0] != null为true的情况<br/>我的启动参数是-server -Xms30M -Xmx30M -XX:NewSize=4m  <a href=javascript:;><img src=/images/sina.jpg >hack</img></a><a href=javascript:;><img src=/images/tec.jpg >hack</img></a><br/>";
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
