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
        String a = "<h1><a href=http://sports.sina.com.cn/><img src=http://i3.sinaimg.cn/ty/main/logo/logo_home_sports_nonike.gif >hack</img></a></h1><p>　　新浪体育讯　虽然临时协议达成，但对洛杉矶湖人<a href=http://weibo.com/lakersnews?zw=sports>(微博)</a>来说，他们的麻烦才刚刚开始。在洛杉矶当地媒体《洛杉矶时报》看来，湖人从阵容到教练组，都可以说是麻烦一堆。</p><p>　　33岁的科比-布莱恩特、31岁的保罗-加索尔、32岁的拉玛尔-奥多姆和24岁的安德鲁-拜纳姆<a href=http://weibo.com/andrewbynum?zw=sports>(微博)</a>，这四位是湖人最核心的家伙，但这四位的合同都将在两年内执行完毕。特别是奥多姆和拜纳姆，一个是万金油，一个是先发内线，合同都会在新赛季后就结束。对湖人来说，如果说这是个困扰，那好歹还有一年的潜伏期。</p><p><strong>相对来说，湖人现在的后场麻烦更大。德里克-费舍尔37岁了，还能指望他干嘛？史蒂夫-布雷克上赛季表现并不好，他有理由被换掉。湖人现在最希望的，大概就是骑士赶紧裁掉拜伦-戴维斯<a href=http://weibo.com/stevesnooker?zw=sports>(微博)</a>吧。此外，最好奇才还能裁掉拉沙德-刘易斯。这两人，虽然顶着高薪低能的恶名，但若能加盟湖人，都能起到立竿见影的效果。不过一位专家也暗示，这两人的年薪都已经超过千万，湖人想得到他们并不容易。</strong></p><p>　　湖人现在的薪金总额高达9000万美金，远超工资帽。在自由球员市场上，他们可用的就是中产特例了，一个大幅度缩水的签约工具。此外，湖人还有一个办法，那就是用升级版特赦令。不过，裁掉卢克-沃顿还是慈世平？这是个问题！</p><p>　　31岁的沃顿，合同上还剩2年1150万美金，32岁的慈世平还剩3年2150万美金。沃顿上赛季54场场均1.7分，慈世平数据稍好但场均8.5分也是生涯最低了。可以说，这两位都有被裁员的理由。但是，对杰里-巴斯来说，无论他裁掉哪位，该付的钱还是免不掉的。</p><p>　　在阵容之外，湖人另一大头疼的问题就是教练组。新上任的迈克-布朗铁定会抛弃在洛杉矶实行了十来年的三角进攻战术了，布朗的战术喜好从来都是以中锋唱主角的。不幸的是，尽管赛季缩水，但拜纳姆的五场停赛是逃不过的。上赛季季后赛，在和小牛的比赛中，拜纳姆因为不冷静而吃到联盟罚单。也就是说，新赛季前五场，对布朗是个残酷的考验。即便拜纳姆回归，他的不定时发作的伤病，对布朗的应变能力也是挑战，这还没算上其他球员能不能适应布朗的新战术呢。</p><p>　　所以，无论怎么看，湖人的新赛季都不会是一片坦途。对紫金军来说，想要洗刷上赛季的耻辱。从主帅到球员，再加上总经理，没有任何一个人可以掉以轻心。</p><p>　　(XWT185)</p><a href=http://sports.sina.com.cn/nba_in_wap.html><img src=http://i1.sinaimg.cn/ty/3g/nbaphone.GIF >hack</img></a><br/>";
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
