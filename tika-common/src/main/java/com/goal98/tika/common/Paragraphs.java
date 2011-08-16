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
        while ((cutAt = articleContent.indexOf("</")) != -1) {
            if(cutAt==0){
               int endAt = articleContent.indexOf(">");
                articleContent = articleContent.substring(endAt+1);
                continue;
            }
            int endAt = articleContent.indexOf(">",cutAt);
            String paragraph = articleContent.substring(0, endAt+1);

            paragraph = parseImg(paragraph);
            paragraphs.add(new Text(paragraph));
            articleContent = articleContent.substring(endAt+1);
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
        p.toParagraph("</p><p>按时间顺序更新。由于现场网络不给力，如果一会更新不了，我会晚上回去补上。</p><p>本文所有图片整理自新浪微博，在图片右下角已注明贡献用户，谢谢大家。</p><H1>开场</H1><p>雷军开始第一句话：“昨天发生了一件大事”。雷军说：<STRONG>“为发烧友做一款手机”</STRONG>。</p><p>小米手机只要满足4个条件：双核，大屏，信号好，大电池。</p><H2>硬件</H2><H3>双核</H3><p>采用高通双核 1.5G 处理器，是“目前全球最快的手机”。</p><p>雷军开始讲CPU、GPU详细参数和效果，本人对此无兴趣，省去1000字。</p><p>雷军：<STRONG>“可扩展对发烧友来说是最理想的”，</STRONG>“小米要做世界上最好的手机” 。</p><H3>大屏：4寸，夏普，16：9</H3><H3>电池：1930mAh，“小米手机保证大家用2天”。</H3><H3>信号好：这事没法细说。</H3><img src=http://www.36kr.com/wp-content/uploads/2011/08/86GC.jpeg >hack</img><H3>设计</H3><p>雷军：”没有设计就是最好的设计。“</p><p>正面有三个键。<STRONG>侧边多一个”米键“</STRONG>，可以自定义，默认是回Home，可以由用户自定义成拍照、发微博等。</p><H2>操作系统</H2><p>MiUI，大家应该很熟悉了。</p><p>今天发布的MiUI 是基于最新的Android 2.3.5 开发。雷军：”目前全球有50万MiUI粉丝，其中20多万粉丝在境外“。</p><H3>云端服务</H3><p>可以在手机上直接查看在线短信（节假日的短信推送），还有在线图标等服务。</p><img src=http://www.36kr.com/wp-content/uploads/2011/08/66dcca76jw1dk7f1nvzdjj.jpg width=440 height=330 >hack</img><H3>米聊：手机社交工具</H3><img src=http://www.36kr.com/wp-content/uploads/2011/08/66e8f898gw1dk7f2ntrn4j.jpg >hack</img><H3>无锁双系统</H3><p>无需破解，可以随便刷机，官网提供MIUI和Android 原生系统。”小米有自信，系统大家随便刷，我们的系统一定比Android 原生系统做得好“。</p><H3>小米的2个承诺</H3><p>”小米手机上市后，其他手机仍然可以继续使用不断更新的MIUI。“</p><p>”小米手机会一直无锁。“</p><H2>怎么买？</H2><p>仅在互联网零售，8月29日上线接受预订。10月初正式量产销售。<STRONG>由凡客提供仓储和配送支持</STRONG>。陈年：”希望小米手机和凡客的T恤卖的一样好，一年卖出6000万部！“</p><p>价格：￥1999</p><H2>基本结束??</H2><p><STRONG>除非注明，本站文章均为原创或编译，转载请注明：</STRONG> 文章来自36氪</p>");
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
