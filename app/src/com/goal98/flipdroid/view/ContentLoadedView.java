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
        this.titleView = (TextView) layout.findViewById(R.id.title);
        titleView.setText(article.getTitle());
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_TITLE);

        if (article.getSourceType().equals(TikaConstants.TYPE_SINA_WEIBO) || article.getSourceType().equals(TikaConstants.TYPE_MY_SINA_WEIBO)) {
            LinearLayout reference = (LinearLayout) layout.findViewById(R.id.reference);
            LinearLayout referenceContent = (LinearLayout) layout.findViewById(R.id.referenceContent);
            LinearLayout shareByll = (LinearLayout) layout.findViewById(R.id.shareByll);
            shareByll.setVisibility(GONE);
            reference.setVisibility(VISIBLE);
            referenceContent.setVisibility(VISIBLE);
            if (article.getPortraitImageUrl() != null) {
                icon = new WebImageView(this.getContext(), article.getPortraitImageUrl().toExternalForm(), false,toLoadImage);
                icon.setRoundImage(true);
                icon.setDefaultHeight(25);
                icon.setDefaultWidth(25);

                reference.addView(icon, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }


            TextView author = new TextView(this.getContext());
            author.setText(article.getAuthor());
            author.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);

            author.setTextColor(Color.parseColor("#AAAAAA"));

            TextView referenceText = new TextView(this.getContext());
            referenceText.setText(article.getStatus());
            referenceText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_REFERENCE);

            referenceText.setTextColor(Color.parseColor("#AAAAAA"));
            final LayoutParams authorLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            authorLayoutParams.gravity = Gravity.CENTER;
            reference.addView(author, authorLayoutParams);
            referenceContent.addView(referenceText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        } else {
            this.authorView = (TextView) layout.findViewById(R.id.author);
            TextView sharedBy = (TextView) layout.findViewById(R.id.sharedBy);
            authorView.setText(article.getAuthor());

            createDateView = (TextView) layout.findViewById(R.id.createdDate);

            String time = PrettyTimeUtil.getPrettyTime(this.getContext(), article.getCreatedDate());
            createDateView.setText(time);

            this.portraitView = (WebImageView) layout.findViewById(R.id.portrait);
            if (article.getPortraitImageUrl() != null) {
                this.portraitView.setImageUrl(article.getPortraitImageUrl().toString());

            } else {
                this.portraitView.setVisibility(View.GONE);
            }
            if (deviceInfo.isLargeScreen()) {
                authorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);
                createDateView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);
                sharedBy.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);

            }
        }
        ScrollView wrapper = (ScrollView) layout.findViewById(R.id.wrapper);
        wrapper.setVerticalScrollBarEnabled(true);

        this.contentHolderView = (LinearLayout) layout.findViewById(R.id.contentHolder);
        int txtSize = Constants.TEXT_SIZE_CONTENT;
        Paragraphs paragraphs = new Paragraphs();
        paragraphs.toParagraph(article.getContent());
        LayoutParams textLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        int imageIndex = 0;
        final List<TikaUIObject> paragraphsList = paragraphs.getParagraphs();
        for (int i = 0; i < paragraphsList.size(); i++) {
            TikaUIObject uiObject = paragraphsList.get(i);
            if (uiObject.getType().equals(TikaUIObject.TYPE_TEXT)) {
                String temp =  uiObject.getObjectBody().replaceAll("<[/]?.+?>","");
                if(temp.trim().length()==0){
                    continue;
                }
                String style = "<p>";
                TextView tv = new TextView(this.getContext());

                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtSize);
                tv.setTextColor(Constants.LOADED_TEXT_COLOR);
                tv.setGravity(Gravity.LEFT | Gravity.TOP);
                if (uiObject.getObjectBody().startsWith("<p><blockquote>")) {
                    style = "<p><blockquote>";
                    tv.setPadding(2 + txtSize*2 , 3, 2 + txtSize *2, 3);
                    tv.setBackgroundColor(Color.parseColor("#DDDDDD"));
                } else {
                    tv.setPadding(2 + txtSize, 3, 2 + txtSize, 3);
                }
                StringBuilder sb = new StringBuilder("<br/>");
                String objectBody = uiObject.getObjectBody();
                String formatted = format(objectBody);
                if (formatted.trim().length() == 0)
                    continue;

                sb.append(formatted);

                while (i + 1 < paragraphsList.size()) {
                    final String nextParagraph = paragraphsList.get(i + 1).getObjectBody();
                    if (nextParagraph.startsWith(style)) {
                        sb.append("<br/><br/>");
                        formatted = format(nextParagraph);
                        sb.append(formatted);
                        i++;
                    } else {
                        break;
                    }
                }
                tv.setText(Html.fromHtml(sb.toString()));
                tv.setAutoLinkMask(Linkify.WEB_URLS);
                contentHolderView.addView(tv, textLayoutParams);
            }


            if (uiObject.getType().equals(TikaUIObject.TYPE_IMAGE)) {
                if (!toLoadImage)
                    continue;
                ImageInfo imageInfo = ((ImageInfo) uiObject);
                String url = imageInfo.getUrl();
                WebImageView imageView = new WebImageView(this.getContext(), url, false,toLoadImage);
                imageView.setRoundImage(false);
                imageView.setBackgroundResource(R.drawable.border);
//                imageView.imageView.setTag(url);

                final DeviceInfo deviceInfo = getDeviceInfoFromApplicationContext();
                final int picWidth = deviceInfo.getDipFromPixel(imageInfo.getWidth());

                int crop = 0;
                if (deviceInfo.getDipFromPixel(imageInfo.getWidth()) > deviceInfo.getDisplayWidth())
                    crop = 40;

                final int defaultWidth = picWidth > deviceInfo.getDisplayWidth() - crop ? deviceInfo.getDisplayWidth() - crop : picWidth;
                imageView.setDefaultWidth(defaultWidth);
                final int defaultHeight = imageInfo.getHeight() * defaultWidth / imageInfo.getWidth();
                imageView.setDefaultHeight(defaultHeight);

                System.out.println("defaultWidth" + defaultWidth);
                System.out.println("defaultHeight" + defaultHeight);
                final LayoutParams imageLayoutParams = new LayoutParams(defaultWidth, defaultHeight);
                imageLayoutParams.gravity = Gravity.CENTER;

                imageLayoutParams.setMargins(0, 10, 0, 0);
                contentHolderView.addView(imageView, imageLayoutParams);

                imageView.loadImage();

                imageIndex++;

            }
        }
//        TextView tv = new TextView(this.getContext());
//        if (deviceInfo.isLargeScreen()) {
//            tv.setPadding(0, 15, 0, 0);
//        } else {
//            tv.setPadding(0, 10, 0, 0);
//        }
//        tv.setText(Html.fromHtml("<br>"));
//        contentHolderView.addView(tv, textLayoutParams);
        Button viewSource = (Button) layout.findViewById(R.id.viewSource);
        viewSource.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, deviceInfo.getHeight() / 12));
        viewSource.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String url = article.getSourceURL();
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    ContentLoadedView.this.getContext().startActivity(intent);
                }else{
                    AlarmSender.sendInstantMessage(R.string.original_url_is_not_available, getContext());
                }
            }
        });
    }

    private String format(String paragraph) {
        return paragraph.replaceAll("<p>", "<span>").replaceAll("</p>", "</span>").replaceAll("<span><.*?></span>", "").replaceAll("(<blockquote>)|(</blockquote>)", "");
    }

    public void renderBeforeLayout() {
        icon.loadImage();
        this.portraitView.loadImage();
    }


}
