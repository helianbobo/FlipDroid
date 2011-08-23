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
//        System.out.println(articleContent.length());
        int cutAt = 0;
//        articleContent = parseImg(articleContent);
        while ((cutAt = findNextClosingTag(articleContent)) != -1) {
//            if (cutAt == 0) {
//                int endAt = articleContent.indexOf(">");
//                articleContent = articleContent.substring(endAt + 1);
//                continue;
//            }
            int endAt = articleContent.indexOf(">", cutAt);
            String paragraph = articleContent.substring(0, endAt + 1);
//            toParagraph(paragraph);
            if (paragraph.indexOf(ImageInfo.IMG_START) != -1)
                paragraph = parseImg(paragraph);
            else
                paragraphs.add(new Text(paragraph));

            articleContent = articleContent.substring(endAt + 1);
        }
//        articleContent = parseImg(articleContent);
    }

    static Pattern startTag = Pattern.compile("(<[^>]+>)|</[^>]+>");

    private int findNextClosingTag(String articleContent) {
//        articleContent = "<img src=http://www.ifanr.com/wp-content/uploads/avatar/1347.JPG >hack</img>";
        if (articleContent.trim().startsWith("</"))
            return 0;
        if (articleContent.trim().length() == 0)
            return -1;


        Matcher startMatcher = startTag.matcher(articleContent);
        int level = 0;
        while (startMatcher.find()) {
//            System.out.println(startMatcher.group());
            if (startMatcher.group().indexOf("</") == -1) {
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
                for (int i = 0; i < filteredImages.size(); i++) {
                    String s = filteredImages.get(i);
                    if (s.indexOf(src) != -1) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    iter.remove();
                } else {
                    ImageInfo imageInfo = imageInfoMap.get(src);
                    if (imageInfo != null) {
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
        p.toParagraph("<H3>Windows for Pen</H3><p>系统开机的模样：</p><p><img src=http://www.ifanr.com/wp-content/uploads/2011/08/wfp-480x360.gif >hack</img></p><p>用笔写字，在上面留下笔迹的情形：</p><p><img src=http://www.ifanr.com/wp-content/uploads/2011/08/wfp2-480x360.gif >hack</img></p><p>“笔势操作（就是用笔来作出动作就能完成一定的操作，和现在的触摸操作原理一样）”的说明：</p><p><img src=http://www.ifanr.com/wp-content/uploads/2011/08/wfp3-480x360.gif >hack</img></p><p>以下是操作视频：</p><p>早期搭载 Windows for Pen 1.0 的电脑有 Compaq Concerto，它拥有一颗 Intel 80486、一块 100 MB 的硬盘、3.5 寸磁盘、内存 1 MB、屏幕分辨率为 640×480，整机重量 2kg：</p><p><img src=http://www.ifanr.com/wp-content/uploads/2011/08/Concerto.png >hack</img></p><p>后来，微软还开发出基于 Windows 95 的 Windows for Pen 2.0。</p><H3>Tablet PC</H3><p><blockquote>一个基于全功能 Microsoft Windows 操作系统的 PC，整合了纸和笔方便直觉的体验。a full-function Microsoft Windows operating system-based PC incorporating the convenient and intuitive aspects of pencil and paper into the PC experience.</blockquote></p><p>随后在 WinHEC 2001 大会上，微软公布了大量 Tablet PC 的硬件，向全球 OEM 厂商讲述关于这个概念的技术方法、发展路线以及未来。在会上，微软展示了名为“Microsoft Notebook”的应用，可以抓捕人们写作的笔迹。微软认为 Tablet PC 将成为“知识工作者”的利器。</p><p>为了发展 Tablet PC，在这一年，微软除了发布 Windows XP 以外，还发布专门面对 Tablet PC 的系统 Windows XP Tablet PC Edition。2002 年，微软公布了“数字墨水（Digital Ink）”技术，让人能够在系统任何界面上进行手写，电子墨水技术会自动把你输入的内容转化为文字，或者以特定的格式保存。到 2005 年，微软发布 Windows XP Tablet PC Edition 2005，是上一代产品的改进版。</p><p><img src=http://www.ifanr.com/wp-content/uploads/2011/08/cp3d-480x333.jpg >hack</img></p><p>到 Windows Vista 微软直接把 Tablet PC 特性集成到系统之中，不再单独发布针对 Tablet PC 的操作系统。</p><p>2006 年，Tablet PC 迎来更加轻薄的家族成员 Ultra-mobile PC（UMPC）。除了体积更小之外，和传统的 Tablet PC 没有区别，运行的系统是 Windows Vista、用的芯片是基于 x86、用笔操作。UMPC 推出的机型不多，厂商反应不热烈，消费者也不买账。</p><p>第一款 UMPC 是三星的 Q1，运行低压版赛扬 M 处理器、内存 512 MB、：</p><p><img src=../wp-content/uploads/2011/08/samsung-q1-ultra.jpg >hack</img></p><p>以下是，Windows Vista 在 Tablet 下的操作视频：</p><p>2009 年，Windows 7 发布，它融合了微软开发的 Surface 技术，改善了对触摸操作的支持：</p><p>Windows 7 在 Tablet PC 上的表现：</p><p>在 CES 2010 大会上，鲍尔默展示了基于 Windows 7 的平板电脑，HP 的 Slate 500：</p><p><img src=../wp-content/uploads/2011/08/zdnet-rachel-king-hp-slate-500-004-480x360.jpg >hack</img></p><H3>神秘消失的 Courier</H3><p>我不得不提一下 Courier 这个微软神秘的平板项目。Courier 拥有两个屏幕，皆可进行触控操作，一个面板主要负责整理内容，包括图片、笔记、音乐等等，一个面板负责处理内容，包括让添加注释、新增内容等等。</p><p>Courier 同时支持用“笔”和“手指”进行操作。从公开的视频来看，不论是操作方式，还是系统界面，与以往的 Tablet PC 截然不同。很可惜，从公开到取消，从 2009 年 9 月 22 日到 2010 年 4 月 29 日，这个项目存活的时间不到一年。</p><H3>微软如何看待 Tablet PC</H3><p>平板电脑是一种 PC</p><p>这个观点不是 2001 年比尔盖茨的观点，而是 2011 年微软 Windows Phone 部门主席 Andrew Lees 的观点：</p><p><blockquote>我们把平板电脑视为一种 PC。我们要让人们在 PC 上希望能做到事情，在平板上也能做到，比如说与网络连接，上网冲浪，使用网络工具，连接 USB 硬盘等等，要把它们都整合在一块平板中去。提供一种混合的体验，让诸如打印这类事情，办公室里用到的所有的东西，所有你希望在 PC 上用到的东西，在平板电脑上都能做到。</blockquote></p><p>从 Tablet PC 提出到现在，微软认定 平板电脑就是一种 PC，而且他们还相信 PC 的优势不会消退。</p><p>基于“笔” 的操作系统</p><p>从 Windows for Pen、Windows XP Tablet Edition 到 Courier，再到 Windows Vista 和 Windows 7。微软推出的平板操作系统，都基于“笔”这个工具。即使是最激进的 Courise 项目，基本操作大多也依赖“笔”。</p><p>如果你对比一下，Windows Vista 的操作视频和 Windows for Pen 的“笔势操作”的说明，你会发现，“笔势操作”已经成为 Tablet PC 的操作基础。即使是 Courier 也不过是把“笔势操作”换了动作定义，原理和实质没有改变。</p><H3>Tablet PC 的教训</H3><p>虽然微软可能不是第一个推出平板电脑产品的公司，但它持续 20 年关注并投入平板电脑领域，却得不到像 iPad 这样的成就，其中的原因值得深思。</p><p><blockquote>传统的 Tablet PC 和手提电脑一般大，和手提电脑一般重，电力和手提电脑一样差，而价格，通常比手提电脑贵。</blockquote></p><p>除了大、重、贵外，Tablet PC 还缺乏专门为其开发的软件。上文中，有一段展示运行 Windows 7 的 Tablet PC 的操作视频，即便视频的作者打算宣传 Tablet PC 的便利，但在 PowerPoint 中新建幻灯片中还是遇到了麻烦。即使微软自己开发的软件都会存在问题，其它的软件开发商，可想而知。</p><H3>微软能转弯吗？</H3><p>瑞信（Credit Suisse）预测，到 2015 年，平板电脑将占个人电脑总量的 40%，能创造销售额 1200 亿美元的市场。微软若不能抓住机会，这个市场将被苹果、Google 吞食。</p><p>但，微软能转弯吗？</p><p>Peter Bright 认为微软能，因为下一代 Windows 8 具备不同以往的特性，它运用了和 Windows Phone 一样的“Metro”设计风格，优化了触摸的体验；而引入 ARM 硬件架构，也可保证运行 Windows 8 平板电脑的使用时间。</p><p>除了 Bright 所提出的这两点意外，最近微软还宣布新的开发框架 Jupiter，新的开发框架将统一桌面系统和移动系统，意味着一个应用能够运行在 Windows 8 桌面、平板和 Windows Phone 手机上。对平板而言，Jupiter 能让开发商能够顺利开发出针对平板的软件。</p><p>目前而言，Windows 8 一切还在扑朔迷离之中，已知 Windows 8 的开发团队要比 Windows 7 庞大。我希望微软这 20 年来的投入，都能有所回报。然而市场是不等人的，微软到底能够转身与否，你认为呢？</p><img src=http://www.ifanr.com/wp-content/uploads/avatar/243.jpg >hack</img><p><img src=http://www.ifanr.com/wp-content/uploads/avatar/243.jpg >hack</img></p>");
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
            return body;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }
}
