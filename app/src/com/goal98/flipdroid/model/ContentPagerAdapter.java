package com.goal98.flipdroid.model;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.util.*;
import com.goal98.tika.common.ImageInfo;
import com.goal98.tika.common.Paragraphs;
import com.goal98.tika.common.TikaUIObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-25
 * Time: 下午7:27
 * To change this template use File | Settings | File Templates.
 */
public class ContentPagerAdapter extends PagerAdapter {
    List<ContentPage> contentPages = new ArrayList<ContentPage>();
    private DeviceInfo deviceInfo;
    private Activity activity;
    private final Paragraphs paragraphs;
    protected boolean toLoadImage;
    private Article article;
    private LinearLayout contentHolderView;
    private ContentPage contentPage;


    public ContentPagerAdapter(Article article, DeviceInfo deviceInfo, Activity activity) {
        this.deviceInfo = deviceInfo;
        this.article = article;
        toLoadImage = NetworkUtil.toLoadImage(activity);
        paragraphs = new Paragraphs();
        paragraphs.toParagraph(article.getToParagraph(deviceInfo));
        this.activity = activity;
        final List<TikaUIObject> paragraphsList = paragraphs.getParagraphs();

        doPage(paragraphsList);
    }

    private void doPage(List<TikaUIObject> paragraphsList) {
        float maxHeightInPixel = (float) (deviceInfo.getDisplayHeight() - 55 * deviceInfo.getDensity());

        contentPage = new ContentPage(maxHeightInPixel);
        final LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final int txtSize = Constants.TEXT_SIZE_CONTENT;
        LinearLayout layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.enlarged_content_page, null);

        createFirstPage(layout);

        contentHolderView = (LinearLayout) layout.findViewById(R.id.contentHolder);

        int size = paragraphsList.size();
        for (int i = 0; i < size; i++) {
            TikaUIObject uiObject = paragraphsList.get(i);
            if (uiObject.getType().equals(TikaUIObject.TYPE_TEXT)) {
                String temp = uiObject.getObjectBody().replaceAll("<[/]?.+?>", "");
                if (temp.trim().length() == 0) {
                    continue;
                }
                String style = "<p>";
                TextView tv = new TextView(activity);

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

                while (i + 1 < size) {
                    final String nextParagraph = paragraphsList.get(i + 1).getObjectBody();
                    if (nextParagraph.startsWith("<p><blockquote>") && !style.equals("<p><blockquote>")) {
                        break;
                    }
                    if (nextParagraph.startsWith(style)) {
                        sb.append("<br/><br/>");
                        formatted = format(nextParagraph);
                        sb.append(formatted);
                        i++;
                    } else {
                        break;
                    }
                }
                tv.setMovementMethod(LinkMovementMethod.getInstance());

                Spanned spanned = Html.fromHtml(sb.toString());

                Spannable spannable = Spannable.Factory.getInstance().newSpannable(spanned);

                TextPaintUtil.removeUnderlines(spannable);
                tv.setText(spannable);

                tv.setAutoLinkMask(Linkify.WEB_URLS);
                tv.setLinkTextColor(Constants.COLOR_LINK_TEXT);

                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (deviceInfo.getDisplayWidth()-40*deviceInfo.getDensity()), View.MeasureSpec.AT_MOST);
                tv.measure(widthMeasureSpec, View.MeasureSpec.UNSPECIFIED);

                int height = tv.getMeasuredHeight();

                contentHolderView.addView(tv, textLayoutParams);
                float diff = contentPage.overFlowIfPut(height);
                while(diff != -1){
                    int lines = (int) (tv.getLineCount() - diff / tv.getLineHeight());
                    if(lines > 0)
                        tv.setMaxLines(lines);

                    layout = resetNewPage(maxHeightInPixel, layout);

                    TextView clonedTextView = cloneTextView(textLayoutParams, txtSize, uiObject, spannable);
                    clonedTextView.setPadding(clonedTextView.getPaddingLeft(),-lines*tv.getLineHeight()+clonedTextView.getPaddingTop(),clonedTextView.getPaddingRight(),+clonedTextView.getPaddingBottom());
//                    contentPage.overFlowIfPut(-lines*tv.getLineHeight());

                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (deviceInfo.getDisplayWidth()-40*deviceInfo.getDensity()), View.MeasureSpec.AT_MOST);
                    clonedTextView.measure(widthMeasureSpec, View.MeasureSpec.UNSPECIFIED);

                    height = clonedTextView.getMeasuredHeight();

                    contentHolderView.addView(clonedTextView, textLayoutParams);

                    diff = contentPage.overFlowIfPut(height);
                    tv = clonedTextView;
                }
            }


            if (uiObject.getType().equals(TikaUIObject.TYPE_IMAGE)) {
                final ImageInfo imageInfo = ((ImageInfo) uiObject);
                if (imageInfo.getWidth() == 0)
                    continue;
                final String url = imageInfo.getUrl();


                int height = imageInfo.getHeight();
                int width = imageInfo.getWidth();

                int actualWidth = (int) (deviceInfo.getWidth() - 40 * deviceInfo.getDensity());
                float scale = (float)actualWidth/width;
                if(scale >=1 )
                    scale = 1;

                float diff = contentPage.overFlowIfPut(height*scale);
                if(diff == -1)
                    addImageView(url);
                else{
                    if(contentPage.getTotalHeight() == 0.0f){
                        addImageView(url);
                    }else{
                        layout = resetNewPage(maxHeightInPixel, layout);
                        i--;
                    }

                }
            }
        }

    }

    private LinearLayout resetNewPage(float maxHeightInPixel, LinearLayout layout) {
        contentPage.setView(layout);
        contentPages.add(contentPage);
        contentPage = new ContentPage(maxHeightInPixel);
        layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.enlarged_content_page, null);
        contentHolderView = (LinearLayout) layout.findViewById(R.id.contentHolder);
        return layout;
    }

    private TextView cloneTextView(LinearLayout.LayoutParams textLayoutParams, int txtSize, TikaUIObject uiObject, Spannable spannable) {
        TextView tv;
        tv = new TextView(activity);

        tv.setLineSpacing(1, getLineSpacing());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtSize);
        tv.setTextColor(Constants.LOADED_TEXT_COLOR);
        tv.setGravity(Gravity.LEFT | Gravity.TOP);

        if (uiObject.getObjectBody().startsWith("<p><blockquote>")) {
            tv.setPadding(2 + txtSize * 2, 3, 2 + txtSize * 2, 3);
            tv.setBackgroundColor(Color.parseColor("#DDDDDD"));
            textLayoutParams.setMargins(0, (int) tv.getTextSize(), 0, 0);
        } else {
            tv.setPadding(2 + txtSize, 3, 2 + txtSize, 3);
        }

        tv.setText(spannable);


        tv.setAutoLinkMask(Linkify.WEB_URLS);
        tv.setLinkTextColor(Constants.COLOR_LINK_TEXT);
        return tv;
    }

    private void createFirstPage(LinearLayout layout) {
        TextView titleView = (TextView) layout.findViewById(R.id.title);
        titleView.setText(article.getTitle());
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_TITLE);

        TextView authorView = (TextView) layout.findViewById(R.id.author);

        WebImageView portraitView = (WebImageView) layout.findViewById(R.id.portrait);
        if (article.getPortraitImageUrl() != null) {
            portraitView.setImageUrl(article.getPortraitImageUrl().toString());
        } else {
            portraitView.setVisibility(View.GONE);
        }
        if (deviceInfo.isLargeScreen()) {
            portraitView.setDefaultHeight(32);
            portraitView.setDefaultWidth(32);
        }
        portraitView.loadImage();

        authorView.setText(article.getAuthor() + " ");//nasty but works
        authorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);
        authorView.setTextColor(Color.parseColor("#AAAAAA"));

        TextView createDateView = (TextView) layout.findViewById(R.id.createdDate);

        String localeStr = activity.getString(R.string.locale);
        String time = PrettyTimeUtil.getPrettyTime(localeStr, article.getCreatedDate());
        createDateView.setText(time + " ");//nasty but works
        createDateView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);

        View titleWrapper = layout.findViewById(R.id.titleWrapper);
        titleWrapper.setVisibility(View.VISIBLE);
        View infoWrapper = layout.findViewById(R.id.infoWrapper);
        infoWrapper.setVisibility(View.VISIBLE);

        titleWrapper.measure(View.MeasureSpec.makeMeasureSpec(deviceInfo.getDisplayWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED);
        int titleWrapperHeight = titleWrapper.getMeasuredHeight();

        infoWrapper.measure(View.MeasureSpec.makeMeasureSpec(deviceInfo.getDisplayWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED);
        int infoWrapperHeight = infoWrapper.getMeasuredHeight();

        contentPage.overFlowIfPut(titleWrapperHeight);
        contentPage.overFlowIfPut(infoWrapperHeight);
    }

    public int getCount() {
        return contentPages.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    private void addImageView(String url) {
        WebImageView imageView = new WebImageView(activity, url, activity.getResources().getDrawable(Constants.DEFAULT_PIC), activity.getResources().getDrawable(Constants.DEFAULT_PIC), false, toLoadImage, ImageView.ScaleType.FIT_CENTER);
        imageView.setDefaultWidth(deviceInfo.getWidth());
        final FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        imageLayoutParams.setMargins(0, 10, 0, 0);
        contentHolderView.addView(imageView, imageLayoutParams);
        imageView.loadImage();
    }

    private float getLineSpacing() {
        return 1.2f;
    }

    private String format(String paragraph) {
        return paragraph.replaceAll("<br/>", "<br/><br/>").replaceAll("<p>", "").replaceAll("</p>", "").replaceAll("(<blockquote>)|(</blockquote>)", "");
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(contentPages.get(position).getView());
        return contentPages.get(position).getView();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(contentPages.get(position).getView());
    }
}
