package com.goal98.tika.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        articleContent = parseImg(articleContent);
        while ((cutAt = articleContent.indexOf("</p>")) != -1) {
            String paragraph = articleContent.substring(0, cutAt + 4);
            paragraph = parseImg(paragraph);
            if (!paragraph.startsWith("<"))
                paragraph = "<p>" + paragraph;
            paragraphs.add(new Text(paragraph));
            articleContent = articleContent.substring(cutAt + 4);
        }
        articleContent = parseImg(articleContent);
    }

    private String parseImg(String paragraph) {
        while (paragraph.startsWith("<img")) {
            int endOfImg = paragraph.indexOf(ImageInfo.HACK_IMG);
            int endIndex = endOfImg + ImageInfo.HACK_IMG.length();
            final ImageInfo imageInfo = new ImageInfo();
            imageInfo.setUnparsedInfo(paragraph.substring(0, endIndex));
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
                if (filteredImages.indexOf(src) == -1) {
                    iter.remove();
                } else {
                    ImageInfo imageInfo = imageInfoMap.get(src);
                    tikaImage.setWidth(imageInfo.getWidth());
                    tikaImage.setHeight(imageInfo.getHeight());
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
        p.toParagraph("<img src=http://www.ifanr.com/wp-content/uploads/2011/08/k4.png width=212 height=123 >hack</img><blockquote><p> Amazon Kindle 一直把持着电子书阅读器市场份额的头把交椅。去年推出的 Kindle 3 设计纤薄并对机能进行改进，譬如对比度增强 50%、翻页速度加快 20% 等，并且配备了全键盘。不过遗憾的是，由于 Amazon 并未对中国大陆地区正式发售 Kindle，中文输入法的出现一直停留在幻想阶段。</blockquote></p><p><blockquote>在一次偶然的机会下，我在一个社区论坛中看到有人提出了 Kindle 3 俄文输入法的可行性方案，就在想中文输入法也会有春天。巧合的是，有一位 Kindle 用户也注意到了这条信息，并且经过一段时间的开发，真的给广大 Kindle 3 中国用户带来了较为完善的中文输入法。此人就是今次 iSeed 访谈的主人公—— 200（Blog, Email）。</blockquote></p><p><blockquote>200 不是个非常张扬的人，在几次交谈中都保持着低调和谦逊。这次访谈中有很多关于开发背后的故事，你们一定不能错过。</blockquote></p><p><STRONG>ifanr: 先介绍下自己吧，说说自己的本职工作和业余爱好之类的，以及开发 Kindle 3 中文输入法的初衷。</STRONG></p><p><STRONG>200:</STRONG> 好的。大家好，我是200， Kindle 3 中文拼音输入法的作者。目前我的本职是学生，业余生活算是比较单一，看看书，玩玩 FPS，再就是捣鼓一些自认为有趣的小玩意儿，比如这个输入法。</p><img src=http://www.ifanr.com/wp-content/uploads/2011/08/k1.gif >hack</img><p>我是去年 11 月入手的 Kindle 3，当时之所以选择 Kindle 3，其实主要是觊觎 Amazon 的免费3G。不过买回来才发现 Kindle 3 对于网络的支持并没有想象中的强大，有些许失望，不过能随时随地无阻碍地看 Wikipedia 也算是让我欣慰不少。和入手其他电子设备一样，第一件事就是搜索相关的资源，看看能不能对设备的功能进行拓展补充。然后误打误撞地就找到了 Mobileread 论坛，看到了别人开发的一些第三方应用，自己也试着用了几个，感觉挺不错的，于是也就萌生了写点东西的念头。当时只是觉得在 Kindle 上开发程序应该会很有意思，却也没有决定是要实现输入法。</p><p><STRONG>ifanr: 那以前有开发过其它作品吗？现在不少 Kindle 3 中文用户对于你的作品十分喜欢，有没有打算开发其他延展出来的产品？譬如 Kindle 2 代和 Kindle DXG 用户。</STRONG></p><p><STRONG>200:</STRONG> 我以前倒是写过一些其他的程序，不过与 Kindle 倒是没什么关系，就不多提了。关于 Kindle 的相关开发，我还是比较期待 Amazon 能够开放 KDK (Kindle Development Kit) ，这样会方便不少开发者来对 Kindle 进行拓展，我也能方便的写点类似行程表记事本此类的小应用。当然，现阶段我的打算是抽时间把输入法进行重构，因为之前完成拼音输入法也只是为了满足个人需求，并没有想到会有这么多朋友来支持，自然也就没有想到需要对其进行后期维护，因此，写的比较随意。后期我打算把 SCIM 移植过来，这样就可以拓展出多种输入法，比如<STRONG>五笔、仓颉</STRONG>，以满足不同用户的需求。当然，Kindle 2 和 Kindle DXG 的用户我也会尽力去照顾，不过身边没有这样的设备所以实施起来肯定比较费劲，相对于 Kindle 3 那肯定得滞后不少了。</p><p><STRONG>ifanr: 从你的个人 blog 上了解到，Kindle 3 中文输入法的编写工作从年初 2 月 2 日开始到 4 月 30 日结束，应该说效率并不低，你是从哪儿得到开发的灵感的呢？</STRONG></p><p><STRONG>200:</STRONG> 其实关于输入法的构想去年11月就有了，只是一直停留在想法，记得当时只是把输入法的可行性在日志上唠叨了一下，那会儿我并没有想着要去实现它，自己倒是蛮期待有人能从我的日志收获一些灵感进而完成输入法，到时候我也顺便沾光上上特别鸣谢什么的。可是一直等到春节，也没见得有类似的应用出现，反倒是有不少网友在那篇日志的回复里鼓励我去实现输入法。于是刚好趁着过年的空挡，就想着着手试试看呗。</p><img src=http://www.ifanr.com/wp-content/uploads/2011/08/20110810_01-360x541.jpg >hack</img><p><STRONG>ifanr:这其中遇到最大的困难是什么？</STRONG></p><p><STRONG>200:</STRONG> 真正动手进行开发，才发现过程并没有自己原先想象的那么有趣，首先 Kindle 的交叉编译环境配置就让我头疼了好几天，由于我之前没有从事过类似的嵌入式程序开发，所以交叉编译链的配置把我折磨得焦头烂额，还好最后找到了 NiLuJe 大神关于交叉编译的 terminal history，问题才得以解决。</p><p>还有就是 Kindle 前台的应用界面是用 java 编写的，这本身应该算是一个好事，因为 java 编写的程序逆向起来比较容易，这样有助于我分析出相关的接口.但是除了与 KDK 相关的部分，其他 class 都被混淆过了，刚好那段时间的 java 在线反混淆工具也工作不正常，这就给我的分析工作带来了很大的麻烦，自己人肉反混淆几个 class 终于撑不住了，最后向其作者 Stiver 求助，讨要了一个离线版的 fernflower，终于完成了相关接口的逆向工作。</p><img src=http://www.ifanr.com/wp-content/uploads/2011/08/k2.gif >hack</img><p>其实最麻烦的还要属程序的调试了，因为我没有 Kindle 的模拟器，所以没有办法边开发边调试，所以只好把程序都写完了，然后放到实体机器上去测试，开始的时候一旦程序错误，电子书也就跟着挂掉了，只好强制重启（反复好多次，我怀疑我的 Kindle 现在续航能力大减就跟重启有关）。失败了很多次之后，终于放弃这种原始野蛮的测试方法，采取前后台分离测试的办法，因为后台与Google 输入法通讯的部分与 Kindle 平台并无耦合，所以我在 Linux 上单独写了一个简易的前台GUI 接收键盘输入来测试输入法的后台代码，只在 Kindle 的实体上测试前台界面的绘制，确保两个都没有问题了再将程序组装起来。即使这样到最后还是出了一些问题，因为 Kindle 上的 java 并不是完全版的，有些特性比如枚举类型其根本不支持，没办法，最后只有一点一点排查兼容性问题，总之非常考验耐性。</p><p><STRONG>ifanr: 那么开发过程中有没有想过放弃？</STRONG></p><p><STRONG>200:</STRONG> 说来惭愧，我中途因为其他事情把输入法开发搁浅了一段日子，以至于自己都快忘了有这么一茬事儿，之后有天去查看留言，看到底下那么多回复感觉很愧疚，然后就停下手头的事，继续开发了。严格的说，整个过程对我来说不存在放弃不放弃，只是到底能走多远的问题，当时我也不知道这个输入法最后能不能写完，我的计划其实是自己先试试看，能完成最好，但是如果遇到了瓶颈最后解决不了，那就把我期间的成果整理一下，等待其他高人继续完成。总之非常感谢在留言里支持我的朋友们，不然现在这个输入法到底存不存在还两说呢。</p><p><STRONG>ifanr:国内现在有不少做电子书的厂家，不过似乎业绩都不算好。</STRONG><STRONG>你对这个怎么看？</STRONG></p><p><STRONG>200:</STRONG> 对于国内的电子书产业我所知甚少，也就不妄加评论了。但是对于电子书设备，我个人认为支持个性化的定制和提供健全的配套服务是取胜的关键因素。我虽然没有用过汉王的电子书，但是从论坛上其他朋友的反应来看，在这两方面汉王还是有继续提升的空间，总之希望这样一个国产品牌今后可以一路走好吧。</p><p><STRONG>ifanr: 除了 Kindle 3 中文输入法外，你还对 Kindle 的浏览器做了番 DIY 并作了不少研究。同时，国内也有开发者把仙剑移植到了 Kindle 上。你觉得 Kindle 在开发方面的可玩性高吗？</STRONG></p><p><STRONG>200:</STRONG> Amazon 官方对于 Kindle 开发资料释出的并不是很多，但是从接连几次固件升级中可以看出，Amazon 并不打算把这些非授权的第三方应用完全限死，还是为其保留了一些发展的空间。同时，从 Mobileread 上我们也可以看到，有很多热心的 geek 在不断地发掘 Kindle 的功能，所以我对 Kindle 开发方面的前景还是保持乐观的，同时换个角度看，其实也正是这种不完全开放的环境让人更有好奇心去进一步探索，所以我相信诸如此类的应用今后还是会层出不穷的。</p><p><STRONG>ifanr: 作为 Kindle 用户，怎么看待 Amazon 主力打造的这个电子书消费平台？</STRONG></p><p>200: 虽然我本人只在 Amazon 上购置过几本电子书，但是我还是很支持这个消费平台的，一方面它促进了版权保护，另外一方面它能和 Kindle 设备完美的融合，当然，如果价格能更优惠一些就更好了，嘿嘿。</p><p><STRONG>ifanr: 问个轻松些的话题，你是 Kindle 3 中文输入项目的主脑，你觉得如果有读者和你比赛 Kindle 3 汉字录入速度，有信心赢吗？</STRONG></p><p><STRONG>200：</STRONG>我是个现实主义者，所以这个我就不发表评论了，哈哈。对了，最后大家可以在这里获得 Kindle 3 中文输入法的详细安装说明。</p><p><blockquote>。</blockquote></p>");
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
