package com.goal98.flipdroid.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TableRow;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.Page;

import java.util.List;

public class PageView extends LinearLayout {


    private Page page;
    private int articlePerRow = 2;



    public void setPage(Page page) {
        this.page = page;
        setArticleList();
    }

    private void setArticleList() {

        removeAllViews();

        if (page != null) {
            List<Article> articleList = page.getArticleList();
            if (articleList != null) {
                boolean useRow = true;
                LinearLayout row = null;
                for (int i = 0; i < articleList.size(); i++) {
                    Article article = articleList.get(i);
                    ArticleView articleView = new ArticleView(this.getContext(), article);

                    if(useRow){
                        if(row == null){
                            row = new LinearLayout(this.getContext());
                            int orientation = getOrientation() == VERTICAL? HORIZONTAL:VERTICAL;
                            row.setOrientation(orientation);
                            addView(row);
                        }
                        row.addView(articleView);

                        if(row.getChildCount() == articlePerRow){
                            useRow = false;
                            row = null;
                        }
                    }else {
                        this.addView(articleView);
                        useRow = true;
                    }
                }
            }
        }

    }

    public PageView(Context context) {
        super(context);
    }

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
