package com.goal98.flipdroid.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.util.TypedValue;
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

    public void renderTextView(TextView tv, final Article article) {
        if (article != null && article.getContent() != null) {
            tv.setText(Html.fromHtml(article.getContent().replaceAll("(<br/>)|(</h[1-6]+>)|(<h[1-6]+>)|(<img.*?>)|(<blockquote>)|(</blockquote>)|(hack</img>)","")));
            tv.setLinkTextColor(Constants.COLOR_LINK_TEXT);
        } else {
            tv.setVisibility(View.GONE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        }
    }
}
