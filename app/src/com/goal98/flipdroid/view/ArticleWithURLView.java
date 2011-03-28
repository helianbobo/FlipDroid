package com.goal98.flipdroid.view;

import android.content.Context;
import android.opengl.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleWithURLView extends ArticleView {
    public ArticleWithURLView(Context context, Article article) {
        super(context, article);
    }

    protected String getPrefix() {
        return "      ";
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ViewParent parent = this.getParent();
        while(parent!=this.getRootView()){
            if(parent.getClass().equals(PageView.class)){
               ((PageView)parent).enlarge(this);
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    protected void buildView() {
        LinearLayout titleLL = new LinearLayout(this.getContext());
        titleLL.setOrientation(HORIZONTAL);

        titleLL.addView(titleView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout noneTitle = new LinearLayout(this.getContext());
        noneTitle.setOrientation(VERTICAL);

        LinearLayout publisherView = new LinearLayout(this.getContext());

        TextView sharedByView = new TextView(this.getContext());
        sharedByView.setText(this.getContext().getString(R.string.sharedBy));
        sharedByView.setPadding(2, 2, 5, 2);
        sharedByView.setTextSize(14);
        sharedByView.setTextColor(0xffAAAAAA);
        publisherView.addView(sharedByView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT));
        publisherView.addView(portraitView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT));
        publisherView.addView(super.authorView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT));

        LinearLayout contentLL = new LinearLayout(this.getContext());
        contentView.setText(getPrefix() + article.getContent());
        //contentView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,1));
        contentLL.addView(contentView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 1));
        this.setOrientation(VERTICAL);

        noneTitle.addView(publisherView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        noneTitle.addView(contentLL, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        this.addView(titleLL, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.addView(noneTitle, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        //this.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT,1));
    }
}
