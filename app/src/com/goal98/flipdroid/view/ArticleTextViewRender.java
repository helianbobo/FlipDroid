package com.goal98.flipdroid.view;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.Constants;
import org.xml.sax.XMLReader;

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

    public void renderTextView(TextView tv, Article article) {
        if (article != null && article.getContent() != null) {
//            if (article.getSourceType() != null && article.getSourceType().equals(Constants.TYPE_TAOBAO)) {
                tv.setText(Html.fromHtml(article.getContent()));
//            } else
//                tv.setText(prefix + (article.getContent().trim().concat("\n")).replaceAll("\n+", prefix + "\n"));
        } else {
            tv.setVisibility(View.GONE);
            tv.setTextSize(25);
        }
    }
}
