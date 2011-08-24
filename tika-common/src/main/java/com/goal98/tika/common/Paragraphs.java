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
            int startAt = articleContent.indexOf("<");
            int brAt = articleContent.indexOf("<br/>");
            while (brAt == startAt) {
                startAt = articleContent.indexOf("<", brAt + 1);
                brAt = articleContent.indexOf("<br/>", brAt + 1);
            }
            if (startAt != -1) {
                paragraphs.add(new Text(articleContent.substring(0, startAt)));
            }

            endAt = articleContent.indexOf(">", cutAt);
            String paragraph = articleContent.substring(startAt, endAt + 1);
            if (paragraph.indexOf(ImageInfo.IMG_START) != -1)
                paragraph = parseImg(paragraph);
            else
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
        throw new RuntimeException("not match");
    }

    private String parseImg(String paragraph) {
        while (paragraph.indexOf(ImageInfo.IMG_START) != -1) {
            int startOfImg = paragraph.indexOf(ImageInfo.IMG_START);
            int endOfImg = paragraph.indexOf(ImageInfo.HACK_IMG);

            int endIndex = endOfImg + ImageInfo.HACK_IMG.length();
            final ImageInfo imageInfo = new ImageInfo();
            imageInfo.setUnparsedInfo(paragraph.substring(startOfImg, endIndex));
            paragraphs.add(imageInfo);
            paragraph = paragraph.substring(endIndex);
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
                    if(imageUrlWithInfo.indexOf("#")==-1){
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
        Paragraphs p = new Paragraphs();
        p.toParagraph("<h1>Ben Britten:为什么\"失败也是精彩的\"?</h1>在日前的在澳洲独立游戏大会Freeplay 2011上，Tin Man Games创始人Ben Britten在系列讲座”One Piece of Advice”（《一条建议》）中呼吁开发商一定要“完成你的游戏作品”。<br/><p>Britten的讲座既幽默又犀利。他说道：“千万不要期望自己的首款游戏就能获得巨大成功，因为你永远也不会做到，也没有人能在一开始就这么幸运。”</p><p>Britten 表示在大学的游戏课程里，最好的地方在于老师会强迫学生开发完成大量游戏作品，这样学生们才能最好地驾驭整个游戏开发过程。</p><p>“光说是不行的。我去过很多游戏展。有些人会来对我说他们有非常好的游戏创意。我会说‘是吗？很好啊——我能看看你的作品吗？’然后就没有下文了。”</p><p>为什么完成一款游戏作品就那么难呢？Britten向观众展示了一张很不科学的图表，上面显示的是游戏设计师在开发游戏时可能经历的一些内部流程。</p><p>“当你从无到有进行创作时，就像是一次见证奇迹发生的经历，”Britten 边说边指向图表的“完美”轴上方，“然后你做出了作品的原型，它非常的出色，但这个过程的时间会更长…同时非常的有趣和刺激。”</p><p>但是创作的过程却是对许多想法的一次残酷考验，Britten说道，这个过程中还会出现许多诱惑，会使你最初的游戏理念变得面目全非。</p><p>他说：“当我第一次接触关于游戏的东西时，我就是个原型开发者。制作这个原型的过程很有趣，而当我想到‘如果我添加这个会怎么样’时，我会调整这个原型。我经常做诸如此类的事情。”</p><p>“那么你如何停止做原型开发者，并把已经开始的游戏都做完？”Britten 反问道。他的建议是：在提供第一个选择之前：把菜单添加到你的原型中，然后全部释放出来。“这听起来很愚蠢，但是它会帮你解决一些其他的游戏难题，例如分销、反馈等。”</p><p>比原型还要重要的是项目选择。Britten 说道：“首个项目不要选那些要花费两年时间的，也不要首先构建你理想中的游戏。”</p><p>Britten还提出了一个有悖常理的看法，那就是“失败也是非常精彩的！”</p><p>Britten最后还加上了一句为许多作家所恪守的座右铭：“准确的判断来自于经验，而经验又来自于错误的判断。”这句普遍真理提醒着人们，在许多情况下，失败不仅是一个选择，而且还是一个很好的选择。</p><br/><br/><br/><img src=images/caogen/btn_ding.gif >hack</img><img src=images/caogen/btn_cang.gif >hack</img><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>");
        List l = p.getParagraphs();
        System.out.println(p.toContent());
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
            if(body.trim().length()==0)
                return "";
            if (!body.startsWith("<"))
                return "<p>" + body + "</p>";

            return body;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }
}
