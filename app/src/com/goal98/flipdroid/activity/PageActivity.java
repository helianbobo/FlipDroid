package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.anim.Rotate3DAnimation;
import com.goal98.flipdroid.model.ContentRepo;
import com.goal98.flipdroid.model.FakeArticleSource;
import com.goal98.flipdroid.model.SimplePagingStrategy;
import com.goal98.flipdroid.model.sina.SinaArticleSource;
import com.goal98.flipdroid.util.GestureUtil;
import com.goal98.flipdroid.view.PageView;

public class PageActivity extends Activity {

    static final private int CONFIG_ID = Menu.FIRST;

    private boolean animationStarted = false;

    private ViewGroup container;
    private View current;
    private View next;

    private ContentRepo repo;
    private SharedPreferences preferences;

    private SimplePagingStrategy simplePagingStrategy;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String repoStr = (String) getIntent().getExtras().get("repo");

        setContentView(R.layout.main);

        container = (ViewGroup) findViewById(R.id.pageContainer);
        current = findViewById(R.id.currentPage);
        next = findViewById(R.id.nextPage);

        repo = new ContentRepo();
        String userId = "13774256612";
        String password = "541116";
        String sourceUserId = null;
        if ("weibo".equals(repoStr)) {
            sourceUserId = null;
            repo.setArticleSource(new SinaArticleSource(false, userId, password, sourceUserId));
        } else if ("helianbobo".equals(repoStr)) {
            sourceUserId = "1702755335";
            repo.setArticleSource(new SinaArticleSource(false, userId, password, sourceUserId));
        } else if ("fake".equals(repoStr)) {
            repo.setArticleSource(new FakeArticleSource());

        }
        simplePagingStrategy = new SimplePagingStrategy();
        repo.setPagingStretagy(simplePagingStrategy);

        new FetchRepoTask().execute();

    }

    private void initPageViews() {
        ((PageView) current).setPage(repo.getPage(0));
        ((PageView) next).setPage(repo.getPage(1));
    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        simplePagingStrategy.setArticlePerPage(getArticlePerPageFromPreference());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0 && !animationStarted) {
                    if (GestureUtil.flipRight(event))
                        flipPage(false);
                    else if (GestureUtil.flipLeft(event))
                        flipPage(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                float middle = container.getWidth() / 2.0f;
                boolean rightHalf = event.getX() > middle;
                if (rightHalf) {
                    flipPage(true);
                } else {
                    flipPage(false);
                }
                break;
            default:
                break;
        }
        return true;

    }

    private void flipPage(final boolean forward) {

        container.removeAllViews();

        next.setVisibility(View.VISIBLE);

        Animation rotation = buildAnimation(forward);

        if (forward) {
            container.addView(next);
            container.addView(current);
            current.startAnimation(rotation);
        } else {
            container.addView(current);
            container.addView(next);
            next.startAnimation(rotation);
        }

        animationStarted = true;
    }

    private Animation buildAnimation(final boolean forward) {
        final float centerY = container.getHeight() / 2.0f;
        final float fromDegrees = forward ? 0 : -90;
        final float toDegrees = forward ? -90 : 0;
        final float centerX = 0;
        float depthZ = 0.0f;
        boolean reverse = true;
        long duration = getFlipDurationFromPreference();

        Rotate3DAnimation rotation = new Rotate3DAnimation(fromDegrees, toDegrees, centerX, centerY, depthZ, reverse);
        rotation.setDuration(duration);
        rotation.setFillAfter(false);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());

        rotation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                current.setVisibility(View.GONE);
                animationStarted = false;

                switchViews(forward);

                current.setAnimation(null);
                next.setAnimation(null);
            }

            public void onAnimationRepeat(Animation animation) {

            }
        });

        return rotation;
    }

    private long getFlipDurationFromPreference() {
        String key = getString(R.string.key_anim_flip_duration_preference);

        return Long.parseLong(preferences.getString(key, "500"));
    }

    private int getArticlePerPageFromPreference() {
        String key = getString(R.string.key_article_per_page_preference);

        return Integer.parseInt(preferences.getString(key, "3"));
    }

    private void switchViews(boolean forward) {
        View tmp = current;
        current = next;
        next = tmp;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, CONFIG_ID, 0, R.string.config);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONFIG_ID:
                startActivity(new Intent(this, ConfigActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FetchRepoTask extends AsyncTask<Void, Void, ContentRepo> {
        protected ContentRepo doInBackground(Void... voids) {
            repo.refresh();
            return repo;
        }

        protected void onPostExecute(ContentRepo contentRepo) {
            initPageViews();
        }
    }

}
