package com.goal98.flipdroid2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid2.R;
import com.goal98.flipdroid2.client.OAuth;
import com.goal98.flipdroid2.db.RSSURLDB;
import com.goal98.flipdroid2.exception.NoSinaAccountBindedException;
import com.goal98.flipdroid2.model.Article;
import com.goal98.flipdroid2.util.AlarmSender;
import com.goal98.flipdroid2.util.Constants;
import com.goal98.flipdroid2.util.SinaAccountUtil;
import com.goal98.flipdroid2.view.ArticleHolder;
import com.goal98.flipdroid2.view.ContentLoadedView;
import com.goal98.flipdroid2.view.TopBar;
import com.goal98.tika.common.TikaConstants;
import com.mobclick.android.MobclickAgent;
import weibo4j.WeiboException;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-2-7
 * Time: 下午10:01
 * To change this template use File | Settings | File Templates.
 */
public class ContentLoadedActivity extends Activity {
    private LayoutInflater inflater;
    private SinaWeiboHelper sinaWeiboHelper;
    private Article article;
    private Handler hander = new Handler();
    private View nonFavoriteButton;
    private View favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentloaded);
        final RelativeLayout body = (RelativeLayout) findViewById(R.id.body);

        final TopBar topBar = (TopBar) findViewById(R.id.topbar);
        inflater = LayoutInflater.from(this);
        sinaWeiboHelper = new SinaWeiboHelper(this);
        article = ArticleHolder.getInstance().get();
        if (article == null)
            finish();
        ContentLoadedView loadedArticleView = new ContentLoadedView(this, article, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        loadedArticleView.setPadding(0, 10, 0, 0);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.topbar);
        body.addView(loadedArticleView, layoutParams);
        topBar.addButton(TopBar.TEXT, R.string.addfavorite, new View.OnClickListener() {
            public void onClick(View view) {
                addFavoriteTagAnim(view, body, topBar.getId());


                RSSURLDB rssUrlDB = new RSSURLDB(ContentLoadedActivity.this);
                rssUrlDB.insert(article);
                rssUrlDB.close();

            }
        });
        topBar.addButton(TopBar.IMAGE, R.drawable.ic_share_topbar, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {

                    if (!SinaAccountUtil.alreadyBinded(ContentLoadedActivity.this)) {
                        showDialog(PageActivity.PROMPT_OAUTH);
                        return;
                    }
                    final LinearLayout commentShadowLayer = new LinearLayout(ContentLoadedActivity.this);
                    commentShadowLayer.setBackgroundColor(Color.parseColor(Constants.SHADOW_LAYER_COLOR));
                    commentShadowLayer.setPadding(14, 20, 14, 20);
                    LinearLayout commentPad = (LinearLayout) inflater.inflate(R.layout.comment_pad, null);
                    WebImageView sourceImage = (WebImageView) commentPad.findViewById(R.id.source_image);
                    TextView sourceName = (TextView) commentPad.findViewById(R.id.source_name);
                    Button closeBtn = (Button) commentPad.findViewById(R.id.close);
                    ImageButton sendBtn = (ImageButton) commentPad.findViewById(R.id.send);

                    //closeBtn
                    closeBtn.setOnTouchListener(new View.OnTouchListener() {

                        public boolean onTouch(View view, MotionEvent motionEvent) {

                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_UP:
                                    body.removeView(commentShadowLayer);
                                    break;
                                default:
                                    break;
                            }

                            return false;
                        }

                    });

                    TextView status = (TextView) commentPad.findViewById(R.id.status);
                    final TextView wordCount = (TextView) commentPad.findViewById(R.id.wordCount);
                    final EditText commentEditText = (EditText) commentPad.findViewById(R.id.comment);
                    commentEditText.addTextChangedListener(new TextWatcher() {
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }

                        public void afterTextChanged(Editable s) {
                            setWordCountIndicator(wordCount, s.length());
                        }
                    });

                    sendBtn.setOnTouchListener(new View.OnTouchListener() {

                        public boolean onTouch(View view, MotionEvent motionEvent) {

                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_UP:
                                    if (commentEditText.getText().length() > 140) {
                                        wordCount.setText(R.string.toolong);
                                        return true;
                                    }

                                    MobclickAgent.onEvent(ContentLoadedActivity.this, "ShareViaSinaWeibo");

                                    Thread t = new Thread(new Runnable() {
                                        public void run() {
                                            if (article.getSourceType().equals(TikaConstants.TYPE_SINA_WEIBO)) {
                                                try {
                                                    sinaWeiboHelper.comment(commentEditText.getText().toString(), article);
                                                } catch (WeiboException e) {
                                                    e.printStackTrace();
                                                } catch (NoSinaAccountBindedException e) {
                                                    ContentLoadedActivity.this.startActivity(new Intent(ContentLoadedActivity.this, SinaAccountActivity.class));
                                                }
                                            } else {
                                                try {
                                                    sinaWeiboHelper.forward(commentEditText.getText().toString(), article);

                                                } catch (WeiboException e) {
                                                    e.printStackTrace();
                                                } catch (NoSinaAccountBindedException e) {
                                                    ContentLoadedActivity.this.startActivity(new Intent(ContentLoadedActivity.this, SinaAccountActivity.class));
                                                }
                                            }
                                            topBar.post(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(ContentLoadedActivity.this, R.string.share_success, 2000).show();
                                                    ContentLoadedActivity.this.dismissDialog(PageActivity.PROMPT_INPROGRESS);
                                                }
                                            });
                                        }
                                    });
                                    t.start();
                                    ContentLoadedActivity.this.showDialog(PageActivity.PROMPT_INPROGRESS);
                                    break;
                                default:
                                    break;
                            }

                            return false;
                        }

                    });

                    if (article.getPortraitImageUrl() != null) {
                        sourceImage.setImageUrl(article.getPortraitImageUrl().toExternalForm());
                        sourceImage.loadImage();
                    }

                    sourceName.setText(article.getAuthor());
                    status.setText(article.getStatus());

                    String paragraph1 = article.getPreviewParagraph();
                    if (paragraph1.length() > 40) {
                        paragraph1 = paragraph1.substring(0, 40);
                    }
                    String prefixPart = "";
                    String templateText = "";
                    if (article.getTitle() != null && article.getTitle().length() != 0) {
                        prefixPart = "[" + article.getTitle() + "]";
                        templateText = "| " + prefixPart + " " + paragraph1 + " " + (article.getSourceURL() == null ? "" : article.getSourceURL());
                    }

                    commentEditText.setText(templateText);
                    commentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View view, boolean b) {
                            commentEditText.requestFocus();
                            commentEditText.setSelection(0);
                            commentEditText.setFocusable(true);
                        }
                    });


                    int count = templateText.length();
                    setWordCountIndicator(wordCount, count);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                    commentShadowLayer.addView(commentPad, params);
                    body.addView(commentShadowLayer, params);
                }
            }
        });

    }

    private void setWordCountIndicator(TextView wordCount, int current) {
        if (current > 140) {
            wordCount.setTextColor(Color.parseColor(Constants.COLOR_RED));
        } else {
            wordCount.setTextColor(Color.BLACK);
        }

        wordCount.setText(current + "/140");
    }

    public static final int PROMPT_OAUTH = 1;
    public static final int NAVIGATION = 2;
    public static final int PROMPT_INPROGRESS = 3;

    public Dialog dialog;

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;
        switch (id) {
            case PROMPT_INPROGRESS:
                ProgressDialog dialogProgress = new ProgressDialog(this);
                dialogProgress.setIcon(R.drawable.icon);
                dialogProgress.setMessage(this.getString(R.string.inprogress));
                dialogProgress.setCancelable(false);
                this.dialog = dialogProgress;
                break;
            case PROMPT_OAUTH:
                builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.commentneedsinaoauth)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FlipdroidApplications application = (FlipdroidApplications) getApplication();
                                final OAuth oauth = new OAuth();
                                application.setOauth(oauth);

                                showDialog(PROMPT_INPROGRESS);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hander.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                boolean result = oauth.RequestAccessToken(ContentLoadedActivity.this, "flipdroid2://SinaAccountSaver", new OAuth.OnRetrieved() {
                                                    @Override
                                                    public void onRetrieved() {
                                                        if (ContentLoadedActivity.this.dialog != null)
                                                            ContentLoadedActivity.this.dialog.dismiss();
                                                    }
                                                });
                                                if (!result) {
                                                    new AlarmSender(ContentLoadedActivity.this.getApplicationContext()).sendInstantMessage(R.string.networkerror);
                                                }
                                            }
                                        });

                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                this.dialog = builder.create();

                break;

            default:
                this.dialog = null;
        }
        if (dialog != null) {
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    dialog = null;
                }
            });
        }
        return this.dialog;
    }

    private void addFavoriteTagAnim(View view, RelativeLayout body, int belowViewId) {

        ImageView favoriteImageView = new ImageView(view.getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.BELOW, belowViewId);

        favoriteImageView.setLayoutParams(layoutParams);
        favoriteImageView.setImageResource(R.drawable.tag_favorite);
        body.addView(favoriteImageView);
        Animation animation = AnimationUtils.loadAnimation(
                view.getContext(), R.anim.favoriteanim);
        favoriteImageView.startAnimation(animation);
    }
}
