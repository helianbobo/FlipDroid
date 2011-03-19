package com.goal98.flipdroid.activity;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.*;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.anim.AnimationFactory;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.exception.NoMorePageException;
import com.goal98.flipdroid.exception.NoNetworkException;
import com.goal98.flipdroid.model.*;
import com.goal98.flipdroid.model.sina.SinaArticleSource;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.GestureUtil;
import com.goal98.flipdroid.view.PageView;

public class PageActivity extends Activity {

    static final private int CONFIG_ID = Menu.FIRST;

    private boolean flipStarted = false;
    private boolean mToggleIndeterminate = false;
    private boolean forward = false;

    private ViewGroup container;
    private View current;
    private View next;

    private ContentRepo repo;
    private SharedPreferences preferences;

    private SimplePagingStrategy simplePagingStrategy;

    private String deviceId;
    private int currentPageIndex = -1;
    private Page currentPage;


    private AlarmSender alarmSender;

    private AccountDB accountDB;
    private String accountType;
    private String sourceId;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountDB = new AccountDB(this);
        alarmSender = new AlarmSender(this);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tManager.getDeviceId();

        accountType = (String) getIntent().getExtras().get("type");
        sourceId = (String) getIntent().getExtras().get("sourceId");

        setContentView(R.layout.main);

        container = (ViewGroup) findViewById(R.id.pageContainer);
        current = new com.goal98.flipdroid.view.PageView(this);
        current.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        next = new com.goal98.flipdroid.view.PageView(this);
        next.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        next.setVisibility(LinearLayout.GONE);
        repo = new ContentRepo();

        simplePagingStrategy = new SimplePagingStrategy();
        repo.setPagingStrategy(simplePagingStrategy);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accountDB.close();
    }


    private void handleException(NoNetworkException e) {

        String msg = e.getMessage();
        alarmSender.sendAlarm(msg);
    }


    @Override
    protected void onStart() {
        super.onStart();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        simplePagingStrategy.setArticlePerPage(getArticlePerPageFromPreference());


        if (Constants.TYPE_SINA_WEIBO.equals(accountType)) {
            String userId = preferences.getString("sina_account", null);
            Cursor cursor = accountDB.findByTypeAndUsername(accountType, userId);
            cursor.moveToFirst();
            String password = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD));
            cursor.close();

            repo.setArticleSource(new SinaArticleSource(false, userId, password, sourceId));
        } else if (Constants.TYPE_FAKE.equals(accountType)) {
            repo.setArticleSource(new FakeArticleSource());

        }

        flipPage(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0 && !flipStarted) {
                    if (GestureUtil.flipRight(event))
                        flipPage(false);
                    else if (GestureUtil.flipLeft(event))
                        flipPage(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (deviceId == null || deviceId.startsWith("0000")) {
                    float middle = container.getWidth() / 2.0f;
                    boolean rightHalf = event.getX() > middle;
                    if (rightHalf) {
                        flipPage(true);
                    } else {
                        flipPage(false);
                    }
                }
                break;
            default:
                break;
        }
        return true;

    }

    private void flipPage(final boolean forward) {
        flipStarted = true;

        this.forward = forward;

        if (!forward)
            decreasePageNo();

        int nextPageIndex = forward ? currentPageIndex + 1 : currentPageIndex;
        if (nextPageIndex >= 0) {

            try {
                currentPage = repo.getPage(nextPageIndex);
                processCurrentPage();
            } catch (NoMorePageException e) {
                new FetchRepoTask().execute(nextPageIndex);
            }


        } else {
//            currentPageIndex++;
//            startActivity(new Intent(this, IndexActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);

            finish();
        }
    }

    private void decreasePageNo() {
        currentPageIndex--;
    }

    private Animation buildAnimation(final boolean forward) {
        final float centerY = container.getHeight() / 2.0f;
        final float centerX = 0;
        long duration = getFlipDurationFromPreference();

        AnimationFactory animationFactory = new AnimationFactory();
        Animation rotation = animationFactory.buildFlipAnimation(forward, duration, centerX, centerY);

        rotation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                current.setVisibility(View.GONE);
                flipStarted = false;

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
        return Integer.parseInt(preferences.getString(key, "5"));
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

    private void noMorePage() {
        alarmSender.sendAlarm("");
        flipStarted = false;

    }

    private class FetchRepoTask extends AsyncTask<Integer, NoNetworkException, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        protected Integer doInBackground(Integer... integers) {

            try {
                repo.refresh();
            } catch (NoNetworkException e) {
                publishProgress(e);
            }
            return integers[0];
        }

        protected void onPostExecute(Integer pageIndex) {

            try {
                currentPage = repo.getPage(pageIndex);
            } catch (NoMorePageException e1) {
                noMorePage();
            }

            processCurrentPage();
            setProgressBarIndeterminateVisibility(false);
        }

        @Override
        protected void onProgressUpdate(NoNetworkException... exceptions) {
            if (exceptions != null && exceptions.length > 0)
                handleException(exceptions[0]);
        }
    }


    private void processCurrentPage() {
        container.removeAllViews();

        next.setVisibility(View.VISIBLE);

        if (currentPage != null) {
            ((PageView) next).setPage(currentPage);

            Animation rotation = buildAnimation(forward);

            if (forward) {
                currentPageIndex++;
                container.addView(next);
                container.addView(current);
                current.startAnimation(rotation);
            } else {
                container.addView(current);
                container.addView(next);
                next.startAnimation(rotation);
            }
        }
    }

}
