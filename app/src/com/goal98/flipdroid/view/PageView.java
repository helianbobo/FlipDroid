package com.goal98.flipdroid.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.Page;

import java.util.List;
import java.util.Random;

public class PageView extends LinearLayout {


    private Page page;
    private int articlePerRow = 2;
    private LinearLayout layout; //this layout is supposed to be dynamic, depending on the Articles on this page


    public void setPage(Page page) {
        this.page = page;
        setArticleList();
    }

    private void setArticleList() {
        int layout = determineLayout(this.page);
        //set page's layout
        setDynamicLayout(this.getContext(), layout);


        if (page != null) {
            List<Article> articleList = page.getArticleList();
            if (articleList != null) {
                boolean useRow = true;
                LinearLayout row = null;
                fillPartOnLayout(0, R.id.part1_1, articleList);
                //fillPartOnLayout(1, R.id.part1_2, articleList);
                fillPartOnLayout(2, R.id.part2_1, articleList);
                //fillPartOnLayout(3, R.id.part2_2, articleList);
                //fillPartOnLayout(4, R.id.part2_3, articleList);
            }
        }

    }

    //determine a layout based on the page's content
    // TODO
    private int determineLayout(Page page) {
        int[] candidate = new int[]{R.layout.l1, R.layout.l2,R.layout.l3};
        int layoutIndex = new Random().nextInt(3);
        return candidate[2];
    }

    private void fillPartOnLayout(int index, int viewid, List<Article> articleList) {
        Article article = articleList.get(index);
        ArticleView articleView;
       // if(article.hasLink()){
       //    articleView = new ArticleWithURLView(this.getContext(), article);
       // }else{
           articleView = new ArticleWithoutURLView(this.getContext(), article);
        //}
        articleView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,1));
        LinearLayout part1 = (LinearLayout)layout.findViewById(viewid);
        part1.addView(articleView,new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,1));
    }

    public PageView(Context context) {
        super(context);
        this.setBaselineAligned(false);
        //setDynamicLayout(context, R.layout.l1);
    }

    private void setDynamicLayout(Context context, int layoutId) {
        this.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        this.layout = (LinearLayout) inflater.inflate(layoutId, null);
        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        TextView headerTextView = new TextView(context);
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setTextSize(15);
        headerTextView.setText(R.string.sinaweoboheader);
        Typeface boldTypeface = Typeface.DEFAULT_BOLD;
        headerTextView.setTypeface(boldTypeface);
        headerLayout.setGravity(Gravity.CENTER);
        LayoutParams headerLayoutLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        headerLayoutLayoutParams.setMargins(0, 0, 0, 1);
        headerLayout.setBackgroundColor(Color.WHITE);

        headerLayout.addView(headerTextView, headerLayoutLayoutParams);
        this.setOrientation(VERTICAL);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 93);
        //layoutParams.setMargins(0,0,0,1);
        this.addView(headerLayout, layoutParams);
        this.addView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT,7));
    }

//    public PageView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        this.setBaselineAligned(false);
//        //setDynamicLayout(context, R.layout.l1);
//    }
}
