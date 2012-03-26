package com.goal98.flipdroid.util;

import android.text.Spannable;
import android.text.style.URLSpan;
import com.goal98.flipdroid.view.URLSpanNoUnderline;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-22
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */
public class TextPaintUtil {
    public static void removeUnderlines(Spannable p_Text) {
        URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);

        for(URLSpan span:spans) {
            int start = p_Text.getSpanStart(span);
            int end = p_Text.getSpanEnd(span);
            p_Text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            p_Text.setSpan(span, start, end, 0);
        }
    }
}
