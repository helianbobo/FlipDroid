package com.goal98.flipdroid.model;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class FakeArticleSource extends AbstractArticleSource {


    private final static String strLib = "A hacking free-for-all has exploded on the Web, and Facebook and Twitter are stuck in the middle.\n" +
            "On Wednesday, anonymous hackers took aim at companies perceived to have harmed WikiLeaks after its release of a flood of confidential diplomatic documents. MasterCard, Visa and PayPal, which had cut off people's ability to donate money to WikiLeaks, were hit by attacks that tried to block access to the companies' Web sites and services.\n" +
            "To organize their efforts, the hackers have turned to sites like Facebook and Twitter. That has drawn these Web giants into the fray and created a precarious situation for them.\n" +
            "Both Facebook and Twitter but particularly Twitter have received praise in recent years as outlets for free speech. Governments trying to control the flow of information have found it difficult to block people from voicing their concerns or setting up meetings through the sites.\n" +
            "At the same time, both Facebook and Twitter have corporate aspirations that hinge on their ability to serve as ad platforms for other companies. This leaves them with tough public relations and business decisions around how they should handle situations as politically charged as the WikiLeaks developments.";

    private static String[] strLibArray;

    private List<Article> list = new LinkedList<Article>();


    public FakeArticleSource() {

        strLibArray = strLib.split("\\s+");

        for (int i = 0; i < 20; i++) {
            list.add(generateFakeArticle());
        }

        lastModified = new Date();

    }

    public List<Article> getArticleList() {
        return list;
    }

    private Article generateFakeArticle() {

        String title = "Title";
        String content = generateRandomText(140);


        Article article = new Article();
        article.setTitle(title);
        URL imageUrl = null;
        try {
            imageUrl = new URL("http://tp4.sinaimg.cn/1702755335/50/1283204608/1");
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        article.setImageUrl(imageUrl);
        article.setContent(content);
        return article;
    }

    private String generateRandomText(int max) {
        int wordNo = Math.abs(new Random().nextInt())%max;
        int start = Math.abs(new Random().nextInt())%(strLibArray.length - wordNo);
        String title = "";
        for(int i = 0; i < wordNo; i++){
            title += strLibArray[start + i] + " ";
        }
        return title;
    }
}
