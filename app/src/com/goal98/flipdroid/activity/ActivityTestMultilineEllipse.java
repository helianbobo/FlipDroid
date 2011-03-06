package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.Page;
import com.goal98.flipdroid.view.PageView;
import com.goal98.flipdroid.view.TextViewMultilineEllipse;


public class ActivityTestMultilineEllipse extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        LinearLayout llMain = new LinearLayout(this);
//        llMain.setOrientation(LinearLayout.VERTICAL);
//        llMain.setBackgroundColor(0xFFFFFFFF);
//        addContentView(llMain, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//
//        ScrollView scroll = new ScrollView(this);
//        scroll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//        llMain.addView(scroll);
//
//        LinearLayout llContent = new LinearLayout(this);
//        llContent.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//        llContent.setOrientation(LinearLayout.HORIZONTAL);
//        llContent.setBackgroundColor(0xFFFFFFFF);
//        scroll.addView(llContent);
//        LayoutInflater inflate = LayoutInflater.from(this);
//        LinearLayout ll = (LinearLayout) inflate.inflate(R.layout.l1, null);
        PageView pv = new PageView(this);
        Page page = new Page();
        Article article = new Article();
        article.setContent("This is some longer text. It should wrap and then eventually be ellipsized once it gets way too long for the horizontal width of the current application screen. We should be fixed to max [N] lines height.");
        page.addArticle(article);
        page.addArticle(article);
        page.addArticle(article);
        page.addArticle(article);
        page.addArticle(article);
//
//
        pv.setPage(page);
        pv.setBaselineAligned(false);
        this.setContentView(pv);
//       LinearLayout llContent = new LinearLayout(this);
//        llContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        llContent.setOrientation(LinearLayout.HORIZONTAL);
//        llContent.setBackgroundColor(0xFFFFFFFF);
//
//        LinearLayout llContent2 = new LinearLayout(this);
//        llContent2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,35));
//        llContent2.setOrientation(LinearLayout.HORIZONTAL);
//        llContent2.setBackgroundColor(0xFFFFFFFF);
//
//        LinearLayout llContent3 = new LinearLayout(this);
//        llContent3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,65));
//        llContent3.setOrientation(LinearLayout.HORIZONTAL);
//        llContent3.setBackgroundColor(0xFFFFFFFF);
//
//
//                TextViewMultilineEllipse tv1 = new TextViewMultilineEllipse(this);
//        tv1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//        tv1.setEllipsis("...");
//        tv1.setEllipsisMore(" Read More!");
//        tv1.setText("This is some longer text. It should wrap and then eventually be ellipsized once it gets way too long for the horizontal width of the current application screen. We should be fixed to max [N] lines height.");
//
//        tv1.setPadding(10, 10, 10, 10);
//        tv1.setBackgroundColor(0xFFE4BEF1);
//
//        TextViewMultilineEllipse tv2 = new TextViewMultilineEllipse(this);
//        tv2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//        tv2.setEllipsis("...");
//        tv2.setEllipsisMore(" Read More!");
//        tv2.setText("This is some longer text. It should wrap and then eventually be ellipsized once it gets way too long for the horizontal width of the current application screen. We should be fixed to max [N] lines height.");
//
//        tv2.setPadding(10, 10, 10, 10);
//        tv2.setBackgroundColor(0xFFE4BEF1);
//
//       llContent2.addView(tv1);
//
//        llContent3.addView(tv2);
//        llContent.setBaselineAligned(false);
//        llContent.addView(llContent2);
//        llContent.addView(llContent3);
//        this.setContentView(llContent, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        // Make one widget that won't ellipsize within three lines.
//        TextViewMultilineEllipse tv1 = new TextViewMultilineEllipse(this);
//        tv1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//        tv1.setEllipsis("...");
//        tv1.setEllipsisMore(" Read More!");
//        tv1.setText("This is some short text. It won't need to be ellipsized.");
//        tv1.setMaxLines(3);
//        tv1.setPadding(10, 10, 10, 10);
//        tv1.setBackgroundColor(0xFFE4BEF1);
       // llContent.addView(tv1);

        // Make one widget that is long enough to ellipsize within three lines. Add a click handler,
        // expand and collapse on click.
//        final TextViewMultilineEllipse tv2 = new TextViewMultilineEllipse(this);
//        tv2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//        tv2.setEllipsis("...");
//        tv2.setEllipsisMore(" Read More!");
//        tv2.setMaxLines(3);
//        tv2.setText("This is some longer text. It should wrap and then eventually be ellipsized once it gets way too long for the horizontal width of the current application screen. We should be fixed to max [N] lines height.");
//        tv2.setPadding(10, 10, 10, 10);
//        tv2.setBackgroundColor(0xFFFCDFB2);
//        tv2.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                if (tv2.getIsExpanded()) {
//                    tv2.collapse();
//                }
//                else {
//                    tv2.expand();
//                }
//            }
//        });
//        llContent.addView(tv2);
    }
}