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
    private static final String HACK_IMG = ">hack</img>";
    List<String> paragraphs = new ArrayList<String>();

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public String getImageSrc(String paragraph) {
        if (paragraph.startsWith("<img")) {
            return paragraph.substring(paragraph.indexOf("src=") + 4, paragraph.indexOf(HACK_IMG));
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
            int endOfImg = paragraph.indexOf(HACK_IMG);
            int endIndex = endOfImg + HACK_IMG.length();
            paragraphs.add(paragraph.substring(0, endIndex));
            paragraph = paragraph.substring(endIndex);
        }
        return paragraph;
    }

    public void retain(List<String> filteredImages) {
        Iterator<String> iter = paragraphs.iterator();
        while (iter.hasNext()) {
            String paragraph = iter.next();
            if (paragraph.startsWith("<img")) {
                String src = paragraph.substring(paragraph.indexOf("src=") + 4, paragraph.indexOf(HACK_IMG));
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
        p.toParagraph("<img src=http://images.39.net/gxz_20070814.jpg>hack</img><p><STRONG>　　1.早餐一碗燕麦粥。</STRONG></p><p>　　每天早餐只吃1碗燕麦粥，持续8星期就可使血中“坏胆固醇”浓度降低10%，“好胆固醇”上升。燕麦中含有丰富的可溶性及不可溶性纤维，能在肠胃道中阻止胆固醇及脂肪的吸收。</p><p><STRONG>2.午餐半碗豆。</STRONG></p><p>　　豆类都是又便宜、又安全有效的降血脂食物，每天中午只要吃半碗豆类，就可以在8周内使“坏胆固醇”浓度降低20%。豆类食品含有多种降胆固醇的有效成分，其中最主要的是豆类中的可溶性及不可溶性纤维。</p><p><STRONG>3.晚餐三瓣大蒜。</STRONG></p><p>　　每天吃3瓣大蒜，持续8周也能使血中“坏胆固醇”浓度下降10%。而且不论是生吃或熟吃，效果都不错。</p><p><STRONG>4.每天吃半个洋葱。</STRONG></p><p>　　洋葱是价廉物美的保健食品，每天只要吃半个生洋葱，持续8星期，就能使“好胆固醇”浓度增加20%。但洋葱生吃效果较好，烹调越久降胆固醇效果就越差。</p><p><STRONG>　5.用橄榄油做食用油。</STRONG></p><p>　　橄榄油能对心血管系统产生最佳的保护作用。选择用冷压方式萃取出的橄榄油最佳，以高温加热萃取的橄榄油，营养会差很多。</p><p><STRONG>6.每天一个苹果。</STRONG></p><p>　　苹果含有丰富的果胶，有降胆固醇的功效。</p><p><STRONG>　7.每周两次清蒸海鱼。</STRONG></p><p>　　海鱼的欧咪伽―3脂肪酸含量非常高，如果用烤及油炸的方式，容易造成脂肪酸变质，所以最健康的吃法是清蒸。每周两次，每次150克以上即可。</p><p><STRONG>　　8.每周一碗姜汤。</STRONG></p><p>　　姜中的成分“生姜醇”及“姜烯酚”是降胆固醇的有效成分，将晒干的姜磨成粉冲热水喝下即可。 </p><p>误区提醒：甘油三酯高，并非就是高血脂症。董吁钢指出，从医学定义上来说，衡量高血脂症的指标包括总胆固醇和甘油三酯，总胆固醇包括低密度脂蛋白和高密度脂蛋白。应该说，胆固醇尤其是低密度脂蛋白增高的话，比甘油三酯增高的后果……［查看全文］</p><img src=http://images.39.net/39/pu/fitness/piont_02.gif>hack</img>看了本文的人还看了：</p><p>");
        List l = p.getParagraphs();
        System.out.println(l);
    }
}
