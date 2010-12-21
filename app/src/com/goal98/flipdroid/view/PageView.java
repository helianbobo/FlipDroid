package com.goal98.flipdroid.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TableLayout;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.Page;

import java.util.List;

public class PageView extends TableLayout{


    private Page page;

    public void setPage(Page page) {
        this.page = page;
        setArticleList(page.getArticleList());
    }

    private void setArticleList(List articleList) {
        if(articleList != null){
            for (int i = 0; i < articleList.size(); i++) {
                Article article = (Article) articleList.get(i);
                ArticleView articleView = new ArticleView(this.getContext(), article);
                articleView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                this.addView(articleView);
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
