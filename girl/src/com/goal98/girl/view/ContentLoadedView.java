package com.goal98.girl.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.goal98.android.WebImageView;
import com.goal98.girl.R;
import com.goal98.girl.model.Article;
import com.goal98.girl.util.*;
import com.goal98.tika.common.ImageInfo;
import com.goal98.tika.common.Paragraphs;
import com.goal98.tika.common.TikaConstants;
import com.goal98.tika.common.TikaUIObject;

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


    public ContentLoadedView(Context context, Article article, ThumbnailViewContainer pageViewContainer) {
        super(context, article, pageViewContainer, true);
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
        final View layout = inflator.inflate(R.layout.enlarged_content, this);
        final boolean expandable = article.isExpandable();
        if (expandable) {
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
            if (expandable) {
                TextView referenceText = new TextView(this.getContext());
                referenceText.setText(article.getStatus());
                referenceText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_REFERENCE);

                referenceText.setTextColor(Color.parseColor("#AAAAAA"));
                referenceContent.addView(referenceText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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

        authorView.setText(article.getAuthor() + " ");//nasty but works
        authorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);
        authorView.setTextColor(Color.parseColor("#AAAAAA"));

        createDateView = (TextView) layout.findViewById(R.id.createdDate);

        String localeStr = this.getContext().getString(R.string.locale);
        String time = PrettyTimeUtil.getPrettyTime(localeStr, article.getCreatedDate());
        createDateView.setText(time + " ");//nasty but works
        createDateView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_AUTHOR);

        ScrollView wrapper = (ScrollView) layout.findViewById(R.id.wrapper);
        wrapper.setVerticalScrollBarEnabled(true);

        this.contentHolderView = (LinearLayout) layout.findViewById(R.id.contentHolder);
        final int txtSize = Constants.TEXT_SIZE_CONTENT;
        handler.post(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Paragraphs paragraphs = new Paragraphs();
                        paragraphs.toParagraph(article.getToParagraph(deviceInfo));
                        final LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        int imageIndex = 0;

                        final List<TikaUIObject> paragraphsList = paragraphs.getParagraphs();
                        int size = paragraphsList.size();
                        for (int i = 0; i < size; i++) {
                            TikaUIObject uiObject = paragraphsList.get(i);
                            if (uiObject.getType().equals(TikaUIObject.TYPE_TEXT)) {
                                String temp = uiObject.getObjectBody().replaceAll("<[/]?.+?>", "");
                                if (temp.trim().length() == 0) {
                                    continue;
                                }
                                String style = "<p>";
                                final TextView tv = new TextView(ContentLoadedView.this.getContext());

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
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        contentHolderView.addView(tv, textLayoutParams);

                                    }
                                });
                            }


                            if (uiObject.getType().equals(TikaUIObject.TYPE_IMAGE)) {
                                final ImageInfo imageInfo = ((ImageInfo) uiObject);
                                if (imageInfo.getWidth() == 0)
                                    continue;
                                final String url = imageInfo.getUrl();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        addImageView(url);
                                    }
                                });

                                imageIndex++;

                            }
                        }
                        if (!expandable) {
                            final ImageInfo imageInfo = new ImageInfo();
                            imageInfo.setHeight(deviceInfo.getHeight());
                            imageInfo.setWidth(deviceInfo.getWidth());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    addImageView(article.getImageUrl().toExternalForm());
                                }
                            });
                        }
                        final Button viewSource = (Button) layout.findViewById(R.id.viewSource);
                        if (expandable) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewSource.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, deviceInfo.getHeight() / 12));
                                    viewSource.setOnClickListener(new OnClickListener() {
                                        public void onClick(View view) {
                                            String url = article.getSourceURL();
                                            if (url != null) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(url));
                                                ContentLoadedView.this.getContext().startActivity(intent);
                                            } else {
                                                new AlarmSender(getContext().getApplicationContext()).sendInstantMessage(R.string.original_url_is_not_available);
                                            }
                                        }
                                    });
                                    viewSource.setVisibility(VISIBLE);
                                }
                            });

                        }
                    }
                }).start();
            }
        });

    }

    private void addImageView(String url) {
        WebImageView imageView = new WebImageView(this.getContext(), url, this.getResources().getDrawable(Constants.DEFAULT_PIC), this.getResources().getDrawable(Constants.DEFAULT_PIC), false, toLoadImage, ImageView.ScaleType.FIT_CENTER);
        imageView.setDefaultWidth(deviceInfo.getWidth());
        final LayoutParams imageLayoutParams = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        imageLayoutParams.setMargins(0, 10, 0, 0);
        contentHolderView.addView(imageView, imageLayoutParams);
//        imageView.setAutoSize(true);
        imageView.loadImage();
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
