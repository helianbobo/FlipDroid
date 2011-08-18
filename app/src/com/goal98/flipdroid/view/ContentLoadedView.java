package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.text.Html;
import android.text.TextUtils;
import android.text.style.ParagraphStyle;
import android.util.FloatMath;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.util.PrettyTimeUtil;
import com.goal98.tika.common.ImageInfo;
import com.goal98.tika.common.Paragraphs;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount <= 1)
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                //Log.d("MTZ", "oldDist=" + oldDist);

                if (oldDist > 10f) {
                    mode = ZOOM;
                    //Log.d("MTZ", "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    float newDist = spacing(event);
                    //Log.d("MTZ", "newDist=" + newDist);
                    if (newDist > oldDist) {
                        //Log.d("MTZ", "zoomout" + newDist);
                    } else if (newDist < oldDist) {
                        //Log.d("MTZ", "zoomin" + newDist);
                    }
                }
                break;
        }
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void buildView() {
        LayoutInflater inflator = LayoutInflater.from(this.getContext());
        LinearLayout layout = (LinearLayout) inflator.inflate(R.layout.enlarged_content, this);

        if (article.getSourceType().equals(Constants.TYPE_SINA_WEIBO) || article.getSourceType().equals(Constants.TYPE_MY_SINA_WEIBO)) {
            LinearLayout reference = (LinearLayout) layout.findViewById(R.id.reference);
            LinearLayout shareByll = (LinearLayout) layout.findViewById(R.id.shareByll);
            shareByll.setVisibility(GONE);
            reference.setVisibility(VISIBLE);
            WebImageView icon = new WebImageView(this.getContext(), article.getPortraitImageUrl().toExternalForm(), true);
            icon.setDefaultHeight(50);
            icon.setDefaultWidth(50);
            reference.addView(icon, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            TextView author = new TextView(this.getContext());
            author.setText(article.getAuthor());
            if (deviceInfo.isLargeScreen())
                author.setTextSize(24);
            else if (deviceInfo.isSmallScreen())
                author.setTextSize(16);
            else
                author.setTextSize(20);

            author.setTextColor(Color.parseColor("#AAAAAA"));

            TextView referenceText = new TextView(this.getContext());
            referenceText.setSingleLine(false);
            referenceText.setEllipsize(TextUtils.TruncateAt.END);
            referenceText.setMaxLines(2);
            referenceText.setText(article.getStatus());
            if (deviceInfo.isLargeScreen())
                referenceText.setTextSize(24);
            else if (deviceInfo.isSmallScreen())
                referenceText.setTextSize(16);
            else
                referenceText.setTextSize(20);

            referenceText.setTextColor(Color.parseColor("#AAAAAA"));
            reference.addView(author, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            reference.addView(referenceText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        } else {
            this.authorView = (TextView) layout.findViewById(R.id.author);
            TextView sharedBy = (TextView) layout.findViewById(R.id.sharedBy);
            this.titleView = (TextView) layout.findViewById(R.id.title);
            authorView.setText(article.getAuthor());
            titleView.setText(article.getTitle());
            createDateView = (TextView) layout.findViewById(R.id.createdDate);

            String time = PrettyTimeUtil.getPrettyTime(this.getContext(), article.getCreatedDate());
            createDateView.setText(time);

            this.portraitView = (WebImageView) layout.findViewById(R.id.portrait);
            if (article.getPortraitImageUrl() != null) {
                this.portraitView.setImageUrl(article.getPortraitImageUrl().toString());
                this.portraitView.loadImage();
            } else {
                this.portraitView.setVisibility(View.GONE);
            }
            if (deviceInfo.isLargeScreen()) {
                authorView.setTextSize(16);
                createDateView.setTextSize(16);
                sharedBy.setTextSize(16);
                titleView.setTextSize(20);
            }
        }
        ScrollView wrapper = (ScrollView) layout.findViewById(R.id.wrapper);
        wrapper.setVerticalScrollBarEnabled(true);

        this.contentHolderView = (LinearLayout) layout.findViewById(R.id.contentHolder);
        int txtSize = 0;
        if (deviceInfo.isLargeScreen()) {
            txtSize = 20;
        } else {
            txtSize = 18;
        }
        Paragraphs paragraphs = new Paragraphs();
        paragraphs.toParagraph(article.getContent());
        LayoutParams textLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        int imageIndex = 0;
        final List<TikaUIObject> paragraphsList = paragraphs.getParagraphs();
        for (int i = 0; i < paragraphsList.size(); i++) {
            TikaUIObject uiObject = paragraphsList.get(i);
            if (uiObject.getType().equals(TikaUIObject.TYPE_TEXT)) {
                String style = "<p>";
                TextView tv = new TextView(this.getContext());
                tv.setTextSize(txtSize);
                tv.setTextColor(Constants.LOADED_TEXT_COLOR);
                tv.setGravity(Gravity.LEFT | Gravity.TOP);
                if (uiObject.getObjectBody().startsWith("<p><blockquote>")) {
                    style = "<p><blockquote>";
                    tv.setPadding(2 + txtSize * 1, 3, 2 + txtSize * 1, 3);
                    tv.setBackgroundColor(Color.parseColor("#EEEEEE"));
                } else {
                    tv.setPadding(2, 3, 2, 3);
                }
                StringBuilder sb = new StringBuilder();
                sb.append("<br/>");
                String formatted = format(uiObject.getObjectBody());
                sb.append(formatted);

                while (i + 1 < paragraphsList.size()) {
                    final String nextParagraph = paragraphsList.get(i + 1).getObjectBody();
                    if (nextParagraph.startsWith(style)) {
                        sb.append("<br><br>");
                        formatted = format(nextParagraph);
                        sb.append(formatted);
                        i++;
                    } else {
                        break;
                    }
                }
                tv.setText(Html.fromHtml(sb.toString()));
                contentHolderView.addView(tv, textLayoutParams);
            }


            if (uiObject.getType().equals(TikaUIObject.TYPE_IMAGE)) {
                ImageInfo imageInfo = ((ImageInfo) uiObject);
                String url = imageInfo.getUrl();
                WebImageView imageView = new WebImageView(this.getContext(), url, false);

//                imageView.imageView.setTag(url);

                final DeviceInfo deviceInfo = getDeviceInfoFromApplicationContext();
                final int picWidth = deviceInfo.getDipFromPixel(imageInfo.getWidth());

                int crop = 0;
                if(deviceInfo.getDipFromPixel(imageInfo.getWidth()) > deviceInfo.getDisplayWidth())
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
        TextView tv = new TextView(this.getContext());
        if (deviceInfo.isLargeScreen()) {
            tv.setPadding(0, 15, 0, 0);
        } else {
            tv.setPadding(0, 10, 0, 0);
        }
        tv.setText(Html.fromHtml("<br>"));
        contentHolderView.addView(tv, textLayoutParams);
    }

    private String format(String paragraph) {
        return paragraph.replaceAll("<p>", "<span>").replaceAll("</p>", "</span>").replaceAll("(<blockquote>)|(</blockquote>)", "");
    }

    public void renderBeforeLayout() {

    }


}
