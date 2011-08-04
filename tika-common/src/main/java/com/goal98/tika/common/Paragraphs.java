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
}
