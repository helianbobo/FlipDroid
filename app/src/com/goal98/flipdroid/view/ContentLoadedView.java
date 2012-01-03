package com.goal98.flipdroid.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ParagraphStyle;
import android.text.util.Linkify;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.util.PrettyTimeUtil;
import com.goal98.tika.common.ImageInfo;
import com.goal98.tika.common.Paragraphs;
import com.goal98.tika.common.TikaConstants;
import com.goal98.tika.common.TikaUIObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentLoadedView extends ArticleView {
    private float oldDist;
    private LinearLayout contentHolderView;
    public WebImageView icon;

    public ContentLoadedView(Context context, Article article, WeiboPageView pageView) {
        super(context, article, pageView, true);
    }

    protected String getPrefix() {
        return "            ";
    }

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;


    public void buildView() {
        LayoutInflater inflator = LayoutInflater.from(this.getContext());
        LinearLayout layout = (LinearLayout) inflator.inflate(R.layout.enlarged_content, this);
        if (article.isExpandable()) {
            this.titleView = (TextView) layout.findViewById(R.id.title);
            titleView.setText(article.getTitle());
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_TITLE);
        } else {
            LinearLayout titleViewWrapper = (LinearLayout) layout.findViewById(R.id.titleWrapper);
            titleViewWrapper.setVisibility(GONE);
        }
        if (article.getSourceType().equals(TikaConstants.TYPE_SINA_WEIBO) || article.getSourceType().equals(TikaConstants.TYPE_MY_SINA_WEIBO)) {
            LinearLayout referenceContent = (LinearLayout) layout.findViewById(R.id.referenceContent);
            referenceContent.setVisibility(VISIBLE);
            if (article.isExpandable()) {
                TextView referenceText = new TextView(this.getContext());
                referenceText.setText(article.getStatus());
                referenceText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_REFERENCE);

                referenceText.setTextColor(Color.parseColor("#AAAAAA"));
                referenceContent.addView(referenceText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }
        }

        this.authorView = (TextView) layout.findViewById(R.id.author);

        this.portraitView = (WebImageView) layout.findViewById(R.id.portrait);
        if (article.getPortraitImageUrl() != null) {
            this.portraitView.setImageUrl(article.getPortraitImageUrl().toString());
        } else {
            this.portraitView.setVisibility(View.GONE);
        }
        if (deviceInfo.isLargeScreen()) {
            portraitView.setDefaultHeight(32);
            portraitView.setDefaultWidth(32);
        }
        this.portraitView.loadImage();

        authorView.setText(article.getAuthor());
        authorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);
        authorView.setTextColor(Color.parseColor("#AAAAAA"));

        createDateView = (TextView) layout.findViewById(R.id.createdDate);

        String time = PrettyTimeUtil.getPrettyTime(this.getContext(), article.getCreatedDate());
        createDateView.setText(time);
        createDateView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);

        ScrollView wrapper = (ScrollView) layout.findViewById(R.id.wrapper);
        wrapper.setVerticalScrollBarEnabled(true);

        this.contentHolderView = (LinearLayout) layout.findViewById(R.id.contentHolder);
        int txtSize = Constants.TEXT_SIZE_CONTENT;
        Paragraphs paragraphs = new Paragraphs();
        paragraphs.toParagraph(article.getToParagraph(deviceInfo));
        LayoutParams textLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        int imageIndex = 0;
        final List<TikaUIObject> paragraphsList = paragraphs.getParagraphs();
        for (int i = 0; i < paragraphsList.size(); i++) {
            TikaUIObject uiObject = paragraphsList.get(i);
            if (uiObject.getType().equals(TikaUIObject.TYPE_TEXT)) {
                String temp = uiObject.getObjectBody().replaceAll("<[/]?.+?>", "");
                if (temp.trim().length() == 0) {
                    continue;
                }
                String style = "<p>";
                TextView tv = new TextView(this.getContext());

                tv.setLineSpacing(1, getLineSpacing());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtSize);
                tv.setTextColor(Constants.LOADED_TEXT_COLOR);
                tv.setGravity(Gravity.LEFT | Gravity.TOP);
                StringBuilder sb = null;
                if (uiObject.getObjectBody().startsWith("<p><blockquote>")) {
                    style = "<p><blockquote>";
                    tv.setPadding(2 + txtSize * 2, 3, 2 + txtSize * 2, 3);
                    tv.setBackgroundColor(Color.parseColor("#DDDDDD"));
                    sb = new StringBuilder();
                    textLayoutParams.setMargins(0, (int) tv.getTextSize(), 0, 0);
                } else {
                    tv.setPadding(2 + txtSize, 3, 2 + txtSize, 3);
                    sb = new StringBuilder("<br/>");
                }

                String objectBody = uiObject.getObjectBody();
                String formatted = format(objectBody);
                if (formatted.trim().length() == 0)
                    continue;

                sb.append(formatted);

                while (i + 1 < paragraphsList.size()) {
                    final String nextParagraph = paragraphsList.get(i + 1).getObjectBody();
                    if (nextParagraph.startsWith("<p><blockquote>") && !style.equals("<p><blockquote>")) {
                        break;
                    }
                    if (nextParagraph.startsWith(style) ) {
                        sb.append("<br/><br/>");
                        formatted = format(nextParagraph);
                        sb.append(formatted);
                        i++;
                    } else {
                        break;
                    }
                }
                tv.setMovementMethod(LinkMovementMethod.getInstance());

                tv.setText(Html.fromHtml(sb.toString()));
                tv.setAutoLinkMask(Linkify.WEB_URLS);
                tv.setLinkTextColor(Constants.COLOR_LINK_TEXT);
                contentHolderView.addView(tv, textLayoutParams);
            }


            if (uiObject.getType().equals(TikaUIObject.TYPE_IMAGE)) {
                ImageInfo imageInfo = ((ImageInfo) uiObject);
                String url = imageInfo.getUrl();

                WebImageView imageView = new WebImageView(this.getContext(), url, this.getResources().getDrawable(Constants.DEFAULT_PIC), this.getResources().getDrawable(Constants.DEFAULT_PIC), false, toLoadImage);
                imageView.setRoundImage(false);
                imageView.setBackgroundResource(R.drawable.border);

                final DeviceInfo deviceInfo = getDeviceInfoFromApplicationContext();
                final int picWidth = imageInfo.getWidth();

                final int defaultWidth = (picWidth > deviceInfo.getDisplayWidth()-40)?deviceInfo.getDisplayWidth()-40:picWidth;
                imageView.setDefaultWidth(defaultWidth);
                final int defaultHeight = imageInfo.getHeight() * defaultWidth / imageInfo.getWidth();
                imageView.setDefaultHeight(defaultHeight);

                final LayoutParams imageLayoutParams = new LayoutParams(defaultWidth, defaultHeight);
                imageLayoutParams.gravity = Gravity.CENTER;

                imageLayoutParams.setMargins(0, 10, 0, 0);
                contentHolderView.addView(imageView, imageLayoutParams);

                imageView.loadImage();

                imageIndex++;

            }
        }

        Button viewSource = (Button) layout.findViewById(R.id.viewSource);
        if (article.isExpandable()) {
            viewSource.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, deviceInfo.getHeight() / 12));
            viewSource.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    String url = article.getSourceURL();
                    if (url != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        ContentLoadedView.this.getContext().startActivity(intent);
                    } else {
                        AlarmSender.sendInstantMessage(R.string.original_url_is_not_available, getContext());
                    }
                }
            });
        } else {
            viewSource.setVisibility(GONE);
        }
    }

    private float getLineSpacing() {
        return 1.2f;
    }

    private String format(String paragraph) {
        return paragraph.replaceAll("<br/>", "<br/><br/>").replaceAll("<p>", "").replaceAll("</p>", "").replaceAll("(<blockquote>)|(</blockquote>)", "");
    }


    public void renderBeforeLayout() {

    }


}
