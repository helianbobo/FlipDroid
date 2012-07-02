package com.goal98.flipdroid2.view;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import com.goal98.flipdroid2.model.Article;
import com.goal98.flipdroid2.util.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-20
 * Time: 上午10:09
 * To change this template use File | Settings | File Templates.
 */
public class ArticleTextViewRender {
    private String prefix;

    ArticleTextViewRender(String prefix) {
        this.prefix = prefix;
    }

    public void renderTextView(TextView tv, final Article article) {
        if (article != null && article.getContent() != null) {
            tv.setText(article.getThumbnailText());
            tv.setLinkTextColor(Constants.COLOR_LINK_TEXT);
        } else {
            tv.setVisibility(View.GONE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        }
    }
}
