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

    private int count = 1;


    public FakeArticleSource() {

        loadArticle();

    }

    private void loadArticle() {
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

        String title = "Title is Title.Title is Title.Title is Title.Title is Title." + count++;
        String content = generateRandomText(140);
        String status = generateRandomStatus();
        String author = "helianbobo";


        Article article = new Article();
        article.setTitle(title);
        URL portraitImageUrl = null;
        URL imageUrl = null;
        try {
            portraitImageUrl = new URL("http://tp4.sinaimg.cn/1702755335/50/1283204608/1");
            imageUrl = new URL("http://42qu.net/pic_show/721/48/e2/10895.jpg");
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        article.setPortraitImageUrl(portraitImageUrl);
        article.setImageUrl(imageUrl);
        article.setContent(content);
        article.setStatus(status);
        article.setAuthor(author);
        return article;
    }

    private String generateRandomStatus(){
        boolean includeUrl = new Random().nextBoolean();
        String url = " http://sinaurl.cn/hGbIo8";
        return "这场比较好看，世界杯之后的较量，看看谁的南非十六强含金量高。真正的亚洲老大之争。韩国如何跑动逼迫日本的传递技术，日本如何化解对手的硬朗。而且都是东亚球队，但是各自风格鲜明" + (includeUrl?url:"");
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

    public void loadMore() {
        loadArticle();
    }
}
