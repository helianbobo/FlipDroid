package com.goal98.flipdroid.model;


import java.util.LinkedList;
import java.util.List;

public class SimplePagingStretagyTest {
    public void testDoPaging() throws Exception {

        List<Article> articleList = buildArticleList(14);


        SimplePagingStretagy stretagy = new SimplePagingStretagy();
        List<Page> pageList = stretagy.doPaging(articleList);

        System.out.println(3 == pageList.size());
        System.out.println(4 == pageList.get(2).getArticleList().size());
        System.out.println("Article 13".equals(pageList.get(2).getArticleList().get(3).getTitle()));

    }

    private List<Article> buildArticleList(int size) {
        List<Article> articleList = new LinkedList<Article>();
        for (int i = 0; i < size; i++){
            Article article = new Article();
            article.setTitle("Article " + i);
            articleList.add(article);
        }
        return articleList;
    }

    public static void main(String[] args) throws Exception{
        SimplePagingStretagyTest test = new SimplePagingStretagyTest();
        test.testDoPaging();
    }
}
