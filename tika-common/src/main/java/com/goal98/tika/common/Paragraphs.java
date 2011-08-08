package com.goal98.tika.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/4/11
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class Paragraphs {
    List<String> paragraphs = new ArrayList<String>();

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public String getImageSrc(String paragraph) {
        if (paragraph.startsWith("<img")) {
            return paragraph.substring(paragraph.indexOf("src=") + 4, paragraph.indexOf("/>"));
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
            if(!paragraph.startsWith("<"))
                paragraph = "<p>"+paragraph;
            paragraphs.add(paragraph);
            articleContent = articleContent.substring(cutAt + 4);
        }
        articleContent = parseImg(articleContent);
    }

    private String parseImg(String paragraph) {
        while (paragraph.startsWith("<img")) {
            int endOfImg = paragraph.indexOf("/>");
            paragraphs.add(paragraph.substring(0, endOfImg + 2));
            paragraph = paragraph.substring(endOfImg + 2);
        }
        return paragraph;
    }

    public void retain(List<String> filteredImages) {
        Iterator<String> iter = paragraphs.iterator();
        while (iter.hasNext()) {
            String paragraph = iter.next();
            if (paragraph.startsWith("<img")) {
                String src = paragraph.substring(paragraph.indexOf("src=") + 4, paragraph.indexOf("/>"));
                if (filteredImages.indexOf(src) == -1) {
                    iter.remove();
                }
            }
        }

    }

    public String toContent() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = paragraphs.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Paragraphs p = new Paragraphs();
        p.toParagraph("<p>摄影APP:拍一张五秒的照片</p>Glmps是一款新颖有趣的iPhone端app，不仅仅可以拍摄照片，它在拍摄之前会录制5秒的视频。点此可以观看此软件的试用。</p><img src=http://tech2ipo.com/wp-content/uploads/2011/08/glmps-screenshots-640x468.png/><p>这款产品本周在圣地亚哥的BlogHer会议中正式推出，一出现就吸引了用户和媒体的强烈关注。本周五，作者在WSJ.com畅谈了这款app的优 缺点。“Glmps是免费软件，拍摄后和好友分享也非常方便。但它有两个比较大的问题，其一是它耗电量巨大，是个名副其实的吃电大户，其次是用户不能储存 照片到本地——为了保存图片，用户必须将视频和图片公之于众，这对于一些用户而言无疑非常尴尬，比如打哈欠或者做鬼脸的照片。Glmps官方表示保存功能 还在开发中，我们希望能尽快解决问题。”</p><p>Glmps的照片和视频都保持在较低分辨率，这也是方便用户分享的妥协。用户还建议在使用软件的时候应该有是否分享地理位置的选项。</p><p>对于Glmps的商业模式，公司表示他们改革了拍摄软件的现有形式，为用户提供了更有乐趣，更值得回忆的拍摄方式。现在该公司正在和一些投资公司洽谈，寻找投资机会。</p><p>本文由Tech2IPO作者能嘛整理自allthingsD，点此查看原文。</p><img src=images/caogen/btn_ding.gif/><img src=images/caogen/btn_cang.gif/><p>");
        List l = p.getParagraphs();
        System.out.println(l);
    }
}
